package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.AppUnit
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.dataclass.StatusStatistic
import ga.lupuss.anotherbikeapp.models.dataclass.TimeStatistic
import ga.lupuss.anotherbikeapp.models.dataclass.UnitStatistic


interface ResourceResolver {
    fun resolve(message: Message): String
    fun resolve(text: Text): String
    fun resolve(unit: AppUnit): String
    fun resolve(stat: TimeStatistic): String
    fun resolve(stat: UnitStatistic): String
    fun resolve(stat: Statistic<*>): String
    fun resolve(stat: StatusStatistic): String
    fun resolve(statName: Statistic.Name): String
    fun resolve(statName: Statistic.Name, stat: Statistic<*>): String
}