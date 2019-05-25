package de.si.backdroplibrary

import android.view.View
import androidx.annotation.LayoutRes
import de.si.backdroplibrary.activity.Activity
import de.si.backdroplibrary.children.CardFragment
import de.si.backdroplibrary.children.FullscreenFragment
import de.si.backdroplibrary.children.FullscreenRevealFragment
import de.si.backdroplibrary.components.ToolbarItem

interface Component {
    val activity: Activity
    val viewModel: BackdropViewModel
        get() = BackdropViewModel.registeredInstance(activity)

    fun showBackdropContent(@LayoutRes layoutResId: Int) {
        viewModel.emit(Event.SHOW_BACKDROP_CONTENT, layoutResId)
    }

    fun hideBackdropContent() {
        viewModel.emit(Event.HIDE_BACKDROP_CONTENT)
    }

    fun addCardFragment(cardFragment: CardFragment) {
        viewModel.emit(Event.ADD_TOP_CARD, cardFragment)
    }

    fun removeTopCardFragment() {
        viewModel.emit(Event.REMOVE_TOP_CARD)
    }

    fun showFullscreenFragment(fragment: FullscreenFragment) {
        viewModel.emit(Event.SHOW_FULLSCREEN_FRAGMENT, fragment)
    }

    fun revealFullscreenFragment(parameters: FullscreenRevealFragment) {
        viewModel.emit(Event.REVEAL_FULLSCREEN_FRAGMENT, parameters)
    }

    fun hideFullscreenFragment() {
        viewModel.emit(Event.HIDE_FULLSCREEN_FRAGMENT)
    }

    fun changeToolbarItem(toolbarItem: ToolbarItem) {
        viewModel.emit(Event.CHANGE_NAVIGATION_ITEM, toolbarItem)
    }

    fun onBackdropContentVisible(view: View): Boolean {
        return false
    }

    fun onBackdropContentInvisible(): Boolean {
        return false
    }

    fun onPrimaryActionClicked(): Boolean {
        return false
    }

    fun onMoreActionClicked(): Boolean {
        return false
    }
}