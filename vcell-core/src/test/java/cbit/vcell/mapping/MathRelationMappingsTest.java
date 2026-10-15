package cbit.vcell.mapping;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Variable;
import cbit.vcell.parser.Expression;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MathRelationMappingsTest {

	private enum ModelCategory{
		NON_SPATIAL_DETERMINISTIC,
		SPATIAL_DETERMINISTIC,
		NON_SPATIAL_STOCHASTIC,
		SPATIAL_STOCHASTIC,
		;
	}

	private record TestCase(ModelCategory category, String simContextName, String startingVariableName, Constant correspondingInitialConditionConstant){ }

	private static final String sourceBioModelRelativePath = "../biomodel/MathDescriptionSampler.vcml";
	private static final List<TestCase> TEST_CASES = Arrays.asList(
			new TestCase(
					ModelCategory.NON_SPATIAL_DETERMINISTIC,
					"non-spatial ODE",
					"C_cyt",
					new Constant("C_cyt_init_uM", new Expression(0.0))
			),
			new TestCase(
					ModelCategory.SPATIAL_DETERMINISTIC,
					"3D pde image",
					"C_cyt",
					new Constant("C_cyt_init_uM", new Expression(0.0))
			),
			new TestCase(
					ModelCategory.NON_SPATIAL_STOCHASTIC,
					"non-spatial stoch",
					"C_cyt_Count",
					new Constant("C_cyt_Count_initCount", new Expression(0.0))
			),
			new TestCase(
					ModelCategory.SPATIAL_STOCHASTIC,
					"3D stoch image",
					"C_cyt",
					new Constant("C_cyt_initCount", new Expression(0.0))
			)
	);

	private static BioModel sourceBioModel;

	@BeforeAll
	public static void preTestChecks() {
		XMLSource bioModelSourceCode;
		try (InputStream inStream = MathRelationMappingsTest.class.getResourceAsStream(sourceBioModelRelativePath)) {
			if (null == inStream) throw new FileNotFoundException();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
			bioModelSourceCode = new XMLSource(reader.lines().collect(Collectors.joining("\n")));
			MathRelationMappingsTest.sourceBioModel = XmlHelper.XMLToBioModel(bioModelSourceCode);
			MathRelationMappingsTest.sourceBioModel.refreshDependencies();
		} catch (Exception e){
			Assertions.fail("Unable to initialize BioModel:", e);
		}

		Map<ModelCategory, TestCase> validityMap = TEST_CASES.stream().collect(Collectors.toMap(tc -> tc.category, Function.identity()));
		if (validityMap.size() != TEST_CASES.size()) Assertions.fail("Duplicate type of test detected; check test case values!");
	}

	public static Collection<MathRelationMappingsTest.TestCase> getTestCases(){
		return TEST_CASES;
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("getTestCases")
	public void testIfInitialConditionsCanBeFetched(MathRelationMappingsTest.TestCase testCase) throws MappingException {
		SimulationContext simContext = MathRelationMappingsTest.sourceBioModel.getSimulationContext(testCase.simContextName());
		simContext.updateAll(false);

		MathDescription mathDesc = simContext.getMathDescription();
		MathRelationMappings relationMappings = new MathRelationMappings(mathDesc);
		Variable startingVariable = mathDesc.getVariable(testCase.startingVariableName());
		Optional<Constant> foundConstant = relationMappings.findInitializingFactor(startingVariable);
		Assertions.assertTrue(foundConstant.isPresent());
		Assertions.assertTrue(foundConstant.get().compareEqual(testCase.correspondingInitialConditionConstant()));
	}
}
