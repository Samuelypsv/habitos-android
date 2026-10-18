package com.samuelypsv.habitos.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "habitos")
data class Habito(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val emoji: String,
    val creadoEpochDay: Long,
)

/** Un día en que se cumplió un hábito. La fecha se guarda como LocalDate.toEpochDay(). */
@Entity(
    tableName = "registros",
    primaryKeys = ["habitoId", "epochDay"],
    foreignKeys = [
        ForeignKey(
            entity = Habito::class,
            parentColumns = ["id"],
            childColumns = ["habitoId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("habitoId")],
)
data class Registro(
    val habitoId: Long,
    val epochDay: Long,
)
