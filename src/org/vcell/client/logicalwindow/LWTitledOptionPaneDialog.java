package org.vcell.client.logicalwindow;

import javax.swing.JOptionPane;

/**
 * uses title as menu description
 */
@SuppressWarnings("serial")
public class LWTitledOptionPaneDialog extends LWOptionPaneDialog {

	private final LWMenuSupport menuSupport;
	
	/**
	 * @param parent not null
	 * @param title not null
	 * @param optionPane not null
	 */
	public LWTitledOptionPaneDialog(LWContainerHandle parent, String title, JOptionPane optionPane) {
		super(parent, title, optionPane);
		menuSupport = new LWMenuSupport(this,title);
	}
	

	/**
	 * @param title may not be null
	 */
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		menuSupport.setTitle(title);
	}

	@Override
	public String menuDescription() {
		return menuSupport.menuDescription(); 
	}
}
