package ga.lupuss.anotherbikeapp.trackingservice

import android.content.Context
import android.os.Handler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.StatisticsMathProvider

@Module
class TrackingServiceModule {

    val handler: Handler
        @Provides
        @TrackingServiceScope
        get() = Handler()

    val statisticsMathProvider: StatisticsMathProvider
        @Provides
        @TrackingServiceScope
        get() = StatisticsMathProvider(System::currentTimeMillis)


    @Provides
    @TrackingServiceScope
    fun providesLocationClient(context: Context): FusedLocationProviderClient {

        return LocationServices.getFusedLocationProviderClient(context)
    }


}