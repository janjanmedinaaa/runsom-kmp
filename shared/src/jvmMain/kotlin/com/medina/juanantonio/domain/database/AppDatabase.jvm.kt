package com.medina.juanantonio.domain.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.medina.juanantonio.domain.database.migrations.MIGRATION_2_3
import com.medina.juanantonio.domain.database.migrations.MIGRATION_3_4
import com.medina.juanantonio.domain.database.migrations.MIGRATION_4_5
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val appDir = File(
        System.getProperty("user.home"),
        ".runsom"
    ).apply {
        mkdirs()
    }

    val dbFile = File(appDir, "runsom.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    ).addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
}
