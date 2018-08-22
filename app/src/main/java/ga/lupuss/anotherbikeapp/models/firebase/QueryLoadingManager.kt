package ga.lupuss.anotherbikeapp.models.firebase

import android.app.Activity
import com.google.firebase.firestore.*
import ga.lupuss.anotherbikeapp.kotlin.SchedulersPackage
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Observes query and load its data in portions depends on limit.
 * Interface is adapted to RecyclerView.
 */
class QueryLoadingManager(
    private val schedulersPackage: SchedulersPackage
) : EventListener<QuerySnapshot>  {

    private val children = mutableListOf<DocumentSnapshot>()
    val size
        get() = children.size

    private lateinit var rootQuery: Query
    private var listeners: MutableList<OnDocumentChanged> = mutableListOf()
    private var limit: Long = FirebaseRoutesManager.DEFAULT_LIMIT
    var isQueryListeningInitialized = false
        private set


    fun setTargetQuery(query: Query) {

        rootQuery = query
    }

    fun addRoutesDataChangedListener(onRoutesChangedListener: OnDocumentChanged) {

        listeners.add(onRoutesChangedListener)
    }

    fun removeOnRoutesDataChangedListener(onRoutesChangedListener: OnDocumentChanged) {

        listeners.remove(onRoutesChangedListener)
    }

    fun initQueryListening() {

        isQueryListeningInitialized = true
        rootQuery.addSnapshotListener(this)
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {

        exception?.let {
            Timber.e("Query callback exception: $it")
        }

        if (querySnapshot != null) {

            fetchDocuments(querySnapshot.documentChanges)
        }
    }

    private fun fetchDocuments(documentChanges: List<DocumentChange>) {

        Observable.create<Pair<DocumentChange.Type,Int>> {

            documentChanges.forEach { change ->

                Timber.i("$change, ${change.type}, ${change.oldIndex}, ${change.newIndex}, ${change.document.id}")

                when (change.type) {

                    DocumentChange.Type.ADDED -> {

                        if (change.newIndex in 0..children.size) {

                            val isLocallySaved = children.find { it.id == change.document.id } != null

                            if (change.newIndex < children.size && !isLocallySaved) {

                                children.add(change.newIndex, change.document)
                                it.onNext(Pair(change.type, change.newIndex))

                            }
                        }
                    }

                    DocumentChange.Type.REMOVED -> {
                        if (change.oldIndex in 0 until children.size)
                            children.removeAt(change.oldIndex)

                        it.onNext(Pair(change.type, change.oldIndex))
                    }

                    DocumentChange.Type.MODIFIED -> {
                        if (change.newIndex in 0 until children.size)
                            children[change.newIndex] = change.document

                        it.onNext(Pair(change.type, change.newIndex))
                    }
                }
            }

        }.observeOn(schedulersPackage.frontScheduler)
                .subscribeOn(schedulersPackage.backScheduler)
                .subscribe { (first, second) ->

                    notifyParent(first, second)
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