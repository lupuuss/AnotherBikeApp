package ga.lupuss.anotherbikeapp.ui.modules.routeshistory

import dagger.Component
import ga.lupuss.anotherbikeapp.di.UserComponent
import javax.inject.Scope

@Scope
annotation class RoutesHistoryComponentScope

@RoutesHistoryComponentScope
@Component(modules = [RoutesHistoryModule::class], dependencies = [UserComponent::class])
interface RoutesHistoryComponent {

    fun inject(fragment: RoutesHistoryFragment)
}
