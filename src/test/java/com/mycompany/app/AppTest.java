package com.mycompany.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppTest {

    @Test
    void calculatesTheThreeRequiredValues() {
        assertKnownValue(0.20, 6, 0.55338);
        assertKnownValue(0.45, 15, 1.75305);
        assertKnownValue(0.495, 4, 4.60409);
    }

    @Test
    void handlesZeroAsTheIntegrationLimit() {
        assertEquals(0.0, StudentTSearch.integrate(0.0, 6), 0.0);
        assertEquals(0.0, StudentTSearch.findX(0.0, 6), 0.0);
    }

    @Test
    void rejectsInputsOutsideTheSupportedRange() {
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.findX(-0.1, 6));
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.findX(0.5, 6));
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.findX(0.2, 0));
        assertThrows(IllegalArgumentException.class, () -> StudentTSearch.integrate(-1.0, 6));
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
}
