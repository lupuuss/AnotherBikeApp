package ga.lupuss.anotherbikeapp.models.android

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Bundle
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.base.ThemedActivity
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.ui.TrackingNotification
import junit.framework.Assert
import org.junit.Test

class AndroidTrackingServiceGovernorTest {

    private val notificationManager: NotificationManager = mock { }
    private val trackingServiceGovernor = AndroidTrackingServiceGovernor(mock { }, mock { })
    private val parentActivity: ThemedActivity = mock {

        on { checkLocationPermission() }.then { true }
        on { getSystemService(Service.NOTIFICATION_SERVICE) }.then{ notificationManager }
    }

    private val bundleIsServiceActiveTrue = mock<Bundle> {
        on { getBoolean(ga.lupuss.anotherbikeapp.models.android.AndroidTrackingServiceGovernor.IS_SERVICE_ACTIVE_KEY) }
                .then { true }
    }

    private val bundleIsServiceActiveFalse = mock<Bundle> {
        on { getBoolean(ga.lupuss.anotherbikeapp.models.android.AndroidTrackingServiceGovernor.IS_SERVICE_ACTIVE_KEY) }
                .then { false }
    }

    @Test
    fun init_whenRestoringState_shouldBindService() {

        trackingServiceGovernor.init(
                parentActivity,
                mock {  }
        )

        verify(parentActivity, never())
                .bindService(any(), eq(trackingServiceGovernor), eq(Context.BIND_AUTO_CREATE))

        trackingServiceGovernor.init(
                parentActivity,
                mock {
                    on { getBoolean(AndroidTrackingServiceGovernor.IS_SERVICE_ACTIVE_KEY) }
                        .then { true }
                }
        )

        verify(parentActivity, times(1))
                .bindService(any(), eq(trackingServiceGovernor), eq(Context.BIND_AUTO_CREATE))
    }

    @Test
    fun saveInstanceState_shouldSaveIsServiceActiveToBundle() {

        val bundle: Bundle = mock {  }

        trackingServiceGovernor.init(
                parentActivity,
                bundleIsServiceActiveTrue
        )

        trackingServiceGovernor.saveInstanceState(bundle)
        verify(bundle, times(1))
                .putBoolean(AndroidTrackingServiceGovernor.IS_SERVICE_ACTIVE_KEY, true)

        trackingServiceGovernor.init(
                parentActivity,
                bundleIsServiceActiveFalse

        )

        trackingServiceGovernor.saveInstanceState(bundle)
        verify(bundle, times(1))
                .putBoolean(AndroidTrackingServiceGovernor.IS_SERVICE_ACTIVE_KEY, false)
    }

    @Test
    fun destroy_whenFinishingAndServiceIsActive_shouldStopTracking() {

        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveFalse)
        trackingServiceGovernor.onServiceConnected(mock {  }, mock<TrackingService.ServiceBinder> {  })

        trackingServiceGovernor.destroy(true)

