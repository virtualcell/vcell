package org.vcell.model.rbm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.model.rbm.RuleAnalysis.MolecularComponentEntry;
import org.vcell.model.rbm.RuleAnalysis.MolecularTypeEntry;
import org.vcell.model.rbm.RuleAnalysis.ReactantBondEntry;

public class RuleAnalysisReport {
	public static abstract class Operation {
		
	}

	public static class DeleteParticipantOperation extends Operation {
		public final RuleAnalysis.ParticipantEntry removedParticipantEntry;
		
		public DeleteParticipantOperation(RuleAnalysis.ParticipantEntry removedParticipantEntry){
			this.removedParticipantEntry = removedParticipantEntry;
		}

		@Override
		public String toString() {
			return "DELETE PARTICIPANT "+RuleAnalysis.getID(removedParticipantEntry);
		}
	}

	public static class DeleteMolecularTypeOperation extends Operation {
		public final RuleAnalysis.MolecularTypeEntry removedReactantMolecularEntry;
		
		public DeleteMolecularTypeOperation(RuleAnalysis.MolecularTypeEntry removedReactantMolecularEntry) {
			this.removedReactantMolecularEntry = removedReactantMolecularEntry;
		}
		
		@Override
		public String toString(){
			return "DELETE MOLECULE "+RuleAnalysis.getID(removedReactantMolecularEntry);
		}
	}

	public static class AddMolecularTypeOperation extends Operation {
		public final RuleAnalysis.MolecularTypeEntry unmatchedProductMoleculeEntry;
	
		public AddMolecularTypeOperation(RuleAnalysis.MolecularTypeEntry unmatchedProductMoleculeEntry) {
			this.unmatchedProductMoleculeEntry = unmatchedProductMoleculeEntry;
		}
		
		@Override
		public String toString(){
			return "ADD MOLECULE "+RuleAnalysis.getID(unmatchedProductMoleculeEntry);
		}
		
	}

	public static class DeleteBondOperation extends Operation {
		public final ReactantBondEntry removedBondEntry;
		
		public DeleteBondOperation(ReactantBondEntry removedBondEntry){
			this.removedBondEntry = removedBondEntry;
		}
		
		@Override
		public String toString(){
			return "DELETE BOND from "+RuleAnalysis.getID(removedBondEntry.reactantComponent1)+" to "+RuleAnalysis.getID(removedBondEntry.reactantComponent2);
		}
	}

	public static class AddBondOperation extends Operation {
		public final ReactantBondEntry addedProductBondEntry;
		
		public AddBondOperation(ReactantBondEntry addedProductBondEntry){
			this.addedProductBondEntry = addedProductBondEntry;
		}
		
		@Override
		public String toString(){
			return "ADD BOND from "+RuleAnalysis.getID(addedProductBondEntry.reactantComponent1)+" to "+RuleAnalysis.getID(addedProductBondEntry.reactantComponent2);
		}
		
	}

	public static class ChangeStateOperation extends Operation {
		public final RuleAnalysis.MolecularComponentEntry reactantComponentEntry;
		public final RuleAnalysis.MolecularComponentEntry productComponentEntry;
		public final String newState;
		
		public ChangeStateOperation(RuleAnalysis.MolecularComponentEntry reactantComponentEntry, RuleAnalysis.MolecularComponentEntry productComponentEntry, String newState){
			this.reactantComponentEntry = reactantComponentEntry;
			this.productComponentEntry = productComponentEntry;
			this.newState = newState;
		}
		
		@Override
		public String toString(){
			return "CHANGE STATE ON "+RuleAnalysis.getID(reactantComponentEntry)+" to "+newState;
		}
		
	}

	private double symmetryFactor = 1;
	private final ArrayList<String> errors = new ArrayList<String>();
	private final Map<MolecularTypeEntry,ArrayList<MolecularTypeEntry>> forwardMolecularMapping = new LinkedHashMap<MolecularTypeEntry,ArrayList<MolecularTypeEntry>>();
	private final Map<MolecularComponentEntry, MolecularComponentEntry> forwardComponentMapping = new LinkedHashMap<MolecularComponentEntry,MolecularComponentEntry>();
	private final ArrayList<RuleAnalysisReport.Operation> operations = new ArrayList<RuleAnalysisReport.Operation>();
	
	public void addOperation(RuleAnalysisReport.Operation operation) {
		this.operations.add(operation);
	}
	
	public List<RuleAnalysisReport.Operation> getOperations(){
		return this.operations;
	}
	
	public void map(MolecularComponentEntry reactantComponentEntry, MolecularComponentEntry productComponentEntry){
		this.forwardComponentMapping.put(reactantComponentEntry,productComponentEntry);
	}
	
	public void addMapping(MolecularTypeEntry reactantMolecularTypeEntry, MolecularTypeEntry productMolecularTypeEntry){
		ArrayList<MolecularTypeEntry> matchingProductEntries = this.forwardMolecularMapping.get(reactantMolecularTypeEntry);
		if (matchingProductEntries == null){
			matchingProductEntries = new ArrayList<MolecularTypeEntry>();
			forwardMolecularMapping.put(reactantMolecularTypeEntry, matchingProductEntries);
		}
		matchingProductEntries.add(productMolecularTypeEntry);
	}
	
