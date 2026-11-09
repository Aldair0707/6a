package com.mycompany.app;

import java.util.Locale;
import java.util.Scanner;

public final class App {

    // Edit these inputs before committing and pushing to run a different Jenkins calculation.
    private static final double JENKINS_PROBABILITY = 0.20;
    private static final int JENKINS_DEGREES_OF_FREEDOM = 6;

    private App() {
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length == 0) {
            runInteractive();
            return;
        }
        if (args.length == 1 && "--jenkins".equals(args[0])) {
            printJenkinsResult();
            return;
        }
        if (args.length != 2) {
            printUsage();
            System.exit(2);
            return;
        }

        try {
            double probability = Double.parseDouble(args[0]);
            int degreesOfFreedom = Integer.parseInt(args[1]);
            printResult(probability, degreesOfFreedom);
        } catch (NumberFormatException exception) {
            System.err.println("p debe ser un número y dof debe ser un entero.");
            printUsage();
            System.exit(2);
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            printUsage();
            System.exit(2);
        }
    }

    private static void printJenkinsResult() {
        double x = StudentTSearch.findX(JENKINS_PROBABILITY, JENKINS_DEGREES_OF_FREEDOM);
        System.out.printf(Locale.US, "p=%.3f, dof=%d, x=%.5f%n",
                JENKINS_PROBABILITY, JENKINS_DEGREES_OF_FREEDOM, x);
    }

    private static void runInteractive() {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        System.out.print("Ingresa p : ");
        if (!scanner.hasNextDouble()) {
            System.err.println("p debe ser un número decimal, por ejemplo 0.20.");
            return;
        }
        double probability = scanner.nextDouble();

        System.out.print("Ingresa dof: ");
        if (!scanner.hasNextInt()) {
            System.err.println("dof debe ser un número entero positivo.");
            return;
        }
        int degreesOfFreedom = scanner.nextInt();

        try {
            printResult(probability, degreesOfFreedom);
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private static void printResult(double probability, int degreesOfFreedom) {
        double x = StudentTSearch.findX(probability, degreesOfFreedom);
        System.out.printf(Locale.US, "%.5f%n", x);
    }

    private static void printUsage() {
        System.err.println("Uso: java -jar my-app-1.0-SNAPSHOT.jar <p> <dof>");
        System.err.println("      java -jar my-app-1.0-SNAPSHOT.jar --jenkins");
        System.err.println("      Sin argumentos, el programa solicita p y dof.");
        System.err.println("      p debe cumplir 0 <= p < 0.5 y dof debe ser positivo.");
    }
}
