package com.alexandros.p.gialamas.taxiapp.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> AutoCompleteTextField(
    modifier: Modifier = Modifier,
    text: String,
    options: Array<T>,
    onClearClicked: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onOptionSelected: (T) -> Unit,
    optionToString: (T) -> String = { it.name },
    keyboardController: SoftwareKeyboardController? = null,
    label: String,
    isValid: Boolean
) {

    var text1 by remember { mutableStateOf("") }
    var suggestionsVisible by remember { mutableStateOf(false) }
    val filteredOptions = remember(text) { options.filter { optionToString(it).contains(text, ignoreCase = true) } }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ExposedDropdownMenuBox(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp)),
            expanded = suggestionsVisible,
            onExpandedChange = {
                suggestionsVisible = it
            },
        ) {

            TextField(
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth(0.9f)
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryEditable,
                        enabled = text.isBlank()
                    )
                    .border(
                        width = 2.dp,
                        color = if (!isValid) Color.Red else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.DarkGray,
                    focusedLabelColor = Color.DarkGray,
                    focusedIndicatorColor = Color.Gray,
                    selectionColors = TextSelectionColors(
                        handleColor = Color.DarkGray,
                        backgroundColor = Color.Gray
                    )
                ),
                maxLines = 3,
                value = text,
                onValueChange = {
//                    text = it
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
//                                text = ""
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

                MaterialTheme(
                    shapes = Shapes(RoundedCornerShape(16.dp)),
                    content = {
                    ExposedDropdownMenu(
                        modifier = modifier
                            .background(Color.DarkGray)
                            .border(width = 1.dp, shape = RoundedCornerShape(16.dp), color = Color.LightGray),
                        expanded = suggestionsVisible,
                        onDismissRequest = { suggestionsVisible = false }
                    ) {
                        filteredOptions.forEach { option ->
                            DropdownMenuItem(
                                modifier = modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .drawBehind {
                                        val strokeWidth = 1.dp.toPx()
                                        val lineWidth = size.width * 0.7f
                                        drawLine(
                                            color = Color.LightGray,
                                            start = Offset(
                                                (size.width - lineWidth) / 2,
                                                size.height - strokeWidth / 2
                                            ),
                                            end = Offset(
                                                (size.width + lineWidth) / 2,
                                                size.height - strokeWidth / 2
                                            ),
                                            strokeWidth = strokeWidth
                                        )
                                    }
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
//                                    text =
                                        optionToString(option)
                                    suggestionsVisible = false
                                    onOptionSelected(option)
                                    keyboardController?.hide()
                                },
                                text = {
                                    Text(
                                        modifier = modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        text = optionToString(option),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                    )
                                }
                            )
                        }

                    }

                }
                )

            }
        }
    }
}



