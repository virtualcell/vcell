package org.vcell.model.rbm;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.graph.GraphConstants;

import org.vcell.util.Displayable;

public class SpeciesPattern extends RbmElementAbstract implements Matchable, IssueSource, Displayable {
	public static final String PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS = "molecularTypePatterns";
	private List<MolecularTypePattern> molecularTypePatterns = new ArrayList<MolecularTypePattern>();
	
	public static class Bond implements Serializable {
		public MolecularTypePattern molecularTypePattern;
		public MolecularComponentPattern molecularComponentPattern;
		
		private Bond(MolecularTypePattern molecularTypePattern,		// keep this constructor private!!!
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
			if (bp.molecularTypePattern != molecularTypePattern) {
				return false;
			}
			if (bp.molecularComponentPattern.getMolecularComponent() != molecularComponentPattern.getMolecularComponent()) {
				return false;
			}
			return true;
		}
		@Override
		public int hashCode(){
			return molecularTypePattern.hashCode() + molecularComponentPattern.getMolecularComponent().hashCode();
		}
		@Override
		public String toString() {
			 return molecularTypePattern.getMolecularType().getName() 
				+ "(" + molecularTypePattern.getIndex() + ")(" 
				+ molecularComponentPattern.getMolecularComponent().getName() + "(" + molecularComponentPattern.getMolecularComponent().getIndex()+")" + ")";
		}
		public String toHtmlStringShort() {
			int index = molecularTypePattern.getIndex();
			String name = molecularTypePattern.getMolecularType().getName();
			name += "<sub>" + index + "</sub>"; 
			name += "(" + molecularComponentPattern.getMolecularComponent().getName() + ")";
			return "<html><b>" + name + "</b></html>";
		}
		// we only show the component we want to bond with
		public String toHtmlStringLong(MolecularTypePattern mtpFrom, MolecularComponent mcFrom, SpeciesPattern sp, int colorIndex) 
		{
			String strBondType = molecularTypePattern.getMolecularType().getName() + "(" + molecularTypePattern.getIndex() + ")";
			String strBondComponent = molecularComponentPattern.getMolecularComponent().getName() + "(" + molecularComponentPattern.getMolecularComponent().getIndex() + ")";

			String sfrom = mtpFrom.getMolecularType().getName() + mtpFrom.getIndex() + mcFrom.getName();
			String sto = molecularTypePattern.getMolecularType().getName() + molecularTypePattern.getIndex() + molecularComponentPattern.getMolecularComponent().getName();
			System.out.println("Linking " + sfrom + " to " + sto);
			
//			String str = mtpFrom.getIndex() + "(" + mcFrom.getName() + ")";
//			str += " to " +  molecularTypePattern.getIndex() + "(" + molecularComponentPattern.getMolecularComponent().getName() + ")";
//			return "<html>Bond from " + str + "</html>";
			
			String str = toString();
			return "<html>" + str + "</html>";
			
//			Color c = RbmTreeCellRenderer.bondHtmlColors[colorIndex];
//			String colorTextStart = "<font color=" + "\"rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")\">";
//			String colorTextEnd = "</font>";
//			String str = "";
//			for(int i = 0; i < sp.getMolecularTypePatterns().size(); i++) {
//				if(i != 0) str += ".";
//				MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(i);
//				if(mtp.equals(molecularTypePattern)) {		// our component is in this molecular type pattern
//					str += "<b>" + mtp.getMolecularType().getName() + "</b>(";
//					for(int j = 0; j < mtp.getMolecularType().getComponentList().size(); j++) {
//						MolecularComponent mc = mtp.getMolecularType().getComponentList().get(j);
//						if(mc.equals(molecularComponentPattern.getMolecularComponent())) {
//							str += colorTextStart + "<b>" + mc.getName() + "</b>" + colorTextEnd;
//							break;		// found it, don't need to look at other components
//						}
//					}
//					str += ")";
//				} else {
//					str += mtp.getMolecularType().getName() + "()";
//				}
//			}
//			return "<html>Bond to " + str + "</html>";
		}

