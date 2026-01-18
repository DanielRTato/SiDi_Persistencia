---
post_title: "TASK-011: Pruebas de integración entre implementaciones"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "task-011-integration-tests"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["testing","integration","repository"]
ai_note: false
summary: "Añadir pruebas que validen que la lógica de negocio funciona igual con distintas implementaciones del repositorio."
post_date: "2026-01-18"
---

## Descripción

Crear tests de integración (androidTest) que usen la misma lógica de negocio y verifiquen el comportamiento con
implementaciones en memoria y `MongoDbRepository` (cuando esté disponible en pruebas).

## Criterios de aceptación

- Tests que puedan intercambiar implementaciones sin modificar la lógica de negocio.

## Estimación

- 1-2 días

