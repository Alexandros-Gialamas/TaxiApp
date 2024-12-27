package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.AutoCompleteTextField
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiAppScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.action.RideEstimateAction
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.components.DisplayEstimateErrorText
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.components.RideEstimateButton
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.viewmodel.RideEstimateViewModel
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Customer
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Destination
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Origin

@Composable
fun RideEstimateScreen(
    modifier: Modifier = Modifier,
    viewModel: RideEstimateViewModel = hiltViewModel<RideEstimateViewModel>(),
    onRideSelected: (result: RideEstimate, customerId: String, origin: String, destination: String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val imagePresentation = painterResource(R.drawable.search_ride)


    val backgroundImage = if (uiState.isLoading) {
        painterResource(R.drawable.taking_a_ride)
    } else {
        painterResource(R.drawable.splash_screen)
    }



    TaxiAppScaffold(
        modifier = modifier
            .fillMaxSize()
    ) { paddingValues ->

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
                painter = backgroundImage,
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
                    Image(
                        modifier = modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        painter = imagePresentation,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                }

                item { Spacer(modifier = modifier.height(8.dp)) }


                item {
                    Column(
                        modifier = modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when {
                            uiState.isLoading -> {


                                Box(
                                    modifier = modifier
                                        .fillMaxSize()
                                        .align(Alignment.CenterHorizontally),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Column(
                                        modifier = modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {


                                        Spacer(modifier = modifier.height(16.dp))

                                        Text(
                                            modifier = modifier
                                                .fillMaxWidth(),
                                            text = ("Waiting the response..."),
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            textAlign = TextAlign.Center

                                        )
                                        Spacer(modifier = modifier.height(16.dp))

                                        CircularProgressIndicator(
                                            modifier = modifier
                                                .size(60.dp)
                                                .padding(vertical = 16.dp),
                                            color = Color.LightGray
                                        )

                                        Spacer(modifier = modifier.height(16.dp))
                                    }
                                }
                            }

                            !uiState.isLoading -> {
                                AutoCompleteTextField(
                                    options = Customer.entries.toTypedArray(),
                                    keyboardController = keyboardController,
                                    onOptionSelected = { selectedCustomer ->
                                        viewModel.updateCustomerId(selectedCustomer.customerId)
                                        viewModel.updateIsCustomerIdValid(isCustomerIdValid = true)
                                    },
                                    optionToString = { it.customerId },
                                    onValueChange = { newValue ->
                                        viewModel.updateCustomerId(newValue)
                                        viewModel.updateIsCustomerIdValid(isCustomerIdValid = true)
                                    },
                                    onClearClicked = { newValue ->
                                        viewModel.updateCustomerId(newValue)
                                        viewModel.updateIsCustomerIdValid(isCustomerIdValid = false)
                                    },
                                    label = stringResource(R.string.customer_id_label),
                                    text = uiState.customerId,
                                    isValid = uiState.isCustomerIdValid
                                )

                                AutoCompleteTextField(
                                    options = Origin.entries.toTypedArray(),
                                    keyboardController = keyboardController,
                                    onOptionSelected = { selectedOrigin ->
                                        viewModel.updateOrigin(selectedOrigin.origin)
                                        viewModel.updateIsOriginValid(isOriginValid = true)
                                    },
                                    optionToString = { it.origin },
                                    onValueChange = { newValue ->
                                        viewModel.updateOrigin(newValue)
                                        viewModel.updateIsOriginValid(isOriginValid = true)
                                    },
                                    onClearClicked = { newValue ->
                                        viewModel.updateOrigin(newValue)
                                        viewModel.updateIsOriginValid(isOriginValid = false)
                                    },
                                    label = stringResource(R.string.origin_label),
                                    text = uiState.origin,
                                    isValid = uiState.isOriginValid
                                )

                                AutoCompleteTextField(
                                    options = Destination.entries.toTypedArray(),
                                    keyboardController = keyboardController,
                                    onOptionSelected = { selectedDestination ->
                                        viewModel.updateDestination(selectedDestination.destination)
                                        viewModel.updateIsDestinationValid(isDestinationValid = true)
                                    },
                                    optionToString = { it.destination },
                                    onValueChange = { newValue ->
                                        viewModel.updateDestination(newValue)
                                        viewModel.updateIsDestinationValid(isDestinationValid = true)
                                    },
                                    onClearClicked = { newValue ->
                                        viewModel.updateDestination(newValue)
                                        viewModel.updateIsDestinationValid(isDestinationValid = false)
                                    },
                                    label = stringResource(R.string.destination_label),
                                    text = uiState.destination,
                                    isValid = uiState.isDestinationValid
                                )
                            }
                        }
                    }
                }


                item { Spacer(modifier = modifier.height(8.dp)) }

                item {
                    RideEstimateButton(
                        isStateLoading = uiState.isLoading,
                        isRideEstimateCallReady = uiState.isRideEstimateCallReady,
                        keyboardController = keyboardController,
                        onAction = {
                            viewModel.handleAction(it)
                        }
                    )
                }

//                if (uiState.error != null) {
                    item {
                        DisplayEstimateErrorText(
                            errorUiTextMessage = uiState.error,
                            context = context,
                            onAction = { viewModel.handleAction(it) }
                        )
                    }
//                }

                uiState.rideEstimate.let { result ->

                    when (result) {
                        is Result.Error -> {
                            if (uiState.error != null) {
                                item {
                                    DisplayEstimateErrorText(
                                        errorUiTextMessage = uiState.error,
                                        context = context,
                                        onAction = { viewModel.handleAction(it) }
                                    )
                                }
                            }
                        }

                        is Result.Success -> {
                            onRideSelected(
                                result.data,
                                uiState.customerId,
                                uiState.origin,
                                uiState.destination
                            )
                            RideEstimateAction.LoadingStateToFalse
                        }

                        Result.Idle -> Result.Idle
                    }
                }
            }
        }
    }
}








