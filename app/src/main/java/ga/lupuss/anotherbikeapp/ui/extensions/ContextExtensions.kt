package ga.lupuss.anotherbikeapp.ui.extensions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.checkPermission(permission: String): Boolean {
    val permissionStatus = ContextCompat
            .checkSelfPermission(this, permission)

    return permissionStatus == PackageManager.PERMISSION_GRANTED
}