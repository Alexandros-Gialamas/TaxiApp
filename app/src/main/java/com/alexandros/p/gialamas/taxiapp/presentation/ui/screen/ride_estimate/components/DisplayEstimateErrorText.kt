package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.components

import android.content.Context
import androidx.compose.foundation.layout.Box
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
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.action.RideEstimateAction
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.UiText

@Composable
fun DisplayEstimateErrorText(
    errorUiTextMessage: UiText?,
    context: Context,
    onAction: (RideEstimateAction) -> Unit
) {

    if (errorUiTextMessage != null) {
        onAction(RideEstimateAction.DelayedErrorClearance)
    }

//    errorUiTextMessage?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = errorUiTextMessage?.asString(context = context) ?: "",
                softWrap = true,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp
            )
        }
//    }
}