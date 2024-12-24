package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.mapper.toRideEstimate
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.data.util.checkInternetConnectivity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateUserDataValidator
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetRideEstimateUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.asEstimateUiText
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Customer
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
import javax.inject.Inject

@HiltViewModel
class RideEstimateViewModel @Inject constructor(
    private val getRideEstimateUseCase: GetRideEstimateUseCase,
    private val rideEstimateUserDataValidator: RideEstimateUserDataValidator
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

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }


    fun updateIsCustomerIdValid(isCustomerIdValid: Boolean) {
        _uiState.update { it.copy(isCustomerIdValid = isCustomerIdValid) }
    }

    fun updateIsOriginValid(isOriginValid: Boolean) {
        _uiState.update { it.copy(isOriginValid = isOriginValid) }
    }

    fun updateIsDestinationValid(isDestinationValid: Boolean) {
        _uiState.update { it.copy(isDestinationValid = isDestinationValid) }
    }


    private var apiRequestJob: Job? = null

    suspend fun cancelApiRequest() {
        apiRequestJob?.cancel()
        apiRequestJob = null
        _uiState.update {
            it.copy(
                isLoading = false,
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

    fun getRideEstimate(
        context: Context,
        customerId: String,
        origin: String,
        destination: String,
    ) {


        if (!checkInternetConnectivity(context)) {
            _uiState.update {
                it.copy(
                    error = RideEstimateError.Network.NETWORK_ERROR.asEstimateUiText(),
                    isLoading = false
                )
            }
        } else {

            _uiState.update {
                it.copy(
                    isCustomerIdValid = customerId.isNotBlank(),
                    isOriginValid = origin.isNotBlank(),
                    isDestinationValid = destination.isNotBlank()
                )
            }


            apiRequestJob = Job()


            apiRequestJob?.let {
                viewModelScope.launch(Dispatchers.IO + apiRequestJob as CompletableJob) {

                    when (val result = rideEstimateUserDataValidator.validation(
                        customerId = customerId,
                        origin = origin,
                        destination = destination
                    )
                    ) {
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    error = when (result.error) {
                                        RideEstimateError.UserDataValidation.INVALID_CUSTOMER_ID -> result.error.asEstimateUiText()
                                        RideEstimateError.UserDataValidation.INVALID_ORIGIN -> result.error.asEstimateUiText()
                                        RideEstimateError.UserDataValidation.INVALID_DESTINATION -> result.error.asEstimateUiText()
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
                                            isLoading = true
                                        )
                                    }
                                }

                                when (val rideEstimate =
                                    getRideEstimateUseCase(customerId, origin, destination)) {

                                    is Result.Error -> {
                                        _uiState.update {
                                            it.copy(
                                                error = rideEstimate.error.asEstimateUiText(),
                                                isLoading = false
                                            )
                                        }
                                    }

                                    is Result.Success -> {

                                        when (val rideEstimateResult = rideEstimate.data) {
                                            is RideEstimateResponse.Error -> {
                                                _uiState.update {
                                                    it.copy(
                                                        error = when (rideEstimateResult.response.status.value) {
                                                            400 -> {
                                                                RideEstimateError.Network.INVALID_DATA.asEstimateUiText()
                                                            }

                                                            404 -> {
                                                                RideEstimateError.Network.DRIVER_NOT_FOUND.asEstimateUiText()
                                                            }

                                                            406 -> {
                                                                RideEstimateError.Network.INVALID_DISTANCE.asEstimateUiText()
                                                            }

                                                            else -> {
                                                                RideEstimateError.Network.UNKNOWN_ERROR.asEstimateUiText()
                                                            }
                                                        },
                                                        isLoading = false
                                                    )
                                                }
                                            }

                                            is RideEstimateResponse.EstimateResponse -> {
                                                var isValid = true

                                                if (uiState.value.customerId != Customer.CUSTOMER_1.customerId) {
                                                    _uiState.update {
                                                        it.copy(
                                                            error = RideEstimateError.Network.INVALID_CUSTOMER_ID.asEstimateUiText(),
                                                            isLoading = false
                                                        )
                                                    }
                                                    isValid = false
                                                }

                                                if (rideEstimateResult.distance <= 0.0) {
                                                    _uiState.update {
                                                        it.copy(
                                                            error = RideEstimateError.Network.INVALID_LOCATION.asEstimateUiText(),
                                                            isLoading = false
                                                        )
                                                    }
                                                    isValid = false
                                                }

                                                if (isValid && rideEstimateResult.distance > 0.0) {
                                                    _uiState.update {
                                                        it.copy(
                                                            rideEstimate = Result.Success(
                                                                rideEstimateResult.toRideEstimate()
                                                            )
                                                        )
                                                    }
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
                                            rideEstimate = Result.Error(RideEstimateError.Network.UNKNOWN_ERROR),
                                            isLoading = false
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
    }
}


