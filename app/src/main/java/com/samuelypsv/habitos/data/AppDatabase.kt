package com.samuelypsv.habitos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Habito::class, Registro::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitoDao(): HabitoDao

    companion object {
        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "habitos.db").build()
    }
}
