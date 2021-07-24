package com.suzei.minote.ui.editor.note

import com.suzei.minote.data.local.entity.Notes

class MockEditorNoteView: EditorNoteContract.View {

    var resultPresenter: EditorNoteContract.Presenter? = null
    var resultShowDetails: Notes? = null
    var resultMessage: String? = null
    var isAutoSaveEnable: Boolean = false
    var resultSetNoteColor: Int? = null
    var resultSetTextColor: Int? = null

    override fun setPresenter(presenter: EditorNoteContract.Presenter) {
        this.resultPresenter = presenter
    }

    override fun showDetails(data: Notes) {
        this.resultShowDetails = data
    }

    override fun showToastMessage(message: String) {
        this.resultMessage = message
    }

    override fun setSaveBtnVisibility(isAutoSaveEnable: Boolean) {
        this.isAutoSaveEnable = isAutoSaveEnable
    }

    override fun setNoteColor(color: Int) {
        this.resultSetNoteColor = color
    }

    override fun setTextColor(color: Int) {
        this.resultSetTextColor = color
    }
}