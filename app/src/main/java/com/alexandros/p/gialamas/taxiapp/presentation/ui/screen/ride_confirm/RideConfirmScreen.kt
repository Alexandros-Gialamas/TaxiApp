package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiAppScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDistance
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDurationToReadableString
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
    onRideConfirmed: () -> Unit,
    onBackPress: () -> Unit
) {
    BackHandler {
        onBackPress()
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.collectState(result, customerId, origin, destination)
    }

    LaunchedEffect(uiState.isRideConfirmed) {
        if (uiState.isRideConfirmed) onRideConfirmed()
    }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                result.origin.latitude,
                result.origin.longitude
            ), 11f
        )
    }



    val context = LocalContext.current


    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            Toast.makeText(
                context,
                uiState.error.asString(context),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    TaxiAppScaffold { paddingValues ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {

            Image(
                modifier = modifier
                    .fillMaxSize()
                    .alpha(0.1f),
                painter = painterResource(R.drawable.splash_screen),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                item {
                    GoogleMap(
                        modifier = modifier
                            .fillMaxWidth()
                            .heightIn(min = 350.dp, max = 350.dp),
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
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 26.sp
                        )
                    }
                }

                items(result.options) { rideOption ->
                    RideOptionItem(
                        rideOption = rideOption,
                        onRideOptionSelected = {
//                            val minKm = driverMinKmMap[rideOption.id] ?: 0
//                            val distanceKm = uiState.rideEstimate?.let { it.distance / 1000 }
//                            if (distanceKm?.let { it < minKm } == true) {
//                                distanceOnKm = distanceKm.toInt()
//                                driverKm = minKm
//                                wrongKm = true
                            viewModel.collectRideOption(it)
                            viewModel.confirmRide(context)



//                            }
//                        else {
//
//                                if (uiState.rideEstimate != null) {
//                                    viewModel.confirmRide(rideOption)
//                                    onRideConfirmed(rideOption)
//                                }
//                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmRideContent(
    modifier: Modifier = Modifier,
    rideEstimate: RideEstimate,
    uiState: RideConfirmState,
) {

    val textColor = Color.White

    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.DarkGray, shape = RoundedCornerShape(16.dp))
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
                .background(color = Color.DarkGray, shape = RoundedCornerShape(16.dp))
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
                .background(color = Color.DarkGray, shape = RoundedCornerShape(16.dp))
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
                        text = formattedDistance,
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


@Composable
private fun RideOptionItem(
    modifier: Modifier = Modifier,
    rideOption: RideOption,
    onRideOptionSelected: (RideOption) -> Unit,
) {

    val textColor = Color.White

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.DarkGray, shape = RoundedCornerShape(16.dp))
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

            Row (
                modifier = modifier
                    .fillMaxWidth()
            ){
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

            Row (
                modifier = modifier
                    .fillMaxWidth()
            ){
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
                    onClick = { onRideOptionSelected(rideOption) }
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














