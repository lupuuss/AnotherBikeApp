package ga.lupuss.anotherbikeapp.ui.modules.summary

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.di.UserComponent
import javax.inject.Scope

@Scope
annotation class SummaryComponentScope


@Component(dependencies = [UserComponent::class], modules = [SummaryModule::class])
@SummaryComponentScope
interface SummaryComponent {

    fun inject(summaryActivity: SummaryActivity)
}