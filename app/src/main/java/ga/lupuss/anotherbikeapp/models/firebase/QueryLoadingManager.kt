package ga.lupuss.anotherbikeapp.models.firebase

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Observes query and load its data in portions depends on limit.
 * Interface is adapted to RecyclerView.
 */
class QueryLoadingManager(private val rootQuery: Query,
                          private val limit: Long,
                          private val listeners: List<OnDocumentChanged>) {

    private val children = mutableListOf<DocumentSnapshot>()
    private var firstQueryNotification = true

    val size
        get() = children.size

    init {
        rootQuery.addSnapshotListener { querySnapshot, exception ->

            exception?.let {
                Timber.d("Query callback exception: $it")
            }

            if (querySnapshot != null) {
                if (!firstQueryNotification) {

                    Single.create<List<Pair<DocumentChange.Type, Int>>> {

                        it.onSuccess(fetchDocumentChanges(querySnapshot.documentChanges))

                    }.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe { res ->
                                res.forEach { (first, second) ->
                                    notifyParent(first, second)
                                }
                            }
                }

                firstQueryNotification = false

            }
        }
    }

    fun readDocument(position: Int): DocumentSnapshot {

        return children[position]
    }

    fun loadMoreDocuments(onEndOfData: (() -> Unit)?, onFail: ((Exception) -> Unit)?) {

        val query = if (children.isNotEmpty()) {

            rootQuery
                    .startAfter(children.last())
                    .limit(limit)

        } else {

            rootQuery.limit(limit)
        }

        query.get()
                .addOnSuccessListener {

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
                .addOnFailureListener {
                    onFail?.invoke(it)
                }
    }

    private fun fetchDocumentChanges(allChanges: List<DocumentChange>): List<Pair<DocumentChange.Type, Int>> {

        val list = mutableListOf<Pair<DocumentChange.Type, Int>>()

        Timber.d("New changes package >>> ")

        for (change in allChanges) {

            Timber.d("  > ${change.document.data}")

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

        Timber.d("End of package >>>")

        return list
    }


    private fun notifyParent(type: DocumentChange.Type, i: Int) {

        Timber.d("$type : $i")

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