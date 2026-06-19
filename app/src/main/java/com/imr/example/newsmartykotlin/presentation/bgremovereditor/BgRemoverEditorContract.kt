package com.imr.example.newsmartykotlin.presentation.bgremovereditor

enum class BgEditorTab {
    ALL,
    NEW_YEAR,
    CHRISTMAS
}

sealed class BgEditorBackground {
    data object Gallery : BgEditorBackground()
    data object Transparent : BgEditorBackground()
    data class GalleryImage(val imageUri: String) : BgEditorBackground()
    data class ColorBackground(val color: Long) : BgEditorBackground()
    data class DrawableBackground(val resId: Int) : BgEditorBackground()
}

data class BgRemoverEditorUiState(
    val removedImageUri: String = "",
    val selectedTab: BgEditorTab = BgEditorTab.ALL,
    val selectedBackground: BgEditorBackground = BgEditorBackground.Transparent,
    val flipX: Float = 1f,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

sealed interface BgRemoverEditorAction {
    data object BackClick : BgRemoverEditorAction
    data object SaveClick : BgRemoverEditorAction
    data object FlipClick : BgRemoverEditorAction
    data class TabClick(val tab: BgEditorTab) : BgRemoverEditorAction
    data class BackgroundClick(val background: BgEditorBackground) : BgRemoverEditorAction
}

sealed interface BgRemoverEditorEvent {
    data object Back : BgRemoverEditorEvent
    data class Saved(val imagePath: String) : BgRemoverEditorEvent
    data class Error(val message: String) : BgRemoverEditorEvent
}