package com.stephen.copywritinggene.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.stephen.copywritinggene.ui.PageNavKey
import com.stephen.copywritinggene.ui.component.CenterText
import com.stephen.copywritinggene.ui.theme.titleFirstText
import com.stephen.copywritinggene.ui.theme.titleSecondText
import copywritinggene.composeapp.generated.resources.Res
import copywritinggene.composeapp.generated.resources.bg_calorie_cal
import copywritinggene.composeapp.generated.resources.bg_social_post_gene
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainCardPage(
    funcList: List<Pair<PageNavKey, DrawableResource>> = listOf(
        Pair(PageNavKey.COPY_WRITING_GENE, Res.drawable.bg_social_post_gene),
        Pair(PageNavKey.CALORIE_CALCULATOR, Res.drawable.bg_calorie_cal),
    ),
    onItemChoosed: (PageNavKey) -> Unit = {}
) {
    Column {
        CenterText(
            text = "功能列表", modifier = Modifier.padding(bottom = 10.dp),
            style = titleFirstText
        )
        LazyColumn {
            items(funcList, key = { it.first.name }) {
                FunctionCard(it, Modifier.fillMaxWidth(1f).height(220.dp)) {
                    onItemChoosed(it.first)
                }
            }
        }
    }
}

@Composable
fun FunctionCard(
    funcInfo: Pair<PageNavKey, DrawableResource>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onClick()
            }) {
        Image(
            painter = painterResource(funcInfo.second),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f)
        )
        CenterText(
            text = funcInfo.first.content,
            style = titleSecondText,
            modifier = Modifier.padding(10.dp).align(Alignment.BottomEnd)
        )
    }
}