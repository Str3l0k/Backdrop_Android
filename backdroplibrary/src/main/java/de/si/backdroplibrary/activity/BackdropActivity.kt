package de.si.backdroplibrary.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_ANIMATION_DURATION
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_CLOSED_TRANSLATION_Y
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.R
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.backdroplibrary.components.BackdropCardStack
import de.si.backdroplibrary.components.BackdropContent
import de.si.backdroplibrary.components.BackdropToolbar
import de.si.backdroplibrary.components.FullscreenDialogs
import kotlinx.android.synthetic.main.layout_main.*

abstract class BackdropActivity : AppCompatActivity(), BackdropComponent {

    // viewModel
    override val viewModel by lazy {
        BackdropViewModel.registeredInstance(this)
    }

    override val backdropActivity: BackdropActivity
        get() = this

    /* backdrop components */
    internal lateinit var backdropToolbar: BackdropToolbar
    internal lateinit var backdropContent: BackdropContent
    internal lateinit var backdropCardStack: BackdropCardStack
    internal lateinit var fullscreenDialogs: FullscreenDialogs

    abstract val baseCardFragment: CardBackdropFragment

    private val open: Boolean
        get() = backdropCardStack.isTranslatedByY

    /* animation properties */
    private val backdropOpenCloseAnimator by lazy {
        ObjectAnimator.ofFloat(layout_backdrop_cardstack, View.TRANSLATION_Y, 0f).apply {
            duration = BACKDROP_ANIMATION_DURATION
        }
    }

    /* region lifecycle */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)
        initializeViewModel()
        initializeComponents()
        initializeBaseCardFragment()
        initializeBaseToolbarItem()
    }

    override fun onResume() {
        super.onResume()
        initializeViewModel()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterEventCallbacks(this::onEvent)
    }

    override fun onBackPressed() {
        when {
            fullscreenDialogs.isVisible -> {
                fullscreenDialogs.hideFullscreenFragment()
            }
            open -> {
                hideBackdropContent()
            }
            backdropCardStack.hasMoreThanOneEntry -> {
                removeTopCardFragment()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun initializeViewModel() {
        viewModel.registerEventCallback(this::onEvent)
    }

    private fun initializeComponents() {
        initializeContent()
        initializeCardStack()
        initializeToolbar()
        initializeFullscreenDialogs()
    }

    private fun initializeToolbar() {
        backdropToolbar = BackdropToolbar(this)
        backdropToolbar.closeBackdropClickCallback = {
            hideBackdropContent()
        }
    }

    private fun initializeContent() {
        backdropContent = BackdropContent(this)
    }

    private fun initializeCardStack() {
        backdropCardStack = BackdropCardStack(this)
    }

    private fun initializeBaseCardFragment() {
        backdropCardStack.baseFragment = baseCardFragment
    }

    private fun initializeBaseToolbarItem() {
        backdropToolbar.configure(backdropCardStack.baseFragment.toolbarItem, false)
    }

    private fun initializeFullscreenDialogs() {
        fullscreenDialogs = FullscreenDialogs(this)
    }
    /* endregion lifecycle */

    /* region animation functions*/
    internal fun animateBackdropOpening(translationY: Float) {
        backdropOpenCloseAnimator.setFloatValues(translationY)
        backdropOpenCloseAnimator.start()
    }

    internal fun animateBackdropClosing() {
        backdropOpenCloseAnimator.setFloatValues(BACKDROP_CLOSED_TRANSLATION_Y)
        backdropOpenCloseAnimator.start()
    }
    /* endregion animation functions */

    /* region main menu */
    @Suppress("unused")
    fun setMenuButtonClickCallback(clickCallback: () -> Unit) {
        backdropToolbar.openMenuClickCallback = clickCallback
    }

    @Suppress("unused")
    fun setMenuButtonLongClickCallback(longClickCallback: () -> Boolean) {
        backdropToolbar.openMenuLongClickCallback = longClickCallback
    }

    fun setMenuLayout(@LayoutRes layoutResId: Int) {
        viewModel.emit(Event.PREFETCH_BACKDROP_CONTENT_VIEW, layoutResId)
        backdropToolbar.openMenuClickCallback = {
            showBackdropContent(layoutResId)
        }
    }
    /* endregion */
}