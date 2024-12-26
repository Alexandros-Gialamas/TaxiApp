package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.util.checkInternetConnectivity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history.ValidateRideHistoryDataUseCase
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistory
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history.GetLocalRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history.GetRideHistoryUseCase
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RideHistoryViewModel @Inject constructor(
    private val getRideHistoryUseCase: GetRideHistoryUseCase,
    private val getLocalRideHistoryUseCase: GetLocalRideHistoryUseCase,
    private val validateRideHistoryDataUseCase: ValidateRideHistoryDataUseCase
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
        delay(3000L)
        _uiState.update { currentState ->
            currentState.copy(
                canRequestAgain = true
            )
        }
    }


    fun getRideHistory(customerId: String, driverId: Int?, context: Context) {
        Timber.tag("RideHistoryViewModel")
            .d("getRideHistory called with customerId: $customerId, driverId: $driverId")

        clearError()


        if (!checkInternetConnectivity(context)) {
            _uiState.update {
                it.copy(
                    error = RideHistoryError.Network.NETWORK_ERROR.asHistoryUiText(),
                    isNetworkLoading = false,
                    isLocalLoading = false
                )
            }
        } else {

            _uiState.update {
                it.copy(
                    isCustomerIdValid = customerId.isNotBlank()
                )
            }

            apiRequestJob = Job()

            apiRequestJob?.let {
                viewModelScope.launch(apiRequestJob as CompletableJob) {

                    when (val result = validateRideHistoryDataUseCase.validation(customerId)) {
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    error = result.error.asHistoryUiText()
                                )
                            }
                        }

                        is Result.Success -> {
                            try {
                                _uiState.update {
                                    it.copy(
                                        isNetworkLoading = true,
                                        rideHistory = Result.Success(emptyList())
                                    )
                                }


                                when (val networkRides =
                                    getRideHistoryUseCase(customerId, driverId)) {
                                    is Result.Error -> {
                                        _uiState.update {
                                            it.copy(
                                                error = networkRides.error.asHistoryUiText(),
                                                isNetworkLoading = false
                                            )
                                        }
                                    }

                                    is Result.Success -> {
                                        var historyRides = emptyList<RideHistory>()
                                        if (uiState.value.driverId != null) {
                                            val resultOfRides =
                                                networkRides.data.rides.filter { rides ->
                                                    rides.driver.name.equals(
                                                        uiState.value.driverName,
                                                        ignoreCase = true
                                                    )
                                                }

                                            historyRides =
                                                resultOfRides.ifEmpty { emptyList() }
                                        } else {
                                            historyRides = networkRides.data.rides
                                        }

                                        if (historyRides.isEmpty()) {
                                            _uiState.update { currentState ->
                                                currentState.copy(
                                                    error = RideHistoryError.Network.NO_RIDES_FOUND.asHistoryUiText(),
                                                    isNetworkLoading = false
                                                )
                                            }
                                        } else {
                                            _uiState.update {
                                                it.copy(
                                                    rideHistory = Result.Success(
                                                        historyRides
                                                    ),
                                                    isNetworkLoading = false
                                                )
                                            }
                                        }
                                    }

                                    else -> {}
                                }
                            } catch (e: Exception) {
                                _uiState.update {
                                    it.copy(
                                        rideHistory = Result.Error(RideHistoryError.Network.UNKNOWN_ERROR),
                                        isNetworkLoading = false
                                    )
                                }
                                _uiState.update { currentState ->
                                    currentState.copy(rideHistory = Result.Success(emptyList()))
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun getLocalRidesHistory(customerId: String, driverId: Int?) {

        _uiState.update {
            it.copy(
                isLocalLoading = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {

            getLocalRideHistoryUseCase(
                customerId,
                driverId
            ).collect { result ->
                when (result) {
                    is Result.Error -> {
                        withContext(Dispatchers.Main) {
                            Timber.tag("error load local rides").d("error load local rides ${result.error}")
                            _uiState.update {
                                it.copy(
                                    error = result.error.asHistoryUiText(),
                                    isLocalLoading = false
                                )
                            }
                        }
                    }

                    is Result.Success -> {
                        withContext(Dispatchers.Main) {
                            Timber.tag("load local rides").d("load local rides ${result.data}")
                            _uiState.update {
                                it.copy(
                                    localRides = Result.Success(result.data),
                                    isLocalLoading = false
                                )
                            }
                        }
                    }

                    Result.Idle -> Result.Idle
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
