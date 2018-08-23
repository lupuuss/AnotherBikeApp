package ga.lupuss.anotherbikeapp.models.firebase

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.TestScheduler
import org.junit.Test

class QueryLoadingManagerTest {

    private val testScheduler = TestScheduler()

    private val queryLoadingManager = QueryLoadingManager(testScheduler, testScheduler)

    private val query = mock<Query>{}
    private val listenerTest = mock<OnDataSetChanged> {  }
    private val listeners = mutableListOf(listenerTest)

    @Test
    fun init_shouldRegisterSnapshotListener() {

        queryLoadingManager.init(query, listeners)
        verify(query, times(1)).addSnapshotListener(any())
    }

    @Test
    fun onEvent_shouldIgnoreFirstCall() {

        queryLoadingManager.init(query,listeners)
        queryLoadingManager.onEvent(mock {  }, mock {  })

        verify(listenerTest, never()).onDocumentDeleted(any())
        verify(listenerTest, never()).onDocumentModified(any())
        verify(listenerTest, never()).onNewDocument(any())
    }

    @Test
    fun onEvent_shouldNotifyParentAfterFirstCall() {

        queryLoadingManager.init(query, listeners)
        queryLoadingManager.onEvent(mock {  }, mock {  })

        verify(listenerTest, never()).onDocumentDeleted(any())
        verify(listenerTest, never()).onDocumentModified(any())
        verify(listenerTest, never()).onNewDocument(any())

        queryLoadingManager.onEvent(mock {
            on { isEmpty }.then { false }
            on { documentChanges }.then { listOf(documentChangeMock(DocumentChange.Type.REMOVED)) }
        }, mock {  })

        testScheduler.triggerActions()

        verify(listenerTest, times(1)).onDocumentDeleted(any())
        verify(listenerTest, never()).onDocumentModified(any())
        verify(listenerTest, never()).onNewDocument(any())

        queryLoadingManager.onEvent(mock {
            on { isEmpty }.then { false }
            on { documentChanges }.then { listOf(documentChangeMock(DocumentChange.Type.MODIFIED)) }
        }, mock {  })

        testScheduler.triggerActions()

        verify(listenerTest, times(1)).onDocumentDeleted(any())
        verify(listenerTest, times(1)).onDocumentModified(any())
        verify(listenerTest, never()).onNewDocument(any())

        queryLoadingManager.onEvent(mock {
            on { isEmpty }.then { false }
            on { documentChanges }.then { listOf(documentChangeMock(DocumentChange.Type.ADDED)) }
        }, mock {  })

        testScheduler.triggerActions()

        verify(listenerTest, times(1)).onDocumentDeleted(any())
        verify(listenerTest, times(1)).onDocumentModified(any())
        verify(listenerTest, times(1)).onNewDocument(any())
    }

    @Test
    fun loadMoreDocuments_whenResultLessThanLimit_shouldTriggerOnNewDocumentAndOnEndOfData() {

        queryLoadingManager.init(rootQueryWithSubQuery(successLoadMoreDocsQuery(listOf(mock {  }))), listeners)

        val endOfData = mock<() -> Unit> {}
        val fail = mock<(Exception) -> Unit> {}
        queryLoadingManager.loadMoreDocuments(endOfData, fail, mock {  })

        verify(listenerTest, times(1)).onNewDocument(any())
        verify(endOfData, times(1)).invoke()
        verifyZeroInteractions(fail)
    }

    @Test
    fun loadMoreDocuments_whenEmptyResult_shouldTriggerEndOfData() {

        queryLoadingManager.init(rootQueryWithSubQuery(successLoadMoreDocsQuery(emptyList())), listeners)

        val endOfData = mock<() -> Unit> {}
        val fail = mock<(Exception) -> Unit> {}
        queryLoadingManager.loadMoreDocuments(endOfData, fail, mock {  })

        verify(endOfData, times(1)).invoke()
        verifyZeroInteractions(listenerTest)
        verifyZeroInteractions(fail)
    }

    @Test
    fun loadMoreDocuments_whenFailed_shouldTriggerOnFail() {

        queryLoadingManager.init(rootQueryWithSubQuery(failLoadMoreDocsQuery(Exception())), listeners)
        val endOfData = mock<() -> Unit> {}
        val fail = mock<(Exception) -> Unit> {}

        queryLoadingManager.loadMoreDocuments(endOfData, fail, mock {  })
        verify(fail, times(1)).invoke(any())
        verifyZeroInteractions(endOfData)
        verifyZeroInteractions(listenerTest)
    }

    companion object {
        fun documentChangeMock(type: DocumentChange.Type, newIndex: Int = 0, oldIndex: Int = 0): DocumentChange {

            return mock {
                on { this.type }.then { type }
                on { this.newIndex }.then { newIndex }
                on { this.oldIndex }.then { oldIndex }
                on { this.document }.then { mock<QueryDocumentSnapshot>{} }
            }
        }

        fun successLoadMoreDocsQuery(documents: List<DocumentSnapshot>): Query {

            return mock {
                on { get() }.then {
                    mock<Task<QuerySnapshot?>> {
                        on { addOnSuccessListener(any<Activity>(), any()) }.then {

                            it.getArgument<OnSuccessListener<QuerySnapshot>>(1)
                                    .onSuccess(mock {
                                        on { isEmpty }.then { documents.isEmpty() }
                                        on { this.documents }.then { documents }
                                    })
                            mock<Task<QuerySnapshot>> {}
                        }
                    }
                }
            }
        }

        fun failLoadMoreDocsQuery(exception: Exception): Query {

            return mock {
                on { get() }.then {
                    mock<Task<QuerySnapshot?>> {
                        on { addOnSuccessListener(any<Activity>(), any()) }.then {
                            mock<Task<QuerySnapshot>> {
                                on { addOnFailureListener(any<Activity>(), any()) }.then {
                                    it.getArgument<OnFailureListener>(1).onFailure(exception)
                                    mock<Task<QuerySnapshot>> {}
                                }
                            }
                        }
                    }
                }
            }
        }

        fun rootQueryWithSubQuery(subQuery: Query): Query {

            return mock {
                on { limit(any()) }.then { subQuery }
                on { startAfter() }.then {
                    mock<Query> {
                        on { limit(any()) }.then { subQuery }
                    }
                }
            }
        }
    }
}