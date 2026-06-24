package com.medina.juanantonio.presentation.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun String.formatAmount(): String {
    val number = this.toDoubleOrNull() ?: return this

    val rounded = kotlin.math.round(number * 100) / 100

    val whole = rounded.toLong()
    val decimal = ((rounded - whole) * 100)
        .toInt()
        .toString()
        .padStart(2, '0')

    val formattedWhole = whole
        .toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()

    return "$formattedWhole.$decimal"
}

fun String.toFormattedDateTime(): String {
    val millis = toLongOrNull() ?: return this

    val dateTime = Instant
        .fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val month = dateTime.month.name
        .lowercase()
        .replaceFirstChar { it.uppercase() }
        .take(3)

    val hour12 = when {
        dateTime.hour == 0 -> 12
        dateTime.hour > 12 -> dateTime.hour - 12
        else -> dateTime.hour
    }

    val amPm = if (dateTime.hour < 12) "AM" else "PM"

    return "$month ${dateTime.dayOfMonth}, ${dateTime.year} " +
            "$hour12:${dateTime.minute.toString().padStart(2, '0')} $amPm"
}

private val months = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

fun String.formatDateTime(): String {
    if (this.isBlank()) return ""
    val instant = Instant.parse(this)
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val monthName = months[local.monthNumber - 1]
    val day = local.dayOfMonth
    val year = local.year

    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')

    return "$monthName $day, $year $hour:$minute"
}
