package com.mgb.grolis.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.mgb.grolis.R
import kotlinx.android.synthetic.main.dialog_add_item.*

/**
 * Created by mgradob on 6/8/17.
 */
class AddItemDialog : DialogFragment() {

    private var listener: AddItemListener? = null

    override fun onStart() {
        super.onStart()

        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater?.inflate(R.layout.dialog_add_item, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        mAddButton.setOnClickListener {
            listener?.onAddClicked(mNameEditText.text.toString(), mNotesEditText.text.toString(), mQuantityEditText.text.toString().toInt())
            dismiss()
        }

        mCancelButton.setOnClickListener { dismiss() }
    }

    fun setAddItemListener(listener: AddItemListener) {
        this.listener = listener
    }

    interface AddItemListener {
        fun onAddClicked(name: String, notes: String, quantity: Int)
    }
}