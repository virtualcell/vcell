package cbit.vcell.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.List;

@Tag("Fast")
public class DomainsTest {
	private static ExpressionDomain segmentedExpressionDomain;
	private static ExpressionDomain continuousExpressionDomain;

	@BeforeAll
	public static void setUpClass() { // Test Continuous Domain here
		Assertions.assertThrows(IllegalArgumentException.class, () -> new ContinuousDomain(""));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new ContinuousDomain("0,0"));
		Assertions.assertThrows(IllegalArgumentException.class,  () -> new ContinuousDomain("[0,0"));
		Assertions.assertThrows(IllegalArgumentException.class,  () -> new ContinuousDomain("0,0]"));
		Assertions.assertThrows(IllegalArgumentException.class,  () -> new ContinuousDomain("[0 0]"));
		Assertions.assertThrows(IllegalArgumentException.class,  () -> new ContinuousDomain("[0,0}"));
		Assertions.assertThrows(IllegalArgumentException.class,  () -> new ContinuousDomain("[0,NaN]"));
		Assertions.assertThrows(IllegalArgumentException.class,  () -> new ContinuousDomain("[NaN, 2]"));
		Assertions.assertThrows(IllegalArgumentException.class,  () -> new ContinuousDomain("[Boots, 2]"));
		Assertions.assertThrows(IllegalArgumentException.class,  () -> new ContinuousDomain("[0, Cats]"));

		ContinuousDomain identityDomain = new ContinuousDomain("[6, 6]");
		Assertions.assertEquals("[6]", identityDomain.getNotation());
		Assertions.assertEquals("[6]", (new ContinuousDomain("[6]")).getNotation());

		ContinuousDomain simpleInclusiveDomain = new ContinuousDomain("[2.2, 3.5]");
		ContinuousDomain simpleSemiInclusiveDomain1 = new ContinuousDomain("[2.2, 3.5)");
		ContinuousDomain simpleSemiInclusiveDomain2 = new ContinuousDomain("(2.2, 3.5]");
		ContinuousDomain simpleExclusiveDomain = new ContinuousDomain("(2.2, 3.5)");
		Assertions.assertEquals(0, simpleInclusiveDomain.compareTo(new ContinuousDomain("[2.2, 3.5]")));
		Assertions.assertEquals(1, simpleInclusiveDomain.compareTo(simpleSemiInclusiveDomain1));
		Assertions.assertEquals(1, simpleSemiInclusiveDomain1.compareTo(simpleSemiInclusiveDomain2));
		Assertions.assertEquals(1, simpleSemiInclusiveDomain2.compareTo(simpleExclusiveDomain));
		Assertions.assertEquals(-1, simpleExclusiveDomain.compareTo(simpleSemiInclusiveDomain2));
		Assertions.assertEquals(-1, simpleSemiInclusiveDomain2.compareTo(simpleSemiInclusiveDomain1));
		Assertions.assertEquals(-1, simpleSemiInclusiveDomain1.compareTo(simpleInclusiveDomain));
		Assertions.assertEquals(1, simpleInclusiveDomain.compareTo(simpleExclusiveDomain));
		Assertions.assertEquals(-1, simpleExclusiveDomain.compareTo(simpleInclusiveDomain));

		ContinuousDomain everythingDomain = new ContinuousDomain(Double.NEGATIVE_INFINITY, true, Double.POSITIVE_INFINITY, true);
		Assertions.assertFalse(everythingDomain.min().isInclusive() && everythingDomain.max().isInclusive());

		Assertions.assertEquals(new ContinuousDomain(), everythingDomain);
		Assertions.assertEquals("(-Infinity, Infinity)", everythingDomain.getNotation());
		Assertions.assertEquals(new ContinuousDomain("[-Infinity, Infinity]"), everythingDomain);

		ContinuousDomain emptyDomain = new ContinuousDomain("[2, -2]"); // since -2 !> 2, this creates the empty set
		Assertions.assertTrue(emptyDomain.isEmptyDomain(), "Empty domain should be empty");
		Assertions.assertEquals(Double.POSITIVE_INFINITY, emptyDomain.min().value(), "Empty domain did not standardize the internal representation.");
		Assertions.assertEquals(Double.NEGATIVE_INFINITY, emptyDomain.max().value(), "Empty domain did not standardize the internal representation.");

		ContinuousDomain nonEmptyDomain0 = new ContinuousDomain(-2, true, 2, false);
		ContinuousDomain nonEmptyDomain = new ContinuousDomain(" [-2  ,   2.0) ");
		Assertions.assertEquals(nonEmptyDomain0, nonEmptyDomain, "Constructors do not create the same object when they should.");

