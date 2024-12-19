package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.util.checkInternetConnectivity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.error.ride_estimate_error.UserRideDataValidator
import com.alexandros.p.gialamas.taxiapp.domain.error.ride_estimate_error.RideValidationResult
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetRideEstimateUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private fun updateError(error: UiText) {
        _uiState.update { it.copy(error = error) }
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
        _uiState.update {
            it.copy(
                isCustomerIdValid = customerId.isNotBlank(),
                isOriginValid = origin.isNotBlank(),
                isDestinationValid = destination.isNotBlank()
            )
        }

        val networkConnectivity =
            checkInternetConnectivity(context)  // TODO { handle the error case }

        apiRequestJob = Job()


        when (val userDataValidation = UserRideDataValidator().validation(customerId, origin, destination)) {
            is RideValidationResult.Error -> {
                updateError(userDataValidation.error)
            }

            is RideValidationResult.Success -> {

                updateError(UiText.DynamicString(""))

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
