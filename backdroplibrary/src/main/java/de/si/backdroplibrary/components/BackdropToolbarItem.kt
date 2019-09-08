package de.si.backdroplibrary.components

data class BackdropToolbarItem(val title: String,
                               val subtitle: String? = null,
                               val primaryAction: Int? = null,
                               val moreActionEnabled: Boolean = false)