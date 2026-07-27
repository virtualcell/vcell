package org.vcell.util.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.math.RoundingMode;

@Tag("Fast")
public class CoordinateFormatTest {


    @Test
    public void testNoiseCleaning() {

        Object[][] cases = {
                // Classic floating‑point noise
                {0.10000000000000002, "0.1"},
                {0.20000000000000004, "0.2"},
                {0.30000000000000004, "0.3"},
                {0.9999999999999997,  "1"},
                {1.0000000000000002,  "1"},

                // Very small values
                {0.0000000000001234, "0"}, // if we want to show 1.234E-13 instead of 0 we'd need to change the code
                {0.0000123456789,    "0.000012"},
                {1e-12,              "0"}, // same as above

                // Large values with noise
                {123456.78900000006, "123456.789"},
                {999999.9999999999,  "1000000"}
        };

        for (Object[] c : cases) {
            double input = (double) c[0];
            String expected = (String) c[1];
            String actual = DialogUtils.clean(input);

            Assertions.assertEquals(
                    expected,
                    actual,
                    "Input: " + input + " should format to " + expected
            );
        }
    }
}

