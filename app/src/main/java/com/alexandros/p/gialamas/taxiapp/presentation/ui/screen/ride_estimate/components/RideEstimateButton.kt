package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.action.RideEstimateAction
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.state.RideEstimateState

@Composable
fun RideEstimateButton(
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController?,
    isRideEstimateCallReady: Boolean,
    isStateLoading: Boolean,
    onAction: (RideEstimateAction) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (!isRideEstimateCallReady) {
            CircularProgressIndicator(
                modifier = modifier
                    .align(Alignment.Center),
                color = Color.LightGray,
            )
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.DarkGray
            ),
            enabled = isRideEstimateCallReady,
            onClick = {
                keyboardController?.hide()
                when {
                    isStateLoading -> {
                        onAction(RideEstimateAction.CancelApiRequest)
                    }

                    else -> {
                        onAction(RideEstimateAction.GetRideEstimate)
                    }
                }
            }
        ) {
            Text(
                modifier = modifier
                    .padding(8.dp),
                text =
                when {
                    isStateLoading -> {
                        stringResource(R.string.cancel_button_label)
                    }

                    else -> {
                        stringResource(R.string.ride_estimate_button_label)
                    }
                },
                fontSize = 22.sp
            )
        }
    }
}