package de.si.backdroplibrary

import de.si.backdroplibrary.activity.BackdropActivity

open class BackdropComponent(internal val activity: BackdropActivity) {
    internal val viewModel: BackdropViewModel by lazy {
        BackdropViewModel.registeredInstance(activity)
    }
}