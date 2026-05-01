package cbit.vcell.units;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Fast")
public class InternalUnitDefinitionRoundTest {

    @Test
    public void roundOfOrdinaryValuesIsIdempotentToTwelveDigits() {
        double[] values = {
                1.0, -1.0, 0.0, 1234.5678, -1234.5678,
                1.234567890123456789, 1e-83, 1e83, Math.PI, Math.E,
        };
        for (double v : values) {
            double r = InternalUnitDefinition.round(v);
            // Twelve significant digits should be preserved exactly.
            assertEquals(r, InternalUnitDefinition.round(r),
                    "round must be idempotent for value " + v);
            // The rounded value must match the original to 12 sig figs.
            if (v != 0.0) {
                double relErr = Math.abs((r - v) / v);
                assertTrue(relErr < 1e-11,
                        "relative error too large for " + v + ": " + relErr);
            }
        }
    }

    @Test
    public void roundPreservesSpecialValues() {
        assertTrue(Double.isNaN(InternalUnitDefinition.round(Double.NaN)));
        assertEquals(Double.POSITIVE_INFINITY,
                InternalUnitDefinition.round(Double.POSITIVE_INFINITY));
        assertEquals(Double.NEGATIVE_INFINITY,
                InternalUnitDefinition.round(Double.NEGATIVE_INFINITY));
        assertEquals(0.0, InternalUnitDefinition.round(0.0));
        // round(-0.0) returns -0.0 (sign preserved); both are mathematically zero.
        assertEquals(0.0, Math.abs(InternalUnitDefinition.round(-0.0)));
    }

    @Test
    public void roundIsThreadSafe() throws InterruptedException {
        // Regression test: a previous implementation used a shared static
        // DecimalFormat. Concurrent access produced strings like
        // "11E.11-83E-83" (= "1E-83" formatted twice with digits
        // interleaved), which then failed Double.parseDouble with
        // NumberFormatException ("multiple points" or "For input string").
        int threads = 16;
        int iterationsPerThread = 5_000;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicInteger failures = new AtomicInteger(0);
        AtomicInteger badResults = new AtomicInteger(0);
        try {
            for (int t = 0; t < threads; t++) {
                final double base = 1.0 + t * 0.0001;
                pool.submit(() -> {
                    for (int i = 0; i < iterationsPerThread; i++) {
                        double value = base * Math.pow(10, (i % 100) - 50);
                        try {
                            double r = InternalUnitDefinition.round(value);
                            if (Double.isNaN(r) != Double.isNaN(value)) {
                                badResults.incrementAndGet();
                            }
                        } catch (RuntimeException e) {
                            failures.incrementAndGet();
                        }
                    }
                });
            }
        } finally {
            pool.shutdown();
            assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS),
                    "thread pool did not terminate in time");
        }
        assertEquals(0, failures.get(),
                "round() threw under concurrent access");
        assertEquals(0, badResults.get(),
                "round() returned wrong-typed result under concurrent access");
    }
}
