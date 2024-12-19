package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistory
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.AutoCompleteTextField
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.Driver
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.extractDate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.extractTime
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.formatDurationTimeToString
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.formatHistoryDistance
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.formatValue
import kotlinx.coroutines.delay

@Composable
fun RideHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: RideHistoryViewModel = hiltViewModel<RideHistoryViewModel>(),
    rideOption: RideOption
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val localRidesCollection = viewModel.localRides.collectAsStateWithLifecycle().value
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    var displayError by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null){
            displayError = true
            delay(5000L)
            displayError = false
        }
    }


    TaxiScaffold { paddingValues ->

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = modifier.height(16.dp))
            }

            item {
                Image(
                    modifier = modifier
                        .fillMaxWidth(),
                    painter = painterResource(R.drawable.search_history),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }

            item {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Card(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AutoCompleteTextField(
                                options = listOf("CT01"),
                                keyboardController = keyboardController,
                                onOptionSelected = {
                                    viewModel.updateCustomerId(it)
                                    viewModel.updateIsCustomerIdValid(true)
                                                   },
                                onValueChange = {
                                    viewModel.updateCustomerId(it)
                                    viewModel.updateIsCustomerIdValid(true)
                                                },
                                onClearClicked = {
                                    viewModel.updateCustomerId(it)
                                    viewModel.updateIsCustomerIdValid(false)
                                },
                                label = stringResource(R.string.customer_id_label),
                                isValid = uiState.isCustomerIdValid
                            )

                            DriverSelector(
                                uiState = uiState,
                                onExpandedChange = { viewModel.toggleDriverMenu(it) },
                                onDismiss = { viewModel.toggleDriverMenu(it) },
                                onDriverSelected = { driver ->
                                    viewModel.updateDriver(
                                        driverId = driver.driverId,
                                        driverName = driver.driverName
                                    )
                                }
                            )
                        }
                    }
                }
            }



            item {
                Button(
                    onClick = {
                        viewModel.getRideHistory(
                            customerId = uiState.customerId,
                            driverId = uiState.driverId
                        )
                    }
                ) {
                    Text("Get Ride History", fontSize = 16.sp)
                }
            }

            if (displayError){
                item {
                    DisplayErrorText(uiState = uiState, context = context)
                }
            }

            if (uiState.isLocalLoading || uiState.isNetworkLoading) {
                item {
                    CircularProgressIndicator(color = if (uiState.isLocalLoading) Color.Red else Color.Unspecified)
                }
            }

//            uiState.localRides?.let { data ->
//                when (data) {
//                    is Result.Error -> {
//
//                    }
//
//                    is Result.Success -> {
//                        items(data.data) { ride ->
//                            LocalRideItem(ride)
//                        }
//                    }
//
//                    else -> {}
//                }
//            }

            when (localRidesCollection) {
                is Result.Error -> {

                }

                is Result.Success -> {
                    items(localRidesCollection.data) { ride ->
                        LocalRideItem(ride)
                    }
                }

                else -> {}
            }

            uiState.rideHistory?.let { result ->
                when (result) {
                    is Result.Error -> {

                    }

                    is Result.Success -> {
                        items(result.data) { rideHistory ->
                            SelectionContainer {
                                HistoryRideItem(rideHistory)
                            }
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
) {

    ExposedDropdownMenuBox(
        expanded = uiState.isDriverMenuExpanded,
        onExpandedChange = {
            onExpandedChange(it)
        },
    ) {

        TextField(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable,
                ),
            readOnly = true,
            value = uiState.driverName,
            onValueChange = {},
            textStyle = TextStyle(
                textAlign = TextAlign.Center
            ),
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
                    modifier = modifier
                        .fillMaxWidth(),
                    onClick = {
                        onDriverSelected(driver)
                        onDismiss(false)
                    },
                    text = {
                        Text(
                            modifier = modifier.fillMaxWidth(),
                            text = driver.driverName,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun LocalRideItem(ride: Ride) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Ride ID: ${ride.id}", fontWeight = FontWeight.Bold)
            Text(text = "Customer ID: ${ride.customerId}")
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

@Composable
private fun HistoryRideItem(ride: RideHistory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Ride ID: ${ride.id}", fontWeight = FontWeight.Bold)
            }

            Row {
                Text(text = "Date: ", fontWeight = FontWeight.SemiBold)
                ride.date?.let {
                    Text(text = extractDate(ride.date))
                }
            }

            Row {
                Text(text = "Time: ", fontWeight = FontWeight.SemiBold)
                ride.date?.let {
                    Text(text = extractTime(ride.date))
                }
            }

            Row {
                Text(text = "Driver: ", fontWeight = FontWeight.SemiBold)
                Text(text = ride.driver.name)
            }

            Text(text = "Origin", fontWeight = FontWeight.SemiBold)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = ride.origin, textAlign = TextAlign.Justify, softWrap = true)
            }

            Text(text = "Destination", fontWeight = FontWeight.SemiBold)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = ride.destination, textAlign = TextAlign.Justify, softWrap = true)
            }

            Row {
                Text(text = "Distance: ", fontWeight = FontWeight.SemiBold)
                Text(text = formatHistoryDistance(ride.distance))
            }

            Row {
                Text(text = "Duration: ", fontWeight = FontWeight.SemiBold)
                Text(text = formatDurationTimeToString(ride.duration))
            }

            Row {
                Text(text = "Value: ", fontWeight = FontWeight.SemiBold)
                Text(text = "$${formatValue(ride.value)}")
            }
        }
    }
}

@Composable
private fun DisplayErrorText(
    uiState: RideHistoryState,
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
            fontWeight = FontWeight.SemiBold,
        )
    }
}

