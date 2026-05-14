# DOCUMENTACIÓN

## Instalación de Git
Ejecuta el siguiente comando en tu terminal:
```bash
sudo apt install git
```

### 2. Configuración de Identidad
Configura tu nombre y correo electrónico. Esto es necesario para que tus *commits* queden registrados a tu nombre.
```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

### 3. Clonar el Repositorio
Crea una carpeta para tus proyectos y descarga el código del repositorio:
```bash
mkdir git
cd git
git clone https://github.com/AndreiStan02/CentroEducativo.git
cd CentroEducativo
```

### 4. Configuración de Acceso (Personal Access Token)
Debido a las políticas de seguridad de GitHub, debes usar un **Token** en lugar de tu contraseña.

#### 4.1 Generar el Token en la Web
1. Ve a **GitHub** > **Settings** (clic en tu foto de perfil).
2. En el menú de la izquierda, baja hasta **Developer settings**.
3. Selecciona **Personal access tokens** > **Tokens (classic)**.
4. Haz clic en **Generate new token (classic)**.
5. **Nota:** Ponle un nombre (ej. "Mi PC"), elige la duración y **marca la casilla `repo`**.
6. Haz clic en **Generate token** y **COPIA EL CÓDIGO** inmediatamente (no se volverá a mostrar).

#### 4.2 Vincular el Token a Git
Para no tener que escribir el token cada vez que hagas un `push`, configura la URL remota de la siguiente manera:

```bash
git remote set-url origin https://TU_USUARIO:TU_TOKEN@github.com/AndreiStan02/CentroEducativo.git
```

> [!IMPORTANT]
> Sustituye `TU_USUARIO` por tu nombre de usuario de GitHub y `TU_TOKEN` por el código que acabas de copiar.

---

### 5. Comandos útiles (Recordatorio)
* **Ver estado:** `git status`
* **Subir cambios:** 1. `git add .`
    2. `git commit -m "Descripción del cambio"`
    3. `git push TU_RAMA main`
* **Crear rama:** `git checkout -b NOMBRE_RAMA`
* **Cambiar de rama rama:** `git checkout NOMBRE_RAMA`
### 6. Workflow de trabajo (Importante)
* Crear rama desde main, trabajar dentro de esa rama, cuando todo este bien y funcione sin fallos push a main.
  
---

## Servlet de Acceso (Login)

Se ha implementado un servlet que gestiona la autenticación de usuarios contra el backend CentroEducativo y establece la sesión en Tomcat.

### 1. Configuración en web.xml

La URL base del backend se externaliza como parámetro de contexto para garantizar portabilidad entre máquinas:

```xml
<context-param>
    <param-name>centroEducativoUrl</param-name>
    <param-value>http://localhost:9090/CentroEducativo</param-value>
</context-param>
```

Si el parámetro no está definido, el servlet usa `http://localhost:9090/CentroEducativo` como valor por defecto.

### 2. Lógica del servlet

Se ha implementado en el paquete `serverlets` en la clase `Acceso.java`. El servlet intercepta las peticiones de login, valida las credenciales contra el backend y crea la sesión HTTP en Tomcat.

#### 2.1 Flujo de autenticación (doPost)

**Paso 1 — Sesión existente:** Si ya existe una sesión activa con `key`, redirige directamente según el rol sin volver a autenticar.

**Paso 2 — Validación de parámetros:** Si `dni` o `password` están vacíos o nulos, redirige a `login.html?error=1`.

**Paso 3 — Login contra el backend:** Se hace un `POST /login` con el DNI y contraseña en formato JSON. Si el backend devuelve 200, se obtiene la `key` de sesión.

**Paso 4 — Determinación del rol:** Usando la misma `key`, se consulta primero `/profesores/{dni}` y luego `/alumnos/{dni}`. El primero que devuelva 200 determina el rol.

**Paso 5 — Creación de sesión:** Se guardan en la sesión HTTP de Tomcat los atributos `dni`, `pass`, `key` y `rol`.

**Paso 6 — Redirección:** Según el rol se redirige a la página correspondiente.

