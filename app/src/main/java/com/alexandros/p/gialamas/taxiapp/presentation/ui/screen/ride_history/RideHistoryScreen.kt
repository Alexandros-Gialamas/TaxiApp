package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.presentation.ui.common.TaxiScaffold

@Composable
fun RideHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: RideHistoryViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    TaxiScaffold {

        TextField(
            value = uiState.customerId,
            onValueChange = { viewModel.updateCustomerId(it) },
            label = { Text("Customer ID") }
        )
        Spacer(modifier = modifier.height(8.dp))
        // TODO: Add a dropdown or selector for driverId

        Spacer(modifier = modifier.height(16.dp))

        Button(
            onClick = {
            viewModel.getRideHistory(
                customerId = uiState.customerId,
                driverId = uiState.driverId
            )
        }) {
            Text("Get Ride History")
        }

        if (uiState.isLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }

        uiState.rideHistory.let { result ->
            when(result) {
                is Result.Error -> {
                    Text("Error: ${result.error}")
                }
                is Result.Success -> {
                    RideHistoryContent(result.data)
                } else -> {}
            }
        }

//        when (val rideHistoryResult = uiState.rideHistory) {
//            is Result.Loading -> {
//                CircularProgressIndicator()
//            }
//
//            is Result.Error -> {
//                Text("Error: ${rideHistoryResult.error}")
//            }
//
//            is Result.Success -> {
//                RideHistoryContent(rideHistoryResult.data)
//            }
//        }
    }


}

@Composable
fun RideHistoryContent(rideHistory: List<Ride>) {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        items(rideHistory) { ride ->
            RideItem(ride)
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

