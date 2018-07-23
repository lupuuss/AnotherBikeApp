package ga.lupuss.anotherbikeapp.models.firebase

import android.app.Activity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.RouteData
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebasePoints
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseRouteData
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseRouteReference
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseShortRouteData
import org.junit.Test

import org.junit.Assert.*

class FirebaseRoutesManagerTest {

    private val mockedDocument = mock<DocumentSnapshot> {
        on { toObject(any<Class<FirebaseShortRouteData>>()) }.then {
            mock<FirebaseShortRouteData> {
                on { toShortRouteData() }.then {
                    ShortRouteData.Instance("", 0.0, 0.0, 0L, 0L)
                }
            }
        }
    }

    private val firebaseFirestore = mock<FirebaseFirestore> {
        on { collection(any()) }.then {
            mock<CollectionReference> {
                on { orderBy(any<String>(), any()) }.then { mock<Query> { } }
            }
        }
    }

    private val queryManager = mock<QueryLoadingManager> {
        on { loadMoreDocuments(any(), any(), any()) }.then {
            (it.getArgument(0) as () -> Unit).invoke()
            (it.getArgument(1) as (Exception) -> Unit).invoke(Exception())
        }
        on { readDocument(any()) }.then { mockedDocument }
    }

    private val firebaseAuthInteractor = mock<FirebaseAuth> {
        on { currentUser }.then {
            mock<FirebaseUser> { on { uid }.then { "UID" }}
        }
    }

    private val routeKeeper = mock<TempRouteKeeper> {}
    private val gson = mock<Gson> {}

    private val routesManager = FirebaseRoutesManager(
            firebaseAuthInteractor,
            firebaseFirestore,
            routeKeeper,
            mock {  },
            gson,
            queryManager
    )

    private fun buildFirebaseRouteReferenceWith(routeDataExists: Boolean, pointsExists: Boolean, pointsSuccess: Boolean): FirebaseRouteReference {

        val mockedDocument = mock<DocumentSnapshot> {
            on { toObject(any<Class<FirebaseRouteData>>()) }.then {
                mock<FirebaseRouteData> {
                    on { toRouteData(any()) }.then {
                        RouteData.Instance(
                                "", 0.0, 0.0, 0L,
                                0L, "", 0.0, 0.0,
                                0.0, 0.0
                        )
                    }
                }
            }

            on { exists() }.then { routeDataExists }
        }

        val pointsTaskFailure = mock<Task<DocumentSnapshot>> {
            on { addOnFailureListener(any<Activity>(), any()) }.then {
                if (!pointsSuccess) {

                    (it.getArgument<OnFailureListener>(1))
                            .onFailure(Exception())
                }
                mock<Task<DocumentSnapshot>> {}
            }
        }

        val pointsTask = mock<Task<DocumentSnapshot>> {

            on { addOnSuccessListener(any<Activity>(), any()) }.then {
                if (pointsSuccess) {
                    it.getArgument<OnSuccessListener<DocumentSnapshot>>(1)
                            .onSuccess(mock {
                                on { exists() }.then { pointsExists }
                                on { toObject(any<Class<*>>())}.then {
                                    FirebasePoints()
                                }
                            })
                }

                pointsTaskFailure
            }
        }

        val mainTask = mock<Task<DocumentSnapshot>> {
            on { continueWithTask(any<Continuation<DocumentSnapshot, Task<DocumentSnapshot>>>()) }.then {

                        return@then it.getArgument<Continuation<DocumentSnapshot, Task<DocumentSnapshot>>>(0)
                                .then(mock {
                                    on { result }.then { mockedDocument }
                                })
                    }
        }

        return mock {
            on { pointsReference }.then {
                mock<DocumentReference> {
                    on { get() }.then { pointsTask }
                }
            }

            on { routeReference }.then {

                mock<DocumentReference> {
                    on { get() }.then { mainTask }
                }
            }
        }

    }

    @Test
    fun requestMoreShortRouteData_whenOwnerIsNotActivity_shouldThrowException() {
        try {
            routesManager.requestMoreShortRouteData(mock { }, mock { })
            fail("RoutesManager should throw IllegalArgumentException")

        } catch(e: IllegalArgumentException) { }
    }

