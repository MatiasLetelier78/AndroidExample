package com.example.androidexample.data.Repository

import com.example.androidexample.data.Local.ReservaDao
import com.example.androidexample.data.Model.Reserva

class ReservaRepository (private val dao: ReservaDao){
    val todas = dao.obtenerTodas()

    fun deUsuario(id: Int) = dao.obtenerPorId(id)
    suspend fun crear(reserva: Reserva) = dao.insertar(reserva)
    suspend fun aprobar(id: Int) = dao.actualizarEstado(id, "APROBADA")
    suspend fun rechazar(id: Int) = dao.actualizarEstado(id, "RECHAZADA")
    suspend fun fechaOcupada(fecha: String)= dao.contarAprobadasEnFecha(fecha)>0

}