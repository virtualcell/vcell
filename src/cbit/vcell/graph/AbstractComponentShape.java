package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Point;

import org.vcell.model.rbm.MolecularComponentPattern;

import cbit.vcell.client.desktop.biomodel.IssueManager;

public class AbstractComponentShape {

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

	final Color componentGreen = new Color(0xccffcc);
	final Color componentYellow = new Color(0xffff99);
	final Color componentBad = new Color(0xffb2b2);
	final Color componentHidden = new Color(0xe7e7e7);
//	final Color componentHidden = new Color(0xffffff);

	final Color plusSignGreen = new Color(0x37874f);
//	final Color plusSignGreen = new Color(0x006b1f);
	
	// used to colorize components with issues
	static protected IssueManager issueManager;
	public final static void setIssueManager(IssueManager newValue) {
		issueManager = newValue;
	}
	public final static IssueManager getIssueManager() {
		return issueManager;
	}
}
