package ga.lupuss.anotherbikeapp.ui.modules.tracking

import dagger.Component
import ga.lupuss.anotherbikeapp.di.UserComponent
import javax.inject.Scope

@Scope
annotation class TrackingScope

@TrackingScope
@Component(modules = [TrackingModule::class], dependencies = [UserComponent::class])
interface TrackingComponent {
    fun inject(trackingActivity: TrackingActivity)
}