#### 2.2 Redirección según rol

| Rol | Página destino |
|---|---|
| `rolpro` | `/profesor-asignaturas.html` |
| `rolalu` | `/asignaturas_alumno.html` |
| `null` | `/login.html?error=1` |

---

## Filtro de Sesión

Se ha implementado un filtro de servlets que protege las páginas y servlets privados redirigiendo a `login.html` si no hay sesión activa.

### 1. Configuración

El filtro se configura mediante la anotación `@WebFilter`, sin necesidad de entrada en `web.xml`. Tomcat lo registra automáticamente al arrancar.

### 2. Lógica del filtro

Se ha implementado en el paquete `filtros` en la clase `SesionFilter.java`. El filtro intercepta cada petición a las rutas protegidas y comprueba si existe una sesión válida antes de permitir el acceso.

#### 2.1 Rutas protegidas

```java
@WebFilter(urlPatterns = {
    "/asignaturas_alumno.html",
    "/profesor-asignaturas.html",
    "/DetallesAlumnoServlet"
})
```

> [!IMPORTANT]
> Cada nueva página o servlet privado que se añada al proyecto debe incluirse en el array `urlPatterns` del filtro, o no quedará protegido.

#### 2.2 Criterio de autenticación

El filtro comprueba la existencia del atributo `key` en la sesión, que es el token establecido por el servlet `Acceso` tras un login exitoso:

```java
HttpSession session = req.getSession(false);
boolean autenticado = (session != null && session.getAttribute("key") != null);
```

#### 2.3 Comportamiento

| Condición | Acción |
|---|---|
| Sesión activa con `key` | Deja pasar la petición (`chain.doFilter`) |
| Sin sesión o sin `key` | Redirige a `/login.html` |

### 3. Control de errores

**`getSession(false)`:** Se usa `false` para no crear una sesión nueva si no existe, evitando sesiones vacías innecesarias.

---

## Sistema de filtros Logs (Versión 2)
Se ha implementado un filtro de servlets que registra cada interacción con la aplicación en un documento en orden cronológico.

### 1.Inicialización en web.xml
Para garantizar la portabilidad entre las distintas máquinas virtuales del equipo, se ha externalizado la ruta del archivo mediante un parámetro de contexto. El log se genera de forma relativa para evitar errores de permisos o rutas inexistentes.

### 2.Lógica del filtro
Se ha implementado el filtro en un paquete "filtros" y en una clase java: filtroLogs.java
El filtro intercepta cada petición HTTP, extrae los metadatos necesarios y los persiste en el fichero antes de permitir que la petición siga su curso.

#### 2.1 Datos registrados en el Log
**Fecha y hora:** Formato estandarizado del método LocalDateTime.now() -> **yyyy-MM-dd HH:mm:ss**
**Usuario:** NOmbre de usuario de la sesión. Si no hay sesión activa, se registra como anónimo.
**IP:** Dirección IP del cliente que realiza la petición HTTP.
**Recurso:** Servlet o página solicitada en la petición
**Método:** Tipo de petición HTTP (GET, POST)

> [!IMPORTANT]
> Al utilizar una ruta relativa, el archivo **no12526.log**, que contiene los registros en orden cronológico, se creará en el Workspace de Eclipse (la carpeta de ejecución de Tomcat)

### 3. Control de errores
**Append:** Se utiliza **new FileWriter(nombre, true)** para asegurar que las nuevas entradas se añadan al final del archivo sin borrar la actividad previa.
**Bloque try:** Garantiza el cierre automático del flujo de escritura (PrintWriter), asegurando que los datos se guarden físicamente en el disco incluso ante errores inesperados.
**Catch:** Si el sistema de archivos falla, el error se reporta por consola pero la aplicación web sigue funcionando, evitando que los errores de log bloqueen el trabajo del resto del equipo.

---

## Sistema de filtros Logs (Versión 2)
Se ha implementado un filtro de servlets que registra cada interacción con la aplicación en un documento en orden cronológico.

