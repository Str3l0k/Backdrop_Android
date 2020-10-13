package de.si.backdroplibrary

import android.graphics.Point
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
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

    fun resetBackdropColor() {
        backdropViewModel.emit(BackdropEvent.ChangeBackdropColor(ContextCompat.getColor(backdropActivity, R.color.colorPrimary)))
    }

    //-----------------------------------------
    // Card stack
    //-----------------------------------------
    fun addCardFragment(cardFragment: CardBackdropFragment) {
        backdropViewModel.emit(BackdropEvent.AddTopCard(cardFragment))
    }

    fun removeTopCardFragment() {
        backdropViewModel.emit(BackdropEvent.RemoveTopCard)
    }

    //-----------------------------------------
    // Fullscreen fragments
    //-----------------------------------------
    fun showFullscreenFragment(fragment: FullscreenBackdropFragment) {
        backdropViewModel.emit(BackdropEvent.ShowFullscreenFragment(fragment))
    }

    fun revealFullscreenFragment(
            parameters: FullscreenRevealBackdropFragment,
            revealEpicenter: Point,
            concealEpicenter: Point
    ) {

        parameters.revealEpiCenter = revealEpicenter
        parameters.concealEpiCenter = concealEpicenter
        backdropViewModel.emit(BackdropEvent.RevealFullscreenFragment(parameters))
    }

    fun hideFullscreenFragment() {
        backdropViewModel.emit(BackdropEvent.HideFullscreenFragment)
    }

    //-----------------------------------------
    // Content
    //-----------------------------------------
    fun prefetchBackdropContent(@LayoutRes layoutResId: Int) {
        backdropViewModel.emit(BackdropEvent.PrefetchBackdropContentView(layoutResId))
    }

    fun showBackdropContent(@LayoutRes layoutResId: Int) {
        backdropViewModel.emit(BackdropEvent.ShowBackdropContentView(layoutResId))
    }

    fun hideBackdropContent() {
        backdropViewModel.emit(BackdropEvent.HideBackdropContentView)
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
        backdropViewModel.emit(BackdropEvent.ChangeToolbarItem(toolbarItem))
    }

    fun startToolbarActionMode(toolbarItem: BackdropToolbarItem) {
        backdropViewModel.emit(BackdropEvent.StartActionMode(toolbarItem))
    }

    fun finishToolbarActionMode() {
        backdropViewModel.emit(BackdropEvent.FinishActionMode)
    }

    fun changeBackgroundColor(color: Int) {
        backdropViewModel.emit(BackdropEvent.ChangeBackdropColor(color))
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
