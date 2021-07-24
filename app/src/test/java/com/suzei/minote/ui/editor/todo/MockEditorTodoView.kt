package com.suzei.minote.ui.editor.todo

import com.suzei.minote.data.local.entity.Todo

class MockEditorTodoView: EditorTodoContract.View {

    var resultPresenter: EditorTodoContract.Presenter? = null
    var resultShowDetails: Todo? = null
    var resultMessage: String? = null
    var isAutoSaveEnable: Boolean = false
    var resultSetNoteColor: Int? = null
    var resultSetTextColor: Int? = null

    override fun setPresenter(presenter: EditorTodoContract.Presenter) {
        this.resultPresenter = presenter
    }

    override fun showDetails(data: Todo) {
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