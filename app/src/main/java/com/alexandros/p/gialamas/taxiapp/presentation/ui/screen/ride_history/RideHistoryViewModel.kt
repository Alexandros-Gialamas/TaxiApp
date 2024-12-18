package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.model.RideHistoryRequest
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetLocalRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetRideHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RideHistoryViewModel @Inject constructor(
    private val getRideHistoryUseCase: GetRideHistoryUseCase,
    private val getLocalRideHistoryUseCase: GetLocalRideHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RideHistoryState>(RideHistoryState())
    val uiState: StateFlow<RideHistoryState> = _uiState
        .onStart {

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RideHistoryState()
        )


    fun getRideHistory(customerId: String, driverId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            getLocalRideHistoryUseCase(customerId, driverId).collectLatest { localRides ->
                withContext(Dispatchers.Main){
                    _uiState.update {
                        it.copy(
                            localRides = Result.Success(data = localRides),
                            isLoading = true
                        )
                    }
                }
                try {
                    val networkRides = getRideHistoryUseCase(customerId, driverId)
                    withContext(Dispatchers.Main){
                        _uiState.update {
                            it.copy(
                                rideHistory = Result.Success(data = networkRides.rides),
                                isLoading = false
                            )
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main){
                        _uiState.update {
                            it.copy(
                                rideHistory = Result.Error(RideHistoryError.Network.NETWORK_ERROR),
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateCustomerId(customerId: String) {
        _uiState.update { it.copy(customerId = customerId) }
    }

    fun updateDriver(driverId: Int, driverName: String) {
        _uiState.update {
            it.copy(
                driverId = driverId,
                driverName = driverName
            )
        }
    }

    fun toggleDriverMenu(isExpanded: Boolean) {
        _uiState.update { it.copy(isDriverMenuExpanded = isExpanded) }
    }

}