package ga.lupuss.anotherbikeapp.trackingservice

import android.os.Handler
import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.StatisticsMathProvider
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.TrackingServiceModule
import javax.inject.Scope

@Component(dependencies = [AnotherBikeAppComponent::class], modules = [TrackingServiceModule::class])
@TrackingServiceScope
interface TrackingServiceComponent {

    fun inject(trackingService: TrackingService)
    fun providesHandler(): Handler
    fun providesMath(): StatisticsMathProvider
}

@Scope
annotation class TrackingServiceScope