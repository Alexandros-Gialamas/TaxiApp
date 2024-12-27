package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.util.checkInternetConnectivity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistory
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history.GetLocalRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history.GetRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history.ValidateRideHistoryDataUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.asHistoryUiText
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.extractDate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.extractTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    fun updateCustomerId(customerId: String) {
        _uiState.update { it.copy(customerId = customerId) }
    }

    fun updateIsCustomerIdValid(isCustomerIdValid: Boolean) {
        _uiState.update { it.copy(isCustomerIdValid = isCustomerIdValid) }
    }

    private fun clearError() {
        _uiState.update {
            it.copy(
                networkError = null,
                localError = null
            )
        }
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

        clearError()

        _uiState.update {
            it.copy(
                rides = emptyList()
            )
        }

        getLocalRidesHistory(customerId, driverId)

        if (!checkInternetConnectivity(context)) {
            _uiState.update {
                it.copy(
                    networkError = RideHistoryError.Network.NETWORK_ERROR.asHistoryUiText(),
                    isNetworkLoading = false,
                    isLocalLoading = false
                )
            }
            return
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
                                    networkError = result.error.asHistoryUiText()
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
                                                networkError = networkRides.error.asHistoryUiText(),
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
                                                    networkError = RideHistoryError.Network.NO_RIDES_FOUND.asHistoryUiText(),
                                                    isNetworkLoading = false
                                                )
                                            }
                                        } else {
                                            _uiState.update {
                                                it.copy(
                                                    rideHistory = Result.Success(
                                                        historyRides.ifEmpty { emptyList() }
                                                    ),
                                                    isNetworkLoading = false
                                                )
                                            }
                                        }
                                        val networkRidesModel =
                                            historyRides.map { Rides.Network(it) }
                                        val currentRides: List<Rides> = _uiState.value.rides
                                        val combineRides = (currentRides + networkRidesModel)
                                        withContext(Dispatchers.Main) {
                                            _uiState.update {
                                                it.copy(
                                                    rides = combineRides.sortedWith(
                                                        compareByDescending<Rides> { rides ->
                                                            when (rides) {
                                                                is Rides.Local -> rides.rideEntity.date?.let {
                                                                    extractDate(
                                                                        rides.rideEntity.date
                                                                    )
                                                                }

                                                                is Rides.Network -> rides.rideHistory.date?.let {
                                                                    extractDate(
                                                                        rides.rideHistory.date
                                                                    )
                                                                }
                                                            }
                                                        }.thenByDescending { rides ->
                                                            when (rides) {
                                                                is Rides.Local -> rides.rideEntity.date?.let {
                                                                    extractTime(
                                                                        rides.rideEntity.date
                                                                    )
                                                                }

                                                                is Rides.Network -> rides.rideHistory.date?.let {
                                                                    extractTime(
                                                                        rides.rideHistory.date
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    ),
                                                    isNetworkLoading = false
                                                )
                                            }
                                        }
                                    }

                                    Result.Idle -> Result.Idle
                                }
                            } catch (e: Exception) {
                                _uiState.update {
                                    it.copy(
                                        rideHistory = Result.Error(RideHistoryError.Network.UNKNOWN_ERROR),
                                        isNetworkLoading = false
                                    )
                                }
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        rideHistory = Result.Success(emptyList()),
                                        isNetworkLoading = false
                                    )
                                }
                                return@launch
                            }
                        }

                        Result.Idle -> Result.Idle
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
                            Timber.tag("error load local rides")
                                .d("error load local rides ${result.error}")
                            _uiState.update {
                                it.copy(
                                    localError = result.error.asHistoryUiText(),
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
                        val localRidesModel = result.data.map { Rides.Local(it) }
                        val currentRides: List<Rides> = _uiState.value.rides
                        val combineRides = (currentRides + localRidesModel)
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    rides = combineRides.sortedWith(
                                        compareByDescending<Rides> { rides ->
                                            when (rides) {
                                                is Rides.Local -> rides.rideEntity.date?.let {
                                                    extractDate(
                                                        rides.rideEntity.date
                                                    )
                                                }

                                                is Rides.Network -> rides.rideHistory.date?.let {
                                                    extractDate(
                                                        rides.rideHistory.date
                                                    )
                                                }
                                            }
                                        }.thenByDescending { rides ->
                                            when (rides) {
                                                is Rides.Local -> rides.rideEntity.date?.let {
                                                    extractTime(
                                                        rides.rideEntity.date
                                                    )
                                                }

                                                is Rides.Network -> rides.rideHistory.date?.let {
                                                    extractTime(
                                                        rides.rideHistory.date
                                                    )
                                                }
                                            }
                                        }
                                    ),
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


}
