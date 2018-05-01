package ga.lupuss.anotherbikeapp

import android.content.Context
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit

fun dpToPixels(context: Context, dp: Float): Int {

    return (context.resources.displayMetrics.density * dp + 0.5F).toInt()
}

fun resolveTimeString(context: Context, time: Long): String {

    fun generateTimeString(count: Long, unitId: Int) =
            "$count ${context.getString(unitId)} ${context.getString(R.string.ago)}"

    val date = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault())
    val now = Instant.now().atZone(ZoneId.systemDefault())

    val seconds = ChronoUnit.SECONDS.between(date, now)

    if (seconds < 60) {

        return generateTimeString(seconds, R.string.seconds)
    }

    val minutes = ChronoUnit.MINUTES.between(date, now)

    if (minutes < 60) {
        return generateTimeString(minutes, R.string.minutes)
    }

    val hours = ChronoUnit.HOURS.between(date, now)

    if (hours < 24) {
        return generateTimeString(hours, R.string.hours)
    }

    val days = ChronoUnit.DAYS.between(date, now)

    if (days < 7) {

        return generateTimeString(days, R.string.days)
    }

    val weeks = ChronoUnit.WEEKS.between(date, now)

    if (weeks < 5) {

        return generateTimeString(weeks, R.string.weeks)
    }

    val months = ChronoUnit.MONTHS.between(date, now)

    return if (months < 12) {

        generateTimeString(months, R.string.months)
    } else {

        generateTimeString(ChronoUnit.YEARS.between(date, now), R.string.years)
    }
}