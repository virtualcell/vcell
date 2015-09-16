package org.vcell.client.logicalwindow;

import java.awt.Container;
import java.awt.Insets;
import java.awt.Window;

/**
 * LWHandle which has children 
 */
public interface LWContainerHandle extends LWHandle { 
	/**
	 * add child to handles managed by this
	 * @param child not null
	 */
	public void manage(LWHandle child);
	
	/**
	 * arrange children
	 * @param w handle to arrange for, not null
	 * @return last Window positioned
	 */
//	public static Window positionChildren(LWHandle w) {
//		w.unIconify();
//		Window lastW = w.getWindow();
//		lastW.toFront();
//		for (LWHandle childHw : w ) {
//			Window child = childHw.getWindow();
//			stagger(lastW,child);
//			lastW = child;
//			lastW = positionChildren(childHw);
//		}
//		return lastW;
//	}
	
	public static void positionTopDownTo(LWHandle to) {
		LWHandle starting = to;
		LWHandle p = starting.getlwParent();
		while (p != null) {
			starting = p;
			p = starting.getlwParent();
		}
		ContainerSupport.positionChildrenTo(starting, to);
	}
	
	/**
	 * position window relative another window
	 * @param reference window to position relative to not null
	 * @param positioned window to position not null
	 */
	public static void stagger(Container reference, Window positioned) {
		Insets insets = reference.getInsets();
		int x = reference.getX() + insets.top;
		int y = reference.getY() + insets.top;
		positioned.setLocation(x,y);
	}
}
