package com.alexandros.p.gialamas.taxiapp.presentation.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp

@Composable
fun AutoCompleteTextField(
    modifier: Modifier = Modifier,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    keyboardController: SoftwareKeyboardController? = null,
    label: String
) {

    var text by remember { mutableStateOf("") }
    var suggestionsVisible by remember { mutableStateOf(false) }
    val filteredOptions = options.filter { it.contains(text, ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(0.9f),
            maxLines = 3,
            trailingIcon = {
                if (text.isNotBlank()) {
                    IconButton(
                        onClick = {
                            text = ""
                            suggestionsVisible = false
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
            }
            },
            value = text,
            onValueChange = {
                text = it
                suggestionsVisible = true
            },
            label = { Text(label) },
        )

        if (suggestionsVisible && filteredOptions.isNotEmpty()) {
//            LazyColumn (
//                modifier = modifier
//                    .fillMaxWidth()
//                    .height(IntrinsicSize.Min)
//                    .padding(8.dp)
//            ){
//                items(filteredOptions) { option ->
//                    Text(
//                        modifier = modifier
//                            .padding(vertical = 6.dp)
//                            .border(
//                                width = 1.dp,
//                                color = Color.Black,
//                                shape = RectangleShape
//                            )
//                            .clickable {
//                                text = option
//                                suggestionsVisible = false
//                                onOptionSelected(option)
//                                keyboardController?.hide()
//                            }
//                            .padding(8.dp),
//                        text = option,
//                    )
//                }
//            }
            Column (
                modifier = modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                filteredOptions.forEach { option ->
                    Text(
                        modifier = modifier
                            .padding(vertical = 6.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RectangleShape
                            )
                            .clickable {
                                text = option
                                suggestionsVisible = false
                                onOptionSelected(option)
                                keyboardController?.hide()
                            }
                            .padding(8.dp),
                        text = option,
                    )
                }
            }
        }
    }

}