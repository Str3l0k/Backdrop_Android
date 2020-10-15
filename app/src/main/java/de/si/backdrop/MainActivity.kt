package de.si.backdrop

import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import de.si.backdrop.children.BaseFragment
import de.si.backdrop.children.FullscreenRevealBackdropFragment
import de.si.backdrop.content.MainMenu
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.backdroplibrary.children.MainCardBackdropFragment
import de.si.kotlinx.globalCenterPoint

class MainActivity : BackdropActivity() {
    private var mainMenu: MainMenu? = null

    override val baseCardFragment: MainCardBackdropFragment = BaseFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                                                             WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    override fun onBackdropContentVisible(view: View): Boolean {
        return when (view.id) {
            R.id.menu_main_layout -> {
                configureTestMenuView(view)
                true
            }
            R.id.layout_test_content -> {
                view.findViewById<Button>(R.id.button_backdrop_content_test)
                        .setOnClickListener { button ->
                            revealFullscreenFragment(
                                    FullscreenRevealBackdropFragment(),
                                    button.globalCenterPoint,
                                    button.globalCenterPoint
                            )
                        }
                true
            }
            else                     -> false
        }
    }

    private fun configureTestMenuView(menuView: View) {
        when {
            menuView.id != MainMenu.resourceId -> return
            mainMenu == null                   -> {
                mainMenu = MainMenu(menuView, this)
            }
        }
    }
}
