package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import android.os.Handler
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.trackingservice.TrackingServiceScope

@Module
class TrackingServiceModule {

    val handler: Handler
        @Provides
        get() = Handler()

    @TrackingServiceScope
    val statisticsMathProvider: StatisticsMathProvider
        @Provides
        @TrackingServiceScope
        get() = StatisticsMathProvider(System::currentTimeMillis)
}