    @Test
    fun requestMoreShortRouteData_whenProperArguments_shouldPassThemToQueryManager() {

        val onRequestMore = mock<RoutesManager.OnRequestMoreShortRouteDataListener> {}
        val activity = mock<Activity> { }
        routesManager.requestMoreShortRouteData(onRequestMore, activity)

        verify(queryManager, times(1)).loadMoreDocuments(any(), any(), eq(activity))
        verify(onRequestMore, times(1)).onDataEnd()
        verify(onRequestMore, times(1)).onFail(any())
    }

    @Test
    fun readShortRouteData_shouldDelegateToQueryManager() {

        routesManager.readShortRouteData(0)
        verify(queryManager, times(1)).readDocument(0)
    }

    @Test
    fun shortRouteDataCount_shouldDelegateToQueryManager() {

        routesManager.shortRouteDataCount()
        verify(queryManager, times(1)).size
    }

    @Test
    fun getReference_shouldReturnFirebaseRouteReference() {

        assertEquals(FirebaseRouteReference::class.java, routesManager.getRouteReference(0)::class.java)
    }

    @Test
    fun requestExtendedRouteData_whenRequestOwnerIsNotActivity_shouldThrowException() {
        try {

            routesManager.requestExtendedRoutesData(mock<FirebaseRouteReference> {}, mock {}, null)
            fail("IllegalArgumentException should have been thrown!")

        } catch (e: IllegalArgumentException) {

            assertEquals(FirebaseRoutesManager.WRONG_OWNER, e.message)
        }
    }

    @Test
    fun requestExtendedRouteData_whenRouteReferenceIsNotFirebaseRouteReference_shouldThrowException() {
        try {

            routesManager.requestExtendedRoutesData(mock {}, mock {}, mock<Activity> {})
            fail("IllegalArgumentException should have been thrown!")

        } catch (e: IllegalArgumentException) {

            assertEquals(FirebaseRoutesManager.WRONG_REFERENCE_MESSAGE, e.message)
        }
    }

    @Test
    fun requestExtendedRouteData_whenRouteReferenceIsNull_shouldCallMissingDataCallback() {

        val callback = mock<RoutesManager.OnRequestExtendedRouteDataListener> {}
        routesManager.requestExtendedRoutesData(
                mock<FirebaseRouteReference> {
                    on { routeReference }.then { null }
                },
                callback,
                mock<Activity> {}
        )

        verify(callback, times(1)).onMissingData()
        verify(callback, never()).onDataOk(any())
    }

    @Test
    fun requestExtendedRouteData_whenRouteDataNotExistsAndPointsFailed_shouldCallOnMissingDataCallback() {

        val callback = mock<RoutesManager.OnRequestExtendedRouteDataListener> {}
        routesManager.requestExtendedRoutesData(
                buildFirebaseRouteReferenceWith(false, false, false),
                callback,
                mock<Activity> {}
        )

        verify(callback, times(1)).onMissingData()
        verify(callback, never()).onDataOk(any())
    }

    @Test
    fun requestExtendedRouteData_whenRouteDataNotExistsAndPointsSuccess_shouldCallOnMissingDataCallback() {

        val callback = mock<RoutesManager.OnRequestExtendedRouteDataListener> {}
        routesManager.requestExtendedRoutesData(
                buildFirebaseRouteReferenceWith(false, true, true),
                callback,
                mock<Activity> {}
        )

        verify(callback, times(1)).onMissingData()
        verify(callback, never()).onDataOk(any())
    }

    @Test
    fun requestExtendedRouteData_whenPointsNotExists_shouldCallOnOkCallback() {

        val callback = mock<RoutesManager.OnRequestExtendedRouteDataListener> {}
        routesManager.requestExtendedRoutesData(
                buildFirebaseRouteReferenceWith(true, false, true),
                callback,
                mock<Activity> {}
        )

        verify(callback, never()).onMissingData()
        verify(callback, times(1)).onDataOk(any())
    }

    @Test
    fun requestExtendedRouteData_whenPointsFailed_shouldCallOnOkCallback() {

        val callback = mock<RoutesManager.OnRequestExtendedRouteDataListener> {}
        routesManager.requestExtendedRoutesData(
                buildFirebaseRouteReferenceWith(true, false, false),
                callback,
                mock<Activity> {}
        )

        verify(callback, never()).onMissingData()
        verify(callback, times(1)).onDataOk(any())
    }

