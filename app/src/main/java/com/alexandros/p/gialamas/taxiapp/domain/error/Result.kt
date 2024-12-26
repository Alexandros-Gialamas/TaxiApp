
package com.alexandros.p.gialamas.taxiapp.domain.error


/**
 * A sealed interface representing the result of an operation.
 * It can be in one of three states: [Success], [Error], or [Idle].
 *
 * @param D The type of data held by a successful result.
 * @param E The type of error held by an error result. Must be a subtype of [ResultError].
 */
sealed interface Result<out D, out E: ResultError> {
    /**
     * Represents a successful result containing data of type [D].
     *
     * @property data The successful result data.
     */
    data class Success<out D>(val data: D): Result<D, Nothing>
    /**
     * Represents an error result containing an error of type [E].
     *
     * @property error The error object.
     */
    data class Error<out E: ResultError>(val error: E): Result<Nothing, E>
    /**
     * Represents an idle or initial state, indicating no result is available yet.
     */
    data object Idle: Result<Nothing, Nothing>
}

/**
 * Transforms the data of a [Success] result using the provided [mapData] function.
 * If the result is [Error] or [Idle], it remains unchanged.
 *
 * @param mapData A function that transforms data of type [T] to type [R].
 * @return A new [Result] with the transformed data if the original was [Success]; otherwise, the original [Result].
 */
inline fun <T, E: ResultError, R> Result<T, E>.onSuccessMapData(mapData: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(mapData(data))
        is Result.Idle -> Result.Idle
    }
}

/**
 * Applies the given [mapData] function to the data of a [Success] result and returns a new [Success] result with the modified data.
 * If the result is [Error] or [Idle], it remains unchanged.
 *
 * @param mapData A function that modifies data of type [T] and returns a new instance of type [T].
 * @return A new [Result] with the modified data if the original was [Success]; otherwise, the original [Result].
 */
inline fun <T, E : ResultError> Result<T, E>.onSuccessReturn(mapData: (T) -> T): Result<T, E> {
    return when (this) {
        is Result.Error -> this
        is Result.Success -> Result.Success(mapData(data))
        Result.Idle -> Result.Idle
    }
}


/**
 * Maps an [Error] result to a [Success] result with custom data provided by [mapError].
 * If the result is [Success] or [Idle], it remains unchanged.
 *
 * @param mapError A function that takes the [ResultError] of type [E] and returns data of type [T] to be used in the new [Success] result.
 * @return A [Success] result with the data from [mapError] if the original was [Error]; otherwise, the original [Result].
 */
inline fun <T, E : ResultError> Result<T, E>.onErrorReturn(mapError: (E) -> T): Result<T, E> {
    return when (this) {
        is Result.Error -> Result.Success(mapError(error))
        is Result.Success -> this
        Result.Idle -> Result.Idle
    }
}

/**
 * Executes an [action] if the result is a [Success].
 *
 * @param action The action to execute, receiving the success data of type [T].
 * @return The original [Result] to allow for chaining.
 */
inline fun <T, E: ResultError> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }

        is Result.Idle -> Result.Idle
    }
}

/**
 * Executes an [action] if the result is an [Error].
 *
 * @param action The action to execute, receiving the error object of type [E].
 * @return The original [Result] to allow for chaining.
 */
inline fun <T, E: ResultError> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> {
            action(error)
            this
        }
        is Result.Success -> this
        is Result.Idle -> Result.Idle
    }
}

/**
 * Transforms a [Result] into an [EmptyResult], discarding the success data.
 * Useful when only the success or failure of an operation is relevant, not the specific data.
 *
 * @return An [EmptyResult] representing the success or failure state.
 */
fun <T, E: ResultError> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return onSuccessMapData {  }
}

/**
 * A type alias for a [Result] where the success data is [Unit], indicating no specific data is carried.
 * Used for operations where only the success or error state matters.
 */
typealias EmptyResult<E> = Result<Unit, E>

/**
 * A sealed interface representing a generic error type for the [Result] interface.
 *
 * This interface serves as a base for defining specific error types that can be used with [Result].
 * It helps to constrain the error types that can be returned by operations using [Result].
 */
sealed interface ResultError