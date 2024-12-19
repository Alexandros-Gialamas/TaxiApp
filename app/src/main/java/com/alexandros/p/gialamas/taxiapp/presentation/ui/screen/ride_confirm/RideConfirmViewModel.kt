package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.mapper.toRide
import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ConfirmRideUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.SaveRideUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RideConfirmViewModel @Inject constructor(
    private val confirmRideUseCase: ConfirmRideUseCase,
    private val saveRideUseCase: SaveRideUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RideConfirmState>(RideConfirmState())
    val uiState: StateFlow<RideConfirmState> = _uiState
        .onStart {

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RideConfirmState()
        )

    fun collectState(
        customerId: String,
        origin: String,
        destination: String,
        rideEstimate: RideEstimate
    ) {
        _uiState.update {
            it.copy(
                rideEstimate = rideEstimate,
                customerId = customerId,
                origin = origin,
                destination = destination
            )
        }
    }

    fun confirmRide(rideOption: RideOption) {
        Log.d("RideConfirmViewModel", "confirmRide called with ride: $rideOption")
        viewModelScope.launch(Dispatchers.IO) {

            if (_uiState.value.rideEstimate != null) {

                val ride = ConfirmRideRequest(
                    customerId = _uiState.value.customerId,
                    origin = _uiState.value.origin,
                    destination = _uiState.value.destination,
                    distance = _uiState.value.rideEstimate!!.distance,
                    duration = _uiState.value.rideEstimate!!.duration,
                    driver = Driver(
                        id = rideOption.id,
                        name = rideOption.name
                    ),
                    value = rideOption.value
                )

                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = true) }
                }
                try {
                    val isSuccess = confirmRideUseCase(ride)
                    Log.d("RideConfirmViewModel", "confirmRide API call success: $isSuccess")

                    if (isSuccess) {
                        val currentDate =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                                Date()
                            )
                        saveRideUseCase(
                            ride.toRide(
                                customerId = _uiState.value.customerId,
                                date = currentDate,
                                driver = Driver(
                                    id = rideOption.id,
                                    name = rideOption.name
                                )
                            )
                        )
                    } else {
                        Unit
                    }
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                rideEstimate = it.rideEstimate,
                                isLoading = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}