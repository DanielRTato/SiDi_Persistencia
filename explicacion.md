# SharedPreferences en Android - Guia para PDMD DAM2

## Indice
1. [Que son las SharedPreferences](#1-que-son-las-sharedpreferences)
2. [Donde se guarda el archivo XML](#2-donde-se-guarda-el-archivo-xml)
3. [Operaciones basicas](#3-operaciones-basicas)
4. [Ejemplo del proyecto Simon Dice](#4-ejemplo-del-proyecto-simon-dice)
5. [Comprobar que los datos se guardan](#5-comprobar-que-los-datos-se-guardan)

---

## 1. Que son las SharedPreferences

SharedPreferences es un mecanismo de almacenamiento **clave-valor** en Android para guardar datos simples de forma persistente. Los datos se almacenan en un archivo **XML** dentro del dispositivo.

### Caracteristicas principales:
- Almacena tipos primitivos: `Int`, `String`, `Boolean`, `Float`, `Long`, `Set<String>`
- Los datos persisten aunque la app se cierre
- Es ideal para configuraciones, preferencias de usuario y datos pequenos
- **NO es adecuado para datos complejos** (usar SQLite o Room para eso)

### Modos de acceso:
| Modo | Descripcion |
|------|-------------|
| `MODE_PRIVATE` | Solo esta app puede acceder (el mas usado) |
| `MODE_WORLD_READABLE` | **DEPRECADO** - otras apps pueden leer |
| `MODE_WORLD_WRITEABLE` | **DEPRECADO** - otras apps pueden escribir |

---

## 2. Donde se guarda el archivo XML

### Ruta del archivo
```
/data/data/<nombre.paquete>/shared_prefs/<nombre_archivo>.xml
```

En nuestro proyecto:
```
/data/data/com.SarayDani.sidi/shared_prefs/preferencias_app.xml
```

### Como ver el archivo en Android Studio

1. **Abrir Device File Explorer:**
   - `View` > `Tool Windows` > `Device File Explorer`

2. **Navegar a la ruta:**
   ```
   data/data/com.SarayDani.sidi/shared_prefs/
   ```

3. **Abrir el archivo XML:**
   - Doble clic en `preferencias_app.xml`

### Estructura del archivo XML generado
```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <int name="record_score" value="15" />
    <string name="record_fecha">03/02/2026 19:30</string>
</map>
```

---

## 3. Operaciones basicas

### 3.1 Obtener instancia de SharedPreferences

```kotlin
// Opcion 1: SharedPreferences con nombre personalizado
val prefs = context.getSharedPreferences("mi_archivo", Context.MODE_PRIVATE)

// Opcion 2: SharedPreferences por defecto de la Activity
val prefs = getPreferences(Context.MODE_PRIVATE)

// Opcion 3: SharedPreferences por defecto de la app (usa PreferenceManager)
val prefs = PreferenceManager.getDefaultSharedPreferences(context)
```

---

### 3.2 CREAR / GUARDAR un nuevo valor

```kotlin
val sharedPreferences = context.getSharedPreferences("preferencias_app", Context.MODE_PRIVATE)

// Obtener el editor
val editor = sharedPreferences.edit()

// Guardar diferentes tipos de datos
editor.putInt("puntuacion", 100)           // Int
editor.putString("nombre", "Daniel")       // String
editor.putBoolean("sonido", true)          // Boolean
editor.putFloat("volumen", 0.8f)           // Float
editor.putLong("timestamp", 1234567890L)   // Long

// IMPORTANTE: Aplicar los cambios
editor.apply()  // Asincrono (recomendado)
// o
editor.commit() // Sincrono (devuelve true/false)
```

**Diferencia entre `apply()` y `commit()`:**
| Metodo | Tipo | Retorno | Uso recomendado |
|--------|------|---------|-----------------|
| `apply()` | Asincrono | Ninguno | Uso general (mas rapido) |
| `commit()` | Sincrono | Boolean | Cuando necesitas confirmar el guardado |

---

### 3.3 LEER / OBTENER un valor

```kotlin
val sharedPreferences = context.getSharedPreferences("preferencias_app", Context.MODE_PRIVATE)

// Leer valores (el segundo parametro es el valor por defecto si no existe)
val puntuacion = sharedPreferences.getInt("puntuacion", 0)
val nombre = sharedPreferences.getString("nombre", "") ?: ""
val sonido = sharedPreferences.getBoolean("sonido", false)
val volumen = sharedPreferences.getFloat("volumen", 1.0f)
val timestamp = sharedPreferences.getLong("timestamp", 0L)

// Comprobar si existe una clave
val existe = sharedPreferences.contains("puntuacion")  // true o false

// Obtener todas las claves
val todasLasClaves = sharedPreferences.all  // Map<String, *>
```

---

### 3.4 ACTUALIZAR / CAMBIAR un valor

Actualizar es igual que crear, simplemente se sobrescribe la clave existente:

```kotlin
val sharedPreferences = context.getSharedPreferences("preferencias_app", Context.MODE_PRIVATE)
val editor = sharedPreferences.edit()

// Si "puntuacion" ya existe, se actualiza; si no, se crea
editor.putInt("puntuacion", 200)  // Antes era 100, ahora es 200
editor.apply()
```

**Ejemplo completo de actualizacion condicional:**
```kotlin
fun actualizarRecord(nuevaPuntuacion: Int) {
    val prefs = context.getSharedPreferences("preferencias_app", Context.MODE_PRIVATE)
    val recordActual = prefs.getInt("record_score", 0)

    // Solo actualizar si la nueva puntuacion es mayor
    if (nuevaPuntuacion > recordActual) {
        prefs.edit()
            .putInt("record_score", nuevaPuntuacion)
            .putString("record_fecha", "03/02/2026 20:00")
            .apply()
    }
}
```

---

### 3.5 ELIMINAR un valor o todos

```kotlin
val sharedPreferences = context.getSharedPreferences("preferencias_app", Context.MODE_PRIVATE)
val editor = sharedPreferences.edit()

// Eliminar una clave especifica
editor.remove("puntuacion")
editor.apply()

// Eliminar TODAS las claves
editor.clear()
editor.apply()
```

---

### 3.6 Sintaxis alternativa con apply{} de Kotlin

```kotlin
// Forma mas limpia usando Kotlin
context.getSharedPreferences("preferencias_app", Context.MODE_PRIVATE)
    .edit()
    .apply {
        putInt("puntuacion", 100)
        putString("nombre", "Daniel")
        putBoolean("sonido", true)
    }
    .apply()  // Guardar cambios
```

---

## 4. Ejemplo del proyecto Simon Dice

### Arquitectura usada

```
UI (Compose) --> ViewModel --> Interfaz Repository --> Implementacion (SharedPreferences)
                                     |                          |
                           GuardarCargarRecord.kt    ControladorPreference.kt
```

### Archivos del proyecto:

#### `RecordJuego.kt` - Modelo de datos
```kotlin
data class RecordJuego(
    var score: Int = 0,      // Puntuacion del record
    var fecha: String = ""   // Fecha del record
)
```

#### `GuardarCargarRecord.kt` - Interfaz (contrato)
```kotlin
interface GuardarCargarRecord {
    fun recogerRecord(): RecordJuego
    fun guardarRecord(nuevoRecord: RecordJuego)
}
```

#### `ControladorPreference.kt` - Implementacion
```kotlin
class ControladorPreference(private val context: Context): GuardarCargarRecord {

    private val PREFS_NAME = "preferencias_app"
    private val KEY_RECORD = "record_score"
    private val KEY_FECHA = "record_fecha"

    override fun recogerRecord(): RecordJuego {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val score = sharedPreferences.getInt(KEY_RECORD, 0)
        val fecha = sharedPreferences.getString(KEY_FECHA, "") ?: ""
        return RecordJuego(score, fecha)
    }

    override fun guardarRecord(nuevoRecord: RecordJuego) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_RECORD, nuevoRecord.score)
        editor.putString(KEY_FECHA, nuevoRecord.fecha)
        editor.apply()
    }
}
```

#### `MyViewModel.kt` - Uso en el ViewModel
```kotlin
class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio: GuardarCargarRecord =
        ControladorPreference(application.applicationContext)

    val record = MutableStateFlow(RecordJuego(0, ""))

    init {
        cargarRecordGuardado()  // Cargar al iniciar
    }

    private fun cargarRecordGuardado() {
        record.value = repositorio.recogerRecord()
    }

    private fun gameOver() {
        val puntuacionActual = ronda.value
        val recordActual = record.value.score

        if (puntuacionActual > recordActual) {
            val nuevoRecord = RecordJuego(
                score = puntuacionActual,
                fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            )
            repositorio.guardarRecord(nuevoRecord)
            record.value = nuevoRecord
        }
    }
}
```

---

## 5. Comprobar que los datos se guardan

### Metodo 1: Device File Explorer (Android Studio)

1. Ejecutar la app y hacer algunas partidas para generar un record
2. Ir a `View` > `Tool Windows` > `Device File Explorer`
3. Navegar a: `data/data/com.SarayDani.sidi/shared_prefs/`
4. Abrir `preferencias_app.xml`
5. Verificar el contenido:
```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <int name="record_score" value="8" />
    <string name="record_fecha">03/02/2026 19:45</string>
</map>
```

### Metodo 2: Usando Logcat

Agregar logs en el codigo para verificar:
```kotlin
private fun cargarRecordGuardado() {
    record.value = repositorio.recogerRecord()
    Log.d("miDebug", "Record cargado: ${record.value.score}")
    Log.d("miDebug", "Fecha: ${record.value.fecha}")
}
```

Filtrar en Logcat por `miDebug` para ver los mensajes.

### Metodo 3: ADB Shell (linea de comandos)

```bash
# Conectar al dispositivo/emulador
adb shell

# Ir al directorio de la app
run-as com.SarayDani.sidi

# Ver el contenido del archivo
cat shared_prefs/preferencias_app.xml
```

### Metodo 4: Test Instrumentado

```kotlin
@RunWith(AndroidJUnit4::class)
class SharedPreferencesTest {

    @Test
    fun testGuardarYRecuperarRecord() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val controlador = ControladorPreference(context)

        // Crear y guardar un record
        val recordOriginal = RecordJuego(score = 25, fecha = "03/02/2026 20:00")
        controlador.guardarRecord(recordOriginal)

        // Recuperar y verificar
        val recordRecuperado = controlador.recogerRecord()

        assertEquals(25, recordRecuperado.score)
        assertEquals("03/02/2026 20:00", recordRecuperado.fecha)
    }
}
```

---

## Resumen de operaciones

| Operacion | Metodo | Ejemplo |
|-----------|--------|---------|
| Obtener prefs | `getSharedPreferences()` | `context.getSharedPreferences("nombre", MODE_PRIVATE)` |
| Leer Int | `getInt()` | `prefs.getInt("clave", 0)` |
| Leer String | `getString()` | `prefs.getString("clave", "") ?: ""` |
| Leer Boolean | `getBoolean()` | `prefs.getBoolean("clave", false)` |
| Guardar Int | `putInt()` | `editor.putInt("clave", 100)` |
| Guardar String | `putString()` | `editor.putString("clave", "valor")` |
| Guardar Boolean | `putBoolean()` | `editor.putBoolean("clave", true)` |
| Eliminar clave | `remove()` | `editor.remove("clave")` |
| Eliminar todo | `clear()` | `editor.clear()` |
| Aplicar cambios | `apply()` / `commit()` | `editor.apply()` |
| Verificar existencia | `contains()` | `prefs.contains("clave")` |

---

## Buenas practicas

1. **Usar constantes para las claves** - Evita errores de tipeo
2. **Usar MODE_PRIVATE** - Es el unico modo seguro
3. **Usar apply() en lugar de commit()** - Es asincrono y mas eficiente
4. **No guardar datos sensibles** - SharedPreferences NO esta encriptado por defecto
5. **Usar un patron Repository** - Desacopla la logica de persistencia
6. **Manejar valores por defecto** - Siempre proporcionar un valor por defecto en los getters

---

## 6. Ejemplo: Guardar TODAS las puntuaciones (no solo la mas alta)

### El problema con SharedPreferences

SharedPreferences **NO es adecuado** para guardar listas de datos porque:
- Solo almacena tipos primitivos (Int, String, Boolean...)
- No permite consultas complejas
- No tiene estructura relacional

Si quisieras guardar todas las partidas jugadas, necesitarias usar **Room** (o SQLite).

---

### Solucion con Room

#### Paso 1: Añadir dependencias en `build.gradle.kts`

```kotlin
dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")  // Para corrutinas
}
```

#### Paso 2: Crear la Entity (tabla)

```kotlin
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidas")
data class Partida(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val puntuacion: Int,
    val fecha: String,
    val esRecord: Boolean = false
)
```

#### Paso 3: Crear el DAO (Data Access Object)

```kotlin
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PartidaDao {

    // Insertar una nueva partida
    @Insert
    suspend fun insertarPartida(partida: Partida)

    // Obtener todas las partidas ordenadas por puntuacion
    @Query("SELECT * FROM partidas ORDER BY puntuacion DESC")
    suspend fun obtenerTodasLasPartidas(): List<Partida>

    // Obtener solo el record (puntuacion mas alta)
    @Query("SELECT * FROM partidas ORDER BY puntuacion DESC LIMIT 1")
    suspend fun obtenerRecord(): Partida?

    // Obtener las 10 mejores puntuaciones
    @Query("SELECT * FROM partidas ORDER BY puntuacion DESC LIMIT 10")
    suspend fun obtenerTop10(): List<Partida>

    // Obtener partidas de una fecha especifica
    @Query("SELECT * FROM partidas WHERE fecha LIKE :fecha || '%'")
    suspend fun obtenerPartidasPorFecha(fecha: String): List<Partida>

    // Contar total de partidas jugadas
    @Query("SELECT COUNT(*) FROM partidas")
    suspend fun contarPartidas(): Int

    // Obtener media de puntuaciones
    @Query("SELECT AVG(puntuacion) FROM partidas")
    suspend fun obtenerMediaPuntuacion(): Float

    // Eliminar todas las partidas
    @Query("DELETE FROM partidas")
    suspend fun eliminarTodas()
}
```

#### Paso 4: Crear la Database

```kotlin
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Partida::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun partidaDao(): PartidaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "simon_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

#### Paso 5: Crear el Repository

```kotlin
class PartidaRepository(private val partidaDao: PartidaDao) {

    suspend fun guardarPartida(puntuacion: Int, fecha: String) {
        val partida = Partida(
            puntuacion = puntuacion,
            fecha = fecha
        )
        partidaDao.insertarPartida(partida)
    }

    suspend fun obtenerHistorial(): List<Partida> {
        return partidaDao.obtenerTodasLasPartidas()
    }

    suspend fun obtenerRecord(): Partida? {
        return partidaDao.obtenerRecord()
    }

    suspend fun obtenerTop10(): List<Partida> {
        return partidaDao.obtenerTop10()
    }
}
```

#### Paso 6: Usar en el ViewModel

```kotlin
class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = PartidaRepository(database.partidaDao())

    val historialPartidas = MutableStateFlow<List<Partida>>(emptyList())
    val record = MutableStateFlow<Partida?>(null)

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            historialPartidas.value = repository.obtenerHistorial()
            record.value = repository.obtenerRecord()
        }
    }

    private fun gameOver() {
        viewModelScope.launch {
            val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            // Guardar TODAS las partidas, no solo el record
            repository.guardarPartida(ronda.value, fecha)

            // Actualizar el historial en la UI
            historialPartidas.value = repository.obtenerHistorial()
            record.value = repository.obtenerRecord()
        }
    }
}
```

#### Paso 7: Mostrar historial en la UI

```kotlin
@Composable
fun HistorialScreen(vm: MyViewModel) {
    val historial by vm.historialPartidas.collectAsState()
    val record by vm.record.collectAsState()

    Column {
        // Mostrar record
        record?.let {
            Text("Record: ${it.puntuacion} puntos (${it.fecha})")
        }

        Divider()

        // Mostrar historial completo
        Text("Historial de partidas:")
        LazyColumn {
            items(historial) { partida ->
                Row {
                    Text("${partida.puntuacion} pts")
                    Spacer(Modifier.weight(1f))
                    Text(partida.fecha)
                }
            }
        }
    }
}
```

---

### Comparativa: SharedPreferences vs Room para este caso

| Aspecto | SharedPreferences | Room |
|---------|-------------------|------|
| Guardar 1 record | ✅ Perfecto | ✅ Funciona |
| Guardar N partidas | ❌ No adecuado | ✅ Perfecto |
| Consultar top 10 | ❌ Imposible | ✅ Con SQL |
| Calcular media | ❌ Imposible | ✅ `AVG()` |
| Filtrar por fecha | ❌ Imposible | ✅ `WHERE` |
| Complejidad | Baja | Media |

### Conclusion

- **SharedPreferences**: Usa cuando solo necesitas guardar **un valor** (el record actual)
- **Room**: Usa cuando necesitas guardar **multiples registros** y hacer consultas

---

## Comparativa: SharedPreferences vs SQLite vs Room

| Caracteristica | SharedPreferences | SQLite | Room |
|----------------|-------------------|--------|------|
| Tipo de datos | Clave-valor simples | Tablas relacionales | Tablas con ORM |
| Complejidad | Baja | Media | Media-Baja |
| Consultas | No | SQL manual | SQL con anotaciones |
| Relaciones | No | Si | Si |
| Ideal para | Configuraciones | Datos estructurados | Datos estructurados |
| Formato | XML | Base de datos | Base de datos |
