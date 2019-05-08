package de.si.backdroplibrary

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import de.si.kotlinx.layoutInflater
import kotlinx.android.synthetic.main.backdrop_base.*

abstract class BackdropActivity : AppCompatActivity() {

    private companion object {
        private const val BACKDROP_CLOSED_TRANSLATION_Y = 0f
        private const val BACKDROP_ANIMATION_DURATION = 250L
    }

    // viewmodel
    protected val viewModel by lazy {
        BackdropViewModel.registeredInstance(this)
    }

    /* backdrop state information */
    protected val backdropContentInvisible: Boolean
        get() = layout_backdrop_cardstack.translationY.toInt() == BACKDROP_CLOSED_TRANSLATION_Y.toInt()

    /* cached backdrop views */
    private val backdropViews: MutableMap<Int, View> = mutableMapOf()

    /* animation properties */
    private val backdropOpenCloseAnimator by lazy {
        ObjectAnimator.ofFloat(layout_backdrop_cardstack, View.TRANSLATION_Y, 0f).apply {
            duration = BACKDROP_ANIMATION_DURATION
            doOnEnd {
                val event = if (backdropContentInvisible) {
                    BackdropEvent.BACKDROP_CONTENT_INVISIBLE
                } else {
                    BackdropEvent.BACKDROP_CONTENT_VISIBLE
                }

                viewModel.emit(event)
            }
        }
    }
    private val buttonMenuShowHideAnimator by lazy {
        ObjectAnimator.ofFloat(button_backdrop_toolbar_menu_show, View.ALPHA, 0f).apply {
            duration = BACKDROP_ANIMATION_DURATION / 2

            doOnEnd {
                button_backdrop_toolbar_menu_show.alpha = 0f
                button_backdrop_toolbar_menu_show.isVisible = false
            }
        }
    }
    private val buttonCloseBackdropShowAnimator by lazy {
        ObjectAnimator.ofFloat(button_backdrop_toolbar_hide, View.ALPHA, 1f).apply {
            duration = BACKDROP_ANIMATION_DURATION / 2
        }
    }
    private val buttonCloseBackdropHideAnimator by lazy {
        ObjectAnimator.ofFloat(button_backdrop_toolbar_hide, View.ALPHA, 0f).apply {
            duration = BACKDROP_ANIMATION_DURATION / 2

            doOnEnd {
                button_backdrop_toolbar_hide.alpha = 0f
                button_backdrop_toolbar_hide.isVisible = false
            }
        }
    }
    private val buttonChangeToBackdropOpenAnimatorSet by lazy {
        AnimatorSet().apply {
            playSequentially(buttonMenuShowHideAnimator, buttonCloseBackdropShowAnimator)
        }
    }
    private val buttonChangeToBackdropClosedAnimatorSet by lazy {
        AnimatorSet().apply {
            playSequentially(buttonMenuShowHideAnimator, buttonCloseBackdropHideAnimator)
        }
    }

    /* region lifecycle */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.backdrop_base)
        initializeViewModel()
        configureViews()
    }

    private fun initializeViewModel() {
        viewModel.registerEventCallback(this, this::onEventCallback)
    }

    private fun configureViews() {
        button_backdrop_toolbar_hide.setOnClickListener {
            hideBackdropContent()
        }
    }
    /* endregion lifecycle */

    /* region backdrop specific event handling */
    private fun onEventCallback(event: BackdropEvent, payload: Any?): Boolean {
        return when (event) {
            BackdropEvent.PREFETCH_BACKDROP_CONTENT_VIEW -> {
                val layoutResId = payload as? Int
                layoutResId?.let {
                    prefetchBackdropContent(layoutResId)
                }
                true
            }
            BackdropEvent.SHOW_BACKDROP_CONTENT -> {
                val layoutResId = payload as? Int
                layoutResId?.let {
                    showBackdropContent(layoutResId)
                }
                true
            }
            BackdropEvent.HIDE_BACKDROP_CONTENT -> {
                hideBackdropContent()
                true
            }
            else -> false
        }
    }
    /* endregion */

    /* region internal */
    private fun inflateView(@LayoutRes layoutResId: Int): View? {
        return applicationContext?.layoutInflater?.inflate(layoutResId, layout_backdrop_content, false)
    }

    private fun animateBackdropOpening(translationY: Float) {
        buttonMenuShowHideAnimator.setFloatValues(0f)

        button_backdrop_toolbar_hide.alpha = 0f
        button_backdrop_toolbar_hide.isVisible = true

        backdropOpenCloseAnimator.setFloatValues(translationY)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(buttonChangeToBackdropOpenAnimatorSet, backdropOpenCloseAnimator)
        animatorSet.start()
    }

    private fun animateBackdropClosing() {
        button_backdrop_toolbar_menu_show.isVisible = true

        button_backdrop_toolbar_hide.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                button_backdrop_toolbar_hide.isVisible = false
            }
        })

        button_backdrop_toolbar_menu_show.animate().alpha(1f)

        backdropOpenCloseAnimator.setFloatValues(BACKDROP_CLOSED_TRANSLATION_Y)
        backdropOpenCloseAnimator.start()
    }
    /* endregion internal */

    /* region backdrop content control */
    private fun prefetchBackdropContent(@LayoutRes layoutResId: Int) {
        inflateView(layoutResId)?.let { contentView ->
            backdropViews[layoutResId] = contentView
        }
    }

    private fun showBackdropContent(@LayoutRes layoutResId: Int) {
        if (backdropViews.containsKey(layoutResId).not()) {
            prefetchBackdropContent(layoutResId)
        }

        val contentView = backdropViews[layoutResId]
        contentView?.doOnNextLayout {
            animateBackdropOpening(contentView.height.toFloat())
        }

        layout_backdrop_content.removeAllViews()
        layout_backdrop_content.addView(contentView)
    }

    private fun hideBackdropContent() {
        animateBackdropClosing()
    }
    /* endregion backdrop content control */

    /* region main menu */
    fun setMenuButtonClickListener(onClickListener: View.OnClickListener) {
        button_backdrop_toolbar_menu_show.setOnClickListener(onClickListener)
        button_backdrop_toolbar_menu_show.isVisible = true
    }

    fun setMenuButtonLongClickListener(onLongClickListener: View.OnLongClickListener) {
        button_backdrop_toolbar_menu_show.setOnLongClickListener(onLongClickListener)
    }

    fun setMenuLayout(@LayoutRes layoutResId: Int) {
        // TODO
    }

    fun setMenuView(view: View) {
        // TODO
    }
    /* endregion */
}