package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.mapper.toRideEstimate
import com.alexandros.p.gialamas.taxiapp.data.util.checkInternetConnectivity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_estimate.GetRideEstimateUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_estimate.ValidateRideEstimateDataUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.asEstimateUiText
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
    private val validateRideEstimateDataUseCase: ValidateRideEstimateDataUseCase
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


    // Job for managing the API request coroutine.
    private var apiRequestJob: Job? = null

    /**
     * Cancels the ongoing API request, updates the UI state accordingly and enables the request button again after a delay.
     *
     */
    suspend fun cancelApiRequest() {
        // Cancel the ongoing API request Job if it exists.
        apiRequestJob?.cancel()
        // Set the Job to null, indicating no active request.
        apiRequestJob = null
        // Update the UI state to reflect that the app is no longer loading and disable the request button.
        _uiState.update {
            it.copy(
                isLoading = false,
                canRequestAgain = false
            )
        }
        // Introduce a delay before allowing the user to make another request.
        // This prevents rapid, repeated requests that can lead to unnecessary load or UI issues.
        delay(3000L)
        // After the delay, update the UI state to enable the request button again.
        _uiState.update { currentState ->
            currentState.copy(
                canRequestAgain = true
            )
        }
    }

    /**
     * Initiates the process of getting a ride estimate.
     * This function is the entry point for fetching a ride estimate based on user input.
     * It performs the following steps:
     * 1. Clears any previous errors.
     * 2. Checks for internet connectivity.
     * 3. Validates the input data.
     * 4. Initiates the API request in a coroutine.
     * 5. Updates the UI state based on the result of the API request.
     *
     * @param context The Android context, used for checking internet connectivity.
     * @param customerId The customer's ID.
     * @param origin The origin of the ride.
     * @param destination The destination of the ride.
     */
    fun getRideEstimate(
        context: Context,
        customerId: String,
        origin: String,
        destination: String,
    ) {
        // Clear any existing errors from the UI state.
        clearError()

        // Check for internet connectivity before proceeding.
        if (!checkInternetConnectivity(context)) {
            // If there's no internet connection, update the UI state with a network error and stop loading.
            _uiState.update {
                it.copy(
                    error = RideEstimateError.Network.NETWORK_ERROR.asEstimateUiText(),
                    isLoading = false
                )
            }
            // Return early as there's no internet connectivity.
            return
        } else {

            // Update the UI state with the validity of the input fields based on simple not-blank checks.
            _uiState.update {
                it.copy(
                    isCustomerIdValid = customerId.isNotBlank(),
                    isOriginValid = origin.isNotBlank(),
                    isDestinationValid = destination.isNotBlank()
                )
            }

            // Create a new Job for the API request.
            apiRequestJob = Job()

            // Proceed with the API request if the job was created successfully.
            apiRequestJob?.let {
                // Launch a new coroutine within the viewModelScope for the network request.
                // The coroutine runs on the IO dispatcher, which is optimized for network and disk operations.
                viewModelScope.launch(Dispatchers.IO + apiRequestJob as CompletableJob) {
                    // Validate the input data using the use case.
                    when (val result = validateRideEstimateDataUseCase.validation(
                        customerId = customerId,
                        origin = origin,
                        destination = destination
                    )
                    ) {
                        // If validation fails, update the UI state with the error.
                        is Result.Error -> {
                            // Update the UI state on the Main dispatcher with the validation error.
                            withContext(Dispatchers.Main) {
                                _uiState.update {
                                    it.copy(
                                        error = result.error.asEstimateUiText()
                                    )
                                }
                            }
                        }
                        // If validation succeeds, proceed with the API request.
                        is Result.Success -> {
                            try {
                                // Update the UI state to indicate loading on the Main dispatcher with the validation error.
                                withContext(Dispatchers.Main) {
                                    _uiState.update {
                                        it.copy(
                                            isLoading = true
                                        )
                                    }
                                }
                                // Fetch the ride estimate using the use case.
                                when (val rideEstimate =
                                    getRideEstimateUseCase(customerId, origin, destination)) {
                                    // If the API request fails, update the UI state with the error.
                                    is Result.Error -> {
                                        // Update the UI state on the Main dispatcher with the API error and stop loading.
                                        withContext(Dispatchers.Main) {
                                            _uiState.update {
                                                it.copy(
                                                    error = rideEstimate.error.asEstimateUiText(),
                                                    isLoading = false
                                                )
                                            }
                                        }
                                    }
                                    // If the API request succeeds, update the UI state with the ride estimate.
                                    is Result.Success -> {
                                        // Update the UI state on the Main dispatcher with the ride estimate data.
                                        withContext(Dispatchers.Main) {
                                            _uiState.update {
                                                it.copy(
                                                    rideEstimate = Result.Success(rideEstimate.data.toRideEstimate()),
                                                )
                                            }
                                        }
                                    }
                                    // Handle the Idle state if necessary.
                                    Result.Idle -> Result.Idle
                                }

                            } catch (e: Exception) {
                                // Handle any exceptions during the API request.
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
                        // Handle the Idle state if necessary.
                        Result.Idle -> Result.Idle
                    }
                }
            }
        }
    }
}


