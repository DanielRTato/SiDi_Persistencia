# Room en Android - Guia para PDMD DAM2

## Indice
1. [Que es Room](#1-que-es-room)
2. [Componentes principales de Room](#2-componentes-principales-de-room)
3. [Entity - La anotacion mas importante](#3-entity---la-anotacion-mas-importante)
4. [Estructura del proyecto](#4-estructura-del-proyecto)
5. [Implementacion paso a paso](#5-implementacion-paso-a-paso)
6. [Donde se guarda la base de datos](#6-donde-se-guarda-la-base-de-datos)
7. [Operaciones CRUD con Room](#7-operaciones-crud-con-room)
8. [Ejemplo avanzado: Dos tablas con relaciones](#8-ejemplo-avanzado-dos-tablas-con-relaciones)
9. [Migraciones de base de datos](#9-migraciones-de-base-de-datos)
10. [Verificar datos en la base de datos](#10-verificar-datos-en-la-base-de-datos)

---

## 1. Que es Room

Room es una **biblioteca de persistencia** de Android Jetpack que proporciona una capa de abstraccion sobre SQLite. Facilita el trabajo con bases de datos mediante:

- **Anotaciones** que generan codigo automaticamente
- **Validacion en tiempo de compilacion** de consultas SQL
- **Integracion nativa** con LiveData, Flow y corrutinas

### Ventajas sobre SQLite puro

| Aspecto | SQLite puro | Room |
|---------|-------------|------|
| Codigo SQL | Manual, propenso a errores | Validado en compilacion |
| Mapeo objetos | Manual con Cursor | Automatico |
| Boilerplate | Mucho codigo repetitivo | Minimo |
| Migraciones | Manuales | Semi-automaticas |
| Testing | Dificil | Facil con base de datos en memoria |
| Corrutinas | No nativo | Soporte nativo con suspend |

---

## 2. Componentes principales de Room

Room tiene **3 componentes principales**:

```
+------------------+     +------------------+     +------------------+
|     ENTITY       |     |       DAO        |     |    DATABASE      |
|  (Tabla/Modelo)  |     | (Data Access Obj)|     |  (Punto entrada) |
+------------------+     +------------------+     +------------------+
| @Entity          |     | @Dao             |     | @Database        |
| @PrimaryKey      |     | @Insert          |     | entities = [...]  |
| @ColumnInfo      |     | @Update          |     | version = 1      |
| data class       |     | @Delete          |     | abstract class   |
|                  |     | @Query           |     |                  |
+------------------+     +------------------+     +------------------+
        |                        |                        |
        +------------------------+------------------------+
                                 |
                         Room.databaseBuilder()
```

### Resumen:
- **Entity**: Define la estructura de una tabla (columnas, tipos, claves)
- **DAO**: Define las operaciones (INSERT, SELECT, UPDATE, DELETE)
- **Database**: Punto de entrada que conecta todo

---

## 3. Entity - La anotacion mas importante

### 3.1 Que es una Entity

Una **Entity** representa una **tabla** en la base de datos. Cada instancia de la clase es una **fila** de la tabla.

```kotlin
@Entity(tableName = "tabla_records")
data class EntidadRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "puntuacion") val puntuacion: Int,
    @ColumnInfo(name = "fecha") val fecha: String
)
```

Esto genera la tabla SQL:
```sql
CREATE TABLE tabla_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    puntuacion INTEGER NOT NULL,
    fecha TEXT NOT NULL
)
```

---

### 3.2 Anotaciones de Entity

#### @Entity
Define que la clase es una tabla.

```kotlin
// Nombre de tabla personalizado
@Entity(tableName = "mi_tabla")

// Indices para optimizar consultas
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)

// Clave primaria compuesta
@Entity(primaryKeys = ["nombre", "apellido"])
```

#### @PrimaryKey
Define la clave primaria.

```kotlin
// Autogenerada (autoincrement)
@PrimaryKey(autoGenerate = true)
val id: Int = 0

// Manual (debes proporcionarla)
@PrimaryKey
val id: String
```

#### @ColumnInfo
Personaliza el nombre y propiedades de la columna.

```kotlin
@ColumnInfo(name = "nombre_columna")           // Nombre en BD
@ColumnInfo(defaultValue = "Sin nombre")       // Valor por defecto
@ColumnInfo(typeAffinity = ColumnInfo.TEXT)    // Tipo de dato
@ColumnInfo(index = true)                      // Crear indice
```

#### @Ignore
Excluye un campo de la tabla.

```kotlin
@Entity
data class Usuario(
    @PrimaryKey val id: Int,
    val nombre: String,
    @Ignore val edadCalculada: Int = 0  // No se guarda en BD
)
```

#### @Embedded
Incluye campos de otra clase como columnas.

```kotlin
data class Direccion(
    val calle: String,
    val ciudad: String,
    val codigoPostal: String
)

@Entity
data class Usuario(
    @PrimaryKey val id: Int,
    val nombre: String,
    @Embedded val direccion: Direccion  // Añade calle, ciudad, codigoPostal
)
```

#### @ForeignKey
Define relaciones entre tablas.

```kotlin
@Entity(
    tableName = "records",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE  // Eliminar en cascada
        )
    ]
)
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val puntuacion: Int,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int
)
```

---

### 3.3 Tipos de datos soportados

| Kotlin | SQLite | Notas |
|--------|--------|-------|
| `Int` | INTEGER | |
| `Long` | INTEGER | |
| `Float` | REAL | |
| `Double` | REAL | |
| `String` | TEXT | |
| `Boolean` | INTEGER | 0 = false, 1 = true |
| `ByteArray` | BLOB | Para datos binarios |

Para tipos complejos (Date, List, etc.) necesitas **TypeConverters**.

---

### 3.4 TypeConverters para tipos complejos

```kotlin
// Conversor para Date
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

// Registrar en la Database
@Database(entities = [Record::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase()
```

---

## 4. Estructura del proyecto

```
com.SarayDani.sidi/
├── data/
│   ├── GuardarCargarRecord.kt       // Interfaz repositorio
│   └── room/
│       ├── EntidadRecord.kt         // @Entity - Define la tabla
│       ├── RecordDao.kt             // @Dao - Operaciones CRUD
│       ├── AppDatabase.kt           // @Database - Punto de entrada
│       └── ControladorRoom.kt       // Implementa la interfaz
├── model/
│   └── RecordJuego.kt               // Modelo de dominio
└── viewModel/
    └── MyViewModel.kt               // Usa el controlador
```

### Arquitectura
```
UI (Compose) --> ViewModel --> Interfaz --> ControladorRoom --> DAO --> Room --> SQLite
                                  |              |               |
                        GuardarCargarRecord  AppDatabase    RecordDao
```

---

## 5. Implementacion paso a paso

### Paso 1: Añadir dependencias en `build.gradle.kts`

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"  // Para KSP
}

dependencies {
    val roomVersion = "2.6.1"

    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")  // Procesador de anotaciones

    // Opcional: Soporte para Kotlin corrutinas
    implementation("androidx.room:room-ktx:$roomVersion")

    // Opcional: Testing
    testImplementation("androidx.room:room-testing:$roomVersion")
}
```

**Nota:** Usa `ksp` en lugar de `kapt` para mejor rendimiento.

---

### Paso 2: Crear la Entity

```kotlin
// EntidadRecord.kt
package com.SarayDani.sidi.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_records")
data class EntidadRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "puntuacion") val puntuacion: Int,
    @ColumnInfo(name = "fecha") val fecha: String
)
```

---

### Paso 3: Crear el DAO

```kotlin
// RecordDao.kt
package com.SarayDani.sidi.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecordDao {

    @Query("SELECT * FROM tabla_records")
    fun getAll(): List<EntidadRecord>

    @Delete
    fun delete(record: EntidadRecord)

    @Insert
    fun insert(record: EntidadRecord)

    @Query("SELECT * FROM tabla_records ORDER BY puntuacion DESC LIMIT 1")
    fun getRecordMaximo(): EntidadRecord?
}
```

---

### Paso 4: Crear la Database

```kotlin
// AppDatabase.kt
package com.SarayDani.sidi.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EntidadRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}
```

---

### Paso 5: Crear el Controlador (Repository)

```kotlin
// ControladorRoom.kt
package com.SarayDani.sidi.data.room

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.SarayDani.sidi.data.GuardarCargarRecord
import com.SarayDani.sidi.model.RecordJuego

class ControladorRoom(context: Context) : GuardarCargarRecord {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "SidiRoom.db"
    ).allowMainThreadQueries().build()

    private val TAG = "tag_room"

    override fun recogerRecord(): RecordJuego {
        val entidad: EntidadRecord? = db.recordDao().getRecordMaximo()

        return if (entidad != null) {
            Log.d(TAG, "Record recuperado: ${entidad.puntuacion}")
            RecordJuego(entidad.puntuacion, entidad.fecha)
        } else {
            RecordJuego(0, "")
        }
    }

    override fun guardarRecord(nuevoRecord: RecordJuego) {
        val nuevaEntidad = EntidadRecord(
            puntuacion = nuevoRecord.score,
            fecha = nuevoRecord.fecha
        )

        db.recordDao().insert(nuevaEntidad)
        Log.d(TAG, "Guardado. Puntos: ${nuevoRecord.score}")
    }
}
```

---

### Paso 6: Usar en el ViewModel

```kotlin
// MyViewModel.kt
class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio: GuardarCargarRecord =
        ControladorRoom(application.applicationContext)

    // ... resto del codigo
}
```

---

## 6. Donde se guarda la base de datos

### Ruta del archivo
```
/data/data/com.SarayDani.sidi/databases/SidiRoom.db
```

### Como verla en Android Studio

1. **App Inspection** (recomendado):
   - `View` > `Tool Windows` > `App Inspection`
   - Pestaña `Database Inspector`
   - Seleccionar `SidiRoom.db`

2. **Device File Explorer**:
   - `View` > `Tool Windows` > `Device File Explorer`
   - Navegar a `data/data/com.SarayDani.sidi/databases/`

---

## 7. Operaciones CRUD con Room

### 7.1 INSERT

```kotlin
@Dao
interface RecordDao {

    // Insertar uno
    @Insert
    fun insert(record: EntidadRecord)

    // Insertar varios
    @Insert
    fun insertAll(vararg records: EntidadRecord)

    // Insertar lista
    @Insert
    fun insertAll(records: List<EntidadRecord>)

    // Estrategia de conflicto
    @Insert(onConflict = OnConflictStrategy.REPLACE)  // Reemplaza si existe
    fun insertOrReplace(record: EntidadRecord)

    @Insert(onConflict = OnConflictStrategy.IGNORE)   // Ignora si existe
    fun insertOrIgnore(record: EntidadRecord)

    // Devolver ID insertado
    @Insert
    fun insertAndGetId(record: EntidadRecord): Long
}
```

### 7.2 SELECT (Query)

```kotlin
@Dao
interface RecordDao {

    // Todos los registros
    @Query("SELECT * FROM tabla_records")
    fun getAll(): List<EntidadRecord>

    // Por ID
    @Query("SELECT * FROM tabla_records WHERE id = :id")
    fun getById(id: Int): EntidadRecord?

    // Con condiciones
    @Query("SELECT * FROM tabla_records WHERE puntuacion > :minPuntos")
    fun getMayoresQue(minPuntos: Int): List<EntidadRecord>

    // Ordenado
    @Query("SELECT * FROM tabla_records ORDER BY puntuacion DESC")
    fun getAllOrdenado(): List<EntidadRecord>

    // Limitado
    @Query("SELECT * FROM tabla_records ORDER BY puntuacion DESC LIMIT :limite")
    fun getTop(limite: Int): List<EntidadRecord>

    // Maximo
    @Query("SELECT * FROM tabla_records ORDER BY puntuacion DESC LIMIT 1")
    fun getRecordMaximo(): EntidadRecord?

    // Contar
    @Query("SELECT COUNT(*) FROM tabla_records")
    fun contar(): Int

    // Media
    @Query("SELECT AVG(puntuacion) FROM tabla_records")
    fun getMedia(): Float

    // Suma
    @Query("SELECT SUM(puntuacion) FROM tabla_records")
    fun getSuma(): Int

    // Con LIKE
    @Query("SELECT * FROM tabla_records WHERE fecha LIKE :patron")
    fun buscarPorFecha(patron: String): List<EntidadRecord>

    // Entre valores
    @Query("SELECT * FROM tabla_records WHERE puntuacion BETWEEN :min AND :max")
    fun getEnRango(min: Int, max: Int): List<EntidadRecord>
}
```

### 7.3 UPDATE

```kotlin
@Dao
interface RecordDao {

    // Actualizar entidad completa (busca por PrimaryKey)
    @Update
    fun update(record: EntidadRecord)

    // Actualizar varios
    @Update
    fun updateAll(vararg records: EntidadRecord)

    // Actualizar campo especifico
    @Query("UPDATE tabla_records SET puntuacion = :nuevaPuntuacion WHERE id = :id")
    fun updatePuntuacion(id: Int, nuevaPuntuacion: Int)

    // Devolver filas afectadas
    @Update
    fun updateAndGetCount(record: EntidadRecord): Int
}
```

### 7.4 DELETE

```kotlin
@Dao
interface RecordDao {

    // Eliminar entidad (busca por PrimaryKey)
    @Delete
    fun delete(record: EntidadRecord)

    // Eliminar varios
    @Delete
    fun deleteAll(vararg records: EntidadRecord)

    // Eliminar por condicion
    @Query("DELETE FROM tabla_records WHERE id = :id")
    fun deleteById(id: Int)

    // Eliminar todos
    @Query("DELETE FROM tabla_records")
    fun deleteAll()

    // Devolver filas eliminadas
    @Delete
    fun deleteAndGetCount(record: EntidadRecord): Int
}
```

---

## 8. Ejemplo avanzado: Dos tablas con relaciones

### 8.1 Entidades

```kotlin
// EntidadUsuario.kt
@Entity(tableName = "usuarios")
data class EntidadUsuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nombre") val nombre: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "fecha_registro") val fechaRegistro: String
)

// EntidadRecord.kt - Con Foreign Key
@Entity(
    tableName = "records",
    foreignKeys = [
        ForeignKey(
            entity = EntidadUsuario::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuario_id")]  // Indice para optimizar JOINs
)
data class EntidadRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "puntuacion") val puntuacion: Int,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int
)
```

### 8.2 Clase para relaciones (1:N)

```kotlin
// UsuarioConRecords.kt
data class UsuarioConRecords(
    @Embedded val usuario: EntidadUsuario,
    @Relation(
        parentColumn = "id",
        entityColumn = "usuario_id"
    )
    val records: List<EntidadRecord>
)
```

### 8.3 DAOs completos

```kotlin
// UsuarioDao.kt
@Dao
interface UsuarioDao {

    @Insert
    fun insert(usuario: EntidadUsuario): Long

    @Query("SELECT * FROM usuarios")
    fun getAll(): List<EntidadUsuario>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getById(id: Int): EntidadUsuario?

    @Query("SELECT * FROM usuarios WHERE email = :email")
    fun getByEmail(email: String): EntidadUsuario?

    @Update
    fun update(usuario: EntidadUsuario)

    @Delete
    fun delete(usuario: EntidadUsuario)

    // Obtener usuario con todos sus records
    @Transaction
    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuarioConRecords(id: Int): UsuarioConRecords?

    // Obtener todos los usuarios con sus records
    @Transaction
    @Query("SELECT * FROM usuarios")
    fun getAllUsuariosConRecords(): List<UsuarioConRecords>
}

// RecordDao.kt
@Dao
interface RecordDao {

    @Insert
    fun insert(record: EntidadRecord): Long

    @Query("SELECT * FROM records")
    fun getAll(): List<EntidadRecord>

    @Query("SELECT * FROM records WHERE usuario_id = :usuarioId")
    fun getByUsuarioId(usuarioId: Int): List<EntidadRecord>

    @Query("SELECT * FROM records WHERE usuario_id = :usuarioId ORDER BY puntuacion DESC LIMIT 1")
    fun getMaxRecordByUsuarioId(usuarioId: Int): EntidadRecord?

    @Query("SELECT * FROM records ORDER BY puntuacion DESC LIMIT 1")
    fun getMaxRecord(): EntidadRecord?

    @Query("SELECT * FROM records ORDER BY puntuacion DESC LIMIT :limite")
    fun getTopRecords(limite: Int): List<EntidadRecord>

    @Query("SELECT COUNT(*) FROM records WHERE usuario_id = :usuarioId")
    fun contarByUsuarioId(usuarioId: Int): Int

    @Query("SELECT AVG(puntuacion) FROM records")
    fun getMediaGlobal(): Float

    @Delete
    fun delete(record: EntidadRecord)

    @Query("DELETE FROM records WHERE usuario_id = :usuarioId")
    fun deleteByUsuarioId(usuarioId: Int)
}
```

### 8.4 Database con dos entidades

```kotlin
// AppDatabase.kt
@Database(
    entities = [EntidadUsuario::class, EntidadRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun recordDao(): RecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "SidiRoom.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

### 8.5 Interfaz extendida

```kotlin
// GuardarCargarRecord.kt - COMPLETA
interface GuardarCargarRecord {

    // ===== USUARIOS =====
    fun crearUsuario(nombre: String, email: String): Long
    fun getUsuarioById(id: Int): EntidadUsuario?
    fun getUsuarioByEmail(email: String): EntidadUsuario?
    fun getTodosUsuarios(): List<EntidadUsuario>
    fun actualizarUsuario(usuario: EntidadUsuario)
    fun eliminarUsuario(usuario: EntidadUsuario)

    // ===== RECORDS =====
    fun guardarRecord(puntuacion: Int, fecha: String, usuarioId: Int)
    fun getMaxRecord(): EntidadRecord?
    fun getMaxRecordByUserId(usuarioId: Int): EntidadRecord?
    fun getTodosRecords(): List<EntidadRecord>
    fun getRecordsByUserId(usuarioId: Int): List<EntidadRecord>
    fun getTopRecords(limite: Int): List<EntidadRecord>
    fun contarPartidasByUserId(usuarioId: Int): Int
    fun getMediaGlobal(): Float
    fun eliminarRecord(record: EntidadRecord)
    fun eliminarRecordsByUserId(usuarioId: Int)

    // ===== RELACIONES =====
    fun getUsuarioConRecords(usuarioId: Int): UsuarioConRecords?
    fun getTodosUsuariosConRecords(): List<UsuarioConRecords>
}
```

### 8.6 Controlador completo

```kotlin
// ControladorRoom.kt - COMPLETO
class ControladorRoom(context: Context) : GuardarCargarRecord {

    private val db = AppDatabase.getDatabase(context)
    private val usuarioDao = db.usuarioDao()
    private val recordDao = db.recordDao()

    // ===== USUARIOS =====

    override fun crearUsuario(nombre: String, email: String): Long {
        val usuario = EntidadUsuario(
            nombre = nombre,
            email = email,
            fechaRegistro = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )
        return usuarioDao.insert(usuario)
    }

    override fun getUsuarioById(id: Int): EntidadUsuario? {
        return usuarioDao.getById(id)
    }

    override fun getUsuarioByEmail(email: String): EntidadUsuario? {
        return usuarioDao.getByEmail(email)
    }

    override fun getTodosUsuarios(): List<EntidadUsuario> {
        return usuarioDao.getAll()
    }

    override fun actualizarUsuario(usuario: EntidadUsuario) {
        usuarioDao.update(usuario)
    }

    override fun eliminarUsuario(usuario: EntidadUsuario) {
        usuarioDao.delete(usuario)
    }

    // ===== RECORDS =====

    override fun guardarRecord(puntuacion: Int, fecha: String, usuarioId: Int) {
        val record = EntidadRecord(
            puntuacion = puntuacion,
            fecha = fecha,
            usuarioId = usuarioId
        )
        recordDao.insert(record)
    }

    override fun getMaxRecord(): EntidadRecord? {
        return recordDao.getMaxRecord()
    }

    override fun getMaxRecordByUserId(usuarioId: Int): EntidadRecord? {
        return recordDao.getMaxRecordByUsuarioId(usuarioId)
    }

    override fun getTodosRecords(): List<EntidadRecord> {
        return recordDao.getAll()
    }

    override fun getRecordsByUserId(usuarioId: Int): List<EntidadRecord> {
        return recordDao.getByUsuarioId(usuarioId)
    }

    override fun getTopRecords(limite: Int): List<EntidadRecord> {
        return recordDao.getTopRecords(limite)
    }

    override fun contarPartidasByUserId(usuarioId: Int): Int {
        return recordDao.contarByUsuarioId(usuarioId)
    }

    override fun getMediaGlobal(): Float {
        return recordDao.getMediaGlobal()
    }

    override fun eliminarRecord(record: EntidadRecord) {
        recordDao.delete(record)
    }

    override fun eliminarRecordsByUserId(usuarioId: Int) {
        recordDao.deleteByUsuarioId(usuarioId)
    }

    // ===== RELACIONES =====

    override fun getUsuarioConRecords(usuarioId: Int): UsuarioConRecords? {
        return usuarioDao.getUsuarioConRecords(usuarioId)
    }

    override fun getTodosUsuariosConRecords(): List<UsuarioConRecords> {
        return usuarioDao.getAllUsuariosConRecords()
    }
}
```

---

## 9. Migraciones de base de datos

Cuando cambias el esquema (añadir columna, tabla, etc.), debes incrementar la version y crear una migracion.

### Ejemplo: Añadir columna

```kotlin
// Migracion de version 1 a 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tabla_records ADD COLUMN nivel INTEGER NOT NULL DEFAULT 1")
    }
}

// Registrar en la Database
Room.databaseBuilder(context, AppDatabase::class.java, "SidiRoom.db")
    .addMigrations(MIGRATION_1_2)
    .build()
```

### Ejemplo: Añadir nueva tabla

```kotlin
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS configuracion (
                id INTEGER PRIMARY KEY NOT NULL,
                sonido INTEGER NOT NULL DEFAULT 1,
                vibracion INTEGER NOT NULL DEFAULT 1
            )
        """)
    }
}
```

### Migracion destructiva (borra datos)

```kotlin
Room.databaseBuilder(context, AppDatabase::class.java, "SidiRoom.db")
    .fallbackToDestructiveMigration()  // Borra y recrea si no hay migracion
    .build()
```

---

## 10. Verificar datos en la base de datos

### Metodo 1: Database Inspector (Android Studio)

1. Ejecutar la app
2. `View` > `Tool Windows` > `App Inspection`
3. Pestaña `Database Inspector`
4. Seleccionar `SidiRoom.db`
5. Ver tablas y ejecutar consultas

### Metodo 2: Test instrumentado

```kotlin
@RunWith(AndroidJUnit4::class)
class RoomTest {

    private lateinit var db: AppDatabase
    private lateinit var recordDao: RecordDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        recordDao = db.recordDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRead() {
        val record = EntidadRecord(puntuacion = 100, fecha = "03/02/2026")
        recordDao.insert(record)

        val todos = recordDao.getAll()
        assertEquals(1, todos.size)
        assertEquals(100, todos[0].puntuacion)
    }

    @Test
    fun getMaxRecord() {
        recordDao.insert(EntidadRecord(puntuacion = 50, fecha = "01/01/2026"))
        recordDao.insert(EntidadRecord(puntuacion = 100, fecha = "02/01/2026"))
        recordDao.insert(EntidadRecord(puntuacion = 75, fecha = "03/01/2026"))

        val max = recordDao.getRecordMaximo()
        assertEquals(100, max?.puntuacion)
    }
}
```

---

## Resumen de anotaciones Room

| Anotacion | Donde se usa | Proposito |
|-----------|--------------|-----------|
| `@Entity` | Clase | Define una tabla |
| `@PrimaryKey` | Propiedad | Clave primaria |
| `@ColumnInfo` | Propiedad | Configura columna |
| `@Ignore` | Propiedad | Excluye de la tabla |
| `@Embedded` | Propiedad | Incluye campos de otra clase |
| `@ForeignKey` | @Entity | Relacion entre tablas |
| `@Dao` | Interface | Define operaciones |
| `@Insert` | Metodo DAO | Insertar |
| `@Update` | Metodo DAO | Actualizar |
| `@Delete` | Metodo DAO | Eliminar |
| `@Query` | Metodo DAO | SQL personalizado |
| `@Transaction` | Metodo DAO | Operacion atomica |
| `@Database` | Clase abstracta | Punto de entrada |
| `@TypeConverters` | @Database | Conversores de tipos |
| `@Relation` | Propiedad | Relacion 1:N o N:M |

---

## Comparativa final

| Aspecto | SharedPreferences | SQLite | Room |
|---------|-------------------|--------|------|
| Tipo | Clave-valor | SQL manual | ORM sobre SQLite |
| Complejidad | Baja | Alta | Media |
| Validacion SQL | - | En ejecucion | En compilacion |
| Boilerplate | Poco | Mucho | Poco |
| Relaciones | No | Manual | Automatico |
| Corrutinas | No | Manual | Nativo |
| Migraciones | No necesita | Manual | Semi-auto |
| Testing | Dificil | Dificil | Facil (in-memory) |
| Ideal para | Config simple | Control total | Apps modernas |
