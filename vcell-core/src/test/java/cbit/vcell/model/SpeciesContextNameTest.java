package cbit.vcell.model;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XMLSource;
import org.vcell.util.TokenMangler;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Locks the name rule enforced by {@link SpeciesContext} to what the expression grammar in
 * Parser.jjt actually accepts.
 *
 * These drifted apart once already: name validation used Character.isJavaIdentifier*, which
 * accepts any Unicode letter, while the grammar's LETTER is ["a"-"z","_","A"-"Z"]. A species
 * named PROTEINA_A with an accent was therefore accepted at entry, saved, and then failed math
 * generation with "Parse Error while parsing expression", leaving the application with no
 * generated math (issue #2062). The first test below is the one that keeps them together: it
 * asserts agreement by actually parsing, so tightening or loosening either side without the
 * other fails here.
 */
@Tag("Fast")
public class SpeciesContextNameTest {

	/** Names that must be legal, and names that must not be. */
	private static final String[] LEGAL = {
		"s0", "S", "_", "_s", "Ca_cyt", "sC1", "x2y3", "ABC_123",
	};
	private static final String[] ILLEGAL = {
		// non-ASCII letters: accepted by Character.isJavaIdentifierPart, rejected by the grammar
		"PROTE\u00cdNA_A", "UNI\u00d3N", "\u00f1u", "\u53d7\u5bb9\u4f53", "\u0431\u0435\u043b\u043e\u043a",
		// plain ASCII violations
		"1abc", "a b", "a-b", "a+b", "", "a/b",
	};

	/**
	 * '.' is the one character the parser accepts that a name still may not contain. The grammar's
	 * IDENTIFIER token is (&lt;ID&gt; ".")* &lt;ID&gt;, so a dot separates name-scope qualifiers
	 * (application.parameter) rather than forming part of a single name. A species may therefore
	 * not be called "a.b" even though "a.b" parses.
	 */
	private static final String[] ILLEGAL_BUT_PARSEABLE_AS_QUALIFIED_NAME = {
		"a.b", "app.param",
	};

	@Test
	public void identifierRuleAgreesWithTheExpressionParser() {
		for (String name : LEGAL) {
			assertTrue(TokenMangler.isValidExpressionIdentifier(name),
				"expected '" + name + "' to be a legal identifier");
			assertTrue(parsesAsSingleIdentifier(name),
				"'" + name + "' is accepted by TokenMangler but the expression parser rejects it");
		}
		for (String name : ILLEGAL) {
			assertFalse(TokenMangler.isValidExpressionIdentifier(name),
				"expected '" + name + "' to be rejected as an identifier");
			assertFalse(parsesAsSingleIdentifier(name),
				"'" + name + "' is rejected by TokenMangler but the expression parser accepts it");
		}
		for (String name : ILLEGAL_BUT_PARSEABLE_AS_QUALIFIED_NAME) {
			assertFalse(TokenMangler.isValidExpressionIdentifier(name),
				"a dot-qualified name is not a legal single identifier: '" + name + "'");
			assertTrue(parsesAsSingleIdentifier(name),
				"the grammar is expected to read '" + name + "' as one qualified reference");
		}
	}

	private static boolean parsesAsSingleIdentifier(String name) {
		try {
			Expression exp = new Expression(name);
			String[] symbols = exp.getSymbols();
			return symbols != null && symbols.length == 1 && symbols[0].equals(name);
		} catch (ExpressionException e) {
			return false;
		} catch (RuntimeException e) {
			return false;
		}
	}

	@Test
	public void renameToNonAsciiNameIsRejectedWithAUsefulMessage() throws Exception {
		SpeciesContext sc = newSpeciesContext("Ca_cyt");
		PropertyVetoException ex = assertThrows(PropertyVetoException.class,
			() -> sc.setName("PROTE\u00cdNA_A"));
		assertTrue(ex.getMessage().contains("PROTE\u00cdNA_A"), "message should quote the rejected name");
		assertTrue(ex.getMessage().contains(TokenMangler.IDENTIFIER_RULE_DESCRIPTION),
			"message should state the rule, was: " + ex.getMessage());
		assertEquals("Ca_cyt", sc.getName(), "a rejected rename must not change the name");
	}

	@Test
	public void constructingWithANonAsciiNameIsRejected() {
		assertThrows(RuntimeException.class, () -> newSpeciesContext("PROTE\u00cdNA_A"));
	}

	@Test
	public void aDottedNameIsRejectedEvenThoughItParses() throws Exception {
		SpeciesContext sc = newSpeciesContext("Ca_cyt");
		assertThrows(PropertyVetoException.class, () -> sc.setName("app.Ca"),
			"'.' is the name-scope separator and may not appear inside a name");
	}

	@Test
	public void aLegalRenameStillWorks() throws Exception {
		SpeciesContext sc = newSpeciesContext("Ca_cyt");
		sc.setName("Ca_ext");
		assertEquals("Ca_ext", sc.getName());
	}

	/**
	 * A model saved before the rule was enforced must still open - it is already unrunnable, and
	 * refusing to load it would take away the user's only way to rename the species.
	 */
	@Test
	public void persistedNameLoadsButIsReportedAsAnIssue() throws Exception {
		SpeciesContext sc = SpeciesContext.fromPersistedContent(
			null, "PROTE\u00cdNA_A", new Species("PROTE\u00cdNA_A", null), new Feature("c0"));
		assertEquals("PROTE\u00cdNA_A", sc.getName(), "persisted name must survive the load");

		List<Issue> issues = new ArrayList<>();
		sc.gatherIssues(new IssueContext(), issues);
		Issue nameIssue = null;
		for (Issue issue : issues) {
			if (issue.getMessage() != null && issue.getMessage().contains("PROTE\u00cdNA_A")) {
				nameIssue = issue;
			}
		}
		assertNotNull(nameIssue, "loading an illegal persisted name must raise an issue");
		assertEquals(Issue.Severity.ERROR, nameIssue.getSeverity());
	}

	/** Once loaded, the relaxation must not persist: a later rename is validated normally. */
	@Test
	public void aPersistedContextStillValidatesLaterRenames() throws PropertyVetoException {
		SpeciesContext sc = SpeciesContext.fromPersistedContent(
			null, "PROTE\u00cdNA_A", new Species("PROTE\u00cdNA_A", null), new Feature("c0"));
		assertThrows(PropertyVetoException.class, () -> sc.setName("OTRA_PROTE\u00cdNA"));
	}

	/**
	 * The real regression: a model saved with a name the parser cannot read must still open.
	 * Refusing to load it would be worse than the bug being fixed - it would take away the only
	 * way the user has to rename the species - so XmlReader restores such a name and the problem
	 * is reported as an issue instead.
	 */
	@Test
	public void aVcmlModelHoldingAnIllegalNameStillLoads() throws Exception {
		BioModel bioModel = new BioModel(null);
		bioModel.setName("issue2062");
		Model model = bioModel.getModel();
		Feature cytosol = model.addFeature("cytosol");
		model.addSpecies(new Species("LEGAL_NAME", null));
		model.addSpeciesContext(model.getSpecies("LEGAL_NAME"), cytosol);

		// stand in for a model saved before the rule was enforced
		String vcml = XmlHelper.bioModelToXML(bioModel).replace("LEGAL_NAME", "PROTE\u00cdNA_A");
		assertTrue(vcml.contains("PROTE\u00cdNA_A"), "test setup: the name should be in the VCML");

		BioModel reloaded = XmlHelper.XMLToBioModel(new XMLSource(vcml));
		// the species context is auto-named <species>_<structure>, so match on the species name
		SpeciesContext sc = null;
		for (SpeciesContext candidate : reloaded.getModel().getSpeciesContexts()) {
			if (candidate.getName().contains("PROTE\u00cdNA_A")) {
				sc = candidate;
			}
		}
		assertNotNull(sc, "the species context should have loaded under its persisted name");
		assertFalse(TokenMangler.isValidExpressionIdentifier(sc.getName()),
			"test setup: the loaded name should still be the illegal one");

		List<Issue> issues = new ArrayList<>();
		sc.gatherIssues(new IssueContext(), issues);
		boolean reported = false;
		for (Issue issue : issues) {
			if (issue.getSeverity() == Issue.Severity.ERROR
					&& issue.getMessage() != null && issue.getMessage().contains(sc.getName())) {
				reported = true;
			}
		}
		assertTrue(reported, "the illegal name should be reported as an error issue, issues were: " + issues);
	}

	/**
	 * The importers mangle externally supplied names into identifiers with TokenMangler; that
	 * mangling used the same Unicode-wide predicates and so passed non-ASCII letters through
	 * untouched, producing a token that does not parse.
	 */
	@Test
	public void tokenManglerProducesParseableIdentifiers() {
		for (String name : ILLEGAL) {
			if (name.isEmpty()) {
				continue;
			}
			String fixed = TokenMangler.fixToken(name);
			assertTrue(TokenMangler.isValidExpressionIdentifier(fixed),
				"fixToken('" + name + "') returned '" + fixed + "', which is not a legal identifier");
			String strict = TokenMangler.fixTokenStrict(name);
			assertTrue(TokenMangler.isValidExpressionIdentifier(strict),
				"fixTokenStrict('" + name + "') returned '" + strict + "', which is not a legal identifier");
		}
	}

	@Test
	public void firstIllegalCharPointsAtTheOffendingCharacter() {
		assertEquals(5, TokenMangler.indexOfFirstIllegalIdentifierChar("PROTE\u00cdNA_A"));
		assertEquals(0, TokenMangler.indexOfFirstIllegalIdentifierChar("1abc"));
		assertEquals(1, TokenMangler.indexOfFirstIllegalIdentifierChar("a b"));
		assertEquals(-1, TokenMangler.indexOfFirstIllegalIdentifierChar("Ca_cyt"));
		// a digit is legal everywhere but at the start, so the position matters
		assertEquals(-1, TokenMangler.indexOfFirstIllegalIdentifierChar("a1"));
		assertEquals(0, TokenMangler.indexOfFirstIllegalIdentifierChar("1a"));
	}

	private static SpeciesContext newSpeciesContext(String name) throws PropertyVetoException {
		return new SpeciesContext(null, name, new Species(name, null), new Feature("c0"));
	}
}
