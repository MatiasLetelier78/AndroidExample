package com.example.androidexample.data.Local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidexample.data.Model.Usuarios

@Database(
    entities = [Usuarios::class],
    version = 1,
    exportSchema = false
)

abstract class AppDataBase : RoomDatabase(){
    abstract fun usuariosDao(): UsuariosDao

    companion object {
        @Volatile private var INSTANCE: AppDataBase? = null

        fun get(context: Context): AppDataBase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "junta_vecinos.db"
                ).build().also {INSTANCE = it}
            }
    }
}