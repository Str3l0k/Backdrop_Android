package de.si.backdroplibrary

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

internal typealias BackdropEventCallback = ((Event, Any?) -> Boolean)

class BackdropViewModel : ViewModel() {

    @get:Synchronized
    private val callbackReceivers: MutableSet<BackdropEventCallback> = mutableSetOf()

    internal fun registerEventCallback(callbackBackdrop: BackdropEventCallback) {
        callbackReceivers.add(callbackBackdrop)
    }

    internal fun unregisterEventCallbacks(callback: BackdropEventCallback) {
        callbackReceivers.remove(callback)
    }

    internal fun emit(event: Event, payload: Any? = null) {
        val callbackResult = callbackReceivers.reversed().firstOrNull { it.invoke(event, payload) }

        if (callbackResult == null) {
            Log.w("Backdrop Event System", "Nobody consumed event = [$event], payload = [$payload]")
        }
    }

    companion object {
        internal fun registeredInstance(activity: AppCompatActivity): BackdropViewModel {
            return ViewModelProviders.of(activity)[BackdropViewModel::class.java]
        }
    }
}