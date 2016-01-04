package org.vcell.client.logicalwindow;

import java.awt.HeadlessException;

/**
 * use title as {@link #menuDescription()}
 */
@SuppressWarnings("serial")
public class LWTitledChildFrame extends LWChildFrame {

	public LWTitledChildFrame(LWContainerHandle parent) throws HeadlessException {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	public LWTitledChildFrame(LWContainerHandle parent, String title) throws HeadlessException {
		super(parent, title);
	}

	@Override
	public String menuDescription() {
		return getTitle( );
	}

}
