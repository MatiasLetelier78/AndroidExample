package com.example.androidexample.data.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservas")
data class Reserva(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val fecha: String,
    val horaInicio: String,
    val cantidadPersonas: Int,
    val motivo: String,
    val usaCocina: Boolean,
    val requireRecordatorio: Boolean,
    val estado: String = "PENDIENTE"
)