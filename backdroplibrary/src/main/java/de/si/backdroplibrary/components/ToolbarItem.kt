package de.si.backdroplibrary.components

data class ToolbarItem(
    val title: String,
    val subtitle: String? = null,
    val primaryAction: Int? = null,
    val moreActionEnabled: Boolean = false
)