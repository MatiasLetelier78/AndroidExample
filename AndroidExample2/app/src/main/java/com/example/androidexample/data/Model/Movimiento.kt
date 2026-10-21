package com.example.androidexample.data.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val tipo: String,
    val descripcion: String,
    val monto: Int,
    val fecha: String,
    val comprobante: String? = null
)