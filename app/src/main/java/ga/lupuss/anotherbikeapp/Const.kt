package ga.lupuss.anotherbikeapp

const val WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/"
const val FLAT_ICON_URL = "https://www.flaticon.com"
const val FLAT_ICON_AUTHORS_URL = "$FLAT_ICON_URL/authors/"
const val FIREBASE_URL = "https://firebase.google.com"
const val OPEN_WEATHER_MAP_URL = "https://openweathermap.org"
const val CACHE_SIZE: Long = 10 * 1024 * 102 // 10 MB

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

/**
 * Represent units used in app.
 *
 * @property convertFunction is used to convert SI units to others
 * e.g 3.6 km/h is 1 m/s so convertParam is 3.6
 */
interface AppUnit {

    val convertFunction: (rawSiValue: Double) -> Double

    enum class Distance(override val convertFunction: (rawSiValue: Double) -> Double): AppUnit {

        M({ it * 1.0 }), // SI unit
        KM({ it * 0.001 }),
        MI({ it * 0.000621371192 })
    }

    enum class Speed(override val convertFunction:(rawSiValue: Double) -> Double): AppUnit {

        M_S({ it * 1.0 }), // SI unit
        KM_H({ it * 3.6 }),
        MPH({ it * 2.23693629 })
    }

    enum class Temperature(override val convertFunction:(rawSiValue: Double) -> Double): AppUnit {

        KELVIN({ it * 1.0 }), // SI unit
        CELSIUS({ it - 273.15 }),
        FAHRENHEIT({it * (9.0/5.0) - 459.67})
    }
}