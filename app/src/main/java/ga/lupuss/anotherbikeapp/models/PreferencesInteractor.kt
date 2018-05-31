package ga.lupuss.anotherbikeapp.models

import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic

interface PreferencesInteractor {

    var appTheme: AppTheme
    var speedUnit: Statistic.Unit
    var distanceUnit: Statistic.Unit
}