---
post_title: "TASK-006: Implementar mapping RecordJuego <-> BSON"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-006-mapping-recordjuego-bson"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["mapping","bson","model"]
ai_note: false
summary: "Definir y documentar el mapping entre `RecordJuego` y documentos BSON en MongoDB."
post_date: "2026-01-18"
---

## Descripción

Implementar el mapping entre `RecordJuego` y documento BSON. Debe quedar claro:

- Qué campo actúa como identificador en la app (`id: Long`) y cómo se guarda en Mongo (`appId`).
- Campos adicionales y formatos (fecha como ISO String, score numeric).
- Orden esperado en `getAll` (por ejemplo: score descendente, fecha descendente).

## Criterios de aceptación

- Documento de especificación en el repo.
- Implementación del mapping en `MongoDbRepository` coherente con la especificación.

## Estimación

- 2-3 horas

