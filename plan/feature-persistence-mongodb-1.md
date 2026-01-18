---
goal: "Implementar persistencia desacoplada en MongoDB siguiendo una interfaz común (DataRepository)"
version: "1.0"
date_created: "2026-01-14"
last_updated: "2026-01-14"
owner: "Equipo de desarrollo / DanielRTato"
status: "Planned"
tags: ["feature","data","persistence","mongodb","architecture"]
---

# Introduction

![Status: Planned](https://img.shields.io/badge/status-Planned-blue)

Un plan conciso para engadir persistencia en MongoDB mantendo un desacoplamento total entre a capa de persistencia e a lóxica de negocio: definirase unha interface única (DataRepository) e implementarase a versión con Mongo, deixando a estructura preparada para Room e unha implementación en memoria.

## 1. Requirements & Constraints

- **REQ-001**: Desacoplamiento total: la lógica de negocio no debe depender de ninguna librería de persistencia (Mongo, Room, SQLite, etc.).
- **REQ-002**: Interfaz única: crear una interfaz Kotlin `DataRepository` que defina operaciones mínimas necesarias: save, getAll, delete.
- **REQ-003**: Soporte coroutines: los métodos deben ser `suspend` para integrarse con ViewModel y coroutine scope del proyecto.
- **SEC-001**: Nada en la interfaz debe requerir dependencias externas (la interfaz solo usa tipos del modelo del proyecto).
- **CON-001**: La implementación de Mongo debe usar el driver oficial de JVM/Kotlin; la app es Android — habrá que usar la librería en una capa que pueda ejecutarse en Android (considerar uso de driver Sync o Reactive y wrappers). 
- **GUD-001**: Mantener la misma firma y comportamiento que las implementaciones actuales de Room/SQLite (si existen) para facilitar intercambio.
- **PAT-001**: Usar patrón repositorio + fábrica/Provider para seleccionar la implementación en tiempo de ejecución (inyección simple o mediante DI container).

## 2. Implementation Steps

### Implementation Phase 1

- GOAL-001: Definir la abstracción (interfaz) `DataRepository` y tipos asociados.

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-001 | Crear `DataRepository` en `app/src/main/java/com/SarayDani/sidi/data/DataRepository.kt` con las firmas exactas especificadas abajo. | | |
| TASK-002 | Revisar `RecordJuego` (`app/src/main/java/com/SarayDani/sidi/model/RecordJuego.kt`) y documentar el mapping entre `RecordJuego` y el documento Mongo (ObjectId). | | |
| TASK-003 | Añadir tests unitarios de contrato de repositorio: `app/src/test/java/com/SarayDani/sidi/data/DataRepositoryContractTest.kt`. (Ver sección Testing). | | |

Criterios de finalización (Phase 1):
- Archivo `DataRepository.kt` existe y compila (sin dependencias externas en su firma).
- Tests de contrato ejecutables y green usando implementación en memoria.

#### Especificación precisa de la interfaz (REQ-002 / SEC-001)
Crear el archivo con el siguiente contrato (Kotlin):

- Ruta: `app/src/main/java/com/SarayDani/sidi/data/DataRepository.kt`
- Contenido (firma requerida, sin imports de librerías externas):

interface DataRepository {
    suspend fun save(record: com.SarayDani.sidi.model.RecordJuego): Long
    suspend fun getAll(): List<com.SarayDani.sidi.model.RecordJuego>
    suspend fun delete(recordId: Long): Boolean
}

Notas:
- Se asume que `RecordJuego` tiene un campo `id: Long` en el modelo de la app. Si el modelo usa otro tipo, adaptar el `recordId` a `String` (mapeando ObjectId) según la decisión documentada en TASK-002.

### Implementation Phase 2

- GOAL-002: Implementación de MongoDB (`MongoDbRepository`) que cumpla la interfaz.

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-004 | Crear `MongoDbRepository` en `app/src/main/java/com/SarayDani/sidi/data/mongo/MongoDbRepository.kt` que implemente `DataRepository` usando el driver de Mongo. | | |
| TASK-005 | Definir configuración de conexión: `MongoConfig` en `app/src/main/java/com/SarayDani/sidi/data/mongo/MongoConfig.kt` con parámetros explícitos (URI, database name, collection name). | | |
| TASK-006 | Implementar mapping entre `RecordJuego` y documento BSON (convertir `id: Long` <-> ObjectId o guardar `id` explícito). | | |
| TASK-007 | Escribir pruebas unitarias/integración para `MongoDbRepository` en `app/src/androidTest/java/com/SarayDani/sidi/data/mongo/MongoDbRepositoryTest.kt` usando una instancia embebida de Mongo (Flapdoodle) o un contenedor de testing. | | |

Criterios de finalización (Phase 2):
- `MongoDbRepository` implementa `DataRepository` y todas las pruebas pasan contra el contrato.
- La implementación maneja errores de conexión y documenta cómo mapear `id`.

Detalles de implementación (TASK-004 a TASK-006):
- Archivo: `app/src/main/java/com/SarayDani/sidi/data/mongo/MongoDbRepository.kt`
- Clases/funciones a implementar (nombres y firmas obligatorias):
  - class MongoDbRepository(private val config: MongoConfig) : DataRepository {
      override suspend fun save(record: com.SarayDani.sidi.model.RecordJuego): Long { ... }
      override suspend fun getAll(): List<com.SarayDani.sidi.model.RecordJuego> { ... }
      override suspend fun delete(recordId: Long): Boolean { ... }
    }
- Archivo: `app/src/main/java/com/SarayDani/sidi/data/mongo/MongoConfig.kt`
  - data class MongoConfig(val uri: String, val database: String, val collection: String)
- Comportamiento requerido:
  - `save` debe devolver el id numérico asignado (Long). Si Mongo usa ObjectId, almacenar además un campo `appId: Long` o convertir ObjectId a Long mediante una tabla de correspondencia (documentar la estrategia). Preferencia: mantener `id: Long` en la aplicación y guardar ese campo explícitamente en el documento para evitar acoplar la app a ObjectId.
  - `getAll` debe devolver todos los registros ordenados por puntuación/fecha (documentar orden esperado).
  - `delete` debe borrar por `appId` y devolver true si se borró >= 1 doc.

Riesgos técnicos a mitigar en la implementación:
- Compatibilidad del driver en Android: validar que la dependencia elegida es usable en el entorno de la app o encapsular la lógica en un módulo JS/Kotlin multiplatform si fuera necesario.
- Mapping de ids: decidir estrategia (usar `Long` en app y guardar campo `appId` en Mongo) para evitar dependencia en ObjectId.

### Implementation Phase 3

- GOAL-003: Proveedor / Factory y wiring para elegir la implementación en tiempo de ejecución.

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-008 | Crear `RepositoryProvider` en `app/src/main/java/com/SarayDani/sidi/data/RepositoryProvider.kt` con funci��n `fun provideRepository(mode: RepositoryMode): DataRepository`. | | |
| TASK-009 | Enumerado `RepositoryMode` en el mismo archivo: `enum class RepositoryMode { MEMORY, ROOM, MONGO }`. | | |
| TASK-010 | Documentar cómo configurar la elección en `MainActivity` o en el `ViewModel` constructor. | | |

Criterios de finalización (Phase 3):
- Se puede cambiar entre implementaciones con un solo parámetro (RepositoryMode) sin tocar la lógica de negocio.

### Implementation Phase 4

- GOAL-004: Integración, pruebas automáticas y documentación.

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-011 | Añadir pruebas de integración que confirmen intercambio entre implementaciones sin cambio en la lógica de negocio: `app/src/androidTest/.../RepositoryIntegrationTest.kt`. | | |
| TASK-012 | Actualizar `README.md` del proyecto con instrucciones de configuración de Mongo (URI, variables de entorno, pruebas). | | |
| TASK-013 | Añadir ejemplos de uso en `app/src/main/java/com/SarayDani/sidi/data/README-PERSISTENCE.md`. | | |

Criterios de finalización (Phase 4):
- Documentación completa y pruebas de integración que demuestren el intercambio de implementaciones.

## 3. Alternatives

- **ALT-001**: Usar ObjectId como id de la app (String) y adaptar todo el dominio a String en lugar de Long — ventaja: alineación con Mongo; desventaja: implica cambios en toda la app.
- **ALT-002**: Usar una capa de sincronización y replicación (por ejemplo Realm o Couchbase) — ventaja: soporte offline/replicación, desventaja: mayor complejidad y dependencias.

## 4. Dependencies

- **DEP-001**: Driver Mongo JVM (recomendado): `org.mongodb:mongodb-driver-sync:4.11.1` o `org.mongodb:mongodb-driver-reactivestreams:4.x` — elegir según compatibilidad con coroutines/Android.
- **DEP-002**: Flapdoodle embed mongo para pruebas: `de.flapdoodle.embed:de.flapdoodle.embed.mongo:3.5.0` (o versión estable actual).
- **DEP-003**: JUnit / MockK / kotlin coroutines test para unit tests.

Notes: Branch created and file uploaded. The create_branch call returned a ref on SiDi_Persistencia (note capitalization) with SHA 4ae4fe3792e... and then the create file call earlier failed because branch didn't exist; now branch exists.
The last create_or_update call hasn't returned yet in this trace.