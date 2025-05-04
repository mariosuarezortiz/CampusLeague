package org.example;

import org.example.dtos.Competencia;
import org.example.dtos.Duelo;
import org.example.dtos.Inscripcion;
import org.example.dtos.Usuario;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Competencia> competiciones = new ArrayList<>();
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Inscripcion> inscripciones = new ArrayList<>();
    private static String institucionActual;
    private static String idActual;

    public static void main(String[] args) {
        while (true) {
            System.out.println("===== BIENVENIDO AL SISTEMA =====");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    iniciarSesion();
                    break;
                case "2":
                    registrarUsuario();
                    break;
                case "0":
                    System.out.println("Gracias por usar el sistema. ¡Hasta luego!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción incorrecta. Intente nuevamente.\n");
            }
        }
    }

    private static void registrarUsuario() {
        System.out.println("===== REGISTRO DE ESTUDIANTE =====");
        System.out.print("Ingrese nombre completo: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese correo: ");
        String correo = scanner.nextLine();
        System.out.print("Ingrese contraseña: ");
        String contraseña = scanner.nextLine();

        File archivo = new File("src/main/java/org/example/data/usuarios.txt");

        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 1) {
                    try {
                        int id = Integer.parseInt(datos[0].trim());
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                // Validar que el correo no esté repetido
                if (datos.length >= 3 && datos[2].trim().equalsIgnoreCase(correo)) {
                    System.out.println("Error: El correo ya está registrado.");
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        int nuevoId = maxId + 1;
        String nuevoUsuario = nuevoId + "," + nombre + "," + correo + "," + contraseña + ",ESTUDIANTE";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(nuevoUsuario);
            bw.newLine();
            System.out.println("Estudiante registrado exitosamente con ID: " + nuevoId);
        } catch (IOException e) {
            System.out.println("Error al registrar el estudiante: " + e.getMessage());
        }
    }



    private static void iniciarSesion() {
        System.out.print("Ingrese su correo: ");
        String correoIngresado = scanner.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String contraseñaIngresada = scanner.nextLine();

        cargarUsuariosDesdeArchivo();
        cargarInscripciones();
        String tipoUsuario = validarUsuario(correoIngresado, contraseñaIngresada);

        if (tipoUsuario != null) {
            System.out.println("Inicio de sesión exitoso. ¡Bienvenido!");

            institucionActual = correoIngresado;
            limpiarConsola();

            switch (tipoUsuario) {
                case "INSTITUCION":
                    menuInstitucion();
                    break;
                case "ESTUDIANTE":
                    menuEstudiante();
                    break;
                case "ADMINISTRADOR":
                    menuAdmin();
                    break;
                default:
                    System.out.println("Su cuenta no existe o ha sido deshabilitada.");
            }
        } else {
            System.out.println("Error: Correo o contraseña incorrectos.");
        }
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

    private static void menuAdmin() {
        while (true) {
            limpiarConsola();
            System.out.println("\n--- MENÚ ADMINISTRADOR ---");
            System.out.println("1. Ver contenido interesante");
            System.out.println("2. Agregar nuevo usuario");
            System.out.println("3. Listar Usuarios ");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    limpiarConsola();
                    mostrarKPIsAdministrador();
                    break;
                case "2":
                    limpiarConsola();
                    agregarNuevoUsuarioComoAdmin();
                    break;
                case "3":
                    limpiarConsola();
                    deshabilitarUsuariosPorID();
                    break;

                case "0":
                    limpiarConsola();
                    System.out.println("Saliendo del menú...");
                    return;
                default:
                    limpiarConsola();
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private static void deshabilitarUsuariosPorID() {
        File archivo = new File("src/main/java/org/example/data/usuarios.txt");
        List<String[]> usuariosDatos = new ArrayList<>();

        System.out.println("===== LISTA DE USUARIOS =====");

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 5) {
                    usuariosDatos.add(datos);
                    System.out.println("ID: " + datos[0] + " | Nombre: " + datos[1] + " | Rol: " + datos[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer usuarios: " + e.getMessage());
            return;
        }

        System.out.print("\nIngrese los ID de los usuarios a deshabilitar separados por comas: ");
        String entrada = scanner.nextLine();
        String[] idsSeleccionados = entrada.split(",");

        Set<String> idsADeshabilitar = Arrays.stream(idsSeleccionados)
                .map(String::trim)
                .collect(Collectors.toSet());

        List<String> nuevasLineas = new ArrayList<>();

        for (String[] datos : usuariosDatos) {
            String id = datos[0];
            String nombre = datos[1];
            String correo = datos[2];
            String contrasena = datos[3];
            String rol = datos[4];

            // Verifica si el ID está en la lista y no es ADMINISTRADOR
            if (idsADeshabilitar.contains(id) && !rol.equalsIgnoreCase("ADMINISTRADOR")) {
                rol = "INHABILITADO";
                System.out.println("✔ Usuario " + nombre + " ha sido deshabilitado.");
            }

            nuevasLineas.add(id + "," + nombre + "," + correo + "," + contrasena + "," + rol);
        }

        // Guardar cambios
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            for (String linea : nuevasLineas) {
                bw.write(linea);
                bw.newLine();
            }
            System.out.println("\n✅ Cambios guardados correctamente.");
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }

        scanner.nextLine(); // Pausa
    }


    private static void agregarNuevoUsuarioComoAdmin() {
        System.out.println("===== CREAR NUEVO USUARIO =====");
        System.out.print("Ingrese nombre completo: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese correo: ");
        String correo = scanner.nextLine();
        System.out.print("Ingrese contraseña: ");
        String contraseña = scanner.nextLine();

        System.out.print("Ingrese rol (ESTUDIANTE / ADMINISTRADOR / INSTITUCION): ");
        String rol = scanner.nextLine().toUpperCase();

        // Validar rol
        if (!rol.equals("ESTUDIANTE") && !rol.equals("ADMINISTRADOR") && !rol.equals("INSTITUCION")) {
            System.out.println("Error: Rol inválido. Solo se permiten ESTUDIANTE, ADMINISTRADOR o INSTITUCION.");
            return;
        }

        File archivo = new File("src/main/java/org/example/data/usuarios.txt");

        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 1) {
                    try {
                        int id = Integer.parseInt(datos[0].trim());
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                // Validar correo único
                if (datos.length >= 3 && datos[2].trim().equalsIgnoreCase(correo)) {
                    System.out.println("Error: El correo ya está registrado.");
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        int nuevoId = maxId + 1;
        String nuevoUsuario = nuevoId + "," + nombre + "," + correo + "," + contraseña + "," + rol;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(nuevoUsuario);
            bw.newLine();
            System.out.println("✅ Usuario registrado exitosamente con ID: " + nuevoId);
        } catch (IOException e) {
            System.out.println("Error al registrar el usuario: " + e.getMessage());
        }

        scanner.nextLine(); // Pausa
    }



    private static void mostrarKPIsAdministrador() {
        cargarCompeticionesDesdeArchivo();
        cargarUsuariosDesdeArchivo();

        double ingresosTotales = 0.0;
        double costosTotales = 0.0;

        int totalUsuarios = usuarios.size();
        int totalEstudiantes = 0;
        int totalInstituciones = 0;
        int totalAdmins = 0;

        int totalCompetencias = competiciones.size();
        int competenciasCanceladas = 0;
        int competenciasFinalizadas = 0;

        Set<String> usuariosEstudiantesActivos = new HashSet<>();
        Set<String> institucionesActivas = new HashSet<>();

        Map<Integer, Integer> competenciasPorAnio = new TreeMap<>();

        // Clasificar usuarios por rol
        for (Usuario user : usuarios) {
            switch (user.getRol().toUpperCase()) {
                case "ESTUDIANTE" -> totalEstudiantes++;
                case "INSTITUCION" -> totalInstituciones++;
                case "ADMINISTRADOR" -> totalAdmins++;
            }
        }

        // Recolectar estadísticas de inscripciones (TODOS cuentan como participación)
        for (Inscripcion inscripcion : inscripciones) {
            for (Competencia competencia : competiciones) {
                if (String.valueOf(competencia.getId()).equals(inscripcion.getCompetenciaId())) {
                    ingresosTotales += competencia.getCostoInscripcion();
                    usuariosEstudiantesActivos.add(inscripcion.getUsuarioId());
                    break;
                }
            }
        }


        // Procesar competencias
        for (Competencia competencia : competiciones) {
            if (competencia.getEstado().equalsIgnoreCase("CANCELADA")) {
                competenciasCanceladas++;
            }
            if (competencia.getEstado().equalsIgnoreCase("FINALIZADA")) {
                competenciasFinalizadas++;
            }

            // Costo estimado por organización
            costosTotales += 20.0;

            // Marcar instituciones activas
            institucionesActivas.add(competencia.getInstitucionId());

            // Agrupar por año
            Calendar cal = Calendar.getInstance();
            cal.setTime(competencia.getFechaInicio());
            int anio = cal.get(Calendar.YEAR);
            competenciasPorAnio.put(anio, competenciasPorAnio.getOrDefault(anio, 0) + 1);
        }

        // Resultados
        System.out.println("\n📊 --- INDICADORES CLAVE DE LA PLATAFORMA (KPI) --- 📊");
        System.out.println("👥 Total de usuarios: " + totalUsuarios);
        System.out.println("   📚 Estudiantes: " + totalEstudiantes);
        System.out.println("   🏫 Instituciones: " + totalInstituciones);
        System.out.println("   👑 Administradores: " + totalAdmins);

        System.out.println("\n🧍‍♂️ Estudiantes activos (participaron): " + usuariosEstudiantesActivos.size());
        System.out.printf("📈 %% de estudiantes que usan la app: %.2f%%%n",
                totalEstudiantes == 0 ? 0 : (usuariosEstudiantesActivos.size() * 100.0 / totalEstudiantes));

        System.out.println("\n🏛️ Instituciones activas (organizaron algo): " + institucionesActivas.size());
        System.out.printf("📈 %% de instituciones activas: %.2f%%%n",
                totalInstituciones == 0 ? 0 : (institucionesActivas.size() * 100.0 / totalInstituciones));

        System.out.println("\n🏆 Total de competencias creadas: " + totalCompetencias);
        System.out.println("✅ Finalizadas: " + competenciasFinalizadas);
        System.out.println("❌ Canceladas: " + competenciasCanceladas);
        System.out.printf("📉 %% canceladas: %.2f%%\n",
                totalCompetencias == 0 ? 0 : (competenciasCanceladas * 100.0 / totalCompetencias));
        System.out.printf("📊 %% finalizadas: %.2f%%\n",
                totalCompetencias == 0 ? 0 : (competenciasFinalizadas * 100.0 / totalCompetencias));

        System.out.println("\n💰 Ingresos por inscripciones: S/ " + String.format("%.2f", ingresosTotales));
        System.out.println("💸 Costos totales estimados: S/ " + String.format("%.2f", costosTotales));
        System.out.println("📈 Ganancia neta: S/ " + String.format("%.2f", ingresosTotales - costosTotales));

        System.out.println("\n📅 Competencias por año:");
        int anioAnterior = -1;
        int cantidadAnterior = 0;
        for (Map.Entry<Integer, Integer> entry : competenciasPorAnio.entrySet()) {
            int anio = entry.getKey();
            int cantidad = entry.getValue();
            double crecimiento = anioAnterior == -1 ? 0 : ((cantidad - cantidadAnterior) / (double) cantidadAnterior) * 100;
            System.out.printf("   - %d: %d competencias", anio, cantidad);
            if (anioAnterior != -1) {
                System.out.printf(" | Crecimiento: %.2f%%", crecimiento);
            }
            System.out.println();
            anioAnterior = anio;
            cantidadAnterior = cantidad;
        }

        scanner.nextLine(); // Pausa
    }





    private static void menuEstudiante() {
        while (true) {
            limpiarConsola();
            System.out.println("\n--- MENÚ ESTDUIANTES ---");
            System.out.println("1. Enlistar Competiciones");
            System.out.println("2. Competiciones Inscritas");
            System.out.println("3. Mis metricas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    limpiarConsola();
                    enlistarCompeticionesEstudiante();
                    break;
                case "2":
                    limpiarConsola();
                    enlistarCompeticionesInscritasEstudiante();
                    break;
                case "3":
                    limpiarConsola();
                    mostrarKPIsEstudiante();
                    break;

                case "0":
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
            System.out.println("4. Mis Metricas");
            System.out.println("5. Salir");
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
                    verResumenKPIInstitucion();
                    break;
                case "5":
                    limpiarConsola();
                    System.out.println("Saliendo del menú...");
                    return;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }


    private static void verResumenKPIInstitucion() {
        cargarCompeticionesDesdeArchivo();
        cargarInscripciones();

        int totalCompeticiones = 0;
        int totalInscritos = 0;
        int totalActivos = 0;
        int totalMaxParticipantes = 0;
        double totalIngresosBrutos = 0;
        double totalCostos = 0;
        double totalGanancias = 0;

        for (Competencia competencia : competiciones) {
            if (competencia.getInstitucionId().equals(idActual)) {
                totalCompeticiones++;

                String competenciaId = String.valueOf(competencia.getId());
                int inscritos = competencia.getParticipantes();
                int activos = contarInscripcionesActivasPorCompetencia(competenciaId);
                int maxParticipantes = competencia.getMaxParticipantes();
                double costoInscripcion = competencia.getCostoInscripcion();

                double ingresos = inscritos * costoInscripcion;
                double costoEvento = calcularCostoEvento(competencia); // Usa el método de antes
                double ganancia = ingresos - costoEvento;

                totalInscritos += inscritos;
                totalActivos += activos;
                totalMaxParticipantes += maxParticipantes;
                totalIngresosBrutos += ingresos;
                totalCostos += costoEvento;
                totalGanancias += ganancia;
            }
        }

        if (totalCompeticiones == 0) {
            System.out.println("No tienes competiciones registradas.");
            return;
        }

        double porcentajeOcupacionPromedio = (totalInscritos * 100.0) / totalMaxParticipantes;

        System.out.println("\n===== KPI GLOBAL DE LA INSTITUCIÓN =====");
        System.out.println("Total de competiciones organizadas: " + totalCompeticiones);
        System.out.println("Total de inscritos: " + totalInscritos);
        System.out.println("Total de participantes activos: " + totalActivos);
        System.out.println("Máximo total permitido de participantes: " + totalMaxParticipantes);
        System.out.printf("Porcentaje promedio de ocupación: %.2f%%\n", porcentajeOcupacionPromedio);
        System.out.printf("Ingresos brutos totales: S/ %.2f\n", totalIngresosBrutos);
        System.out.printf("Costos estimados totales: S/ %.2f\n", totalCostos);
        System.out.printf("Ganancia neta total: S/ %.2f\n", totalGanancias);
        System.out.println("===========================================");

        scanner.nextLine();
    }


    public static void mostrarKPICompetencia(Competencia competencia) {
        String competenciaId = String.valueOf(competencia.getId());
        int inscritos = competencia.getParticipantes();
        int maxParticipantes = competencia.getMaxParticipantes();
        int activos = contarInscripcionesActivasPorCompetencia(competenciaId);
        double costoInscripcion = competencia.getCostoInscripcion();

        double ingresosBrutos = inscritos * costoInscripcion;
        double costoEvento = calcularCostoEvento(competencia); // Puedes ajustar esta fórmula como desees
        double gananciaNeta = ingresosBrutos - costoEvento;
        double porcentajeOcupacion = (inscritos * 100.0) / maxParticipantes;

        System.out.println("=== Indicadores Clave de Desempeño (KPI) ===");
        System.out.println("Nombre de la competencia: " + competencia.getNombre());
        System.out.println("Estado: " + competencia.getEstado());
        System.out.println("Máx. participantes permitidos: " + maxParticipantes);
        System.out.println("Participantes inscritos: " + inscritos);
        System.out.println("Participantes activos: " + activos);
        System.out.printf("Porcentaje de participación: %.2f%%\n", porcentajeOcupacion);
        System.out.printf("Costo por inscripción: S/ %.2f\n", costoInscripcion);
        System.out.printf("Ingresos brutos: S/ %.2f\n", ingresosBrutos);
        System.out.printf("Costo estimado del evento: S/ %.2f\n", costoEvento);
        System.out.printf("Ganancia neta: S/ %.2f\n", gananciaNeta);

        if (competencia.getEstado().equalsIgnoreCase("FINALIZADA")) {
            String ganadorId = obtenerUltimoUsuarioActivo(competenciaId);
            if (ganadorId != null) {
                String nombreGanador = obtenerNombreUsuario(ganadorId); // Asumiendo que tienes esta función
                System.out.println("Ganador: " + nombreGanador + " (ID: " + ganadorId + ")");
            } else {
                System.out.println("No se pudo determinar un único ganador.");
            }
        }

        System.out.println("=============================================");

    }

    public static double calcularCostoEvento(Competencia competencia) {
        // Supón que por cada participante el costo operativo es S/ 5.00
        double costoPorParticipante = 5.0;
        return competencia.getParticipantes() * costoPorParticipante;
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

    private static void enlistarCompeticionesInscritasEstudiante() {
        cargarCompeticionesDesdeArchivo(); // Primero, cargar las competiciones en la lista

        List<Inscripcion> misInscripciones = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getUsuarioId().equals(idActual))
                misInscripciones.add(inscripcion);
        }

        if (misInscripciones.isEmpty()) {
            System.out.println("No hay competiciones disponibles.");
            return;
        }

        System.out.println("\n--- Lista de Competiciones ---");
        int i = 1;

        for (Competencia c : competiciones) {
            for (Inscripcion miInscripcion : misInscripciones) {
                if (String.valueOf(c.getId()).equals(miInscripcion.getCompetenciaId())) {
                    String estadoMiInscripcion = miInscripcion.getEstado();

                    // Verificar si es el único activo en esa competencia
                    if (estadoMiInscripcion.equalsIgnoreCase("ACTIVO")) {
                        int activos = contarInscripcionesActivasPorCompetencia(miInscripcion.getCompetenciaId());
                        if (activos == 1 && c.getEstado().equals("FINALIZADA")) {
                            estadoMiInscripcion = "GANADOR 🏆";
                        }
                    }

                    System.out.println(i++ + ". Nombre: " + c.getNombre() +
                            " | Estado de Competencia: " + c.getEstado() +
                            " | Fecha Inicio: " + new SimpleDateFormat("yyyy-MM-dd").format(c.getFechaInicio()) +
                            " | Mi estado: " + estadoMiInscripcion);
                }
            }
        }

        scanner.nextLine();
    }

    private static void mostrarKPIsEstudiante() {
        cargarCompeticionesDesdeArchivo();

        int totalInscripciones = 0;
        int totalGanadas = 0;
        double totalGastado = 0.0;

        // Mapa para contar participaciones por mes y año (ej: "2024-04" -> 3)
        Map<String, Integer> participacionesPorMes = new TreeMap<>();

        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getUsuarioId().equals(idActual)) {
                totalInscripciones++;

                for (Competencia competencia : competiciones) {
                    if (String.valueOf(competencia.getId()).equals(inscripcion.getCompetenciaId())) {

                        // Sumar gasto si no está descalificado
                        if (!inscripcion.getEstado().equalsIgnoreCase("DESCALIFICADO")) {
                            totalGastado += competencia.getCostoInscripcion();
                        }

                        // GANADOR si es único activo y competencia finalizada
                        if (inscripcion.getEstado().equalsIgnoreCase("ACTIVO")
                                && competencia.getEstado().equalsIgnoreCase("FINALIZADA")) {

                            int activos = contarInscripcionesActivasPorCompetencia(inscripcion.getCompetenciaId());
                            if (activos == 1) {
                                totalGanadas++;
                            }
                        }

                        // Agrupar por mes y año
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM");
                        String clave = formato.format(competencia.getFechaInicio());

                        participacionesPorMes.put(clave, participacionesPorMes.getOrDefault(clave, 0) + 1);

                        break;
                    }
                }
            }
        }

        System.out.println("\n📈 --- MIS INDICADORES CLAVE (KPI) --- 📈");
        System.out.println("🧾 Total de inscripciones realizadas: " + totalInscripciones);
        System.out.printf("💰 Total gastado en inscripciones: S/ %.2f%n", totalGastado);
        System.out.println("🏆 Veces que fuiste GANADOR (único ACTIVO y competencia FINALIZADA): " + totalGanadas);

        // Mostrar por mes y año
        System.out.println("🗓️ Participaciones por mes y año:");
        for (Map.Entry<String, Integer> entry : participacionesPorMes.entrySet()) {
            System.out.println("   - " + entry.getKey() + ": " + entry.getValue());
        }

        if (totalInscripciones > 0) {
            double porcentajeVictorias = (double) totalGanadas / totalInscripciones * 100;
            System.out.printf("✅ Porcentaje de victorias: %.2f%%\n", porcentajeVictorias);
        } else {
            System.out.println("✅ Porcentaje de victorias: 0.00%");
        }

        scanner.nextLine(); // Pausa
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


    private static void detallarCompetenciaGestion(Integer id) {
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





                int opcion = 0;
                while (true) {
                    System.out.println("1. Sortear duelos");
                    System.out.println("2. Enlistar Participantes");
                    System.out.println("3. Ver duelos actuales");
                    System.out.println("4. Ver metricas de la competencia");
                    System.out.println("0. Salir");
                    System.out.print("Ingrese su opcion o 0 para salir: ");
                    String input = scanner.nextLine();
                    try {
                        opcion = Integer.parseInt(input);
                        if(opcion == 0)
                            return;
                        if(opcion == 3) {
                            listarDuelosPorCompetencia(String.valueOf(competencia.getId()));
                        }else {
                            if (opcion == 2) {
                                enlistarInscritos(id);
                            } else {
                                if (opcion == 4) {
                                    mostrarKPICompetencia(competencia);
                                } else {
                                    if (opcion == 1) {
                                        if (contarInscripcionesActivasPorCompetencia(String.valueOf(competencia.getId())) == 1) {
                                            System.out.println("Ya hay un ganador: " + obtenerNombreUsuario(obtenerUltimoUsuarioActivo(String.valueOf(competencia.getId()))));
                                        } else {

                                            if (todosDuelosFinalizados(String.valueOf(competencia.getId()))) {
                                                generarDuelos(String.valueOf(competencia.getId()));
                                                competiciones.get(competencia.getId()).setEstado("EN PROCESO");
                                                actualizarArchivoCompetencias(competiciones);
                                            } else {
                                                System.out.println("Aun hay duelos pendientes");
                                            }
                                        }
                                    } else {
                                        System.out.println("La opcion es incorrecta.");
                                    }
                                }
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

    public static String obtenerUltimoUsuarioActivo(String competenciaId) {
        String ultimoUsuarioId = null;
        int contador = 0;

        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getCompetenciaId().equals(competenciaId)
                    && inscripcion.getEstado().equalsIgnoreCase("ACTIVO")) {
                ultimoUsuarioId = inscripcion.getUsuarioId();
                contador++;
            }
        }

        // Si hay exactamente un usuario activo, lo retornamos
        if (contador == 1) {
            return ultimoUsuarioId;
        }

        // Si hay más de uno o ninguno, retornamos null
        return null;
    }

    public static int contarInscripcionesActivasPorCompetencia(String competenciaId) {
        int contador = 0;
        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getCompetenciaId().equals(competenciaId)
                    && inscripcion.getEstado().equalsIgnoreCase("ACTIVO")) {
                contador++;
            }
        }
        return contador;
    }


    private static void generarDuelos(String competenciaId) {
        List<String> participantes = new ArrayList<>();

        // Buscar usuarios inscritos en la competencia
        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getCompetenciaId().equals(competenciaId) && inscripcion.getEstado().equals("ACTIVO")) {
                participantes.add(inscripcion.getUsuarioId());
            }
        }

        // Mezclar la lista aleatoriamente para generar duelos al azar
        Collections.shuffle(participantes);

        List<Duelo> duelos = new ArrayList<>();

        // Emparejar de 2 en 2
        for (int i = 0; i < participantes.size(); i += 2) {
            if (i + 1 < participantes.size()) {
                // Duelo normal con 2 participantes
                duelos.add(new Duelo(participantes.get(i), participantes.get(i + 1), competenciaId));
            } else {
                // Si queda un participante solo, crea un duelo donde gana automáticamente
                Duelo dueloSolo = new Duelo(participantes.get(i), "0", competenciaId);
                dueloSolo.setGanadorId(participantes.get(i)); // Gana automáticamente
                duelos.add(dueloSolo);
            }
        }
        guardarDuelosEnArchivo(duelos);
    }


    private static final String ARCHIVO_DUELOS = "src/main/java/org/example/data/duelos.txt";


    private static void actualizarGanadorDuelo(String dueloId) {
        List<String> lineasActualizadas = new ArrayList<>();
        boolean actualizado = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_DUELOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");

                if (datos.length == 4 && datos[2].equals(dueloId) && datos[3].equals("0")) {

                    System.out.println("Duelo encontrado entre: " + usuarios.get(Integer.parseInt(datos[0])-1).getNombre() + " y " + usuarios.get(Integer.parseInt(datos[1])-1).getNombre());



                    String opcion;
                    while (true) {
                        System.out.print("¿Quién ganó el duelo? (1 para usuario1, 2 para usuario2): ");
                        opcion = scanner.nextLine().trim();

                        if (opcion.equals("1")) {
                            datos[3] = datos[0]; // usuario1
                            for (Inscripcion inscripcion : inscripciones) {
                                if(inscripcion.getUsuarioId().equals(datos[1]) && inscripcion.getCompetenciaId().equals(datos[2])) {
                                    inscripcion.setEstado("DESCALIFICADO");
                                }
                            }
                            break;
                        } else if (opcion.equals("2")) {
                            datos[3] = datos[1]; // usuario2
                            for (Inscripcion inscripcion : inscripciones) {
                                if(inscripcion.getUsuarioId().equals(datos[0]) && inscripcion.getCompetenciaId().equals(datos[2])) {
                                    inscripcion.setEstado("DESCALIFICADO");
                                }
                            }
                            break;
                        } else {
                            System.out.println("Opción no válida. Intente de nuevo.");
                        }
                    }


                    linea = String.join(",", datos);
                    actualizado = true;
                }
                guardarInscripciones();
                lineasActualizadas.add(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        // Sobrescribir el archivo si se encontró y actualizó el duelo
        if (actualizado) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_DUELOS))) {
                for (String l : lineasActualizadas) {
                    writer.write(l);
                    writer.newLine();
                }
                System.out.println("Duelo actualizado correctamente.");
            } catch (IOException e) {
                System.out.println("Error al escribir el archivo: " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró un duelo con ese ID.");
        }
    }


    private static void guardarDuelosEnArchivo(List<Duelo> duelos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_DUELOS, true))) {
            for (Duelo duelo : duelos) {
                writer.write(String.format("%s,%s,%s,%s\n",
                        duelo.getUsuario1Id(),
                        duelo.getUsuario2Id(),
                        duelo.getCompetenciaId(),
                        duelo.getGanadorId()
                ));
            }
            System.out.println("Duelos guardados correctamente en " + ARCHIVO_DUELOS);
        } catch (IOException e) {
            System.out.println("Error al guardar los duelos en el archivo: " + e.getMessage());
        }
    }



    private static void listarDuelosPorCompetencia(String competenciaId) {
        List<Duelo> duelos = new ArrayList<>();

        // Leer el archivo de duelos y cargar la lista
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_DUELOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 4) {
                    duelos.add(new Duelo(datos[0], datos[1], datos[2]));
                    duelos.getLast().setGanadorId(datos[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de duelos: " + e.getMessage());
            return;
        }

        // Filtrar los duelos que pertenecen a la competencia indicada
        List<Duelo> duelosFiltrados = new ArrayList<>();
        for (Duelo duelo : duelos) {
            if (duelo.getCompetenciaId().equals(competenciaId)) {
                duelosFiltrados.add(duelo);
            }
        }

        // Si no hay duelos filtrados, informamos
        if (duelosFiltrados.isEmpty()) {
            System.out.println("No hay duelos registrados para la competencia ID: " + competenciaId);
        } else {
            System.out.println("Duelos para la competencia ID: " + competenciaId);

            // Mostrar los duelos con los nombres de los usuarios
            for (Duelo duelo : duelosFiltrados) {
                String usuario1Nombre = obtenerNombreUsuario(duelo.getUsuario1Id());
                String usuario2Nombre = obtenerNombreUsuario(duelo.getUsuario2Id());
                String estado = duelo.getEstado();
                String ganador = duelo.getGanadorId().equals("0") ? "N/A" : obtenerNombreUsuario(duelo.getGanadorId());

                if(!Objects.equals(ganador, "0")) {
                    System.out.println(String.format("Usuario1: %s | Usuario2: %s | Estado: %s | Ganador: %s",
                            usuario1Nombre, usuario2Nombre, estado, ganador));
                }
            }

            System.out.print("Ingrese 1 si quiere editar los duelos o 0 si quiere salir: ");
            String dueloId = scanner.nextLine();
            if (Objects.equals(dueloId, "1")) {
                actualizarGanadorDuelo(competenciaId);
            }
        }

    }


    private static boolean todosDuelosFinalizados(String competenciaId) {
        List<Duelo> duelos = new ArrayList<>();

        // Leer el archivo de duelos y cargar la lista
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_DUELOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 4) {
                    duelos.add(new Duelo(datos[0], datos[1], datos[2]));
                    duelos.getLast().setGanadorId(datos[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de duelos: " + e.getMessage());
            return false;
        }

        // Filtrar los duelos que pertenecen a la competencia indicada
        for (Duelo duelo : duelos) {
            if (duelo.getCompetenciaId().equals(competenciaId)) {
                // Si algún duelo tiene estado pendiente, devolver false
                if (duelo.getGanadorId().equals("0")) {
                    return false;
                }
            }
        }

        // Si todos los duelos están finalizados, devolver true
        return true;
    }




    // Método para obtener el nombre del usuario por su ID
    private static String obtenerNombreUsuario(String usuarioId) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(usuarioId)) {
                return usuario.getNombre();
            }
        }
        return "Desconocido";  // Si no se encuentra el usuario, retornamos "Desconocido"
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

    public static void guardarInscripciones() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_INSCRIPCIONES))) {
            for (Inscripcion inscripcion : inscripciones) {
                String linea = String.join(",",
                        inscripcion.getId(),
                        inscripcion.getUsuarioId(),
                        inscripcion.getCompetenciaId(),
                        inscripcion.getEstado()
                );
                writer.write(linea);
                writer.newLine();
            }
            System.out.println("Archivo de inscripciones actualizado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar las inscripciones: " + e.getMessage());
        }
    }


    public static void agregarInscripcion(Inscripcion inscripcion) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_INSCRIPCIONES, true))) {
            bw.write(inscripcion.getId() + "," + inscripcion.getUsuarioId() + "," +
                    inscripcion.getCompetenciaId() + "," + inscripcion.getEstado());
            bw.newLine();
            inscripciones.add(inscripcion);
            competiciones.get(Integer.parseInt(inscripcion.getCompetenciaId())-1).setParticipantes(competiciones.get(Integer.parseInt(inscripcion.getCompetenciaId())-1).getParticipantes()+1);
            actualizarArchivoCompetencias(competiciones);
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


    private static void actualizarArchivoCompetencias(List<Competencia> competencias) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_COMPETENCIAS, false))) {
            for (Competencia competencia : competencias) {
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
            }
        } catch (IOException e) {
            System.out.println("Error al actualizar el archivo de competencias: " + e.getMessage());
        }
    }


    private static void verMisCompeticiones() {
        cargarCompeticionesDesdeArchivo();
        System.out.println("\n--- Mis Competiciones ---");
        boolean tieneCompeticiones = false;

        ArrayList<Integer> identificadores = new ArrayList<>();

        int i = 0;
        for (Competencia c : competiciones) {
            if (c.getInstitucionId().equals(idActual)) {
                identificadores.add(c.getId());
                System.out.println(++i + " | Nombre: " + c.getNombre() + " | Estado: " + c.getEstado());
                tieneCompeticiones = true;
            }
        }
        if (!tieneCompeticiones) {
            System.out.println("No tienes competiciones registradas.");
            return;
        }

        int opcion = 0;
        while (true) {
            System.out.print("Ingrese competición a detallar o 0 para salir: ");
            String input = scanner.nextLine();
            try {
                opcion = Integer.parseInt(input);
                if(opcion == 0)
                    break;
                if (opcion < identificadores.size() && opcion > 0) {
                    detallarCompetenciaGestion(identificadores.get(opcion-1));
                    break;
                } else {
                    System.out.println("La competicion debe existir.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }


    }

    private static void limpiarConsola() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
