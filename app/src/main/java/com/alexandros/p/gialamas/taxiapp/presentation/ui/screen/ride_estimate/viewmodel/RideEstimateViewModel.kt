package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.mapper.toRideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_estimate.GetRideEstimateUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_estimate.ValidateRideEstimateDataUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.action.RideEstimateAction
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.state.RideEstimateState
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

    fun handleAction(action: RideEstimateAction){
        when (action){
            RideEstimateAction.CancelApiRequest -> cancelApiRequest()
            RideEstimateAction.DelayedErrorClearance -> delayedErrorClearance()
            RideEstimateAction.LoadingStateToFalse -> updateState { it.copy(isLoading = false) }
            RideEstimateAction.GetRideEstimate -> getRideEstimate(
                customerId = _uiState.value.customerId,
                origin = _uiState.value.origin,
                destination = _uiState.value.destination
            )
        }
    }


    private fun updateState(block: (RideEstimateState) -> RideEstimateState) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _uiState.update { currentState ->
                block(currentState)
            }
        }
    }

    private fun delayedErrorClearance(){
        viewModelScope.launch(Dispatchers.Main.immediate) {
            delay(3000L)
            clearError()
        }
    }

    fun updateCustomerId(customerId: String) {
        updateState { it.copy(customerId = customerId) }
    }

    fun updateOrigin(origin: String) {
        updateState { it.copy(origin = origin) }
    }

    fun updateDestination(destination: String) {
        updateState { it.copy(destination = destination) }
    }


    private fun clearError() {
        updateState { it.copy(error = null) }
    }


    fun updateIsCustomerIdValid(isCustomerIdValid: Boolean) {
        updateState { it.copy(isCustomerIdValid = isCustomerIdValid) }
    }

    fun updateIsOriginValid(isOriginValid: Boolean) {
       updateState { it.copy(isOriginValid = isOriginValid) }
    }

    fun updateIsDestinationValid(isDestinationValid: Boolean) {
        updateState { it.copy(isDestinationValid = isDestinationValid) }
    }

    private fun debounce() {
        viewModelScope.launch {
            // update the UI state to disable the request button.
        updateState {
            it.copy(
                isRideEstimateCallReady = !_uiState.value.isRideEstimateCallReady,
                isLoading = false
            )
        }
            // Introduce a delay before allowing the user to make another request.
            // This prevents rapid, repeated requests that can lead to unnecessary load or UI issues.
            delay(3000L)
            // After the delay, update the UI state to enable the request button again.
            updateState {
                it.copy(
                    isRideEstimateCallReady = !_uiState.value.isRideEstimateCallReady,
                    isLoading = false
                )
            }
        }
    }


    // Job for managing the API request coroutine.
    private var apiRequestJob: Job? = null

    /**
     * Cancels the ongoing API request, updates the UI state accordingly and enables the request button again after a delay.
     *
     */
    fun cancelApiRequest() {
        // Cancel the ongoing API request Job if it exists.
        apiRequestJob?.cancel()
        // Set the Job to null, indicating no active request.
        apiRequestJob = null
        // Update the UI state to reflect that the app is no longer loading and disable the request button.
        debounce()
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
     * @param customerId The customer's ID.
     * @param origin The origin of the ride.
     * @param destination The destination of the ride.
     */
    private fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String,
    ) {
        // Clear any existing errors from the UI state.
        clearError()


        // Update the UI state with the validity of the input fields based on simple not-blank checks.
        updateState {
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
                        // Update the UI state with the validation error.
                        updateState { it.copy(error = result.error.asEstimateUiText()) }
                    }
                    // If validation succeeds, proceed with the API request.
                    is Result.Success -> {
                        try {
                            // Update the UI state to indicate loading with the validation error.
                            updateState { it.copy(isLoading = true) }
                            // Fetch the ride estimate using the use case.
                            when (val rideEstimate =
                                getRideEstimateUseCase(customerId, origin, destination)) {
                                // If the API request fails, update the UI state with the error.
                                is Result.Error -> {
                                    // Update the UI state with the API error and stop loading.
                                    updateState {
                                        it.copy(
                                            error = rideEstimate.error.asEstimateUiText(),
                                            isLoading = false
                                        )
                                    }
                                }
                                // If the API request succeeds, update the UI state with the ride estimate.
                                is Result.Success -> {
                                    // Update the UI state on the Main dispatcher with the ride estimate data.
                                    updateState {
                                        it.copy(
                                            rideEstimate = Result.Success(rideEstimate.data.toRideEstimate()),
                                            // Do not update loading to false here because of glitch
                                        )
                                    }
                                }
                                // Handle the Idle state if necessary.
                                Result.Idle -> Result.Idle
                            }

                        } catch (e: Exception) {
                            // Handle any exceptions during the API request.
                            updateState {
                                it.copy(
                                    rideEstimate = Result.Error(RideEstimateError.Network.UNKNOWN_ERROR),
                                    isLoading = false
                                )
                            }
                            return@launch
                        }
                    }
                    // Handle the Idle state if necessary.
                    Result.Idle -> Result.Idle
                }
            }
        }
    }
}


