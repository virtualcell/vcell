package cbit.vcell.mapping.stoch;

import cbit.image.ImageException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.mapping.*;
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

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements.AllowTruncatedStandardTimeout;
import static cbit.vcell.mapping.stoch.StochasticTransformer.StochasticTransformException;

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
        return new HashSet<>(Arrays.asList(
                "biomodel_100596964.vcml",
                "biomodel_113655498.vcml",
                "biomodel_116929912.vcml",
                "biomodel_116929971.vcml",
                "biomodel_116930032.vcml",
                "biomodel_16763273.vcml",
                "biomodel_16804037.vcml",
                "biomodel_17098642.vcml",
                "biomodel_20253928.vcml",
                "biomodel_211839191.vcml",
                "biomodel_22403233.vcml",
                "biomodel_22403238.vcml",
                "biomodel_22403244.vcml",
                "biomodel_22403250.vcml",
                "biomodel_22403358.vcml",
                "biomodel_22403576.vcml",
                "biomodel_225440511.vcml",
                "biomodel_2912851.vcml",
                "biomodel_2913730.vcml",
                "biomodel_2915537.vcml",
                "biomodel_2917738.vcml",
                "biomodel_2917788.vcml",
                "biomodel_2917999.vcml",
                "biomodel_2930915.vcml",
                "biomodel_2962862.vcml",
                "biomodel_32568171.vcml",
                "biomodel_32568356.vcml",
                "biomodel_55178308.vcml",
                "biomodel_55396830.vcml",
                "biomodel_60203358.vcml",
                "biomodel_60227051.vcml",
                "biomodel_63307133.vcml",
                "biomodel_66265579.vcml",
                "biomodel_7803961.vcml",
                "biomodel_7803976.vcml",
                "biomodel_82250339.vcml",
                "biomodel_98147638.vcml",
                "biomodel_98150237.vcml"
        ));
    }

    public static Set<String> lumpedReactionModelSet() {
        return new HashSet<>(Arrays.asList(
                "lumped_reaction_no_size_in_rate.vcml",
                "lumped_reaction_proper_size_in_rate.vcml",
                "lumped_reaction_local_size_in_rate.vcml",
                "biomodel_100596964.vcml",
                "biomodel_100961371.vcml",
                "biomodel_145545992.vcml",
                "biomodel_156134818.vcml",
                "biomodel_189512756.vcml",
                "biomodel_189513183.vcml",
                "biomodel_211839191.vcml",
                "biomodel_35789302.vcml",
                "biomodel_77305266.vcml",
                "biomodel_81284732.vcml",
                "biomodel_82250339.vcml",
                "biomodel_82799056.vcml",
                "biomodel_82799247.vcml",
                "biomodel_82799266.vcml",
                "biomodel_92354366.vcml",
                "biomodel_98174143.vcml",
                "biomodel_98296160.vcml",
                "biomodel_92383390.vcml",
                "biomodel_92830796.vcml",
                "biomodel_92839940.vcml",
                "biomodel_92845118.vcml",
                "biomodel_92847452.vcml",
                "biomodel_92908902.vcml",
                "biomodel_92932169.vcml",
                "biomodel_92942045.vcml",
                "biomodel_92944917.vcml",
                "biomodel_92946371.vcml",
                "biomodel_92951324.vcml",
                "biomodel_92952350.vcml",
                "biomodel_92955722.vcml",
                "biomodel_92967115.vcml",
                "biomodel_92968671.vcml",
                "biomodel_92981603.vcml"
         ));
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

        final List<String> testCases;

//        testCases = Arrays.asList(
//                "biomodel_100961371.vcml"
//        );
        testCases = new ArrayList<>(filenames);

        return testCases;
    }



    @ParameterizedTest
    @MethodSource("testCases")
    public void testMassActionSolver(String filename) throws XmlParseException, StochasticTransformer.StochasticTransformException, PropertyVetoException, ImageException, GeometryException, IllegalMappingException, ExpressionException, MappingException {
        System.out.println(filename);
        InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);
        String vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
                .lines().collect(Collectors.joining("\n"));
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));

        boolean hasElectricalCurrent = false;
        boolean hasLumpedReaction = false;
        boolean processedAllReactions = true;
		for (ReactionStep reactionStep : bioModel.getModel().getReactionSteps()) {
            try {
                StochasticFunction results = StochasticTransformer.transformToStochastic(reactionStep);
                if (results == null) {
                    throw new IllegalStateException("unable to transform stochastic kinetics for reaction "+reactionStep.getName());
                }
            } catch (StochasticTransformException e) {
                processedAllReactions = false;
                switch (e.errorType) {
                    case LUMPED_KINETICS_NOT_YET_SUPPORTED_FOR_STOCHASTIC_SIMULATION:
                        hasLumpedReaction = true;
                        break;
                    case ELECTRICAL_CURRENT_NOT_SUPPORTED:
                        hasElectricalCurrent = true;
                        break;
                    default:
                        throw e;
                }
            }
		}
        if (hasLumpedReaction && !lumpedReactionModelSet().contains(filename)) {
            throw new IllegalStateException("found undeclared Lumped Kinetics, Add model "+filename+" to lumpedReactionModelSet");
        }
        if (hasElectricalCurrent && !electricalCurrentModelSet().contains(filename)) {
            throw new IllegalStateException("found undeclared Electrical Current, Add model " + filename + " to electricalCurrentModelSet");
        }
        if (!hasElectricalCurrent && electricalCurrentModelSet().contains(filename)) {
            throw new IllegalStateException("model declared to have electric current - but not found, remove " + filename + " from electricalCurrentModelSet");
        }
        if (!hasLumpedReaction && lumpedReactionModelSet().contains(filename)) {
            throw new IllegalStateException("model declared to have lumped reactions - but not found, remove " + filename + " from lumpedReactionModelSet");
        }
        if (processedAllReactions) {
            // create a nonspatial stochastic virtual cell application and generate MathDescription
            // categorize any resulting errors
            SimulationContext stochasticApp = bioModel.addNewSimulationContext("new_test_stochastic_application", SimulationContext.Application.NETWORK_STOCHASTIC);
            // count the number of reaction specs which are not excluded
            int enabledReactionCount = 0;
            for (ReactionSpec reactionStep : stochasticApp.getReactionContext().getReactionSpecs()) {
                if (!reactionStep.isExcluded()) {
                    enabledReactionCount++;
                }
            }
            if (enabledReactionCount > 0) {
                // avoid failures by generating mathDescriptions if we expect no JumpProcesses
                // (some day we should support MathDescriptions with no dynamics)
                SimulationContext.MathMappingCallback callback = new SimulationContext.MathMappingCallback() {
                    @Override public void setMessage(String message) { System.out.println(message); }
                    @Override public void setProgressFraction(float fractionDone) {}
                    @Override public boolean isInterrupted() { return false; }
                };
                stochasticApp.refreshMathDescription(callback, AllowTruncatedStandardTimeout);
            }
        }
    }
}
