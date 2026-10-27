package com.example.androidexample.ui.ui.Screebs

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.androidexample.ui.ui.components.CampoFecha
import com.example.androidexample.ui.ui.components.CampoTexto
import com.example.androidexample.util.Sesion
import com.example.androidexample.viewmodel.AppViewModelFactory
import com.example.androidexample.viewmodel.ReservaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaReservaScreen(
    onVolver: () -> Unit,
    vm: ReservaViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val s by vm.form.collectAsState()
    val context = LocalContext.current

    // Cuando se guarda: avisa, limpia el formulario y vuelve al Home
    LaunchedEffect(s.guardada) {
        if (s.guardada) {
            Toast.makeText(context, "Solicitud enviada ✅", Toast.LENGTH_SHORT).show()
            vm.limpiar()
            onVolver()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Nueva reserva") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Seccion("¿Cuándo?") {
                // Date
                CampoFecha(s.fecha, "Fecha del arriendo", s.errorFecha, vm::onFecha)
                // String
                CampoTexto(s.hora, vm::onHora, "Hora de inicio (HH:mm)", s.errorHora)
            }

            Seccion("Detalles del arriendo") {
                // Int
                CampoTexto(
                    s.personas, vm::onPersonas, "Cantidad de personas", s.errorPersonas,
                    teclado = KeyboardType.Number
                )
                // String
                CampoTexto(s.motivo, vm::onMotivo, "Motivo del arriendo", s.errorMotivo)

                // Checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = s.usaCocina, onCheckedChange = vm::onUsaCocina)
                    Text("Usará la cocina")
                }

                // Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recordarme la reserva")
                    Switch(checked = s.recordatorio, onCheckedChange = vm::onRecordatorio)
                }
            }

            // Error general (por ejemplo, fecha ya ocupada)
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

            // Button
            Button(
                onClick = { Sesion.usuario?.let { vm.crear(it.id) } },
                enabled = !s.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (s.cargando) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Enviar solicitud")
                }
            }
        }
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            contenido()
        }
    }
}