        assert(!trackingServiceGovernor.isServiceActive)
        assert(trackingServiceGovernor.serviceBinder == null)
        verify(parentActivity, times(1)).unbindService(trackingServiceGovernor)
        verify(parentActivity, times(1)).stopService(any())
    }

    @Test
    fun destroy_whenNotFinishingAndServiceIsActive_shouldUnbindService() {

        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveFalse)
        trackingServiceGovernor.onServiceConnected(mock {  }, mock<TrackingService.ServiceBinder> {  })

        trackingServiceGovernor.destroy(false)

        verify(parentActivity, times(1)).unbindService(trackingServiceGovernor)
    }

    @Test
    fun startTracking_whenBinderIsNotNull_shouldInvokeCallbackWithoutInitializingTracking() {

        var callbackTriggered = 0
        val serviceBinder = mock<TrackingService.ServiceBinder> {  }

        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveFalse)
        trackingServiceGovernor.onServiceConnected(mock { }, serviceBinder)

        trackingServiceGovernor.startTracking(object : TrackingServiceGovernor.OnTrackingRequestDone{
            override fun onTrackingRequestDone() {
                callbackTriggered++
            }

            override fun onTrackingRequestNoPermission() {}
        })

        Assert.assertEquals(1, callbackTriggered)
        verify(parentActivity, never()).startService(any())
        verify(parentActivity, never()).bindService(any(), any(), any())
    }

    @Test
    fun startTracking_whenPermissionIsOk_shouldInitTracking() {

        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveFalse)
        trackingServiceGovernor.startTracking(mock {  })

        verify(parentActivity, never()).requestLocationPermission(any())
        verify(parentActivity, times(1)).startService(any())
        verify(parentActivity, times(1))
                .bindService(any(), eq(trackingServiceGovernor), eq(Context.BIND_AUTO_CREATE))
    }

    @Test
    fun startTracking_shouldInitTracking_whenUserAcceptedPermission() {

        val parentActivity = mock<ThemedActivity> {
            on { checkLocationPermission() }.then { false }
            on { requestLocationPermission(any()) }
                    .then { (it.getArgument(0) as ((Boolean) -> Unit)?)?.invoke(true) } // imitates user's consent
        }
        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveFalse)
        trackingServiceGovernor.startTracking(mock {  })

        verify(parentActivity, times(1)).requestLocationPermission(any())
        verify(parentActivity, times(1)).startService(any())
        verify(parentActivity, times(1))
                .bindService(any(), eq(trackingServiceGovernor), eq(Context.BIND_AUTO_CREATE))
    }

    @Test
    fun startTracking_shouldTriggerCallback_whenUserRejectPermission() {

        val parentActivity = mock<ThemedActivity> {
            on { checkLocationPermission() }.then { false }
            on { requestLocationPermission(any()) }
                    .then { (it.getArgument(0) as ((Boolean) -> Unit)?)?.invoke(false) } // imitates user's rejection
        }

        var callbackTriggered = 0

        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveFalse)
        trackingServiceGovernor.startTracking(object : TrackingServiceGovernor.OnTrackingRequestDone{
            override fun onTrackingRequestDone() {}

            override fun onTrackingRequestNoPermission() {
                callbackTriggered++
            }

        })

        Assert.assertEquals(1, callbackTriggered)
        verify(parentActivity, times(1)).requestLocationPermission(any())
        verify(parentActivity, never()).startService(any())
        verify(parentActivity, never()).bindService(any(),any(),any())
    }

    @Test
    fun onServiceConnected_shouldSetBinder() {

        val serviceBinder = mock<TrackingService.ServiceBinder> {  }

        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveFalse)
        trackingServiceGovernor.onServiceConnected(mock {  }, serviceBinder)

        verify(serviceBinder, times(1)).initNotification(eq(TrackingNotification.ID), anyOrNull())
        verify(serviceBinder, times(1)).addOnStatsUpdateListener(trackingServiceGovernor)
        assert(trackingServiceGovernor.serviceBinder == serviceBinder)
    }

    @Test
    fun onStatsUpdate_whenServiceIsActive_shouldNotifyNotification() {

        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveTrue)
        trackingServiceGovernor.onStatsUpdate(mock {  })

        verify(notificationManager, times(1)).notify(eq(TrackingNotification.ID), anyOrNull())
    }

    @Test
    fun onServiceConnected_shouldInitFields_whenServiceIsNotActive() {

        var callbackTriggered = 0

        val serviceBinder = mock<TrackingService.ServiceBinder> {  }
        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveFalse)
        trackingServiceGovernor.startTracking(object : TrackingServiceGovernor.OnTrackingRequestDone{
            override fun onTrackingRequestDone() {
                callbackTriggered++
            }
            override fun onTrackingRequestNoPermission() {
                callbackTriggered++
            }

        })
        trackingServiceGovernor.onServiceConnected(mock {  }, serviceBinder)

        Assert.assertEquals(serviceBinder, trackingServiceGovernor.serviceBinder)
        assert(trackingServiceGovernor.isServiceActive)
        Assert.assertEquals(1, callbackTriggered)
    }

    @Test
    fun onServiceDisconnected_shouldDoCleanUp() {

        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveTrue)
        trackingServiceGovernor.onServiceConnected(mock {  }, mock<TrackingService.ServiceBinder> {  })

        trackingServiceGovernor.onServiceDisconnected(mock {  })

        assert(!trackingServiceGovernor.isServiceActive)
        assert(trackingServiceGovernor.serviceBinder == null)
    }

    @Test
    fun stopTracking_shouldDoCleanUpAndStopTrackingService() {

        trackingServiceGovernor.init(parentActivity, bundleIsServiceActiveTrue)
        trackingServiceGovernor.onServiceConnected(mock {  }, mock<TrackingService.ServiceBinder> {  })

        trackingServiceGovernor.stopTracking()

        assert(!trackingServiceGovernor.isServiceActive)
        assert(trackingServiceGovernor.serviceBinder == null)
        verify(parentActivity, times(1)).unbindService(trackingServiceGovernor)
        verify(parentActivity, times(1)).stopService(any())
    }
}