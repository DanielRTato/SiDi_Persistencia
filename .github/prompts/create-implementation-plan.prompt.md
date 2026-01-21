---
agent: 'agent'
description: 'Crear un nuevo archivo de plan de implementación para nuevas características, refactorización de código existente o actualización de paquetes, diseño, arquitectura o infraestructura.'
tools: ['changes', 'search/codebase', 'edit/editFiles', 'extensions', 'fetch', 'githubRepo', 'openSimpleBrowser', 'problems', 'runTasks', 'search', 'search/searchResults', 'runCommands/terminalLastCommand', 'runCommands/terminalSelection', 'testFailure', 'usages', 'vscodeAPI']
---
# Crear Plan de Implementación

## Directiva Principal

Tu objetivo es crear un nuevo archivo de plan de implementación para `${input:PlanPurpose}`. Tu salida debe ser legible por máquina, determinista y estructurada para su ejecución autónoma por otros sistemas de IA o humanos.
**Usa el idioma "Castellano" para redactar el contenido del plan.**

## Contexto de Ejecución

Este prompt está diseñado para la comunicación de IA a IA y el procesamiento automatizado. Todas las instrucciones deben interpretarse literalmente y ejecutarse sistemáticamente sin interpretación ni aclaración humana.

## Requisitos Centrales

- Generar planes de implementación que sean totalmente ejecutables por agentes de IA o humanos.
- Usar lenguaje determinista con cero ambigüedad.
- Estructurar todo el contenido en formatos analizables por máquina (tablas, listas, datos estructurados).
- Garantizar la autocontención completa sin dependencias externas para su comprensión.

## Requisitos de Estructura del Plan

Los planes deben consistir en fases discretas y atómicas que contengan tareas ejecutables. Cada fase debe ser procesable independientemente por agentes de IA o humanos sin dependencias entre fases a menos que se declaren explícitamente.

## Arquitectura de Fase

- Cada fase debe tener criterios de finalización medibles.
- Las tareas dentro de las fases deben ser ejecutables en paralelo a menos que se especifiquen dependencias.
- Todas las descripciones de tareas deben incluir rutas de archivo específicas, nombres de funciones y detalles exactos de implementación.
- Ninguna tarea debe requerir interpretación o toma de decisiones humana.

## Estándares de Implementación Optimizados para IA

- Usar lenguaje explícito e inequívoco sin necesidad de interpretación.
- Estructurar todo el contenido como formatos analizables por máquina.
- Incluir rutas de archivo específicas, números de línea y referencias de código exactas donde corresponda.
- Definir todas las variables, constantes y valores de configuración explícitamente.
- Proporcionar contexto completo dentro de la descripción de cada tarea.
- Usar prefijos estandarizados para todos los identificadores (REQ-, TASK-, etc.).
- Incluir criterios de validación que puedan verificarse automáticamente.

## Especificaciones del Archivo de Salida

- Guardar los archivos del plan de implementación en el directorio `/plan/`.
- Usar la convención de nombres: `[propósito]-[componente]-[versión].md`.
- Prefijos de propósito: `upgrade|refactor|feature|data|infrastructure|process|architecture|design` (actualización|refactorización|característica|datos|infraestructura|proceso|arquitectura|diseño).
- Ejemplo: `upgrade-system-command-4.md`, `feature-auth-module-1.md`.
- El archivo debe ser Markdown válido con una estructura de *front matter* adecuada.

## Estructura de Plantilla Obligatoria

Todos los planes de implementación deben adherirse estrictamente a la siguiente plantilla. Cada sección es obligatoria y debe completarse con contenido específico y accionable. Los agentes de IA deben validar el cumplimiento de la plantilla antes de la ejecución.

## Reglas de Validación de Plantilla

