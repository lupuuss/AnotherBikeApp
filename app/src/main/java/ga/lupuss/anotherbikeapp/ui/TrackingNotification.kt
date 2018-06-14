package ga.lupuss.anotherbikeapp.ui

import android.app.Notification
import android.content.Context
import android.support.v4.app.NotificationCompat
import ga.lupuss.anotherbikeapp.R
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.ui.extensions.getColorForAttr

class TrackingNotification {



    private var builder: NotificationCompat.Builder? = null

    fun initNotificationChannelOreo(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "Simple name"
            val description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_STRING, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun build(context: Context,
              stringsResolver: StringsResolver,
              statistic: Map<Statistic.Name, Statistic<*>>?): Notification {

        val content =
                if (statistic != null)
                    "${stringsResolver.resolve(Statistic.Name.DURATION, statistic[Statistic.Name.DURATION]!!)} |" +
                            " ${stringsResolver.resolve(Statistic.Name.DISTANCE, statistic[Statistic.Name.DISTANCE]!!)}"
                else ""

        val title =
                if (statistic != null)
                    stringsResolver.resolve(statistic[Statistic.Name.STATUS]!!)
                else
                    context.getString(R.string.trackingInProgress)

        if (builder == null) {

            builder = NotificationCompat.Builder(context, CHANNEL_STRING)
                    .setSmallIcon(R.drawable.ic_notification_24dp)
                    .setColor(context.theme.getColorForAttr(R.attr.colorPrimaryDark))
                    .setContentTitle(context.getString(R.string.trackingInProgress))
                    .setContentText(content)
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