### 1.Inicialización en web.xml
Para garantizar la portabilidad entre las distintas máquinas virtuales del equipo, se ha externalizado la ruta del archivo mediante un parámetro de contexto. El log se genera de forma relativa para evitar errores de permisos o rutas inexistentes.

### 2.Lógica del filtro
Se ha implementado el filtro en un paquete "filtros" y en una clase java: filtroLogs.java
El filtro intercepta cada petición HTTP, extrae los metadatos necesarios y los persiste en el fichero antes de permitir que la petición siga su curso.

#### 2.1 Datos registrados en el Log
**Fecha y hora:** Formato estandarizado del método LocalDateTime.now() -> **yyyy-MM-dd HH:mm:ss**
**Usuario:** NOmbre de usuario de la sesión. Si no hay sesión activa, se registra como anónimo.
**IP:** Dirección IP del cliente que realiza la petición HTTP.
**Recurso:** Servlet o página solicitada en la petición
**Método:** Tipo de petición HTTP (GET, POST)

> [!IMPORTANT]
> Al utilizar una ruta relativa, el archivo **no12526.log**, que contiene los registros en orden cronológico, se creará en el Workspace de Eclipse (la carpeta de ejecución de Tomcat)

### 3. Control de errores
**Append:** Se utiliza **new FileWriter(nombre, true)** para asegurar que las nuevas entradas se añadan al final del archivo sin borrar la actividad previa.
**Bloque try:** Garantiza el cierre automático del flujo de escritura (PrintWriter), asegurando que los datos se guarden físicamente en el disco incluso ante errores inesperados.
**Catch:** Si el sistema de archivos falla, el error se reporta por consola pero la aplicación web sigue funcionando, evitando que los errores de log bloqueen el trabajo del resto del equipo.

---

## Servlets de Gestión de Alumnado

Se han implementado servlets específicos para gestionar la información que visualiza el alumno en su panel, comunicándose directamente con la API REST del backend `CentroEducativo`. Ambos servlets están protegidos por el filtro de sesión.

### 1. AsignaturasAlumnoServlet
Este servlet se encarga de recuperar y mostrar el listado de asignaturas en las que está matriculado un alumno.

* **Método HTTP:** `GET`
* **Lógica de ejecución:**
    1.  **Validación:** Extrae el `dni` y la `key` de la sesión HTTP actual.
    2.  **Petición a la API:** Construye la URL base y realiza una petición GET al endpoint `/alumnos/{dni}/asignaturas?key={key}` mediante la clase de utilidad `ApiClient`.
    3.  **Mapeo de datos:** Utiliza la librería `Gson` y `TypeToken` para convertir el JSON en crudo devuelto por la API en una lista de objetos Java `List<Asignatura>`.
    4.  **Redirección:** Inyecta la lista resultante en el `request` bajo el atributo `asignaturas` y delega la visualización a la vista `/asignaturas_alumno.html` usando `RequestDispatcher`.

### 2. DetallesAlumnoServlet
Este servlet construye el expediente detallado del alumno, combinando su información personal con su rendimiento académico.

* **Método HTTP:** `GET`
* **Lógica de ejecución:**
    1.  **Peticiones múltiples:** A diferencia del anterior, este servlet realiza dos llamadas a la API:
        * `/alumnos/{dni}?key={key}`: Para obtener la ficha personal (nombre, apellidos, etc.) y mapearlo a un objeto `Alumno`.
        * `/alumnos/{dni}/asignaturas?key={key}`: Para obtener las materias cursadas.
    2.  **Lógica temporal (Hito 1):** Dado que la API (Swagger) no expone claramente las notas dentro del listado de asignaturas, se ha implementado una nota media "dummy" (8.5) para cumplir con los requisitos visuales de esta primera entrega.
    3.  **Requisitos de la práctica:** Se inyecta un texto *Lorem Ipsum* estático en el atributo `loremIpsum`, tal y como exige el enunciado del Hito 1.
    4.  **Redirección:** Empaqueta todos los datos (`alumno`, `asignaturas`, `notaMedia`, `loremIpsum`) y redirige a la vista `/detalles_alumno.html`.

