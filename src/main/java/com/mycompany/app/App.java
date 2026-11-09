package com.mycompany.app;

import java.util.Locale;
import java.util.Scanner;

public final class App {

    private static final TestCase[] REQUIRED_CASES = {
        new TestCase(0.20, 6, 0.55338),
        new TestCase(0.45, 15, 1.75305),
        new TestCase(0.495, 4, 4.60409)
    };

    private App() {
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length == 0) {
            runInteractive();
            return;
        }
        if (args.length == 1 && "--table".equals(args[0])) {
            printRequiredCases();
            return;
        }
        if (args.length == 2 && "--case".equals(args[0])) {
            try {
                int caseNumber = Integer.parseInt(args[1]);
                printRequiredCase(caseNumber);
            } catch (NumberFormatException exception) {
                System.err.println("El número de caso debe ser 1, 2 o 3.");
                printUsage();
                System.exit(2);
            } catch (IllegalArgumentException exception) {
                System.err.println(exception.getMessage());
                printUsage();
                System.exit(2);
            }
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

    private static void printRequiredCases() {
        System.out.println("p\tdof\tx esperado\tx calculado");
        for (TestCase testCase : REQUIRED_CASES) {
            double x = StudentTSearch.findX(testCase.probability, testCase.degreesOfFreedom);
            System.out.printf("%.3f\t%d\t%.5f\t%.5f%n",
                    testCase.probability, testCase.degreesOfFreedom, testCase.expectedX, x);
        }
    }

    private static void printRequiredCase(int caseNumber) {
        if (caseNumber < 1 || caseNumber > REQUIRED_CASES.length) {
            throw new IllegalArgumentException("El número de caso debe ser 1, 2 o 3.");
        }

        TestCase testCase = REQUIRED_CASES[caseNumber - 1];
        double x = StudentTSearch.findX(testCase.probability, testCase.degreesOfFreedom);
        System.out.printf(Locale.US, "Caso %d: p=%.3f, dof=%d, x esperado=%.5f, x calculado=%.5f%n",
                caseNumber, testCase.probability, testCase.degreesOfFreedom, testCase.expectedX, x);
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
        System.err.println("      java -jar my-app-1.0-SNAPSHOT.jar --case <1|2|3>");
        System.err.println("      java -jar my-app-1.0-SNAPSHOT.jar --table");
        System.err.println("      Sin argumentos, el programa solicita p y dof.");
        System.err.println("      p debe cumplir 0 <= p < 0.5 y dof debe ser positivo.");
    }

    private static final class TestCase {
        private final double probability;
        private final int degreesOfFreedom;
        private final double expectedX;

        private TestCase(double probability, int degreesOfFreedom, double expectedX) {
            this.probability = probability;
            this.degreesOfFreedom = degreesOfFreedom;
            this.expectedX = expectedX;
        }
    }
}
