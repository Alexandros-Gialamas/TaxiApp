package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.formatDistance
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.formatDurationToReadableString
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun RideConfirmScreen(
    modifier: Modifier = Modifier,
    viewModel: RideConfirmViewModel = hiltViewModel<RideConfirmViewModel>(),
    result: RideEstimate,
    customerId: String,
    origin: String,
    destination: String,
    onRideConfirmed: (RideOption) -> Unit,
    onBackPress: () -> Unit
) {
    BackHandler {
        onBackPress()
    }

    LaunchedEffect(Unit) {
        viewModel.collectState(customerId, origin, destination, result)
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                result.origin.latitude,
                result.origin.longitude
            ), 11f
        )
    }

    val driverMinKmMap = mapOf<Int, Int>(
        1 to 1,
        2 to 5,
        3 to 10
    )

    val context = LocalContext.current
    var wrongKm by remember { mutableStateOf(false) }
    var distanceOnKm :Int? by remember { mutableStateOf(null) }
    var driverKm: Int? by remember { mutableStateOf(null) }

    LaunchedEffect(wrongKm) {
        if (wrongKm) {
            Toast.makeText(context, "Distance: $distanceOnKm  DriverKm: $driverKm", Toast.LENGTH_LONG).show()
            wrongKm = false
        }
    }


    TaxiScaffold { paddingValues ->

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier = modifier.height(16.dp))
            }

            item {
                GoogleMap(
                    modifier = modifier
                        .fillMaxWidth()
                        .heightIn(min = 370.dp, max = 370.dp)
                        .padding(paddingValues),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        scrollGesturesEnabled = true,
                        zoomControlsEnabled = true,
                        zoomGesturesEnabled = true,
                        mapToolbarEnabled = true,
                        rotationGesturesEnabled = true,
                        scrollGesturesEnabledDuringRotateOrZoom = true,
                        compassEnabled = true
                    )

                ) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                result.origin.latitude,
                                result.origin.longitude
                            )
                        ),
                        title = uiState.origin,
                    )
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                result.destination.latitude,
                                result.destination.longitude
                            )
                        ),
                        title = uiState.destination
                    )
                }
            }

            item {
                ConfirmRideContent(
                    rideEstimate = result,
                    uiState = uiState
                )
            }

            item {

                Box(
                    modifier = modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.drivers_available_label),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(result.options) { rideOption ->
                RideOptionItem(
                    rideOption = rideOption,
                    onRideOptionSelected = {
                        val minKm = driverMinKmMap[rideOption.id] ?: 0
                        val distanceKm = uiState.rideEstimate?.let { it.distance / 1000 }
                        if (distanceKm?.let { it < minKm } == true) {
                            distanceOnKm = distanceKm.toInt()
                            driverKm = minKm
                            wrongKm = true
                        } else {

                            if (uiState.rideEstimate != null) {
                                viewModel.confirmRide(rideOption)
                                onRideConfirmed(rideOption)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ConfirmRideContent(
    modifier: Modifier = Modifier,
    rideEstimate: RideEstimate,
    uiState: RideConfirmState,
){
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
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = uiState.origin, textAlign = TextAlign.Justify, softWrap = true)
            }

            Text(
                text = "${stringResource(R.string.destination_location_label)}:",
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = uiState.destination, textAlign = TextAlign.Justify, softWrap = true)
            }

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

@Composable
private fun RideOptionItem(
    modifier: Modifier = Modifier,
    rideOption: RideOption,
    onRideOptionSelected: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.driver_label), fontWeight = FontWeight.Bold)
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = rideOption.name, fontWeight = FontWeight.Bold)
            }
            Text(text = stringResource(R.string.description_label), fontWeight = FontWeight.Bold)
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ){
                Text(text = rideOption.description, textAlign = TextAlign.Justify, softWrap = true)
            }
            Text(text = stringResource(R.string.vehicle_label), fontWeight = FontWeight.Bold)
            Text(text = rideOption.vehicle)

            if (rideOption.review.comment.isNotBlank()) {

                Text(
                    text = stringResource(R.string.comment_label), fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ){
                Text(
                    text = rideOption.review.comment,
                    textAlign = TextAlign.Justify,
                    softWrap = true
                )
            }
            }

            Row {
                Text(
                    text = "${stringResource(R.string.rating_label)}: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = rideOption.review.rating.toString())
            }

            Row {
                Text(
                    text = "${stringResource(R.string.value_label)}: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "$${rideOption.value}")
            }
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { onRideOptionSelected() }
                ) {
                    Text(stringResource(R.string.select_driver_button_label))
                }
            }

        }
    }
}














