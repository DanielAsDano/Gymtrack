# GymTracker - Sistema de Seguimiento de Ejercicios

Aplicación Java con interfaz gráfica para registrar y hacer seguimiento de tus entrenamientos en el gimnasio.

## Estructura del Proyecto

```
Gymtrack/
├── lib/                      # Librerías externas (drivers JDBC)
│   ├── sqlite-jdbc.jar
│   ├── slf4j-api.jar
│   └── slf4j-simple.jar
├── src/                      # Código fuente Java
│   ├── Main.java            # Archivo principal
│   ├── GymTrackerGUI.java   # Interfaz gráfica
│   ├── Ejercicio.java       # Modelo de datos
│   ├── EjercicioDAO.java    # Acceso a datos (CRUD)
│   └── DatabaseManager.java # Gestión de base de datos
├── database/                # Base de datos SQLite
│   └── Gymtracker.db
└── README.md
```

## Requisitos

- Java JDK 8 o superior
- Las dependencias ya están incluidas en la carpeta `lib/`:
  - `sqlite-jdbc.jar` - Driver JDBC de SQLite
  - `slf4j-api.jar` - API de logging (dependencia de sqlite-jdbc)
  - `slf4j-simple.jar` - Implementación simple de logging

## Compilación y Ejecución

### Opción 1: Usar scripts (Windows)
1. **Compilar:**
   ```bash
   compile.bat
   ```

2. **Ejecutar:**
   ```bash
   run.bat
   ```

### Opción 2: Comandos manuales
1. **Compilar el proyecto:**
   ```bash
   javac -cp "lib/*" src/*.java -d .
   ```

2. **Ejecutar el programa:**
   ```bash
   java -cp ".;lib/*" Main
   ```

## Funcionalidades

### Interfaz Gráfica
- **Tema oscuro** con tonos azules estilo Gymshark
- **Diseño responsive** que se adapta al tamaño de la ventana
- **Navegación intuitiva** con menú y páginas organizadas

### Página Principal
- **Lista simple** de ejercicios guardados (solo nombres)
- **Doble clic** en un ejercicio para ver su detalle completo
- **Botones**: Nuevo Ejercicio y Refrescar

### Detalle de Ejercicio
- **Tabla de historial** con todos los registros del ejercicio (ID, Peso, Repeticiones, Fecha)
- **Formulario para agregar registros**:
  - Peso editable con teclado y botones +0.5/-0.5 kg
  - Repeticiones editables con teclado y botones +1/-1
  - Fecha con botón "Hoy"
  - **Los campos se cargan automáticamente** con los valores del último registro
- **Botón Guardar** que actualiza la base de datos SQL

### Nuevo Ejercicio
- Formulario completo para crear un nuevo ejercicio:
  - Nombre del ejercicio
  - Grupo muscular (Pecho, Espalda, Piernas, Brazos, Hombros, Core, Glúteos, Otro)
  - Peso y repeticiones con controles incrementales
  - Fecha del entrenamiento

### Base de Datos
- Almacenamiento persistente en SQLite
- La tabla se crea automáticamente al iniciar la aplicación
- Datos guardados en `database/Gymtracker.db`

## Características Adicionales

- **Validación de datos**: El sistema valida que los campos requeridos estén completos
- **Carga automática**: Al abrir el detalle de un ejercicio, los campos se cargan con el último registro
- **Mensajes informativos**: Notificaciones de éxito o error en las operaciones
- **Menú de navegación**: Acceso rápido a todas las funcionalidades

## Optimizaciones

- **Código optimizado**: Eliminación de duplicación, métodos auxiliares reutilizables
- **Gestión de recursos**: Uso de try-with-resources para conexiones de base de datos
- **Archivos compilados**: Los archivos `.class` se generan en la raíz (se pueden limpiar con `Remove-Item *.class`)

## Notas

- El programa crea automáticamente la tabla `ejercicios` en la base de datos si no existe
- La fecha es opcional pero recomendable para hacer seguimiento de la progresión
- Los datos se guardan de forma persistente en la base de datos SQLite
- Al agregar un nuevo registro, los campos se inicializan con los valores del último registro guardado

