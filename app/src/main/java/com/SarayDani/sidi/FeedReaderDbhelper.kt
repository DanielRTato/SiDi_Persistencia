package com.SarayDani.sidi

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${EstructuraBD.EntradaRecord.NOMBRE_TABLA} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION} INTEGER," +
            "${EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_FECHA} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${EstructuraBD.EntradaRecord.NOMBRE_TABLA}"

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1         // If you change the database schema, you must increment the database version.
        const val DATABASE_NAME = "Sidi.db"
    }
}