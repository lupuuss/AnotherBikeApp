package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import dagger.Component
import ga.lupuss.anotherbikeapp.di.UserComponent
import javax.inject.Scope

@Scope
annotation class RoutePhotosScope

@RoutePhotosScope
@Component(modules = [RoutePhotosModule::class], dependencies = [UserComponent::class])
interface RoutePhotosComponent {

    fun inject(routePhotosScope: RoutePhotosFragment)
}