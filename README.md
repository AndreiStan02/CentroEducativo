# CentroEducativo

# Acta de Reunión - 27/04/2026

**Proyecto:** CentroEducativo  
**Secretario:** Andrei  

---

## 1. Resumen de la sesión
En el día de hoy se han realizado las gestiones iniciales para el arranque técnico del proyecto:
* **GitHub:** Se ha creado el repositorio oficial y se ha inicializado el proyecto en el entorno remoto.
* **Roles:** Se ha establecido que **Andrei** ejercerá las funciones de secretario del equipo.

## 2. Acuerdos y compromisos
Se han fijado los siguientes puntos obligatorios para todos los integrantes:
1. **Configuración de Git:** Instalar y configurar Git en las máquinas locales (usuario y correo).
2. **Prueba de conexión:** Clonar el repositorio y verificar que el acceso y la descarga del código funcionan correctamente.

## 3. Próximos pasos
* Revisar en la siguiente sesión que todo el equipo tiene acceso de escritura y lectura al repositorio.

---

# Instalación de Git
Ejecuta el siguiente comando en tu terminal:
```bash
sudo apt install git
```

## 2. Configuración de Identidad
Configura tu nombre y correo electrónico. Esto es necesario para que tus *commits* queden registrados a tu nombre.
```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

## 3. Clonar el Repositorio
Crea una carpeta para tus proyectos y descarga el código del repositorio:
```bash
mkdir git
cd git
git clone https://github.com/AndreiStan02/CentroEducativo.git
cd CentroEducativo
```

## 4. Configuración de Acceso (Personal Access Token)
Debido a las políticas de seguridad de GitHub, debes usar un **Token** en lugar de tu contraseña.

### 4.1 Generar el Token en la Web
1. Ve a **GitHub** > **Settings** (clic en tu foto de perfil).
2. En el menú de la izquierda, baja hasta **Developer settings**.
3. Selecciona **Personal access tokens** > **Tokens (classic)**.
4. Haz clic en **Generate new token (classic)**.
5. **Nota:** Ponle un nombre (ej. "Mi PC"), elige la duración y **marca la casilla `repo`**.
6. Haz clic en **Generate token** y **COPIA EL CÓDIGO** inmediatamente (no se volverá a mostrar).

### 4.2 Vincular el Token a Git
Para no tener que escribir el token cada vez que hagas un `push`, configura la URL remota de la siguiente manera:

```bash
git remote set-url origin https://TU_USUARIO:TU_TOKEN@github.com/AndreiStan02/CentroEducativo.git
```

> [!IMPORTANT]
> Sustituye `TU_USUARIO` por tu nombre de usuario de GitHub y `TU_TOKEN` por el código que acabas de copiar.

---

## 5. Comandos útiles (Recordatorio)
* **Ver estado:** `git status`
* **Subir cambios:** 1. `git add .`
    2. `git commit -m "Descripción del cambio"`
    3. `git push TU_RAMA main`
* **Crear rama:** `git checkout -b NOMBRE_RAMA`
* **Cambiar de rama rama:** `git checkout NOMBRE_RAMA`
## 6. Workflow de trabajo (Importante)
* Crear rama desde main, trabajar dentro de esa rama, cuando todo este bien y funcione sin fallos push a main.
  
