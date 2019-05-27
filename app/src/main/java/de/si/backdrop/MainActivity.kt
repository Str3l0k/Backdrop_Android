package de.si.backdrop

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import de.si.backdrop.children.BaseCardFragment
import de.si.backdroplibrary.activity.Activity
import de.si.backdroplibrary.children.CardFragment
import de.si.backdroplibrary.children.FullscreenRevealFragment
import de.si.kotlinx.globalCenterPoint

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
                view.findViewById<Button>(R.id.button_backdrop_content_test).setOnClickListener { button ->
                    val fragment = RevealFragment()
                    fragment.revealEpiCenter = button.globalCenterPoint
                    revealFullscreenFragment(fragment)
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

class RevealFragment : FullscreenRevealFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.base_card, container, false)
        inflate.setBackgroundColor(Color.WHITE)
        return inflate
    }
}