- Todos los campos del *front matter* deben estar presentes y formateados correctamente.
- Todos los encabezados de sección deben coincidir exactamente (distinguen mayúsculas y minúsculas).
- Todos los prefijos de identificadores deben seguir el formato especificado.
- Las tablas deben incluir todas las columnas requeridas.
- No debe quedar texto de marcador de posición (placeholder) en la salida final.

## Estado

El estado del plan de implementación debe definirse claramente en el *front matter* y debe reflejar el estado actual del plan. El estado puede ser uno de los siguientes (color del estado entre paréntesis): `Completed` (insignia verde brillante), `In progress` (insignia amarilla), `Planned` (insignia azul), `Deprecated` (insignia roja), u `On Hold` (insignia naranja). También debe mostrarse como una insignia (badge) en la sección de introducción.


---
goal: [Título conciso que describa el objetivo del Plan de Implementación del Paquete]
version: [Opcional: ej., 1.0, Fecha]
date_created: [AAAA-MM-DD]
last_updated: [Opcional: AAAA-MM-DD]
owner: [Opcional: Equipo/Individuo responsable de esta especificación]
status: 'Completed'|'In progress'|'Planned'|'Deprecated'|'On Hold'
tags: [Opcional: Lista de etiquetas relevantes, ej., `feature`, `upgrade`, `chore`, `architecture`, `migration`, `bug` etc]
---

# Introduction

![Status: <status>](https://img.shields.io/badge/status-<status>-<status_color>)

[Una introducción corta y concisa al plan y el objetivo que se pretende lograr (en Gallego).]

## 1. Requirements & Constraints

[Enumera explícitamente todos los requisitos y restricciones que afectan al plan y limitan cómo se implementa. Usa viñetas o tablas para mayor claridad.]

- **REQ-001**: Requisito 1
- **SEC-001**: Requisito de Seguridad 1
- **[3 LETRAS]-001**: Otro Requisito 1
- **CON-001**: Restricción 1
- **GUD-001**: Directriz 1
- **PAT-001**: Patrón a seguir 1

## 2. Implementation Steps

### Implementation Phase 1

- GOAL-001: [Describe el objetivo de esta fase, ej., "Implementar característica X", "Refactorizar módulo Y", etc.]

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-001 | Descripción de la tarea 1 | ✅ | 2025-04-25 |
| TASK-002 | Descripción de la tarea 2 | |  |
| TASK-003 | Descripción de la tarea 3 | |  |

### Implementation Phase 2

- GOAL-002: [Describe el objetivo de esta fase]

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-004 | Descripción de la tarea 4 | |  |
| TASK-005 | Descripción de la tarea 5 | |  |
| TASK-006 | Descripción de la tarea 6 | |  |

## 3. Alternatives

[Una lista con viñetas de cualquier enfoque alternativo que se consideró y por qué no se eligió. Esto ayuda a proporcionar contexto y justificación para el enfoque elegido.]

- **ALT-001**: Enfoque alternativo 1
- **ALT-002**: Enfoque alternativo 2

## 4. Dependencies

[Enumera cualquier dependencia que deba abordarse, como bibliotecas, marcos de trabajo u otros componentes en los que se basa el plan.]

- **DEP-001**: Dependencia 1
- **DEP-002**: Dependencia 2

## 5. Files

[Enumera los archivos que se verán afectados por la característica o la tarea de refactorización.]

- **FILE-001**: Descripción del archivo 1
- **FILE-002**: Descripción del archivo 2

## 6. Testing

[Enumera las pruebas que deben implementarse para verificar la característica o la tarea de refactorización.]

- **TEST-001**: Descripción de la prueba 1
- **TEST-002**: Descripción de la prueba 2

## 7. Risks & Assumptions

[Enumera cualquier riesgo o suposición relacionada con la implementación del plan.]

- **RISK-001**: Riesgo 1
- **ASSUMPTION-001**: Suposición 1

## 8. Related Specifications / Further Reading

[Enlace a especificación relacionada 1]
[Enlace a documentación externa relevante]