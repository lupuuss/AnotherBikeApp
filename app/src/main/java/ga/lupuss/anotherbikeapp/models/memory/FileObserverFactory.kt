package ga.lupuss.anotherbikeapp.models.memory

import android.os.FileObserver

class FileObserverFactory {
    fun create(path: String, mask: Int, onEvent: (Int, String?) -> Unit): FileObserver =
            object : FileObserver(path, mask) {
                override fun onEvent(p0: Int, p1: String?) {
                    onEvent.invoke(p0, p1)
                }

            }
}