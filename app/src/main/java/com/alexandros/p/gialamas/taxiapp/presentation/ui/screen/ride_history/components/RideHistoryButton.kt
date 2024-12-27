package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.RideHistoryState

@Composable
fun RideHistoryButton(
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController?,
    uiState: RideHistoryState,
    cancelRequest: () -> Unit,
    confirmRequest: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.DarkGray,
            ),
            enabled = uiState.canRequestAgain,
            onClick = {
                keyboardController?.hide()
                when {
                    uiState.isNetworkLoading -> {
                        cancelRequest()
                    }

                    else -> {
                        confirmRequest()
                    }
                }
            }) {
            Text(
                modifier = modifier
                    .padding(8.dp),
                text =
                when {
                    uiState.isNetworkLoading -> {
                        stringResource(R.string.cancel_button_label)
                    }

                    else -> {
                        stringResource(R.string.ride_history_button_label)
                    }
                },
                fontSize = 22.sp
            )
        }
    }
}