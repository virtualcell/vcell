package cbit.vcell.graph.structures;

import java.util.List;

import org.vcell.sybil.util.lists.ListOfThree;

import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;

public class MembraneStructureSuite extends StructureSuite {

	protected Membrane membrane;
	protected List<Structure> structures;
	
	public MembraneStructureSuite(Membrane membrane) {
		super(createTitle(membrane));
		this.membrane = membrane;
		structures = new ListOfThree<Structure>(membrane.getOutsideFeature(), membrane, 
				membrane.getInsideFeature());
	}
	
	public List<Structure> getStructures() { return structures; }
	public boolean areReactionsShownFor(Structure structure) { return membrane.equals(structure); }
	
	public static String createTitle(Membrane membrane) {
		return "Reactions for Membrane " + membrane.getName();
	}
		
}
