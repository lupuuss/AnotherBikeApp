package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver
import ga.lupuss.anotherbikeapp.models.dataclass.*
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.Status

@Suppress("REDUNDANT_ELSE_IN_WHEN")
class AndroidStringsResolver(private val context: Context) : StringsResolver {

    override fun resolve(message: Message): String = context.getString(when (message) {

        Message.ACCOUNT_CREATED -> R.string.accountCreated
        Message.USER_EXISTS -> R.string.userExists
        Message.SOMETHING_GOES_WRONG -> R.string.somethingGoesWrong
        Message.FILL_ALL_FIELDS -> R.string.fillAllFileds
        Message.NO_INTERNET_CONNECTION -> R.string.noInternetConnection
        Message.EMAIL_OR_PASSWORD_BLANK -> R.string.passwordOrEmailBlank
        Message.INCORRECT_CREDENTIALS -> R.string.wrongCredentials
        Message.NO_PERMISSION -> R.string.noPermission
        Message.LOCATION_NOT_AVAILABLE -> R.string.locationNotAvailable
    })

    override fun resolve(text: Text): String = context.getString(when (text) {
        Text.DEFAULT_ROUTE_NAME -> R.string.default_route_name
    })


    override fun resolve(statName: Statistic.Name): String = context.getString(when (statName) {

        Statistic.Name.SPEED -> R.string.speed
        Statistic.Name.AVG_SPEED -> R.string.avgSpeed
        Statistic.Name.MAX_SPEED -> R.string.maxSpeed
        Statistic.Name.DISTANCE -> R.string.distance
        Statistic.Name.DURATION -> R.string.duration
        Statistic.Name.STATUS -> R.string.status
        Statistic.Name.START_TIME -> R.string.startTime
    })

    override fun resolve(unit: Statistic.Unit): String = context.getString(when (unit) {

        Statistic.Unit.M_S -> R.string.unitSpeedMs
        Statistic.Unit.KM_H -> R.string.unitSpeedKmh
        Statistic.Unit.M -> R.string.unitDistanceM
        Statistic.Unit.KM -> R.string.unitDistanceKm
    })

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