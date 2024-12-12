package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.RideEstimateViewModel
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.RideOptionItem
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun RideConfirmScreen(
    modifier: Modifier = Modifier,
    viewModel: RideEstimateViewModel = hiltViewModel(),
    onRideConfirmed: (RideOption) -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value



    uiState.rideEstimate.let { result ->
        when (result) {
            is Result.Error -> {
                Text("Error: ${result.error}")
            }

            is Result.Success -> {

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            result.data.origin.latitude,
                            result.data.origin.longitude
                        ), 10f
                    )
                }


                TaxiScaffold {

                    Text("Origin: ${result.data.origin}")
                    Text("Destination: ${result.data.destination}")

                    Text("Distance: ${result.data.distance}")
                    Text("Duration: ${result.data.duration}")

                    Spacer(modifier = modifier.height(8.dp))

                    GoogleMap(
                        modifier = modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    result.data.origin.latitude,
                                    result.data.origin.longitude
                                )
                            ),
                            title = "Origin"
                        )
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    result.data.destination.latitude,
                                    result.data.destination.longitude
                                )
                            ),
                            title = "Destination"
                        )
                    }

                    Spacer(modifier = modifier.height(8.dp))

                    result.data.options.forEach { rideOption ->
                        RideOptionItem(rideOption = rideOption) {
                            onRideConfirmed(rideOption)
                        }
                    }


                }


            } else -> {}
        }
    }
}








