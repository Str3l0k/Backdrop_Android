package de.si.backdroplibrary.children

import androidx.fragment.app.Fragment
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.BackdropEvent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.activity.BackdropActivity

abstract class BackdropFragment : Fragment(), BackdropComponent {

    override val backdropActivity: BackdropActivity
        get() = requireActivity() as BackdropActivity

    override val backdropViewModel: BackdropViewModel by lazy {
        BackdropViewModel.registeredInstance(backdropActivity)
    }

    override fun onStart() {
        super.onStart()
        backdropViewModel.registerEventCallback(backdropComponent = this, callbackBackdrop = this::onEvent)
    }

    override fun onPause() {
        super.onPause()
        backdropViewModel.unregisterEventCallbacks(backdropComponent = this)
    }

    private fun onEvent(event: BackdropEvent): Boolean {
        return when (event) {
            is BackdropEvent.BackdropContentNowVisible -> onBackdropContentVisible(event.view)
            BackdropEvent.BackdropContentNowHidden -> onBackdropContentInvisible()
            BackdropEvent.PrimaryActionTriggered -> onPrimaryActionClicked()
            BackdropEvent.MoreActionTriggered -> onMoreActionClicked()
            BackdropEvent.PrimaryActionInActionModeTriggered -> onPrimaryActionInActionModeClicked()
            BackdropEvent.MoreActionInActionModeTriggered -> onMoreActionInActionModeClicked()
            BackdropEvent.ActionModeFinished -> onToolbarActionModeFinished()
            BackdropEvent.MenuActionTriggered -> onMenuActionClicked()
            else                                             -> false
        }
    }
}
