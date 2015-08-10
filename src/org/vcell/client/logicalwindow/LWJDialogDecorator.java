package org.vcell.client.logicalwindow;

import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.uchc.connjur.wb.ExecutionTrace;

public class LWJDialogDecorator implements LWHandle {
	private static final Map<JDialog, LWJDialogDecorator>  decorators = new HashMap<>( );
	private static final Logger LG = Logger.getLogger(LWJDialogDecorator.class);
	
	private final JDialog jdialog;

	private LWJDialogDecorator(JDialog jdialog) {
		super();
		this.jdialog = jdialog;
		LWContainerHandle lwch = LWNamespace.findLWOwner( jdialog.getOwner() );
		if (lwch != null) {
			lwch.manage(this);
		}
	}
	
	/**
	 * @param jdialog not null
	 * @return new or existing decorator
	 */
	public static LWJDialogDecorator decoratorFor(JDialog jdialog) {
		LWJDialogDecorator deco = decorators.get(jdialog);
		if (deco != null) {
			return deco.modalityNormalized();
		}
		deco = new LWJDialogDecorator(jdialog);
		jdialog.addWindowListener(closeListener);
		decorators.put(jdialog, deco);
		return deco.modalityNormalized();
	}

	@Override
	public LWModality getLWModality() {
		return LWNamespace.getEquivalentModality(jdialog);
	}

	@Override
	public Window getWindow() {
		return jdialog; 
	}

	@Override
	public Iterator<LWHandle> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public LWContainerHandle getlwParent() {
		return LWNamespace.findLWOwner(jdialog);
	}

	/**
	 * no-op, dialogs don't iconify, I think
	 */
	@Override
	public void unIconify() {
		//no-op, 

	}

	@Override
	public JMenuItem menuItem(int level) {
		return LWMenuItemFactory.menuFor(level, this);
	}

	@Override
	public void closeRecursively() {
		jdialog.dispose();
	}

	@Override
	public String menuDescription() {
		return jdialog.getTitle(); 
	}
	
	/**
	 * remove application / toolkit modality
	 * @return this (for chaining)
	 */
	private LWJDialogDecorator modalityNormalized( ) {
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
		return this;
	}
	
	/**
	 * remove closed {@link JDialog}s from hash
	 */
	private static final WindowListener closeListener = new WindowAdapter() {

		@Override
		public void windowClosed(WindowEvent e) {
			decorators.remove(e.getSource());
		}
		
	};
}
