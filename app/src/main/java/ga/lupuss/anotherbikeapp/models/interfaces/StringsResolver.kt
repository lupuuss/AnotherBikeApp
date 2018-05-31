package ga.lupuss.anotherbikeapp.models.interfaces

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.Text


interface StringsResolver {
    fun resolve(message: Message): String
    fun resolve(text: Text): String
}