package cbit.vcell.graph.structures;

import java.util.Arrays;
import java.util.List;

import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

public class AllStructureSuite extends StructureSuite {

	public static final String TITLE = "Reactions for all Structures";
	
	protected final Model.Owner modelOwner;

	public AllStructureSuite(Model.Owner modelOwner) {
		super(TITLE);
		this.modelOwner = modelOwner;
	}
	
	public List<Structure> getStructures() {
		return Arrays.asList(modelOwner.getModel().getStructures());
	}

	@Override
	public boolean areReactionsShownFor(Structure structure) { return true; }
	
}
