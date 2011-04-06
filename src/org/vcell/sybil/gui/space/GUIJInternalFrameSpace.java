package org.vcell.sybil.gui.space;

/*   GUIJInternalFrameSpace --- by Oliver Ruebenacker, UCHC --- June 2007 to March 2010
 *   Space for a JInternalFrame
 */

import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;

import org.vcell.sybil.util.ui.UIFrameSpace;

public class GUIJInternalFrameSpace extends GUIFrameSpace implements UIFrameSpace {
	
	private DialogParentProvider dialogParentProvider = null;
	
	public GUIJInternalFrameSpace(DialogParentProvider dialogParentProvider, JInternalFrame frameNew) { 
		super(frameNew);
		frameNew.getContentPane().setLayout(new java.awt.BorderLayout());
		this.dialogParentProvider = dialogParentProvider;
	}
	
	@Override
	public JInternalFrame frame() { return (JInternalFrame) super.frame(); }
	
	public void prepare() { 
//		frame().setSize(Sizer.initialSize());
//		frame().setClosable(true);
//		frame().setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
//		frame().setIconifiable(true);
//		frame().setMaximizable(true);
//		frame().setResizable(true);
		//frame().pack();
	}
	
	public String title() { return frame().getTitle(); }
	@Override
	public void setTitle(String titleNew) { frame().setTitle(titleNew); }
	@Override
	public void setMenuBar(JMenuBar barNew) { frame().setJMenuBar(barNew); }

	@Override
	public void setVisible(boolean newVisible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DialogParentProvider getDialogParentProvider() {
		return dialogParentProvider;
	}
	
}
