---
post_title: "TASK-003: Tests de contrato para `DataRepository` (implementación en memoria)"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-003-datarepository-contract-tests"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["testing","contract","unit-tests","coroutines"]
ai_note: false
summary: "Añadir tests unitarios que validen el contrato de `DataRepository` usando una implementación en memoria."
post_date: "2026-01-18"
---

## Descripción

Crear tests de contrato en `app/src/test/java/com/SarayDani/sidi/data/DataRepositoryContractTest.kt` que verifiquen
el comportamiento esperado (save, getAll, delete) contra una implementación en memoria.

## Criterios de aceptación

- Tests ejecutables en `./gradlew test` (unit tests JVM) y pasan.
- Incluyen al menos: guardar un record (y recibir id), obtener lista, borrar por id y comportamiento ante id inexistente.

## Estimación

- 4 horas