	public void setSymmetryFactor(double symmetryFactor){
		this.symmetryFactor = symmetryFactor;
	}
	
	public double getSymmetryFactor(){
		return symmetryFactor;
	}
	
	public boolean hasBondChanged(MolecularComponentEntry component){
		for (Operation op : operations){
			if (op instanceof AddBondOperation){
				AddBondOperation bondOp = (AddBondOperation)op;
				if (bondOp.addedProductBondEntry.reactantComponent1 == component ||
					bondOp.addedProductBondEntry.reactantComponent2 == component ||
					forwardComponentMapping.get(bondOp.addedProductBondEntry.reactantComponent1) == component ||
					forwardComponentMapping.get(bondOp.addedProductBondEntry.reactantComponent2) == component){
					return true;
				}
					
			}
			if (op instanceof DeleteBondOperation){
				DeleteBondOperation bondOp = (DeleteBondOperation)op;
				if (bondOp.removedBondEntry.reactantComponent1 == component ||
					bondOp.removedBondEntry.reactantComponent2 == component ||
					forwardComponentMapping.get(bondOp.removedBondEntry.reactantComponent1) == component ||
					forwardComponentMapping.get(bondOp.removedBondEntry.reactantComponent2) == component){
					return true;
				}
					
			}
		}
		return false;
	}
	
	public boolean hasNoMatch(MolecularTypeEntry molecularTypeEntry) {
		System.out.println("-------------------");
		for (Operation op : operations){
			System.out.println(op.toString());
			if (op instanceof AddMolecularTypeOperation){
				AddMolecularTypeOperation addOp = (AddMolecularTypeOperation)op;
				if (addOp.unmatchedProductMoleculeEntry == molecularTypeEntry){
					return true;
				}	
			}
			if (op instanceof DeleteMolecularTypeOperation){
				DeleteMolecularTypeOperation deleteOp = (DeleteMolecularTypeOperation)op;
				if (deleteOp.removedReactantMolecularEntry == molecularTypeEntry){
					return true;
				}
					
			}
			if (op instanceof DeleteParticipantOperation){
				DeleteParticipantOperation deleteOp = (DeleteParticipantOperation)op;
				if (deleteOp.removedParticipantEntry.getMolecularTypeEntries().contains(molecularTypeEntry)){
					return true;
				}
					
			}
		}
		return false;
	}


	public boolean hasStateChanged(MolecularComponentEntry component) {
		for (Operation op : operations){
			if (op instanceof ChangeStateOperation){
				ChangeStateOperation stateOp = (ChangeStateOperation)op;
				if (stateOp.productComponentEntry == component ||
					stateOp.reactantComponentEntry == component){
					return true;
				}
			}
		}
		return false;
	}

	public MolecularComponentEntry getMapping(MolecularComponentEntry component) {
		return forwardComponentMapping.get(component);
	}

	public List<MolecularTypeEntry> getMappedProductMolecules(MolecularTypeEntry reactantMolecule) {
		return forwardMolecularMapping.get(reactantMolecule);
	}

	public Set<MolecularTypeEntry> getMappedReactantMolecules() {
		return forwardMolecularMapping.keySet();
	}

	public Set<MolecularComponentEntry> getMappedReactantComponents() {
		return forwardComponentMapping.keySet();
	}

	public MolecularComponentEntry getMappedProductComponent(MolecularComponentEntry reactantComponentEntry) {
		return forwardComponentMapping.get(reactantComponentEntry);
	}
	@Deprecated
	public String getSummary() {
		Set<String> set = getSummaryAsSet();
		String summary = "";
		for (String line : set){
			summary += line;
		}
		return summary;
	}
	public Set<String> getSummaryAsSet() {
		Set<String> set = new HashSet<String>();
		// molecule mapping
		for (MolecularTypeEntry reactantMolecule : forwardMolecularMapping.keySet()){
			MolecularTypeEntry productMolecules = forwardMolecularMapping.get(reactantMolecule).get(0);
			if(productMolecules == null) {
				set.add("map "+RuleAnalysis.getID(reactantMolecule)+"\n");
			} else {
				set.add("map "+RuleAnalysis.getID(reactantMolecule)+" to "+RuleAnalysis.getID(productMolecules)+"\n");
			}
		}
		// component mapping
		for (MolecularComponentEntry reactantComponent : forwardComponentMapping.keySet()){
			MolecularComponentEntry productComponent = forwardComponentMapping.get(reactantComponent);
			if(productComponent == null) {
				set.add("map "+RuleAnalysis.getID(reactantComponent)+"\n");
			} else {
				set.add("map "+RuleAnalysis.getID(reactantComponent)+" to "+RuleAnalysis.getID(productComponent)+"\n");
			}
		}
		for (Operation op : operations){
			set.add("operation "+op+"\n");
		}
		return set;
	}

	public MolecularComponentEntry getReactantComponentEntry(MolecularComponentEntry productComponent1) {
		for (MolecularComponentEntry reactantComponentEntry : forwardComponentMapping.keySet()){
			if (forwardComponentMapping.get(reactantComponentEntry) == productComponent1){
				return reactantComponentEntry;
			}
		}
		return null;
	}

	public void addError(String error) {
		errors.add(error);
	}
	
	public List<String> getErrors(){
		return errors;
	}


}