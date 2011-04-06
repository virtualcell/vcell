package org.vcell.sybil.gui.space;

/*   GUIBase  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Provides an environment in which the GUI lives. Might be potentially instantiated
 *   more than once.
 */

import java.awt.Component;
import java.awt.Container;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.PlainDocument;

import org.vcell.sybil.actions.ActionMap;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.gui.bpimport.ImportPanel;
import org.vcell.sybil.gui.dialog.InfoDialog;
import org.vcell.sybil.gui.dialog.SystemMonitorDialog;
import org.vcell.sybil.gui.dialog.SyBiLInfoDialog;
import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.gui.graph.GraphEditorPanel;
import org.vcell.sybil.gui.graph.Shape;
import org.vcell.sybil.gui.port.PortPanel;
import org.vcell.sybil.util.ui.UIComponent;
import org.vcell.sybil.util.ui.UserInterface;
import org.vcell.sybil.util.ui.UserInterfaceGraph;

public class GUIBase extends UserInterface implements UserInterfaceGraph<Shape, Graph> {

	protected CoreManager coreManager;
	protected ActionMap actionMap;
	protected GUIFrameSpace frameSpace;
	
	public GUIBase(GUIFrameSpace frameSpaceNew, CoreManager modSysNew, ActionMap actionMapNew) { 
		frameSpace = frameSpaceNew;
		coreManager = modSysNew;
		actionMap = actionMapNew; 
	}
	
	@Override
	public GUIFrameSpace frameSpace() { return frameSpace; }

	public Container frame() { return frameSpace().component(); }

	@Override
	public GUITabbedSpace<JTabbedPane> createTabbedSpace() { 
		return new GUITabbedSpace<JTabbedPane>(new JTabbedPane()); 
	}

	@Override
	@SuppressWarnings("unchecked")
	public GUIScrollSpace<JScrollPane> createScrollSpace(UIComponent comp) { 
		return new GUIScrollSpace<JScrollPane>(new JScrollPane(((GUIComponent<JScrollPane>)comp).component())); 
	}

	@Override
	public GUIImportSpace<ImportPanel> createImportSpace() { 
		return new GUIImportSpace<ImportPanel>(new ImportPanel(coreManager.fileManager())); 
	}

	@Override
	public GUITextSpace<JTextArea> createTextSpace() { 
		return new GUITextSpace<JTextArea>(new JTextArea(new PlainDocument())); 
	}

	public GUIGraphSpace<GraphEditorPanel> createGraphSpace() { 
		return new GUIGraphSpace<GraphEditorPanel>(new GraphEditorPanel(coreManager.fileManager().box(), actionMap)); 
	}

	@Override
	public GUIFileChooserSpace<JFileChooser> createFileChooserSpace() { 
		return new GUIFileChooserSpace<JFileChooser>(frameSpace.getDialogParentProvider(), 
				new JFileChooser());
	}

	@Override
	public GUIPortSpace<PortPanel> createPortSpace() {
		return new GUIPortSpace<PortPanel>(new PortPanel(coreManager.fileManager()));
	}

	@Override
	public GUIInfoDialogSpace<SyBiLInfoDialog> createInfoDialogSpace() {
		Component topFrame = frameSpace().getDialogParentProvider().getDialogParent();
		InfoDialog.Factory<SyBiLInfoDialog> factory = new SyBiLInfoDialog.Factory();
		return new GUIInfoDialogSpace<SyBiLInfoDialog>(factory.create(topFrame));
	}

	@Override
	public GUIInfoDialogSpace<SystemMonitorDialog> createSystemMonitorDialogSpace() {
		Component topFrame = frameSpace().getDialogParentProvider().getDialogParent();
		InfoDialog.Factory<SystemMonitorDialog> factory = new SystemMonitorDialog.Factory();
		return new GUIInfoDialogSpace<SystemMonitorDialog>(factory.create(topFrame));
	}

	@Override
	public void stopSyBiL() {
		if(frame() instanceof JFrame) { System.exit(0); }
	}

}
