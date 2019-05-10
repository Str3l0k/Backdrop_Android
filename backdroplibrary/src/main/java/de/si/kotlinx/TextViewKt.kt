package de.si.kotlinx

import android.widget.TextView

fun TextView.fadeTextChange(newText: String?) {
    fade {
        text = newText
    }
}