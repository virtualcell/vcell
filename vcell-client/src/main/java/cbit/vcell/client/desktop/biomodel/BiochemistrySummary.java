package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;
import java.util.List;

import org.vcell.relationship.RelationshipModel;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;

/**
 * What a BioModel's Biochemistry holds, counted as the navigation tree's labels count it: its Physiology (structures,
 * species, molecules, observables, reactions and rules) and its Pathway (the imported pathway objects, and how they're
 * linked to the physiology).
 */
public final class BiochemistrySummary {
	private BiochemistrySummary() {}

	/**
	 * One counted row. A heading names a part (Physiology, Pathway) and has no count of its own; <code>opens</code> is
	 * the tree node whose tab lists what's counted (null: nothing more to show).
	 */
	public record Row(String label, Integer count, boolean heading, DocumentEditorTreeFolderClass opens) {}

	/** The rows, Physiology's then Pathway's. */
	public static List<Row> of(BioModel bioModel) {
		List<Row> rows = new ArrayList<>();
		if (bioModel == null) return rows;
		Model model = bioModel.getModel();
		Model.RbmModelContainer rbm = model.getRbmModelContainer();

		rows.add(new Row("Physiology", null, true, DocumentEditorTreeFolderClass.MODEL_NODE));
		int membranes = 0;
		for (Structure structure : model.getStructures()) {
			if (structure instanceof Membrane) membranes++;
		}
		int compartments = model.getStructures().length - membranes;
		rows.add(new Row("Structures (" + BiochemistrySummary.counted(compartments, "compartment", "compartments") + ", "
				+ BiochemistrySummary.counted(membranes, "membrane", "membranes") + ")", model.getStructures().length, false, DocumentEditorTreeFolderClass.STRUCTURES_NODE));
		rows.add(new Row("Species", model.getSpeciesContexts().length, false, DocumentEditorTreeFolderClass.SPECIES_NODE));
		rows.add(new Row("Molecules", rbm.getMolecularTypeList().size(), false, DocumentEditorTreeFolderClass.MOLECULAR_TYPES_NODE));
		rows.add(new Row("Observables", rbm.getObservableList().size(), false, DocumentEditorTreeFolderClass.OBSERVABLES_NODE));
		rows.add(new Row("Reactions", model.getReactionSteps().length, false, DocumentEditorTreeFolderClass.REACTIONS_NODE));
		rows.add(new Row("Reaction rules", rbm.getReactionRuleList().size(), false, DocumentEditorTreeFolderClass.REACTIONS_NODE));

		rows.add(new Row("Pathway", null, true, DocumentEditorTreeFolderClass.PATHWAY_NODE));
		rows.add(new Row("Pathway objects", bioModel.getPathwayModel().getBiopaxObjects().size(), false, DocumentEditorTreeFolderClass.PATHWAY_OBJECTS_NODE));
		RelationshipModel relationships = bioModel.getRelationshipModel();
		rows.add(new Row("Links to physiology", relationships.getRelationshipObjects().size(), false, DocumentEditorTreeFolderClass.PATHWAY_OBJECTS_NODE));
		int linkedSpecies = 0;
		for (SpeciesContext species : model.getSpeciesContexts()) {
			if (!relationships.getRelationshipObjects(species).isEmpty()) linkedSpecies++;
		}
		int linkedReactions = 0;
		for (ReactionStep reaction : model.getReactionSteps()) {
			if (!relationships.getRelationshipObjects(reaction).isEmpty()) linkedReactions++;
		}
		rows.add(new Row("Species linked to the pathway", linkedSpecies, false, DocumentEditorTreeFolderClass.SPECIES_NODE));
		rows.add(new Row("Reactions linked to the pathway", linkedReactions, false, DocumentEditorTreeFolderClass.REACTIONS_NODE));
		return rows;
	}

	private static String counted(int count, String one, String many) {
		return count + " " + (count == 1 ? one : many);
	}
}
