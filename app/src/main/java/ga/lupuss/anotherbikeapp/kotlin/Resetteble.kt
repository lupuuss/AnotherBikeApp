package ga.lupuss.anotherbikeapp.kotlin

import kotlin.reflect.KProperty

class ResettableManager {
    private val delegates = mutableListOf<Resettable<*, *>>()

    fun register(delegate: Resettable<*, *>) { delegates.add(delegate) }

    fun reset() { delegates.forEach { it.reset() } }
}

class Resettable<R, T : Any>(manager: ResettableManager) {
    init { manager.register(this) }

    private var value: T? = null

    operator fun getValue(thisRef: R, property: KProperty<*>): T =
            value ?: throw UninitializedPropertyAccessException()

    operator fun setValue(thisRef: R, property: KProperty<*>, t: T) { value = t }

    fun reset() { value = null }
}