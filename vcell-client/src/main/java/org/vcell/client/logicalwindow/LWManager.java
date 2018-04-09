package org.vcell.client.logicalwindow;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.client.logicalwindow.LWTraits.InitialPosition;

/**
 * mix-in class that supports {@link LWContainerHandle} API
 */
class LWManager extends WindowAdapter{
	
	private final LWContainerHandle lwParent;
	private static final Logger LG = LogManager.getLogger(LWManager.class);
	
	 /**
	 * storage of children
	 */
	private LinkedList<LWHandle> children = new LinkedList<>();
	
	public LWManager(LWContainerHandle parent, LWContainerHandle client) {
		lwParent = parent;
		client.getWindow().addWindowListener(new ChildManager());
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
		childW.addComponentListener( new WindowPositioner(parent.getWindow(), child));
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
	
	@Override
	public void windowClosing(WindowEvent e) {
		if (LG.isDebugEnabled()) {
			LG.debug(e.getSource() +  " closing");
		}
	}

	private class ChildManager extends WindowAdapter {

		@Override
		public void windowOpened(WindowEvent e) {
			e.getWindow().toFront();
		}

		/**
		 * calls {@link LWManager#closeRecursively()}
		 */
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
		final LWHandle hndl;

		WindowPositioner(Container parent, LWHandle child) {
			this.parent = parent;
			this.hndl = child;
		}

		public void componentShown(ComponentEvent e) {
			Window child = hndl.getWindow();
			LWTraits traits = hndl.getTraits();
			InitialPosition position = traits.getInitialPosition();
			switch (position) {
			case STAGGERED_ON_PARENT:
				LWContainerHandle.stagger(parent, child);
				break;
			case CENTERED_ON_PARENT:
				child.setLocationRelativeTo(parent);
				break;
			case NOT_LW_MANAGED:
				//leave it alone
			}
			child.removeComponentListener(this);
		}
	}
	
}
