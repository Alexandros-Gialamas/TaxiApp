package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetRideHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideHistoryViewModel @Inject constructor(
    private val getRideHistoryUseCase: GetRideHistoryUseCase
) : ViewModel(){

    private val _uiState = MutableStateFlow<RideHistoryState>(RideHistoryState())
    val uiState: StateFlow<RideHistoryState> = _uiState
        .onStart {

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RideHistoryState()
        )


    fun getRideHistory(customerId: String, driverId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getRideHistoryUseCase(customerId = customerId, driverId = driverId).collectLatest { rideHistory ->
                    _uiState.update {
                        it.copy(
                            rideHistory = Result.Success(data = rideHistory),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        rideHistory = Result.Error(RideHistoryError.Network.NETWORK_ERROR),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateCustomerId(customerId: String) {
        _uiState.update { it.copy(customerId = customerId) }
    }

    fun updateDriverId(driverId: Int) {
        _uiState.update { it.copy(driverId = driverId) }
    }

}