package com.example.androidexample.ui.ui.Screebs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidexample.data.Model.Movimiento
import com.example.androidexample.ui.ui.components.CampoTexto
import com.example.androidexample.ui.ui.components.EstadoVacio
import com.example.androidexample.ui.ui.components.ImagenUri
import com.example.androidexample.viewmodel.AppViewModelFactory
import com.example.androidexample.viewmodel.FinanzasViewModel
import java.text.NumberFormat
import java.util.Locale

private enum class FiltroMov(val titulo: String, val tipo: String?) {
    TODOS("Todos", null),
    INGRESOS("Ingresos", "INGRESO"),
    GASTOS("Gastos", "GASTO")
}

private fun pesos(valor: Int): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(valor)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparenciaScreen(
    onVolver: () -> Unit,
    vm: FinanzasViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val ingresos by vm.ingresos.collectAsState()
    val gastos by vm.gastos.collectAsState()
    val saldo by vm.saldo.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val movimientos by vm.movimientos.collectAsState()   // ya viene filtrada por el buscador

    val filtros = FiltroMov.entries
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var boleta by remember { mutableStateOf<Movimiento?>(null) }

    val lista = movimientos.filter { filtros[tab].tipo == null || it.tipo == filtros[tab].tipo }

    // Barra animada: qué parte de los ingresos se ha gastado
    val proporcion = if (ingresos > 0) (gastos.toFloat() / ingresos).coerceIn(0f, 1f) else 0f
    val proporcionAnimada by animateFloatAsState(proporcion, tween(600), label = "proporcion")

    // Diálogo con la foto de la boleta
    boleta?.let { m ->
        AlertDialog(
            onDismissRequest = { boleta = null },
            icon = { Icon(Icons.Filled.Receipt, contentDescription = null) },
            title = { Text(m.descripcion) },
            text = {
                ImagenUri(
                    uri = m.comprobante ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            },
            confirmButton = { TextButton(onClick = { boleta = null }) { Text("Cerrar") } }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Transparencia") },
                windowInsets = WindowInsets(0, 0, 0, 0),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Resumen
            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Saldo disponible", style = MaterialTheme.typography.labelLarge)
                        Text(pesos(saldo), style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Ingresos", style = MaterialTheme.typography.labelMedium)
                                Text(pesos(ingresos), style = MaterialTheme.typography.titleMedium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Gastos", style = MaterialTheme.typography.labelMedium)
                                Text(pesos(gastos), style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Gastos: ${(proporcion * 100).toInt()}% de los ingresos",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { proporcionAnimada },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Filtro dinámico: la lista se filtra mientras escribes
            item { CampoTexto(busqueda, vm::onBusqueda, "Buscar movimiento", null) }

            // Pestañas con contador (mismo estilo que Gestión de reservas)
            item {
                PrimaryTabRow(selectedTabIndex = tab) {
                    filtros.forEachIndexed { i, f ->
                        val cantidad = movimientos.count { f.tipo == null || it.tipo == f.tipo }
                        Tab(
                            selected = tab == i,
                            onClick = { tab = i },
                            text = { Text("${f.titulo} ($cantidad)") }
                        )
                    }
                }
            }

            if (lista.isEmpty()) {
                item {
                    EstadoVacio(
                        icono = Icons.Filled.SearchOff,
                        mensaje = if (busqueda.isBlank()) "No hay movimientos ${filtros[tab].titulo.lowercase()}"
                        else "Sin resultados para \"$busqueda\"",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(lista, key = { it.id }) { m ->
                    TarjetaMovimiento(
                        m = m,
                        onVerBoleta = { boleta = m },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaMovimiento(
    m: Movimiento,
    onVerBoleta: () -> Unit,
    modifier: Modifier = Modifier
) {
    val esIngreso = m.tipo == "INGRESO"
    val tinte = if (esIngreso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (esIngreso) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    contentDescription = if (esIngreso) "Ingreso" else "Gasto",
                    tint = tinte
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(m.descripcion, style = MaterialTheme.typography.titleMedium)
                    Text(m.fecha, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    (if (esIngreso) "+" else "-") + pesos(m.monto),
                    style = MaterialTheme.typography.titleMedium,
                    color = tinte
                )
            }

            if (m.comprobante != null) {
                AssistChip(
                    onClick = onVerBoleta,
                    label = { Text("Ver boleta") },
                    leadingIcon = { Icon(Icons.Filled.Receipt, contentDescription = null) }
                )
            }
        }
    }
}