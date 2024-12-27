package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.action.RideHistoryAction
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.UiText

@Composable
fun DisplayHistoryErrorText(
    localErrorUiTextMessage: UiText?,
    networkErrorUiTextMessage: UiText?,
    context: Context,
    onAction: (RideHistoryAction) -> Unit
) {

    if (localErrorUiTextMessage != null) {
        onAction(RideHistoryAction.DelayedErrorClearance)
    }
    if (networkErrorUiTextMessage != null) {
        onAction(RideHistoryAction.DelayedErrorClearance)
    }


    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            localErrorUiTextMessage?.let {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = localErrorUiTextMessage?.asString(context) ?: "",
                    softWrap = true,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
//            }
//            networkErrorUiTextMessage?.let {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = networkErrorUiTextMessage?.asString(context) ?: "",
                    softWrap = true,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
//            }
        }
    }
}