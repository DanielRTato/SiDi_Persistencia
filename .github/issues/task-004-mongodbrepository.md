---
post_title: "TASK-004: Implementar `MongoDbRepository`"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-004-mongodbrepository"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["mongodb","implementation","repository"]
ai_note: false
summary: "Implementar la clase `MongoDbRepository` que cumpla la interfaz `DataRepository`.
Debe gestionar conexiones y mapear el modelo al documento BSON según la estrategia definida."
post_date: "2026-01-18"
---

## Descripción

Implementar `app/src/main/java/com/SarayDani/sidi/data/mongo/MongoDbRepository.kt` con la firma:

class MongoDbRepository(private val config: MongoConfig) : DataRepository {
    override suspend fun save(record: com.SarayDani.sidi.model.RecordJuego): Long { ... }
    override suspend fun getAll(): List<com.SarayDani.sidi.model.RecordJuego> { ... }
    override suspend fun delete(recordId: Long): Boolean { ... }
}

La implementación deberá:
- Conectarse usando `MongoConfig` (URI, database, collection).
- Guardar el campo `appId: Long` o la estrategia acordada en `TASK-002`.
- Manejar errores de conexión y ser segura para usarse desde coroutines.

## Criterios de aceptación

- La clase compila y pasa los tests del contrato (`TASK-003`).
- Documenta la estrategia de mapeo y el manejo de errores.

## Estimación

- 1-2 días (dependiendo de compatibilidad del driver en Android)

