package ga.lupuss.anotherbikeapp.models.trackingservice

import android.os.Handler
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Component
import ga.lupuss.anotherbikeapp.di.UserComponent
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.StatisticsMathProvider
import javax.inject.Scope

@Component(dependencies = [UserComponent::class], modules = [TrackingServiceModule::class])
@TrackingServiceScope
interface TrackingServiceComponent {

    fun inject(trackingService: TrackingService)
    fun providesHandler(): Handler
    fun providesMath(): StatisticsMathProvider
    fun providesLocationClient(): FusedLocationProviderClient
}

@Scope
annotation class TrackingServiceScope