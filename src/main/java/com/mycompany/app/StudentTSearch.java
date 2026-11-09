package com.mycompany.app;


public final class StudentTSearch {

    public static final double PROBABILITY_TOLERANCE = 1.0e-10;

    private static final int SIMPSON_INTERVALS = 10_000;
    private static final int MAX_SEARCH_ITERATIONS = 512;

    private StudentTSearch() {
    }

    /**
     * 
     *
     * @param probability desired integral, in the range [0, 0.5)
     * @param degreesOfFreedom positive degrees of freedom
     * @return x such that the integral from 0 to x is within the configured tolerance
     */
    public static double findX(double probability, int degreesOfFreedom) {
        validateDegreesOfFreedom(degreesOfFreedom);
        if (!Double.isFinite(probability) || probability < 0.0 || probability >= 0.5) {
            throw new IllegalArgumentException("p debe estar en el intervalo [0, 0.5).");
        }
        if (probability == 0.0) {
            return 0.0;
        }

        double x = 1.0;
        double step = 0.5;
        int previousErrorSign = 0;

        for (int iteration = 0; iteration < MAX_SEARCH_ITERATIONS; iteration++) {
            double actualProbability = integrate(x, degreesOfFreedom);
            double error = probability - actualProbability;
            if (Math.abs(error) <= PROBABILITY_TOLERANCE) {
                return x;
            }

            int errorSign = error > 0.0 ? 1 : -1;
            if (previousErrorSign != 0 && errorSign != previousErrorSign) {
                step /= 2.0;
            }
            previousErrorSign = errorSign;

            double nextX = x + errorSign * step;
            if (nextX < 0.0) {
                nextX = 0.0;
            }
            if (nextX == x) {
                break;
            }
            x = nextX;
        }

        throw new IllegalStateException("La búsqueda no convergió para p=" + probability
                + " y dof=" + degreesOfFreedom + ".");
    }

    /** Calculates the Student t density integral from 0 to upperLimit using Simpson's rule. */
    public static double integrate(double upperLimit, int degreesOfFreedom) {
        validateDegreesOfFreedom(degreesOfFreedom);
        if (!Double.isFinite(upperLimit) || upperLimit < 0.0) {
            throw new IllegalArgumentException("El límite x debe ser finito y no negativo.");
        }
        // In particular, avoid evaluating Simpson's formula with a zero-width interval.
        if (upperLimit == 0.0) {
            return 0.0;
        }

        double normalization = Math.exp(
                logGamma((degreesOfFreedom + 1.0) / 2.0)
                        - logGamma(degreesOfFreedom / 2.0)
                        - 0.5 * Math.log(degreesOfFreedom * Math.PI));
        double width = upperLimit / SIMPSON_INTERVALS;
        double weightedSum = density(0.0, degreesOfFreedom, normalization)
                + density(upperLimit, degreesOfFreedom, normalization);

        for (int index = 1; index < SIMPSON_INTERVALS; index++) {
            double t = index * width;
            weightedSum += (index % 2 == 0 ? 2.0 : 4.0)
                    * density(t, degreesOfFreedom, normalization);
        }
        return weightedSum * width / 3.0;
    }

    private static double density(double t, int degreesOfFreedom, double normalization) {
        double scaledSquare = (t * t) / degreesOfFreedom;
        return normalization * Math.pow(1.0 + scaledSquare, -(degreesOfFreedom + 1.0) / 2.0);
    }

    private static void validateDegreesOfFreedom(int degreesOfFreedom) {
        if (degreesOfFreedom <= 0) {
            throw new IllegalArgumentException("Los grados de libertad deben ser positivos.");
        }
    }

    /** Lanczos approximation for log Gamma(z), used by the Student t density. */
    private static double logGamma(double value) {
        double[] coefficients = {
            676.5203681218851,
            -1259.1392167224028,
            771.32342877765313,
            -176.61502916214059,
            12.507343278686905,
            -0.13857109526572012,
            9.9843695780195716e-6,
            1.5056327351493116e-7
        };

        double shifted = value - 1.0;
        double sum = 0.99999999999980993;
        for (int index = 0; index < coefficients.length; index++) {
            sum += coefficients[index] / (shifted + index + 1.0);
        }
        double t = shifted + coefficients.length - 0.5;
        return 0.5 * Math.log(2.0 * Math.PI)
                + (shifted + 0.5) * Math.log(t) - t + Math.log(sum);
    }
}
