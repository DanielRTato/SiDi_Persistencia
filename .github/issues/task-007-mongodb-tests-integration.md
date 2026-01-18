---
post_title: "TASK-007: Pruebas de integración para `MongoDbRepository`"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-007-mongodb-tests-integration"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["testing","integration","mongodb","flapdoodle"]
ai_note: false
summary: "Configurar pruebas de integración para `MongoDbRepository` usando Flapdoodle o contenedor de pruebas."
post_date: "2026-01-18"
---

## Descripción

Escribir pruebas en `app/src/androidTest/java/com/SarayDani/sidi/data/mongo/MongoDbRepositoryTest.kt` que verifiquen
la integración real con Mongo (insert, query, delete). Para CI/local se puede usar Flapdoodle embed-mongo o contenedor.

## Criterios de aceptación

- Tests que puedan ejecutarse en el entorno indicado y validen el contrato.
- Documentación en el README de cómo ejecutar estas pruebas localmente.

## Estimación

- 1-2 días

