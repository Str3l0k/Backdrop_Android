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
    override val menuButtonState: BackdropToolbarMainButtonState
        get() = BackdropToolbarMainButtonState.MENU

    override val toolbarItem: BackdropToolbarItem =
        BackdropToolbarItem(title = "Backdrop",
                            subtitle = "Demonstration",
                            primaryAction = R.drawable.ic_add,
                            moreActionEnabled = true)

    override fun onCreateContentView(inflater: LayoutInflater,
                                     container: ViewGroup?,
                                     savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.base_card, container, false)
    }

    override fun onContentViewCreated(view: View?, savedInstanceState: Bundle?) {
        // configure content view here
        button.setOnClickListener {

        }
    }

    override fun onPrimaryActionClicked(): Boolean {
        changeToolbarItem(BackdropToolbarItem(title = "Title ${Random.nextInt(42)}",
                                              primaryAction = toolbarItem.primaryAction,
                                              moreActionEnabled = toolbarItem.moreActionEnabled))
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
}