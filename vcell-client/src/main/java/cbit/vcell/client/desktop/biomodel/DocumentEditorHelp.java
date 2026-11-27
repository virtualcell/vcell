package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;

import javax.swing.JTabbedPane;

import org.vcell.documentation.HelpIds;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.gui.MolecularStructuresPropertiesPanel;
import cbit.vcell.mapping.gui.ReactionRuleSpecPropertiesPanel;
import cbit.vcell.mapping.gui.SpatialObjectPropertyPanel;
import cbit.vcell.mapping.gui.SpatialProcessPropertyPanel;
import cbit.vcell.mapping.gui.SpeciesContextSpecPanel;
import cbit.vcell.model.gui.KineticsTypeTemplatePanel;
import cbit.vcell.solver.ode.gui.SimulationSummaryPanel;
import cbit.vcell.model.FluxReaction;

/**
 * Which help page explains what a model window shows: its main view (the tree node and the tab
 * open in it, which Help &gt; VCell Help opens) and the Object Properties, Annotations and Problems
 * tabs under it (which the "?" beside those tabs opens).
 */
final class DocumentEditorHelp {
	private DocumentEditorHelp() {
	}

	/** The page for the main view; null when no page explains it. */
	static String pageFor(ActiveView view) {
		if (view == null) return null;
		ActiveViewID id = view.getActiveViewID();
		String page = id == null ? null : DocumentEditorHelp.pageFor(id, view.getSimulationContext());
		if (page != null) return page;
		return DocumentEditorHelp.pageFor(view.getDocumentEditorTreeFolderClass());
	}

	private static String pageFor(ActiveViewID id, SimulationContext application) {
		boolean spatial = application != null && application.getGeometry() != null && application.getGeometry().getDimension() > 0;
		boolean springSaLaD = application != null && application.getApplicationType() == SimulationContext.Application.SPRINGSALAD;
		return switch (id) {
			case model -> HelpIds.PHYSIOLOGY;
			case mathematics_summary, parameters_functions -> HelpIds.PARAMETERS_AND_FUNCTIONS;
			case predefined_symbols -> HelpIds.PREDEFINED_EXPRESSIONS;
			case model_unit_system -> HelpIds.MODEL_UNITS;
			case applications -> HelpIds.APPLICATIONS;
			case reactions -> HelpIds.REACTIONS;
			case structures -> HelpIds.STRUCTURES;
			case species -> HelpIds.SPECIES;
			case species_definitions -> HelpIds.MOLECULES;
			case observables -> HelpIds.OBSERVABLES;
			case reaction_diagram, structure_diagram -> HelpIds.REACTION_DIAGRAM;
			case geometry_definition -> HelpIds.GEOMETRY;
			case structure_mapping -> spatial ? HelpIds.SPATIAL_STRUCTURE_MAPPING : HelpIds.NONSPATIAL_STRUCTURE_MAPPING;
			case spatial_entities, spatial_objects, spatial_processes -> HelpIds.KINEMATICS;
			case species_settings -> springSaLaD ? HelpIds.SPRINGSALAD_SPECIES_SPECIFICATIONS : HelpIds.SPECIES_SPECIFICATIONS;
			case molecular_structure_setting -> HelpIds.SPRINGSALAD_SPECIES_SPECIFICATIONS;
			case reaction_setting -> springSaLaD ? HelpIds.SPRINGSALAD_REACTION_SPECIFICATIONS : HelpIds.REACTION_SPECIFICATIONS;
			case network_setting -> HelpIds.NETWORK_SPECIFICATIONS;
			case network_free_setting -> HelpIds.NETWORK_FREE_SPECIFICATIONS;
			case events -> HelpIds.EVENTS;
			case electrical -> HelpIds.ELECTRICAL;
			case microscope_measuremments -> HelpIds.MICROSCOPE_MEASUREMENTS;
			case rateRules -> HelpIds.RATE_RULES;
			case assignmentRules -> HelpIds.ASSIGNMENT_RULES;
			case simulations, math_simulations -> HelpIds.SIMULATIONS;
			case output_functions, math_output_functions -> HelpIds.OUTPUT_FUNCTIONS;
			case generated_math -> HelpIds.GENERATED_MATH;
			case parameter_estimation -> HelpIds.PARAMETER_ESTIMATION;
			case parameter_estimation_parameters, parameter_estimation_experimental_data_import,
				 parameter_estimation_experimental_data_mapping, parameter_estimation_run_task -> HelpIds.PARAMETER_ESTIMATION_USAGE;
			case math_geometry -> HelpIds.GEOMETRY;
			case membrane_setting -> HelpIds.MEMBRANE_SPECIFICATIONS;
			case math_vcml -> HelpIds.MATH_MODEL_VCML_EDITOR;
			case math_annotation -> HelpIds.MATH_MODEL;
			// the pathway views are in the Pathway Tools window, which has its own help; experimental data
			// has no view any more
			case pathway, pathway_diagram, pathway_objects, biopax_summary, biopax_tree, pathway_lookup, data -> null;
		};
	}

