package ga.lupuss.anotherbikeapp.models.routes

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class QueryManager(val rootQuery: Query, val limit: Long, val listeners: List<OnDocumentChanged>) {

    private val children = mutableListOf<DocumentObserver>()

    val size
        get() = children.map { it.size }.sum()

    fun readDocument(position: Int): DocumentSnapshot {

        var x = position

        for (observer in children) {

            if (x < observer.size) {

                return observer[x]
            }
            x -= observer.size
        }

        throw IllegalStateException("No such a element! Position: $position X: $x")
    }

    fun loadMoreDocuments() {
        children.add(DocumentObserver(children.size, rootQuery, lastBeforeIfExists()))
    }

    private fun notifyNewDocument(childId: Int, relativePos: Int) {

        val i = countAbsoulutePosition(childId, relativePos)
        listeners.forEach { it.onNewDocument(i) }
    }

    private fun notifyRemovedDocument(childId: Int, relativePos: Int) {
        val i = countAbsoulutePosition(childId, relativePos)
        listeners.forEach { it.onDocumentDeleted(i) }
    }

    private fun notifyModifiedDocument(childId: Int, relativePos: Int) {
        val i = countAbsoulutePosition(childId, relativePos)
        listeners.forEach { it.onRouteModified(i) }
    }

    private fun countAbsoulutePosition(childId: Int, relativePos: Int): Int {

        var pos = 0

        for (i in 0 until childId) {
            pos += children[i].size
        }

        pos += relativePos
        return pos
    }

    private fun lastBeforeIfExists(): DocumentSnapshot? {

        for (observer in children.asReversed()) {

            if (observer.isNotEmpty()) {
                return observer.last()
            }
        }
        return null
    }

    inner class DocumentObserver(val num: Int,
                                 rootQuery: Query,
                                 startAfter: DocumentSnapshot?) : ArrayList<DocumentSnapshot>() {

        private val query: Query

        init {

            var q = rootQuery.limit(this@QueryManager.limit)
            if (startAfter != null)
                q = q.startAfter(startAfter)
            query = q

            query.addSnapshotListener { querySnapshot, _ ->
                if (querySnapshot != null && !querySnapshot.isEmpty) {

                    Single.create<LinkedHashMap<DocumentChange.Type, MutableList<Int>>> {

                        it.onSuccess(fetchDocumentChanges(querySnapshot.documentChanges))

                    }.observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe { map ->
                                map.forEach {
                                    notifyParent(it.key, it.value)
                                }
                            }
                }
            }
        }

        private fun notifyParent(type: DocumentChange.Type, mutableList: MutableList<Int>) {
            when (type) {

                DocumentChange.Type.MODIFIED ->
                    mutableList.forEach { notifyModifiedDocument(num, it) }

                DocumentChange.Type.REMOVED ->
                    mutableList.forEach { notifyRemovedDocument(num, it) }

                DocumentChange.Type.ADDED ->
                    mutableList.forEach { notifyNewDocument(num, it) }
            }
        }

        private fun findById(id: String): Int {

            for (i in 0 until this.size) {

                if (this[i].id == id) {

                    return i
                }
            }

            return -1
        }

        private fun fetchDocumentChanges(allChanges: List<DocumentChange>): LinkedHashMap<DocumentChange.Type, MutableList<Int>> {

            val map = linkedMapOf(
                    DocumentChange.Type.MODIFIED to mutableListOf<Int>(),
                    DocumentChange.Type.REMOVED to mutableListOf(),
                    DocumentChange.Type.ADDED to mutableListOf()
            )

            for (change in allChanges) {
                when (change.type) {

                    DocumentChange.Type.ADDED -> {
                        this.add(change.document)
                        map[DocumentChange.Type.ADDED]!!.add(size - 1)
                    }

                    else -> fetchData(change, map[change.type]!!)
                }
            }

            return map
        }

        private fun fetchData(change: DocumentChange, list: MutableList<Int>) {

            val i = findById(change.document.id)

            if (i != -1) {

                if (change.type == DocumentChange.Type.REMOVED) {

                    removeAt(i)

                } else {

                    set(i, change.document)
                }
                list.add(i)
            }
        }
    }
}