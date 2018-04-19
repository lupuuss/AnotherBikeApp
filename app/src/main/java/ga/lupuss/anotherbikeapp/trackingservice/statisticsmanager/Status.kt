package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import ga.lupuss.anotherbikeapp.R

/** Possible statuses of tracking feature. */
enum class Status(val descriptionId: Int) {
    LOCATION_WAIT(R.string.waiting_for_location),
    PAUSE(R.string.pause),
    RUNNING(R.string.tracking_in_progress)
}