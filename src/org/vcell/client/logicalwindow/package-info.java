/**
 *  Management of windows to allow greater flexibility than standard Swing / AWT functionality
 *  <p>
 *  
 *  Abstract classes {@link org.vcell.client.logicalwindow.LWDialog}, {@link org.vcell.client.logicalwindow.LWChildFrame}, {@link org.vcell.client.logicalwindow.LWOptionPaneDialog}
 *  allow developer to specifiy menu item description by implementing menuDescription( );<br>
 *  
 *  Class {@link org.vcell.client.logicalwindow.LWTitledDialog}, {@link org.vcell.client.logicalwindow.LWTitledOptionPaneDialog}
 *  use the dialog title as menu descriptions.<br>
 *  
 *  
 *  To create a dialog given an existing component, the pattern should be
 *  
 *	<i>LWContainerHandle lwParent = LWNamespace.findLWOwner(requester);<br>
 *	LWDialog d = new LWTitledDialog(lwParent,"Define New Subdomain Shape");
 *  </i>
 */
package org.vcell.client.logicalwindow;