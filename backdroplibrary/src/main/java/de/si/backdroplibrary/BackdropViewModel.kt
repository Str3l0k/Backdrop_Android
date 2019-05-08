package de.si.backdroplibrary

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*

typealias BackdropEventCallback = ((BackdropEvent, Any?) -> Boolean)

class BackdropViewModel : ViewModel() {

    @get:Synchronized
    private val callbackReceivers: MutableMap<LifecycleOwner, BackdropEventCallback> = mutableMapOf()

    fun registerEventCallback(lifecycleOwner: LifecycleOwner, callbackBackdrop: BackdropEventCallback) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun unregister() {
                println("Removing $lifecycleOwner from callback receivers.")
                unregisterEventCallbacks(lifecycleOwner)
            }
        })

        callbackReceivers[lifecycleOwner] = callbackBackdrop
    }

    fun unregisterEventCallbacks(owner: LifecycleOwner) {
        callbackReceivers.remove(owner)
    }

    fun emit(event: BackdropEvent, payload: Any? = null) {
        Log.d("Backdrop event system", "Emitting event $event with payload $payload.")

        if (callbackReceivers.values.reversed().firstOrNull { it.invoke(event, payload) } == null) {
            Log.w("Backdrop Event System", "Nobody consumed event = [$event], payload = [$payload]")
        }
    }

    companion object {
        fun registeredInstance(activity: AppCompatActivity): BackdropViewModel {
            return ViewModelProviders.of(activity)[BackdropViewModel::class.java]
        }
    }
}