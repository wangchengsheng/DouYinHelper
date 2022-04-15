package com.hairong.douyinhelper.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hairong.douyinhelper.data.watchVideoBean

/**
 * 浏览视频
 */
@Composable
fun WatchVideoPage(back: () -> Unit) {
    Scaffold(Modifier.fillMaxSize(), topBar = { TopBar(back) }) {
        Column(
            Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            SearchKey()
            Divider()
            var text by remember { mutableStateOf(watchVideoBean.watchCount.toString()) }
            NeedCount(label = "浏览视频数量", text = text) { count ->
                text = count.toString()
                watchVideoBean.watchCount = count
            }
            Divider()
            FollowItem()
            Divider()
            LikeComment()
            Divider()
            StartRun()
        }
    }
}

@Composable
private fun TopBar(back: () -> Unit) {
    TopAppBar(title = { Text(text = "浏览视频") }, Modifier.fillMaxWidth(), navigationIcon = {
        IconButton(onClick = back) {
            Icon(Icons.Default.ArrowBack, null)
        }
    })
}

@Composable
private fun SearchKey() {
    var text by remember { mutableStateOf(watchVideoBean.searchKey) }
    TextField(
        value = text,
        onValueChange = {
            text = it
            watchVideoBean.searchKey = it
        },
        label = { Text(text = "搜索关键字") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
}

@Composable
private fun FollowItem() {
    Row {
        var checked by remember { mutableStateOf(watchVideoBean.isFollow) }
        CheckItem(text = "关注主播", checked = checked) {
            checked = it
            watchVideoBean.isFollow = checked
        }
        if (checked) {
            var text by remember { mutableStateOf(watchVideoBean.follow.toString()) }
            RandomEdit(
                Modifier
                    .padding(start = 12.dp)
                    .width(140.dp), label = "关注概率", text = text
            ) {
                text = it.toString()
                watchVideoBean.follow = it
            }
        }
    }
}

@Composable
private fun LikeComment() {
    var checked by remember { mutableStateOf(watchVideoBean.isLikeComment) }
    CheckItem(text = "点赞评论", checked = checked) {
        checked = it
        watchVideoBean.isLikeComment = checked
    }
    if (checked) {
        Row {
            var like by remember { mutableStateOf(watchVideoBean.like.toString()) }
            RandomEdit(Modifier.width(140.dp), label = "点赞概率", text = like) {
                like = it.toString()
                watchVideoBean.like = it
            }
            var comment by remember { mutableStateOf(watchVideoBean.comment.toString()) }
            RandomEdit(
                Modifier
                    .padding(start = 12.dp)
                    .width(140.dp), label = "评论概率", text = comment
            ) {
                comment = it.toString()
                watchVideoBean.comment = it
            }
        }
        CommentType()
    }
}

@Composable
private fun CommentType() {
    var select by remember { mutableStateOf(0) }
    val text = remember { arrayOf("自定义评论", "智能评论") }
    Row {
        for (i in text.indices) {
            RadioText(text[i], selected = select == i) {
                select = i
                watchVideoBean.customComment = select == 0
            }
        }
    }
    if (select == 0) {
        var input by remember { mutableStateOf(watchVideoBean.commentText) }
        TextField(
            value = input,
            onValueChange = {
                input = it
                watchVideoBean.commentText = it
            },
            label = { Text(text = "评论内容(”,“隔开)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
    }
}

@Composable
fun RandomEdit(modifier: Modifier = Modifier, label: String, text: String, change: (Int) -> Unit) {
    TextField(
        value = text,
        onValueChange = {
            if (it.isEmpty()) {
                change(1)
            } else {
                it.toIntOrNull()?.let { number ->
                    if (number in 1..99) change(number)
                }
            }
        },
        modifier = modifier,
        label = { Text(text = "$label(1-99)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@Composable
fun RadioText(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = text, Modifier.clickable(onClick = onClick))
    }
}