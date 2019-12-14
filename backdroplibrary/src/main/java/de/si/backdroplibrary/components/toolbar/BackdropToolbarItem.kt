package de.si.backdroplibrary.components.toolbar

data class BackdropToolbarItem(val title: String,
                               val subtitle: String? = null,
                               val primaryAction: Int? = null,
                               val moreActionEnabled: Boolean = false) {

    internal fun calculateDiff(other: BackdropToolbarItem): BackdropToolbarItemDiff {
        return BackdropToolbarItemDiff(titleChanged = this.title != other.title,
                                       subtitleChanged = this.subtitle != (other.subtitle ?: ""),
                                       primaryActionChanged = this.primaryAction != other.primaryAction,
                                       moreActionChanged = this.moreActionEnabled != other.moreActionEnabled)
    }
}
