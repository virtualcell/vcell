package cbit.vcell.geometry;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;

/**
 * The same golden comparison as {@link GeometrySurfaceRegressionTest}, over REAL stored models from
 * the VCML test corpus rather than synthetic shapes.
 *
 * Separate class and separate group because these parse multi-megabyte documents and rebuild
 * regions and surfaces over 0.5-4 MP images. They belong in {@code Geometry_IT}, which
 * regression.yml runs, rather than in the fast lane on every push.
 *
 * They earn their place by being irregular in ways synthetic fixtures are not. The synthetic set is
 * spheres, shells and stripes; a real segmentation has thin features, awkward aspect ratios and
 * disconnected regions sharing a pixel value. {@code corpus_95707047_208x153x83} alone yields SIX
 * regions from TWO pixel classes -- five separate cytosol bodies -- which no synthetic fixture here
 * produces.
 *
 * Note these deliberately REBUILD surfaces rather than reading the stored {@code
 * <SurfaceDescription>}; see {@code GeometrySurfaceGolden.fromCorpus}. Pinning the stored values
 * would test the XML reader instead of surface generation.
 */
@Tag("Geometry_IT")
public class GeometrySurfaceCorpusRegressionTest {

    @TestFactory
    public List<DynamicTest> corpusSurfaceDescriptionsMatchTheDeployedImplementation() {
        return GeometrySurfaceRegressionTest.testsFor(GeometrySurfaceGolden.corpusFixtures());
    }

    @Test
    public void everyCorpusFixtureHasAGolden() {
        GeometrySurfaceRegressionTest.assertEveryFixtureHasAGolden(GeometrySurfaceGolden.corpusFixtures());
    }
}
