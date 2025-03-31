package org.example;

import org.example.dtos.Competencia;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Competencia> competiciones = new ArrayList<>();
    private static String institucionActual;

    public static void main(String[] args) {
        System.out.println("===== BIENVENIDO AL SISTEMA =====");
        System.out.print("Ingrese su correo: ");
        String correoIngresado = scanner.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String contraseñaIngresada = scanner.nextLine();

        String tipoUsuario = validarUsuario(correoIngresado, contraseñaIngresada);
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
                    String correoRegistrado = datos[2].trim();
                    String contraseñaRegistrada = datos[3].trim();
                    String tipoUsuario = datos[4].trim().toUpperCase(); // "INSTITUCION" o "USUARIO"

                    if (correoRegistrado.equals(correo) && contraseñaRegistrada.equals(contraseña)) {
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
            } //test
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
                if (datos.length == 7) {
                    String id = datos[0].trim();
                    String nombre = datos[1].trim();
                    String descripcion = datos[2].trim();
                    Date fechaInicio = formatoFecha.parse(datos[3].trim());
                    Date fechaFin = formatoFecha.parse(datos[4].trim());
                    String institucionId = datos[5].trim();
                    double costoInscripcion = Double.parseDouble(datos[6].trim());

                    competiciones.add(new Competencia(id, nombre, descripcion, fechaInicio, fechaFin, institucionId, costoInscripcion));
                }
            }
        } catch (IOException | java.text.ParseException e) {
            System.out.println("Error al leer el archivo de competiciones: " + e.getMessage());
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
        int i = 0;
        for (Competencia c : competiciones) {
            System.out.println(i + ". Nombre: " + c.getNombre() + " | Estado: " + c.getEstado());
        }
        System.out.println("\nIngrese competición a detallar: ");
    }

    private static final String ARCHIVO_COMPETENCIAS = "src/main/java/org/example/data/competencias.txt";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

    private static void agregarCompeticion() {
        System.out.print("Ingrese el nombre de la competencia: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese la descripción: ");
        String descripcion = scanner.nextLine();
        System.out.print("Ingrese el costo de inscripción: ");
        double costoInscripcion = scanner.nextDouble();
        scanner.nextLine();
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
                    UUID.randomUUID().toString(), nombre, descripcion, fechaInicio, fechaFin, institucionActual, costoInscripcion);
            competiciones.add(nuevaCompetencia);

            guardarCompetenciaEnArchivo(nuevaCompetencia);

            System.out.println("Competencia agregada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al agregar la competencia: " + e.getMessage());
        }
    }

    private static void guardarCompetenciaEnArchivo(Competencia competencia) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_COMPETENCIAS, true))) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%.2f\n",
                    competencia.getId(),
                    competencia.getNombre(),
                    competencia.getDescripcion(),
                    formatoFecha.format(competencia.getFechaInicio()),
                    formatoFecha.format(competencia.getFechaFin()),
                    competencia.getInstitucionId(),
                    competencia.getCostoInscripcion()));
        } catch (IOException e) {
            System.out.println("Error al guardar la competencia en el archivo: " + e.getMessage());
        }
    }


    private static void verMisCompeticiones() {
        System.out.println("\n--- Mis Competiciones ---");
        boolean tieneCompeticiones = false;
        for (Competencia c : competiciones) {
            if (c.getInstitucionId().equals(institucionActual)) {
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
