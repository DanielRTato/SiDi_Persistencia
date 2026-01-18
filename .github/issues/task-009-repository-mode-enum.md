---
post_title: "TASK-009: Crear `RepositoryMode` enum"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-009-repository-mode-enum"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["enum","config"]
ai_note: false
summary: "Añadir `RepositoryMode` enum para listar implementaciones disponibles: MEMORY, ROOM, MONGO."
post_date: "2026-01-18"
---

## Descripción

Crear en `RepositoryProvider.kt` (o archivo propio) el enum:

```kotlin
enum class RepositoryMode { MEMORY, ROOM, MONGO }
```

## Criterios de aceptación

- Enum presente y usado por `RepositoryProvider`.

## Estimación

- 30 minutos

