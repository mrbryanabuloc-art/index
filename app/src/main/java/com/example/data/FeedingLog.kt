package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FeedingType(val displayName: String, val iconEmoji: String) {
    BOTTLE_FORMULA("Formula", "🍼"),
    BOTTLE_BREAST_MILK("Breast Milk", "🍼"),
    NURSING("Nursing", "🤱"),
    SOLID("Solids", "🥣"),
    DIAPER("Diaper Change", "👶")
}

enum class DiaperStatus(val displayName: String, val iconEmoji: String) {
    NONE("None", ""),
    PEE("Pee", "💧"),
    POOP("Poop", "💩"),
    BOTH("Pee & Poop", "💧💩"),
    DRY("Dry", "✨")
}

enum class NursingSide(val displayName: String) {
    LEFT("Left"),
    RIGHT("Right"),
    BOTH("Both")
}

enum class VolumeUnit(val label: String) {
    ML("ml"),
    OZ("oz")
}

@Entity(tableName = "feeding_logs")
data class FeedingLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val amountMl: Float = 0f,
    val feedingType: String = FeedingType.BOTTLE_FORMULA.name,
    val durationMinutes: Int = 0,
    val nursingSide: String = "",
    val diaperStatus: String = DiaperStatus.NONE.name,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun hasDiaperChange(): Boolean = diaperStatus.isNotEmpty() && diaperStatus != DiaperStatus.NONE.name

    fun getDiaperBadge(): String? {
        return when (diaperStatus) {
            DiaperStatus.PEE.name -> "💧 Pee"
            DiaperStatus.POOP.name -> "💩 Poop"
            DiaperStatus.BOTH.name -> "💧💩 Both"
            DiaperStatus.DRY.name -> "✨ Dry"
            else -> null
        }
    }

    fun getFormattedAmount(unit: VolumeUnit): String {
        if (feedingType == FeedingType.DIAPER.name) {
            return getDiaperBadge() ?: "Diaper Change"
        }
        return when (unit) {
            VolumeUnit.ML -> {
                if (amountMl > 0f) "${amountMl.toInt()} ml" else if (durationMinutes > 0) "${durationMinutes}m" else "Fed"
            }
            VolumeUnit.OZ -> {
                if (amountMl > 0f) {
                    val oz = amountMl / 29.5735f
                    val rounded = (Math.round(oz * 10) / 10.0)
                    "${rounded} oz (${amountMl.toInt()} ml)"
                } else if (durationMinutes > 0) {
                    "${durationMinutes}m"
                } else {
                    "Fed"
                }
            }
        }
    }

    fun getShortAmount(unit: VolumeUnit): String {
        if (feedingType == FeedingType.DIAPER.name) {
            return getDiaperBadge() ?: "Diaper"
        }
        return when (unit) {
            VolumeUnit.ML -> {
                if (amountMl > 0f) "${amountMl.toInt()} ml" else if (durationMinutes > 0) "${durationMinutes}m" else "-"
            }
            VolumeUnit.OZ -> {
                if (amountMl > 0f) {
                    val oz = amountMl / 29.5735f
                    val rounded = (Math.round(oz * 10) / 10.0)
                    "${rounded} oz"
                } else if (durationMinutes > 0) {
                    "${durationMinutes}m"
                } else {
                    "-"
                }
            }
        }
    }
}
