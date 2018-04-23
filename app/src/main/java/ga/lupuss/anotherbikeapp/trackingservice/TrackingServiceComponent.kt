package ga.lupuss.anotherbikeapp.trackingservice

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import javax.inject.Scope

@Component(
        modules = [TrackingServiceModule::class],
        dependencies = [AnotherBikeAppComponent::class]
)
@TrackingServiceScope
interface TrackingServiceComponent {

    fun inject(trackingService: TrackingService)
}

@Scope
annotation class TrackingServiceScope