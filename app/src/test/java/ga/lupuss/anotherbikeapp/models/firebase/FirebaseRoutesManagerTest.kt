package ga.lupuss.anotherbikeapp.models.firebase

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData
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
}