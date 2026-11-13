package cbit.vcell.client.desktop.biomodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.BiochemistrySummary.Row;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;

@Tag("Fast")
public class BiochemistrySummaryTest {

	private static Row find(List<Row> rows, String label) {
		return rows.stream().filter(row -> row.label().equals(label)).findFirst().orElseThrow(() -> new AssertionError("no " + label + " in " + rows));
	}

	@Test
	public void countsThePhysiologyAsTheTreeDoes() throws Exception {
		BioModel bioModel = new BioModel(null);
		Feature cytosol = bioModel.getModel().addFeature("cyt");
		bioModel.getModel().addFeature("ext");
		Membrane membrane = bioModel.getModel().addMembrane("pm");
		bioModel.getModel().createSpeciesContext(cytosol);
		bioModel.getModel().createSpeciesContext(cytosol);
		bioModel.getModel().createSimpleReaction(cytosol);
		bioModel.getModel().createFluxReaction(membrane);

		List<Row> rows = BiochemistrySummary.of(bioModel);
		Row structures = BiochemistrySummaryTest.find(rows, "Structures (2 compartments, 1 membrane)");
		assertEquals(3, structures.count());
		assertEquals(DocumentEditorTreeFolderClass.STRUCTURES_NODE, structures.opens());
		assertEquals(2, BiochemistrySummaryTest.find(rows, "Species").count());
		assertEquals(2, BiochemistrySummaryTest.find(rows, "Reactions").count(), "a reaction and a flux, as the tree's Reactions counts them");
		assertEquals(0, BiochemistrySummaryTest.find(rows, "Reaction rules").count());
		assertEquals(0, BiochemistrySummaryTest.find(rows, "Pathway objects").count());
		assertEquals(0, BiochemistrySummaryTest.find(rows, "Species linked to the pathway").count());
	}

	@Test
	public void headingsLeadEachPartAndOpenItsNode() throws Exception {
		List<Row> rows = BiochemistrySummary.of(new BioModel(null));
		List<Row> headings = rows.stream().filter(Row::heading).toList();
		assertEquals(List.of("Physiology", "Pathway"), headings.stream().map(Row::label).toList());
		assertEquals(DocumentEditorTreeFolderClass.MODEL_NODE, headings.get(0).opens());
		assertEquals(DocumentEditorTreeFolderClass.PATHWAY_NODE, headings.get(1).opens());
		assertTrue(headings.stream().allMatch(heading -> heading.count() == null), "a heading has no count of its own");
		assertEquals("Structures (0 compartments, 0 membranes)", rows.get(1).label());
		assertNull(BiochemistrySummary.of(null).stream().findAny().orElse(null));
	}
}
