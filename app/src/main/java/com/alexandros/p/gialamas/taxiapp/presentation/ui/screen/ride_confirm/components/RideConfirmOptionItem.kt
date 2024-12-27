package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption

@Composable
fun RideConfirmOptionItem(
    modifier: Modifier = Modifier,
    rideOption: RideOption,
    debounce: Boolean,
    onRideOptionSelected: (RideOption) -> Unit,
) {

    val textColor = Color.White

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth(0.9f)
            .padding(16.dp)
    ) {
        Column(
            modifier = modifier
                .background(Color.DarkGray)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.driver_label),
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = rideOption.name,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                modifier = modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.description_label),
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = rideOption.description,
                    textAlign = TextAlign.Justify,
                    softWrap = true,
                    color = textColor
                )
            }
            Text(
                modifier = modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.vehicle_label),
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                modifier = modifier
                    .fillMaxWidth(),
                text = rideOption.vehicle,
                color = textColor
            )

            if (rideOption.review.comment.isNotBlank()) {

                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = stringResource(R.string.comment_label),
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Box(
                    modifier = modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        modifier = modifier
                            .fillMaxWidth(),
                        text = rideOption.review.comment,
                        textAlign = TextAlign.Justify,
                        softWrap = true,
                        color = textColor
                    )
                }
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = modifier
                        .padding(end = 6.dp),
                    text = "${stringResource(R.string.rating_label)}: ",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    modifier = modifier
                        .padding(start = 6.dp),
                    text = rideOption.review.rating.toString(),
                    color = textColor
                )
            }

            Row(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = modifier
                        .padding(end = 6.dp),
                    text = "${stringResource(R.string.value_label)}: ",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    modifier = modifier
                        .padding(start = 6.dp),
                    text = "$${rideOption.value}",
                    color = textColor
                )
            }
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.DarkGray
                    ),
                    onClick = { onRideOptionSelected(rideOption) },
                    enabled = !debounce
                ) {
                    Text(
                        stringResource(R.string.select_driver_button_label),
                        color = Color.Black,
                        fontSize = 18.sp,
                    )
                }
            }
        }
    }
}
