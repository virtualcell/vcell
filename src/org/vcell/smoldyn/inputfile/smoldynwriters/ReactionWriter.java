package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.Species;
import org.vcell.smoldyn.model.SpeciesAndState;
import org.vcell.smoldyn.model.SurfaceReaction;
import org.vcell.smoldyn.model.VolumeReaction;
import org.vcell.smoldyn.simulation.Simulation;

/**
 * @author mfenwick
 *
 */
public class ReactionWriter {
	
	private Model model;
	private PrintWriter writer;

	
	public ReactionWriter(Simulation simulation, PrintWriter writer) {
		this.model = simulation.getModel();
		this.writer = writer;
	}
	
	
	public void write() {
		writer.println("# reactions");
		this.writeVolumeReactions();
		this.writeSurfaceReactions();
		writer.println();
		writer.println();
	}
	
	/**
	 * example (if global): 
	 * 		reaction myreactionname spec1 + spec2 -> spec3 + spec4 0.5
	 * example (if compartment localized):
	 * 		reaction_cmpt funnycompartmentname funnyreactionname spec7 -> spec8 .003
	 */
	private void writeVolumeReactions() {
		VolumeReaction [] volumereactions = model.getVolumeReactions();
		for(VolumeReaction volumereaction : volumereactions) {
			Species [] reactants = volumereaction.getReactants();//getReactantspeciesstates();
			Species [] products = volumereaction.getProducts();
			writer.println(SmoldynFileKeywords.Reaction.reaction + " " + volumereaction.getName() + " " + 
					ReactionWriterHelp.getVolumeReactionParticipantString(reactants) + " -> " + 
					ReactionWriterHelp.getVolumeReactionParticipantString(products) + " " + volumereaction.getRate());
		}
	}
	
	/**
	 * example: reaction_surface surfacename reactionname spec5(up) + spec6(up) -> spec9(up) 17
	 */
	private void writeSurfaceReactions() {
		SurfaceReaction [] surfacereactions = model.getSurfaceReactions();
		for(SurfaceReaction surfacereaction : surfacereactions) {
			SpeciesAndState [] reactants = surfacereaction.getReactants();
			SpeciesAndState [] products = surfacereaction.getProducts();
			writer.println(SmoldynFileKeywords.Reaction.reaction_surface + " " + surfacereaction.getSurface().getName() + " " +
					surfacereaction.getName() + " " + ReactionWriterHelp.getSurfaceReactionParticipantString(reactants) + " -> " + 
					ReactionWriterHelp.getSurfaceReactionParticipantString(products) + " " + surfacereaction.getRate());
		}
	}
}
