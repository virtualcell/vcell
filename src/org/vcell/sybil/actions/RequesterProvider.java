package org.vcell.sybil.actions;

/*   RequesterProvider  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   A temporary solution to providing a component required for a VCell transaction, 
 *   while SyBiL still contains a number of listener-triggered workers. Intended
 *   to become obsolete as soon as SyBiL is adapted to VCell transactions
 */

import java.awt.Component;
import java.awt.event.ActionEvent;

public class RequesterProvider {
	
	static protected Component requester;
	public static void setRequester(Component requester) { RequesterProvider.requester = requester; }
	public static Component requester() { return requester; }

	public static Component requester(ActionEvent event) {
		Object source = event.getSource();
		return source instanceof Component ? (Component) source : requester();
	}
	
	public static Component requester(CoreManager coreManager) {
		Object frame = coreManager.frameSpace().component();
		return frame instanceof Component ? (Component) frame : requester();
	}
	
	public static Component requester(ActionEvent event, CoreManager coreManager) {
		Object source = event.getSource();
		return source instanceof Component ? (Component) source : requester(coreManager);
	}
	
}
