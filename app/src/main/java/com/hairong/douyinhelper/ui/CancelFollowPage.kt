package com.hairong.douyinhelper.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CancelFollowPage(back: () -> Unit) {
    Scaffold(Modifier.fillMaxSize(), topBar = { TopBar(back) }) {
        StartRun()
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