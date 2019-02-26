package ga.lupuss.anotherbikeapp.ui

import android.app.Notification
import android.content.Context
import android.support.v4.app.NotificationCompat
import ga.lupuss.anotherbikeapp.R
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import ga.lupuss.anotherbikeapp.base.ThemedActivity
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.ui.extensions.getColorForAttr
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity
import ga.lupuss.anotherbikeapp.ui.modules.main.MainPresenter

class TrackingNotification {



    private var builder: NotificationCompat.Builder? = null

    fun initNotificationChannelOreo(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = context.getString(R.string.titleActivityTacking)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_STRING, name, importance)
            channel.setSound(null, null)
            channel.description = context.getString(R.string.notification_description)
            channel.enableVibration(false)
            channel.enableLights(false)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun generatePendingIntent(intent: Intent, parentActivity: ThemedActivity): PendingIntent {

        intent.putExtra(MainActivity.REQUEST_CODE_KEY, MainPresenter.Request.TRACKING_NOTIFICATION_REQUEST)

        return PendingIntent.getActivity(
                parentActivity,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun build(context: Context,
              resourceResolver: ResourceResolver,
              statistic: Map<Statistic.Name, Statistic<*>>?,
              onClick: PendingIntent): Notification {

        val content =
                if (statistic != null)
                    "${resourceResolver.resolve(statistic.getValue(Statistic.Name.DURATION))}  |  " +
                            "${resourceResolver.resolve(statistic.getValue(Statistic.Name.DISTANCE))}  |  " +
                            " ${resourceResolver.resolve(statistic.getValue(Statistic.Name.AVG_SPEED))}"
                else ""

        val title =
                if (statistic != null)
                    resourceResolver.resolve(statistic.getValue(Statistic.Name.STATUS))
                else
                    context.getString(R.string.trackingInProgress)

        if (builder == null) {

            builder = NotificationCompat.Builder(context, CHANNEL_STRING)
                    .setSmallIcon(R.drawable.ic_notification_24dp)
                    .setColor(context.theme.getColorForAttr(R.attr.colorAccent))
                    .setContentTitle(context.getString(R.string.trackingInProgress))
                    .setContentText(content)
                    .setContentIntent(onClick)
                    .setOnlyAlertOnce(true)
        } else {

            builder!!.setContentTitle(title)
                    .setContentText(content)
        }

        return builder!!.build()

    }

    fun clearReferences() {
        builder = null
    }

    companion object {
        const val CHANNEL_STRING =  "trackingNotificationChannel"
        const val ID = 2137
    }
}