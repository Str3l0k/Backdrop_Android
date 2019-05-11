package de.si.backdroplibrary.components

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import de.si.backdroplibrary.BackdropEvent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.kotlinx.fadeOut
import de.si.kotlinx.inflateView
import kotlinx.android.synthetic.main.backdrop_base.*

class BackdropContent(private val activity: BackdropActivity) {

    // view container
    private val layoutContentContainer: ViewGroup = activity.layout_backdrop_content

    // view cache
    private val backdropViewCache: MutableMap<Int, View> = mutableMapOf()

    // viewmodel
    private val viewModel: BackdropViewModel = BackdropViewModel.registeredInstance(activity)

    /* API */
    internal fun preCacheContentView(@LayoutRes layoutResId: Int) {
        val inflatedView = activity.inflateView(layoutResId, layoutContentContainer)
        inflatedView?.takeIf { view ->
            view.id > 0
        }?.let { view ->
            backdropViewCache[layoutResId] = view
        } ?: run {
            Log.e(
                "BackdropContent",
                "The root view of the inflated layout with id $layoutResId does not have a proper id itself."
            )
        }
    }

    internal fun setContentView(@LayoutRes layoutResId: Int, nextLayoutCallback: (View) -> Unit) {
        if (backdropViewCache.containsKey(layoutResId).not()) {
            preCacheContentView(layoutResId)
        }

        val newContentView = backdropViewCache[layoutResId]
        newContentView?.doOnNextLayout { newContentViewAfterLayout ->
            nextLayoutCallback(newContentViewAfterLayout)
            viewModel.emit(BackdropEvent.BACKDROP_CONTENT_VISIBLE, newContentViewAfterLayout)
        }

        layoutContentContainer.alpha = 1f
        layoutContentContainer.isVisible = true
        layoutContentContainer.removeAllViews()
        layoutContentContainer.addView(newContentView)
    }

    internal fun hide() {
        layoutContentContainer.fadeOut {
            layoutContentContainer.isVisible = false
        }
    }
}