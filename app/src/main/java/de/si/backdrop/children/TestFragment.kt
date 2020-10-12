package de.si.backdrop.children

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdrop.R
import de.si.backdroplibrary.children.BackdropFragment
import kotlinx.android.synthetic.main.test_content.view.*
import kotlin.random.Random

class TestFragment : BackdropFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.test_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setBackgroundColor(Color.argb(100, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)))
        view.button_backdrop_content_test.setOnClickListener {
            changeBackgroundColor(Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255)))
        }
    }
}