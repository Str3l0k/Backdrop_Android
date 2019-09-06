package de.si.backdroplibrary.components

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.kotlinx.fade
import de.si.kotlinx.fadeOut
import de.si.kotlinx.inflateView
import kotlinx.android.synthetic.main.layout_main.*

internal class BackdropContent(override val backdropActivity: BackdropActivity) :
    BackdropComponent {

    // view container
    private val layoutContentContainer: ViewGroup = backdropActivity.layout_backdrop_content

    // view cache
    private val backdropViewCache: MutableMap<Int, View> = mutableMapOf()

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
        getContentViewAndSetLayoutCallback(
            layoutResId,
            nextLayoutCallback
        )?.let(this::prepareLayoutContainerAndAddContent)
    }

    internal fun hide() {
        layoutContentContainer.fadeOut {
            layoutContentContainer.isVisible = false
        }
    }

    /* internal functions */
    private fun inflateViewAndCheckForId(@LayoutRes layoutResId: Int): View? {
        val inflatedView = backdropActivity.inflateView(layoutResId, layoutContentContainer)

        return inflatedView?.takeIf { view ->
            view.id > 0
        }
    }

    private fun putViewInCache(view: View, layoutResId: Int) {
        backdropViewCache[layoutResId] = view
    }

    private fun isViewAlreadyCached(@LayoutRes layoutResId: Int): Boolean {
        return backdropViewCache.containsKey(layoutResId)
    }

    private fun checkCacheAndInflateIfNecessary(@LayoutRes layoutResId: Int) {
        if (isViewAlreadyCached(layoutResId).not()) {
            preCacheContentView(layoutResId)
        }
    }

    private fun getContentViewAndSetLayoutCallback(@LayoutRes layoutResId: Int, nextLayoutCallback: (View) -> Unit): View? {
        val newContentView = backdropViewCache[layoutResId]

        newContentView?.doOnNextLayout { newContentViewAfterLayout ->
            nextLayoutCallback(newContentViewAfterLayout)
            viewModel.emit(Event.BACKDROP_CONTENT_VISIBLE, newContentViewAfterLayout)
        }

        return newContentView
    }

    private fun prepareLayoutContainerAndAddContent(contentView: View) {
        if (layoutContentContainer.isVisible) {
            layoutContentContainer.fade {
                layoutContentContainer.removeAllViews()
                layoutContentContainer.addView(contentView)
            }
        } else {
            layoutContentContainer.alpha = 1f
            layoutContentContainer.isVisible = true
            layoutContentContainer.removeAllViews()
            layoutContentContainer.addView(contentView)
        }
    }
}