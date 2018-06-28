package ga.lupuss.anotherbikeapp.models.firebase

import android.app.Activity
import com.google.firebase.firestore.*
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Observes query and load its data in portions depends on limit.
 * Interface is adapted to RecyclerView.
 */
class QueryLoadingManager(
        private val backScheduler: Scheduler = Schedulers.io(),
        private val frontScheduler: Scheduler = AndroidSchedulers.mainThread()
) : EventListener<QuerySnapshot>  {

    private val children = mutableListOf<DocumentSnapshot>()
    private var firstQueryNotification = true
    val size
        get() = children.size

    private lateinit var rootQuery: Query
    private var listeners: List<OnDocumentChanged> = listOf()
    private var limit: Long = FirebaseRoutesManager.DEFAULT_LIMIT

    fun init(query: Query, listeners: List<OnDocumentChanged>?) {


        this.rootQuery = query
        listeners?.let { this.listeners = it }

        rootQuery.addSnapshotListener(this)
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {

        exception?.let {
            Timber.e("Query callback exception: $it")
        }

        if (querySnapshot != null) {
            if (!firstQueryNotification) {

                Single.create<List<Pair<DocumentChange.Type, Int>>> {

                    it.onSuccess(fetchDocumentChanges(querySnapshot.documentChanges))

                }.observeOn(frontScheduler)
                        .subscribeOn(backScheduler)
                        .subscribe { res ->
                            res.forEach { (first, second) ->
                                notifyParent(first, second)
                            }
                        }
            }

            firstQueryNotification = false

        }
    }

    fun readDocument(position: Int): DocumentSnapshot {

        return children[position]
    }

    fun loadMoreDocuments(onEndOfData: (() -> Unit)?, onFail: ((Exception) -> Unit)?, activity: Activity) {

        val query = if (children.isNotEmpty()) {

            rootQuery
                    .startAfter(children.last())
                    .limit(limit)

        } else {

            rootQuery.limit(limit)
        }

        query.get()
                .addOnSuccessListener(activity) {

                    if (it.isEmpty) {

                        onEndOfData?.invoke()

                    } else {
                        it.documents.forEach {
                            children.add(it)
                            notifyNewDocument(children.size - 1)
                        }

                        if (it.documents.size < limit) {

                            onEndOfData?.invoke()
                        }
                    }
                }
                .addOnFailureListener(activity) {
                    onFail?.invoke(it)
                }
    }

    private fun fetchDocumentChanges(allChanges: List<DocumentChange>): List<Pair<DocumentChange.Type, Int>> {

        val list = mutableListOf<Pair<DocumentChange.Type, Int>>()

        Timber.v("New changes package >>> ")

        for (change in allChanges) {

            Timber.v("${change.type} > ${change.document.data}")

            when (change.type) {

                DocumentChange.Type.ADDED -> {

                    if (change.newIndex in 0..children.size) {

                        if (change.newIndex < children.size) {
                            children.add(change.newIndex, change.document)
                        } else {
                            children.add(change.document)
                        }
                        list.add(Pair(change.type, change.newIndex))
                    }
                }

                DocumentChange.Type.REMOVED -> {
                    if (change.oldIndex in 0 until children.size)
                        children.removeAt(change.oldIndex)

                    list.add(Pair(change.type, change.oldIndex))
                }

                DocumentChange.Type.MODIFIED -> {
                    if (change.newIndex in 0 until children.size)
                        children[change.newIndex] = change.document

                    list.add(Pair(change.type, change.newIndex))
                }
            }
        }

        Timber.v("End of package >>>")

        return list
    }

    private fun notifyParent(type: DocumentChange.Type, i: Int) {

        Timber.v("$type : $i")

        when (type) {

            DocumentChange.Type.MODIFIED -> notifyModifiedDocument(i)

            DocumentChange.Type.REMOVED -> notifyRemovedDocument(i)

            DocumentChange.Type.ADDED -> notifyNewDocument(i)
        }
    }

    private fun notifyNewDocument(i: Int) {

        listeners.forEach { it.onNewDocument(i) }
    }

    private fun notifyRemovedDocument(i: Int) {
        listeners.forEach { it.onDocumentDeleted(i) }
    }

    private fun notifyModifiedDocument(i: Int) {

        listeners.forEach { it.onDocumentModified(i) }
    }
}