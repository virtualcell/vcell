package org.vcell.model.rbm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jdom.Element;
import org.vcell.solver.nfsim.NFsimXMLWriter.BondSites;
import org.vcell.solver.nfsim.NFsimXMLWriter.ComponentOfMolecularTypeOfReactionParticipant;
import org.vcell.solver.nfsim.NFsimXMLWriter.MappingOfReactionParticipants;
import org.vcell.solver.nfsim.NFsimXMLWriter.MolecularTypeOfReactionParticipant;
import org.vcell.util.Compare;

import com.sun.org.apache.xalan.internal.xsltc.dom.MatchingIterator;

import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;

public abstract class RuleAnalysis {
	
	public class MoleculeMapping {
		public ReactantPattern sourceReactantPattern;
		public MolecularTypePattern sourceMolecularTypePattern;
		//public MolecularComponentPattern source;
		
		public ProductPattern targetProductPattern;
		public MolecularTypePattern targetMolecularTypePattern;
		//public MolecularComponentPattern target;
	}
	
	public abstract class Operation {
		
	}
	
	public class DeleteParticipantOperation extends Operation {
		public final ParticipantEntry removedParticipantEntry;
		
		public DeleteParticipantOperation(ParticipantEntry removedParticipantEntry){
			this.removedParticipantEntry = removedParticipantEntry;
		}
	}
	
	public class DeleteMolecularTypeOperation extends Operation {
		public final MolecularTypeEntry removedReactantMolecularEntry;
		
		public DeleteMolecularTypeOperation(MolecularTypeEntry removedReactantMolecularEntry) {
			this.removedReactantMolecularEntry = removedReactantMolecularEntry;
		}
	}
	
	public class AddMolecularTypeOperation extends Operation {
		public final MolecularTypeEntry unmatchedProductMoleculeEntry;

		public AddMolecularTypeOperation(MolecularTypeEntry unmatchedProductMoleculeEntry) {
			this.unmatchedProductMoleculeEntry = unmatchedProductMoleculeEntry;
		}
		
	}
	
	public class DeleteBondOperation extends Operation {
		public final BondEntry removedBondEntry;
		
		public DeleteBondOperation(BondEntry removedBondEntry){
			this.removedBondEntry = removedBondEntry;
		}
	}

	public class AddBondOperation extends Operation {
		public final BondEntry addedProductBondEntry;
		
		public AddBondOperation(BondEntry addedProductBondEntry){
			this.addedProductBondEntry = addedProductBondEntry;
		}
		
	}
	
	public class ChangeStateOperation extends Operation {
		public final MolecularComponentEntry reactantComponentEntry;
		public final MolecularComponentEntry productComponentEntry;
		public final String newState;
		
		public ChangeStateOperation(MolecularComponentEntry reactantComponentEntry, MolecularComponentEntry productComponentEntry, String newState){
			this.reactantComponentEntry = reactantComponentEntry;
			this.productComponentEntry = productComponentEntry;
			this.newState = newState;
		}
	}	
	
	private Integer symmetryFactor;
	public static final int INDEX_OFFSET = 0;
	protected ArrayList<ParticipantEntry> reactantEntries = new ArrayList<ParticipantEntry>();
	protected ArrayList<ParticipantEntry> productEntries = new ArrayList<ParticipantEntry>();
	protected ArrayList<MolecularTypeEntry> reactantMolecularTypeEntries = new ArrayList<MolecularTypeEntry>();
	protected ArrayList<MolecularComponentEntry> reactantMolecularComponentEntries = new ArrayList<MolecularComponentEntry>();
	protected ArrayList<MolecularTypeEntry> productMolecularTypeEntries = new ArrayList<MolecularTypeEntry>();
	protected ArrayList<MolecularComponentEntry> productMolecularComponentEntries = new ArrayList<MolecularComponentEntry>();
	protected ArrayList<BondEntry> reactantBondEntries = new ArrayList<BondEntry>();
	protected ArrayList<BondEntry> productBondEntries = new ArrayList<BondEntry>();
	
	public RuleAnalysis(){
	}
	
	public Integer getSymmetryFactor(){
		return symmetryFactor;
	}
	
	public enum ParticipantType {
		Reactant,
		Product
	};
	
