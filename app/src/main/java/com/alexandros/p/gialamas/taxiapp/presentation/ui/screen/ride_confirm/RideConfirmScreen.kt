package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.RideEstimateContent
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.RideEstimateState
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.RideEstimateViewModel
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.RideOptionItem
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.formatDistance
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.formatDurationToReadableString
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun RideConfirmScreen(
    modifier: Modifier = Modifier,
    viewModel: RideEstimateViewModel = hiltViewModel<RideEstimateViewModel>(),
    result: RideEstimate,
    onRideConfirmed: (RideOption) -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                result.origin.latitude,
                result.origin.longitude
            ), 10f
        )
    }


    TaxiScaffold {

        LazyColumn (
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            item { Spacer(modifier = modifier.height(16.dp)) }

            item {
                ConfirmRideContent(
                    rideEstimate = result,
                    uiState = uiState,
                )
            }


            item { Spacer(modifier = modifier.height(8.dp)) }

            item {
                GoogleMap(
                    modifier = modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                result.origin.latitude,
                                result.origin.longitude
                            )
                        ),
                        title = "Origin"
                    )
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                result.destination.latitude,
                                result.destination.longitude
                            )
                        ),
                        title = "Destination"
                    )
                }
            }

            item { Spacer(modifier = modifier.height(8.dp)) }

            items(result.options) { rideOption ->
                RideOptionItem(rideOption = rideOption) {
                    onRideConfirmed(rideOption)
                }
            }
        }

    }

}


@Composable
fun ConfirmRideContent(
    modifier: Modifier = Modifier,
    rideEstimate: RideEstimate,
    uiState: RideEstimateState,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${stringResource(R.string.origin_location_label)}:",
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.origin, textAlign = TextAlign.Center, softWrap = true)
                }

                Row {
                    Text(
                        text = "${stringResource(R.string.latitude_label)}: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = rideEstimate.origin.latitude.toString())
                }
                Row {
                    Text(
                        text = "${stringResource(R.string.longitude_label)}: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = rideEstimate.origin.longitude.toString())
                }
            }
        }



        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${stringResource(R.string.destination_location_label)}:",
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.destination, textAlign = TextAlign.Center, softWrap = true)
                }
                Row {
                    Text(
                        text = "${stringResource(R.string.latitude_label)}: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = rideEstimate.destination.latitude.toString())
                }
                Row {
                    Text(
                        text = "${stringResource(R.string.longitude_label)}: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = rideEstimate.destination.longitude.toString())
                }
            }
        }



        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val formattedDistance = formatDistance(rideEstimate.distance)
                val formattedDurationString =
                    formatDurationToReadableString(rideEstimate.duration.toInt())
                Row {
                    Text(
                        text = "${stringResource(R.string.distance_label)}: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = formattedDistance)
                }

                Row {
                    Text(
                        text = "${stringResource(R.string.duration_label)}: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = formattedDurationString)
                }
            }
        }

    }
}














