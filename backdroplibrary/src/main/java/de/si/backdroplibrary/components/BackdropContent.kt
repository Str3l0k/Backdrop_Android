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
        val contentView = inflateViewAndCheckForId(layoutResId)
        contentView?.let { view ->
            putViewInCache(view, layoutResId)
        } ?: run {
            Log.e(
                "BackdropContent",
                "The root view of the inflated layout with id $layoutResId does not have a proper id itself."
            )
        }
    }

    internal fun setContentView(@LayoutRes layoutResId: Int, nextLayoutCallback: (View) -> Unit) {
        checkCacheAndInflateIfNecessary(layoutResId)
        getContentViewAndSetLayoutCallback(layoutResId, nextLayoutCallback)?.let { contentView ->
            prepareLayoutContainerAndAddContent(contentView)
        }
    }

    internal fun hide() {
        layoutContentContainer.fadeOut {
            layoutContentContainer.isVisible = false
        }
    }

    /* internal functions */
    private fun inflateViewAndCheckForId(@LayoutRes layoutResId: Int): View? {
        val inflatedView = activity.inflateView(layoutResId, layoutContentContainer)

        return inflatedView?.takeIf { view ->
            view.id > 0
        }
    }

    private fun putViewInCache(view: View, layoutResId: Int) {
        backdropViewCache[layoutResId] = view
    }

    private fun isViewAlreadyCached(@LayoutRes layoutResId: Int): Boolean {
        return backdropViewCache.containsKey(layoutResId).not()
    }

    private fun checkCacheAndInflateIfNecessary(@LayoutRes layoutResId: Int) {
        if (isViewAlreadyCached(layoutResId)) {
            preCacheContentView(layoutResId)
        }
    }

    private fun getContentViewAndSetLayoutCallback(@LayoutRes layoutResId: Int, nextLayoutCallback: (View) -> Unit): View? {
        val newContentView = backdropViewCache[layoutResId]

        newContentView?.doOnNextLayout { newContentViewAfterLayout ->
            nextLayoutCallback(newContentViewAfterLayout)
            viewModel.emit(BackdropEvent.BACKDROP_CONTENT_VISIBLE, newContentViewAfterLayout)
        }

        return newContentView
    }

    private fun prepareLayoutContainerAndAddContent(contentView: View) {
        layoutContentContainer.alpha = 1f
        layoutContentContainer.isVisible = true
        layoutContentContainer.removeAllViews()
        layoutContentContainer.addView(contentView)
    }
}