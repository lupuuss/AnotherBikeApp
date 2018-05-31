package ga.lupuss.anotherbikeapp.ui.modules.tracking

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import javax.inject.Scope

@Scope
annotation class TrackingScope

@TrackingScope
@Component(modules = [TrackingModule::class], dependencies = [AnotherBikeAppComponent::class])
interface TrackingComponent {
    fun inject(trackingActivity: TrackingActivity)
}