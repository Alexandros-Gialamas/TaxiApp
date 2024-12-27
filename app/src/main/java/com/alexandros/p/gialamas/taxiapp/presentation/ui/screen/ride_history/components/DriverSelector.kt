package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.RideHistoryState
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Driver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverSelector(
    modifier: Modifier = Modifier,
    uiState: RideHistoryState,
    keyboardController: SoftwareKeyboardController? = null,
    onExpandedChange: (Boolean) -> Unit,
    onDismiss: (Boolean) -> Unit,
    onDriverSelected: (Driver) -> Unit
) {

    ExposedDropdownMenuBox(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth(0.9f),
        expanded = uiState.isDriverMenuExpanded,
        onExpandedChange = {
            onExpandedChange(it)
        },
    ) {

        TextField(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .menuAnchor(
                    type = MenuAnchorType.PrimaryEditable,
                ),
            readOnly = true,
            value = uiState.driverName,
            onValueChange = {},
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            ),
            label = { Text("Select Driver") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isDriverMenuExpanded)
            }
        )
        MaterialTheme(
            shapes = Shapes(RoundedCornerShape(16.dp)),
            content = {
                ExposedDropdownMenu(
                    modifier = modifier
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(16.dp),
                            color = Color.LightGray
                        ),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Color.DarkGray,
                    expanded = uiState.isDriverMenuExpanded,
                    onDismissRequest = { onDismiss(false) }
                ) {
                    Driver.entries.forEach { driver ->
                        DropdownMenuItem(
                            modifier = modifier
                                .clip(RoundedCornerShape(16.dp))
                                .fillMaxWidth()
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
                                },
                            onClick = {
                                onDriverSelected(driver)
                                onDismiss(false)
                                keyboardController?.hide()
                            },
                            text = {
                                Text(
                                    modifier = modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    text = driver.driverName,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                        )
                    }
                }
            }
        )
    }
}