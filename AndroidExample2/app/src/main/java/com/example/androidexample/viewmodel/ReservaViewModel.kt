package com.example.androidexample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidexample.data.Model.Reserva
import com.example.androidexample.data.Repository.ReservaRepository
import com.example.androidexample.util.Validadores
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReservaUiState(
    val fecha: String = "",
    val hora: String = "",
    val personas: String = "",
    val motivo: String = "",
    val usaCocina: Boolean = false,
    val recordatorio: Boolean = true,
    val errorFecha: String? = null,
    val errorHora: String? = null,
    val errorPersonas: String? = null,
    val errorMotivo: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val guardada: Boolean = false
)

class ReservaViewModel(private val repo: ReservaRepository) : ViewModel() {
    private val _form = MutableStateFlow(ReservaUiState())
    val form = _form.asStateFlow()

    val todas = repo.todas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deUsuario(id: Int): Flow<List<Reserva>> = repo.deUsuario(id)

    fun onFecha(v: String) = _form.update {
        it.copy(fecha = v, errorFecha = Validadores.fecha(v))
    }
    fun onHora(v: String) = _form.update {
        it.copy(hora = v, errorHora = Validadores.hora(v))
    }
    fun onPersonas(v: String) = _form.update {
        it.copy(personas = v, errorPersonas = Validadores.cantidadPersonas(v))
    }
    fun onMotivo(v: String) = _form.update {
        it.copy(motivo = v, errorMotivo = Validadores.motivo(v))
    }
    fun onUsaCocina(v: Boolean) = _form.update {
        it.copy(usaCocina = v)
    }
    fun onRecordatorio(v: Boolean) = _form.update {
        it.copy(recordatorio = v)
    }

    fun crear(usuarioId: Int) {
        val s = _form.value
        val eF = Validadores.fecha(s.fecha)
        val eH = Validadores.hora(s.hora)
        val eP = Validadores.cantidadPersonas(s.personas)
        val eM = Validadores.motivo(s.motivo)
        if (listOf(eF, eH, eP, eM).any { it != null }) {
            _form.update {
                it.copy(errorFecha = eF, errorHora = eH, errorPersonas = eP, errorMotivo = eM)
            }
            return
        }
        viewModelScope.launch {
            _form.update { it.copy(cargando = true, errorGeneral = null) }
            if (repo.fechaOcupada(s.fecha)) {
                _form.update {
                    it.copy(
                        cargando = false,
                        errorGeneral = "Esa fecha ya tiene una reserva aprobada"
                    )
                }
                return@launch
            }

            repo.crear(
                Reserva(
                    usuarioId = usuarioId,
                    fecha = s.fecha,
                    horaInicio = s.hora,
                    cantidadPersonas = s.personas.toInt(),
                    motivo = s.motivo.trim(),
                    usaCocina = s.usaCocina,
                    requireRecordatorio = s.recordatorio
                )
            )
            _form.update { it.copy(cargando = false, guardada = true) }
        }

    }
    fun limpiar() { _form.value = ReservaUiState() }

    fun aprobar(reserva: Reserva, onResultado: (aprobada: Boolean) -> Unit) {
        viewModelScope.launch {
            if (repo.fechaOcupada(reserva.fecha)) onResultado(false)
            else { repo.aprobar(reserva.id); onResultado(true) }
        }
    }

    fun rechazar(reserva: Reserva) {
        viewModelScope.launch { repo.rechazar(reserva.id) }
    }
}