package de.si.backdroplibrary.children

import androidx.fragment.app.Fragment
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.activity.BackdropActivity

abstract class BackdropFragment : Fragment(), BackdropComponent {
    override val viewModel: BackdropViewModel by lazy {
        val backdropActivity = activity as? BackdropActivity
        backdropActivity?.let {
            BackdropViewModel.registeredInstance(it)
        } ?: throw ExceptionInInitializerError()
    }
}