package cbit.vcell.model;

import org.vcell.model.rbm.MolecularType;

public class ReactionSpeciesCopy {
		
		private SpeciesContext[] speciesContextArr;
		private ReactionStep[] reactStepArr;
		private ReactionRule[] rrArr;
		private MolecularType[] mtArr;
		private Structure fromStruct;	// the structure from where we copy
		private Structure[] structArr;	// Feature, Membrane  must always have at least one element
		
		public ReactionSpeciesCopy(SpeciesContext[] speciesContextArr, ReactionStep[] reactStepArr, 
				ReactionRule[] rrArr, MolecularType[] mtArr, 
				Structure fromStruct, Structure[] structArr) {
			this.speciesContextArr = (speciesContextArr==null || speciesContextArr.length==0 ? null : speciesContextArr);
			this.reactStepArr = (reactStepArr==null || reactStepArr.length==0 ? null : reactStepArr);
			this.rrArr = (rrArr==null || rrArr.length==0 ? null : rrArr);
			this.mtArr = (mtArr==null || mtArr.length==0 ? null : mtArr);
			this.fromStruct = fromStruct;													// can't be null
			this.structArr = (structArr==null || structArr.length==0 ? null : structArr);	// can't be null
			if(this.speciesContextArr == null && this.reactStepArr == null && this.rrArr == null && this.mtArr == null) {
				throw new IllegalArgumentException(ReactionSpeciesCopy.class.getName() + " all parameters null.");
			}
		}
		public SpeciesContext[] getSpeciesContextArr() {
			return speciesContextArr;
		}
		public ReactionStep[] getReactStepArr() {
			return reactStepArr;
		}
		public ReactionRule[] getReactionRuleArr() {
			return rrArr;
		}
		public MolecularType[] getMolecularTypeArr() {
			return mtArr;
		}
		public Structure[] getStructuresArr() {
			return structArr;
		}
		public Structure getFromStructure() {
			return fromStruct;
		}
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if(reactStepArr != null){
//				sb.append("-----Reactions-----\n");
				for (int i = 0; i < reactStepArr.length; i++) {
					sb.append(reactStepArr[i].getName()+"\n");
				}
			}
			if(reactStepArr != null && speciesContextArr != null){
				sb.append("\n");
			}
			if(speciesContextArr != null){
//				sb.append("-----SpeciesContexts-----\n");
				for (int i = 0; i < speciesContextArr.length; i++) {
					sb.append(speciesContextArr[i].getName()+"\n");
				}
			}
			if((reactStepArr != null || speciesContextArr != null) && rrArr != null) {
				sb.append("\n");
			}
			if(rrArr != null) {
//				sb.append("-----ReactionRules-----\n");
				for (int i = 0; i < rrArr.length; i++) {
					sb.append(rrArr[i].getName()+"\n");
				}
			}
			
			if((reactStepArr != null || speciesContextArr != null || rrArr != null) && mtArr != null) {
				sb.append("\n");
			}
			if(mtArr != null) {
//				sb.append("-----MolecularTypes-----\n");
				for (int i = 0; i < mtArr.length; i++) {
					sb.append(mtArr[i].getName()+"\n");
				}
			}

			if((reactStepArr != null || speciesContextArr != null || rrArr != null || mtArr != null) && structArr != null) {
				sb.append("\n");
			}
			if(structArr != null) {
//				sb.append("-----Structures-----\n");
				for (int i = 0; i < structArr.length; i++) {
					sb.append(structArr[i].getName()+"\n");
				}
			}

			System.out.println(sb.toString());
			
			return sb.toString();
		}
	}