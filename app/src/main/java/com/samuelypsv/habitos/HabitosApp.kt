package com.samuelypsv.habitos

import android.app.Application
import com.samuelypsv.habitos.data.AppDatabase
import com.samuelypsv.habitos.data.HabitoRepository
import com.samuelypsv.habitos.data.RoomHabitoRepository

/** Contenedor de dependencias sencillo, sin framework de inyección. */
class HabitosApp : Application() {

    val repositorio: HabitoRepository by lazy {
        RoomHabitoRepository(AppDatabase.crear(this).habitoDao())
    }
}
