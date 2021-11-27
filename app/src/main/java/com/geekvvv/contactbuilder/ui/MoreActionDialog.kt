package com.geekvvv.contactbuilder.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.geekvvv.contactbuilder.MainActivity
import com.geekvvv.contactbuilder.R

import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.google.android.material.bottomsheet.BottomSheetDialog


class MoreActionDialog : BottomSheetDialogFragment() {

    private lateinit var mBehavior: BottomSheetBehavior<*>
    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogBg)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.dialog_more_action, null)
        dialog.setContentView(view)
        checkBox3 = view.findViewById<CheckBox>(R.id.checkbox3)
        checkBox2 = view.findViewById<CheckBox>(R.id.checkbox2)
        checkBox1 = view.findViewById<CheckBox>(R.id.checkbox)
        view.findViewById<TextView>(R.id.other_relation).setOnClickListener {
            checkBox3.isChecked = !checkBox3.isChecked
        }
        view.findViewById<TextView>(R.id.work_relation).setOnClickListener {
            checkBox2.isChecked = !checkBox2.isChecked
        }
        view.findViewById<TextView>(R.id.best_relation).setOnClickListener {
            checkBox1.isChecked = !checkBox1.isChecked
        }

//        checkBox1.setOnCheckedChangeListener { buttonView, isChecked ->
//
//        }
//
//        checkBox2.setOnCheckedChangeListener { buttonView, isChecked ->
//
//        }
//
//        checkBox3.setOnCheckedChangeListener { buttonView, isChecked ->
//
//        }

        mBehavior = BottomSheetBehavior.from(view.parent as View)

        val layoutParams = view.layoutParams
        val height: Int =
            (dialog.context.resources.displayMetrics.heightPixels * 0.6).toInt()
        layoutParams.height = height
        view.layoutParams = layoutParams

        mBehavior.peekHeight = height


        return dialog
    }

    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val checkBox1Value=checkBox1.isChecked.valueTo("1","0")
        val checkBox2Value=checkBox2.isChecked.valueTo("1","0")
        val checkBox3Value=checkBox3.isChecked.valueTo("1","0")
        val activity=requireActivity()
        if (activity is MainActivity){
            activity.setting="$checkBox1Value$checkBox2Value$checkBox3Value"
        }
    }

    private inline fun <reified T : Any?> Boolean.valueTo(whenTrue: T, whenFalse: T): T {
        return if (this) {
            whenTrue
        } else {
            whenFalse
        }
    }
}