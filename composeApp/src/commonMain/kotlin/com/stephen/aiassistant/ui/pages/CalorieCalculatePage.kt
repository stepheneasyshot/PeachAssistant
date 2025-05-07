package com.stephen.aiassistant.ui.pages

import aiassistant.composeapp.generated.resources.Res
import aiassistant.composeapp.generated.resources.arrow_left
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.stephen.aiassistant.ui.component.CenterText
import com.stephen.aiassistant.ui.theme.titleFirstText
import com.stephen.aiassistant.ui.theme.titleSecondText
import com.stephen.aiassistant.vm.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@Composable
fun CalorieCalculatePage(
    viewModel: MainViewModel = getKoin().get(),
    onBackpressed: () -> Unit = {}
) {

    // 食物和卡路里的对应关系
    val fakeList = remember { mutableStateListOf<Pair<String, Int>>() }

    LaunchedEffect(Unit) {
        fakeList.add(Pair("苹果", 100))
        fakeList.add(Pair("可乐", 200))
        fakeList.add(Pair("汉堡", 300))
        fakeList.add(Pair("薯条", 400))
        fakeList.add(Pair("炸鸡", 400))
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(1f).padding(bottom = 20.dp)
        ) {
            // 返回图标
            Image(
                painter = painterResource(Res.drawable.arrow_left),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                contentDescription = "back",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(28.dp)
                    .clickable { onBackpressed() }
            )
            CenterText(
                text = "拍照计算食物卡路里",
                style = titleFirstText,
            )
        }

        Box(
            modifier = Modifier.padding(10.dp).fillMaxWidth(1f).heightIn(min = 200.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            CenterText(
                text = "点击上传照片",
            )
        }

        Column {
            CenterText(
                text = "食物列表", modifier = Modifier.padding(bottom = 10.dp),
                style = titleSecondText
            )
            LazyColumn {
                items(fakeList, key = { it.first }) {
                    FoodListItem(it.first, it.second)
                }
            }
        }

    }
}

@Composable
fun FoodListItem(
    name: String,
    calories: Int,
    modifier: Modifier = Modifier,
) {
    Row {
        CenterText(
            text = name,
            modifier = Modifier.weight(3f),
        )
        CenterText(
            text = "$calories kcal",
            modifier = Modifier.weight(1f),
        )
    }
}
