package ga.lupuss.anotherbikeapp.trackingservice

import android.content.Context
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.StatisticsManager
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.StatisticsManagerImpl

@Module
class TrackingServiceModule {

    @Provides
    @TrackingServiceScope
    fun providesStatisticsManager(context: Context): StatisticsManager =
            StatisticsManagerImpl(context)
}