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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.AutoCompleteTextField
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiScaffold

@Composable
fun RideEstimateScreen(
    modifier: Modifier = Modifier,
    viewModel: RideEstimateViewModel = hiltViewModel<RideEstimateViewModel>(),
    onRideSelected: (result: RideEstimate, customerId: String, origin: String, destination: String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val keyboardController = LocalSoftwareKeyboardController.current


    TaxiScaffold (
        modifier = modifier
            .fillMaxSize()
    ){ paddingValues ->

        LazyColumn (
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
                Card(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.isLoading) {
                            Box(
                                modifier = modifier,
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = modifier,
                                    verticalArrangement = Arrangement.spacedBy(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Waiting for the response...")

                                    CircularProgressIndicator()

                                    RideEstimateButton(
                                        uiState = uiState,
                                        keyboardController = keyboardController,
                                        cancelRequest = { viewModel.cancelApiRequest() },
                                        confirmRequest = { }
                                    )
                                }
                            }
                        } else {
                            AutoCompleteTextField(
                                options = listOf("CT01"),
                                keyboardController = keyboardController,
                                onOptionSelected = { selectedCustomer ->
                                    viewModel.updateCustomerId(selectedCustomer)
                                },
                                label = stringResource(R.string.customer_id_label)
                            )

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

                            AutoCompleteTextField(
                                options = listOf("Av. Paulista, 1538 - Bela Vista, São Paulo - SP, 01310-200"),
                                keyboardController = keyboardController,
                                onOptionSelected = { selectedDestination ->
                                    viewModel.updateDestination(selectedDestination)
                                },
                                label = stringResource(R.string.destination_label)
                            )
                        }
                    }
                }
            }

            if (!uiState.isLoading) {
            item {
                RideEstimateButton(
                    uiState = uiState,
                    keyboardController = keyboardController,
                    cancelRequest = { },
                    confirmRequest = {
                        viewModel.getRideEstimate(
                            customerId = uiState.customerId,
                            origin = uiState.origin,
                            destination = uiState.destination
                        )
                    }
                )
            }
        }

            uiState.rideEstimate.let { result ->
                when (result) {
                    is Result.Error -> {
                       item {
                           Text("Error: ${result.error}")
                       }
                    }
                    is Result.Success -> {
                        onRideSelected(
                            result.data,
                            uiState.customerId,
                            uiState.origin,
                            uiState.destination
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun RideEstimateButton(
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController?,
    uiState: RideEstimateState,
    cancelRequest: () -> Unit,
    confirmRequest: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                keyboardController?.hide()
                when {
                    uiState.isLoading -> {
                        cancelRequest()
                    }

                    else -> {
                        confirmRequest()
                    }
                }
            }) {
            Text(
                text =
                when {
                    uiState.isLoading -> {
                        stringResource(R.string.cancel_button_label)
                    }

                    else -> {
                        stringResource(R.string.ride_estimate_button_label)
                    }
                },
                fontSize = 16.sp
            )
        }
        Spacer(modifier = modifier.height(16.dp))
    }
}





