# Creating GitHub Issues from Markdown Files

Este documento explica cómo crear issues de GitHub automáticamente desde los archivos markdown ubicados en `.github/issues/`.

## Archivos Disponibles

Los siguientes archivos de issues están listos para ser convertidos en GitHub Issues:

1. `epic-persistence-mongodb.md` - Epic principal de persistencia MongoDB
2. `task-001-create-datarepository.md` - Crear interfaz DataRepository
3. `task-002-review-recordjuego-mapping.md` - Revisar mapping RecordJuego a Mongo
4. `task-003-datarepository-contract-tests.md` - Tests de contrato DataRepository
5. `task-004-mongodbrepository.md` - Implementar MongoDbRepository
6. `task-005-mongoconfig.md` - Crear clase MongoConfig
7. `task-006-mapping-recordjuego-bson.md` - Mapping RecordJuego a BSON
8. `task-007-mongodb-tests-integration.md` - Tests de integración MongoDB
9. `task-008-repository-provider.md` - Crear RepositoryProvider
10. `task-009-repository-mode-enum.md` - Crear enum RepositoryMode
11. `task-010-document-selection.md` - Documentar selección de persistencia
12. `task-011-integration-tests.md` - Tests de integración completos
13. `task-012-update-readme.md` - Actualizar README principal
14. `task-013-examples-persistence-readme.md` - Ejemplos de uso de persistencia

## Métodos para Crear los Issues

### Método 1: GitHub Actions Workflow (Recomendado)

Este es el método más fácil y no requiere configuración local.

1. Ve a la pestaña "Actions" en el repositorio de GitHub
2. Selecciona el workflow "Create GitHub Issues from Markdown Files"
3. Haz clic en "Run workflow"
4. Selecciona la rama `features/mongodb`
5. Opcionalmente, activa "dry_run" para ver qué se crearía sin crear realmente los issues
6. Haz clic en "Run workflow"

El workflow:
- Leerá todos los archivos `.md` en `.github/issues/`
- Extraerá el título, descripción y etiquetas de cada archivo
- Creará un issue en GitHub por cada archivo
- Mostrará un resumen al finalizar

### Método 2: Script Python Local

Si prefieres ejecutar el script localmente:

**Requisitos previos:**
- Python 3.7 o superior
- GitHub CLI (`gh`) instalado y autenticado

**Instalación de GitHub CLI:**

```bash
# macOS
brew install gh

# Ubuntu/Debian
sudo apt install gh

# Windows
winget install GitHub.cli
```

**Autenticación:**

```bash
gh auth login
```

**Ejecución:**

```bash
# Desde la raíz del repositorio
python3 create-issues.py
```

### Método 3: Script Bash Local

Alternativa usando bash (requiere GitHub CLI):

```bash
# Asegúrate de estar autenticado
gh auth login

# Ejecuta el script
./create-issues.sh
```

## Formato de los Archivos de Issues

Cada archivo markdown tiene el siguiente formato:

```markdown
---
post_title: "TASK-XXX: Título del Issue"
author1: "Nombre del Autor"
post_slug: "task-xxx-slug"
microsoft_alias: "alias"
featured_image: ""
categories: ["feature"]
tags: ["tag1","tag2","tag3"]
ai_note: false
summary: "Resumen breve del issue"
post_date: "2026-01-18"
---

## Descripción

Descripción detallada del issue...

## Criterios de aceptación

- Criterio 1
- Criterio 2

## Estimación

- X horas/días
```

Los campos del frontmatter se utilizan para:
- `post_title`: Título del issue en GitHub
- `tags` y `categories`: Se convierten en labels del issue
- Todo el contenido después del frontmatter se usa como cuerpo del issue

## Verificación

Después de crear los issues, puedes verificar que se crearon correctamente visitando:

```
https://github.com/DanielRTato/SiDi_Persistencia/issues
```

## Troubleshooting

### Error: "gh: command not found"
- Instala GitHub CLI siguiendo las instrucciones arriba

### Error: "not authenticated"
- Ejecuta `gh auth login` y sigue las instrucciones

### Error: "permission denied"
- Asegúrate de tener permisos para crear issues en el repositorio

### Los issues se crean con las labels incorrectas
- Verifica que las labels existan en el repositorio
- GitHub creará automáticamente las labels si no existen

## Notas

- Los issues se crearán en el orden alfabético de los archivos
- Si un issue ya existe con el mismo título, se creará uno nuevo (GitHub permite títulos duplicados)
- Los scripts son idempotentes pero no detectan duplicados - úsalos con cuidado
- Se recomienda hacer una prueba con el modo "dry run" del workflow primero
