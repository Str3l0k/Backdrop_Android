package de.si.kotlinx

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

val Context.layoutInflater: LayoutInflater?
    get() = LayoutInflater.from(this)

fun Context.inflateView(layoutResId: Int, parentViewGroup: ViewGroup): View? {
    return layoutInflater?.inflate(layoutResId, parentViewGroup, false)
}

fun Int.realPixelsFromDensityPixels(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}