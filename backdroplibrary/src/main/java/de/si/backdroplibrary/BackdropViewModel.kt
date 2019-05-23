package de.si.backdroplibrary

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

typealias BackdropEventCallback = ((BackdropEvent, Any?) -> Boolean)

class BackdropViewModel : ViewModel() {
    @get:Synchronized
    private val callbackReceivers: MutableSet<BackdropEventCallback> = mutableSetOf()

    internal fun registerEventCallback(callbackBackdrop: BackdropEventCallback) {
        callbackReceivers.add(callbackBackdrop)
    }

    internal fun unregisterEventCallbacks(callback: BackdropEventCallback) {
        callbackReceivers.remove(callback)
    }

    fun emit(event: BackdropEvent, payload: Any? = null) {
//        Log.d("Backdrop event system", "Emitting event $event with payload $payload.")

        if (callbackReceivers.reversed().firstOrNull { it.invoke(event, payload) } == null) {
            Log.w("Backdrop Event System", "Nobody consumed event = [$event], payload = [$payload]")
        }
    }

    companion object {
        fun registeredInstance(activity: AppCompatActivity): BackdropViewModel {
            return ViewModelProviders.of(activity)[BackdropViewModel::class.java]
        }
    }
}