### Gestión de Errores en Servlets
En ambos servlets, si ocurre algún fallo de red (conexión caída con la API) o error de parseo JSON, la excepción es capturada en un bloque `catch`, se imprime en la salida estándar de errores (consola) y se redirige de forma segura al usuario a `/login-error.html` para evitar exponer las trazas al cliente.
---

## Cliente de Comunicación API (ApiClient)

Para evitar la duplicación de código en los distintos servlets y centralizar la lógica de comunicación con el backend `CentroEducativo`, se ha creado una clase de utilidad en el paquete `api`.

### Clase ApiClient
Esta clase actúa como un envoltorio (wrapper) para realizar peticiones HTTP hacia la API REST externa utilizando la librería **Apache HttpClient 5**.

#### Método: `obtenerDatosGet(String urlFinal)`
Es un método estático y reutilizable diseñado específicamente para realizar peticiones HTTP de tipo `GET`. 

* **Lógica de ejecución:**
    1. **Configuración del Cliente:** Inicializa un cliente HTTP por defecto (`CloseableHttpClient`).
    2. **Preparación de la Petición:** Crea un objeto `HttpGet` con la URL final construida previamente por los servlets (que ya incluye el DNI y la *key* de sesión).
    3. **Negociación de Contenido:** Establece explícitamente la cabecera `Accept` a `application/json` para asegurar que el backend devuelve los datos en el formato correcto para ser procesados posteriormente por Gson.
    4. **Ejecución y Validación:** Ejecuta la petición y evalúa el código de estado HTTP de la respuesta:
        * Si el código es **200 (OK)**: Extrae el contenido del cuerpo de la respuesta como una cadena de texto (String) utilizando `EntityUtils.toString()` y lo retorna al servlet llamador.
        * Si el código es **distinto de 200**: Lanza una excepción indicando que hubo un error en la API junto con el código recibido, lo cual interrumpe el flujo y transfiere el control al bloque `catch` del servlet.
* **Gestión de Recursos:** Implementa bloques `try-with-resources` tanto para el cliente HTTP como para la respuesta (`CloseableHttpResponse`). Esto garantiza que las conexiones de red se cierren correctamente de forma automática tras su uso o en caso de error, previniendo fugas de memoria.
---

## Capa de Presentación (Frontend y Vistas)

La interfaz de usuario se ha desarrollado garantizando un diseño responsivo y accesible, utilizando **Bootstrap 5** a través de CDN, complementado con reglas CSS propias para la adaptación visual del sistema.

### 1. Estructura de Vistas
Las vistas se encuentran en el directorio `src/main/webapp/` y se dividen en estáticas y dinámicas:

* **Vistas estáticas (`.html`):** * `index.html`: Portal de bienvenida que redirige a los usuarios según su perfil.
    * `login.html`: Formulario de autenticación que envía las credenciales al backend (Servlet `Acceso`).
    * `profesor-evaluacion.html`: Interfaz para calificar alumnos diseñada con controles interactivos. 
* **Vistas dinámicas (`.jsp`):**
    * `asignaturas_alumno.jsp` y `detalles_alumno.jsp`: Páginas preparadas con la directiva de página de Jakarta EE para recibir e iterar sobre los datos enviados por los servlets del alumno.
    * `profesor-asignaturas.jsp`: Listado dinámico de las materias impartidas por el docente.

### 2. Estilos y Scripts (`/css` y `/scripts`)
* **`css/estilos.css`:** Contiene los ajustes visuales específicos del proyecto (colores corporativos, sombreados de tarjetas y ajustes de layout) que sobrescriben o complementan a Bootstrap.
* **`scripts/evaluacion-ajax.js`:** Archivo JavaScript encargado de gestionar la lógica del cliente en la pantalla de evaluación. Actualmente implementa una simulación (mock) de carga de datos mediante un array en memoria para probar el carrusel interactivo circular de alumnos, así como la simulación visual del guardado de calificaciones sin recargar la página.