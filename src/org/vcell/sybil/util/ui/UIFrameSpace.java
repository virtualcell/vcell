package org.vcell.sybil.util.ui;

/*   UIFrameSpace  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Stores information accessible to a wide range of components, such as configuration, 
 *   progress reports and system state
 */

public interface UIFrameSpace extends UISpace {
	public void prepare();
	public String title();
	public void setTitle(String title);
	public void setVisible(boolean newVisible);
	public void updateUI();
}
