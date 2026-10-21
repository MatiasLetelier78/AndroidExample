package com.example.androidexample.ui.ui.Screebs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidexample.ui.ui.components.CampoTexto
import com.example.androidexample.viewmodel.AppViewModelFactory
import com.example.androidexample.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistrado: () -> Unit,
    onIrLogin: () -> Unit,
    vm: AuthViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val s by vm.registro.collectAsState()

    // Cuando el registro sale bien, se limpia el formulario y se avisa a la navegación
    LaunchedEffect(s.registroExitoso) {
        if (s.registroExitoso) {
            vm.limpiarRegistro()
            onRegistrado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Text("Junta de Vecinos Población Zaror", style = MaterialTheme.typography.bodyMedium)

        CampoTexto(s.nombre, vm::onNombre, "Nombre", s.errorNombre)
        CampoTexto(s.email, vm::onEmailReg, "Correo", s.errorEmail, teclado = KeyboardType.Email)
        CampoTexto(s.clave, vm::onClaveReg, "Clave", s.errorClave, esClave = true)
        CampoTexto(s.confirmar, vm::onConfirmar, "Confirmar clave", s.errorConfirmar, esClave = true)

        Text("Perfil (solo para pruebas académicas)", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("VECINO", "SECRETARIA", "TESORERIA").forEach { rol ->
                FilterChip(
                    selected = s.rol == rol,
                    onClick = { vm.onRolReg(rol) },
                    label = { Text(rol.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        // Error general (por ejemplo, correo repetido), con animación
        AnimatedVisibility(visible = s.errorGeneral != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(s.errorGeneral ?: "")
                }
            }
        }

        Button(
            onClick = vm::registrar,
            enabled = !s.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (s.cargando) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            else Text("Registrarme")
        }

        TextButton(
            onClick = onIrLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}