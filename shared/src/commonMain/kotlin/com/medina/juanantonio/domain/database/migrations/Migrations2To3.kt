package com.medina.juanantonio.domain.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            ALTER TABLE contracts ADD COLUMN title TEXT NOT NULL DEFAULT ''
            """.trimIndent()
        )

        connection.execSQL(
            """
            ALTER TABLE contracts ADD COLUMN maximumActivities INTEGER NOT NULL DEFAULT -1
            """.trimIndent()
        )

        connection.execSQL(
            """
            ALTER TABLE contracts ADD COLUMN minimumDistanceKm INTEGER NOT NULL DEFAULT 1
            """.trimIndent()
        )
    }
}