package com.example.androidexample.data.Repository

import com.example.androidexample.data.Local.MovimientoDao
import com.example.androidexample.data.Model.Movimiento

class MovimientoRepo (private val dao: MovimientoDao){
    val todos = dao.observarTodos()
    val totalIngresos = dao.observarTotal("INGRESO")
    val totalGastos = dao.observarTotal("GASTO")
    suspend fun registrar(m: Movimiento) = dao.insertar(m)
}