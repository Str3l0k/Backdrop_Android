package de.si.backdrop.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdrop.R
import de.si.backdroplibrary.children.CardFragment
import de.si.backdroplibrary.components.ToolbarItem

class MidCardFragment : CardFragment() {
    override val toolbarItem: ToolbarItem
        get() = ToolbarItem(
            title = "Mid card",
            moreActionEnabled = false
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
//        changeTitle("Title ${Random.nextInt(42)}")
        return true
    }
}