package de.si.backdroplibrary.activity

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.luminance
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_ANIMATION_DURATION
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_CLOSED_TRANSLATION_Y
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.R
import de.si.backdroplibrary.children.MainCardBackdropFragment
import de.si.backdroplibrary.components.BackdropCardStack
import de.si.backdroplibrary.components.BackdropContent
import de.si.backdroplibrary.components.FullscreenDialogs
import de.si.backdroplibrary.components.toolbar.BackdropToolbar
import de.si.backdroplibrary.components.toolbar.BackdropToolbarMainButtonState
import de.si.backdroplibrary.gestures.BackdropGestureNavigationListener
import kotlinx.android.synthetic.main.layout_main.*

abstract class BackdropActivity : AppCompatActivity(), BackdropComponent {

    //-----------------------------------------
    // viewModel
    //-----------------------------------------
    override val backdropViewModel by lazy {
        BackdropViewModel.registeredInstance(activity = this)
    }

    override val backdropActivity: BackdropActivity by lazy { this }

    //-----------------------------------------
    //  backdrop components
    //-----------------------------------------
    internal lateinit var backdropToolbar: BackdropToolbar
    internal lateinit var backdropContent: BackdropContent
    internal lateinit var backdropCardStack: BackdropCardStack
    internal lateinit var fullscreenDialogs: FullscreenDialogs
    private lateinit var gestureNavigationListener: BackdropGestureNavigationListener
    private lateinit var gestureDetector: GestureDetector

    //-----------------------------------------
    // properties
    //-----------------------------------------
    abstract val baseCardFragment: MainCardBackdropFragment
    private val menuLayoutRes: Int? by lazy { baseCardFragment.mainMenuRes }

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
    // lifecycle
    //-----------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_main)

        initializeSystemUi()
        initializeViewModel()
        initializeComponents()
        initializeBaseCardFragment()
        initializeBaseToolbarItem()
        initializeGestureNavigation()
        initializeMainMenu()
    }

    override fun onRestart() {
        super.onRestart()
        initializeViewModel()
    }

    override fun onStop() {
        super.onStop()
        backdropViewModel.unregisterEventCallbacks(backdropComponent = this)
    }

    override fun onBackPressed() {
        when {
            fullscreenDialogs.isVisible           -> fullscreenDialogs.hideFullscreenFragment()
            backdropOpen                          -> hideBackdropContent()
            backdropToolbar.isInActionMode        -> finishToolbarActionMode()
            backdropCardStack.hasMoreThanOneEntry -> removeTopCardFragment()
            else                                  -> super.onBackPressed()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onMenuActionClicked(): Boolean {
        baseCardFragment.mainMenuRes?.let(this::showBackdropContent)
        return true
    }

    //-----------------------------------------
    // Animation functions
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
    // Helper
    //-----------------------------------------
    private fun initializeSystemUi() {
        window.apply {

            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            configureStatusBarAppearance(ContextCompat.getColor(this@BackdropActivity, R.color.colorPrimary))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setDecorFitsSystemWindows(false)
            }
        }
    }

    internal fun configureStatusBarAppearance(@ColorInt color: Int) {
        when {
            color.luminance < 0.45 -> setSystemsBarDarkAppearance()
            else                   -> setSystemsBarLightAppearance()
        }
    }

    private fun setSystemsBarLightAppearance() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                                                             WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        }
    }

    private fun setSystemsBarDarkAppearance() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            window.insetsController?.setSystemBarsAppearance(0,
                                                             WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        }
    }

    private fun initializeViewModel() {
        backdropViewModel.registerEventCallback(
                backdropComponent = this,
                callbackBackdrop = this::onEvent
        )
    }

    private fun initializeComponents() {
        backdropContent = BackdropContent(backdropActivity = this)
        backdropCardStack = BackdropCardStack(backdropActivity = this)
        backdropToolbar = BackdropToolbar(backdropActivity = this)
        fullscreenDialogs = FullscreenDialogs(backdropActivity = this)
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
            if (backdropViewModel.gestureNavigationEnabled) {
                if (backdropCardStack.hasMoreThanOneEntry) {
                    removeTopCardFragment()
                } else if (backdropToolbar.menuButtonVisible && backdropOpen.not() && menuLayoutRes != null) {
                    menuLayoutRes?.let(this::showBackdropContent)
                }
            }
        }
    }

    internal fun initializeMainMenu() {
        val menuRes: Int? = baseCardFragment.mainMenuRes

        if (menuRes != null) {
            prefetchBackdropContent(menuRes)
        } else {
            backdropToolbar.configure(baseCardFragment.toolbarItem, BackdropToolbarMainButtonState.NO_LAYOUT_ERROR)
        }
    }
}
