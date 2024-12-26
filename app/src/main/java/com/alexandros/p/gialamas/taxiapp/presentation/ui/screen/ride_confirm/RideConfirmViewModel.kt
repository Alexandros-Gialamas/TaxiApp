package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.mapper.toEntity
import com.alexandros.p.gialamas.taxiapp.data.mapper.toRide
import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.data.util.checkInternetConnectivity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_confirm.ValidateRideConfirmDriverDistance
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.error.onSuccess
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_confirm.ConfirmRideUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_confirm.SaveRideUseCase
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.asConfirmUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RideConfirmViewModel @Inject constructor(
    private val confirmRideUseCase: ConfirmRideUseCase,
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

    fun collectState(
        rideEstimate: RideEstimate,
        customerId: String,
        origin: String,
        destination: String
    ) {
        _uiState.update {
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

    fun collectRideOption(rideOption: RideOption) {
        _uiState.update {
            it.copy(
                rideOption = rideOption
            )
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
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

    private fun getRide(confirmRideRequest: ConfirmRideRequest): Ride {
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date()
            )
        return Ride(
            date = currentDate,
            customerId = confirmRideRequest.customerId,
            origin = confirmRideRequest.origin,
            destination = confirmRideRequest.destination,
            distance = confirmRideRequest.distance,
            duration = confirmRideRequest.duration,
            driver = confirmRideRequest.driver,
            value = confirmRideRequest.value
        )

    }


    fun confirmRide(context: Context) {

        clearError()

        if (!checkInternetConnectivity(context)) {
            _uiState.update {
                it.copy(
                    error = RideConfirmError.NetWork.NETWORK_ERROR.asConfirmUiText()
                )
            }
        } else {

            viewModelScope.launch(Dispatchers.IO) {
                if ((uiState.value.rideOption != null) && (uiState.value.rideEstimate != null)) {

                    withContext(Dispatchers.Main){
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }

                    try {
                        val rideRequest = rideRequest()

                        when (val isRideConfirmed = confirmRideUseCase(rideRequest)) {
                            is Result.Error -> {
                                withContext(Dispatchers.Main){
                                    _uiState.update {
                                        it.copy(
                                            error = isRideConfirmed.error.asConfirmUiText(),
                                            isRideConfirmed = false,
                                            isLoading = false
                                        )
                                    }
                                }

                            }
                            is Result.Success -> {
                                try {
                                    val newRide = rideRequest.toRide()
                                    newRide?.let { saveRideUseCase(it) }
                                    Timber.tag("viewModel").d("Saving ride: $newRide")

                                    withContext(Dispatchers.Main) {
                                        _uiState.update {
                                            it.copy(
                                                isRideConfirmed = true,
                                                isLoading = false
                                            )
                                        }
                                    }

                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        _uiState.update {
                                            it.copy(
                                                error = RideConfirmError.Local.FAILED_TO_SAVE_THE_RIDE.asConfirmUiText(),
                                                isRideConfirmed = false
                                            )
                                        }
                                    }
                                }



//                                when (isRideConfirmed.data) {
//                                    true -> {
//
//                                        RideConfirmError.NetWork.INVALID_DISTANCE.asConfirmUiText()
//                                    }

//                                    false -> {
//                                        saveRideUseCase.invoke(isRideConfirmed)
//                                        isRideConfirmed.data
//                                        val ride = getRide(rideRequest).toEntity()


//                                        rideRequest?.let {
//                                            saveRideUseCase(
//                                                RideEntity(
//                                                    date = ride.date,
//                                                    customerId = ride.customerId,
//                                                    origin = ride.origin,
//                                                    destination = ride.destination,
//                                                    distance = ride.distance,
//                                                    duration = ride.duration,
//                                                    driverId = ride.driverId,
//                                                    driverName = ride.driverName,
//                                                    value = ride.value
//                                                )
//                                            )
//                                        }
//                                            .runCatching {
//                                            withContext(Dispatchers.Main) {
//                                                _uiState.update {
//                                                    it.copy(
//                                                        error = RideConfirmError.Local.FAILED_TO_SAVE_THE_RIDE.asConfirmUiText()
//                                                    )
//                                                }
//                                            }
//                                        }



//                                        withContext(Dispatchers.Main) {
//                                            _uiState.update {
//                                                it.copy(
//                                                    isRideConfirmed = true
//                                                )
//                                            }
//                                        }
//
//                                    }
//                                }

//                                Result.Idle -> Result.Idle
                            }

                            Result.Idle -> Result.Idle
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    error = RideConfirmError.NetWork.UNKNOWN_ERROR.asConfirmUiText(),
                                    isRideConfirmed = false
                                )
                            }
                        }
                    }


                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                error = RideConfirmError.Local.INVALID_STATE_DATA.asConfirmUiText(),
                                isRideConfirmed = false,
                                restart = true
                            )
                        }
                    }
                }
            }
        }
    }
}