package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiScaffold

@Composable
fun RideEstimateScreen(
    modifier: Modifier = Modifier,
    viewModel: RideEstimateViewModel = hiltViewModel(),
    onRideSelected: () -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value


    TaxiScaffold {

        TextField(
            value = uiState.customerId,
            onValueChange = { viewModel.updateCustomerId(it) },
            label = { Text("Customer ID") }
        )

        Spacer(modifier = modifier.height(8.dp))

        TextField(
            value = uiState.origin,
            onValueChange = { viewModel.updateOrigin(it) },
            label = { Text("Origin") }
        )

        Spacer(modifier = modifier.height(8.dp))

        TextField(
            value = uiState.destination,
            onValueChange = { viewModel.updateDestination(it) },
            label = { Text("Destination") }
        )

        Spacer(modifier = modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.getRideEstimate(
                    customerId = uiState.customerId,
                    origin = uiState.origin,
                    destination = uiState.destination
                )
            }) {
            Text("Get Ride Estimate")
        }


        if (uiState.isLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.rideEstimate.let { result ->
            when (result) {
                is Result.Error -> {
                    Text("Error: ${result.error}")
                }

                is Result.Success -> {
                    RideEstimateContent(
                        rideEstimate = result.data,
                        onRideEstimateSelected = { selectedRideOption ->
                            viewModel.confirmRide(
                                Ride(
                                    origin = uiState.origin,
                                    destination = uiState.destination,
                                    distance = result.data.distance,
                                    duration = result.data.duration,
                                    driver = Driver(
                                        id = selectedRideOption.id,
                                        name = selectedRideOption.name
                                    ),
                                    value = selectedRideOption.value
                                )
                            )
                            onRideSelected()
                        }
                    )
                }

                else -> {}
            }
        }


    }

}

@Composable
fun RideEstimateContent(
    modifier: Modifier = Modifier,
    rideEstimate: RideEstimate,
    onRideEstimateSelected: (RideOption) -> Unit
) {
    LazyColumn (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Display origin and destination
        item { Text("Origin: ${rideEstimate.origin}")}
        item { Text("Destination: ${rideEstimate.destination}") }

        // Display distance and duration
        item { Text("Distance: ${rideEstimate.distance}") }
        item { Text("Duration: ${rideEstimate.duration}") }

        // Display ride options
        items(rideEstimate.options) { rideOption ->
            RideOptionItem(
                rideOption = rideOption,
                onRideOptionSelected = { onRideEstimateSelected(rideOption) }
            )
        }


    }
}

@Composable
fun RideOptionItem(
    modifier: Modifier = Modifier,
    rideOption: RideOption,
    onRideOptionSelected: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(text = "Driver: ${rideOption.name}", fontWeight = FontWeight.Bold)
            Text(text = "Description: ${rideOption.description}")
            Text(text = "Vehicle: ${rideOption.vehicle}")
            Text(text = "Rating: ${rideOption.review.rating}")
            Text(text = "Value: ${rideOption.value}")
            Button(
                onClick = { onRideOptionSelected() }
            ) {
                Text("Select")
            }
        }
    }
}