package de.si.backdroplibrary.children

import android.view.View
import androidx.fragment.app.Fragment
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.activity.BackdropActivity

abstract class BackdropFragment : Fragment(), BackdropComponent {

    override val backdropActivity: BackdropActivity
        get() = requireActivity() as BackdropActivity

    override val backdropViewModel: BackdropViewModel by lazy {
        BackdropViewModel.registeredInstance(backdropActivity)
    }

    override fun onStart() {
        super.onStart()
        backdropViewModel.registerEventCallback(this::onEvent)
    }

    override fun onPause() {
        super.onPause()
        backdropViewModel.unregisterEventCallbacks(this::onEvent)
    }

    private fun onEvent(event: Event, payload: Any?): Boolean {
        return when (event) {
            Event.BACKDROP_CONTENT_VISIBLE            -> onBackdropContentVisible(payload as View)
            Event.BACKDROP_CONTENT_INVISIBLE          -> onBackdropContentInvisible()
            Event.PRIMARY_ACTION_TRIGGERED            -> onPrimaryActionClicked()
            Event.MORE_ACTION_TRIGGERED               -> onMoreActionClicked()
            Event.PRIMARY_ACTION_ACTIONMODE_TRIGGERED -> onPrimaryActionInActionModeClicked()
            Event.MORE_ACTION_ACTIONMODE_TRIGGERED    -> onMoreActionInActionModeClicked()
            Event.ACTION_MODE_FINISHED                -> onToolbarActionModeFinished()
            else                                      -> false
        }
    }
}
