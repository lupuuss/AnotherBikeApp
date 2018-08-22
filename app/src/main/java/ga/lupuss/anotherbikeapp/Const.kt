package ga.lupuss.anotherbikeapp

const val WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/"

enum class Message {
    ACCOUNT_CREATED,
    USER_EXISTS,
    SOMETHING_GOES_WRONG,
    FILL_ALL_FIELDS,
    NO_INTERNET_CONNECTION,
    EMAIL_OR_PASSWORD_BLANK,
    INVALID_CREDENTIALS_LOGIN,
    NO_PERMISSION,
    LOCATION_NOT_AVAILABLE,
    USER_NOT_EXISTS,
    PASSWORD_IS_TOO_WEAK,
    INVALID_CREDENTIALS_CREATING
}

enum class AppTheme {
    LIGHT, DARK
}

enum class Text {
    DEFAULT_ROUTE_NAME
}

enum class WeatherIcon(
        val rank: Float = 1.0F,
        @Suppress("unused") val isDay: Boolean = true
) {
//  DAY                                 NIGHT
    SUNNY_D,                            SUNNY_N(isDay = false),
    FEW_CLOUDS_D,                       FEW_CLOUDS_N(isDay = false),
    SCATTERED_CLOUDS_D(rank = 1.5F),    SCATTERED_CLOUDS_N(rank = 1.5F, isDay = false),
    BROKEN_CLOUDS_D(rank = 2.0F),       BROKEN_CLOUDS_N(rank = 2.0F, isDay = false),
    SHOWER_RAIN_D(rank = 3.0F),         SHOWER_RAIN_N(rank = 3.0F, isDay = false),
    RAIN_D(rank = 3.0F),                RAIN_N(rank = 3.0F, isDay = false),
    THUNDERSTORM_D(rank = 4.0F),        THUNDERSTORM_N(rank = 4.0F, isDay = false),
    SNOW_D(rank = 3.0F),                SNOW_N(rank = 3.0F, isDay = false),
    MIST_D,                             MIST_N,

    EMPTY(rank = 0F)
}