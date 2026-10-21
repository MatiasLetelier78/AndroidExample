package com.example.androidexample.data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidexample.data.Model.Movimiento
import com.example.androidexample.data.Model.Reserva
import com.example.androidexample.data.Model.Usuarios

@Database(
    entities = [Usuarios::class, Reserva::class, Movimiento::class],
    version = 2,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun usuariosDao(): UsuariosDao
    abstract fun reservaDao(): ReservaDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile private var INSTANCE: AppDataBase? = null

        fun get(context: Context): AppDataBase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "junta_vecinos.db"
                ).fallbackToDestructiveMigration()
                 .build().also { INSTANCE = it }
            }
    }
}
