package com.hairong.douyinhelper.ui

import android.content.Context
import android.graphics.PixelFormat
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.TextView
import com.hairong.douyinhelper.R
import com.hairong.douyinhelper.data.configData

class LogWindow(context: Context) {

    val lp = WindowManager.LayoutParams().apply {
        width = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 220f, context.resources.displayMetrics
        ).toInt()
        height = WRAP_CONTENT
        gravity = Gravity.START or Gravity.TOP
        format = PixelFormat.RGBA_8888
        flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
    }

    private var textView: TextView
    private var stopView: TextView

    val view: View = LayoutInflater.from(context).inflate(R.layout.log_window, null).apply {
        textView = findViewById(R.id.text)
        stopView = findViewById<TextView>(R.id.stop).apply {
            setOnClickListener {
                configData.showLogWindow.value = false
            }
        }
    }

    fun log(text: String?) {
        textView.text = text
    }

}