    @Test
    fun requestExtendedRouteData_whenEverythingIsFine_shouldCallOnOkCallback() {

        val callback = mock<RoutesManager.OnRequestExtendedRouteDataListener> {}
        routesManager.requestExtendedRoutesData(
                buildFirebaseRouteReferenceWith(true, true, true),
                callback,
                mock<Activity> {}
        )

        verify(callback, times(1)).onDataOk(any())
        verify(callback, never()).onMissingData()
    }

    @Test
    fun saveRoute_shouldDoProperSaveOnFirestore() {

        val commitBatch = mock<WriteBatch> {}

        val setBatch4 =  mock<WriteBatch> {
            on { set(anyOrNull(), any<FirebaseShortRouteData>()) }.then { commitBatch }
        }

        val setBatch3 = mock<WriteBatch> {
            on { update(anyOrNull(), any<String>(), any<DocumentReference>()) }.then { setBatch4 }
        }

        val setBatch2 = mock<WriteBatch> {
            on { set(anyOrNull(), any<FirebaseRouteData>()) }.then { setBatch3 }
        }
        
        val setBatch1 = mock<WriteBatch> {
            on { set(anyOrNull(), any<FirebasePoints>()) }.then { setBatch2 }
        }

        val firebaseFirestore: FirebaseFirestore =  mock {
            on { collection(any()) }.then {
                mock<CollectionReference> {
                    on { orderBy(any<String>(), any()) }.then { mock<Query> { } }
                    on { document() }.then { mock<DocumentReference> { } }
                    on { document(any()) }.then {
                        mock<DocumentReference> {
                            on { collection(any()) }.then {
                                mock<CollectionReference> {
                                    on { document() }.then { mock<DocumentReference> {} }
                                }
                            }
                        }
                    }
                }
            }
            on { batch() }.then {
                setBatch1 }
        }

        val routesManager = FirebaseRoutesManager(
                firebaseAuthInteractor,
                firebaseFirestore,
                routeKeeper,
                mock {},
                gson,
                queryManager
        )

        routesManager.saveRoute(ExtendedRouteData.Instance(
                "", 0.0, 0.0, 0.0, 0L, "",
                0L, 0.0, 0.0, 0.0, listOf(LatLng(0.0, 0.0))
        ))

        verify(firebaseFirestore, times(1)).collection(FirebaseRoutesManager.FIREB_POINTS)
        verify(firebaseFirestore, times(1)).collection(FirebaseRoutesManager.FIREB_ROUTES)
        verify(firebaseFirestore, times(1)).collection(FirebaseRoutesManager.FIREB_USERS)
        verify(firebaseFirestore, times(1)).batch()
        verify(setBatch1, times(1)).set(any(), any<FirebasePoints>())
        verify(setBatch2, times(1)).set(any(), any<FirebaseRouteData>())
        verify(setBatch3, times(1)).update(any(), any<String>(), any())
        verify(setBatch4, times(1)).set(any(), any<FirebaseShortRouteData>())
        verify(commitBatch, times(1)).commit()
    }

    @Test
    fun changeName_shouldDoProperChangeNameOnFirestore() {

        val commitBatch = mock<WriteBatch> { }

        val setBatch2 = mock<WriteBatch> {
            on { update(anyOrNull(), any<String>(), any<String>()) }.then { commitBatch }
        }

        val setBatch1 = mock<WriteBatch> {
            on { update(anyOrNull(), any<String>(), any<String>()) }.then { setBatch2 }
        }

        val firebaseFirestore: FirebaseFirestore =  mock {
            on { collection(any()) }.then {
                mock<CollectionReference> {
                    on { orderBy(any<String>(), any()) }.then { mock<Query> { } }
                }
            }
            on { batch() }.then { setBatch1 }
        }

        val routesManager = FirebaseRoutesManager(
                firebaseAuthInteractor,
                firebaseFirestore,
                routeKeeper,
                mock {},
                gson,
                queryManager
        )

        routesManager.changeName(mock<FirebaseRouteReference> {
            on { userRouteReference }.then { mock<DocumentReference> { } }
            on { routeReference }.then { mock<DocumentReference> { } }
        }, "Name")

        verify(setBatch1, times(1)).update(any(), eq(FirebaseRoutesManager.FIREB_NAME), eq("Name"))
        verify(setBatch2, times(1)).update(any(), eq(FirebaseRoutesManager.FIREB_NAME), eq("Name"))
    }

