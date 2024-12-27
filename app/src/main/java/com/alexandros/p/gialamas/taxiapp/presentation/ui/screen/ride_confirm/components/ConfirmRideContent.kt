package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.state.RideConfirmState
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDistance
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDurationToReadableString

@Composable
fun ConfirmRideContent(
    modifier: Modifier = Modifier,
    rideEstimate: RideEstimate,
    uiState: RideConfirmState,
) {

    val textColor = Color.White

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
                .fillMaxWidth(0.9f)
                .padding(8.dp)
        ) {
            Column(
                modifier = modifier
                    .background(Color.DarkGray)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = "${stringResource(R.string.origin_location_label)}:",
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
                        text = uiState.origin,
                        textAlign = TextAlign.Justify,
                        softWrap = true,
                        color = textColor
                    )
                }
            }
        }
        Card(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
                .fillMaxWidth(0.9f)
                .padding(8.dp)
        ) {
            Column(
                modifier = modifier
                    .background(Color.DarkGray)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = "${stringResource(R.string.destination_location_label)}:",
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
                        text = uiState.destination,
                        textAlign = TextAlign.Justify,
                        softWrap = true,
                        color = textColor
                    )
                }
            }
        }

        Card(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
                .fillMaxWidth(0.9f)
                .padding(8.dp)
        ) {
            Column(
                modifier = modifier
                    .background(Color.DarkGray)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                val formattedDistance = formatDistance(rideEstimate.distance)
                val formattedDurationString =
                    formatDurationToReadableString(rideEstimate.duration.toInt())

                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        modifier = modifier
                            .padding(end = 6.dp),
                        text = "${stringResource(R.string.distance_label)}: ",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        modifier = modifier
                            .padding(start = 6.dp),
                        text = "$formattedDistance Km",
                        color = textColor
                    )
                }

                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        modifier = modifier
                            .padding(end = 6.dp),
                        text = "${stringResource(R.string.duration_label)}: ",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        modifier = modifier
                            .padding(start = 6.dp),
                        text = formattedDurationString,
                        color = textColor
                    )
                }
            }
        }
    }
}