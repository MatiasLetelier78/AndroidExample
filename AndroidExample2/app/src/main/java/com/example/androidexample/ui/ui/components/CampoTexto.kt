package com.example.androidexample.ui.ui.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidexample.ui.theme.AndroidExampleTheme

@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    esClave: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text
) {
    var verClave by remember { mutableStateOf(false) }

    Column(modifier) {
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            label = { Text(etiqueta) },
            isError = error != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (esClave) KeyboardType.Password else teclado
            ),
            visualTransformation = if (esClave && !verClave) PasswordVisualTransformation()
            else VisualTransformation.None,
            trailingIcon = {
                when {
                    // Si hay error, se muestra el ícono rojo
                    error != null -> Icon(
                        Icons.Filled.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    // Si es clave y no hay error, el ojito para mostrar u ocultar
                    esClave -> IconButton(onClick = { verClave = !verClave }) {
                        Icon(
                            if (verClave) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Mostrar u ocultar clave"
                        )
                    }
                }
            }
        )

        // El mensaje aparece y desaparece con animación
        AnimatedVisibility(visible = error != null) {
            Text(
                text = error ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CampoTextoPreview() {
    AndroidExampleTheme {
        Column(Modifier.padding(16.dp)) {
            CampoTexto("", {}, "Correo", "Correo no válido")
        }
    }
}