		ContinuousDomain positiveThreeDomain = new ContinuousDomain("(0, 3 )");
		ContinuousDomain negativeThreeDomain = new ContinuousDomain("[-3, 0)");
		ContinuousDomain zeroDomain = new ContinuousDomain("[0,0]");
		ContinuousDomain strictNegativeDomain = new ContinuousDomain("(-Infinity, 0)");
		ContinuousDomain strictPositiveDomain =  new ContinuousDomain("(0, Infinity)");

		// Intersection
		Assertions.assertEquals(positiveThreeDomain, ContinuousDomain.intersectDomains(positiveThreeDomain));
		ContinuousDomain intersect = ContinuousDomain.intersectDomains(nonEmptyDomain, positiveThreeDomain);
		Assertions.assertEquals(new ContinuousDomain("    (    0.00000,2e0      )"), intersect);
		Assertions.assertEquals(ContinuousDomain.getEmptyDomain(), ContinuousDomain.intersectDomains(intersect, ContinuousDomain.getEmptyDomain()));
		Assertions.assertEquals(ContinuousDomain.getEmptyDomain(), ContinuousDomain.intersectDomains(ContinuousDomain.getEmptyDomain(), intersect));
		Assertions.assertEquals(ContinuousDomain.getEmptyDomain(), ContinuousDomain.intersectDomains(strictNegativeDomain, strictPositiveDomain));
		Assertions.assertEquals(new ContinuousDomain("[-1, 1]"), ContinuousDomain.intersectDomains(
				new ContinuousDomain("[-5, 1]"),
				new ContinuousDomain("[-4, 2]"),
				new ContinuousDomain("[-3, 3]"),
				new ContinuousDomain("[-2, 4]"),
				new ContinuousDomain("[-1, 5]")
		));
		// Union
		List<ContinuousDomain> redundantUnion = ContinuousDomain.unifyDomains(everythingDomain, positiveThreeDomain);
		Assertions.assertEquals(1, redundantUnion.size());
		Assertions.assertEquals(everythingDomain, redundantUnion.get(0));
		List<ContinuousDomain> identityUnion = ContinuousDomain.unifyDomains(positiveThreeDomain, positiveThreeDomain);
		Assertions.assertEquals(1, identityUnion.size());
		Assertions.assertEquals(positiveThreeDomain, identityUnion.get(0));
		List<ContinuousDomain> emptyUnion1 = ContinuousDomain.unifyDomains(positiveThreeDomain, emptyDomain);
		Assertions.assertEquals(1, emptyUnion1.size());
		Assertions.assertEquals(positiveThreeDomain, emptyUnion1.get(0));
		List<ContinuousDomain> emptyUnion2 = ContinuousDomain.unifyDomains(emptyDomain, positiveThreeDomain);
		Assertions.assertEquals(1, emptyUnion2.size());
		Assertions.assertEquals(positiveThreeDomain, emptyUnion2.get(0));
		List<ContinuousDomain> intersectingUnion = ContinuousDomain.unifyDomains(nonEmptyDomain, positiveThreeDomain);
		Assertions.assertEquals(1, intersectingUnion.size());
		Assertions.assertEquals(new ContinuousDomain("[-2, 3)"), intersectingUnion.get(0));
		List<ContinuousDomain> barelyConnectingMultiUnion = ContinuousDomain.unifyDomains(positiveThreeDomain, negativeThreeDomain, zeroDomain);
		Assertions.assertEquals(1, barelyConnectingMultiUnion.size());
		Assertions.assertEquals(new ContinuousDomain("[-3, 3)"), barelyConnectingMultiUnion.get(0));
		List<ContinuousDomain> nonContinuousDomain = ContinuousDomain.unifyDomains(strictNegativeDomain, strictPositiveDomain);
		Assertions.assertEquals(2, nonContinuousDomain.size());
		// Difference
		List<ContinuousDomain> identityDifference = ContinuousDomain.differenceOfDomains(positiveThreeDomain, positiveThreeDomain);
		Assertions.assertEquals(1, identityDifference.size());
		Assertions.assertEquals(emptyDomain, identityDifference.get(0));
		List<ContinuousDomain> removeFromNothing = ContinuousDomain.differenceOfDomains(emptyDomain, positiveThreeDomain);
		Assertions.assertEquals(1, removeFromNothing.size());
		Assertions.assertEquals(emptyDomain, removeFromNothing.get(0));
		List<ContinuousDomain> removeNothingFromSomething = ContinuousDomain.differenceOfDomains(positiveThreeDomain, emptyDomain);
		Assertions.assertEquals(1, removeNothingFromSomething.size());
		Assertions.assertEquals(positiveThreeDomain, removeNothingFromSomething.get(0));
		List<ContinuousDomain> eraseItAll = ContinuousDomain.differenceOfDomains(positiveThreeDomain, everythingDomain);
		Assertions.assertEquals(1, eraseItAll.size());
		Assertions.assertTrue(eraseItAll.get(0).isEmptyDomain());
		List<ContinuousDomain> carvingOutZero = ContinuousDomain.differenceOfDomains(everythingDomain, zeroDomain);
		Assertions.assertEquals(2, carvingOutZero.size());
		Assertions.assertEquals(strictNegativeDomain, carvingOutZero.get(0));
		Assertions.assertEquals(strictPositiveDomain, carvingOutZero.get(1));
		List<ContinuousDomain> overlapDiff1 = ContinuousDomain.differenceOfDomains(intersectingUnion.get(0), positiveThreeDomain);
		Assertions.assertEquals(1, overlapDiff1.size());
		Assertions.assertEquals(new ContinuousDomain("[-2, 0]"), overlapDiff1.get(0));
		List<ContinuousDomain> overlapDiff2 = ContinuousDomain.differenceOfDomains(intersectingUnion.get(0), negativeThreeDomain);
		Assertions.assertEquals(1, overlapDiff2.size());
		Assertions.assertEquals(new ContinuousDomain("[0, 3)"), overlapDiff2.get(0));
		List<ContinuousDomain> noOverlapDiff = ContinuousDomain.differenceOfDomains(positiveThreeDomain, negativeThreeDomain);
		Assertions.assertEquals(1, noOverlapDiff.size());
		Assertions.assertEquals(positiveThreeDomain, noOverlapDiff.get(0));

