package com.example.androidexample.data.Local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.androidexample.data.Model.Reserva


@Dao
interface ReservaDao {
    @Insert
    suspend fun insertar(reserva: Reserva): Long

    @Query("SELECT * FROM reservas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Reserva>>

    @Query("SELECT * FROM reservas WHERE id = :id ORDER BY id DESC")
    fun obtenerPorId(id: Int): Flow<List<Reserva>>

    @Query("UPDATE reservas SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("DELETE FROM reservas WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Query("SELECT COUNT(*) FROM reservas WHERE fecha = :fecha AND estado = 'APROBADA'")
    suspend fun contarAprobadasEnFecha(fecha: String): Int

}