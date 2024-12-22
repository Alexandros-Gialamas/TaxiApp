package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate


import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Customer
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Destination
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Origin
import kotlinx.coroutines.delay

@Composable
fun RideEstimateScreen(
    modifier: Modifier = Modifier,
    viewModel: RideEstimateViewModel = hiltViewModel<RideEstimateViewModel>(),
    onRideSelected: (result: RideEstimate, customerId: String, origin: String, destination: String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val imagePresentation = if (uiState.isLoading) {
        painterResource(R.drawable.taking_a_ride)
    } else {
        painterResource(R.drawable.search_ride)
    }

    var displayError by remember { mutableStateOf(false) }
    var debounce by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            displayError = true
            delay(5000L)
            displayError = false
        }
    }

    LaunchedEffect (debounce){
        if (debounce) {
            viewModel.cancelApiRequest()
            debounce = false
        }
    }

    TaxiAppScaffold(
        modifier = modifier
            .fillMaxSize()
    ) { paddingValues ->

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


                item {
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
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when {
                                uiState.isLoading -> {

                                    Box(
                                        modifier = modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            modifier = modifier
                                                .fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {

                                            Text(
                                                modifier = modifier
                                                    .fillMaxWidth(),
                                                text = ("Waiting the response..."),
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                textAlign = TextAlign.Center

                                            )

                                            CircularProgressIndicator(color = Color.LightGray)

                                            RideEstimateButton(
                                                uiState = uiState,
                                                keyboardController = keyboardController,
                                                cancelRequest = { debounce = true },
                                                confirmRequest = { }
                                            )
                                        }
                                    }
                                }

                                !uiState.isLoading -> {
                                    Column(
                                        modifier = modifier
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AutoCompleteTextField(
                                            options = Customer.entries.toTypedArray(),
                                            keyboardController = keyboardController,
                                            onOptionSelected = { selectedCustomer ->
                                                viewModel.updateCustomerId(selectedCustomer.customerId)
                                                viewModel.updateIsCustomerIdValid(true)
                                            },
                                            optionToString = { it.customerId },
                                            onValueChange = { newValue ->
                                                viewModel.updateCustomerId(newValue)
                                                viewModel.updateIsCustomerIdValid(true)
                                            },
                                            onClearClicked = { newValue ->
                                                viewModel.updateCustomerId(newValue)
                                                viewModel.updateIsCustomerIdValid(false)
                                            },
                                            label = stringResource(R.string.customer_id_label),
                                            isValid = uiState.isCustomerIdValid
                                        )

                                        AutoCompleteTextField(
                                            options = Origin.entries.toTypedArray(),
                                            keyboardController = keyboardController,
                                            onOptionSelected = { selectedOrigin ->
                                                viewModel.updateOrigin(selectedOrigin.origin)
                                                viewModel.updateIsOriginValid(true)
                                            },
                                            optionToString = { it.origin },
                                            onValueChange = { newValue ->
                                                viewModel.updateOrigin(newValue)
                                                viewModel.updateIsOriginValid(true)
                                            },
                                            onClearClicked = { newValue ->
                                                viewModel.updateOrigin(newValue)
                                                viewModel.updateIsOriginValid(false)
                                            },
                                            label = stringResource(R.string.origin_label),
                                            isValid = uiState.isOriginValid
                                        )

                                        AutoCompleteTextField(
                                            options = Destination.entries.toTypedArray(),
                                            keyboardController = keyboardController,
                                            onOptionSelected = { selectedDestination ->
                                                viewModel.updateDestination(selectedDestination.destination)
                                                viewModel.updateIsDestinationValid(true)
                                            },
                                            optionToString = { it.destination },
                                            onValueChange = { newValue ->
                                                viewModel.updateDestination(newValue)
                                                viewModel.updateIsDestinationValid(true)
                                            },
                                            onClearClicked = { newValue ->
                                                viewModel.updateDestination(newValue)
                                                viewModel.updateIsDestinationValid(false)
                                            },
                                            label = stringResource(R.string.destination_label),
                                            isValid = uiState.isDestinationValid
                                        )
                                    }
                                }
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
                                    destination = uiState.destination,
                                    context = context
                                )
                            }
                        )
                    }
                }

                if (displayError) {
                    item {
                        DisplayErrorText(uiState = uiState, context = context)
                    }
                }

                uiState.rideEstimate.let { result ->

                    when (result) {
                        is Result.Success -> {
                            onRideSelected(
                                result.data,
                                uiState.customerId,
                                uiState.origin,
                                uiState.destination
                            )
                            viewModel.updateLoadingState(false)
                        }
                        else -> {}
                    }
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
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.isLoading) Color.LightGray else Color.DarkGray,
                contentColor = if (uiState.isLoading) Color.Black else Color.White
            ),
            enabled = uiState.canRequestAgain,
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
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun DisplayErrorText(
    uiState: RideEstimateState,
    context: Context
) {
    uiState.error?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = uiState.error.asString(context),
            softWrap = true,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )
    }
}




