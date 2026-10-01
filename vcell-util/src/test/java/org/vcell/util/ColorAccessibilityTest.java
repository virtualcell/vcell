package org.vcell.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * WCAG contrast and Machado 2009 / CIELAB ΔE76 checks for {@link ColorUtil#CVD_SAFE_LIGHT}.
 * Constants match {@code .agents/cvd_analysis.py} so the published margin still clears the threshold.
 */
@Tag("Fast")
public class ColorAccessibilityTest {

    private static final double[][] PROTAN = {
            {0.152286, 1.052583, -0.204868},
            {0.114503, 0.786281, 0.099216},
            {-0.003882, -0.048116, 1.051998}
    };
    private static final double[][] DEUTAN = {
            {0.367322, 0.860646, -0.227968},
            {0.280085, 0.672501, 0.047413},
            {-0.011820, 0.042940, 0.968881}
    };
    private static final double[][] TRITAN = {
            {1.255528, -0.076749, -0.178779},
            {-0.078411, 0.930809, 0.147602},
            {0.004733, 0.691367, 0.303900}
    };

    private enum Simulation {
        PROTAN, DEUTAN, TRITAN
    }

    @Test
    public void cvdSafeLightHasSixDocumentedColors() {
        int[][] expected = {
                {0x00, 0x00, 0x00},
                {0x99, 0x99, 0x33},
                {0x00, 0x44, 0x88},
                {0x8C, 0x51, 0x0A},
                {0x00, 0x72, 0xB2},
                {0xCC, 0x66, 0x77}
        };
        assertEquals(expected.length, ColorUtil.CVD_SAFE_LIGHT.length);
        for (int i = 0; i < expected.length; i++) {
            Color color = ColorUtil.CVD_SAFE_LIGHT[i];
            assertEquals(expected[i][0], color.getRed(), "red at " + i);
            assertEquals(expected[i][1], color.getGreen(), "green at " + i);
            assertEquals(expected[i][2], color.getBlue(), "blue at " + i);
            assertEquals(255, color.getAlpha(), "alpha at " + i);
        }
    }

    @Test
    public void cvdSafeLightMeetsContrastOnWhite() {
        for (int i = 0; i < ColorUtil.CVD_SAFE_LIGHT.length; i++) {
            double ratio = contrastAgainstWhite(ColorUtil.CVD_SAFE_LIGHT[i]);
            assertTrue(ratio >= 3.0, "CVD_SAFE_LIGHT[" + i + "] contrast " + ratio + " is below 3:1 on white");
        }
    }

    @Test
    public void cvdSafeLightSeparatesUnderMachado() {
        for (Simulation simulation : Simulation.values()) {
            double minimum = minDeltaE76(ColorUtil.CVD_SAFE_LIGHT, simulation);
            assertTrue(minimum >= 15.0, simulation + " min ΔE76 " + minimum + " is below 15");
        }
    }

    @Test
    public void seriesStylePairsUniqueForFirst24() {
        Set<String> pairs = new HashSet<>();
        for (int i = 0; i < 24; i++) {
            Color color = ColorUtil.seriesColor(i);
            String pair = color.getRGB() + "|" + dashSignature(ColorUtil.seriesDash(i));
            assertTrue(pairs.add(pair), "duplicate style at i=" + i + ": " + pair);
            assertEquals(ColorUtil.seriesColor(i % 6), color);
        }
        assertEquals(24, pairs.size());
        assertNull(ColorUtil.seriesDash(0));
        assertArrayEquals(new float[]{6f, 3f}, ColorUtil.seriesDash(1));
        assertArrayEquals(new float[]{2f, 2f}, ColorUtil.seriesDash(2));
        assertArrayEquals(new float[]{8f, 3f, 2f, 3f}, ColorUtil.seriesDash(3));
        assertArrayEquals(new float[]{8f, 3f, 2f, 3f}, ColorUtil.seriesDash(6));
        assertNull(ColorUtil.seriesDash(7));
        assertEquals(ColorUtil.seriesColor(0), ColorUtil.seriesColor(24));
        assertNull(ColorUtil.seriesDash(24));
    }

    @Test
    public void seriesDashReturnsDefensiveCopy() {
        float[] first = ColorUtil.seriesDash(1);
        first[0] = 0f;
        assertArrayEquals(new float[]{6f, 3f}, ColorUtil.seriesDash(1));
    }

    @Test
    public void seriesIndexRejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> ColorUtil.seriesColor(-1));
        assertThrows(IllegalArgumentException.class, () -> ColorUtil.seriesDash(-1));
    }

    @Test
    public void tableau20First8FailsTheMetric() {
        Color[] first8 = Arrays.copyOf(ColorUtil.TABLEAU20, 8);
        assertFalse(meetsCvdSafeMetric(first8));
    }

    /**
     * Enable from #1604 once a dark-background palette constant exists.
     * The assertion should mirror {@link #cvdSafeLightMeetsContrastOnWhite()} against that constant.
     */
    @Test
    @Disabled("Enabled by #1604 when a dark-background palette constant exists")
    public void cvdSafeDarkPaletteMeetsContrastOnDarkBackground() {
    }

    private static boolean meetsCvdSafeMetric(Color[] colors) {
        for (Color color : colors) {
            if (contrastAgainstWhite(color) < 3.0) {
                return false;
            }
        }
        for (Simulation simulation : Simulation.values()) {
            if (minDeltaE76(colors, simulation) < 15.0) {
                return false;
            }
        }
        return true;
    }

    private static String dashSignature(float[] dash) {
        return dash == null ? "solid" : Arrays.toString(dash);
    }

    private static double contrastAgainstWhite(Color color) {
        double lighter = Math.max(relativeLuminance(color), relativeLuminance(Color.WHITE));
        double darker = Math.min(relativeLuminance(color), relativeLuminance(Color.WHITE));
        return (lighter + 0.05) / (darker + 0.05);
    }

    private static double relativeLuminance(Color color) {
        return 0.2126 * linearize(color.getRed() / 255.0)
                + 0.7152 * linearize(color.getGreen() / 255.0)
                + 0.0722 * linearize(color.getBlue() / 255.0);
    }

    private static double linearize(double channel) {
        if (channel <= 0.04045) {
            return channel / 12.92;
        }
        return Math.pow((channel + 0.055) / 1.055, 2.4);
    }

    private static double minDeltaE76(Color[] colors, Simulation simulation) {
        double minimum = Double.POSITIVE_INFINITY;
        for (int i = 0; i < colors.length; i++) {
            double[] left = lab(simulate(colors[i], simulation));
            for (int j = i + 1; j < colors.length; j++) {
                double[] right = lab(simulate(colors[j], simulation));
                minimum = Math.min(minimum, deltaE76(left, right));
            }
        }
        return minimum;
    }

    private static double[] simulate(Color color, Simulation simulation) {
        double[] linear = {
                linearize(color.getRed() / 255.0),
                linearize(color.getGreen() / 255.0),
                linearize(color.getBlue() / 255.0)
        };
        double[][] matrix = switch (simulation) {
            case PROTAN -> PROTAN;
            case DEUTAN -> DEUTAN;
            case TRITAN -> TRITAN;
        };
        double[] simulated = new double[3];
        for (int row = 0; row < 3; row++) {
            simulated[row] = matrix[row][0] * linear[0]
                    + matrix[row][1] * linear[1]
                    + matrix[row][2] * linear[2];
        }
        return simulated;
    }

    private static double[] lab(double[] linear) {
        double r = clamp01(linear[0]);
        double g = clamp01(linear[1]);
        double b = clamp01(linear[2]);
        double x = (0.4124 * r + 0.3576 * g + 0.1805 * b) / 0.95047;
        double y = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 1.0;
        double z = (0.0193 * r + 0.1192 * g + 0.9505 * b) / 1.08883;
        double fx = labF(x);
        double fy = labF(y);
        double fz = labF(z);
        return new double[]{116.0 * fy - 16.0, 500.0 * (fx - fy), 200.0 * (fy - fz)};
    }

    private static double labF(double t) {
        if (t > 216.0 / 24389.0) {
            return Math.cbrt(t);
        }
        return (24389.0 / 27.0 * t + 16.0) / 116.0;
    }

    private static double clamp01(double value) {
        return Math.min(1.0, Math.max(0.0, value));
    }

    private static double deltaE76(double[] left, double[] right) {
        double dl = left[0] - right[0];
        double da = left[1] - right[1];
        double db = left[2] - right[2];
        return Math.sqrt(dl * dl + da * da + db * db);
    }
}
