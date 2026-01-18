---
post_title: "TASK-002: Revisar `RecordJuego` y documentar mapping a Mongo"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-002-review-recordjuego-mapping"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["data","mapping","model","mongodb"]
ai_note: false
summary: "Revisar la definición actual de `RecordJuego` y decidir/documentar cómo mapearla a un documento Mongo (ObjectId vs appId Long)."
post_date: "2026-01-18"
---

## Descripción

Revisar `app/src/main/java/com/SarayDani/sidi/RecordJuego.kt` y documentar la estrategia de mapping entre el modelo
`RecordJuego` y el documento Mongo. En particular hay que decidir:

- Si la app cambia `id` a `String` y usa ObjectId, o
- Mantener `id: Long` en la app y persistir un campo `appId: Long` en Mongo para evitar acoplamiento.

## Criterios de aceptación

- Documento en repo que describa la decisión y el mapeo (por ejemplo `plan/` o un archivo nuevo en `data/`).
- Si se decide mantener `id: Long`, queda especificado que `MongoDbRepository` usará `appId` para búsquedas.

## Estimación

- 1 hora

