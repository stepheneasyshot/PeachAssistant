package com.stephen.aiassistant.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SingleConfirmDialog(title: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier.width(320.dp).clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.onSecondary)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CenterText(title, modifier = Modifier.fillMaxWidth(1f).padding(10.dp))
            }
            Row(
                modifier = Modifier.height(40.dp).fillMaxWidth(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    SimpleDivider(Modifier.height(1.dp).fillMaxWidth(1f))
                    Row {
                        CenterText(
                            "知道了",
                            modifier = Modifier.clickable { onConfirm() }.weight(1f).fillMaxHeight(1f)
                        )
                    }
                }
            }
        }
    }
}