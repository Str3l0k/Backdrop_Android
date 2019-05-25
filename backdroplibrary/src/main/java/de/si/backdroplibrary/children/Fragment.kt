package de.si.backdroplibrary.children

import android.view.View
import androidx.fragment.app.Fragment
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.Component
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.activity.Activity

abstract class Fragment : Fragment(), Component {
    override val viewModel: BackdropViewModel by lazy {
        val backdropActivity = activity as? Activity
        backdropActivity?.let {
            BackdropViewModel.registeredInstance(it)
        } ?: throw ExceptionInInitializerError()
    }

    override fun onStart() {
        super.onStart()
        viewModel.registerEventCallback(this::onEvent)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterEventCallbacks(this::onEvent)
    }

    private fun onEvent(event: Event, payload: Any?): Boolean {
        return when (event) {
            Event.BACKDROP_CONTENT_VISIBLE -> onBackdropContentVisible(payload as View)
            Event.BACKDROP_CONTENT_INVISIBLE -> onBackdropContentInvisible()
            Event.PRIMARY_ACTION_TRIGGERED -> onPrimaryActionClicked()
            Event.MORE_ACTION_TRIGGERED -> onMoreActionClicked()
            else -> false
        }
    }
}