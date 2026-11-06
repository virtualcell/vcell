package cbit.vcell.desktop;

import cbit.vcell.desktop.SearchOptions.Access;
import cbit.vcell.desktop.SearchOptions.Approach;
import cbit.vcell.desktop.SearchOptions.Field;
import cbit.vcell.desktop.SearchOptions.Order;
import cbit.vcell.desktop.SearchOptions.Saved;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.*;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.VCDocument.VCDocumentType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The advanced search's `SearchOptions` let through only the models of the kinds, owner, save dates, modeling approaches
 * and permissions asked for; `ModelSearch` looks for the text only where asked, and orders the results as asked.
 */
@Tag("Fast")
public class SearchOptionsTest {
	private static final User JESSICA = new User("jessica", new KeyValue("1"));
	private static final User BORIS = new User("boris", new KeyValue("2"));
	private static final Date NOW = new Date(1_800_000_000_000L);
	private int nextKey = 100;

	@Test
	public void letsEveryModelThroughByDefaultAndCountsWhatIsNarrowed() {
		assertEquals(0, SearchOptions.defaults().changedCount());
		assertTrue(SearchOptions.defaults().accepts(this.bioModel("Any", JESSICA, 400, new GroupAccessNone(), MathType.Deterministic), NOW));
		assertEquals(2, this.options(EnumSet.of(Field.NAME), Set.of(VCDocumentType.BIOMODEL_DOC), "", Saved.ANY_TIME,
				EnumSet.allOf(Approach.class), EnumSet.allOf(Access.class), Order.BEST_MATCH).changedCount());
	}

	@Test
	public void narrowsByKindOwnerAndWhenSaved() {
		BioModelInfo recent = this.bioModel("Recent", JESSICA, 3, new GroupAccessNone(), MathType.Deterministic);
		MathModelInfo old = this.mathModel("Old", BORIS, 400, MathType.Stochastic);

		SearchOptions bioModelsOnly = this.options(EnumSet.allOf(Field.class), Set.of(VCDocumentType.BIOMODEL_DOC), "", Saved.ANY_TIME,
				EnumSet.allOf(Approach.class), EnumSet.allOf(Access.class), Order.BEST_MATCH);
		assertTrue(bioModelsOnly.accepts(recent, NOW));
		assertFalse(bioModelsOnly.accepts(old, NOW));

		SearchOptions borisPastYear = this.options(EnumSet.allOf(Field.class), Set.of(VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC),
				"BOR", Saved.PAST_YEAR, EnumSet.allOf(Approach.class), EnumSet.allOf(Access.class), Order.BEST_MATCH);
		assertFalse(borisPastYear.accepts(recent, NOW)); // jessica's
		assertFalse(borisPastYear.accepts(old, NOW)); // boris's, but saved over a year ago
	}

	@Test
	public void narrowsByApproachAnyApplicationWill() {
		BioModelInfo ruleBased = this.bioModel("Rules", JESSICA, 3, new GroupAccessNone(), MathType.Deterministic, MathType.RuleBased);
		MathModelInfo stochastic = this.mathModel("Gillespie", JESSICA, 3, MathType.Stochastic);
		SearchOptions ruleBasedOnly = this.options(EnumSet.allOf(Field.class), Set.of(VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC),
				"", Saved.ANY_TIME, EnumSet.of(Approach.RULE_BASED), EnumSet.allOf(Access.class), Order.BEST_MATCH);
		assertTrue(ruleBasedOnly.accepts(ruleBased, NOW)); // one of its applications is
		assertFalse(ruleBasedOnly.accepts(stochastic, NOW));
	}

	@Test
	public void sortsModelsByPermissions() {
		BioModelInfo published = this.bioModel("Paper", JESSICA, 3, new GroupAccessAll(), MathType.Deterministic);
		published.addPublicationInfo(new PublicationInfo(new KeyValue("9"), published.getVersion().getVersionKey(), "A paper",
				new String[] {"An author"}, "A citation", null, null, null, VCDocumentType.BIOMODEL_DOC, JESSICA, NOW));
		assertEquals(EnumSet.of(Access.PUBLIC, Access.CURATED), SearchOptions.accessOf(published)); // flag isn't Published
		assertEquals(EnumSet.of(Access.PRIVATE), SearchOptions.accessOf(this.bioModel("Mine", JESSICA, 3, new GroupAccessNone(), MathType.Deterministic)));

		SearchOptions privateOnly = this.options(EnumSet.allOf(Field.class), Set.of(VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC),
				"", Saved.ANY_TIME, EnumSet.allOf(Approach.class), EnumSet.of(Access.PRIVATE), Order.BEST_MATCH);
		assertFalse(privateOnly.accepts(published, NOW));
	}

	@Test
	public void looksOnlyWhereAskedAndOrdersAsAsked() {
		BioModelInfo older = this.bioModel("Zebra calcium", JESSICA, 30, new GroupAccessNone(), MathType.Deterministic);
		BioModelInfo newer = this.bioModel("Apple calcium", JESSICA, 3, new GroupAccessNone(), MathType.Deterministic);
		BioModelInfo byOwner = this.bioModel("Glycolysis", BORIS, 10, new GroupAccessNone(), MathType.Deterministic);
		List<VCDocumentInfo> models = List.of(older, newer, byOwner);

		SearchOptions namesByName = this.options(EnumSet.of(Field.NAME), Set.of(VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC), "",
				Saved.ANY_TIME, EnumSet.allOf(Approach.class), EnumSet.allOf(Access.class), Order.NAME);
		assertEquals(List.of(newer, older), ModelSearch.search(models, "calcium", namesByName, NOW));
		assertEquals(List.of(), ModelSearch.search(models, "boris", namesByName, NOW)); // owners not searched

		SearchOptions newest = this.options(EnumSet.allOf(Field.class), Set.of(VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC), "",
				Saved.ANY_TIME, EnumSet.allOf(Approach.class), EnumSet.allOf(Access.class), Order.NEWEST);
		assertEquals(List.of(newer, byOwner, older), ModelSearch.search(models, "", newest, NOW));
	}

	private SearchOptions options(Set<Field> fields, Set<VCDocumentType> types, String owner, Saved saved, Set<Approach> approaches,
			Set<Access> access, Order order) {
		return new SearchOptions(fields, types, owner, saved, approaches, access, order);
	}

	private BioModelInfo bioModel(String name, User owner, int daysAgo, GroupAccess access, MathType... appTypes) {
		String[] apps = new String[appTypes.length];
		for (int index = 0; index < apps.length; index++) apps[index] = "Application " + index;
		BioModelChildSummary summary = new BioModelChildSummary(apps, appTypes, new String[apps.length], new String[apps.length][0],
				new String[apps.length][0], new String[0], new String[apps.length], new int[apps.length]);
		return new BioModelInfo(this.version(name, owner, daysAgo, access), summary, null);
	}

	private MathModelInfo mathModel(String name, User owner, int daysAgo, MathType type) {
		MathModelChildSummary summary = new MathModelChildSummary(type, "Compartmental", 0, new String[0], new String[0], new String[0]);
		return new MathModelInfo(this.version(name, owner, daysAgo, new GroupAccessNone()), new KeyValue(String.valueOf(this.nextKey++)), summary, null);
	}

	private Version version(String name, User owner, int daysAgo, GroupAccess access) {
		return new Version(new KeyValue(String.valueOf(this.nextKey++)), name, owner, access, null, BigDecimal.valueOf(this.nextKey),
				new Date(NOW.getTime() - daysAgo * 86_400_000L), VersionFlag.Current, null);
	}
}
