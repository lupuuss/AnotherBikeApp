package ga.lupuss.anotherbikeapp.trackingservice

import android.os.Handler
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.StatisticsMathProvider
import javax.inject.Scope

@Component(dependencies = [AnotherBikeAppComponent::class], modules = [TrackingServiceModule::class])
@TrackingServiceScope
interface TrackingServiceComponent {

    fun inject(trackingService: TrackingService)
    fun providesHandler(): Handler
    fun providesMath(): StatisticsMathProvider
    fun providesLocationClient(): FusedLocationProviderClient
}

@Scope
annotation class TrackingServiceScope