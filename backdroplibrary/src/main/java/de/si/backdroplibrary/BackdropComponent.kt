package de.si.backdroplibrary

import android.graphics.Point
import android.view.View
import androidx.annotation.LayoutRes
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.backdroplibrary.children.FullscreenBackdropFragment
import de.si.backdroplibrary.children.FullscreenRevealBackdropFragment
import de.si.backdroplibrary.components.BackdropToolbarItem

interface BackdropComponent {

    //-----------------------------------------
    //-----------------------------------------
    val backdropActivity: BackdropActivity

    val viewModel: BackdropViewModel
        get() = BackdropViewModel.registeredInstance(backdropActivity)

    //-----------------------------------------
    // General
    //-----------------------------------------
    fun enableGestureNavigation() {
        viewModel.gestureNavigationEnabled = true
    }

    fun disableGestureNavigation() {
        viewModel.gestureNavigationEnabled = false
    }

    //-----------------------------------------
    // Card stack
    //-----------------------------------------
    fun addCardFragment(cardFragment: CardBackdropFragment) {
        viewModel.emit(Event.ADD_TOP_CARD, cardFragment)
    }

    fun removeTopCardFragment() {
        viewModel.emit(Event.REMOVE_TOP_CARD)
    }

    //-----------------------------------------
    // Fullscreen fragments
    //-----------------------------------------
    fun showFullscreenFragment(fragment: FullscreenBackdropFragment) {
        viewModel.emit(Event.SHOW_FULLSCREEN_FRAGMENT, fragment)
    }

    fun revealFullscreenFragment(parameters: FullscreenRevealBackdropFragment, revealEpicenter: Point, concealEpicenter: Point) {
        parameters.revealEpiCenter = revealEpicenter
        parameters.concealEpiCenter = concealEpicenter
        viewModel.emit(Event.REVEAL_FULLSCREEN_FRAGMENT, parameters)
    }

    fun hideFullscreenFragment() {
        viewModel.emit(Event.HIDE_FULLSCREEN_FRAGMENT)
    }

    //-----------------------------------------
    // Content
    //-----------------------------------------
    fun prefetchBackdropContent(@LayoutRes layoutResId: Int) {
        viewModel.emit(Event.PREFETCH_BACKDROP_CONTENT_VIEW, layoutResId)
    }

    fun showBackdropContent(@LayoutRes layoutResId: Int) {
        viewModel.emit(Event.SHOW_BACKDROP_CONTENT, layoutResId)
    }

    fun hideBackdropContent() {
        viewModel.emit(Event.HIDE_BACKDROP_CONTENT)
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
    fun changeToolbarItem(toolbarItem: BackdropToolbarItem) {
        viewModel.emit(Event.CHANGE_NAVIGATION_ITEM, toolbarItem)
    }

    fun onPrimaryActionClicked(): Boolean {
        return false
    }

    fun onMoreActionClicked(): Boolean {
        return false
    }
}