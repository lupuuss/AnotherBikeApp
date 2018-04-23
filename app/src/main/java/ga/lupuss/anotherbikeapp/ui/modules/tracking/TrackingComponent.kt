package ga.lupuss.anotherbikeapp.ui.modules.tracking

import dagger.Component
import javax.inject.Scope

@Scope
annotation class TrackingScope

@TrackingScope
@Component(modules = [TrackingModule::class])
interface TrackingComponent {
    fun inject(trackingActivity: TrackingActivity)
}