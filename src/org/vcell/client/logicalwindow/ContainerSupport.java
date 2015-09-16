package org.vcell.client.logicalwindow;

import java.awt.Window;

/**
 * private static methods for {@link LWContainerHandle}
 */
class ContainerSupport {

	/**
	 * only static methods here 
	 */
	private ContainerSupport() { }

	/**
	 * arrange children recursively
	 * @param from handle to arrange for, not null
	 * @param to handle to stop at; implies the containing tree is positioned last
	 * @return last Window positioned
	 */
	static Window positionChildrenTo(LWHandle from, LWHandle to) {
		from.unIconify();
		Window lastW = from.getWindow();
		lastW.toFront();
		if (from != to) {
			LWHandle lastToPosition = null;
			for (LWHandle childHw : from ) {
				if (!isInSubtree(childHw,to)) { //leave this one until last so target ends up on top
					lastW = ContainerSupport.staggerAndPosition(childHw, lastW, to);
				}
				else {
					lastToPosition = childHw;
				}
			}
			if (lastToPosition != null) {
				lastW = ContainerSupport.staggerAndPosition(lastToPosition, lastW, to);
			}
		}
		return lastW;
	}

	/**
	 * support {@link positionChildrenTo} 
	 * @param childHw where we are
	 * @param lastW last we set
	 * @param goal where we want to go
	 * @return last Window positioned
	 */
	private static Window staggerAndPosition(LWHandle childHw, Window lastW, LWHandle goal) {
			Window child = childHw.getWindow();
			LWContainerHandle.stagger(lastW,child);
			return positionChildrenTo(childHw,goal);
	}
	
	/**
	 * @param search
	 * @param to
	 * @return true if to == search or is in its subtree
	 */
	private static boolean isInSubtree(LWHandle search, LWHandle to) {
		if (search == to) {
			return true;
		}
		for (LWHandle childHW : search) {
			if (isInSubtree(childHW, to)) {
				return true;
			}
		}
		return false;
	}
}
