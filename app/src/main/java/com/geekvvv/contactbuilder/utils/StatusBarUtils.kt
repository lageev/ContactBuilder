package com.geekvvv.contactbuilder.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

object StatusBarUtils {

    fun setStatusBar(activity: Activity, darkContent: Boolean, statusBarColor: Int = Color.TRANSPARENT, translucent: Boolean) {
        val window = activity.window
        val decorView = window.decorView
        var visibility = decorView.systemUiVisibility

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = statusBarColor
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            visibility = if (darkContent) {
                visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

        if (translucent) {
            visibility = visibility  or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        decorView.systemUiVisibility = visibility
    }
}