# SQLite en Android - Guia para PDMD DAM2

## Indice
1. [Que es SQLite](#1-que-es-sqlite)
2. [Estructura del proyecto](#2-estructura-del-proyecto)
3. [Donde se guarda la base de datos](#3-donde-se-guarda-la-base-de-datos)
4. [Componentes principales](#4-componentes-principales)
5. [Operaciones CRUD basicas](#5-operaciones-crud-basicas)
6. [Ejemplo avanzado: Dos tablas (Usuarios y Records)](#6-ejemplo-avanzado-dos-tablas-usuarios-y-records)
7. [Interfaz extendida con multiples operaciones](#7-interfaz-extendida-con-multiples-operaciones)
8. [Controlador completo con todas las operaciones](#8-controlador-completo-con-todas-las-operaciones)
9. [Verificar datos en la base de datos](#9-verificar-datos-en-la-base-de-datos)

---

## 1. Que es SQLite

SQLite es un motor de base de datos **relacional** integrado en Android. A diferencia de SharedPreferences:
- Permite almacenar datos **estructurados en tablas**
- Soporta **consultas SQL** complejas
- Permite **relaciones** entre tablas
- Ideal para **multiples registros**

### Caracteristicas:
| Caracteristica | Descripcion |
|----------------|-------------|
| Tipo | Base de datos relacional |
| Almacenamiento | Archivo `.db` en el dispositivo |
| Consultas | SQL estandar |
| Transacciones | Soportadas (ACID) |
| Tamaño maximo | Limitado por almacenamiento del dispositivo |

---

## 2. Estructura del proyecto

```
com.SarayDani.sidi/
├── EstructuraBD.kt          // Define estructura de tablas (contrato)
├── FeedReaderDbHelper.kt    // Crea y gestiona la BD (SQLiteOpenHelper)
├── ControladorSQLite.kt     // Operaciones CRUD (implementa interfaz)
├── GuardarCargarRecord.kt   // Interfaz del repositorio
├── RecordJuego.kt           // Modelo de datos
└── MyViewModel.kt           // Usa el controlador
```

### Arquitectura
```
UI (Compose) --> ViewModel --> Interfaz --> ControladorSQLite --> SQLiteOpenHelper --> BD
                                  |                                      |
                        GuardarCargarRecord                      FeedReaderDbHelper
```

---

## 3. Donde se guarda la base de datos

### Ruta del archivo
```
/data/data/<paquete>/databases/<nombre>.db
```

En nuestro proyecto:
```
/data/data/com.SarayDani.sidi/databases/Sidi.db
```

### Como ver la base de datos en Android Studio

1. **Abrir App Inspection:**
   - `View` > `Tool Windows` > `App Inspection`
   - Seleccionar la pestaña `Database Inspector`

2. **O usar Device File Explorer:**
   - `View` > `Tool Windows` > `Device File Explorer`
   - Navegar a: `data/data/com.SarayDani.sidi/databases/`
   - Exportar `Sidi.db` y abrirlo con DB Browser for SQLite

---

## 4. Componentes principales

### 4.1 EstructuraBD.kt - Contrato de la base de datos

Define las constantes de las tablas y columnas:

```kotlin
package com.SarayDani.sidi

import android.provider.BaseColumns

object EstructuraBD {
    // Tabla de records
    object EntradaRecord : BaseColumns {
        const val NOMBRE_TABLA = "records"
        const val NOMBRE_COLUMNA_PUNTUACION = "puntuacion"
        const val NOMBRE_COLUMNA_FECHA = "fecha"
    }
}
```

**Nota:** `BaseColumns` proporciona automaticamente la columna `_ID` que es el identificador unico.

---

### 4.2 FeedReaderDbHelper.kt - SQLiteOpenHelper

Gestiona la creacion y actualizacion de la base de datos:

```kotlin
package com.SarayDani.sidi

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

// Sentencia SQL para crear la tabla
private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${EstructuraBD.EntradaRecord.NOMBRE_TABLA} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION} INTEGER," +
            "${EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_FECHA} TEXT)"

// Sentencia SQL para eliminar la tabla
private const val SQL_DELETE_ENTRIES =
    "DROP TABLE IF EXISTS ${EstructuraBD.EntradaRecord.NOMBRE_TABLA}"

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    // Se llama cuando la BD se crea por primera vez
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    // Se llama cuando se incrementa DATABASE_VERSION
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)  // Elimina tabla antigua
        onCreate(db)                     // Crea tabla nueva
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1      // Incrementar al cambiar esquema
        const val DATABASE_NAME = "Sidi.db"
    }
}
```

---

### 4.3 GuardarCargarRecord.kt - Interfaz

```kotlin
interface GuardarCargarRecord {
    fun recogerRecord(): RecordJuego
    fun guardarRecord(nuevoRecord: RecordJuego)
}
```

---

### 4.4 ControladorSQLite.kt - Implementacion actual

```kotlin
package com.SarayDani.sidi

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.util.Log

class ControladorSQLite(context: Context) : GuardarCargarRecord {

    private val dbHelper = FeedReaderDbHelper(context)
    private val TAG = "SQLite"

    override fun recogerRecord(): RecordJuego {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION,
            EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_FECHA
        )

        val sortOrder = "${EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION} DESC"

        val cursor = db.query(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,
            projection,
            null,           // WHERE
            null,           // WHERE args
            null,           // GROUP BY
            null,           // HAVING
            sortOrder       // ORDER BY
        )

        var record = RecordJuego(0, "")

        with(cursor) {
            if (moveToFirst()) {
                val score = getInt(getColumnIndexOrThrow(
                    EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION))
                val fecha = getString(getColumnIndexOrThrow(
                    EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_FECHA))
                record = RecordJuego(score, fecha)
            }
        }
        cursor.close()

        return record
    }

    override fun guardarRecord(nuevoRecord: RecordJuego) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_PUNTUACION, nuevoRecord.score)
            put(EstructuraBD.EntradaRecord.NOMBRE_COLUMNA_FECHA, nuevoRecord.fecha)
        }

        val newRowId = db.insert(EstructuraBD.EntradaRecord.NOMBRE_TABLA, null, values)
        Log.d(TAG, "Nueva fila insertada con ID: $newRowId")
    }
}
```

---

## 5. Operaciones CRUD basicas

### 5.1 CREATE - Insertar datos

```kotlin
fun insertar(puntuacion: Int, fecha: String): Long {
    val db = dbHelper.writableDatabase

    val values = ContentValues().apply {
        put("puntuacion", puntuacion)
        put("fecha", fecha)
    }

    // Devuelve el ID de la fila insertada, o -1 si falla
    return db.insert("records", null, values)
}
```

### 5.2 READ - Leer datos

```kotlin
// Leer todos los registros
fun leerTodos(): List<RecordJuego> {
    val db = dbHelper.readableDatabase
    val lista = mutableListOf<RecordJuego>()

    val cursor = db.query(
        "records",
        arrayOf("puntuacion", "fecha"),
        null, null, null, null,
        "puntuacion DESC"
    )

    with(cursor) {
        while (moveToNext()) {
            val score = getInt(getColumnIndexOrThrow("puntuacion"))
            val fecha = getString(getColumnIndexOrThrow("fecha"))
            lista.add(RecordJuego(score, fecha))
        }
    }
    cursor.close()
    return lista
}

// Leer un registro por ID
fun leerPorId(id: Long): RecordJuego? {
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        "records",
        arrayOf("puntuacion", "fecha"),
        "${BaseColumns._ID} = ?",    // WHERE
        arrayOf(id.toString()),       // Valor del WHERE
        null, null, null
    )

    var record: RecordJuego? = null
    with(cursor) {
        if (moveToFirst()) {
            val score = getInt(getColumnIndexOrThrow("puntuacion"))
            val fecha = getString(getColumnIndexOrThrow("fecha"))
            record = RecordJuego(score, fecha)
        }
    }
    cursor.close()
    return record
}
```

### 5.3 UPDATE - Actualizar datos

```kotlin
fun actualizar(id: Long, nuevaPuntuacion: Int): Int {
    val db = dbHelper.writableDatabase

    val values = ContentValues().apply {
        put("puntuacion", nuevaPuntuacion)
    }

    // Devuelve el numero de filas afectadas
    return db.update(
        "records",
        values,
        "${BaseColumns._ID} = ?",
        arrayOf(id.toString())
    )
}
```

### 5.4 DELETE - Eliminar datos

```kotlin
// Eliminar por ID
fun eliminar(id: Long): Int {
    val db = dbHelper.writableDatabase

    return db.delete(
        "records",
        "${BaseColumns._ID} = ?",
        arrayOf(id.toString())
    )
}

// Eliminar todos
fun eliminarTodos(): Int {
    val db = dbHelper.writableDatabase
    return db.delete("records", null, null)
}
```

---

## 6. Ejemplo avanzado: Dos tablas (Usuarios y Records)

### 6.1 Estructura de datos con dos tablas

```kotlin
// EstructuraBD.kt - AMPLIADO con tabla de usuarios

package com.SarayDani.sidi

import android.provider.BaseColumns

object EstructuraBD {

    // Tabla de USUARIOS
    object EntradaUsuario : BaseColumns {
        const val NOMBRE_TABLA = "usuarios"
        const val COLUMNA_NOMBRE = "nombre"
        const val COLUMNA_EMAIL = "email"
        const val COLUMNA_FECHA_REGISTRO = "fecha_registro"
    }

    // Tabla de RECORDS (con referencia a usuario)
    object EntradaRecord : BaseColumns {
        const val NOMBRE_TABLA = "records"
        const val COLUMNA_PUNTUACION = "puntuacion"
        const val COLUMNA_FECHA = "fecha"
        const val COLUMNA_USUARIO_ID = "usuario_id"  // Foreign Key
    }
}
```

### 6.2 Modelo de datos

```kotlin
// Usuario.kt
data class Usuario(
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val fechaRegistro: String
)

// RecordJuego.kt - AMPLIADO
data class RecordJuego(
    val id: Long = 0,
    var score: Int = 0,
    var fecha: String = "",
    var usuarioId: Long = 0  // Referencia al usuario
)
```

### 6.3 DbHelper con dos tablas

```kotlin
// FeedReaderDbHelper.kt - AMPLIADO

package com.SarayDani.sidi

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        const val DATABASE_VERSION = 2  // INCREMENTAR al añadir tabla
        const val DATABASE_NAME = "Sidi.db"
    }

    // SQL para crear tabla USUARIOS
    private val SQL_CREATE_USUARIOS =
        """CREATE TABLE ${EstructuraBD.EntradaUsuario.NOMBRE_TABLA} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${EstructuraBD.EntradaUsuario.COLUMNA_NOMBRE} TEXT NOT NULL,
            ${EstructuraBD.EntradaUsuario.COLUMNA_EMAIL} TEXT UNIQUE,
            ${EstructuraBD.EntradaUsuario.COLUMNA_FECHA_REGISTRO} TEXT
        )"""

    // SQL para crear tabla RECORDS con Foreign Key
    private val SQL_CREATE_RECORDS =
        """CREATE TABLE ${EstructuraBD.EntradaRecord.NOMBRE_TABLA} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION} INTEGER NOT NULL,
            ${EstructuraBD.EntradaRecord.COLUMNA_FECHA} TEXT,
            ${EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID} INTEGER,
            FOREIGN KEY (${EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID})
                REFERENCES ${EstructuraBD.EntradaUsuario.NOMBRE_TABLA}(${BaseColumns._ID})
                ON DELETE CASCADE
        )"""

    private val SQL_DELETE_USUARIOS =
        "DROP TABLE IF EXISTS ${EstructuraBD.EntradaUsuario.NOMBRE_TABLA}"

    private val SQL_DELETE_RECORDS =
        "DROP TABLE IF EXISTS ${EstructuraBD.EntradaRecord.NOMBRE_TABLA}"

    override fun onCreate(db: SQLiteDatabase) {
        // Habilitar Foreign Keys
        db.execSQL("PRAGMA foreign_keys = ON")

        // Crear tablas en orden (primero usuarios, luego records)
        db.execSQL(SQL_CREATE_USUARIOS)
        db.execSQL(SQL_CREATE_RECORDS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Eliminar en orden inverso por las Foreign Keys
        db.execSQL(SQL_DELETE_RECORDS)
        db.execSQL(SQL_DELETE_USUARIOS)
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        // Habilitar Foreign Keys cada vez que se abre la BD
        db.execSQL("PRAGMA foreign_keys = ON")
    }
}
```

---

## 7. Interfaz extendida con multiples operaciones

```kotlin
// GuardarCargarRecord.kt - INTERFAZ COMPLETA

package com.SarayDani.sidi

interface GuardarCargarRecord {

    // ============ OPERACIONES DE USUARIOS ============

    /** Crear un nuevo usuario */
    fun crearUsuario(usuario: Usuario): Long

    /** Obtener usuario por ID */
    fun getUsuarioById(id: Long): Usuario?

    /** Obtener usuario por email */
    fun getUsuarioByEmail(email: String): Usuario?

    /** Obtener todos los usuarios */
    fun getTodosUsuarios(): List<Usuario>

    /** Actualizar usuario */
    fun actualizarUsuario(usuario: Usuario): Int

    /** Eliminar usuario */
    fun eliminarUsuario(id: Long): Int

    // ============ OPERACIONES DE RECORDS ============

    /** Guardar un nuevo record */
    fun guardarRecord(nuevoRecord: RecordJuego)

    /** Obtener el record maximo (mejor puntuacion global) */
    fun getMaxRecord(): RecordJuego?

    /** Obtener el record maximo de un usuario especifico */
    fun getMaxRecordByUserId(usuarioId: Long): RecordJuego?

    /** Obtener todos los records */
    fun getTodosRecords(): List<RecordJuego>

    /** Obtener todos los records de un usuario */
    fun getRecordsByUserId(usuarioId: Long): List<RecordJuego>

    /** Obtener los N mejores records */
    fun getTopRecords(limite: Int): List<RecordJuego>

    /** Obtener records por rango de puntuacion */
    fun getRecordsByRango(minPuntuacion: Int, maxPuntuacion: Int): List<RecordJuego>

    /** Contar total de partidas */
    fun contarPartidas(): Int

    /** Contar partidas de un usuario */
    fun contarPartidasByUserId(usuarioId: Long): Int

    /** Obtener media de puntuaciones */
    fun getMediaPuntuacion(): Float

    /** Eliminar un record */
    fun eliminarRecord(id: Long): Int

    /** Eliminar todos los records de un usuario */
    fun eliminarRecordsByUserId(usuarioId: Long): Int

    // ============ CONSULTAS CON JOIN ============

    /** Obtener records con nombre de usuario (JOIN) */
    fun getRecordsConUsuario(): List<Pair<RecordJuego, String>>

    /** Obtener ranking de usuarios por mejor puntuacion */
    fun getRankingUsuarios(): List<Pair<Usuario, Int>>
}
```

---

## 8. Controlador completo con todas las operaciones

```kotlin
// ControladorSQLite.kt - IMPLEMENTACION COMPLETA

package com.SarayDani.sidi

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.util.Log

class ControladorSQLite(context: Context) : GuardarCargarRecord {

    private val dbHelper = FeedReaderDbHelper(context)
    private val TAG = "SQLite"

    // ============================================================
    //                    OPERACIONES DE USUARIOS
    // ============================================================

    /**
     * Crear un nuevo usuario
     * @return ID del usuario creado, o -1 si falla
     */
    override fun crearUsuario(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(EstructuraBD.EntradaUsuario.COLUMNA_NOMBRE, usuario.nombre)
            put(EstructuraBD.EntradaUsuario.COLUMNA_EMAIL, usuario.email)
            put(EstructuraBD.EntradaUsuario.COLUMNA_FECHA_REGISTRO, usuario.fechaRegistro)
        }

        val id = db.insert(EstructuraBD.EntradaUsuario.NOMBRE_TABLA, null, values)
        Log.d(TAG, "Usuario creado con ID: $id")
        return id
    }

    /**
     * Obtener usuario por ID
     */
    override fun getUsuarioById(id: Long): Usuario? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            EstructuraBD.EntradaUsuario.NOMBRE_TABLA,
            null,  // Todas las columnas
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        var usuario: Usuario? = null
        with(cursor) {
            if (moveToFirst()) {
                usuario = cursorToUsuario(this)
            }
        }
        cursor.close()
        return usuario
    }

    /**
     * Obtener usuario por email
     */
    override fun getUsuarioByEmail(email: String): Usuario? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            EstructuraBD.EntradaUsuario.NOMBRE_TABLA,
            null,
            "${EstructuraBD.EntradaUsuario.COLUMNA_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )

        var usuario: Usuario? = null
        with(cursor) {
            if (moveToFirst()) {
                usuario = cursorToUsuario(this)
            }
        }
        cursor.close()
        return usuario
    }

    /**
     * Obtener todos los usuarios
     */
    override fun getTodosUsuarios(): List<Usuario> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Usuario>()

        val cursor = db.query(
            EstructuraBD.EntradaUsuario.NOMBRE_TABLA,
            null, null, null, null, null,
            "${EstructuraBD.EntradaUsuario.COLUMNA_NOMBRE} ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                lista.add(cursorToUsuario(this))
            }
        }
        cursor.close()
        return lista
    }

    /**
     * Actualizar usuario
     * @return Numero de filas afectadas
     */
    override fun actualizarUsuario(usuario: Usuario): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(EstructuraBD.EntradaUsuario.COLUMNA_NOMBRE, usuario.nombre)
            put(EstructuraBD.EntradaUsuario.COLUMNA_EMAIL, usuario.email)
        }

        return db.update(
            EstructuraBD.EntradaUsuario.NOMBRE_TABLA,
            values,
            "${BaseColumns._ID} = ?",
            arrayOf(usuario.id.toString())
        )
    }

    /**
     * Eliminar usuario (los records se eliminan en cascada)
     */
    override fun eliminarUsuario(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            EstructuraBD.EntradaUsuario.NOMBRE_TABLA,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString())
        )
    }

    // ============================================================
    //                    OPERACIONES DE RECORDS
    // ============================================================

    /**
     * Guardar un nuevo record
     */
    override fun guardarRecord(nuevoRecord: RecordJuego) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION, nuevoRecord.score)
            put(EstructuraBD.EntradaRecord.COLUMNA_FECHA, nuevoRecord.fecha)
            put(EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID, nuevoRecord.usuarioId)
        }

        val newRowId = db.insert(EstructuraBD.EntradaRecord.NOMBRE_TABLA, null, values)
        Log.d(TAG, "Record insertado con ID: $newRowId")
    }

    /**
     * Obtener el record maximo (mejor puntuacion global)
     */
    override fun getMaxRecord(): RecordJuego? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,
            null,
            null, null, null, null,
            "${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION} DESC",
            "1"  // LIMIT 1
        )

        var record: RecordJuego? = null
        with(cursor) {
            if (moveToFirst()) {
                record = cursorToRecord(this)
            }
        }
        cursor.close()
        return record
    }

    /**
     * Obtener el record maximo de un usuario especifico
     */
    override fun getMaxRecordByUserId(usuarioId: Long): RecordJuego? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,
            null,
            "${EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID} = ?",
            arrayOf(usuarioId.toString()),
            null, null,
            "${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION} DESC",
            "1"
        )

        var record: RecordJuego? = null
        with(cursor) {
            if (moveToFirst()) {
                record = cursorToRecord(this)
            }
        }
        cursor.close()
        return record
    }

    /**
     * Obtener todos los records
     */
    override fun getTodosRecords(): List<RecordJuego> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<RecordJuego>()

        val cursor = db.query(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,
            null, null, null, null, null,
            "${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                lista.add(cursorToRecord(this))
            }
        }
        cursor.close()
        return lista
    }

    /**
     * Obtener todos los records de un usuario
     */
    override fun getRecordsByUserId(usuarioId: Long): List<RecordJuego> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<RecordJuego>()

        val cursor = db.query(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,
            null,
            "${EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID} = ?",
            arrayOf(usuarioId.toString()),
            null, null,
            "${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                lista.add(cursorToRecord(this))
            }
        }
        cursor.close()
        return lista
    }

    /**
     * Obtener los N mejores records (TOP N)
     */
    override fun getTopRecords(limite: Int): List<RecordJuego> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<RecordJuego>()

        val cursor = db.query(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,
            null, null, null, null, null,
            "${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION} DESC",
            limite.toString()
        )

        with(cursor) {
            while (moveToNext()) {
                lista.add(cursorToRecord(this))
            }
        }
        cursor.close()
        return lista
    }

    /**
     * Obtener records por rango de puntuacion
     */
    override fun getRecordsByRango(minPuntuacion: Int, maxPuntuacion: Int): List<RecordJuego> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<RecordJuego>()

        val cursor = db.query(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,
            null,
            "${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION} BETWEEN ? AND ?",
            arrayOf(minPuntuacion.toString(), maxPuntuacion.toString()),
            null, null,
            "${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                lista.add(cursorToRecord(this))
            }
        }
        cursor.close()
        return lista
    }

    /**
     * Contar total de partidas
     */
    override fun contarPartidas(): Int {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${EstructuraBD.EntradaRecord.NOMBRE_TABLA}",
            null
        )

        var count = 0
        with(cursor) {
            if (moveToFirst()) {
                count = getInt(0)
            }
        }
        cursor.close()
        return count
    }

    /**
     * Contar partidas de un usuario
     */
    override fun contarPartidasByUserId(usuarioId: Long): Int {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """SELECT COUNT(*) FROM ${EstructuraBD.EntradaRecord.NOMBRE_TABLA}
               WHERE ${EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID} = ?""",
            arrayOf(usuarioId.toString())
        )

        var count = 0
        with(cursor) {
            if (moveToFirst()) {
                count = getInt(0)
            }
        }
        cursor.close()
        return count
    }

    /**
     * Obtener media de puntuaciones
     */
    override fun getMediaPuntuacion(): Float {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT AVG(${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION}) FROM ${EstructuraBD.EntradaRecord.NOMBRE_TABLA}",
            null
        )

        var media = 0f
        with(cursor) {
            if (moveToFirst()) {
                media = getFloat(0)
            }
        }
        cursor.close()
        return media
    }

    /**
     * Eliminar un record por ID
     */
    override fun eliminarRecord(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString())
        )
    }

    /**
     * Eliminar todos los records de un usuario
     */
    override fun eliminarRecordsByUserId(usuarioId: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            EstructuraBD.EntradaRecord.NOMBRE_TABLA,
            "${EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID} = ?",
            arrayOf(usuarioId.toString())
        )
    }

    // ============================================================
    //                    CONSULTAS CON JOIN
    // ============================================================

    /**
     * Obtener records con nombre de usuario (INNER JOIN)
     */
    override fun getRecordsConUsuario(): List<Pair<RecordJuego, String>> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Pair<RecordJuego, String>>()

        val query = """
            SELECT r.*, u.${EstructuraBD.EntradaUsuario.COLUMNA_NOMBRE}
            FROM ${EstructuraBD.EntradaRecord.NOMBRE_TABLA} r
            INNER JOIN ${EstructuraBD.EntradaUsuario.NOMBRE_TABLA} u
            ON r.${EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID} = u.${BaseColumns._ID}
            ORDER BY r.${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION} DESC
        """

        val cursor = db.rawQuery(query, null)

        with(cursor) {
            while (moveToNext()) {
                val record = cursorToRecord(this)
                val nombreUsuario = getString(
                    getColumnIndexOrThrow(EstructuraBD.EntradaUsuario.COLUMNA_NOMBRE)
                )
                lista.add(Pair(record, nombreUsuario))
            }
        }
        cursor.close()
        return lista
    }

    /**
     * Obtener ranking de usuarios por mejor puntuacion
     * Usa GROUP BY para obtener el MAX de cada usuario
     */
    override fun getRankingUsuarios(): List<Pair<Usuario, Int>> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Pair<Usuario, Int>>()

        val query = """
            SELECT u.*, MAX(r.${EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION}) as mejor_puntuacion
            FROM ${EstructuraBD.EntradaUsuario.NOMBRE_TABLA} u
            LEFT JOIN ${EstructuraBD.EntradaRecord.NOMBRE_TABLA} r
            ON u.${BaseColumns._ID} = r.${EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID}
            GROUP BY u.${BaseColumns._ID}
            ORDER BY mejor_puntuacion DESC
        """

        val cursor = db.rawQuery(query, null)

        with(cursor) {
            while (moveToNext()) {
                val usuario = cursorToUsuario(this)
                val mejorPuntuacion = getInt(getColumnIndexOrThrow("mejor_puntuacion"))
                lista.add(Pair(usuario, mejorPuntuacion))
            }
        }
        cursor.close()
        return lista
    }

    // ============================================================
    //                    METODOS AUXILIARES
    // ============================================================

    /**
     * Convierte un Cursor a objeto Usuario
     */
    private fun cursorToUsuario(cursor: Cursor): Usuario {
        return Usuario(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(
                EstructuraBD.EntradaUsuario.COLUMNA_NOMBRE)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(
                EstructuraBD.EntradaUsuario.COLUMNA_EMAIL)) ?: "",
            fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow(
                EstructuraBD.EntradaUsuario.COLUMNA_FECHA_REGISTRO)) ?: ""
        )
    }

    /**
     * Convierte un Cursor a objeto RecordJuego
     */
    private fun cursorToRecord(cursor: Cursor): RecordJuego {
        return RecordJuego(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
            score = cursor.getInt(cursor.getColumnIndexOrThrow(
                EstructuraBD.EntradaRecord.COLUMNA_PUNTUACION)),
            fecha = cursor.getString(cursor.getColumnIndexOrThrow(
                EstructuraBD.EntradaRecord.COLUMNA_FECHA)) ?: "",
            usuarioId = cursor.getLong(cursor.getColumnIndexOrThrow(
                EstructuraBD.EntradaRecord.COLUMNA_USUARIO_ID))
        )
    }
}
```

---

## 9. Verificar datos en la base de datos

### Metodo 1: Database Inspector (Android Studio)

1. Ejecutar la app en un emulador o dispositivo
2. Ir a `View` > `Tool Windows` > `App Inspection`
3. Seleccionar pestaña `Database Inspector`
4. Seleccionar la base de datos `Sidi.db`
5. Ver tablas y ejecutar consultas SQL

### Metodo 2: ADB Shell

```bash
# Conectar al dispositivo
adb shell

# Acceder a la app
run-as com.SarayDani.sidi

# Abrir SQLite
sqlite3 databases/Sidi.db

# Comandos utiles
.tables                    # Ver tablas
.schema records            # Ver estructura de tabla
SELECT * FROM records;     # Ver todos los records
SELECT * FROM usuarios;    # Ver todos los usuarios
.quit                      # Salir
```

### Metodo 3: Exportar y abrir con DB Browser

1. En Device File Explorer, navegar a:
   `data/data/com.SarayDani.sidi/databases/`
2. Click derecho en `Sidi.db` > `Save As...`
3. Abrir con [DB Browser for SQLite](https://sqlitebrowser.org/)

### Metodo 4: Test instrumentado

```kotlin
@RunWith(AndroidJUnit4::class)
class SQLiteTest {

    private lateinit var controlador: ControladorSQLite

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        controlador = ControladorSQLite(context)
    }

    @Test
    fun testCrearUsuarioYRecord() {
        // Crear usuario
        val usuario = Usuario(
            nombre = "Daniel",
            email = "daniel@test.com",
            fechaRegistro = "03/02/2026"
        )
        val usuarioId = controlador.crearUsuario(usuario)
        assertTrue(usuarioId > 0)

        // Crear record para ese usuario
        val record = RecordJuego(
            score = 25,
            fecha = "03/02/2026 20:00",
            usuarioId = usuarioId
        )
        controlador.guardarRecord(record)

        // Verificar
        val recordsUsuario = controlador.getRecordsByUserId(usuarioId)
        assertEquals(1, recordsUsuario.size)
        assertEquals(25, recordsUsuario[0].score)
    }

    @Test
    fun testGetMaxRecord() {
        val maxRecord = controlador.getMaxRecord()
        assertNotNull(maxRecord)
        Log.d("Test", "Max record: ${maxRecord?.score}")
    }

    @Test
    fun testGetTopRecords() {
        val top5 = controlador.getTopRecords(5)
        assertTrue(top5.size <= 5)
    }
}
```

---

## Resumen de operaciones SQL

| Operacion | Metodo db | Ejemplo SQL equivalente |
|-----------|-----------|------------------------|
| Insertar | `db.insert()` | `INSERT INTO tabla VALUES (...)` |
| Leer uno | `db.query()` con WHERE | `SELECT * FROM tabla WHERE id = ?` |
| Leer todos | `db.query()` sin WHERE | `SELECT * FROM tabla` |
| Actualizar | `db.update()` | `UPDATE tabla SET col = ? WHERE id = ?` |
| Eliminar | `db.delete()` | `DELETE FROM tabla WHERE id = ?` |
| Contar | `db.rawQuery()` | `SELECT COUNT(*) FROM tabla` |
| Media | `db.rawQuery()` | `SELECT AVG(col) FROM tabla` |
| Maximo | `db.query()` con ORDER + LIMIT | `SELECT * FROM tabla ORDER BY col DESC LIMIT 1` |
| Join | `db.rawQuery()` | `SELECT * FROM t1 INNER JOIN t2 ON ...` |

---

## Comparativa final

| Aspecto | SharedPreferences | SQLite | Room |
|---------|-------------------|--------|------|
| Complejidad | Baja | Media-Alta | Media |
| Tipo datos | Clave-valor | Tablas SQL | Tablas con ORM |
| Consultas | No | SQL manual | SQL con anotaciones |
| Relaciones | No | Si (manual) | Si (automatico) |
| Migraciones | No necesita | Manual | Automaticas |
| Validacion | No | No | En compilacion |
| Ideal para | Configuracion | Datos complejos | Datos complejos |
