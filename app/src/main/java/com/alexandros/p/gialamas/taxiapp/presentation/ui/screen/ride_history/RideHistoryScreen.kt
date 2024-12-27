package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.AutoCompleteTextField
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiAppScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.components.DisplayHistoryErrorText
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.components.DriverSelector
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.components.RideHistoryButton
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.components.RideHistoryRideItem
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.state.Rides
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.viewmodel.RideHistoryViewModel
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Customer
import java.util.UUID

@Composable
fun RideHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: RideHistoryViewModel = hiltViewModel<RideHistoryViewModel>(),
    onBackPress: () -> Unit
) {

    BackHandler {
        onBackPress()
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current


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
                alignment = Alignment.TopCenter
            )

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    Image(
                        modifier = modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        painter = painterResource(R.drawable.search_history),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                }

                item {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Column(
                            modifier = modifier
                                .fillMaxWidth(0.9f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AutoCompleteTextField(
                                options = Customer.entries.toTypedArray(),
                                keyboardController = keyboardController,
                                onOptionSelected = {
                                    viewModel.updateCustomerId(it.customerId)
                                    viewModel.updateIsCustomerIdValid(isCustomerIdValid = true)
                                },
                                optionToString = { it.customerId },
                                onValueChange = {
                                    viewModel.updateCustomerId(it)
                                    viewModel.updateIsCustomerIdValid(isCustomerIdValid = true)
                                },
                                onClearClicked = {
                                    viewModel.updateCustomerId(it)
                                    viewModel.updateIsCustomerIdValid(isCustomerIdValid = false)
                                },
                                label = stringResource(R.string.customer_id_label),
                                text = uiState.customerId,
                                isValid = uiState.isCustomerIdValid
                            )

                            Spacer(modifier = modifier.height(8.dp))

                            DriverSelector(
                                keyboardController = keyboardController,
                                driverName = uiState.driverName,
                                isDriverMenuExpanded = uiState.isDriverMenuExpanded,
                                onAction = { viewModel.handleAction(it) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = modifier.height(6.dp)) }


                item {
                    RideHistoryButton(
                        isNetworkLoading = uiState.isNetworkLoading,
                        isRideHistoryCallReady = uiState.isRideHistoryCallReady,
                        keyboardController = keyboardController,
                        onAction = { viewModel.handleAction(it) }
                    )
                }


                item {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLocalLoading || uiState.isNetworkLoading) {
                            CircularProgressIndicator(color = if (uiState.isLocalLoading) Color.Gray else Color.LightGray)
                        }
                        DisplayHistoryErrorText(
                            localErrorUiTextMessage = uiState.localError,
                            networkErrorUiTextMessage = uiState.networkError,
                            context = context,
                            onAction = { viewModel.handleAction(it) }
                        )
                    }
                }

                item { Spacer(modifier = modifier.height(8.dp)) }

                items(
                    items = uiState.rides,
                    key = {
                        when (it) {
                            is Rides.Local -> it.rideEntity.id
                            is Rides.Network -> UUID.randomUUID().toString()
                        }
                    }
                ) { ride: Rides -> RideHistoryRideItem(ride) }
            }
        }
    }
}










