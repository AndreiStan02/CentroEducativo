// Variable global para almacenar la lista de alumnos que nos devuelva el servidor
let listaAlumnos = [];
let indiceActual = 0;

// Esta función se ejecuta nada más cargar la página HTML
document.addEventListener("DOMContentLoaded", () => {
    cargarAlumnosDesdeServidor();
});

function cargarAlumnosDesdeServidor() {
    console.log("Pidiendo datos al backend...");

    // SIMULACIÓN (Hasta que el backend haga su parte, usamos estos datos falsos)
    const jsonSimuladoDelBackend = [
        { "dni": "23456387R", "nombre": "Fernandez Gómez, Maria", "nota": 8.5 },
        { "dni": "12345678W", "nombre": "García Sánchez, Pepe", "nota": null }, 
        { "dni": "34567891F", "nombre": "Hernandez Llopis, Miguel", "nota": 5.0 }
    ];

    // Por ahora usamos los simulados:
    listaAlumnos = jsonSimuladoDelBackend;
    mostrarAlumnoEnPantalla(0); // Mostramos el primer alumno
}

function mostrarAlumnoEnPantalla(indice) {
    if (listaAlumnos.length === 0) return;

    // Lógica para que el carrusel sea circular
    if (indice < 0) indice = listaAlumnos.length - 1;
    if (indice >= listaAlumnos.length) indice = 0;

    indiceActual = indice;
    let alumno = listaAlumnos[indiceActual];

    // Inyectar los datos en el HTML
    document.getElementById("nombreAlumno").innerText = alumno.nombre;
    document.getElementById("dniAlumno").innerText = alumno.dni;
    
    // Inyectamos el nombre de la foto
    document.getElementById("fotoAlumno").innerText = alumno.dni + ".png"; 
    
    // Ponemos la nota (si es null, lo dejamos vacío)
    document.getElementById("notaAlumno").value = alumno.nota !== null ? alumno.nota : "";
    
    // Reseteamos el estado visual
    document.getElementById("estadoGuardado").innerText = "Nota sincronizada";
    document.getElementById("estadoGuardado").className = "text-muted mt-2 d-block";
}

function cambiarAlumno(direccion) {
    mostrarAlumnoEnPantalla(indiceActual + direccion);
}

function guardarNota() {
    let nuevaNota = document.getElementById("notaAlumno").value;
    let dniActual = listaAlumnos[indiceActual].dni;
    
    let estado = document.getElementById("estadoGuardado");
    estado.innerText = "Guardando...";
    estado.className = "text-warning mt-2 d-block";

    console.log(`Enviando por POST al servidor -> DNI: ${dniActual}, Nota: ${nuevaNota}`);

    // Simulación de que el servidor responde OK en 1 segundo:
    setTimeout(() => {
        listaAlumnos[indiceActual].nota = nuevaNota; 
        
        estado.innerText = "✓ Nota guardada correctamente";
        estado.className = "text-success mt-2 d-block";
    }, 1000);
}