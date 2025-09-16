# WildHorse App

## Descripción General

WildHorse App es una aplicación Android robusta diseñada para la gestión integral de ganado equino, enfocándose en el seguimiento detallado de la salud, reproducción y ubicación de los animales. La aplicación busca facilitar y optimizar las operaciones diarias en establecimientos dedicados a la cría y cuidado de caballos.

Esta aplicación permite a los usuarios:
*   Registrar y administrar información detallada de cada caballo (vientres, sementales, crías).
*   Realizar un seguimiento preciso de ciclos reproductivos, incluyendo fechas de monta, preñez y partos estimados.
*   Gestionar usuarios y permisos dentro de la aplicación para un control de acceso seguro.
*   Visualizar la ubicación de los animales en un mapa interactivo.
*   Implementar geocercas para recibir alertas sobre el movimiento de los animales.
*   Mantener un registro de auditoría de todas las acciones importantes realizadas dentro del sistema.
*   Recibir notificaciones y alertas relevantes, como fechas de parto próximas.

## Capturas de Pantalla (Opcional pero Recomendado)

<!-- 
Añade aquí capturas de pantalla de tu aplicación. 
Sube las imágenes a una carpeta en tu repositorio (ej. `docs/screenshots/`) y enlaza a ellas.
Ejemplos:
![Pantalla Principal](docs/screenshots/main_screen.png)
![Gestión de Vientres](docs/screenshots/vientres_management.png)
![Mapa de Ubicación](docs/screenshots/map_view.png)
-->

## Tecnologías Utilizadas

Este proyecto está construido utilizando las siguientes tecnologías, librerías y plataformas:

### Frontend (Aplicación Android - WildHorse)
*   **Lenguaje de Programación:** Java
*   **IDE de Desarrollo:** Android Studio
*   **Arquitectura (Sugerida/Parcial):** Orientada a eventos con Activities gestionando la UI y lógica de negocio directamente, interactuando con Firebase.
*   **Componentes Principales de Android Jetpack y SDK:**
    *   `Activities` (para cada pantalla principal y funcionalidad)
    *   `ListView` y `ArrayAdapter` (para mostrar listas de datos como vientres, permisos, etc.)
    *   `DatePickerDialog` (para la selección interactiva de fechas)
    *   `AlertDialog` (para mostrar mensajes y confirmaciones al usuario)
    *   `NotificationCompat`, `NotificationManager`, `NotificationChannel` (para notificaciones locales en la barra de estado, ej. alertas de parto)
    *   `BroadcastReceiver` (específicamente `GeofenceBroadcastReceiver` para geocercas)
    *   Permisos de Android (Internet, Localización Fina/Gruesa/En Segundo Plano, Notificaciones Post)
    *   `SharedPreferences` (potencialmente para configuraciones simples o tokens de sesión)
*   **Interfaz de Usuario (UI):**
    *   Layouts definidos en XML.
    *   View Binding (ej. `ActivityVientresBinding`) para acceso seguro a vistas.
    *   Componentes de UI estándar de Android.
    *   Estilos y Temas personalizados (ej. `@style/Theme.LookCow`, `@style/Base.Theme.LookCowMapa`).
*   **Mapas y Localización:**
    *   **Google Maps SDK para Android:** Para mostrar mapas e interactuar con ellos.
    *   **Android Location Services:** Para obtener la ubicación del dispositivo.
    *   **Geofencing API:** Para crear y monitorear geocercas (`GeofenceBroadcastReceiver`).
*   **Manejo de Fechas y Tiempo:**
    *   `java.time.LocalDate` y `java.time.format.DateTimeFormatter` (para un manejo moderno de fechas).
    *   `java.util.Calendar` (usado en algunas partes para obtener la fecha actual).

### Backend y Base de Datos
*   **Firebase Realtime Database:**
    *   Base de datos NoSQL en la nube utilizada como el principal almacén de datos.
    *   Almacena información de Vientres, Permisos, Usuarios, Grupos, Razas, Temperaturas, Ganado, Dispositivos, Auditorías.
    *   Sincronización de datos en tiempo real con la aplicación cliente.
