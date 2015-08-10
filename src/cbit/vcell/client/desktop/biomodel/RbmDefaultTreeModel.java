package cbit.vcell.client.desktop.biomodel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.SpeciesPattern;

import cbit.vcell.model.ReactionRuleParticipant;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;

public class RbmDefaultTreeModel extends DefaultTreeModel {

	public RbmDefaultTreeModel(TreeNode root) {
		super(root);
	}
	public RbmDefaultTreeModel(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}
	
	static class ReactionRuleParticipantLocal {
		ReactionRuleParticipantType type;
		ReactionRuleParticipant speciesPattern;
		int index;
		ReactionRuleParticipantLocal(ReactionRuleParticipantType type, ReactionRuleParticipant sp, int index) {
			super();
			this.type = type;			// reactant or product
			this.speciesPattern = sp;
			this.index = index;			// ex Reactant 1, Reactant 2...
		}
	}
	static class SpeciesPatternLocal {
		SpeciesPattern speciesPattern;
		int index;
		SpeciesPatternLocal(SpeciesPattern sp, int index) {
			super();
			this.speciesPattern = sp;
			this.index = index;			// ex SpeciesPattern 1, SpeciesPattern 2...
		}
	}
	static class BondLocal {
		private MolecularComponentPattern mcp;
		BondLocal(MolecularComponentPattern mcp) {
			this.mcp = mcp;
		}
		public MolecularComponentPattern getMolecularComponentPattern() {
			return mcp;
		}
	}
	static class StateLocal {
		private MolecularComponentPattern mcp;
		StateLocal(MolecularComponentPattern mcp) {
			this.mcp = mcp;
		}
		public MolecularComponentPattern getMolecularComponentPattern() {
			return mcp;
		}
	}
	static class ParticipantMatchLabelLocal {
		private String participantMatchLabel;
		ParticipantMatchLabelLocal(String participantMatchLabel) {
			this.participantMatchLabel = participantMatchLabel;
		}
		public String getParticipantMatchLabel() {
			return participantMatchLabel;
		}
	}

}
