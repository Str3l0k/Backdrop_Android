package de.si.backdrop.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdrop.R
import de.si.backdroplibrary.children.MainCardBackdropFragment
import de.si.backdroplibrary.components.toolbar.BackdropToolbarItem
import de.si.backdroplibrary.components.toolbar.BackdropToolbarMainButtonState
import kotlinx.android.synthetic.main.base_card.*
import kotlin.random.Random

class BaseFragment : MainCardBackdropFragment() {

    override val menuButtonState: BackdropToolbarMainButtonState = BackdropToolbarMainButtonState.MENU
    override val mainMenuRes: Int? = null

    override val toolbarItem: BackdropToolbarItem = BackdropToolbarItem(title = "Backdrop",
                                                                        subtitle = "Demonstration",
                                                                        primaryAction = R.drawable.ic_add,
                                                                        moreActionEnabled = true)

    override fun onCreateContentView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.base_card, container, false)
    }

    override fun onContentViewCreated(view: View?, savedInstanceState: Bundle?) {
        // configure content view here
        button.setOnClickListener {
            changeToolbarItem(toolbarItem)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem -> showFragmentForNavigationItemId(menuItem.itemId) }
        bottomNavigationView.setOnNavigationItemReselectedListener { }

        showFragmentForNavigationItemId(bottomNavigationView.selectedItemId)
    }

    override fun onPrimaryActionClicked(): Boolean {
        changeToolbarItem(BackdropToolbarItem(title = "${Random.nextInt(until = 42)}",
                                              primaryAction = toolbarItem.primaryAction,
                                              moreActionEnabled = toolbarItem.moreActionEnabled.not()))
        return true
    }

    override fun onMoreActionClicked(): Boolean {
        addCardFragment(TopCardBackdropFragment())
        return true
    }

    override fun onFragmentWillBeCovered() {
        println("BaseFragment.onFragmentWillBeCovered")
    }

    override fun onFragmentWillBeRevealed() {
        println("BaseFragment.onFragmentWillBeRevealed")
    }

    private fun showFragmentForNavigationItemId(itemId: Int): Boolean {
        return when (itemId) {
            R.id.home,
            R.id.details -> {
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.testContainer, TestFragment())
                }.commit()
                true
            }
            else         -> false
        }
    }
}