	private static String pageFor(DocumentEditorTreeFolderClass folder) {
		if (folder == null) return null;
		return switch (folder) {
			case MODEL_NODE -> HelpIds.PHYSIOLOGY;
			case REACTIONS_NODE -> HelpIds.REACTIONS;
			case REACTION_DIAGRAM_NODE -> HelpIds.REACTION_DIAGRAM;
			case STRUCTURES_NODE -> HelpIds.STRUCTURES;
			case SPECIES_NODE -> HelpIds.SPECIES;
			case MOLECULAR_TYPES_NODE -> HelpIds.MOLECULES;
			case OBSERVABLES_NODE -> HelpIds.OBSERVABLES;
			case BIOMODEL_PARAMETERS_NODE, PARAMETERS_FUNCTIONS_NODE -> HelpIds.PARAMETERS_AND_FUNCTIONS;
			case PREDEFINED_SYMBOLS_NODE -> HelpIds.PREDEFINED_EXPRESSIONS;
			case MODEL_UNIT_SYSTEM_NODE -> HelpIds.MODEL_UNITS;
			case APPLICATIONS_NODE -> HelpIds.APPLICATIONS;
			case GEOMETRY_NODE, MATH_GEOMETRY_NODE -> HelpIds.GEOMETRY;
			case SPECIFICATIONS_NODE -> HelpIds.SPECIFICATIONS;
			case PROTOCOLS_NODE -> HelpIds.PROTOCOLS;
			case SIMULATIONS_NODE, MATH_SIMULATIONS_NODE -> HelpIds.SIMULATIONS;
			case MATH_OUTPUT_FUNCTIONS_NODE -> HelpIds.OUTPUT_FUNCTIONS;
			case MATH_VCML_NODE -> HelpIds.MATH_MODEL_VCML_EDITOR;
			case MATH_ANNOTATION_NODE -> HelpIds.MATH_MODEL;
			case PARAMETER_ESTIMATION_NODE -> HelpIds.PARAMETER_ESTIMATION;
			default -> null;
		};
	}

	/**
	 * The page for the tab open under the main view: the properties pane for what is selected, the
	 * annotations, the problems; the properties panes' overview when no page explains it.
	 */
	static String pageForBottomTab(JTabbedPane tabs, Object[] selectedObjects) {
		int index = tabs.getSelectedIndex();
		if (index < 0) return HelpIds.PROPERTIES_PANES;
		String title = tabs.getTitleAt(index);
		title = title == null ? "" : title.trim(); // titles may carry counts: " Problems (0 Errors, 0 Warnings) "
		if (title.startsWith(DocumentEditor.TAB_TITLE_PROBLEMS)) return HelpIds.PROBLEMS;
		if (title.startsWith(DocumentEditor.TAB_TITLE_ANNOTATIONS)) return HelpIds.ANNOTATIONS;
		if (title.contains(DocumentEditor.DATABASE_PROPERTIES_TAB_TITLE)) return HelpIds.DATABASE_FILE_INFO;
		if (title.equals(BioModelEditor.NETWORK_GENERATION_TAB_TITLE)) return HelpIds.NETWORK_SPECIFICATIONS;
		if (title.equals(BioModelEditor.PATHWAY_PREVIEW_TAB_TITLE)) return HelpIds.PATHWAY_PREVIEW;
		// Object Properties: the pane for what is selected
		Component shown = tabs.getComponentAt(index);
		Object selected = selectedObjects != null && selectedObjects.length == 1 ? selectedObjects[0] : null;
		if (shown instanceof ReactionPropertiesPanel) {
			return selected instanceof FluxReaction ? HelpIds.MEMBRANE_FLUX_PROPERTIES : HelpIds.REACTION_PROPERTIES;
		}
		if (shown instanceof BioModelEditorAnnotationPanel) return HelpIds.ANNOTATIONS;
		if (shown instanceof ReactionRulePropertiesPanel) return HelpIds.REACTION_RULE_PROPERTIES;
		if (shown instanceof SpeciesPropertiesPanel) return HelpIds.SPECIES_PROPERTIES;
		if (shown instanceof MolecularTypePropertiesPanel) return HelpIds.MOLECULE_PROPERTIES;
		if (shown instanceof ObservablePropertiesPanel) return HelpIds.OBSERVABLE_PROPERTIES;
		if (shown instanceof StructurePropertiesPanel) return HelpIds.STRUCTURE_PROPERTIES;
		if (shown instanceof ParameterPropertiesPanel) return HelpIds.PARAMETER_PROPERTIES;
		if (shown instanceof ApplicationPropertiesPanel) return HelpIds.APPLICATION_PROPERTIES;
		if (shown instanceof ReactionParticipantPropertiesPanel) return HelpIds.STOICHIOMETRY_PROPERTIES;
		if (shown instanceof ParameterEstimationTaskPropertiesPanel) return HelpIds.PARAMETER_ESTIMATION_USAGE;
		if (shown instanceof EventPanel) return HelpIds.EVENTS;
		if (shown instanceof SimulationSummaryPanel) return HelpIds.SIMULATIONS;
		if (shown instanceof SpeciesContextSpecPanel) return HelpIds.SPECIES_SPECIFICATIONS;
		if (shown instanceof MolecularStructuresPropertiesPanel) return HelpIds.SPRINGSALAD_SPECIES_SPECIFICATIONS;
		if (shown instanceof KineticsTypeTemplatePanel || shown instanceof ReactionRuleSpecPropertiesPanel) return HelpIds.REACTION_SPECIFICATIONS;
		if (shown instanceof SpatialObjectPropertyPanel || shown instanceof SpatialProcessPropertyPanel) return HelpIds.KINEMATICS;
		return HelpIds.PROPERTIES_PANES;
	}
}
