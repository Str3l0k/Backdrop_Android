package de.si.backdrop.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdrop.R
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.backdroplibrary.components.toolbar.BackdropToolbarItem

class TopCardBackdropFragment : CardBackdropFragment() {
    override var toolbarItem: BackdropToolbarItem =
        BackdropToolbarItem(title = "Mid card",
                            moreActionEnabled = true,
                            primaryAction = R.drawable.ic_add)

    override fun onCreateContentView(inflater: LayoutInflater,
                                     container: ViewGroup?,
                                     savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.top_card, container, false)
    }

    override fun onContentViewCreated(view: View?, savedInstanceState: Bundle?) {
        // configure content view here
    }

    override fun onPrimaryActionClicked(): Boolean {
        toolbarItem = if (toolbarItem.subtitle == null) {
            toolbarItem.copy(subtitle = "Test subtitle animation")
        } else {
            toolbarItem.copy(subtitle = null)
        }

        changeToolbarItem(toolbarItem)
        return true
    }

    override fun onMoreActionClicked(): Boolean {
        startToolbarActionMode(BackdropToolbarItem(title = "Card action mode",
                                                   primaryAction = R.drawable.ic_change_content))
        return true
    }

    override fun onToolbarActionModeFinished(): Boolean {
        println("TopCardBackdropFragment.onToolbarActionModeFinished")
        return true
    }
}