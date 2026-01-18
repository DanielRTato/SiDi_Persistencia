---
post_title: "TASK-001: Crear `DataRepository` (interfaz)"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-001-create-datarepository"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["data","interface","contract","coroutines"]
ai_note: false
summary: "Crear la interfaz Kotlin `DataRepository` con las firmas suspend descritas en el plan para desacoplar la persistencia."
post_date: "2026-01-18"
---

## Descripción

Crear el archivo `app/src/main/java/com/SarayDani/sidi/data/DataRepository.kt` que declare la interfaz
`DataRepository` con la siguiente firma exacta (sin depender de librerías externas en la firma):

- suspend fun save(record: com.SarayDani.sidi.model.RecordJuego): Long
- suspend fun getAll(): List<com.SarayDani.sidi.model.RecordJuego>
- suspend fun delete(recordId: Long): Boolean

## Criterios de aceptación

- El archivo existe en la ruta indicada y compila.
- La interfaz no importa librerías externas (solo usa el tipo del modelo existente).
- Las funciones son `suspend` y están listas para ser implementadas por repositorios.

## Notas

- En el plan se asume que `RecordJuego` tiene un campo `id: Long`. Si no, documentar en `TASK-002` la decisión.

## Estimación

- 1-2 horas

