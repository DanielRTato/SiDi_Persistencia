---
post_title: "TASK-005: Crear `MongoConfig`"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-005-mongoconfig"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["config","mongodb"]
ai_note: false
summary: "Definir `MongoConfig` con parámetros explícitos (uri, database, collection) que usará `MongoDbRepository`."
post_date: "2026-01-18"
---

## Descripción

Crear `app/src/main/java/com/SarayDani/sidi/data/mongo/MongoConfig.kt` con:

```kotlin
data class MongoConfig(val uri: String, val database: String, val collection: String)
```

## Criterios de aceptación

- Archivo presente y compilable.
- Documentación sobre cómo obtener una URI y seguridad (no guardar credenciales en el repo).

## Estimación

- 1 hora

