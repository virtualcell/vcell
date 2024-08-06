package org.vcell.util;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class BeanUtilsTest {
    @Test
    public void testExampleMappings() {
        int[] scanBounds = {2, 3, 5};
        int[] scanCoordinates = {1, 2, 3};
        int expectedJobIndex = 39;

        // check that index is as expected for scan coordinates
        int index = BeanUtils.parameterScanCoordinateToJobIndex(scanCoordinates, scanBounds);
        assertEquals(expectedJobIndex, index);

        // Convert index back to coordinates and verify that they match the original coordinates
        int[] resultCoordinates = BeanUtils.jobIndexToScanParameterCoordinate(index, scanBounds);
        assertArrayEquals(scanCoordinates, resultCoordinates);
    }

    @Test
    public void testAllScanCoordinatesWithinBounds() {
        int[] scanBounds = {4, 5, 6};

        for (int x = 0; x <= scanBounds[0]; x++) {
            for (int y = 0; y <= scanBounds[1]; y++) {
                for (int z = 0; z <= scanBounds[2]; z++) {
                    int[] coordinates = {x, y, z};

                    // Convert coordinates to index
                    int index = BeanUtils.parameterScanCoordinateToJobIndex(coordinates, scanBounds);

                    // Convert index back to coordinates
                    int[] resultCoordinates = BeanUtils.jobIndexToScanParameterCoordinate(index, scanBounds);

                    // Assert that the original coordinates match the result coordinates
                    assertArrayEquals(coordinates, resultCoordinates);
                }
            }
        }
    }

    @Test
    public void testAllJobIndicesWithinScanBounds() {
        int[] scanBounds = {4, 5, 6};
        int numJobs = (scanBounds[0] + 1) * (scanBounds[1] + 1) * (scanBounds[2] + 1);

        for (int jobIndex = 0; jobIndex < numJobs; jobIndex++) {
            // Convert index to coordinates
            int[] scanCoordinates = BeanUtils.jobIndexToScanParameterCoordinate(jobIndex, scanBounds);
            // Convert coordinates back to index
            int resultIndex = BeanUtils.parameterScanCoordinateToJobIndex(scanCoordinates, scanBounds);

            // Assert that the original index matches the result index
            assertEquals(jobIndex, resultIndex);
        }
    }

}