package com.medina.juanantonio.domain.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            ALTER TABLE contracts ADD COLUMN withdrawalMonth TEXT
            """.trimIndent()
        )
    }
}
