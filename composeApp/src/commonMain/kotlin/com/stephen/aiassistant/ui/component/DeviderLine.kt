package com.stephen.aiassistant.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DeviderLine(
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier.background(MaterialTheme.colorScheme.onSecondary)
    )
}