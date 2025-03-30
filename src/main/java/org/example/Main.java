package org.example;

import org.example.dtos.Competencia;
import org.example.dtos.Inscripcion;
import org.example.dtos.Usuario;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Competencia> competiciones = new ArrayList<>();
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Inscripcion> inscripciones = new ArrayList<>();
    private static String institucionActual;
    private static String idActual;

    public static void main(String[] args) {
        System.out.println("===== BIENVENIDO AL SISTEMA =====");
        System.out.print("Ingrese su correo: ");
        String correoIngresado = scanner.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String contraseñaIngresada = scanner.nextLine();

        String tipoUsuario = validarUsuario(correoIngresado, contraseñaIngresada);
        cargarUsuariosDesdeArchivo();
        cargarInscripciones();
        if (tipoUsuario != null) {
            System.out.println("Inicio de sesión exitoso. ¡Bienvenido!");

            if (tipoUsuario.equals("INSTITUCION")) {
                institucionActual = correoIngresado;
                limpiarConsola();
                menuInstitucion();
            } else {
                if (tipoUsuario.equals("ESTUDIANTE")) {
                    institucionActual = correoIngresado;
                    limpiarConsola();
                    menuEstudiante();
                } else {
                    System.out.println("Acceso concedido, pero sin rol identificado.");
                }
            }
        } else {
            System.out.println("Error: Correo o contraseña incorrectos.");
        }

        scanner.close();
    }

    public static String validarUsuario(String correo, String contraseña) {
        File archivo = new File("src/main/java/org/example/data/usuarios.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 5) {
                    String id = datos[0].trim();
                    String correoRegistrado = datos[2].trim();
                    String contraseñaRegistrada = datos[3].trim();
                    String tipoUsuario = datos[4].trim().toUpperCase(); // "INSTITUCION" o "USUARIO"

                    if (correoRegistrado.equals(correo) && contraseñaRegistrada.equals(contraseña)) {
                        idActual = id;
                        return tipoUsuario;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return null;
    }

    private static void menuEstudiante() {
        while (true) {
            limpiarConsola();
            System.out.println("\n--- MENÚ INSTITUCIONES ---");
            System.out.println("1. Enlistar Competiciones");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    limpiarConsola();
                    enlistarCompeticionesEstudiante();
                    break;
                case "2":
                    limpiarConsola();
                    System.out.println("Saliendo del menú...");
                    return;
                default:
                    limpiarConsola();
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private static void menuInstitucion() {
        while (true) {
            limpiarConsola();
            System.out.println("\n--- MENÚ INSTITUCIONES ---");
            System.out.println("1. Enlistar Competiciones");
            System.out.println("2. Agregar Competición");
            System.out.println("3. Mis Competiciones");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    limpiarConsola();
                    enlistarCompeticiones();
                    break;
                case "2":
                    limpiarConsola();
                    agregarCompeticion();
                    break;
                case "3":
                    limpiarConsola();
                    verMisCompeticiones();
                    break;
                case "4":
                    limpiarConsola();
                    System.out.println("Saliendo del menú...");
                    return;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private static void cargarCompeticionesDesdeArchivo() {
        competiciones.clear(); // Limpiar la lista antes de cargar nuevas competiciones

        File archivo = new File(ARCHIVO_COMPETENCIAS);
        if (!archivo.exists()) {
            return; // Si el archivo no existe, no hay competiciones que cargar
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 10) {
                    Integer id = Integer.parseInt(datos[0].trim());
                    String nombre = datos[1].trim();
                    String descripcion = datos[2].trim();
                    Date fechaInicio = formatoFecha.parse(datos[3].trim());
                    Date fechaFin = formatoFecha.parse(datos[4].trim());
                    String institucionId = datos[5].trim();
                    double costoInscripcion = Double.parseDouble(datos[6].trim());
                    Integer maxParticipantes = Integer.parseInt(datos[7].trim());
                    String estado = datos[8].trim();
                    Integer participantes = Integer.parseInt(datos[9].trim());

                    competiciones.add(new Competencia(id, nombre, descripcion, fechaInicio, fechaFin, institucionId, costoInscripcion,maxParticipantes,estado, participantes));
                }
            }
        } catch (IOException | java.text.ParseException e) {
            System.out.println("Error al leer el archivo de competiciones: " + e.getMessage());
        }
    }

    private static final String ARCHIVO_USUARIOS = "src/main/java/org/example/data/usuarios.txt";
    private static void cargarUsuariosDesdeArchivo() {
        usuarios.clear();

        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) {
            return; // Si el archivo no existe, no hay competiciones que cargar
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 5) {
                    String id = datos[0].trim();
                    String nombre = datos[1].trim();
                    String correo = datos[2].trim();
                    String contra = datos[3].trim();
                    String tipo = datos[4].trim();

                    usuarios.add(new Usuario(id, nombre, correo, contra,tipo));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de usuarios: " + e.getMessage());
        }
    }

    private static void enlistarCompeticiones() {
        cargarCompeticionesDesdeArchivo(); // Primero, cargar las competiciones en la lista

        if (competiciones.isEmpty()) {
            System.out.println("No hay competiciones disponibles.");
            return;
        }

        System.out.println("\n--- Lista de Competiciones ---");
        int i = 1;
        for (Competencia c : competiciones) {
            System.out.println(i + ". Nombre: " + c.getNombre() + " | Estado: " + c.getEstado());
            i++;
        }

    }

    private static void enlistarCompeticionesEstudiante() {
        cargarCompeticionesDesdeArchivo(); // Primero, cargar las competiciones en la lista

        if (competiciones.isEmpty()) {
            System.out.println("No hay competiciones disponibles.");
            return;
        }
        System.out.println("\n--- Lista de Competiciones ---");
        int i = 1;
        for (Competencia c : competiciones) {
            System.out.println(i++ + ". Nombre: " + c.getNombre() + " | Estado: " + c.getEstado());
        }
        int opcion = 0;
        while (true) {
            System.out.print("Ingrese competición a detallar o 0 para salir: ");
            String input = scanner.nextLine();
            try {
                opcion = Integer.parseInt(input);
                if(opcion == 0)
                    break;
                if (opcion < competiciones.size() && opcion > 0) {
                    detallarCompetencia(opcion);
                    break;
                } else {
                    System.out.println("La competecion debe existir.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }
    private static void detallarCompetencia(Integer id) {
        cargarUsuariosDesdeArchivo();
        for (Competencia competencia : competiciones) {
            if (competencia.getId().equals(id)) {
                System.out.println("========================================");
                System.out.println("DETALLES DE LA COMPETENCIA");
                System.out.println("========================================");
                System.out.println("ID: " + competencia.getId());
                System.out.println("Nombre: " + competencia.getNombre());
                System.out.println("Descripción: " + competencia.getDescripcion());
                System.out.println("Fecha de Inicio: " + new SimpleDateFormat("yyyy-MM-dd").format(competencia.getFechaInicio()));
                System.out.println("Fecha de Fin: " + new SimpleDateFormat("yyyy-MM-dd").format(competencia.getFechaFin()));
                System.out.println("Estado: " + competencia.getEstado());
                System.out.println("ID de la Institución: " + usuarios.get(Integer.parseInt(competencia.getInstitucionId())-1).getNombre());
                System.out.println("Costo de Inscripción: $" + competencia.getCostoInscripcion());
                System.out.println("Máximo de Participantes: " + competencia.getMaxParticipantes());
                System.out.println("Participantes Inscritos: " + competencia.getParticipantes());
                System.out.println("========================================");

                System.out.println("1. Inscribirse");
                System.out.println("2. Enlistar Participantes");
                System.out.println("0. Salir");


                int opcion = 0;
                while (true) {
                    System.out.print("Ingrese su opcion o 0 para salir: ");
                    String input = scanner.nextLine();
                    try {
                        opcion = Integer.parseInt(input);
                        if(opcion == 0)
                            return;
                        if(opcion == 2) {
                            enlistarInscritos(id);
                            return;
                        }
                        else {
                            if (opcion == 1) {
                                if (Objects.equals(competencia.getMaxParticipantes(), competencia.getParticipantes())) {
                                    System.out.println("Competencia al limite de participantes.");
                                    return;
                                }
                                if (inscrito(id)) {
                                    System.out.println("Ya te encuentras inscrito a esta competencia.");
                                    return;
                                }

                                agregarInscripcion(new Inscripcion(String.valueOf(inscripciones.size() + 1), idActual, String.valueOf(id), "ACTIVO"));
                                return;
                            } else {
                                System.out.println("La opcion es incorrecta.");
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ingrese un número entero válido.");
                    }
                }

            }
        }

        System.out.println("No se encontró ninguna competencia con el ID: " + id);
    }

    private static boolean inscrito(Integer competenciaId) {
        boolean ins = false;
        for (Inscripcion inscripcion : inscripciones) {
            if (Objects.equals(inscripcion.getCompetenciaId(), String.valueOf(competenciaId))
            && Objects.equals(inscripcion.getUsuarioId(), idActual)){
                ins = true;
            }


        }

        return ins;
    }

    private static final String ARCHIVO_INSCRIPCIONES = "src/main/java/org/example/data/inscripciones.txt";

    // Método para cargar inscripciones desde el archivo
    public static List<Inscripcion> cargarInscripciones() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_INSCRIPCIONES))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 4) {
                    inscripciones.add(new Inscripcion(datos[0], datos[1], datos[2], datos[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return inscripciones;
    }

    public static void agregarInscripcion(Inscripcion inscripcion) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_INSCRIPCIONES, true))) {
            bw.write(inscripcion.getId() + "," + inscripcion.getUsuarioId() + "," +
                    inscripcion.getCompetenciaId() + "," + inscripcion.getEstado());
            bw.newLine();
            inscripciones.add(inscripcion);
            System.out.println("Inscripcion añadida.");
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static void enlistarInscritos(int idCompetnecia){
        System.out.println("Participantes: ");
        for(Inscripcion inscripcion: inscripciones) {
            if(String.valueOf(idCompetnecia).equals(inscripcion.getCompetenciaId())){
                System.out.println("- " + usuarios.get(Integer.parseInt(inscripcion.getUsuarioId())-1).getNombre());
            }
        }
        scanner.nextLine();
    }

    private static final String ARCHIVO_COMPETENCIAS = "src/main/java/org/example/data/competencias.txt";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

    private static void agregarCompeticion() {
        cargarCompeticionesDesdeArchivo();
        System.out.print("Ingrese el nombre de la competencia: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese la descripción: ");
        String descripcion = scanner.nextLine();
        System.out.print("Ingrese el costo de inscripción: ");
        double costoInscripcion = scanner.nextDouble();
        scanner.nextLine();

        int numeroParticipantes = 0;
        while (true) {
            System.out.print("Ingrese el número de participantes (mínimo 4): ");
            String input = scanner.nextLine();
            try {
                numeroParticipantes = Integer.parseInt(input);
                if (numeroParticipantes >= 4) {
                    break;
                } else {
                    System.out.println("Debe haber un mínimo de 4 participantes en un torneo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }


        System.out.print("Ingrese la fecha de inicio (YYYY-MM-DD): ");
        String fechaInicioStr = scanner.nextLine();
        try {
            Date fechaInicio = formatoFecha.parse(fechaInicioStr);
        } catch (Exception e) {
            System.out.println("El formato de la fecha esta mal puesta porfavor intente denuevo");
            return;
        }
        System.out.print("Ingrese la fecha de fin (YYYY-MM-DD): ");

        String fechaFinStr = scanner.nextLine();


        try {
            Date fechaInicio = formatoFecha.parse(fechaInicioStr);
            Date fechaFin = formatoFecha.parse(fechaFinStr);

            Competencia nuevaCompetencia = new Competencia(
                    competiciones.size()+1, nombre, descripcion, fechaInicio, fechaFin, institucionActual, costoInscripcion, numeroParticipantes,"DISPONIBLE", 0);
            competiciones.add(nuevaCompetencia);

            guardarCompetenciaEnArchivo(nuevaCompetencia);

            System.out.println("Competencia agregada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al agregar la competencia: " + e.getMessage());
        }
    }

    private static void guardarCompetenciaEnArchivo(Competencia competencia) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_COMPETENCIAS, true))) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%.2f,%s,%s,%s\n",
                    competencia.getId(),
                    competencia.getNombre(),
                    competencia.getDescripcion(),
                    formatoFecha.format(competencia.getFechaInicio()),
                    formatoFecha.format(competencia.getFechaFin()),
                    competencia.getInstitucionId(),
                    competencia.getCostoInscripcion(),
                    competencia.getMaxParticipantes(),
                    competencia.getEstado(),
                    competencia.getParticipantes()));
        } catch (IOException e) {
            System.out.println("Error al guardar la competencia en el archivo: " + e.getMessage());
        }
    }




    private static void verMisCompeticiones() {
        System.out.println("\n--- Mis Competiciones ---");
        boolean tieneCompeticiones = false;
        for (Competencia c : competiciones) {
            if (c.getInstitucionId().equals(idActual)) {
                System.out.println("ID: " + c.getId() + " | Nombre: " + c.getNombre() + " | Estado: " + c.getEstado());
                tieneCompeticiones = true;
            }
        }
        if (!tieneCompeticiones) {
            System.out.println("No tienes competiciones registradas.");
        }
    }

    private static void limpiarConsola() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
