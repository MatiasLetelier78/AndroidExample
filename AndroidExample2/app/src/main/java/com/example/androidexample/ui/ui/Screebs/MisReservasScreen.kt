package com.example.androidexample.ui.ui.Screebs

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidexample.data.Model.Reserva
import com.example.androidexample.ui.ui.components.EstadoVacio
import com.example.androidexample.util.Sesion
import com.example.androidexample.viewmodel.AppViewModelFactory
import com.example.androidexample.viewmodel.ReservaViewModel

private enum class EstadoTab(val titulo: String, val estado: String) {
    PENDIENTES("Pendientes", "PENDIENTE"),
    APROBADAS("Aprobadas", "APROBADA"),
    RECHAZADAS("Rechazadas", "RECHAZADA")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    onVolver: () -> Unit,
    vm: ReservaViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val usuarioId = Sesion.usuario?.id ?: 0
    val flujo = remember(usuarioId) { vm.deUsuario(usuarioId) }
    val reservas by flujo.collectAsState(initial = emptyList())

    val tabs = EstadoTab.entries
    var tab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Mis reservas") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {

            PrimaryTabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { i, t ->
                    val cantidad = reservas.count { it.estado == t.estado }
                    Tab(
                        selected = tab == i,
                        onClick = { tab = i },
                        text = { Text("${t.titulo} ($cantidad)") }
                    )
                }
            }

            AnimatedContent(targetState = tab, label = "pestanas") { t ->
                val lista = reservas.filter { it.estado == tabs[t].estado }

                if (lista.isEmpty()) {
                    EstadoVacio(
                        icono = Icons.Filled.EventBusy,
                        mensaje = "No tienes reservas ${tabs[t].titulo.lowercase()}",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lista, key = { it.id }) { r ->
                            TarjetaMiReserva(r, Modifier.animateItem())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaMiReserva(r: Reserva, modifier: Modifier = Modifier) {
    val (icono, tinte) = when (r.estado) {
        "APROBADA" -> Icons.Filled.CheckCircle to MaterialTheme.colorScheme.primary
        "RECHAZADA" -> Icons.Filled.Cancel to MaterialTheme.colorScheme.error
        else -> Icons.Filled.HourglassEmpty to MaterialTheme.colorScheme.secondary
    }

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, contentDescription = r.estado, tint = tinte)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(r.fecha, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${r.horaInicio} · ${r.cantidadPersonas} personas",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(r.motivo, style = MaterialTheme.typography.bodyMedium)

            if (r.usaCocina || r.requireRecordatorio) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (r.usaCocina) AssistChip(onClick = {}, label = { Text("Cocina") })
                    if (r.requireRecordatorio) AssistChip(onClick = {}, label = { Text("Recordatorio") })
                }
            }
        }
    }
}