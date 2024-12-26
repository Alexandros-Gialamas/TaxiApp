package com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource


/**
 * A sealed class that encapsulates different types of UI text: either a dynamic string or a string resource.
 *
 * This class is useful for abstracting the source of text displayed in the UI, allowing you to handle
 * both dynamically generated strings and localized strings from resources in a unified way.
 */
sealed class UiText {
    /**
     * Represents a dynamically generated string value.
     *
     * @property value The string value.
     */
    data class DynamicString(val value: String): UiText()

    /**
     * Represents a string resource loaded from the application's resources.
     *
     * @property resId The resource ID of the string.
     * @property args Optional arguments to be formatted into the string resource, similar to `String.format()`.
     */
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ): UiText()

    /**
     * Resolves the [UiText] to a [String] using Compose's `stringResource` function.
     * This function is intended for use within Composable contexts.
     *
     * @return The resolved string.
     */
    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }

    /**
     * Resolves the [UiText] to a [String] using the provided Android [Context].
     * This function is suitable for use outside of Composable contexts.
     *
     * @param context The Android context used to resolve the string resource.
     * @return The resolved string.
     */
    fun asString(context: Context): String {
        return when(this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}