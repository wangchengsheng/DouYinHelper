package com.hairong.douyinhelper.helper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.CallSuper
import com.hairong.douyinhelper.util.loge
import kotlinx.coroutines.*

abstract class BaseHelper(private val service: DouYinHelperService) {

    private var running = false
    private val scope = MainScope()
    private var job: Job? = null
    protected val nodeInfo: AccessibilityNodeInfo?
        get() = service.nodeInfo

    fun run() {
        if (running) return
        running = true
        launch()
    }

    private fun launch() {
        job?.cancel()
        scope.launch {
            try {
                while (running) execute()
            } catch (e: Exception) {
                log(e.message)
                loge("协程终止", e)
            } finally {
                running = false
            }
        }
    }

    protected abstract suspend fun execute()

    protected fun log(text: String?) {
        service.logWindow.log(text)
    }

    protected fun back() = service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)

    @CallSuper
    open fun stop() {
        job?.cancel()
        running = false
    }

    private val rect by lazy(LazyThreadSafetyMode.NONE) { Rect() }
    private val path by lazy(LazyThreadSafetyMode.NONE) { Path() }

    /**
     * 手势模拟点击
     */
    protected fun AccessibilityNodeInfo?.gestureClick(): Boolean {
        this ?: return false
        getBoundsInScreen(rect)
        path.reset()
        path.moveTo(rect.centerX().toFloat(), rect.centerY().toFloat())
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 80))
            .build()
        return service.dispatchGesture(gestureDescription, null, null)
    }

    /**
     * 手势模拟点击
     */
    protected suspend fun AccessibilityNodeInfo?.gestureDoubleClick(): Boolean {
        this ?: return false
        gestureClick()
        delay(120)
        return gestureClick()
    }

    /**
     * 向上滑动
     */
    protected fun AccessibilityNodeInfo?.gestureForward(): Boolean {
        this ?: return false
        getBoundsInScreen(rect)
        path.reset()
        path.moveTo(rect.centerX().toFloat(), rect.centerY().toFloat())
        path.rLineTo(5f, -rect.centerY().toFloat())
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 1, 300))
            .build()
        return service.dispatchGesture(
            gestureDescription, object : AccessibilityService.GestureResultCallback() {}, null
        )
    }

}