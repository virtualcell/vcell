package org.vcell.sybil.gui.space;

/*   GUIFrameSpace --- by Oliver Ruebenacker, UCHC --- June 2007 to March 2010
 *   Space for the frame
 */

import java.awt.Container;
import javax.swing.JMenuBar;
import org.vcell.sybil.actions.RequesterProvider;
import org.vcell.sybil.util.ui.UIFrameSpace;

public abstract class GUIFrameSpace extends GUISpace<Container> implements UIFrameSpace {

	private static final long serialVersionUID = -6889031014019259069L;
	
	GUIFrameSpace(Container newFrame) { 
		super(newFrame); 
		RequesterProvider.setRequester(newFrame);
	}
	
	public Container frame() { return component(); }
	
	public void updateUI() { frame().validate(); }

	public abstract void setTitle(String titleNew);
	public abstract void setMenuBar(JMenuBar barNew);

	public abstract DialogParentProvider getDialogParentProvider();
	
	public abstract void setVisible(final boolean newVisible);

}
