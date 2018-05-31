package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver

@Suppress("REDUNDANT_ELSE_IN_WHEN")
class AndroidStringsResolver(private val context: Context) : StringsResolver {
    override fun resolve(message: Message): String {

        return context.getString(
                when (message) {
                    Message.ACCOUNT_CREATED -> R.string.accountCreated
                    Message.USER_EXISTS -> R.string.userExists
                    Message.SOMETHING_GOES_WRONG -> R.string.somethingGoesWrong
                    Message.FILL_ALL_FIELDS -> R.string.fillAllFileds
                    Message.NO_INTERNET_CONNECTION -> R.string.noInternetConnection
                    Message.EMAIL_OR_PASSWORD_BLANK -> R.string.passwordOrEmailBlank
                    Message.INCORRECT_CREDENTIALS -> R.string.wrongCredentials
                    Message.NO_PERMISSION -> R.string.noPermission
                    Message.LOCATION_NOT_AVAILABLE -> R.string.locationNotAvailable
                    else -> throw NoSuchElementException("Message not resolved!")
                }
        )
    }

    override fun resolve(text: Text): String {

        return context.getString(
                when (text) {
                    Text.DEFAULT_ROUTE_NAME -> R.string.default_route_name
                    else -> throw NoSuchElementException("Text not resolved!")
                }
        )

    }

}