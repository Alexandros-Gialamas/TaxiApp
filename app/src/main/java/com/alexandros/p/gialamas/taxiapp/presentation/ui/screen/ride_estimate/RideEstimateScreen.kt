package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.AutoCompleteTextField
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.formatDistance
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.formatDurationToReadableString

@Composable
fun RideEstimateScreen(
    modifier: Modifier = Modifier,
    viewModel: RideEstimateViewModel = hiltViewModel(),
    onRideSelected: (result: RideEstimate) -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val keyboardController = LocalSoftwareKeyboardController.current


    TaxiScaffold {

        Spacer(modifier = modifier.height(16.dp))

        if (!uiState.hideParameters) {

        AutoCompleteTextField(
            options = listOf("CT01"),
            keyboardController = keyboardController,
            onOptionSelected = { selectedCustomer ->
                viewModel.updateCustomerId(selectedCustomer)
            },
            label = stringResource(R.string.customer_id_label)
        )

        Spacer(modifier = modifier.height(8.dp))

        AutoCompleteTextField(
            options = listOf(
                "Av. Pres. Kenedy, 2385 - Remédios, Osasco - SP, 02675-031",
                "Av. Thomas Edison, 365 - Barra Funda, São Paulo - SP, 01140-000",
                "Av. Brasil, 2033 - Jardim America, São Paulo - SP, 01431-001"
            ),
            keyboardController = keyboardController,
            onOptionSelected = { selectedOrigin ->
                viewModel.updateOrigin(selectedOrigin)
            },
            label = stringResource(R.string.origin_label)
        )

        Spacer(modifier = modifier.height(8.dp))

        AutoCompleteTextField(
            options = listOf("Av. Paulista, 1538 - Bela Vista, São Paulo - SP, 01310-200"),
            keyboardController = keyboardController,
            onOptionSelected = { selectedDestination ->
                viewModel.updateDestination(selectedDestination)
            },
            label = stringResource(R.string.destination_label)
        )


        Spacer(modifier = modifier.height(16.dp))

    }
        Button(
            onClick = {
                keyboardController?.hide()
                when {
                    uiState.isLoading -> {
                        viewModel.cancelApiRequest()
                    }
                    uiState.hideParameters -> {
                        viewModel.updateHideParameters(false)
                    }
                    else -> {
                        viewModel.getRideEstimate(
                            customerId = uiState.customerId,
                            origin = uiState.origin,
                            destination = uiState.destination
                        )
                    }
                }
            }) {
            Text(
                text =
                when {
                    uiState.isLoading -> {
                        stringResource(R.string.cancel_button_label)
                    }
                    uiState.hideParameters -> {
                        stringResource(R.string.new_ride_button_label)
                    }
                    else -> {
                        stringResource(R.string.ride_estimate_button_label)
                    }
                },
                fontSize = 16.sp
            )
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
                        uiState = uiState,
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
                            onRideSelected(result.data)
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
    uiState: RideEstimateState,
    onRideEstimateSelected: (RideOption) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
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
                    Text(text = "${stringResource(R.string.origin_location_label)}:", fontWeight = FontWeight.Bold)
                    Box (
                        modifier = modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = uiState.origin, textAlign = TextAlign.Center, softWrap = true)
                    }

                    Row {
                        Text(text = "${stringResource(R.string.latitude_label)}: ", fontWeight = FontWeight.Bold)
                        Text(text = rideEstimate.origin.latitude.toString())
                    }
                    Row {
                        Text(text = "${stringResource(R.string.longitude_label)}: ", fontWeight = FontWeight.Bold)
                        Text(text = rideEstimate.origin.longitude.toString())
                    }
                }
            }
        }

        item {
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
                    Text(text = "${stringResource(R.string.destination_location_label)}:", fontWeight = FontWeight.Bold)
                    Box (
                        modifier = modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = uiState.destination, textAlign = TextAlign.Center, softWrap = true)
                    }
                    Row {
                        Text(text = "${stringResource(R.string.latitude_label)}: ", fontWeight = FontWeight.Bold)
                        Text(text = rideEstimate.destination.latitude.toString())
                    }
                    Row {
                        Text(text = "${stringResource(R.string.longitude_label)}: ", fontWeight = FontWeight.Bold)
                        Text(text = rideEstimate.destination.longitude.toString())
                    }
                }
            }
        }

        item {
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
                    val formattedDurationString = formatDurationToReadableString(rideEstimate.duration.toInt())
                    Row {
                        Text(text = "${stringResource(R.string.distance_label)}: ", fontWeight = FontWeight.Bold)
                        Text(text = formattedDistance)
                    }

                    Row {
                        Text(text = "${stringResource(R.string.duration_label)}: ", fontWeight = FontWeight.Bold)
                        Text(text = formattedDurationString)
                    }
                }
            }
        }

        item { Spacer(modifier = modifier.height(16.dp)) }

        item {
            Box(
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = stringResource(R.string.drivers_available_label),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

        }

        item { Spacer(modifier = modifier.height(16.dp)) }


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
            Text(text = rideOption.description)
            Text(text = stringResource(R.string.vehicle_label), fontWeight = FontWeight.Bold)
            Text(text = rideOption.vehicle)
            Row {
                Text(text = "${stringResource(R.string.rating_label)}: ", fontWeight = FontWeight.Bold)
                Text(text = rideOption.review.rating.toString())
            }

            Row {
                Text(text = "${stringResource(R.string.value_label)}: ", fontWeight = FontWeight.Bold)
                Text(text = rideOption.value.toString())
            }
            Box (
                modifier = modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Button(
                    onClick = { onRideOptionSelected() }
                ) {
                    Text(stringResource(R.string.select_driver_button_label))
                }
            }

        }
    }
}

