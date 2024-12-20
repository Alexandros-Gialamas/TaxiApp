package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.error.ride_history_error.HistoryValidationResult
import com.alexandros.p.gialamas.taxiapp.domain.error.ride_history_error.UserHistoryDataValidator
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetLocalRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
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

    val localRides: StateFlow<Result<List<Ride>, RideHistoryError>> =
        getLocalRideHistoryUseCase(_uiState.value.customerId, _uiState.value.driverId)
            .map {
                Result.Success<List<Ride>, RideHistoryError>(it)
            }
            .catch {
                Result.Error<List<Ride>, RideHistoryError>(
                    RideHistoryError.Local.LOCAL_ERROR
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = Result.Idle
            )

    fun getRideHistory(customerId: String, driverId: Int?) {
        Log.d(
            "fetchRides",
            "getRideHistory called with customerId: $customerId, driverId: $driverId"
        )

        _uiState.update {
            it.copy(
                isCustomerIdValid = customerId.isNotBlank()
            )
        }

        when (val userDataValidation = UserHistoryDataValidator().validation(customerId)) {
            is HistoryValidationResult.Error -> {
                updateError(userDataValidation.error)
            }

            is HistoryValidationResult.Success -> {

                viewModelScope.launch(Dispatchers.IO) {

//            when (val localDataResult = uiState.value.localRides) {
//                is Result.Error -> {
//
//                }
//                Result.Idle -> {
//
//                }
//                is Result.Success -> {
//                    localDataResult.data
//                }
//            }
//
//            when(val networkDataResult = uiState.value.rideHistory) {
//                is Result.Error -> {
//
//                }
//                Result.Idle -> {}
//                is Result.Success -> { networkDataResult.data }
//            }

                    try {
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(isLocalLoading = true) }
                        }
                        getLocalRideHistoryUseCase(
                            customerId,
                            driverId
                        ).collectLatest { localRides ->
                            Log.d("fetchRides", "localRides are: $localRides")
                            withContext(Dispatchers.Main) {
                                _uiState.update {
                                    it.copy(
                                        localRides = Result.Success(data = localRides),
                                        isLocalLoading = false
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    localRides = Result.Error(RideHistoryError.Local.LOCAL_ERROR),
                                    isLocalLoading = false
                                )
                            }
                        }
                        Log.e("fetchRides", "fetch Error = ${e.message}")
                        throw e
                    }
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(isNetworkLoading = true) }
                        }
                        val networkRides = getRideHistoryUseCase(customerId, driverId)
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    rideHistory = Result.Success(data = networkRides.rides),
                                    isNetworkLoading = false
                                )
                            }
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    rideHistory = Result.Error(RideHistoryError.Network.NETWORK_ERROR),
                                    isNetworkLoading = false
                                )
                            }
                        }
                    }
                }

            }
        }


    }


    fun updateCustomerId(customerId: String) {
        _uiState.update { it.copy(customerId = customerId) }
    }

    fun updateIsCustomerIdValid(isCustomerIdValid: Boolean) {
        _uiState.update { it.copy(isCustomerIdValid = isCustomerIdValid) }
    }

    private fun updateError(error: UiText) {
        _uiState.update { it.copy(error = error) }
    }

    fun updateDriver(driverId: Int?, driverName: String) {
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