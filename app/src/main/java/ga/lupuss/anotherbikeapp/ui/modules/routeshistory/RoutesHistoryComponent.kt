package ga.lupuss.anotherbikeapp.ui.modules.routeshistory

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import javax.inject.Scope

@Scope
annotation class RoutesHistoryComponentScope

@RoutesHistoryComponentScope
@Component(modules = [RoutesHistoryModule::class], dependencies = [AnotherBikeAppComponent::class])
interface RoutesHistoryComponent {

    fun inject(fragment: RoutesHistoryFragment)
}
