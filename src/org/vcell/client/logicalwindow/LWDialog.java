package org.vcell.client.logicalwindow;

import java.awt.Window;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

/**
 * base class for logical dialog windows 
 *
 * implements all {@link LWHandle} methods except {@link LWHandle#menuDescription()}  
 */
@SuppressWarnings("serial")
public abstract class LWDialog extends JDialog implements LWFrameOrDialog, LWHandle {
	private final LWContainerHandle lwParent;
	
	/**
	 * see {@link JDialog#JDialog(String title)}
	 * @param parent logical owner, ideally not null but accepted 
	 * @param title to set  null's okay
	 */
	public LWDialog(LWContainerHandle parent, String title) {
		//support null for use in WindowBuilder, and during transition
		super(parent != null ? parent.getWindow() : null, title, ModalityType.DOCUMENT_MODAL);
		lwParent = parent;
		if (parent != null) {
			parent.manage(this);
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	/**
	 * see {@link JDialog#JDialog()}
	 * @param parent logical owner, not null
	 */
	public LWDialog(LWContainerHandle parent) {
		this(parent,null);
	}
	

	@Override
	public LWContainerHandle getlwParent() {
		return lwParent;
	}

	@Override
	public Window getWindow() {
		return this;
	}

	@Override
	public LWModality getLWModality() {
		return LWModality.PARENT_ONLY; 
	}

	@Override
	public Iterator<LWHandle> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public void closeRecursively() {
		
	}

	@Override
	public void unIconify() {
		
	}

	@Override
	public JMenuItem menuItem(int level) {
		return LWMenuItemFactory.menuFor(level, this);
	}
	
//	private static class AncestorModal implements ComponentListener {
//		private List<Window> disabled;
//		private final LWHandle handle;
//
//		AncestorModal(LWHandle handle) {
//			super();
//			this.handle = handle;
//			disabled = null;
//		}
//
//		@Override
//		public void componentResized(ComponentEvent e) { }
//
//		@Override
//		public void componentMoved(ComponentEvent e) { }
//
//		@Override
//		public void componentShown(ComponentEvent e) { 
//			
//			disabled = new ArrayList<Window>( );
//			 LWContainerHandle p = handle.getlwParent();
//			 while (p != null) {
//				 Window w = p.getWindow();
//				 if (w.isEnabled()) {
//					 w.setEnabled(false);
//					 disabled.add(w);
//				 }
//			 }
//		}
//
//		@Override
//		public void componentHidden(ComponentEvent e) { 
//			if (disabled != null) {
//				for (Window w: disabled) {
//					w.setEnabled(true);
//				}
//				disabled = null;
//			}
//			else {
//				System.err.println("????");
//			}
//		}
//		
//	}

}
