package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistory
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history.GetLocalRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history.GetRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history.ValidateRideHistoryDataUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.action.RideHistoryAction
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.state.RideHistoryState
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.state.Rides
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

    fun handleAction(action: RideHistoryAction) {
        when (action) {
            RideHistoryAction.DelayedErrorClearance -> delayedErrorClearance()
            is RideHistoryAction.DriverSelectorOnDismiss -> toggleDriverMenu(action.onDismiss)
            is RideHistoryAction.DriverSelectorOnExpandChange -> toggleDriverMenu(action.onExpandChange)
            is RideHistoryAction.DriverSelectorOnDriverSelected -> {
                updateDriver(
                    driverId = action.driver.driverId,
                    driverName = action.driver.driverName
                )
            }

            RideHistoryAction.CancelRequest -> cancelApiRequest()
            RideHistoryAction.ConfirmRequest -> {
                getRideHistory(
                    driverId = _uiState.value.driverId,
                    customerId = _uiState.value.customerId
                )
            }
        }
    }

    private fun updateState(block: (RideHistoryState) -> RideHistoryState) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _uiState.update { currentState ->
                block(currentState)
            }
        }
    }

    private fun delayedErrorClearance() {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            delay(3000L)
            clearError()
        }
    }

    fun updateCustomerId(customerId: String) {
        updateState { it.copy(customerId = customerId) }
    }

    fun updateIsCustomerIdValid(isCustomerIdValid: Boolean) {
        updateState { it.copy(isCustomerIdValid = isCustomerIdValid) }
    }

    private fun clearError() {
        updateState {
            it.copy(
                networkError = null,
                localError = null
            )
        }
    }

    private fun updateDriver(driverId: Int?, driverName: String) {
        updateState {
            it.copy(
                driverId = driverId,
                driverName = driverName
            )
        }
    }

    private fun toggleDriverMenu(isExpanded: Boolean) {
        updateState { it.copy(isDriverMenuExpanded = isExpanded) }
    }

    private fun debounce() {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isRideHistoryCallReady = !_uiState.value.isRideHistoryCallReady,
                    isNetworkLoading = false
                )
            }
            delay(3000L)
            updateState {
                it.copy(
                    isRideHistoryCallReady = !_uiState.value.isRideHistoryCallReady,
                    isNetworkLoading = false
                )
            }
        }
    }

    private var apiRequestJob: Job? = null

    private fun cancelApiRequest() {
        apiRequestJob?.cancel()
        apiRequestJob = null
        debounce()
    }


    private fun getRideHistory(customerId: String, driverId: Int?) {

        clearError()

        updateState {
            it.copy(
                rides = emptyList(),
                isCustomerIdValid = customerId.isNotBlank()
            )
        }

        getLocalRidesHistory(customerId, driverId)

        apiRequestJob = Job()

        apiRequestJob?.let {

            viewModelScope.launch(Dispatchers.IO + apiRequestJob as CompletableJob) {

                when (val result = validateRideHistoryDataUseCase.validation(customerId)) {
                    is Result.Error -> {
                        updateState {
                            it.copy(
                                networkError = result.error.asHistoryUiText(),
                                isNetworkLoading = false
                            )
                        }
                    }

                    is Result.Success -> {
                        try {
                            updateState {
                                it.copy(
                                    isNetworkLoading = true,
                                    rideHistory = Result.Success(emptyList())
                                )
                            }

                            when (val networkRides =
                                getRideHistoryUseCase(customerId, driverId)) {
                                is Result.Error -> {
                                    updateState {
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
                                        updateState { currentState ->
                                            currentState.copy(
                                                networkError = RideHistoryError.Network.NoRidesFound().asHistoryUiText(),
                                                isNetworkLoading = false
                                            )
                                        }
                                    } else {
                                        updateState {
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

                                    updateState {
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

                                Result.Idle -> Result.Idle
                            }
                        } catch (e: Exception) {
                            updateState {
                                it.copy(
                                    rideHistory = Result.Error(RideHistoryError.Network.UnknownError()),
                                    isNetworkLoading = false
                                )
                            }
                            updateState { currentState ->
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

    private fun getLocalRidesHistory(customerId: String, driverId: Int?) {

        updateState { it.copy(isLocalLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {

            getLocalRideHistoryUseCase(
                customerId,
                driverId
            ).collect { result ->
                when (result) {
                    is Result.Error -> {
                        updateState {
                            it.copy(
                                localError = result.error.asHistoryUiText(),
                                isLocalLoading = false
                            )
                        }
                    }

                    is Result.Success -> {

                        updateState {
                            it.copy(
                                localRides = Result.Success(result.data),
                                isLocalLoading = false
                            )
                        }

                        val localRidesModel = result.data.map { Rides.Local(it) }
                        val currentRides: List<Rides> = _uiState.value.rides
                        val combineRides = (currentRides + localRidesModel)

                        updateState {
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

                    Result.Idle -> Result.Idle
                }
            }
        }
    }


}
