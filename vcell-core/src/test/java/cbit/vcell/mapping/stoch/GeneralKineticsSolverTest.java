package cbit.vcell.mapping.stoch;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MathGenCompareTest;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vcell.sbml.VcmlTestSuiteFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Tag("Fast")
public class GeneralKineticsSolverTest {

    private String savedInstalldirProperty;

    @BeforeEach
    public void setUp() {
        savedInstalldirProperty = System.getProperty(PropertyLoader.installationRoot);
        System.setProperty(PropertyLoader.installationRoot, "..");
    }

    @AfterEach
    public void tearDown() {
        if (savedInstalldirProperty == null) {
            System.clearProperty(PropertyLoader.installationRoot);
        }else{
            System.setProperty(PropertyLoader.installationRoot, savedInstalldirProperty);
        }
    }

    public static Set<String> electricalCurrentModelSet() {
        Set<String> electricalCurrentModels = new HashSet<>();
        electricalCurrentModels.add("biomodel_100596964.vcml");
        electricalCurrentModels.add("biomodel_100961371.vcml");
        electricalCurrentModels.add("biomodel_113655498.vcml");
        electricalCurrentModels.add("biomodel_116929912.vcml");
        electricalCurrentModels.add("biomodel_116929971.vcml");
        electricalCurrentModels.add("biomodel_116930032.vcml");
        electricalCurrentModels.add("biomodel_145545992.vcml");
        electricalCurrentModels.add("biomodel_16763273.vcml");
        electricalCurrentModels.add("biomodel_16804037.vcml");
        electricalCurrentModels.add("biomodel_17098642.vcml");
        electricalCurrentModels.add("biomodel_189512756.vcml");
        electricalCurrentModels.add("biomodel_189513183.vcml");
        electricalCurrentModels.add("biomodel_20253928.vcml");
        electricalCurrentModels.add("biomodel_211839191.vcml");
        electricalCurrentModels.add("biomodel_22403233.vcml");
        electricalCurrentModels.add("biomodel_22403238.vcml");
        electricalCurrentModels.add("biomodel_22403244.vcml");
        electricalCurrentModels.add("biomodel_22403250.vcml");
        electricalCurrentModels.add("biomodel_22403358.vcml");
        electricalCurrentModels.add("biomodel_22403576.vcml");
        electricalCurrentModels.add("biomodel_225440511.vcml");
        electricalCurrentModels.add("biomodel_2912851.vcml");
        electricalCurrentModels.add("biomodel_2913730.vcml");
        electricalCurrentModels.add("biomodel_2915537.vcml");
        electricalCurrentModels.add("biomodel_2917738.vcml");
        electricalCurrentModels.add("biomodel_2917788.vcml");
        electricalCurrentModels.add("biomodel_2917999.vcml");
        electricalCurrentModels.add("biomodel_2930915.vcml");
        electricalCurrentModels.add("biomodel_2962862.vcml");
        electricalCurrentModels.add("biomodel_32568171.vcml");
        electricalCurrentModels.add("biomodel_32568356.vcml");
        electricalCurrentModels.add("biomodel_55178308.vcml");
        electricalCurrentModels.add("biomodel_55396830.vcml");
        electricalCurrentModels.add("biomodel_60203358.vcml");
        electricalCurrentModels.add("biomodel_60227051.vcml");
        electricalCurrentModels.add("biomodel_63307133.vcml");
        electricalCurrentModels.add("biomodel_66265579.vcml");
        electricalCurrentModels.add("biomodel_77305266.vcml");
        electricalCurrentModels.add("biomodel_7803961.vcml");
        electricalCurrentModels.add("biomodel_7803976.vcml");
        electricalCurrentModels.add("biomodel_81284732.vcml");
        electricalCurrentModels.add("biomodel_82250339.vcml");
        electricalCurrentModels.add("biomodel_98147638.vcml");
        electricalCurrentModels.add("biomodel_98150237.vcml");
        electricalCurrentModels.add("biomodel_98174143.vcml");
        electricalCurrentModels.add("biomodel_98296160.vcml");
        return electricalCurrentModels;
    }

    public static Set<String> slowFileSet() {
        Set<String> slowModels = new HashSet<>();
        return slowModels;
    }


    public static Collection<String> testCases() throws XmlParseException, IOException {
        Predicate<String> skipFilter = (t) ->
                !MathGenCompareTest.outOfMemoryFileSet().contains(t)
                        && !MathGenCompareTest.largeFileSet().contains(t)
                        && !slowFileSet().contains(t);
        List<String> filenames = Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter).toList();

        ArrayList<String> testCases = new ArrayList<>();
        for (String filename : filenames){
//if (true
//&& !filename.equals("biomodel_100961371.vcml")
//) continue;
            testCases.add(filename);
        }
        return testCases;
    }



    @ParameterizedTest
    @MethodSource("testCases")
    public void testMassActionSolver(String filename) throws XmlParseException, ExpressionException {
        System.out.println(filename);
        InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);
        String vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
                .lines().collect(Collectors.joining("\n"));
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));

		StochasticTransformer solver = new StochasticTransformer();

		for (ReactionStep reactionStep : bioModel.getModel().getReactionSteps()) {
            try {
                StochasticFunction results = solver.transformToStochastic(reactionStep);
                if (results == null || (results.massActionFunction == null) && (results.generalKineticsStochasticFunction == null)) {
                    throw new IllegalStateException("unable to transform stochastic kinetics for reaction "+reactionStep.getName());
                }
            } catch (Exception e) {
                boolean bExplained = e.getMessage().contains("has membrane current") && electricalCurrentModelSet().contains(filename);
                if (!bExplained) {
                    throw e;
                }
            }
		}
    }
}
