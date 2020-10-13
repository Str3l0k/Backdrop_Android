package de.si.backdroplibrary

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal typealias BackdropEventCallback = ((BackdropEvent) -> Boolean)

class BackdropViewModel : ViewModel() {

    //-----------------------------------------
    // Properties
    //-----------------------------------------
    var gestureNavigationEnabled: Boolean = true

    //-----------------------------------------
    // Observers
    //-----------------------------------------
    @get:Synchronized
    private val callbackReceivers: MutableMap<BackdropComponent, BackdropEventCallback> = mutableMapOf()

    internal fun registerEventCallback(backdropComponent: BackdropComponent, callbackBackdrop: BackdropEventCallback) {
        callbackReceivers[backdropComponent] = callbackBackdrop
    }

    internal fun unregisterEventCallbacks(backdropComponent: BackdropComponent) {
        callbackReceivers.remove(backdropComponent)
    }

    internal fun emit(event: BackdropEvent) {
        val callbackResult: Any? = callbackReceivers.values.reversed().firstOrNull { callback ->
            callback.invoke(event)
        }

        if (callbackResult == null) {
            Log.d("Backdrop Event System", "Nobody consumed event = [$event]")
        }
    }

    //-----------------------------------------
    companion object {
        internal fun registeredInstance(activity: AppCompatActivity): BackdropViewModel {
            return ViewModelProvider(activity)[BackdropViewModel::class.java]
        }
    }
}
