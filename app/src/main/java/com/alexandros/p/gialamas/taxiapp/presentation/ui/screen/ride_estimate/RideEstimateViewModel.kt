package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ConfirmRideUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetRideEstimateUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideEstimateViewModel @Inject constructor(
    private val getRideEstimateUseCase: GetRideEstimateUseCase,
    private val confirmRideUseCase: ConfirmRideUseCase
) : ViewModel() {


    private val _uiState = MutableStateFlow<RideEstimateState>(RideEstimateState())
    val uiState: StateFlow<RideEstimateState> = _uiState
        .onStart {

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RideEstimateState()
        )

    private val errorChannel = Channel<UiText>()
    val errors = errorChannel.receiveAsFlow()

    fun updateCustomerId(customerId: String) {
        _uiState.update { it.copy(customerId = customerId) }
    }

    fun updateOrigin(origin: String) {
        _uiState.update { it.copy(origin = origin) }
    }

    fun updateDestination(destination: String) {
        _uiState.update { it.copy(destination = destination) }
    }


    fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ) {
        viewModelScope.launch() {
            Log.d("RideEstimateViewModel", "getRideEstimate triggered")
            _uiState.update { it.copy(isLoading = true) }
            try {
                val rideEstimate = getRideEstimateUseCase(
                    customerId = customerId,
                    origin = origin,
                    destination = destination
                )
                Log.d("RideEstimateViewModel", "Ride estimate received: $rideEstimate")
                _uiState.update {
                    it.copy(
                        rideEstimate = Result.Success(data = rideEstimate),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("RideEstimateViewModel", "Error getting ride estimate", e)
                _uiState.update {
                    it.copy(
                        rideEstimate = Result.Error(RideEstimateError.Network.NETWORK_ERROR),
                        isLoading = false
                    )
                }
            }
        }
    }


    fun confirmRide(ride: Ride) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val isSuccess = confirmRideUseCase(ride = ride)
                _uiState.update {
                    it.copy(
                        rideEstimate = it.rideEstimate,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        rideEstimate = Result.Error(RideEstimateError.Ride.RIDE_FAILED_TO_CONFIRM),
                        isLoading = false
                    )
                }
            }
        }
    }


}