package ga.lupuss.anotherbikeapp.ui.modules.summary

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import javax.inject.Scope

@Scope
annotation class SummaryComponentScope


@Component(dependencies = [AnotherBikeAppComponent::class], modules = [SummaryModule::class])
@SummaryComponentScope
interface SummaryComponent {

    fun inject(summaryActivity: SummaryActivity)
}