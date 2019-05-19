package de.si.backdroplibrary.components

import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.activity.BackdropActivity
import kotlinx.android.synthetic.main.backdrop_base.*

class BackdropCardStack(private val activity: BackdropActivity) {

    // view elements
    private val layoutContainer = activity.layout_backdrop_cardstack
    private val layoutTouchBlock = activity.layout_cardstack_touchblock

    // view model
    private val viewModel: BackdropViewModel = BackdropViewModel.registeredInstance(activity)
}