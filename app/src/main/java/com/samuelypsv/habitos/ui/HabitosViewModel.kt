package com.samuelypsv.habitos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.samuelypsv.habitos.data.Habito
import com.samuelypsv.habitos.data.HabitoRepository
import com.samuelypsv.habitos.domain.Rachas
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HabitoUi(
    val habito: Habito,
    val hechoHoy: Boolean,
    val racha: Int,
    val mejorRacha: Int,
    val porcentajeMes: Int,
    val semana: List<Pair<LocalDate, Boolean>>,
)

data class HabitosEstado(
    val cargando: Boolean = true,
    val habitos: List<HabitoUi> = emptyList(),
) {
    val hechosHoy: Int get() = habitos.count { it.hechoHoy }
}

class HabitosViewModel(
    private val repositorio: HabitoRepository,
    private val hoy: () -> LocalDate = LocalDate::now,
) : ViewModel() {

    val estado: StateFlow<HabitosEstado> = repositorio.observar()
        .map { lista ->
            val fecha = hoy()
            HabitosEstado(
                cargando = false,
                habitos = lista.map { (habito, dias) ->
                    HabitoUi(
                        habito = habito,
                        hechoHoy = fecha in dias,
                        racha = Rachas.actual(dias, fecha),
                        mejorRacha = Rachas.mejor(dias),
                        porcentajeMes = Rachas.porcentaje(dias, fecha, 30),
                        semana = Rachas.ultimosDias(dias, fecha, 7),
                    )
                },
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitosEstado())

    fun crear(nombre: String, emoji: String) {
        val limpio = nombre.trim()
        if (limpio.isEmpty()) return
        viewModelScope.launch { repositorio.crear(limpio, emoji, hoy()) }
    }

    fun alternar(habito: Habito, dia: LocalDate, cumplido: Boolean) {
        if (dia.isAfter(hoy())) return
        viewModelScope.launch { repositorio.alternar(habito, dia, cumplido) }
    }

    fun borrar(habito: Habito) {
        viewModelScope.launch { repositorio.borrar(habito) }
    }

    companion object {
        fun factory(repositorio: HabitoRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HabitosViewModel(repositorio) }
        }
    }
}
