package org.vcell.sybil.util.ui;

/*   UIPortSpace  --- by Oliver Ruebenacker, UCHC --- June 2008 to October 2009
 *   An interface for a UI component for editing options for a port
 */

import org.vcell.sybil.models.views.SBWorkView;

public interface UIPortSpace extends UIComponent {
	
	public void update(SBWorkView view);
	
}
