package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.AutoCompleteTextField
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.Driver

@Composable
fun RideHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: RideHistoryViewModel = hiltViewModel<RideHistoryViewModel>(),
    rideOption: RideOption
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val keyboardController = LocalSoftwareKeyboardController.current

    TaxiScaffold { paddingValues ->

        LazyColumn (
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            item {
                AutoCompleteTextField(
                    options = listOf("CT01"),
                    keyboardController = keyboardController,
                    onOptionSelected = { viewModel.updateCustomerId(it) },
                    label = stringResource(R.string.customer_id_label)
                )
            }

            item {
                DriverSelector(
                    uiState = uiState,
                    onExpandedChange = { viewModel.toggleDriverMenu(it) },
                    onDismiss = { viewModel.toggleDriverMenu(it) },
                    onDriverSelected = {
                        viewModel.updateDriver(driverId = it.driverId, driverName = it.driverName)
                    }
                )
            }

            item {
                Button(
                    onClick = {
//                        uiState.driverId?.let {
                            viewModel.getRideHistory(
                                customerId = uiState.customerId,
                                driverId = uiState.driverId
                            )
//                        }
                    }) {
                    Text("Get Ride History", fontSize = 16.sp)
                }
            }

            if (uiState.isLoading) {
                item {
                    CircularProgressIndicator()
                }
            }

            uiState.localRides?.let { data ->
                when (data){
                    is Result.Error -> {

                    }
                    is Result.Success -> {
                        items(data.data) { ride ->
                            RideItem(ride = ride)
                        }
                    }
                    else -> {}
                }
            }

            uiState.rideHistory?.let { result ->
                when (result){
                    is Result.Error -> {

                    }
                    is Result.Success -> {
                        items(result.data) { ride ->
                            RideItem(ride = ride)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriverSelector(
    modifier: Modifier = Modifier,
    uiState: RideHistoryState,
    onExpandedChange: (Boolean) -> Unit,
    onDismiss: (Boolean) -> Unit,
    onDriverSelected: (Driver) -> Unit
){

    ExposedDropdownMenuBox(
        expanded = uiState.isDriverMenuExpanded,
        onExpandedChange = {
            if (uiState.isDriverMenuExpanded) {
                onExpandedChange(false)
            } else {
                onExpandedChange(true)
            }
         },
    ) {

        OutlinedTextField(
            modifier = modifier
//                .menuAnchor(
//                type = MenuAnchorType.PrimaryNotEditable,
//                enabled = uiState.isDriverMenuExpanded,
//            )
                .clickable {
//                    if (uiState.isDriverMenuExpanded) {
                        onExpandedChange(!uiState.isDriverMenuExpanded)
//                    } else {
//                        onExpandedChange(true)
//                    }
                },
            readOnly = true,
            value = uiState.driverName,
            onValueChange = {},
            label = { Text("Select Driver") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isDriverMenuExpanded)
            }
        )
        ExposedDropdownMenu(
            expanded = uiState.isDriverMenuExpanded,
            onDismissRequest = { onDismiss(false) }
        ) {
            Driver.entries.forEach { driver ->
                DropdownMenuItem(
                    onClick = {
                        onDriverSelected(driver)
                        onDismiss(false)
                    },
                    text = { driver.driverName }
                )
            }
        }
    }
}



@Composable
fun RideItem(ride: Ride) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Ride ID: ${ride.id}", fontWeight = FontWeight.Bold)
            Text(text = "Date: ${ride.date}")
            Text(text = "Driver: ${ride.driver.name}")
            Text(text = "Origin: ${ride.origin}")
            Text(text = "Destination: ${ride.destination}")
            Text(text = "Distance: ${ride.distance}")
            Text(text = "Duration: ${ride.duration}")
            Text(text = "Value: ${ride.value}")
        }
    }
}

