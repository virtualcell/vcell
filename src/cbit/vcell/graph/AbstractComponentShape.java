package cbit.vcell.graph;

import java.awt.Color;

import cbit.vcell.client.desktop.biomodel.IssueManager;

public class AbstractComponentShape {

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
