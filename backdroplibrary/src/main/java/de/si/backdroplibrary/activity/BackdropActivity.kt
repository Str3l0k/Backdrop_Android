package de.si.backdroplibrary.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_ANIMATION_DURATION
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_CLOSED_TRANSLATION_Y
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.R
import de.si.backdroplibrary.children.MainCardBackdropFragment
import de.si.backdroplibrary.components.BackdropCardStack
import de.si.backdroplibrary.components.BackdropContent
import de.si.backdroplibrary.components.BackdropToolbar
import de.si.backdroplibrary.components.FullscreenDialogs
import de.si.backdroplibrary.gestures.BackdropGestureNavigationListener
import kotlinx.android.synthetic.main.layout_main.*

abstract class BackdropActivity : AppCompatActivity(), BackdropComponent {

    // viewModel
    override val viewModel by lazy {
        BackdropViewModel.registeredInstance(this)
    }

    override val backdropActivity: BackdropActivity
        get() = this

    //-----------------------------------------
    //  backdrop components
    //-----------------------------------------
    internal lateinit var backdropToolbar: BackdropToolbar
    internal lateinit var backdropContent: BackdropContent
    internal lateinit var backdropCardStack: BackdropCardStack
    internal lateinit var fullscreenDialogs: FullscreenDialogs
    internal lateinit var gestureNavigationListener: BackdropGestureNavigationListener
    internal lateinit var gestureDetector: GestureDetector

    //-----------------------------------------
    // properties
    //-----------------------------------------
    abstract val baseCardFragment: MainCardBackdropFragment
    internal var menuLayoutRes: Int = 0

    private val backdropOpen: Boolean
        get() = backdropCardStack.isTranslatedByY

    //-----------------------------------------
    // animation properties
    //-----------------------------------------
    private val backdropOpenCloseAnimator by lazy {
        ObjectAnimator.ofFloat(layout_backdrop_cardstack, View.TRANSLATION_Y, 0f).apply {
            duration = BACKDROP_ANIMATION_DURATION
        }
    }

    //-----------------------------------------
    // region lifecycle
    //-----------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)
        initializeViewModel()
        initializeComponents()
        initializeBaseCardFragment()
        initializeBaseToolbarItem()
        initializeGestureNavigation()
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
            backdropOpen -> {
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun initializeViewModel() {
        viewModel.registerEventCallback(this::onEvent)
    }

    private fun initializeComponents() {
        backdropContent = BackdropContent(this)
        backdropCardStack = BackdropCardStack(this)
        backdropToolbar = BackdropToolbar(this)
        backdropToolbar.closeBackdropClickCallback = {
            hideBackdropContent()
        }
        fullscreenDialogs = FullscreenDialogs(this)
    }

    private fun initializeBaseCardFragment() {
        backdropCardStack.baseFragment = baseCardFragment
    }

    private fun initializeBaseToolbarItem() {
        backdropToolbar.configure(baseCardFragment.toolbarItem, baseCardFragment.menuButtonState)
    }

    private fun initializeGestureNavigation() {
        gestureNavigationListener = BackdropGestureNavigationListener()
        gestureDetector = GestureDetector(applicationContext, gestureNavigationListener)

        gestureNavigationListener.onFlingDownCallback = {
            if (viewModel.gestureNavigationEnabled) {
                if (backdropCardStack.hasMoreThanOneEntry) {
                    removeTopCardFragment()
                } else if (backdropToolbar.menuButtonVisible && backdropOpen.not() && menuLayoutRes > 0) {
                    showBackdropContent(menuLayoutRes)
                }
            }
        }
    }
    //-----------------------------------------
    // endregion lifecycle
    //-----------------------------------------

    //-----------------------------------------
    // region animation functions
    //-----------------------------------------
    internal fun animateBackdropOpening(translationY: Float) {
        backdropOpenCloseAnimator.setFloatValues(translationY)
        backdropOpenCloseAnimator.start()
    }

    internal fun animateBackdropClosing() {
        backdropOpenCloseAnimator.setFloatValues(BACKDROP_CLOSED_TRANSLATION_Y)
        backdropOpenCloseAnimator.start()
    }
    //-----------------------------------------
    // endregion animation functions
    //-----------------------------------------

    //-----------------------------------------
    // region main menu
    //-----------------------------------------
    @Suppress("unused")
    fun setMenuButtonClickCallback(clickCallback: () -> Unit) {
        backdropToolbar.openMenuClickCallback = clickCallback
    }

    @Suppress("unused")
    fun setMenuButtonLongClickCallback(longClickCallback: () -> Boolean) {
        backdropToolbar.openMenuLongClickCallback = longClickCallback
    }

    fun setMenuLayout(@LayoutRes layoutResId: Int) {
        menuLayoutRes = layoutResId
        viewModel.emit(Event.PREFETCH_BACKDROP_CONTENT_VIEW, layoutResId)
        backdropToolbar.showMenuButton()
        backdropToolbar.openMenuClickCallback = {
            showBackdropContent(layoutResId)
        }
    }
    //-----------------------------------------
    // endregion
    //-----------------------------------------
}