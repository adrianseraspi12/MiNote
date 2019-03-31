package com.suzei.minote.ui.editor.todo


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_editor_todo.*

import com.suzei.minote.R
import com.suzei.minote.utils.dialogs.InputDialog
import com.suzei.minote.utils.dialogs.InputDialogListener

/**
 * A simple [Fragment] subclass.
 *
 */
class EditorTodoFragment : Fragment(), View.OnClickListener {

    companion object {

        internal fun newInstance(): EditorTodoFragment {
            return EditorTodoFragment()
        }

        val INPUT_DIALOG_TAG = "INPUT_DIALOG_TAG"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editor_todo_add_item.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val button = v as Button
        val id = button.id

        when (id) {

            R.id.editor_todo_add_item -> {
                val inputDialog = InputDialog.instance

                inputDialog.setOnAddClickListener(object: InputDialogListener {

                    override fun onAddClick(message: String?) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        //TODO:  Add Item to RecyclerView convert it to TodoItem
                    }

                })

                inputDialog.show(fragmentManager!!, INPUT_DIALOG_TAG)
            }

        }

    }

}
