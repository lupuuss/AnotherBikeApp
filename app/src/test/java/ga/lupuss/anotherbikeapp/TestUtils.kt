package ga.lupuss.anotherbikeapp

import android.app.Activity
import org.robolectric.android.controller.ActivityController
import org.robolectric.android.controller.ComponentController

fun replaceComponentInActivityController(activityController: ActivityController<*>, activity: Activity) {
    val componentField = ComponentController::class.java.getDeclaredField("component")
    componentField.isAccessible = true
    componentField.set(activityController, activity)
}