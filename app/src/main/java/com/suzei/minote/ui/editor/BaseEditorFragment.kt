package com.suzei.minote.ui.editor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.suzei.minote.R
import com.suzei.minote.ext.convertToPx
import com.suzei.minote.ext.showColorWheel
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapter
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapterBuilder
import com.suzei.minote.utils.recycler_view.adapters.ColorListAdapterCallback
import com.suzei.minote.utils.recycler_view.decorator.GridSpacingItemDecoration

abstract class BaseEditorFragment : Fragment() {

    lateinit var noteColorsAdapter: ColorListAdapter
    lateinit var textColorsAdapter: ColorListAdapter
    lateinit var itemDecoration: GridSpacingItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        itemDecoration = GridSpacingItemDecoration(
                6,
                resources.getDimensionPixelSize(R.dimen.colorListSpacing),
                false
        )
        initAdapters()
    }

    private fun initAdapters() {
        noteColorsAdapter = ColorListAdapterBuilder.noteList(object : ColorListAdapterCallback {

            override fun onChangedColor(color: Int) {
                onChangeNoteColor(color)
            }

            override fun onShowColorWheel(color: Int) {
                showColorWheel("Choose note color", color) {
                    onShowNoteColorWheel(it)
                }
            }

        })

        textColorsAdapter = ColorListAdapterBuilder.textList(object : ColorListAdapterCallback {
            override fun onChangedColor(color: Int) {
                onChangeTextColor(color)
            }

            override fun onShowColorWheel(color: Int) {
                showColorWheel("Choose text color", color) {
                    onShowTextColorWheel(it)
                }
            }

        })
    }

    fun setupBottomSheet(rootView: ConstraintLayout, editContainer: View) {
        val bottomSheetBehavior = BottomSheetBehavior.from(rootView)
        val hiddenView = rootView.getChildAt(2)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    //  Set Editor (EditText) a margin to avoid
                    //  bottomsheet overlaps the editor
                    val params = CoordinatorLayout.LayoutParams(
                            CoordinatorLayout.LayoutParams.MATCH_PARENT,
                            CoordinatorLayout.LayoutParams.MATCH_PARENT
                    )
                    val bottomsheetSize = hiddenView.top
                    val appBarSize = 56.convertToPx(resources)
                    params.setMargins(0, appBarSize,
                            0, bottomsheetSize)
                    editContainer.layoutParams = params
                    editContainer.requestLayout()
                    bottomSheetBehavior.removeBottomSheetCallback(this)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.setPeekHeight(hiddenView.top, true)
        }
    }

    fun setupNoteColorRecyclerView(view: RecyclerView) {
        view.apply {
            adapter = noteColorsAdapter
            layoutManager = GridLayoutManager(activity!!, 6)
            addItemDecoration(itemDecoration)
        }
    }

    fun setupTextColorRecyclerView(view: RecyclerView) {
        view.apply {
            adapter = textColorsAdapter
            layoutManager = GridLayoutManager(activity!!, 6)
            addItemDecoration(itemDecoration)
        }
    }

    fun setupBack(view: ImageButton) {
        view.setOnClickListener {
            activity!!.finish()
        }
    }

    fun setTextWatcher(editText: EditText) = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            if (editText.hasFocus()) {
                onAfterTextChanged()
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }

    abstract fun onAfterTextChanged()
    abstract fun onChangeNoteColor(color: Int)
    abstract fun onChangeTextColor(color: Int)
    abstract fun onShowNoteColorWheel(color: Int)
    abstract fun onShowTextColorWheel(color: Int)
}