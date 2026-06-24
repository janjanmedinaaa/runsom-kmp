package com.medina.juanantonio.presentation.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

object TimeUtils {

    fun formatRemainingTime(startMillis: Long, endMillis: Long): String {
        val diff = endMillis - startMillis
        if (diff <= 0) return "Expired"

        val duration = diff.milliseconds
        val days = duration.inWholeDays
        val hours = (duration - days.days).inWholeHours
        val minutes = (duration - days.days - hours.hours).inWholeMinutes

        val parts = buildList {
            if (days > 0) add("$days day${if (days > 1) "s" else ""}")
            if (hours > 0) add("$hours hour${if (hours > 1) "s" else ""}")
            if (minutes > 0) add("$minutes min${if (minutes > 1) "s" else ""}")
        }

        return parts.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "Expired"
    }

    fun getCurrentMonthYear(): String {
        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val month = now.monthNumber.toString().padStart(2, '0')
        val year = (now.year % 100).toString().padStart(2, '0')

        return "$month-$year"
    }
}
