package org.vcell.sybil.gui.space;

/*   GUIFrameSpace --- by Oliver Ruebenacker, UCHC --- June 2007 to March 2010
 *   Space for the frame
 */

import java.awt.Component;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.vcell.sybil.gui.util.Sizer;
import org.vcell.sybil.util.ui.UIFrameSpace;

public class GUIJFrameSpace extends GUIFrameSpace
implements UIFrameSpace, DialogParentProvider {

	private static final long serialVersionUID = -6889031014019259069L;
	
	protected Window topFrame;
	public GUIJFrameSpace(JFrame frameNew) { 
		super(frameNew);
		frame().getContentPane().setLayout(new java.awt.BorderLayout());
	}
	
	public JFrame frame() { return (JFrame) super.frame(); }
	
	public void prepare() { 
		frame().setSize(Sizer.initialSize());
		//frame().pack();
	}
	public JFrame topFrame() { return frame(); }
	public String title() { return frame().getTitle(); }
	public void setTitle(String titleNew) { frame().setTitle(titleNew); }
	public void setMenuBar(JMenuBar barNew) { frame().setJMenuBar(barNew); }
	public void setVisible(final boolean newVisible) { 
		frame().setVisible(newVisible);
	}

	public Component getDialogParent() {
		return topFrame;
	}
	public DialogParentProvider getDialogParentProvider() {
		return this;
	}

}
