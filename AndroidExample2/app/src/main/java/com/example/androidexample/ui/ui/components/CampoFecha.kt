package com.example.androidexample.ui.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
    valor: String,
    etiqueta: String,
    error: String?,
    onFecha: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var abrir by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Box {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                label = { Text(etiqueta) },
                isError = error != null,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { abrir = true }) {
                        if (error != null)
                            Icon(Icons.Filled.Error, "Error", tint = MaterialTheme.colorScheme.error)
                        else
                            Icon(Icons.Filled.CalendarMonth, "Elegir fecha")
                    }
                }
            )
            // Capa invisible para permitir tocar cualquier parte del campo de texto y abrir el calendario
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { abrir = true }
            )
        }

        AnimatedVisibility(visible = error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

    if (abrir) {
        val estado = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { abrir = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { ms ->
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        onFecha(sdf.format(Date(ms)))
                    }
                    abrir = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { abrir = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estado)
        }
    }
}
