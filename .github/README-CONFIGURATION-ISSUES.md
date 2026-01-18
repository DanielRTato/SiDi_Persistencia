---
post_title: "README: Configuración .github e Issues creados"
author1: "Daniel Rodríguez Tato"
post_slug: "readme-configuracion-github-issues"
microsoft_alias: "DanielRTato"
featured_image: ""
categories: ["feature"]
tags: ["github","config","issues"]
ai_note: false
summary: "Descrición da configuración que hai na carpeta `.github` e listaxe dos issues creados para a epic de persistencia con MongoDB."
post_date: "2026-01-18"
---

## Descrición

Este documento explica de xeito conciso a configuración presente na carpeta `.github` do proxecto e lista
os issues que se crearon en GitHub para a implementación da persistencia desacoplada (epic: MongoDB).
O contido está orientado a quen precise revisar, reproducir ou modificar a configuración de automación e
os items de traballo xerados.

## Estrutura principal en `.github`

- `.github/issues/` — Contén os ficheiros Markdown que describen as tarefas (issues) preparadas localmente.
  Cada ficheiro contén front matter e unha descrición da tarefa.
- `.github/workflows/` — (Se existe) contén workflows de GitHub Actions para CI/Entrega.
- `.github/ISSUE_TEMPLATE/` — (Se existe) templates para crear issues desde a UI.

> Nota: os ficheiros en `.github/issues/` serven como fonte de verdade para os issues; foron usados para
> crear issues reais en GitHub. Podes editar eses md e reexecutar os scripts de creación para sincronizar cambios.

## Qué se configurou e por que

- Preparáronse 13 ficheiros de issue en `.github/issues/` que describen tarefas do epic "Persistencia desacoplada en MongoDB".
- Cada ficheiro segue un formato consistente con front matter e seccións: Descrición, Criterios de aceptación, Estimación.
- A idea é manter as tarefas versionadas no repo para facilitar revisións e reproducibilidade antes de publicar os issues.

## Issues creados en GitHub

A continuación a listaxe dos issues que se crearon no repositorio `DanielRTato/SiDi_Persistencia` (suxeridos para a rama `features/mongodb`):

- TASK-001: Crear `DataRepository` (interfaz)
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/2
- TASK-002: Revisar `RecordJuego` e documentar mapping a Mongo
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/3
- TASK-003: Tests de contrato para `DataRepository` (implementación en memoria)
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/4
- TASK-004: Implementar `MongoDbRepository`
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/5
- TASK-005: Crear `MongoConfig`
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/6
- TASK-006: Implementar mapping RecordJuego <-> BSON
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/7
- TASK-007: Pruebas de integración para `MongoDbRepository`
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/8
- TASK-008: Crear `RepositoryProvider` (Factory)
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/9
- TASK-009: Crear `RepositoryMode` enum
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/10
- TASK-010: Documentar cómo seleccionar implementación en la app
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/11
- TASK-011: Pruebas de integración entre implementaciones
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/12
- TASK-012: Actualizar README con instrucciones de Mongo
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/13
- TASK-013: Añadir ejemplos de uso y README-PERSISTENCE
  - https://github.com/DanielRTato/SiDi_Persistencia/issues/14

> Se non ves estes issues na UI de GitHub, refresca a páxina e comproba permisos; tamén é posible que as
> issues estean deshabilitadas no repo (entón habilítaas en Settings → Features).

## Como reproducir a creación de issues localmente

1. Instala GitHub CLI (gh): https://cli.github.com/
2. Autentica a CLI: 

```powershell
# Executar en PowerShell ou cmd con gh instalado
gh auth login
```

3. Desde a raíz do repo executa un script que lea `.github/issues/*.md` e cree cada issue usando `gh issue create`.

Exemplo de script PowerShell (simplificado):

```powershell
Get-ChildItem -Path ".github\issues" -Filter "*.md" | ForEach-Object {
  $file = $_.FullName
  $content = Get-Content $file -Raw
  if ($content -match 'post_title:\s*"(.*?)"') { $title = $matches[1] } else { $title = $_.BaseName }
  gh issue create --repo "DanielRTato/SiDi_Persistencia" --title $title --body-file $file --label "persistence,task"
  Start-Sleep -Milliseconds 300
}
```

> Lembranza: as issues non se crean "nunha rama"; a mención `features/mongodb` inclúese no corpo do issue como
> referencia á rama suxerida.

## Boas prácticas e suxestións

- Mantén os ficheiros de `.github/issues/` actualizados cando cambies os criterios de aceptación.
- Usa etiquetas e asignacións en GitHub para organizar prioridades e responsables.
- Se prefires, podes automatizar a sincronización (un pequeno GH Action que publique issues desde md) — pero
  require permisos e control de duplicados.

## Próximos pasos suxeridos

1. Revisar e asignar os issues en GitHub (labels, assignees, milestones).
2. Crear PR de base con plantillas (DataRepository, MongoConfig, RepositoryProvider) na rama `features/mongodb`.
3. Implementar tests de contrato (`TASK-003`) e validar que todas as implementacións pasan o contrato.

## Contacto

Profesor Damián Nogueiras

---

*Este ficheiro foi xerado para documentar a configuración local en `.github` e os issues creados; edita libremente.*