    @Test
    fun deleteRoute_whenRouteReferenceNotNull_shouldDeleteFromIt() {

        val deleteBatch2 = mock<WriteBatch> { }
        val deleteBatch = mock<WriteBatch> {

            on { delete(any()) }.then { deleteBatch2 }
        }
        val routesManager = FirebaseRoutesManager(
                firebaseAuthInteractor,
                mock {
                    on { collection(any()) }.then {
                        mock<CollectionReference> {
                            on { orderBy(any<String>(), any()) }.then { mock<Query> { } }
                        }
                    }
                    on { batch() }.then { deleteBatch }
                },
                routeKeeper,
                mock {},
                gson,
                queryManager
        )

        val routeRefMock = mock<DocumentReference> {}

        routesManager.deleteRoute(mock<FirebaseRouteReference> {
            on { routeReference }.then { routeRefMock }
        })

        verify(deleteBatch, times(1)).delete(routeRefMock)
        verify(deleteBatch2, times(1)).commit()
    }

    @Test
    fun deleteRoute_whenRouteUserRouteReferenceNotNull_shouldDeleteFromIt() {
        val deleteBatch2 = mock<WriteBatch> { }
        val deleteBatch = mock<WriteBatch> {

            on { delete(any()) }.then { deleteBatch2 }
        }
        val routesManager = FirebaseRoutesManager(
                firebaseAuthInteractor,
                mock {
                    on { collection(any()) }.then {
                        mock<CollectionReference> {
                            on { orderBy(any<String>(), any()) }.then { mock<Query> { } }
                        }
                    }
                    on { batch() }.then { deleteBatch }
                },
                routeKeeper,
                mock {},
                gson,
                queryManager
        )

        val userRefMock = mock<DocumentReference> {}

        routesManager.deleteRoute(mock<FirebaseRouteReference> {
            on { userRouteReference }.then { userRefMock }
        })

        verify(deleteBatch, times(1)).delete(userRefMock)
        verify(deleteBatch2, times(1)).commit()
    }

    @Test
    fun deleteRoute_whenPointsReferenceNotNull_shouldDeleteFromIt() {

        val deleteBatch2 = mock<WriteBatch> { }
        val deleteBatch = mock<WriteBatch> {

            on { delete(any()) }.then { deleteBatch2 }
        }
        val routesManager = FirebaseRoutesManager(
                firebaseAuthInteractor,
                mock {
                    on { collection(any()) }.then {
                        mock<CollectionReference> {
                            on { orderBy(any<String>(), any()) }.then { mock<Query> { } }
                        }
                    }
                    on { batch() }.then { deleteBatch }
                },
                routeKeeper,
                mock {},
                gson,
                queryManager
        )

        val pointsRefMock = mock<DocumentReference> {}

        routesManager.deleteRoute(mock<FirebaseRouteReference> {
            on { pointsReference }.then { pointsRefMock }
        })

        verify(deleteBatch, times(1)).delete(pointsRefMock)
        verify(deleteBatch2, times(1)).commit()
    }

    @Test
    fun deleteRoute_whenWrongReference_shouldThrowException() {

        try {
            routesManager.deleteRoute(mock {})
            fail("Exception should have been thrown!")
        } catch (e : IllegalArgumentException) {

        }

    }

    @Test
    fun keepTempRoute_delegatesToRouteKeeper() {
        routesManager.keepTempRoute(mock {  })
        verify(routeKeeper, times(1)).keep(any())
        verifyNoMoreInteractions(routeKeeper)
    }

    @Test
    fun getTempRoute_delegatesToRouteKeeper() {
        routesManager.getTempRoute()
        verify(routeKeeper, times(1)).getRoute()
        verifyNoMoreInteractions(routeKeeper)
    }

    @Test
    fun clearTempRoute_delegatesToRouteKeeper() {
        routesManager.clearTempRoute()
        verify(routeKeeper, times(1)).clear()
        verifyNoMoreInteractions(routeKeeper)
    }
}