	public interface ParticipantEntry {
		public String getRuleName();
		
		public int getRuleIndex();
		
		public int getParticipantIndex();
		
		public ParticipantType getParticipantType();
		
		public List<? extends MolecularTypeEntry> getMolecularTypeEntries();
		
	}
	
	public interface MolecularTypeEntry {
		
		public ParticipantEntry getParticipantEntry();

		public int getMoleculeIndex();

		public boolean matches(MolecularTypeEntry productMTE);

		public String getMolecularTypeName();
		
		public List<? extends MolecularComponentEntry> getMolecularComponentEntries();
	}
	
	public interface MolecularComponentEntry {
		public int getComponentIndex();
		
		MolecularTypeEntry getMolecularTypeEntry();

		String getMolecularComponentName();

		String getExplicitState();

		boolean isBoundTo(MolecularComponentEntry productComponent);
	}
	
	public class BondEntry {
		public final MolecularComponentEntry component1;
		public final MolecularComponentEntry component2;

		public BondEntry(MolecularComponentEntry component1, MolecularComponentEntry component2) {
			this.component1 = component1;
			this.component2 = component2;
		}
	}
	
	protected abstract void populateRuleInfo();
		
	public void analyze() {
		
		this.symmetryFactor = 1;		
		
		ArrayList<MolecularTypeEntry> unmatchedReactantMolecularTypeEntries = new ArrayList<MolecularTypeEntry>(reactantMolecularTypeEntries);
		ArrayList<MolecularTypeEntry> unmatchedProductMolecularTypeEntries = new ArrayList<MolecularTypeEntry>(productMolecularTypeEntries);
		//
		// compute molecular type mappings (must be unique)
		//
		HashMap<MolecularTypeEntry,ArrayList<MolecularTypeEntry>> forwardMolecularMapping = new HashMap<MolecularTypeEntry,ArrayList<MolecularTypeEntry>>();
		for (MolecularTypeEntry reactantMTE : reactantMolecularTypeEntries){
			for (MolecularTypeEntry productMTE : productMolecularTypeEntries){
				if (reactantMTE.matches(productMTE)){
					ArrayList<MolecularTypeEntry> matchingProductEntries = forwardMolecularMapping.get(reactantMTE);
					if (matchingProductEntries == null){
						matchingProductEntries = new ArrayList<MolecularTypeEntry>();
						forwardMolecularMapping.put(reactantMTE, matchingProductEntries);
					}
					matchingProductEntries.add(productMTE);
					unmatchedReactantMolecularTypeEntries.remove(reactantMTE);
					unmatchedReactantMolecularTypeEntries.remove(productMTE);
				}
			}
		}
		
		//
		// print unmatched entries
		//
		for (MolecularTypeEntry mte : unmatchedReactantMolecularTypeEntries){
			System.out.println("unmatched reactant "+getID(mte));
		}
		for (MolecularTypeEntry mte : unmatchedProductMolecularTypeEntries){
			System.out.println("unmatched product "+getID(mte));
		}
		//
		// verify unique match and print matches
		//
		for (Entry<MolecularTypeEntry, ArrayList<MolecularTypeEntry>> reactantMolecularTypeEntry : forwardMolecularMapping.entrySet()){
			MolecularTypeEntry reactantMTE = reactantMolecularTypeEntry.getKey();
			ArrayList<MolecularTypeEntry> matches = reactantMolecularTypeEntry.getValue();
			if (reactantMolecularTypeEntries.size() > 1){
				throw new RuntimeException("reactant molecular pattern id: "+getID(reactantMTE)+" matched more than one product pattern "+matches);
			}else{
				System.out.println("matched sourceID="+getID(reactantMTE)+", targetID="+getID(matches.get(0)));
			}
		}
		
		//
		// find all component mappings (looking in the matched molecules).
		//
		HashMap<MolecularComponentEntry, MolecularComponentEntry> forwardComponentMapping = new HashMap<MolecularComponentEntry,MolecularComponentEntry>();
		for (MolecularTypeEntry mappedReactantMolecularTypeEntry : forwardMolecularMapping.keySet()){
			ArrayList<MolecularTypeEntry> mappedProductMolecularTypeList = forwardMolecularMapping.get(mappedReactantMolecularTypeEntry);
			if (mappedProductMolecularTypeList.size() != 1){
				// if not checked earlier
				throw new RuntimeException("reactant molecular pattern id: "+getID(mappedReactantMolecularTypeEntry)+" matched more than one product pattern "+mappedProductMolecularTypeList);
			}
			MolecularTypeEntry mappedProductMolecularTypeEntry = mappedProductMolecularTypeList.get(0);
			List<? extends MolecularComponentEntry> reactantMolecularComponents = mappedReactantMolecularTypeEntry.getMolecularComponentEntries();
			List<? extends MolecularComponentEntry> productMolecularComponents = mappedProductMolecularTypeEntry.getMolecularComponentEntries();
			if (reactantMolecularComponents.size() != productMolecularComponents.size()){
				throw new RuntimeException("different number of molecular components for mapped molecules");
			}
			for (int componentIndex=0; componentIndex<reactantMolecularComponents.size(); componentIndex++){
				MolecularComponentEntry reactantComponentEntry = reactantMolecularComponents.get(componentIndex);
				MolecularComponentEntry productComponentEntry = productMolecularComponents.get(componentIndex);
				forwardComponentMapping.put(reactantComponentEntry, productComponentEntry);
			}
		}
		
        //  <Map>
        //    <MapItem sourceID="RR1_RP1_M1" targetID="RR1_PP1_M1"/>
        //    <MapItem sourceID="RR1_RP1_M1_C1" targetID="RR1_PP1_M1_C1"/>
        //    <MapItem sourceID="RR1_RP1_M1_C2" targetID="RR1_PP1_M1_C2"/>
        //    <MapItem sourceID="RR1_RP2_M1" targetID="RR1_PP1_M2"/>
        //    <MapItem sourceID="RR1_RP2_M1_C1" targetID="RR1_PP1_M2_C1"/>
        //  </Map>
		System.out.println("----------------------------------------------------------------------");
		for(MolecularTypeEntry p : reactantMolecularTypeEntries) {
			System.out.println(p.getMolecularTypeName() + ", " + getID(p));
		}
		for(MolecularComponentEntry c : reactantMolecularComponentEntries) {
			System.out.println(c.getMolecularTypeEntry().getMolecularTypeName() + ", " + c.getMolecularComponentName() + ", " + getID(c));
		}
		System.out.println("----------------------------------------------------------------------");
		for(MolecularTypeEntry p : productMolecularTypeEntries) {
			System.out.println(p.getMolecularTypeName() + ", " + getID(p));
		}
		for(MolecularComponentEntry c : productMolecularComponentEntries) {
			System.out.println(c.getMolecularTypeEntry().getMolecularTypeName() + ", " + c.getMolecularComponentName() + ", " + getID(c));
		}
		System.out.println("----------------------------------------------------------------------");
		
		ArrayList<Operation> listOfOperations = new ArrayList<Operation>();
		
		//
		// go through component mappings and look for state changes (and verify unique mapping).
		//
		for (MolecularComponentEntry reactantComponentEntry : forwardComponentMapping.keySet()){
			MolecularComponentEntry productComponentEntry = forwardComponentMapping.get(reactantComponentEntry);
			String reactantState = reactantComponentEntry.getExplicitState();
			String productState = productComponentEntry.getExplicitState();
			if (reactantState != null || productState != null){
				if (reactantState == null || productState == null){
					throw new RuntimeException("reactant state = "+reactantState+" and product state = "+productState+", not allowed, null not allowed.");
				}
				if (!reactantState.equals(productState)){
					listOfOperations.add(new ChangeStateOperation(reactantComponentEntry, productComponentEntry, productState));
				}
			}
		}

		
		//
		// find unmatched reactant bonds and unmatched product bonds.
		// note that the two "unmatched" arrays are initialized differently.
		// we add unmatched reactant bonds to "unmatchedReactantBond"
		// we remove matched product bonds from "unmatchedProductBond"
		//
		ArrayList<BondEntry> unmatchedReactantBonds = new ArrayList<RuleAnalysis.BondEntry>();
		ArrayList<BondEntry> unmatchedProductBonds = new ArrayList<RuleAnalysis.BondEntry>(productBondEntries);
		for (BondEntry reactantBondEntry : reactantBondEntries){
			//
			// find corresponding product components
			//
			MolecularComponentEntry productComponent1 = forwardComponentMapping.get(reactantBondEntry.component1);
			MolecularComponentEntry productComponent2 = forwardComponentMapping.get(reactantBondEntry.component2);
			
			if (productComponent1 == null || productComponent2 == null || !productComponent1.isBoundTo(productComponent2)){
				// match not found
				unmatchedReactantBonds.add(reactantBondEntry);
			}else{
				// match found
				BondEntry matchingProductBondEntry = null;
				for (BondEntry productBondEntry : productBondEntries){
					if ((productBondEntry.component1 == productComponent1 && productBondEntry.component2 == productComponent2) ||
						(productBondEntry.component1 == productComponent2 && productBondEntry.component2 == productComponent1)){
						matchingProductBondEntry = productBondEntry;
						break;
					}
				}
				if (matchingProductBondEntry != null){
					unmatchedProductBonds.remove(matchingProductBondEntry);
				}
			}
		}
		
		//
		// add AddBond operations
		//
		for (BondEntry addedBond : unmatchedProductBonds){
			listOfOperations.add(new AddBondOperation(addedBond));
		}
		
		//
		// add AddMolecularType (unmatched product molecule)
		//
		for (MolecularTypeEntry unmatchedProductMoleculeEntry : unmatchedProductMolecularTypeEntries){
			listOfOperations.add(new AddMolecularTypeOperation(unmatchedProductMoleculeEntry));
		}
		
		//
		// delete entire reactant pattern if all molecules in it are unmapped
		//
		for (ParticipantEntry reactantEntry : reactantEntries){
			boolean bAllMoleculesRemoved = true;
			for (MolecularTypeEntry moleculesEntry : reactantEntry.getMolecularTypeEntries()){
				if (forwardMolecularMapping.get(moleculesEntry) != null){
					bAllMoleculesRemoved = false;
				}
			}
			if (bAllMoleculesRemoved){
				//
				// remove species pattern (and disregard all unmatched bonds and molecules belonging to that species pattern)
				//
				//
				listOfOperations.add(new DeleteParticipantOperation(reactantEntry));
				for (MolecularTypeEntry moleculesEntry : reactantEntry.getMolecularTypeEntries()){
					unmatchedReactantMolecularTypeEntries.remove(moleculesEntry);
					for (BondEntry reactantBond : reactantBondEntries){
						if ((reactantBond.component1.getMolecularTypeEntry().getParticipantEntry() == reactantEntry) &&
							(reactantBond.component2.getMolecularTypeEntry().getParticipantEntry() == reactantEntry)){
							unmatchedReactantBonds.remove(reactantBond);
						}
					}
				}
			}
		}
		
		//
		// add RemoveBond operations
		//
		for (BondEntry removedBond : unmatchedReactantBonds){
			listOfOperations.add(new DeleteBondOperation(removedBond));
		}
		
		//
		// delete molecule
		//
		for (MolecularTypeEntry unmatchedMolecularTypeEntry : unmatchedReactantMolecularTypeEntries){
			listOfOperations.add(new DeleteMolecularTypeOperation(unmatchedMolecularTypeEntry));
		}
	}

	public String getID(MolecularTypeEntry mte){
		int molecularTypeIndex = mte.getMoleculeIndex();
		return getID(mte.getParticipantEntry())+"_"+"M"+(molecularTypeIndex+INDEX_OFFSET);
	}
	
	public String getID(MolecularComponentEntry mce){
		int componentIndex = mce.getComponentIndex();
		return getID(mce.getMolecularTypeEntry())+"_"+"M"+(componentIndex+INDEX_OFFSET);
	}
	
	public String getID(ParticipantEntry participantEntry){
		String participantId = null;
		int ruleIndex = participantEntry.getRuleIndex();
		int participantIndex = participantEntry.getRuleIndex();
		if (participantEntry.getParticipantType() == ParticipantType.Reactant){
			participantId = "RR"+(ruleIndex+INDEX_OFFSET)+"_RP"+(participantIndex+INDEX_OFFSET);
		}else{
			participantId = "RR"+(ruleIndex+INDEX_OFFSET)+"_PP"+(participantIndex+INDEX_OFFSET);
		}
		return participantId;
	}	
}
