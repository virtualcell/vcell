package cbit.vcell.desktop;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * `ModelSearch` ranks a model by where the search matches it: name (exact, at its start, at a word's start, anywhere),
 * then owner, then the start of a word in its description; and lists the matches best first.
 */
@Tag("Fast")
public class ModelSearchTest {
	private static final User OWNER = new User("jessica", new KeyValue("1"));
	private int nextKey = 100;

	@Test
	public void ranksNameMatchesAboveOwnerAboveDescription() {
		BioModelInfo exact = this.model("Calcium", null);
		BioModelInfo start = this.model("Calcium Waves", null);
		BioModelInfo word = this.model("SpermCalcium", null); // a capital starts a word
		BioModelInfo anywhere = this.model("Hypercalcium", null);
		BioModelInfo described = this.model("Oscillations", "Calcium release from the ER");

		assertEquals(List.of(0, 1, 2, 3, ModelSearch.IN_DESCRIPTION),
				List.of(exact, start, word, anywhere, described).stream().map(info -> ModelSearch.rank(info, "calcium")).toList());
		assertTrue(ModelSearch.rank(this.model("Glycolysis", null), "jess") < ModelSearch.IN_DESCRIPTION); // its owner
	}

	@Test
	public void findsADescriptionOnlyWhereAWordStarts() {
		assertEquals(ModelSearch.IN_DESCRIPTION, ModelSearch.rank(this.model("Import", "Ran GTPase cycle"), "ran"));
		assertEquals(ModelSearch.NO_MATCH, ModelSearch.rank(this.model("Import", "Membrane transport"), "ran")); // not mid-word
	}

	@Test
	public void listsTheBestFirstKeepingTheOrderOfEqualMatches() {
		BioModelInfo described = this.model("Oscillations", "calcium spikes");
		BioModelInfo firstAnywhere = this.model("Hypercalcium A", null);
		BioModelInfo secondAnywhere = this.model("Hypercalcium B", null);
		BioModelInfo exact = this.model("Calcium", null);
		BioModelInfo unrelated = this.model("Glycolysis", null);

		List<VCDocumentInfo> models = List.of(described, firstAnywhere, unrelated, secondAnywhere, exact);
		assertEquals(List.of(exact, firstAnywhere, secondAnywhere, described), ModelSearch.bestFirst(models, "Calcium"));
		assertEquals(models, ModelSearch.bestFirst(models, "  ")); // no search: all, as they were
	}

	private BioModelInfo model(String name, String description) {
		Version version = new Version(new KeyValue(String.valueOf(this.nextKey++)), name, OWNER, new GroupAccessAll(), null,
				BigDecimal.valueOf(this.nextKey), new Date(1_700_000_000_000L), VersionFlag.Current, description);
		return new BioModelInfo(version, (String) null, null);
	}
}
