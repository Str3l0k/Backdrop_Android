package de.si.backdroplibrary

import android.graphics.Point
import android.view.View
import androidx.annotation.LayoutRes
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.backdroplibrary.children.FullscreenBackdropFragment
import de.si.backdroplibrary.children.FullscreenRevealBackdropFragment
import de.si.backdroplibrary.components.toolbar.BackdropToolbarItem

interface BackdropComponent {

    //-----------------------------------------
    //-----------------------------------------
    val backdropActivity: BackdropActivity

    val backdropViewModel: BackdropViewModel
        get() = BackdropViewModel.registeredInstance(backdropActivity)

    //-----------------------------------------
    // General
    //-----------------------------------------
    fun enableGestureNavigation() {
        backdropViewModel.gestureNavigationEnabled = true
    }

    fun disableGestureNavigation() {
        backdropViewModel.gestureNavigationEnabled = false
    }

    //-----------------------------------------
    // Card stack
    //-----------------------------------------
    fun addCardFragment(cardFragment: CardBackdropFragment) {
        backdropViewModel.emit(Event.ADD_TOP_CARD, cardFragment)
    }

    fun removeTopCardFragment() {
        backdropViewModel.emit(Event.REMOVE_TOP_CARD)
    }

    //-----------------------------------------
    // Fullscreen fragments
    //-----------------------------------------
    fun showFullscreenFragment(fragment: FullscreenBackdropFragment) {
        backdropViewModel.emit(Event.SHOW_FULLSCREEN_FRAGMENT, fragment)
    }

    fun revealFullscreenFragment(
        parameters: FullscreenRevealBackdropFragment,
        revealEpicenter: Point,
        concealEpicenter: Point
    ) {

        parameters.revealEpiCenter = revealEpicenter
        parameters.concealEpiCenter = concealEpicenter
        backdropViewModel.emit(Event.REVEAL_FULLSCREEN_FRAGMENT, parameters)
    }

    fun hideFullscreenFragment() {
        backdropViewModel.emit(Event.HIDE_FULLSCREEN_FRAGMENT)
    }

    //-----------------------------------------
    // Content
    //-----------------------------------------
    fun prefetchBackdropContent(@LayoutRes layoutResId: Int) {
        backdropViewModel.emit(Event.PREFETCH_BACKDROP_CONTENT_VIEW, layoutResId)
    }

    fun showBackdropContent(@LayoutRes layoutResId: Int) {
        backdropViewModel.emit(Event.SHOW_BACKDROP_CONTENT, layoutResId)
    }

    fun hideBackdropContent() {
        backdropViewModel.emit(Event.HIDE_BACKDROP_CONTENT)
    }

    fun onBackdropContentVisible(view: View): Boolean {
        return false
    }

    fun onBackdropContentInvisible(): Boolean {
        return false
    }

    //-----------------------------------------
    // Toolbar
    //-----------------------------------------
    fun currentToolbarItem(): BackdropToolbarItem {
        return backdropActivity.backdropToolbar.currentToolbarItem
    }

    fun actionModeToolbarItem(): BackdropToolbarItem? {
        return backdropActivity.backdropToolbar.actionModeToolbarItem
    }

    fun changeToolbarItem(toolbarItem: BackdropToolbarItem) {
        backdropViewModel.emit(Event.CHANGE_NAVIGATION_ITEM, toolbarItem)
    }

    fun startToolbarActionMode(toolbarItem: BackdropToolbarItem) {
        backdropViewModel.emit(Event.START_ACTION_MODE, toolbarItem)
    }

    fun finishToolbarActionMode() {
        backdropViewModel.emit(Event.FINISH_ACTION_MODE)
    }

    fun onPrimaryActionClicked(): Boolean {
        return false
    }

    fun onMoreActionClicked(): Boolean {
        return false
    }

    fun onPrimaryActionInActionModeClicked(): Boolean {
        return false
    }

    fun onMoreActionInActionModeClicked(): Boolean {
        return false
    }

    fun onToolbarActionModeFinished(): Boolean {
        return false
    }

    fun onMenuActionClicked(): Boolean {
        return false
    }
}
