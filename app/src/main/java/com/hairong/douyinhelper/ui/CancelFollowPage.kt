package com.hairong.douyinhelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.unit.dp
import com.hairong.douyinhelper.data.cancelFollowBean

@Composable
fun CancelFollowPage(back: () -> Unit) {
    Scaffold(Modifier.fillMaxSize(), topBar = { TopBar(back) }) {
        Column(
            Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            CancelType()
            StartRun()
        }
    }
}

@Composable
private fun TopBar(back: () -> Unit) {
    TopAppBar(title = { Text(text = "取消关注") }, Modifier.fillMaxWidth(), navigationIcon = {
        IconButton(
            onClick = back
        ) {
            Icon(Icons.Default.ArrowBack, null)
        }
    })
}

@Composable
private fun CancelType() {
    var select by remember { mutableStateOf(1) }
    Row(Modifier.selectableGroup()) {
        CheckItem(text = "取消全部关注", checked = select == 0) {
            select = 0
            cancelFollowBean.type = select
        }
        CheckItem(text = "排除互关用户", checked = select == 1) {
            select = 1
            cancelFollowBean.type = select
        }
    }
}
