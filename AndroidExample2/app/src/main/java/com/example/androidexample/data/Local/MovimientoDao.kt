package com.example.androidexample.data.Local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import com.example.androidexample.data.Model.Movimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Insert
    suspend fun  insertar(movimiento: Movimiento): Long

    @Query("SELECT * FROM movimientos ORDER BY id DESC")
    fun observarTodos(): Flow<List<Movimiento>>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM movimientos WHERE tipo = :tipo")
    fun observarTotal(tipo: String): Flow<Int>

}