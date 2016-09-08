package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;

import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.SpeciesContext;

public abstract class AbstractComponentShape {
	private MolecularComponent mc;
	private MolecularComponentPattern mcp;

	class BondPair implements Comparable {
		int id;
		Point from;
		Point to;
		public BondPair(int id, Point from, Point to) {
			this.id = id;
			this.from = from;
			this.to = to;
		}
		@Override
		public int compareTo(Object o) {
			if(o instanceof BondPair) {
				BondPair that = (BondPair)o;
				int thisLength = to.x - from.x;
				int thatLength = that.to.x - that.from.x;
				if(thisLength < thatLength) {
					return -1;
				} else {
					return 1;
				}
			}
			return 0;
		}
	}
	class BondSingle {
		public BondSingle(MolecularComponentPattern mcp, Point from) {
			this.mcp = mcp;
			this.from = from;
		}
		MolecularComponentPattern mcp;
		Point from;
	}

	final static Color componentPaleYellow = new Color(0xffff99);
	public final static Color componentPaleGreen = new Color(0xeeffee);
	public final static Color componentPaleBlue = Color.getHSBColor(0.6f, 0.05f, 1.0f);	// hue, saturation, brightness
	public final static Color componentPalePink = Color.getHSBColor(0.9f, 0.05f, 1.0f);
	
	public final static Color componentGreen = new Color(0xccffcc);
	public final static Color componentYellow = new Color(0xffdf00);
	public final static Color componentBad = new Color(0xffb2b2);
	public final static Color componentHidden = new Color(0xe7e7e7);
	public final static Color componentVeryLightGray = new Color(0xd7d7d7);
//	final static Color componentHidden = new Color(0xffffff);
	public final static Color plusSignGreen = new Color(0x37874f);
//	final static Color plusSignGreen = new Color(0x006b1f);
	
	// used to colorize components with issues
	static protected IssueManager issueManager;
	public final static void setIssueManager(IssueManager newValue) {
		issueManager = newValue;
	}
	public final static IssueManager getIssueManager() {
		return issueManager;
	}
	
	protected static boolean isHidden(Displayable owner, MolecularComponentPattern mcp) {
		boolean hidden = false;
		if(owner == null || mcp == null) {
			return false;
		}
		if(owner instanceof RbmObservable) {
			hidden = true;
			if(mcp.isbVisible()) {
				hidden = false;
			}
			ComponentStatePattern csp = mcp.getComponentStatePattern();
			 if(csp != null && mcp.isFullyDefined()) {
				 if(!csp.isAny()) {
					hidden = false;
				 }
			 }
		}
		return hidden;
	}
	public static boolean hasErrorIssues(Displayable owner, MolecularComponentPattern mcp, MolecularComponent mc) {
		if(issueManager == null) {
			return false;
		}
		if(owner == null) {
			return false;
		}
		
		List<Issue> allIssueList = issueManager.getIssueList();
		for (Issue issue: allIssueList) {
			if(issue.getSeverity() != Issue.Severity.ERROR) {
				continue;
			}
			Object source = issue.getSource();
			Object detailedSource = issue.getDetailedSource();
			if(mcp != null && source == owner && detailedSource == mcp) {
				return true;
			} else if(mc != null & source == owner && detailedSource == mc) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(PointLocationInShapeContext locationContext) {
		return false;		// default behavior is that the shape doesn't contain the point
	}
	
	public abstract void paintSelf(Graphics g);
}
