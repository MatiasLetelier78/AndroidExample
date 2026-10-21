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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.androidexample.data.Model.Usuarios
import com.example.androidexample.ui.ui.components.CampoTexto
import com.example.androidexample.viewmodel.AppViewModelFactory
import com.example.androidexample.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: (Usuarios) -> Unit,
    onIrRegistro: () -> Unit,
    vm: AuthViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val s by vm.login.collectAsState()

    // Si el login fue exitoso, se limpia el formulario y se entrega el usuario
    LaunchedEffect(s.usuario) {
        s.usuario?.let { u ->
            vm.limpiarLogin()
            onLoginExitoso(u)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido/a", style = MaterialTheme.typography.headlineMedium)
        Text("Junta de Vecinos Población Zaror", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        CampoTexto(s.email, vm::onEmailLogin, "Correo", s.errorEmail, teclado = KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        CampoTexto(s.clave, vm::onClaveLogin, "Clave", s.errorClave, esClave = true)
        Spacer(Modifier.height(12.dp))

        AnimatedVisibility(visible = s.errorGeneral != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(Icons.Filled.Error, null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text(s.errorGeneral ?: "", color = MaterialTheme.colorScheme.error)
            }
        }

        Button(
            onClick = vm::iniciarSesion,
            enabled = !s.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (s.cargando) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            else Text("Ingresar")
        }

        TextButton(
            onClick = onIrRegistro,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}