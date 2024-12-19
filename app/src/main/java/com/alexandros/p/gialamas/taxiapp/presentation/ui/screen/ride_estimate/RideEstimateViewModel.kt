package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.util.checkInternetConnectivity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.error.ride_estimate_error.UserDataValidator
import com.alexandros.p.gialamas.taxiapp.domain.error.ride_estimate_error.ValidationResult
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetRideEstimateUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RideEstimateViewModel @Inject constructor(
    private val getRideEstimateUseCase: GetRideEstimateUseCase,
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

    private val eventChannel = Channel<UserEvent>()
    val events = eventChannel.receiveAsFlow()

    fun updateCustomerId(customerId: String) {
        _uiState.update { it.copy(customerId = customerId) }
    }

    fun updateOrigin(origin: String) {
        _uiState.update { it.copy(origin = origin) }
    }

    fun updateDestination(destination: String) {
        _uiState.update { it.copy(destination = destination) }
    }

    fun updateLoadingState(loadingState: Boolean) {
        _uiState.update { it.copy(isLoading = loadingState) }
    }

    fun updateError(error: UiText) {
        _uiState.update { it.copy(error = error) }
    }


    private var apiRequestJob: Job? = null

    fun cancelApiRequest() {
        apiRequestJob?.cancel()
        apiRequestJob = null
        _uiState.update {
            it.copy(
                isLoading = false,
            )
        }
    }

    fun getRideEstimate(
        context: Context,
        customerId: String,
        origin: String,
        destination: String,
    ) {
        val networkConnectivity = checkInternetConnectivity(context)  // TODO { handle the error case }

        apiRequestJob = Job()

        apiRequestJob?.let {
            viewModelScope.launch(Dispatchers.IO + apiRequestJob as CompletableJob) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = true) }
                }
                try {
                    val rideEstimate = getRideEstimateUseCase(
                        customerId = customerId,
                        origin = origin,
                        destination = destination
                    )
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                rideEstimate = Result.Success(data = rideEstimate)
                            )
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                rideEstimate = Result.Error(RideEstimateError.Network.NETWORK_ERROR),
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
        val validationResult = UserDataValidator().validation(customerId, origin, destination)

        when (validationResult) {
            is ValidationResult.Error -> {
                updateError(validationResult.error)
                eventChannel.trySend(UserEvent.Error(validationResult.error))
            }

            is ValidationResult.Success -> {

                apiRequestJob?.let {
                    viewModelScope.launch(Dispatchers.IO + apiRequestJob as CompletableJob) {
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        try {
                            val rideEstimate = getRideEstimateUseCase(
                                customerId = customerId,
                                origin = origin,
                                destination = destination
                            )
                            withContext(Dispatchers.Main) {
                                _uiState.update {
                                    it.copy(
                                        rideEstimate = Result.Success(data = rideEstimate),
                                        isLoading = false
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                _uiState.update {
                                    it.copy(
                                        rideEstimate = Result.Error(RideEstimateError.Network.NETWORK_ERROR),
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

sealed interface UserEvent {
    data class Error(val error: UiText) : UserEvent
}