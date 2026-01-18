---
post_title: "TASK-008: Crear `RepositoryProvider` (Factory)"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-008-repository-provider"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["factory","provider","di"]
ai_note: false
summary: "Crear un proveedor/fábrica para seleccionar la implementación de `DataRepository` en tiempo de ejecución."
post_date: "2026-01-18"
---

## Descripción

Crear `app/src/main/java/com/SarayDani/sidi/data/RepositoryProvider.kt` con la función
`fun provideRepository(mode: RepositoryMode): DataRepository` que retorne la implementación adecuada.

## Criterios de aceptación

- `RepositoryProvider` permite elegir entre MEMORIA, ROOM, MONGO.
- Fácil de usar desde `MainActivity` o `ViewModel`.

## Estimación

- 4 horas

