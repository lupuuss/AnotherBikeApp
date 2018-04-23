package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import android.os.Handler
import javax.inject.Inject

class Timer @Inject constructor(val handler: Handler) {

    private val tickTime = 1000L

    var onTimerTick: ((Long) -> Unit)? = null
    var isStarted = false
    var isPaused = false
    private var time = 0L
    private var continueIt = true

    private val runnable = object : Runnable {

        override fun run() {

            if (!isPaused) {
                time += tickTime
            }

            onTimerTick?.invoke(time)

            if (continueIt) handler.postDelayed(this, tickTime)
        }
    }

    fun start() {

        isStarted = true
        continueIt = true
        handler.postDelayed(runnable, tickTime)
    }

    fun pause() {

        if (isStarted) {

            isPaused = true
        }
    }

    fun unpause() {

        isPaused = false
    }

    fun stop() {

        handler.removeCallbacksAndMessages(null)
        isStarted = false
        continueIt = false
    }
}