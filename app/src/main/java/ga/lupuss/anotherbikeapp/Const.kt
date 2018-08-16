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