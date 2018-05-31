package ga.lupuss.anotherbikeapp

const val APP_PREFS = "app_prefs"

enum class Message {
    ACCOUNT_CREATED,
    USER_EXISTS,
    SOMETHING_GOES_WRONG,
    FILL_ALL_FIELDS,
    NO_INTERNET_CONNECTION,
    EMAIL_OR_PASSWORD_BLANK,
    INCORRECT_CREDENTIALS,
    NO_PERMISSION,
    LOCATION_NOT_AVAILABLE
}

enum class Text {
    DEFAULT_ROUTE_NAME
}