package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.model.RideHistoryResponse
import com.alexandros.p.gialamas.taxiapp.data.util.checkInternetConnectivity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryUserDataValidator
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetLocalRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.asHistoryUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val getLocalRideHistoryUseCase: GetLocalRideHistoryUseCase,
    private val rideHistoryUserDataValidator: RideHistoryUserDataValidator,
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

//    val localRides: StateFlow<Result<List<Ride>, RideHistoryError>> =
//        getLocalRideHistoryUseCase(_uiState.value.customerId, _uiState.value.driverId)
//            .map {
//                Result.Success<List<Ride>, RideHistoryError>(it)
//            }
//            .catch {
//                Result.Error<List<Ride>, RideHistoryError>(
//                    RideHistoryError.Local.LOCAL_ERROR
//                )
//            }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(5000L),
//                initialValue = Result.Idle
//            )

    private var apiRequestJob: Job? = null

    suspend fun cancelApiRequest() {
        apiRequestJob?.cancel()
        apiRequestJob = null
        _uiState.update {
            it.copy(
                isNetworkLoading = false,
                canRequestAgain = false
            )
        }
        delay(5000L)
        _uiState.update { currentState ->
            currentState.copy(
                canRequestAgain = true
            )
        }
    }


    fun getRideHistory(customerId: String, driverId: Int?, context: Context) {
        Log.d(
            "fetchRides",
            "getRideHistory called with customerId: $customerId, driverId: $driverId"
        )
        _uiState.update {
            it.copy(
                isCustomerIdValid = customerId.isNotBlank()
            )
        }

        if (!checkInternetConnectivity(context)) {
            _uiState.update {
                it.copy(
                    error = RideHistoryError.Network.NETWORK_ERROR.asHistoryUiText()
                )
            }
        }

        apiRequestJob = Job()

        apiRequestJob?.let {
            viewModelScope.launch(Dispatchers.IO + apiRequestJob as CompletableJob) {

                when (val result = rideHistoryUserDataValidator.validation(customerId)) {
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                error = when (result.error) {
                                    RideHistoryError.UserDataValidation.INVALID_CUSTOMER_ID -> result.error.asHistoryUiText()
                                }
                            )
                        }
                    }

                    is Result.Success -> {
                        try {
                            withContext(Dispatchers.Main) {
                                clearError()
                                _uiState.update {
                                    it.copy(
                                        isNetworkLoading = true,
                                        rideHistory = Result.Success(emptyList())
                                    )
                                }
                            }

                            when (val networkRides = getRideHistoryUseCase(customerId, driverId)) {
                                is Result.Error -> {
                                    _uiState.update {
                                        it.copy(
                                            error = networkRides.error.asHistoryUiText(),
                                            isNetworkLoading = false
                                        )
                                    }
                                }

                                is Result.Success -> {
                                    when (val ridesResult = networkRides.data) {
                                        is RideHistoryResponse.Error -> {
                                            _uiState.update {
                                                it.copy(
                                                    error = when (ridesResult.response.status.value) {
                                                        400 -> {
                                                            RideHistoryError.Network.DRIVER_NOT_FOUND.asHistoryUiText()
                                                        }

                                                        404 -> {
                                                            RideHistoryError.Network.INVALID_DATA.asHistoryUiText()
                                                        }

                                                        406 -> {
                                                            RideHistoryError.Network.INVALID_DISTANCE.asHistoryUiText()
                                                        }

                                                        else -> {
                                                            RideHistoryError.Network.UNKNOWN_ERROR.asHistoryUiText()
                                                        }
                                                    },
                                                    isNetworkLoading = false
                                                )
                                            }
                                        }

                                        is RideHistoryResponse.HistoryResponse -> {
                                            _uiState.update {
                                                it.copy(
                                                    rideHistory = Result.Success(
                                                        if (uiState.value.driverId != null) {
                                                            val resultOfRides =
                                                                ridesResult.rides.filter { rides ->
                                                                    rides.driver.name.equals(
                                                                        uiState.value.driverName,
                                                                        ignoreCase = true
                                                                    )
                                                                }

                                                            resultOfRides.ifEmpty {
                                                                _uiState.update { currentState ->
                                                                    currentState.copy(
                                                                        error = RideHistoryError.Network.NO_RIDES_FOUND.asHistoryUiText(),
                                                                        isNetworkLoading = false
                                                                    )
                                                                }
                                                            }
                                                            resultOfRides
                                                        } else {
                                                            ridesResult.rides
                                                        }

                                                    ),
                                                    isNetworkLoading = false
                                                )
                                            }
                                        }
                                    }
                                }

                                else -> {}
                            }

                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                _uiState.update {
                                    it.copy(
                                        rideHistory = Result.Error(RideHistoryError.Network.UNKNOWN_ERROR),
                                        isNetworkLoading = false
                                    )
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun getLocalRidesHistory(customerId: String, driverId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {

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
    }


    fun updateCustomerId(customerId: String) {
        _uiState.update { it.copy(customerId = customerId) }
    }

    fun updateIsCustomerIdValid(isCustomerIdValid: Boolean) {
        _uiState.update { it.copy(isCustomerIdValid = isCustomerIdValid) }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
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
