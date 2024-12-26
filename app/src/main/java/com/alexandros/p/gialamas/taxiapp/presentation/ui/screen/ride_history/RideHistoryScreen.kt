package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Shapes
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
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
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistory
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.AutoCompleteTextField
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiAppScaffold
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.extractDate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.extractTime
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDurationTimeToString
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatHistoryDistance
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatValue
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Customer
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Driver
import kotlinx.coroutines.delay

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

    var displayError by remember { mutableStateOf(false) }
    var debounce by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            displayError = true
            delay(5000L)
            displayError = false
        }
    }

    LaunchedEffect(debounce) {
        if (debounce) {
            viewModel.cancelApiRequest()
            debounce = false
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

//                        Card(
//                            modifier = modifier
//                                .clip(RoundedCornerShape(16.dp))
//                                .background(
//                                    color = Color.DarkGray,
//                                    shape = RoundedCornerShape(16.dp)
//                                )
//                                .fillMaxWidth(0.9f)
//                                .padding(horizontal = 8.dp, vertical = 24.dp)
//                        ) {
                        Column(
                            modifier = modifier
//                                    .background(Color.DarkGray)
                                .fillMaxWidth(0.9f),
//                                    .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AutoCompleteTextField(
                                options = Customer.entries.toTypedArray(),
                                keyboardController = keyboardController,
                                onOptionSelected = {
                                    viewModel.updateCustomerId(it.customerId)
                                    viewModel.updateIsCustomerIdValid(true)
                                },
                                optionToString = { it.customerId },
                                onValueChange = {
                                    viewModel.updateCustomerId(it)
                                    viewModel.updateIsCustomerIdValid(true)
                                },
                                onClearClicked = {
                                    viewModel.updateCustomerId(it)
                                    viewModel.updateIsCustomerIdValid(false)
                                },
                                label = stringResource(R.string.customer_id_label),
                                text = uiState.customerId,
                                isValid = uiState.isCustomerIdValid
                            )

                            DriverSelector(
                                uiState = uiState,
                                keyboardController = keyboardController,
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
//                        }
                    }
                }

                item { Spacer(modifier = modifier.height(8.dp)) }


                item {
                    RideHistoryButton(
                        uiState = uiState,
                        keyboardController = keyboardController,
                        cancelRequest = {
                            debounce = true
                        },
                        confirmRequest = {
                            viewModel.getRideHistory(
                                customerId = uiState.customerId,
                                driverId = uiState.driverId,
                                context = context
                            )
                            viewModel.getLocalRidesHistory(
                                customerId = uiState.customerId,
                                driverId = uiState.driverId
                            )
                        }
                    )
                }

                item { Spacer(modifier = modifier.height(8.dp)) }


                if (displayError) {
                    item {
                        DisplayErrorText(uiState = uiState, context = context)
                    }
                }

                item { Spacer(modifier = modifier.height(8.dp)) }

                if (uiState.isLocalLoading || uiState.isNetworkLoading) {
                    item {
                        CircularProgressIndicator(color = if (uiState.isLocalLoading) Color.Red else Color.White)
                    }
                }

                item { Spacer(modifier = modifier.height(8.dp)) }


                uiState.localRides?.let { data ->
                    when (data) {
                        is Result.Error -> {

                        }

                        is Result.Success -> {
                            items(data.data) { ride ->
                                LocalRideItem(ride)
                            }
                        }

                        else -> {}
                    }
                }

//            when (localRidesCollection) {
//                is Result.Error -> {
//
//                }
//
//                is Result.Success -> {
//                    items(localRidesCollection.data) { ride ->
//                        LocalRideItem(ride)
//                    }
//                }
//
//                else -> {}
//            }


                uiState.rideHistory?.let { result ->
                    when (result) {
                        is Result.Error -> {
                            item {
                                DisplayErrorText(uiState = uiState, context = context)
                            }
                        }

                        is Result.Success -> {
                            items(result.data) { rideHistory ->
                                HistoryRideItem(rideHistory as RideHistory)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}


@Composable
private fun LocalRideItem(ride: RideEntity) {
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
            Text(text = "Driver: ${ride.driverName}")
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

    val textColor = Color.White

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth(0.9f)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.DarkGray)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Ride ID: ${ride.id}",
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Date: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                ride.date?.let {
                    Text(
                        modifier = Modifier
                            .padding(start = 6.dp),
                        text = extractDate(ride.date),
                        color = textColor
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Time: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                ride.date?.let {
                    Text(
                        modifier = Modifier
                            .padding(start = 6.dp),
                        text = extractTime(ride.date),
                        color = textColor
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Driver: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    modifier = Modifier
                        .padding(start = 6.dp),
                    text = ride.driver.name,
                    color = textColor
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Origin",
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = ride.origin,
                    textAlign = TextAlign.Justify,
                    softWrap = true,
                    color = textColor
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Destination",
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = ride.destination,
                    textAlign = TextAlign.Justify,
                    softWrap = true,
                    color = textColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Distance: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    modifier = Modifier
                        .padding(start = 6.dp),
                    text = formatHistoryDistance(ride.distance),
                    color = textColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Duration: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    modifier = Modifier
                        .padding(start = 6.dp),
                    text = formatDurationTimeToString(ride.duration),
                    color = textColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Value: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    modifier = Modifier
                        .padding(start = 6.dp),
                    text = "$${formatValue(ride.value)}",
                    color = textColor
                )
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
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun RideHistoryButton(
    modifier: Modifier = Modifier,
    keyboardController: SoftwareKeyboardController?,
    uiState: RideHistoryState,
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
                containerColor = Color.LightGray,
                contentColor = Color.DarkGray,
//                containerColor = if (uiState.isNetworkLoading) Color.LightGray else Color.DarkGray,
//                contentColor = if (uiState.isNetworkLoading) Color.Black else Color.White
            ),
            enabled = uiState.canRequestAgain,
            onClick = {
                keyboardController?.hide()
                when {
                    uiState.isNetworkLoading -> {
                        cancelRequest()
                    }

                    else -> {
                        confirmRequest()
                    }
                }
            }) {
            Text(
                modifier = modifier
                    .padding(8.dp),
                text =
                when {
                    uiState.isNetworkLoading -> {
                        stringResource(R.string.cancel_button_label)
                    }

                    else -> {
                        stringResource(R.string.ride_history_button_label)
                    }
                },
                fontSize = 22.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DriverSelector(
    modifier: Modifier = Modifier,
    uiState: RideHistoryState,
    keyboardController: SoftwareKeyboardController? = null,
    onExpandedChange: (Boolean) -> Unit,
    onDismiss: (Boolean) -> Unit,
    onDriverSelected: (Driver) -> Unit
) {

    ExposedDropdownMenuBox(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth(0.9f),
        expanded = uiState.isDriverMenuExpanded,
        onExpandedChange = {
            onExpandedChange(it)
        },
    ) {

        TextField(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .menuAnchor(
                    type = MenuAnchorType.PrimaryEditable,
                ),
            readOnly = true,
            value = uiState.driverName,
            onValueChange = {},
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            ),
            label = { Text("Select Driver") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isDriverMenuExpanded)
            }
        )
        MaterialTheme(
            shapes = Shapes(RoundedCornerShape(16.dp)),
            content = {
                ExposedDropdownMenu(
                    modifier = modifier
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(16.dp),
                            color = Color.LightGray
                        ),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Color.DarkGray,
                    expanded = uiState.isDriverMenuExpanded,
                    onDismissRequest = { onDismiss(false) }
                ) {
                    Driver.entries.forEach { driver ->
                        DropdownMenuItem(
                            modifier = modifier
                                .clip(RoundedCornerShape(16.dp))
                                .fillMaxWidth()
                                .drawBehind {
                                    val strokeWidth = 1.dp.toPx()
                                    val lineWidth = size.width * 0.7f
                                    drawLine(
                                        color = Color.LightGray,
                                        start = Offset(
                                            (size.width - lineWidth) / 2,
                                            size.height - strokeWidth / 2
                                        ),
                                        end = Offset(
                                            (size.width + lineWidth) / 2,
                                            size.height - strokeWidth / 2
                                        ),
                                        strokeWidth = strokeWidth
                                    )
                                },
                            onClick = {
                                onDriverSelected(driver)
                                onDismiss(false)
                                keyboardController?.hide()
                            },
                            text = {
                                Text(
                                    modifier = modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    text = driver.driverName,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                        )
                    }
                }
            }
        )
    }
}



