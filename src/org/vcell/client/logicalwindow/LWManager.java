package org.vcell.client.logicalwindow;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * mix-in class that supports {@link LWContainerHandle} API
 */
class LWManager extends WindowAdapter{
	
	private final LWContainerHandle lwParent;
	
	/**
	 * storage of children
	 */
	private LinkedList<LWHandle> children = new LinkedList<>();
	
	public LWManager(LWContainerHandle parent, LWContainerHandle client) {
		lwParent = parent;
		client.getWindow().addWindowListener(new ChildCloser());
	}
	
	public LWContainerHandle getLwParent() {
		return lwParent;
	}




	/**
	 * @return iterator to visible windows
	 */
	public Iterator<LWHandle> visible( ) {
		return children.stream( ).filter( hw -> hw.getWindow().isVisible() ).iterator();
	}
	
	/**
	 * Position window. Add to children. Add listener to remove when backing window is disposed of.
	 * @param lwParent 
	 * @param child 
	 */
	public void manage(LWContainerHandle parent,LWHandle child) {
		Window childW = child.getWindow();
		children.add(child);
		childW.addWindowListener(this);
		childW.addComponentListener( new WindowPositioner(parent.getWindow(), childW));
	}
	
	/**
	 * see {@link LWHandle#closeRecursively()}
	 */
	public void closeRecursively( ) {
		for ( LWHandle c : children) {
			c.closeRecursively();
			c.getWindow().dispose( );
		}
	}

	/**
	 * remove from children
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		Window w = e.getWindow();
		children.removeIf( hw -> hw.getWindow() == w);
	}
	
	/**
	 * calls {@link LWManager#closeRecursively()}
	 */
	private class ChildCloser extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			closeRecursively();
		}
		
	}
	
	/**
	 * position window once it is set visible; setting Location
	 * prior to being made visible on Mac OS X did not work correctly
	 */
	private class WindowPositioner extends ComponentAdapter {
		final Container parent;
		final Window child;

		WindowPositioner(Container parent, Window child) {
			this.parent = parent;
			this.child = child;
		}

		public void componentShown(ComponentEvent e) {
			LWContainerHandle.stagger(parent, child);
			child.removeComponentListener(this);
		}
	}
	
}
