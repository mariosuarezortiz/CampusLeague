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
                System.out.println("Acceso concedido, pero sin permisos de institución.");
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

    private static void menuInstitucion() {
        while (true) {
            System.out.println("\n--- MENÚ INSTITUCIONES ---");
            System.out.println("1. Enlistar Competiciones");
            System.out.println("2. Agregar Competición");
            System.out.println("3. Mis Competiciones");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    enlistarCompeticiones();
                    break;
                case 2:
                    agregarCompeticion();
                    break;
                case 3:
                    verMisCompeticiones();
                    break;
                case 4:
                    System.out.println("Saliendo del menú...");
                    return;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private static void enlistarCompeticiones() {
        if (competiciones.isEmpty()) {
            System.out.println("No hay competiciones disponibles.");
            return;
        }
        System.out.println("\n--- Lista de Competiciones ---");
        for (Competencia c : competiciones) {
            System.out.println("Nombre: " + c.getNombre() + " | Estado: " + c.getEstado());
        }
    }

    private static final String ARCHIVO_COMPETENCIAS = "src/main/java/org/example/data/competencias.txt";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");

    private static void agregarCompeticion() {
        System.out.print("Ingrese el nombre de la competencia: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese la descripción: ");
        String descripcion = scanner.nextLine();
        System.out.print("Ingrese la fecha de inicio (YYYY-MM-DD): ");
        String fechaInicioStr = scanner.nextLine();
        System.out.print("Ingrese la fecha de fin (YYYY-MM-DD): ");
        String fechaFinStr = scanner.nextLine();
        System.out.print("Ingrese el costo de inscripción: ");
        double costoInscripcion = scanner.nextDouble();
        scanner.nextLine();

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
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("No se pudo limpiar la consola.");
        }
    }
}
