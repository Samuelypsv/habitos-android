package com.samuelypsv.habitos.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

/** Un hábito junto con todos los días en que se ha cumplido. */
data class HabitoConDias(val habito: Habito, val dias: Set<LocalDate>)

interface HabitoRepository {
    fun observar(): Flow<List<HabitoConDias>>
    suspend fun crear(nombre: String, emoji: String, hoy: LocalDate)
    suspend fun borrar(habito: Habito)
    suspend fun alternar(habito: Habito, dia: LocalDate, cumplido: Boolean)
}

class RoomHabitoRepository(private val dao: HabitoDao) : HabitoRepository {

    override fun observar(): Flow<List<HabitoConDias>> =
        combine(dao.observarHabitos(), dao.observarRegistros()) { habitos, registros ->
            val porHabito = registros.groupBy({ it.habitoId }, { LocalDate.ofEpochDay(it.epochDay) })
            habitos.map { HabitoConDias(it, porHabito[it.id].orEmpty().toSet()) }
        }

    override suspend fun crear(nombre: String, emoji: String, hoy: LocalDate) {
        dao.insertar(Habito(nombre = nombre, emoji = emoji, creadoEpochDay = hoy.toEpochDay()))
    }

    override suspend fun borrar(habito: Habito) = dao.borrar(habito)

    override suspend fun alternar(habito: Habito, dia: LocalDate, cumplido: Boolean) {
        val registro = Registro(habito.id, dia.toEpochDay())
        if (cumplido) dao.marcar(registro) else dao.desmarcar(registro)
    }
}
