package com.hairong.douyinhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.hairong.douyinhelper.data.configData
import com.hairong.douyinhelper.util.getAppVersionName
import com.hairong.douyinhelper.util.isAccessibilitySettingsOn
import com.hairong.douyinhelper.util.launchApp
import com.hairong.douyinhelper.util.startAccessibilitySettings

@Composable
fun MainPage(show: (Int) -> Unit) {
    var douYinVersion by remember { mutableStateOf("") }
    var enabled by remember { mutableStateOf(isAccessibilitySettingsOn()) }
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                enabled = isAccessibilitySettingsOn()
                douYinVersion = getAppVersionName()
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    val needVersion = "20.5.0"
    if (douYinVersion != needVersion) {
        NotSupport(douYinVersion, needVersion)
    } else {
        Content(enabled, show)
    }
}

@Suppress("SameParameterValue")
@Composable
private fun NotSupport(douYinVersion: String, needVersion: String) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text(
            text = "当前抖音版本 $douYinVersion 不支持\n请下载抖音版本 $needVersion",
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
private fun Content(enabled: Boolean, show: (Int) -> Unit) {
    Scaffold(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "建议关闭应用市场App自动更新功能",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.error
            )
            AccessibilityState(enabled)
            Spacer(modifier = Modifier.size(12.dp))
            ActionTime()
            Button(onClick = { show(0) }, enabled = enabled) {
                Text(text = "智能关注")
            }
            Button(onClick = { show(1) }, enabled = enabled) {
                Text(text = "浏览视频")
            }
            Button(onClick = { show(2) }, enabled = enabled) {
                Text(text = "浏览同城")
            }
            Button(onClick = { show(3) }, enabled = enabled) {
                Text(text = "取消关注")
            }
        }
    }
}

@Composable
private fun ActionTime() {
    var text by remember { mutableStateOf(configData.actionDelay.toString()) }
    TextField(
        value = text,
        onValueChange = {
            text = it
            text.toLongOrNull()?.let { time ->
                if (time < 1000) {
                    configData.actionDelay = 1000
                } else {
                    configData.actionDelay = time
                }
            }
        },
        label = { Text(text = "执行操作的时间间隔(毫秒)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@Composable
private fun AccessibilityState(enabled: Boolean) {
    if (enabled) {
        Text(text = "无障碍服务已开启")
    } else {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "无障碍服务未开启")
            Button(onClick = { startAccessibilitySettings() }, Modifier.padding(start = 12.dp)) {
                Text(text = "开启无障碍")
            }
        }
    }
}

@Composable
fun CheckItem(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        IconToggleButton(checked = checked, onCheckedChange = onCheckedChange) {
            Text(text = text)
        }
    }
}