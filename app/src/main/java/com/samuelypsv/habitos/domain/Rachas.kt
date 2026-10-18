package com.samuelypsv.habitos.domain

import java.time.LocalDate

/**
 * Cálculos sobre los días en que se cumplió un hábito.
 * Es Kotlin puro (sin Android) para poder probarlo con tests unitarios normales.
 */
object Rachas {

    /**
     * Días seguidos cumplidos hasta hoy. Si hoy aún no se ha marcado,
     * la racha que acaba ayer sigue contando: el día no ha terminado.
     */
    fun actual(dias: Set<LocalDate>, hoy: LocalDate): Int {
        var dia = if (hoy in dias) hoy else hoy.minusDays(1)
        var racha = 0
        while (dia in dias) {
            racha++
            dia = dia.minusDays(1)
        }
        return racha
    }

    /** La racha más larga de la historia del hábito. */
    fun mejor(dias: Set<LocalDate>): Int {
        var mejor = 0
        var actual = 0
        var anterior: LocalDate? = null
        for (dia in dias.sorted()) {
            actual = if (anterior != null && anterior.plusDays(1) == dia) actual + 1 else 1
            mejor = maxOf(mejor, actual)
            anterior = dia
        }
        return mejor
    }

    /** Porcentaje (0-100) de días cumplidos en los últimos [ultimos] días, hoy incluido. */
    fun porcentaje(dias: Set<LocalDate>, hoy: LocalDate, ultimos: Int = 30): Int {
        require(ultimos > 0) { "ultimos debe ser mayor que 0" }
        val desde = hoy.minusDays(ultimos.toLong() - 1)
        val cumplidos = dias.count { !it.isBefore(desde) && !it.isAfter(hoy) }
        return cumplidos * 100 / ultimos
    }

    /** Los últimos [n] días, del más antiguo a hoy, indicando si se cumplieron. */
    fun ultimosDias(dias: Set<LocalDate>, hoy: LocalDate, n: Int = 7): List<Pair<LocalDate, Boolean>> =
        (n - 1 downTo 0).map { atras -> hoy.minusDays(atras.toLong()).let { it to (it in dias) } }
}
