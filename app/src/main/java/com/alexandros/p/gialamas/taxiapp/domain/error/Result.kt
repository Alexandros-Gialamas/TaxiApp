package com.alexandros.p.gialamas.taxiapp.domain.error

typealias RootError = ResultError

sealed interface Result<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): Result<D, E>
    data class Error<out D, out E: RootError>(val error: E): Result<D, E>
    data object Loading : Result<Nothing, Nothing>

}