		// we only show the component we want to bond with
		public String toHtmlStringLong(SpeciesPattern sp, int colorIndex) 
		{
			String strBondType = molecularTypePattern.getMolecularType().getName() + "(" + molecularTypePattern.getIndex() + ")";
			String strBondComponent = molecularComponentPattern.getMolecularComponent().getName() + "(" + molecularComponentPattern.getMolecularComponent().getIndex() + ")";
			System.out.println("Linking to " + strBondType + strBondComponent);
			
			Color c = GraphConstants.bondHtmlColors[colorIndex];
			String colorTextStart = "<font color=" + "\"rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")\">";
			String colorTextEnd = "</font>";
			String str = "";
			for(int i = 0; i < sp.getMolecularTypePatterns().size(); i++) {
				if(i != 0) str += ".";
				MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(i);
				if(mtp.equals(molecularTypePattern)) {		// our component is in this molecular type pattern
					str += "<b>" + mtp.getMolecularType().getName() + "</b>(";
					for(int j = 0; j < mtp.getMolecularType().getComponentList().size(); j++) {
						MolecularComponent mc = mtp.getMolecularType().getComponentList().get(j);
						if(mc.equals(molecularComponentPattern.getMolecularComponent())) {
							str += colorTextStart + "<b>" + mc.getName() + "</b>" + colorTextEnd;
							break;		// found it, don't need to look at other components
						}
					}
					str += ")";
				} else {
					str += mtp.getMolecularType().getName() + "()";
				}
			}
			return "<html>Bond to " + str + "</html>";
		}
		// here we show both component - the one we want to bond and the one we want to bond with
		public String toHtmlStringLong(SpeciesPattern spThat, MolecularTypePattern mtpThat, MolecularComponent mcThat, int colorIndex) 
		{
			// "That" is the molecular type pattern / component we clicked on
			String strThatType = mtpThat.getMolecularType().getName() + "(" + mtpThat.getIndex() + ")";
			String strThatComponent = mcThat.getName() + "(" + mcThat.getIndex() + ")";
			String strBondType = molecularTypePattern.getMolecularType().getName() + "(" + molecularTypePattern.getIndex() + ")";
			String strBondComponent = molecularComponentPattern.getMolecularComponent().getName() + "(" + molecularComponentPattern.getMolecularComponent().getIndex() + ")";
//			System.out.println("Linking " + strThatType + strThatComponent + " to " + strBondType + strBondComponent);
			
			Color c = GraphConstants.bondHtmlColors[colorIndex];
			String colorTextStart = "<font color=" + "\"rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")\">";
			String colorTextEnd = "</font>";
			String str = "";
			for(int i = 0; i < spThat.getMolecularTypePatterns().size(); i++) {
				if(i != 0) str += ".";
				// we iterate through each molecular type pattern of this species pattern  
				// "This" is the current molecular type pattern / component of the iteration			
				MolecularTypePattern mtpThis = spThat.getMolecularTypePatterns().get(i);
				String strThisType = mtpThis.getMolecularType().getName() + "(" + mtpThis.getIndex() + ")";
//				System.out.print(strThisType + " ");
				if(strThisType.equals(strThatType) || strThisType.equals(strBondType)) {
					str += "<b>" + mtpThis.getMolecularType().getName() + "</b>(";
				} else {
//					str += mtpThis.getMolecularType().getName() + "()";
					str += mtpThis.getMolecularType().getName() + "";
					continue;	// we don't look at the components of the unmatches types
				}
				boolean foundComponentMatch = false;
				for(int j = 0; j < mtpThis.getMolecularType().getComponentList().size(); j++) {
					MolecularComponent mcThis = mtpThis.getMolecularType().getComponentList().get(j);
					String strThisComponent = mcThis.getName() + "(" + mcThis.getIndex() + ")";
//					System.out.print(strThisComponent + ", ");
					if((strThisType.equals(strThatType) && strThisComponent.equals(strThatComponent)) || 
						(strThisType.equals(strBondType) && strThisComponent.equals(strBondComponent))) {
						if(foundComponentMatch) str += ",";		// both components are on this molecular type
//						str += colorTextStart + "<b>" + mcThis.getName() + "</b>" + colorTextEnd;
						str += colorTextStart + "<b>" + mcThis.getName() + "!" + colorIndex + "</b>" + colorTextEnd;
						foundComponentMatch = true;
					}
				}
				str += ")";
//				System.out.println("");
			}
			return "<html>" + str + "</html>";
		}

		public String getId() {
			System.err.println("Bond.getId() ... need to implement something good here ... not yet implemented well.");
			return "BondId_"+hashCode();
		}
	}
	
	public SpeciesPattern() {
	}
	public SpeciesPattern(SpeciesPattern thatSp) {		// copy constructor
		Map<RbmElementAbstract, RbmElementAbstract> thatThisMap = new LinkedHashMap<RbmElementAbstract, RbmElementAbstract>();
		for(MolecularTypePattern thatMtp : thatSp.getMolecularTypePatterns()) {
			MolecularTypePattern thisMtp = new MolecularTypePattern(thatMtp, thatThisMap);
			molecularTypePatterns.add(thisMtp);
			thatThisMap.put(thatMtp, thisMtp);
		}
		// now all thisMtp and thisMcp are constructed we can go through all the bonds in thatSp and reconstruct them in thisSp
		for(MolecularTypePattern thatMtp : thatSp.getMolecularTypePatterns()) {
			for(MolecularComponentPattern thatMcp : thatMtp.getComponentPatternList()) {
				MolecularComponentPattern thisMcp = (MolecularComponentPattern)thatThisMap.get(thatMcp);
				Bond thatBond = thatMcp.getBond();
				if(thatBond != null) {
					Bond thisBond = new Bond((MolecularTypePattern)thatThisMap.get(thatBond.molecularTypePattern), (MolecularComponentPattern)thatThisMap.get(thatBond.molecularComponentPattern));
					thisMcp.setBond(thisBond);
				}
			}
			// we also reproduce all the matches
			MolecularTypePattern thisMtp = (MolecularTypePattern)thatThisMap.get(thatMtp);
			thisMtp.setParticipantMatchLabel(thatMtp.getParticipantMatchLabel());
		}
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
	public final List<MolecularTypePattern> getMolecularTypePatterns(String name) {
		List<MolecularTypePattern> mtpList = new ArrayList<MolecularTypePattern>();
		for(MolecularTypePattern mtp : molecularTypePatterns) {
			if(mtp.getMolecularType().getName().equals(name)) {
				mtpList.add(mtp);
			}
		}
		return mtpList;
	}
	
	public final MolecularTypePattern getMolecularTypePattern(String mtName, int index) {
		for(MolecularTypePattern mtp : molecularTypePatterns) {
			if(mtp.getMolecularType().getName().equals(mtName) && mtp.getIndex() == index) {
				return mtp;
			}
		}
		return null;
	}

	public boolean containsMolecularType(MolecularType mt) {
		for(MolecularTypePattern mtp : molecularTypePatterns) {
			if(mtp.getMolecularType() == mt) {
				return true;
			}
		}
		return false;
	}
	public boolean hasExplicitParticipantMatch() {
		// returns true if at least one molecular type pattern has an explicit match
		for(MolecularTypePattern mtp : molecularTypePatterns) {
			if(mtp.hasExplicitParticipantMatch()) {
				return true;
			}
		}
		return false;
	}

	// call this every time when the order or number of elements in the molecularTypePatterns list might have changed
	// TODO: stop directly modifying the molecularTypePatterns list outside this class !!!
	public void recalculateIndexes() {
		for (int i = 0; i < molecularTypePatterns.size(); i++) {
			molecularTypePatterns.get(i).setIndex(i+1);		// recalculate the indexes
		}
	}
	public final void setMolecularTypePatterns(List<MolecularTypePattern> newValue) {
		List<MolecularTypePattern> oldValue = molecularTypePatterns;
		this.molecularTypePatterns = newValue;
		recalculateIndexes();
		firePropertyChange(PROPERTY_NAME_MOLECULAR_TYPE_PATTERNS, oldValue, newValue);
	}
	
	public void shiftLeft(MolecularTypePattern from) {
		int fromIndex = molecularTypePatterns.indexOf(from);
		if(fromIndex == 0) {			// already the first element
			return;
		}
		int toIndex = fromIndex-1;
		MolecularTypePattern to = molecularTypePatterns.remove(toIndex);
		molecularTypePatterns.add(fromIndex, to);
		recalculateIndexes();
	}
	public void shiftRight(MolecularTypePattern from) {
		int fromIndex = molecularTypePatterns.indexOf(from);
		if(molecularTypePatterns.size() == fromIndex+1) {		// already the last element
			return;
		}
		int toIndex = fromIndex+1;
		MolecularTypePattern to = molecularTypePatterns.remove(toIndex);
		molecularTypePatterns.add(fromIndex, to);
		recalculateIndexes();
	}
	
	@Override
	public String toString() {
		return RbmUtils.toBnglString(this, null, CompartmentMode.hide, 0);
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
	
	public void resetStates() {
		for(MolecularTypePattern mtp : molecularTypePatterns) {
			for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
				if(mcp.getMolecularComponent().getComponentStateDefinitions().isEmpty()) {
					break;		// this component can't have states, nothing to do
				}
				ComponentStatePattern csp = new ComponentStatePattern();
				mcp.setComponentStatePattern(csp);	// reset to any state
			}
		}
	}
	public void resetBonds() {
		// ATTENTION: used just for display purposes (sp small shape), the reset is incomplete (bond id for example, other flags)
		for(MolecularTypePattern mtp : molecularTypePatterns) {
			for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
				mcp.setBondType(BondType.Possible);
				mcp.setBond(null);
			}
		}
	}
	public void setBond(MolecularTypePattern mtpTo, MolecularComponentPattern mcpTo, MolecularTypePattern mtpFrom, MolecularComponentPattern mcpFrom) {
		// ATTENTION: used just for display purposes (sp small shape), the set is incomplete (bond id for example, other flags)
		mcpFrom.setBond(new Bond(mtpTo, mcpTo));
		mcpFrom.setBondType(BondType.Specified);
		mcpTo.setBond(new Bond(mtpFrom, mcpFrom));
		mcpTo.setBondType(BondType.Specified);
	}
	
	public void resolveBonds() {
		List<MolecularTypePattern> molecularTypePatterns = getMolecularTypePatterns();
		recalculateIndexes();
		
		// clear all the bonds now
		for (MolecularTypePattern mtp : molecularTypePatterns) {
			for (MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
				mcp.setBond(null);
			}
		}
		for (MolecularTypePattern mtp : molecularTypePatterns) {
			for (MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
				boolean found = false;
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
						found = true;
					}
				}
				if(!found) {
					System.out.println("Failed to match a bond for " + mcp.getMolecularComponent().getName());
					mcp.setBondType(BondType.None);
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

//	public String getId() {
//		System.err.println("SpeciesPattern id generated badly");
//		return "SpeciesPattern_"+hashCode();
//	}
	
	@Override
	public boolean compareEqual(Matchable aThat) {
		if (this == aThat) {
			return true;
		}
		if (!(aThat instanceof SpeciesPattern)) {
			return false;
		}
		SpeciesPattern that = (SpeciesPattern)aThat;
		
		if (!Compare.isEqual(molecularTypePatterns, that.molecularTypePatterns)){
			return false;
		}
		return true;
	}
	
	public String calculateSignature(List<MolecularType> molecularTypeList) {
		// for a list of molecular types ordered like this: X, Z, Y 
		// and a species pattern X().Z().Y().Z().Y().X().X()
		// the signature will be X.X.X.Z.Z.Y.Y  (sorted by the order of the molecular types)
		String signature = "";
		for(int i=0; i<molecularTypeList.size(); i++) {
			for(MolecularTypePattern mtp : molecularTypePatterns) {
				MolecularType mt = mtp.getMolecularType();
				if(i == molecularTypeList.indexOf(mt)) {	// we found a candidate
					signature += mt.getDisplayName() + ".";
				}
			}
		}
		return signature.substring(0, signature.length()-1);
	}
	// as above, but we keep the order of the molecular type patterns X.Z.Y.Z.Y.X.X
	public String getNameShort() {
		String signature = "";
		for(MolecularTypePattern mtp : molecularTypePatterns) {
			MolecularType mt = mtp.getMolecularType();
			signature += mt.getDisplayName() + ".";
		}
		return signature.substring(0, signature.length()-1);
	}

	public void initializeBonds(BondType bondType) {
		for(MolecularTypePattern mtp : getMolecularTypePatterns()) {
			for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
				mcp.setBondType(bondType);
			}
		}
	}

	public void checkSpeciesPattern(IssueContext issueContext, List<Issue> issueList) {
		for(MolecularTypePattern mtp : getMolecularTypePatterns()) {
			for(MolecularComponentPattern mcp : mtp.getComponentPatternList()) {
				if(mcp.isImplied()) {
					continue;
				}
				if (mcp.getBondType() == BondType.Specified) {
					Bond b = mcp.getBond();
					if(b == null && issueList != null) {
						String msg = "The Bonds of Species Pattern " + this.toString() + " do not match.\n";
						IssueSource parent = issueContext.getContextObject();
						issueList.add(new Issue(parent, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_WARNING));
						return;
					}
				}
			}
		}
	}
	
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		IssueSource parent = issueContext.getContextObject();
		if(molecularTypePatterns == null) {
			issueList.add(new Issue(parent, issueContext, IssueCategory.Identifiers, MolecularTypePattern.typeName + " of " + SpeciesPattern.typeName + " is null.", Issue.SEVERITY_WARNING));
		} else if (molecularTypePatterns.isEmpty()) {
			issueList.add(new Issue(parent, issueContext, IssueCategory.Identifiers, MolecularTypePattern.typeName + " of " + SpeciesPattern.typeName + " is empty.", Issue.SEVERITY_WARNING));
		} else {
			for (MolecularTypePattern entity : molecularTypePatterns) {
				entity.gatherIssues(issueContext, issueList);
			}
		}
	}

	public static final String typeName = "Species Pattern";
	@Override
	public final String getDisplayName() {
		return toString();
	}
	@Override
	public final String getDisplayType() {
		return typeName;
	}
}
