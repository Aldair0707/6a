package com.mycompany.app;

import java.util.Locale;

/** Command-line entry point for Program 6. */
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
            printRequiredCases();
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

    private static void printResult(double probability, int degreesOfFreedom) {
        double x = StudentTSearch.findX(probability, degreesOfFreedom);
        double actualProbability = StudentTSearch.integrate(x, degreesOfFreedom);
        System.out.printf("p=%.8f, dof=%d, x=%.8f, integral=%.10f%n",
                probability, degreesOfFreedom, x, actualProbability);
    }

    private static void printUsage() {
        System.err.println("Uso: java -jar my-app-1.0-SNAPSHOT.jar <p> <dof>");
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
