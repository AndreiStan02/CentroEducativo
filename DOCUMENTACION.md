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
