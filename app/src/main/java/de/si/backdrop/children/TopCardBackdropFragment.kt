package de.si.backdrop.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdrop.R
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.backdroplibrary.components.BackdropToolbarItem

class TopCardBackdropFragment : CardBackdropFragment() {
    override val toolbarItem: BackdropToolbarItem = BackdropToolbarItem(title = "Mid card",
                                                                        moreActionEnabled = false,
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
        return true
    }
}