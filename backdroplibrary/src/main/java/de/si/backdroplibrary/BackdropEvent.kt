package de.si.backdroplibrary

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.backdroplibrary.children.FullscreenBackdropFragment
import de.si.backdroplibrary.children.FullscreenRevealBackdropFragment
import de.si.backdroplibrary.components.toolbar.BackdropToolbarItem

internal sealed class BackdropEvent {

    data class PrefetchBackdropContentView(@LayoutRes val layoutRes: Int) : BackdropEvent()
    data class ShowBackdropContentView(@LayoutRes val layoutRes: Int) : BackdropEvent()
    object HideBackdropContentView : BackdropEvent()
    data class BackdropContentNowVisible(val view: View) : BackdropEvent()
    object BackdropContentNowHidden : BackdropEvent()
    data class ChangeBackdropColor(@ColorInt val color: Int) : BackdropEvent()

    object PrimaryActionTriggered : BackdropEvent()
    object MoreActionTriggered : BackdropEvent()

    data class ChangeToolbarItem(val toolbarItem: BackdropToolbarItem) : BackdropEvent()
    object MenuActionTriggered : BackdropEvent()
    data class StartActionMode(val actionModeToolbarItem: BackdropToolbarItem) : BackdropEvent()
    object FinishActionMode : BackdropEvent()
    object PrimaryActionInActionModeTriggered : BackdropEvent()
    object MoreActionInActionModeTriggered : BackdropEvent()
    object ActionModeFinished : BackdropEvent()

    data class AddTopCard(val fragment: CardBackdropFragment) : BackdropEvent()
    object RemoveTopCard : BackdropEvent()
    data class ShowFullscreenFragment(val fragment: FullscreenBackdropFragment) : BackdropEvent()
    data class RevealFullscreenFragment(val fragment: FullscreenRevealBackdropFragment) : BackdropEvent()
    object HideFullscreenFragment : BackdropEvent()
}
