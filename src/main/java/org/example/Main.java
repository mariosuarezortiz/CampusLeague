package org.example;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== BIENVENIDO AL SISTEMA =====");
        System.out.print("Ingrese su correo: ");
        String correoIngresado = scanner.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String contraseñaIngresada = scanner.nextLine();

        if (validarUsuario(correoIngresado, contraseñaIngresada)) {
            System.out.println("Inicio de sesión exitoso. ¡Bienvenido!");
        } else {
            System.out.println("Error: Correo o contraseña incorrectos.");
        }

        scanner.close();
    }

    public static boolean validarUsuario(String correo, String contraseña) {
        File archivo = new File("src/main/java/org/example/data/usuarios.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 5) {
                    String correoRegistrado = datos[2].trim();
                    String contraseñaRegistrada = datos[3].trim();

                    if (correoRegistrado.equals(correo) && contraseñaRegistrada.equals(contraseña)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return false;
    }
}
