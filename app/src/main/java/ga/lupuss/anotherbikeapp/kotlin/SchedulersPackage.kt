package ga.lupuss.anotherbikeapp.kotlin

import io.reactivex.Scheduler

class SchedulersPackage(
        val backScheduler: Scheduler,
        val frontScheduler: Scheduler
)