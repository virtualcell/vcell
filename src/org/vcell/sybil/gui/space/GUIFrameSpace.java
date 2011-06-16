/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
