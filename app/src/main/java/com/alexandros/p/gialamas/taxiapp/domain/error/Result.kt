package com.alexandros.p.gialamas.taxiapp.domain.error



sealed interface Result<out D, out E: ResultError> {
    data class Success<out D, out E: ResultError>(val data: D): Result<D, E>
    data class Error<out D, out E: ResultError>(val error: E): Result<D, E>
    data object Idle: Result<Nothing, Nothing>
}



