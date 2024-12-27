package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.mapper.toEntity
import com.alexandros.p.gialamas.taxiapp.data.mapper.toRide
import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_confirm.ConfirmRideUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_confirm.InsertRideUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_confirm.SaveRideUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.action.RideConfirmAction
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.state.RideConfirmState
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.asConfirmUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
class RideConfirmViewModel @Inject constructor(
    private val confirmRideUseCase: ConfirmRideUseCase,
    private val insertRideUseCase: InsertRideUseCase,
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


    fun handleAction(action: RideConfirmAction) {
        when (action) {
            is RideConfirmAction.OptionSelected -> selectRideOption(action.rideOption)
            is RideConfirmAction.LoadData -> loadData(
                rideEstimate = action.rideEstimate,
                customerId = action.customerId,
                origin = action.origin,
                destination = action.destination
            )
            RideConfirmAction.ClearError -> clearError()
            RideConfirmAction.ConfirmRide -> confirmRide()
            RideConfirmAction.Debounce -> debounce()
            RideConfirmAction.Restart -> updateState { it.copy(restart = true) }
        }
    }

    private fun loadData(
        rideEstimate: RideEstimate,
        customerId: String,
        origin: String,
        destination: String
    ) {
        updateState {
            it.copy(
                customerId = customerId,
                origin = origin,
                destination = destination,
                distance = rideEstimate.distance,
                duration = rideEstimate.duration,
                options = rideEstimate.options,
                rideEstimate = rideEstimate
            )
        }
    }

    private fun selectRideOption(rideOption: RideOption) {
        updateState { it.copy(rideOption = rideOption) }
    }

    private fun debounce(){
        viewModelScope.launch {
            updateState {
                it.copy(isConfirmRideCallReady = !_uiState.value.isConfirmRideCallReady)
            }
            delay(3000L)
            updateState {
                it.copy(isConfirmRideCallReady = !_uiState.value.isConfirmRideCallReady)
            }
        }
    }

    private fun clearError() {
        updateState { it.copy(error = null) }
    }

    private fun rideRequest(): ConfirmRideRequest {
        return ConfirmRideRequest(
            customerId = uiState.value.customerId,
            origin = uiState.value.origin,
            destination = uiState.value.destination,
            distance = uiState.value.distance,
            duration = uiState.value.duration,
            driver = Driver(
                id = uiState.value.rideOption!!.id,
                name = uiState.value.rideOption!!.name
            ),
            value = uiState.value.rideOption!!.value
        )
    }


    private fun saveRide(rideRequest: ConfirmRideRequest) {

        val newRide = rideRequest.toRide().toEntity()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (val result = saveRideUseCase(newRide)) {
                    is Result.Error -> {
                        updateState {
                            it.copy(
                                error = result.error.asConfirmUiText(),
                                isLoading = false
                            )
                        }
                        return@launch
                    }

                    is Result.Success -> {
                        updateState {
                            it.copy(
                                isRideConfirmed = true,
                                isLoading = !_uiState.value.isRideConfirmed
                            )
                        }
                    }

                    Result.Idle -> Result.Idle
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        error = RideConfirmError.Local.FAILED_TO_SAVE_THE_RIDE.asConfirmUiText(),
                        isRideConfirmed = false,
                        isLoading = false
                    )
                }
                return@launch
            }
        }
    }

    private fun updateState(block: (RideConfirmState) -> RideConfirmState) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _uiState.update { currentState ->
                block(currentState)
            }
        }
    }

    private fun confirmRide() {

        clearError()

        viewModelScope.launch(Dispatchers.IO) {

            if ((uiState.value.rideOption != null)
                && (uiState.value.rideEstimate != null)
            ) {

                updateState { it.copy(isLoading = true) }

                try {
                    val rideRequest = rideRequest()

                    when (val isRideConfirmed = confirmRideUseCase(rideRequest)) {
                        is Result.Error -> {
                            updateState {
                                it.copy(
                                    error = isRideConfirmed.error.asConfirmUiText(),
                                    isRideConfirmed = false,
                                    isLoading = false
                                )
                            }
                        }

                        is Result.Success -> {
                            saveRide(rideRequest)
                        }

                        Result.Idle -> Result.Idle
                    }
                } catch (e: Exception) {
                    updateState {
                        it.copy(
                            error = RideConfirmError.NetWork.UNKNOWN_ERROR.asConfirmUiText(),
                            isRideConfirmed = false,
                            isLoading = false
                        )
                    }
                    return@launch
                }
            } else {
                updateState {
                    it.copy(
                        error = RideConfirmError.Local.INVALID_STATE_DATA.asConfirmUiText(),
                        isRideConfirmed = false,
                        isLoading = false,
                        restart = true
                    )
                }
            }
        }
    }
}