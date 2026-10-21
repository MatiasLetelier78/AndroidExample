package com.example.androidexample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidexample.data.Model.Movimiento
import com.example.androidexample.data.Repository.MovimientoRepo
import com.example.androidexample.util.Validadores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



data class MovimientoUiState(
    val tipo: String = "INGRESO",
    val descripcion: String = "",
    val monto: String = "",
    val fecha: String = "",
    val comprobanteUri: String? = null,
    val errorDescripcion: String? = null,
    val errorMonto: String? = null,
    val errorFecha: String? = null,
    val guardado: Boolean = false
)

class FinanzasViewModel(private val repo: MovimientoRepo) : ViewModel() {
    private val _form = MutableStateFlow(MovimientoUiState())
    val form = _form.asStateFlow()

    // Buscador: la lista se filtra mientras el usuario escribe
    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()
    fun onBusqueda(v: String) { _busqueda.value = v }

    val movimientos = combine(repo.todos, _busqueda) { lista, q ->
        if (q.isBlank()) lista else lista.filter { it.descripcion.contains(q, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ingresos = repo.totalIngresos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val gastos = repo.totalGastos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val saldo = combine(ingresos, gastos) { i, g -> i - g }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun onTipo(v: String) = _form.update { it.copy(tipo = v) }
    fun onDescripcion(v: String) = _form.update { it.copy(descripcion = v, errorDescripcion = Validadores.descripcion(v)) }
    fun onMonto(v: String) = _form.update { it.copy(monto = v, errorMonto = Validadores.monto(v)) }
    fun onFecha(v: String) = _form.update { it.copy(fecha = v, errorFecha = Validadores.fechaMovimiento(v)) }
    fun onComprobante(uri: String?) = _form.update { it.copy(comprobanteUri = uri) }

    fun guardar() {
        val s = _form.value
        val eD = Validadores.descripcion(s.descripcion)
        val eM = Validadores.monto(s.monto)
        val eF = Validadores.fechaMovimiento(s.fecha)

        if (listOf(eD, eM, eF).any { it != null }) {
            _form.update { it.copy(errorDescripcion = eD, errorMonto = eM, errorFecha = eF) }
            return
        }
        viewModelScope.launch {
            repo.registrar(
                Movimiento(
                    tipo = s.tipo,
                    descripcion = s.descripcion.trim(),
                    monto = s.monto.toInt(),
                    fecha = s.fecha,
                    comprobante = s.comprobanteUri
                )
            )
            _form.update { it.copy(guardado = true) }
        }
    }

    fun limpiar() { _form.value = MovimientoUiState() }
}