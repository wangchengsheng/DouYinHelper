package com.hairong.douyinhelper.util

import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.os.bundleOf
import com.hairong.douyinhelper.appContext
import com.hairong.douyinhelper.data.configData
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout

/**
 * 启动无障碍设置界面
 */
fun startAccessibilitySettings() {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}

/**
 * 无障碍是否开启
 */
fun isAccessibilitySettingsOn(): Boolean {
    val serviceName = "${appContext.packageName}/com.hairong.douyinhelper.helper.DouYinHelperService"
    val string = Settings.Secure.getString(
        appContext.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    if (string != null) {
        TextUtils.SimpleStringSplitter(':').apply {
            setString(string)
        }.apply {
            while (hasNext()) {
                val next = next()
                if (next == serviceName) {
                    return true
                }
            }
        }
    }
    return false
}

//fun copyText(text: String) {
//    val cm = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//    cm.setPrimaryClip(ClipData.newPlainText(appContext.packageName, text))
//}

fun launchApp(packageName: String = "com.ss.android.ugc.aweme") {
    val intent = appContext.packageManager.getLaunchIntentForPackage(packageName)?.apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        appContext.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(appContext, "启动失败", Toast.LENGTH_SHORT).show()
    }
}

suspend inline fun <T> action(
    timeMillis: Long = if (configData.actionDelay >= 5000) configData.actionDelay * 6 else 30000,
    crossinline action: suspend () -> T?
) = withTimeout(timeMillis) {
    delayTime()
    var t: T? = null
    var finished = false
    while (!finished && configData.showLogWindow.value == true && isActive) {
        t = action()
        finished = if (t is Boolean) t else t != null
        if (!finished) delay(1500)
    }
    t!!
}

suspend fun delayTime() = delay(configData.actionDelay)

fun AccessibilityNodeInfo?.findId(id: String) =
    this?.findAccessibilityNodeInfosByViewId(id)?.firstOrNull()

fun AccessibilityNodeInfo?.findIdLast(id: String) =
    this?.findAccessibilityNodeInfosByViewId(id)?.lastOrNull()

fun AccessibilityNodeInfo?.hasText(text: String) =
    this?.findAccessibilityNodeInfosByText(text)?.firstOrNull() != null

fun AccessibilityNodeInfo?.click() =
    this?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false

fun AccessibilityNodeInfo?.scrollForward() =
    this?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) ?: false

fun AccessibilityNodeInfo?.setText(text: String): Boolean {
    this ?: return false
    val bundle = bundleOf(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE to text)
    return this.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle) ?: false
}