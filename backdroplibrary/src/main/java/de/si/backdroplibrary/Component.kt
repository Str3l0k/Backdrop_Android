package de.si.backdroplibrary

import android.view.View
import androidx.annotation.LayoutRes
import de.si.backdroplibrary.activity.Activity
import de.si.backdroplibrary.children.CardFragment
import de.si.backdroplibrary.children.FullscreenFragment

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
        TODO("Not implemented")
    }

    fun hideFullscreenFragment() {
        TODO("Not implemented")
    }

    fun changeTitle(title: String) {
        viewModel.emit(Event.CHANGE_TITLE, title)
    }

    fun clearTitle() {
        viewModel.emit(Event.CLEAR_TITLE)
    }

    fun setSubtitle(subtitle: String) {
        viewModel.emit(Event.CHANGE_SUBTITLE, subtitle)
    }

    fun clearSubtitle() {
        viewModel.emit(Event.CLEAR_SUBTITLE)
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