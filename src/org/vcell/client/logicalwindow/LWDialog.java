package org.vcell.client.logicalwindow;

import java.awt.Window;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.client.logicalwindow.LWTraits.InitialPosition;

import edu.uchc.connjur.wb.ExecutionTrace;

/**
 * base class for logical dialog windows 
 *
 * defaults to {@link LWTraits.InitialPosition#CENTERED_ON_PARENT}
 * 
 * implements all {@link LWHandle} methods 
 */
@SuppressWarnings("serial")
public abstract class LWDialog extends JDialog implements LWFrameOrDialog, LWHandle {
	private static final Logger LG = Logger.getLogger(LWDialog.class);
	private final LWContainerHandle lwParent;
	protected LWTraits traits;
	
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
		traits = new LWTraits(InitialPosition.CENTERED_ON_PARENT);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	@Override
	public LWTraits getTraits() {
		return traits;
	}

	public void setTraits(LWTraits traits) {
		this.traits = traits;
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

	@Override
	public Window self() {
		return this;
	}

	@Override
	public void setModal(boolean modal) {
		super.setModal(modal);
		LWDialog.normalizeModality(this);
	}

	@Override
	public void setModalityType(ModalityType type) {
		super.setModalityType(type);
		LWDialog.normalizeModality(this);
	}

	/**
	 * remove application / toolkit modality
	 */
	public static void normalizeModality(JDialog jdialog ) {
		switch (jdialog.getModalityType()) {
		case MODELESS:
			if (LG.isEnabledFor(Level.WARN)) {
				//we want our modeless windows to be LWChildWindows, not Dialogs
				LG.warn(ExecutionTrace.justClassName(jdialog) + ' ' + jdialog.getTitle() + " invalid modeless dialog");
			}
			break;
		case DOCUMENT_MODAL:
			//this is what we want
			break;
		case APPLICATION_MODAL:
		case TOOLKIT_MODAL: 
			//fix
			jdialog.setModalityType(ModalityType.DOCUMENT_MODAL);
			break;
		}
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