*   **Firebase Authentication (Asumido/Recomendado):**
    *   Para la gestión segura de la autenticación de usuarios (registro, inicio de sesión, recuperación de contraseña). Las activities `LoginActivity`, `RegisterActivity`, `ForgotPasswordActivity` sugieren su uso.

### Herramientas de Desarrollo y Control de Versiones
*   **Git:** Sistema de control de versiones distribuido.
*   **GitHub:** Plataforma para hospedar el repositorio de código fuente y colaborar.
*   **Gradle:** Sistema de automatización de compilación para Android.

## Características Implementadas

*   **Módulo de Sesión de Usuario:**
    *   Registro, Inicio de Sesión, Cierre de Sesión.
    *   Gestión de Perfil de Usuario (actualizar datos, email, contraseña, foto de perfil).
    *   Recuperación de Contraseña.
    *   Eliminación de Perfil.
*   **Módulo de Vientres:**
    *   CRUD completo (Crear, Leer, Actualizar, Eliminar) para registros de vientres.
    *   Cálculo automático y visualización de fechas de preñez y parto.
    *   Registro de observaciones y estado de los animales.
*   **Módulo de Ganado General:**
    *   Gestión de información de otros tipos de ganado.
*   **Módulos de Configuración/Catálogos:**
    *   Gestión de Grupos, Permisos, Razas, Dispositivos.
*   **Módulo de Auditoría:**
    *   Registro y visualización de acciones clave realizadas en la aplicación para seguimiento y control.
*   **Módulo de Temperatura:**
    *   Registro y seguimiento de temperaturas (presumiblemente de animales o ambiente).
*   **Funcionalidades de Mapa y Localización:**
    *   Visualización de la ubicación en Google Maps (`InicioActivity`).
    *   Implementación de Geofencing para alertas de entrada/salida de zonas predefinidas.
*   **Notificaciones:**
    *   Alertas en la barra de estado para eventos críticos, como fechas de parto inminentes.
*   **Navegación:**
    *   Uso de un `DrawerLayout` (`DrawerBaseActivity`) para una navegación consistente y accesible a través de las diferentes secciones de la aplicación.
*   **Interfaz de Usuario:**
    *   Entrada de datos mediante formularios (`EditText`, `Button`).
    *   Visualización de datos en listas (`ListView`).
    *   Selección de fechas mediante `DatePicker`.

## Configuración del Proyecto

Para configurar y ejecutar este proyecto localmente:

1.  **Requisitos Previos:**
    *   Android Studio (última versión estable recomendada).
    *   Git.
    *   Una cuenta de Google y un proyecto configurado en Firebase.
2.  **Configuración de Firebase:**
    *   Ve a [Firebase Console](https://console.firebase.google.com/) y crea un nuevo proyecto (o usa uno existente).
    *   Añade una aplicación Android a tu proyecto Firebase. Asegúrate de que el nombre del paquete sea `com.example.crudejemplo` (o el que corresponda a tu proyecto WildHorse).
    *   Descarga el archivo `google-services.json` de la configuración de tu proyecto Firebase y colócalo en el directorio `app/` de tu proyecto Android.
    *   En la sección de Realtime Database, configura tus reglas de seguridad. Para desarrollo, puedes usar:
          json { "rules": { ".read": "true", // o "auth != null" para producción ".write": "true" // o "auth != null" para producción } }
    * Habilita los métodos de autenticación que necesites en Firebase Authentication (ej. Email/Contraseña).
3.  **Clave de API de Google Maps:**
    *   Ve a [Google Cloud Console](https://console.cloud.google.com/).
    *   Selecciona tu proyecto (o crea uno nuevo, que puede ser el mismo que usa Firebase).
    *   Habilita la API "Maps SDK for Android".
    *   Crea una clave de API. **Asegúrate de restringir esta clave** para evitar usos no autorizados (restringir por aplicación Android usando el nombre de paquete y la huella SHA-1 de tu certificado de firma, y restringir a las APIs que usas).
    *   Añade la clave de API al archivo `local.properties` en la raíz de tu proyecto Android:
        properties MAPS_API_KEY=TU_CLAVE_ DE_ API_ DE_ GOOGLE_ MAPS