package com.samuelypsv.habitos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitoDao {

    @Query("SELECT * FROM habitos ORDER BY id")
    fun observarHabitos(): Flow<List<Habito>>

    @Query("SELECT * FROM registros")
    fun observarRegistros(): Flow<List<Registro>>

    @Insert
    suspend fun insertar(habito: Habito): Long

    @Delete
    suspend fun borrar(habito: Habito)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun marcar(registro: Registro)

    @Delete
    suspend fun desmarcar(registro: Registro)
}
