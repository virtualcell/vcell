package cbit.vcell.graph.structures;

import java.util.Collections;
import java.util.Set;

import cbit.vcell.model.Structure;

public class SingleStructureSuite extends StructureSuite {

	protected Structure structure;
	protected Set<Structure> structures;
	
	public SingleStructureSuite(Structure structure) {
		super(createTitle(structure));
		this.structure = structure;
		structures = Collections.singleton(structure);
	}
	
	public Set<Structure> getStructures() { return structures; }
	public boolean areReactionsShownFor(Structure structure) {
		return this.structure.equals(structure);
	}
	
	public static String createTitle(Structure structure) {
		return "Reactions for " + structure.getName();
	}
	
}
