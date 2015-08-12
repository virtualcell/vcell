package org.vcell.client.logicalwindow;

/**
 * use dialog title as menu item
 */
@SuppressWarnings("serial")
public class LWTitledDialog extends LWDialog {
	private final LWMenuSupport menuSupport;

	/**
	 * @param parent
	 * @param title may not be null
	 */
	public LWTitledDialog(LWContainerHandle parent, String title) {
		super(parent, title);
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