		// Some Expression Domain Testing Here
		DomainsTest.segmentedExpressionDomain = new ExpressionDomain(new ContinuousDomain("[-20, -3)"), new ContinuousDomain("[-2, 5]"), new ContinuousDomain("(6, Infinity)"));
		DomainsTest.continuousExpressionDomain = new ExpressionDomain(zeroDomain, negativeThreeDomain, positiveThreeDomain);
	}

	@Test
	public void toStringTest() {
		Assertions.assertEquals("ExpressionDomain -> [-20, -3)U[-2, 5]U(6, Infinity)", DomainsTest.segmentedExpressionDomain.toString());
		Assertions.assertEquals("ExpressionDomain -> [-3, 3)", DomainsTest.continuousExpressionDomain.toString());
	}

	@Test
	public void emptyIsEmptyTest(){
		ExpressionDomain shouldBecomeEmptyDomain = new ExpressionDomain("[-2, -5]", "[4,3]");
		Assertions.assertEquals(new ExpressionDomain("()"), shouldBecomeEmptyDomain);
		Assertions.assertEquals("ExpressionDomain -> ()", shouldBecomeEmptyDomain.toString());

		ExpressionDomain shouldNotBeEmptyDomain = new ExpressionDomain("[3, 4]", "[-2, -5]");
		Assertions.assertNotEquals(new ExpressionDomain("()"), shouldNotBeEmptyDomain);
		Assertions.assertEquals(new ExpressionDomain("[3, 4]"), shouldNotBeEmptyDomain);
	}

	@Test
	public void unionTest() {
		ExpressionDomain unionResult = ExpressionDomain.unionOfDomains(DomainsTest.continuousExpressionDomain, DomainsTest.segmentedExpressionDomain);
		ExpressionDomain expectedResult = new ExpressionDomain("[-20, 5]", "(6, Infinity)");
		Assertions.assertEquals(expectedResult, unionResult);
	}

	@Test
	public void intersectTest() {
		ExpressionDomain intersectionResult = ExpressionDomain.intersectionOfDomains(DomainsTest.continuousExpressionDomain, DomainsTest.segmentedExpressionDomain);
		ExpressionDomain expectedResult = new ExpressionDomain("[-2, 3)");
		Assertions.assertEquals(expectedResult, intersectionResult);
	}

	@Test
	public void differenceTest() {
		ExpressionDomain diffResult1 = ExpressionDomain.differenceOfDomains(DomainsTest.continuousExpressionDomain, DomainsTest.segmentedExpressionDomain);
		ExpressionDomain expectedResult1 = new ExpressionDomain("[-3, -2)");
		Assertions.assertEquals(expectedResult1, diffResult1);
		ExpressionDomain diffResult2 = ExpressionDomain.differenceOfDomains(DomainsTest.segmentedExpressionDomain, DomainsTest.continuousExpressionDomain);
		ExpressionDomain expectedResult2 = new ExpressionDomain("[-20, -3)", "[3, 5]", "(6, Infinity)");
		Assertions.assertEquals(expectedResult2, diffResult2);

		ExpressionDomain swissCheeseDomain = new ExpressionDomain("[-6,-4]", "(-3, -2)", "[-1, 0)", "(2,5)", "(5,6]");
		ExpressionDomain resultDomain = ExpressionDomain.differenceOfDomains(swissCheeseDomain, new ExpressionDomain("(-6,6)"));
		ExpressionDomain expectedResult = new ExpressionDomain("[-6, -6]", "[6, 6]");
		Assertions.assertEquals(expectedResult, resultDomain);
	}
}


