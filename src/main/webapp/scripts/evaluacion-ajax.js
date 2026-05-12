// Variable global para almacenar la lista de alumnos que nos devuelva el servidor
let listaAlumnos = [];
let indiceActual = 0;

// Esta función se ejecuta nada más cargar la página HTML
document.addEventListener("DOMContentLoaded", () => {
    cargarAlumnosDesdeServidor();
});

function cargarAlumnosDesdeServidor() {
    console.log("Pidiendo datos al backend...");

    /* ====================================================================
     ATENCIÓN BACKEND: ESTO ES LO QUE TENÉIS QUE PROGRAMAR.
     El frontend hará un fetch() a un Servlet (ej: ObtenerAlumnosServlet).
     Ese Servlet DEBE devolver un JSON con esta estructura exacta:
     ====================================================================
    */
    
    // SIMULACIÓN (Hasta que el backend haga su parte, usamos estos datos falsos para probar el frontend)
    const jsonSimuladoDelBackend = [
        { "dni": "23456387R", "nombre": "Fernandez Gómez, Maria", "nota": 8.5 },
        { "dni": "12345678W", "nombre": "García Sánchez, Pepe", "nota": null }, // null si no está calificado
        { "dni": "34567891F", "nombre": "Hernandez Llopis, Miguel", "nota": 5.0 }
    ];

    // Cuando el backend esté listo, borraremos la simulación y descomentaremos esto:
    /*
    fetch('ObtenerAlumnosServlet?asignatura=DEW')
        .then(response => response.json())
        .then(data => {
            listaAlumnos = data;
            mostrarAlumnoEnPantalla(0);
        });
    */

    // Por ahora usamos los simulados:
    listaAlumnos = jsonSimuladoDelBackend;
    mostrarAlumnoEnPantalla(0); // Mostramos el primer alumno
}

function mostrarAlumnoEnPantalla(indice) {
    if (listaAlumnos.length === 0) return;

    // Lógica para que el carrusel sea circular (si pasas del último, vuelve al primero)
    if (indice < 0) indice = listaAlumnos.length - 1;
    if (indice >= listaAlumnos.length) indice = 0;

    indiceActual = indice;
    let alumno = listaAlumnos[indiceActual];

    // Inyectar los datos en el HTML
    document.getElementById("nombreAlumno").innerText = alumno.nombre;
    document.getElementById("dniAlumno").innerText = alumno.dni;
    
    // Inyectamos el nombre de la foto (El backend deberá tener la imagen con este nombre)
    document.getElementById("fotoAlumno").innerText = alumno.dni + ".png"; 
    
    // Ponemos la nota (si es null, lo dejamos vacío)
    document.getElementById("notaAlumno").value = alumno.nota !== null ? alumno.nota : "";
    
    // Reseteamos el estado visual
    document.getElementById("estadoGuardado").innerText = "Nota sincronizada";
    document.getElementById("estadoGuardado").className = "text-muted mt-2 d-block";
}

function cambiarAlumno(direccion) {
    // direccion es -1 (anterior) o 1 (siguiente)
    mostrarAlumnoEnPantalla(indiceActual + direccion);
}

function guardarNota() {
    let nuevaNota = document.getElementById("notaAlumno").value;
    let dniActual = listaAlumnos[indiceActual].dni;
    
    let estado = document.getElementById("estadoGuardado");
    estado.innerText = "Guardando...";
    estado.className = "text-warning mt-2 d-block";

    /*
     ====================================================================
     ATENCIÓN BACKEND: 
     El frontend hará un POST a GuardarNotaServlet enviando el DNI y la nota.
     ====================================================================
    */
    console.log(`Enviando por POST al servidor -> DNI: ${dniActual}, Nota: ${nuevaNota}`);

    // Simulación de que el servidor responde OK en 1 segundo:
    setTimeout(() => {
        // Actualizamos nuestra lista local
        listaAlumnos[indiceActual].nota = nuevaNota; 
        
        estado.innerText = "✓ Nota guardada correctamente";
        estado.className = "text-success mt-2 d-block";
    }, 1000);
}