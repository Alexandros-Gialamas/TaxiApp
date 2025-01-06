package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiAppScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.action.RideConfirmAction
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.components.ConfirmRideContent
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.components.RideConfirmOptionItem
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.viewmodel.RideConfirmViewModel
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
    onRestart: () -> Unit,
    onBackPress: () -> Unit
) {
    BackHandler {
        onBackPress()
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(
                result.origin.latitude,
                result.origin.longitude
            ), 11f
        )
    }

    LaunchedEffect(Unit) {
        viewModel.handleAction(
            RideConfirmAction.LoadData(
                rideEstimate = result,
                customerId = customerId,
                origin = origin,
                destination = destination
            )
        )
    }

    LaunchedEffect(uiState.isRideConfirmed, uiState.restart) {
        if (uiState.isRideConfirmed) onRideConfirmed()
        if (uiState.restart) onRestart()
    }

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
                .background(Color.DarkGray),
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
            val animatedProgress by animateFloatAsState(
                targetValue = uiState.confirmRideProgress,
                animationSpec = tween(durationMillis = 7000),
                label = "Ride confirm progress bar"
            )

            if (uiState.isLoading) {

                Column(
                    modifier = modifier
                        .fillMaxSize(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = modifier.padding(16.dp),
                        text = "Confirming Ride...",
                        color = Color.LightGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                    )

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = modifier
                            .padding(16.dp)
                            .fillMaxWidth(0.8f)
                            .height(16.dp),
                        color = Color.LightGray,
                        trackColor = Color.Gray,

                    )
                }
            } else {

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

                    item { Spacer(modifier = modifier.height(16.dp)) }

                    item {
                        ConfirmRideContent(
                            rideEstimate = result,
                            uiState = uiState
                        )
                    }


                    item {

                        Spacer(modifier = modifier.height(8.dp))

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
                        Spacer(modifier = modifier.height(16.dp))
                    }

                    items(result.options, key = { it.id }) { rideOption ->
                        RideConfirmOptionItem(
                            rideOption = rideOption,
                            isConfirmRideCallReady = uiState.isConfirmRideCallReady,
                            onAction = {
                                viewModel.handleAction(it)
                            }
                        )
                    }
                }
            }
        }
    }
}




















