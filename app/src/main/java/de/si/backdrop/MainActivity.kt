package de.si.backdrop

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import de.si.backdroplibrary.activity.Activity
import de.si.backdroplibrary.children.CardFragment

class MainActivity : Activity() {
    override val baseCardFragment: CardFragment = BaseCardFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMenuLayout(R.layout.test_menu)
    }

    override fun onBackdropContentVisible(view: View): Boolean {
        return when (view.id) {
            R.id.backdrop_main_menu_layout -> {
                configureTestMenuView(view)
                true
            }
            R.id.layout_test_content -> {
                view.findViewById<Button>(R.id.button_backdrop_content_test).setOnClickListener {
                    Snackbar.make(view, "Test", Snackbar.LENGTH_SHORT).show()
                    showFullscreenFragment(FullscreenDialogFragment())
                }
                true
            }
            else -> false
        }
    }

    private fun configureTestMenuView(menuView: View) {
        if (menuView.id != R.id.backdrop_main_menu_layout) {
            return
        }

        val buttonTest = menuView.findViewById<View>(R.id.backdrop_main_menu_feedback)
        buttonTest.setOnClickListener {
            showBackdropContent(R.layout.backdrop_test_content)
        }
    }
}
