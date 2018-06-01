package ga.lupuss.anotherbikeapp.models.interfaces

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.pojo.Statistic
import ga.lupuss.anotherbikeapp.models.pojo.StatusStatistic
import ga.lupuss.anotherbikeapp.models.pojo.TimeStatistic
import ga.lupuss.anotherbikeapp.models.pojo.UnitStatistic


interface StringsResolver {
    fun resolve(message: Message): String
    fun resolve(text: Text): String
    fun resolve(unit: Statistic.Unit): String
    fun resolve(stat: TimeStatistic): String
    fun resolve(stat: UnitStatistic): String
    fun resolve(stat: Statistic<*>): String
    fun resolve(stat: StatusStatistic): String
    fun resolve(statName: Statistic.Name): String
    fun resolve(statName: Statistic.Name, stat: Statistic<*>): String
}