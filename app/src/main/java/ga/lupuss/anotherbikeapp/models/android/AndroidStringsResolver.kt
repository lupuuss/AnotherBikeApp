package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.models.dataclass.*
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.Status

@Suppress("REDUNDANT_ELSE_IN_WHEN")
class AndroidStringsResolver(private val context: Context) : StringsResolver {

    override fun resolve(message: Message): String = context.getString(when (message) {

        Message.ACCOUNT_CREATED -> R.string.accountCreated
        Message.USER_EXISTS -> R.string.userExists
        Message.SOMETHING_GOES_WRONG -> R.string.somethingGoesWrong
        Message.FILL_ALL_FIELDS -> R.string.fillAllFields
        Message.NO_INTERNET_CONNECTION -> R.string.noInternetConnection
        Message.EMAIL_OR_PASSWORD_BLANK -> R.string.passwordOrEmailBlank
        Message.INVALID_CREDENTIALS_LOGIN -> R.string.wrongCredentials
        Message.NO_PERMISSION -> R.string.noPermission
        Message.LOCATION_NOT_AVAILABLE -> R.string.locationNotAvailable
        Message.USER_NOT_EXISTS -> R.string.messageUserNotExists
        Message.PASSWORD_IS_TOO_WEAK -> R.string.passwordIsTooWeak
        Message.INVALID_CREDENTIALS_CREATING -> R.string.invalidCredentialsCreating
    })

    override fun resolve(text: Text): String = context.getString(when (text) {
        Text.DEFAULT_ROUTE_NAME -> R.string.defaultRouteName
    })


    override fun resolve(statName: Statistic.Name): String = context.getString(when (statName) {

        Statistic.Name.SPEED -> R.string.speed
        Statistic.Name.AVG_SPEED -> R.string.avgSpeed
        Statistic.Name.MAX_SPEED -> R.string.maxSpeed
        Statistic.Name.DISTANCE -> R.string.distance
        Statistic.Name.DURATION -> R.string.duration
        Statistic.Name.STATUS -> R.string.status
        Statistic.Name.START_TIME -> R.string.startTime
        Statistic.Name.ALTITUDE -> R.string.altitude
        Statistic.Name.AVG_ALTITUDE -> R.string.avgAltitude
        Statistic.Name.MAX_ALTITUDE -> R.string.maxAltitude
        Statistic.Name.MIN_ALTITUDE -> R.string.minAltitude
    })

    override fun resolve(unit: Statistic.Unit): String {
        return context.getString(

                when (unit) {

                    is Statistic.Unit.Speed -> when (unit) {

                        Statistic.Unit.Speed.M_S ->  R.string.unitSpeedMs
                        Statistic.Unit.Speed.KM_H ->  R.string.unitSpeedKmh
                        Statistic.Unit.Speed.MPH -> R.string.unitSpeedMph
                    }

                    is Statistic.Unit.Distance -> when (unit) {

                        Statistic.Unit.Distance.M -> R.string.unitDistanceM
                        Statistic.Unit.Distance.KM -> R.string.unitDistanceKm
                        Statistic.Unit.Distance.MI -> R.string.unitDistanceMi
                    }

                    else -> throw IllegalArgumentException("Unknown implementation of Statistic.Unit")
                }
        )
    }

    override fun resolve(stat: TimeStatistic): String {

        fun format(time: Long) = if (time < 10) "0$time" else time.toString()


        val time = stat.value
        val hours = time / 3_600_000
        val minutes = (time - hours * 3_600_000) / 60000
        val seconds = (time - (time / 60000) * 60000) / 1000

        return "${format(hours)}:${format(minutes)}:${format(seconds)}"
    }

    override fun resolve(stat: UnitStatistic): String =
            (Math.round(stat.value * stat.unit.convertParam * 100.0) / 100.0).toString() +
                    " " + resolve(stat.unit)

    override fun resolve(stat: StatusStatistic): String = context.getString(when (stat.value) {

        Status.LOCATION_WAIT -> R.string.waitingForLocation
        Status.PAUSE -> R.string.pause
        Status.RUNNING -> R.string.trackingInProgress
    })

    override fun resolve(stat: Statistic<*>): String = when (stat) {

        is UnitStatistic -> resolve(stat)
        is TimeStatistic -> resolve(stat)
        is StringStatistic -> stat.value
        is StatusStatistic -> resolve(stat)
    }

    override fun resolve(statName: Statistic.Name, stat: Statistic<*>) =
            "${resolve(statName)}: ${resolve(stat)}"

}