package com.hairong.douyinhelper.helper

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.lifecycle.Observer
import com.hairong.douyinhelper.data.configData
import com.hairong.douyinhelper.ui.LogWindow
import com.hairong.douyinhelper.util.loge

class DouYinHelperService : AccessibilityService() {

    private lateinit var wm: WindowManager
    lateinit var logWindow: LogWindow
    var nodeInfo: AccessibilityNodeInfo? = null
    private var helper: BaseHelper? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        loge("onServiceConnected")
        initLogWindow()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (configData.showLogWindow.value != true) {
            loge("已停止，请打开App重新运行")
            return
        }
        event ?: return
//        loge(AccessibilityEvent.eventTypeToString(event.eventType))
        nodeInfo = rootInActiveWindow ?: return
        initHelper()
        helper!!.run()
    }

    private fun initLogWindow() {
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        logWindow = LogWindow(this)
        configData.showLogWindow.observeForever(observer)
    }

    private val observer = Observer<Boolean> {
        if (it) addLogWindow() else removeLogWindow()
    }

    private fun addLogWindow() {
        try {
            wm.addView(logWindow.view, logWindow.lp)
            logWindow.log("已启动")
        } catch (e: Exception) {
            loge("addLogWindow error", e)
        }
    }

    private fun removeLogWindow() {
        try {
            helper?.stop()
            helper = null
            nodeInfo?.recycle()
            nodeInfo = null
            wm.removeView(logWindow.view)
        } catch (e: Exception) {
            loge("removeLogWindow error", e)
        }
    }

    private fun initHelper() {
        if (helper != null) return
        helper = when (configData.type) {
            0 -> FollowHelper(this)
            1 -> WatchVideoHelper(this)
            2 -> CancelFollowHelper(this)
            else -> FollowHelper(this)
        }
    }

    override fun onInterrupt() {
        loge("onInterrupt")
        helper?.stop()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        loge("onUnbind")
        configData.showLogWindow.removeObserver(observer)
        helper?.stop()
        helper = null
        nodeInfo?.recycle()
        nodeInfo = null
        return super.onUnbind(intent)
    }

}