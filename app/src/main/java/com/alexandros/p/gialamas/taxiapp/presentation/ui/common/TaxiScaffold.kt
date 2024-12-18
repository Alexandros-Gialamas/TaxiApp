package com.alexandros.p.gialamas.taxiapp.presentation.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun TaxiScaffold(
    modifier: Modifier = Modifier,
    columnModifier: Modifier = Modifier,
    columnVerticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    columnHorizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {


    Scaffold(

        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = FabPosition.Center,
        content = {
            paddingValues ->
            content(paddingValues)
        }

//            LazyColumn(
//                modifier = columnModifier
//                    .fillMaxSize()
//                    .padding(paddingValues = paddingValues)
//                    .consumeWindowInsets(paddingValues)
//                    .windowInsetsPadding(WindowInsets.safeDrawing),
//                verticalArrangement = columnVerticalArrangement,
//                horizontalAlignment = columnHorizontalAlignment
//            ) {
//                item {  content() }
//            }

//        }
    )

}