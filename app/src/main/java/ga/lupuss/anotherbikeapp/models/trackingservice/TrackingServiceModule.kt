package ga.lupuss.anotherbikeapp.models.trackingservice

import android.content.Context
import android.os.Handler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.StatisticsMathProvider

@Module
class TrackingServiceModule {

    val handler: Handler
        @Provides
        @TrackingServiceScope
        get() = Handler()


    @Provides
    @TrackingServiceScope
    fun statisticMathProvider(timeProvider: () -> Long) =
            StatisticsMathProvider(timeProvider)

    @Provides
    @TrackingServiceScope
    fun locationClient(context: Context): FusedLocationProviderClient {

        return LocationServices.getFusedLocationProviderClient(context)
    }


}