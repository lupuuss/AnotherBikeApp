package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import android.os.Handler

class Timer(val tickTime: Long = 1000L,
            onTimerTick: (Long) -> Unit) {

    val handler = Handler()
    var isStarted = false
    var isPaused = false
    private var time = 0L
    private var continueIt = true

    private val runnable = object : Runnable {

        override fun run() {

            if (!isPaused) {
                time += tickTime
            }

            onTimerTick.invoke(time)

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