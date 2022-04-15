package com.hairong.douyinhelper.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hairong.douyinhelper.data.configData
import com.hairong.douyinhelper.data.followBean
import com.hairong.douyinhelper.util.launchApp

/**
 * 关注、吸粉
 */
@Composable
fun ConcernPage(back: () -> Unit) {
    Scaffold(Modifier.fillMaxSize(), topBar = { TopBar(back) }) {
        Column(
            Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            var text by remember { mutableStateOf(followBean.followCount.toString()) }
            NeedCount(label = "关注数量", text = text) { count ->
                text = count.toString()
                followBean.followCount = count
            }
            CommentText()
            SkipConfigs()
            StartRun()
        }
    }
}

@Composable
private fun TopBar(back: () -> Unit) {
    TopAppBar(title = { Text(text = "智能关注") }, Modifier.fillMaxWidth(), navigationIcon = {
        IconButton(
            onClick = back
        ) {
            Icon(Icons.Default.ArrowBack, null)
        }
    })
}

@Composable
fun NeedCount(modifier: Modifier = Modifier, label: String, text: String, change: (Int) -> Unit) {
    TextField(
        value = text,
        onValueChange = {
            if (it.isEmpty()) {
                change(1)
            } else {
                it.toIntOrNull()?.let { number ->
                    change(number)
                }
            }
        },
        modifier = modifier,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@Composable
private fun CommentText() {
    var text by remember { mutableStateOf(followBean.comment) }
    TextField(
        value = text,
        onValueChange = {
            text = it
            followBean.comment = text
        },
        label = { Text(text = "评论内容") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
}

@Composable
private fun SkipConfigs() {
    var skip1 by remember { mutableStateOf(true) }
    CheckItem(text = "跳过0作品", checked = skip1) {
        skip1 = it
        followBean.skipVideo0 = skip1
    }
    var skip2 by remember { mutableStateOf(true) }
    CheckItem(text = "跳过私密账号", checked = skip2) {
        skip2 = it
        followBean.skipPrivacy = skip2
    }
}

@Composable
fun StartRun() {
    Button(onClick = {
        configData.showLogWindow.value = true
        launchApp()
    }) {
        Text(text = "开始运行")
    }
}