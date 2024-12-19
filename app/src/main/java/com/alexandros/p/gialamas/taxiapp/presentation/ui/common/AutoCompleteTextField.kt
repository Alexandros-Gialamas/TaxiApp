package com.alexandros.p.gialamas.taxiapp.presentation.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextField(
    modifier: Modifier = Modifier,
    options: List<String>,
    onClearClicked : (String) -> Unit,
    onValueChange: (String) -> Unit,
    onOptionSelected: (String) -> Unit,
    keyboardController: SoftwareKeyboardController? = null,
    label: String,
    isValid: Boolean
) {

    var text by remember { mutableStateOf("") }
    var suggestionsVisible by remember { mutableStateOf(false) }
    val filteredOptions = remember(text) { options.filter { it.contains(text, ignoreCase = true) } }

    Column(
        modifier = modifier
            .fillMaxWidth(0.9f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ExposedDropdownMenuBox(
            expanded = suggestionsVisible,
            onExpandedChange = {
                suggestionsVisible = it
            },
        ) {

            TextField(
                modifier = modifier
                    .fillMaxWidth(0.9f)
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryEditable,
                        enabled = text.isBlank()
                    )
                    .border(
                        width = 1.dp,
                        color = if (!isValid) Color.Red else Color.Transparent
                    ),
                maxLines = 3,
                value = text,
                onValueChange = {
                    text = it
                    onValueChange(it)
                    suggestionsVisible = true
                },
                textStyle = TextStyle(
                    textAlign = TextAlign.Center
                ),
                label = { Text(label) },
                trailingIcon = {
                    if (text.isNotBlank()) {
                        IconButton(
                            onClick = {
                                text = ""
                                onClearClicked("")
                                suggestionsVisible = false
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = suggestionsVisible)
                    }
                },

                )

            if (suggestionsVisible && filteredOptions.isNotEmpty()) {

                ExposedDropdownMenu(
                    expanded = suggestionsVisible,
                    onDismissRequest = { suggestionsVisible = false }
                ) {
                    filteredOptions.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                text = option
                                suggestionsVisible = false
                                onOptionSelected(option)
                                keyboardController?.hide()
                            },
                            text = { Text(option) }
                        )
                    }

                }
            }
        }
    }
}



