package org.vcell.smoldyn.inputfile.smoldynwriters;

import java.io.PrintWriter;

import org.vcell.smoldyn.inputfile.SmoldynFileKeywords;
import org.vcell.smoldyn.model.Model;
import org.vcell.smoldyn.model.SurfaceReaction;
import org.vcell.smoldyn.model.VolumeReaction;
import org.vcell.smoldyn.model.util.SurfaceReactionParticipants;
import org.vcell.smoldyn.model.util.VolumeReactionParticipants;
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
	
	private void writeVolumeReactions() {
		VolumeReaction [] volumereactions = model.getVolumeReactions();
		for(VolumeReaction volumereaction : volumereactions) {
			VolumeReactionParticipants reactants = volumereaction.getReactants();//getReactantspeciesstates();
			VolumeReactionParticipants products = volumereaction.getProducts();
			writer.println(SmoldynFileKeywords.Reaction.reaction + " " + volumereaction.getName() + " " + 
					ReactionWriterHelp.getReactionParticipantString(reactants) + " -> " + 
					ReactionWriterHelp.getReactionParticipantString(products) + " " + volumereaction.getRate());
		}
	}
	
	private void writeSurfaceReactions() {
		SurfaceReaction [] surfacereactions = model.getSurfaceReactions();
		for(SurfaceReaction surfacereaction : surfacereactions) {
			SurfaceReactionParticipants reactants = surfacereaction.getReactants();
			SurfaceReactionParticipants products = surfacereaction.getProducts();
			writer.println(SmoldynFileKeywords.Reaction.reaction_surface + " " + surfacereaction.getSurface().getName() + " " +
					surfacereaction.getName() + " " + ReactionWriterHelp.getReactionParticipantString(reactants) + " -> " + 
					ReactionWriterHelp.getReactionParticipantString(products) + " " + surfacereaction.getRate());
		}
	}
}
