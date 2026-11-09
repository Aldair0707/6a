package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Tests the Student t search and the command-line behavior for activity 6a. */
class A6Test {

    private static final double[][] REQUIRED_VALUES = {
        {0.20, 6, 0.55338},
        {0.45, 15, 1.75305},
        {0.495, 4, 4.60409}
    };

    @Test
    void findsAllRequiredStudentTValues() {
        for (double[] values : REQUIRED_VALUES) {
            assertKnownValue(values[0], (int) values[1], values[2]);
        }
    }

    @Test
    void integrationAndSearchReturnZeroAtTheLowerLimit() {
        assertEquals(0.0, StudentTSearch.integrate(0.0, 6), 0.0);
        assertEquals(0.0, StudentTSearch.findX(0.0, 6), 0.0);
    }

    @Test
    void rejectsInvalidProbabilityLimitsAndDegreesOfFreedom() {
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.findX(-0.1, 6));
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.findX(0.5, 6));
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.findX(Double.NaN, 6));
        assertThrows(IllegalArgumentException.class,
                () -> StudentTSearch.findX(Double.POSITIVE_INFINITY, 6));
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.findX(0.2, 0));
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.findX(0.2, -1));
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.integrate(-1.0, 6));
        assertThrows(IllegalArgumentException.class,
                () -> StudentTSearch.integrate(Double.NaN, 6));
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.integrate(1.0, 0));
    }

    @Test
    void localRunPromptsForInputsAndPrintsOnlyXToFiveDecimals() {
        CapturedOutput output = runApp(new String[0], "0.20\n6\n");

        assertTrue(output.stdout.startsWith("Ingresa p : "));
        assertTrue(output.stdout.contains("Ingresa dof: "));
        assertTrue(output.stdout.endsWith("0.55338" + System.lineSeparator()));
        assertFalse(output.stdout.contains("dof=6"));
        assertEquals("", output.stderr);
    }

    @Test
    void commandLineInputsPrintOnlyXToFiveDecimals() {
        CapturedOutput output = runApp(new String[] {"0.45", "15"}, "");

        assertEquals("1.75305" + System.lineSeparator(), output.stdout);
        assertEquals("", output.stderr);
    }

    @Test
    void jenkinsRunPrintsOneConfiguredCalculationWithFiveDecimalPlaces() {
        CapturedOutput output = runApp(new String[] {"--jenkins"}, "");
        String[] fields = output.stdout.trim().split(", ");

        assertEquals(3, fields.length, "Jenkins debe mostrar p, dof y un único resultado x");
        assertTrue(fields[0].matches("p=\\d+\\.\\d{3}"));
        assertTrue(fields[1].matches("dof=\\d+"));
        assertTrue(fields[2].matches("x=\\d+\\.\\d{5}"));
        assertEquals(1, output.stdout.lines().count());

        double probability = Double.parseDouble(fields[0].substring("p=".length()));
        int degreesOfFreedom = Integer.parseInt(fields[1].substring("dof=".length()));
        double actualX = Double.parseDouble(fields[2].substring("x=".length()));
        assertEquals(StudentTSearch.findX(probability, degreesOfFreedom), actualX, 0.000005);
        assertEquals("", output.stderr);
    }

    @Test
    void interactiveRunReportsInvalidOrMissingInput() {
        CapturedOutput invalidProbability = runApp(new String[0], "not-a-number\n");
        assertTrue(invalidProbability.stderr.contains("p debe ser un número decimal"));

        CapturedOutput invalidDegreesOfFreedom = runApp(new String[0], "0.20\nno-entero\n");
        assertTrue(invalidDegreesOfFreedom.stderr.contains("dof debe ser un número entero"));

        CapturedOutput missingInput = runApp(new String[0], "");
        assertTrue(missingInput.stderr.contains("p debe ser un número decimal"));
    }

    private void assertKnownValue(double probability, int degreesOfFreedom, double expectedX) {
        double actualX = StudentTSearch.findX(probability, degreesOfFreedom);

        assertEquals(expectedX, actualX, 0.00001,
                "x calculado para p=" + probability + " y dof=" + degreesOfFreedom);
        assertEquals(probability,
                StudentTSearch.integrate(actualX, degreesOfFreedom),
                StudentTSearch.PROBABILITY_TOLERANCE,
                "integral calculada para p=" + probability + " y dof=" + degreesOfFreedom);
    }

    private CapturedOutput runApp(String[] args, String consoleInput) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        Locale originalLocale = Locale.getDefault();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        PrintStream capturedOut = new PrintStream(stdout, true, StandardCharsets.UTF_8);
        PrintStream capturedErr = new PrintStream(stderr, true, StandardCharsets.UTF_8);

        try {
            System.setIn(new ByteArrayInputStream(consoleInput.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOut);
            System.setErr(capturedErr);
            App.main(args);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);
            Locale.setDefault(originalLocale);
            capturedOut.close();
            capturedErr.close();
        }

        return new CapturedOutput(
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8));
    }

    private static final class CapturedOutput {
        private final String stdout;
        private final String stderr;

        private CapturedOutput(String stdout, String stderr) {
            this.stdout = stdout;
            this.stderr = stderr;
        }
    }
}
