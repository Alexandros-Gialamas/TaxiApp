package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandros.p.gialamas.taxiapp.data.repository.RideRepositoryImpl
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.usecase.ConfirmRideUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.GetRideHistoryUseCase
import com.alexandros.p.gialamas.taxiapp.domain.usecase.SaveRideUseCase
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
import javax.inject.Inject

@HiltViewModel
class RideConfirmViewModel @Inject constructor(
    private val confirmRideUseCase: ConfirmRideUseCase,
    private val saveRideUseCase: SaveRideUseCase,
    private val repositoryImpl: RideRepositoryImpl
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
        customerId: String,
        origin: String,
        destination: String,
        rideEstimate: RideEstimate
    ) {
        _uiState.update {
            it.copy(
                rideEstimate = rideEstimate,
                customerId = customerId,
                origin = origin,
                destination = destination
            )
        }
    }

    fun confirmRide(ride: Ride) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                _uiState.update { it.copy(isLoading = true) }
            }
            try {
                val isSuccess = confirmRideUseCase(ride = ride)
                if (isSuccess){
                    saveRideUseCase(ride)
                } else {
                        Unit
                }
                withContext(Dispatchers.Main){
                    _uiState.update {
                        it.copy(
                            rideEstimate = it.rideEstimate,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }


//    fun getRideHistory(
//        customerId: String,
//        driverId: Int? = null,
//    ){
//        viewModelScope.launch {
//            _uiState.update {
//                it.copy(isLoading = true)
//            }
//            try {
//                val rideHistory = driverId?.let {
//                    getRideHistoryUseCase(
//                        customerId = customerId,
//                        driverId = it
//                    )
//                }
//                _uiState.update {
//                    it.copy(
//                        rideEstimate = Result.Success(data = rideHistory),
//                        isLoading = false
//                    )
//                }
//
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        rideEstimate = Result.Error(RideHistoryError.Network.NETWORK_ERROR),
//                        isLoading = false
//                    )
//                }
//
//            }
//        }
//    }

}