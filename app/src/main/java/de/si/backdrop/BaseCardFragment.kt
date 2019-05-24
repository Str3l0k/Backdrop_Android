package de.si.backdrop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdroplibrary.children.CardFragment
import de.si.backdroplibrary.components.ToolbarItem
import kotlin.random.Random

class BaseCardFragment : CardFragment() {
    override val toolbarItem: ToolbarItem
        get() = ToolbarItem(
            title = "Test",
//            subtitle = "Test subtitle",
            primaryAction = R.drawable.abc_ic_clear_material,
            moreActionEnabled = true
        )

    override fun onCreateContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.base_card, container, false)
    }

    override fun onContentViewCreated(view: View?, savedInstanceState: Bundle?) {
        // configure content view here
    }

    override fun onPrimaryActionClicked(): Boolean {
        changeToolbarItem(
            ToolbarItem(
                title = "Title ${Random.nextInt(42)}",
                primaryAction = toolbarItem.primaryAction,
                moreActionEnabled = toolbarItem.moreActionEnabled
            )
        )
        return true
    }

    override fun onMoreActionClicked(): Boolean {
        addCardFragment(MidCardFragment())
        return true
    }
}