package org.vcell.model.rbm;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.vcell.model.rbm.MolecularComponentPattern.BondType;

public class SpeciesPattern extends RbmElement {
	public static final String PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS = "molecularTypePatterns";
	private List<MolecularTypePattern> molecularTypePatterns = new ArrayList<MolecularTypePattern>();
	
	public static class Bond {
		public MolecularTypePattern molecularTypePattern;
		public MolecularComponentPattern molecularComponentPattern;
		private Bond(MolecularTypePattern molecularTypePattern,
				MolecularComponentPattern molecularComponentPattern) {
			super();
			this.molecularTypePattern = molecularTypePattern;
			this.molecularComponentPattern = molecularComponentPattern;
		}
		
		public Bond() {
		}

		@Override 
		public boolean equals(Object obj) {
			if (!(obj instanceof Bond)) {
				return false;
			}
			Bond bp = (Bond)obj;
			if (bp.molecularTypePattern != bp.molecularTypePattern) {
				return false;
			}
			if (bp.molecularComponentPattern.getMolecularComponent() != molecularComponentPattern.getMolecularComponent()) {
				return false;
			}
			return true;
		}
		
		@Override
		public String toString() {
			 return molecularTypePattern.getMolecularType().getName() 
				+ "(" + molecularTypePattern.getIndex() + ")(" 
				+ molecularComponentPattern.getMolecularComponent().getName() + ")";
		}
	}
	
	public SpeciesPattern() {
	}
	
	public void addMolecularTypePattern(MolecularTypePattern molecularTypePattern) {
		List<MolecularTypePattern> newValue = new ArrayList<MolecularTypePattern>(molecularTypePatterns);
		newValue.add(molecularTypePattern);
		setMolecularTypePatterns(newValue);
	}
	
	public void removeMolecularTypePattern(MolecularTypePattern molecularTypePattern) {
		List<MolecularTypePattern> newValue = new ArrayList<MolecularTypePattern>(molecularTypePatterns);
		newValue.remove(molecularTypePattern);
		setMolecularTypePatterns(newValue);
	}

	public final List<MolecularTypePattern> getMolecularTypePatterns() {
		return molecularTypePatterns;
	}

	public final void setMolecularTypePatterns(List<MolecularTypePattern> newValue) {
		List<MolecularTypePattern> oldValue = molecularTypePatterns;
		this.molecularTypePatterns = newValue;		
		firePropertyChange(PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS, oldValue, newValue);
	}
	
	@Override
	public String toString() {
		return RbmUtils.toBnglString(this);
	}
	
	public int nextBondId() {		
		int N = 128;
		while (true) {
			BitSet bs = new BitSet(N);
			for (MolecularTypePattern mtp : molecularTypePatterns) {
				for (MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
					if (mcp.getBondType() == BondType.Specified) {
						bs.set(mcp.getBondId());
					}
				}
			}
			for (int i = 1; i < N; ++ i) {
				if (!bs.get(i)) {
					return i;
				}
			}
			N *= 2;
		}
	}
	
	public void resolveBonds() {
		// clear all the bonds now
		for (MolecularTypePattern mtp : molecularTypePatterns) {
			for (MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
				mcp.setBond(null);
			}
		}
		for (MolecularTypePattern mtp : molecularTypePatterns) {
			for (MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
				if (mcp.getBond() != null) {
					continue;
				}
				if (mcp.getBondType() != BondType.Specified) {
					continue;
				}
				for (MolecularTypePattern mtp1 : molecularTypePatterns) {
					for (MolecularComponentPattern mcp1 : mtp1.getComponentPatternList()) {
						if (mcp1.getBond() != null) {
							continue;
						}
						if (mtp.getMolecularType() == mtp1.getMolecularType() && mtp.getIndex() == mtp1.getIndex() && mcp1.getMolecularComponent() == mcp.getMolecularComponent()) {
							continue;
						}
						if (mcp1.getBondType() != BondType.Specified) {
							continue;
						}
						if (mcp1.getBondId() != mcp.getBondId()) {
							continue;
						}
						mcp.setBond(new Bond(mtp1, mcp1));
						mcp1.setBond(new Bond(mtp, mcp));
					}
				}
			}
		}
	}
	
	public List<Bond> getAllBondPartnerChoices(MolecularTypePattern sourceMtp, MolecularComponent sourceMc) {
		List<Bond> allChoices = new ArrayList<Bond>();
		for (MolecularTypePattern mtp : molecularTypePatterns) {			
			MolecularType mt = mtp.getMolecularType();
			for (MolecularComponent mc : mt.getComponentList()) {
				// existing
				if (mtp != sourceMtp || mc != sourceMc) {
					MolecularComponentPattern existingMcp = mtp.getMolecularComponentPattern(mc);
					if (existingMcp == null || existingMcp.getBondType() != BondType.Specified
							|| existingMcp.getBond().molecularTypePattern == sourceMtp
							&& existingMcp.getBond().molecularComponentPattern.getMolecularComponent() == sourceMc) {
						Bond bp = new Bond(mtp, mtp.getMolecularComponentPattern(mc));
						allChoices.add(bp);
					}
				}
			}
		}
		return allChoices;
	}
}
