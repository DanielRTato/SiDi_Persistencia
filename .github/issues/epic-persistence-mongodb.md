---
post_title: "Epic: Persistencia desacoplada en MongoDB"
author1: "Equipo de desarrollo / DanielRTato"
post_slug: "epic-persistencia-mongodb"
microsoft_alias: "DanielRTato"
featured_image: "DemostracionSIDI.gif"
categories: ["feature"]
tags: ["persistence","mongodb","epic","data"]
ai_note: false
summary: "Epic que agrupa las tareas para implementar persistencia desacoplada usando MongoDB y una interfaz común DataRepository."
post_date: "2026-01-18"
---

## Epic: Persistencia desacoplada en MongoDB

Este epic agrupa las tareas necesarias para implementar una capa de persistencia desacoplada
que permita cambiar entre implementaciones (memoria, Room/SQLite, MongoDB) sin afectar la lógica
de negocio. El objetivo es cumplir los requisitos señalados en `plan/feature-persistence-mongodb-1.md`.

### Objetivos

- Definir la interfaz `DataRepository` (contrato) compatible con coroutines.
- Implementar `MongoDbRepository` que cumpla el contrato.
- Proveer un `RepositoryProvider` para seleccionar la implementación en tiempo de ejecución.
- Añadir pruebas de contrato e integración y documentar la configuración.

### Issues hijos

- `TASK-001` — Crear `DataRepository`
- `TASK-002` — Revisar y documentar mapping de `RecordJuego`
- `TASK-003` — Tests de contrato para `DataRepository` (memoria)
- `TASK-004` — Implementar `MongoDbRepository`
- `TASK-005` — Crear `MongoConfig`
- `TASK-006` — Mapping RecordJuego <-> BSON
- `TASK-007` — Pruebas de integración con Mongo (Flapdoodle/contendedor)
- `TASK-008` — Crear `RepositoryProvider`
- `TASK-009` — Crear `RepositoryMode` enum
- `TASK-010` — Documentar selección en `MainActivity`/ViewModel
- `TASK-011` — Pruebas de integración entre implementaciones
- `TASK-012` — Actualizar README con instrucciones de Mongo
- `TASK-013` — Añadir ejemplos de uso y README-PERSISTENCE

---

Favor de revisar las tareas y asignarlas en el tracker real (GitHub Issues) cuando proceda. Si quieres, puedo crear los issues directamente en GitHub si me indicas que tenga acceso a la API (no necesario para el repo local).
