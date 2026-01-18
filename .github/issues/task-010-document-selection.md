---
post_title: "TASK-010: Documentar cómo seleccionar implementación en la app"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-010-document-selection"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["docs","usage"]
ai_note: false
summary: "Documentar en README o en código cómo configurar la app para usar una implementación de persistencia distinta."
post_date: "2026-01-18"
---

## Descripción

Agregar instrucciones en `README.md` y un ejemplo en `MyViewModel.kt` o `MainActivity` para seleccionar
la implementación deseada (ejemplo de uso con `RepositoryProvider.provideRepository(RepositoryMode.MONGO)`).

## Criterios de aceptación

- README actualizado con pasos claros.
- Ejemplo de código en `MyViewModel` comentado (no activado) para activar `MongoDbRepository`.

## Estimación

- 1 hora

