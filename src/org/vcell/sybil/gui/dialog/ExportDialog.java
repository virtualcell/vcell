package org.vcell.sybil.gui.dialog;

/*   Export Dialog  --- by Oliver Ruebenacker, UCHC --- September 2008 to March 2010
 *   A dialog for exporting a BioPAX file to SBPAX or SBML
 */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.util.gui.ToolBar;
import org.vcell.sybil.models.io.Exporter;
import org.vcell.sybil.models.views.SBView.ExporterList;
import org.vcell.sybil.util.exception.CatchUtil;
//import org.vcell.sybil.util.hacks.JavaHacks;
//import org.vcell.sybil.util.hacks.JavaHacks.JavaHackException;
import org.vcell.sybil.workers.port.PortWorker;


public class ExportDialog extends JDialog {

	public static class Acceptor implements PortWorker.ResultAcceptor {
		protected JComponent parent = null;
		public Acceptor(JComponent parent) { this.parent = parent; }
		
		public void accept(PortWorker.Result result) {
			ExporterList exporterList = result.view().exporterList();
			ExportDialog.create(parent, exporterList.exporters(), 
					exporterList.exporterDefault()).showExportDialog(); 
		}
		
		public boolean isDeployed() { return true; }
	}
	
	private static final long serialVersionUID = 3370193373388109286L;	
	protected JPanel panel = new JPanel();
	protected JFileChooser fileChooser = new JFileChooser();
	protected ExporterPanel exporterPanel;
	
	public ExportDialog(List<Exporter> exporters, Exporter exporterSelected) { 
		super(); 
		init(exporters, exporterSelected); 
	}
	
	public ExportDialog(JFrame frameNew, List<Exporter> exporters, Exporter exporterSelected) { 
		super(frameNew, "Save", true); 
		init(exporters, exporterSelected); 
	}
	
	public ExportDialog(JDialog dialogNew, List<Exporter> exporters, Exporter exporterSelected) { 
		super(dialogNew, "Save", true); 
		init(exporters, exporterSelected); 
	}

	protected void init(List<Exporter> exporters, Exporter exporterSelected) {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { finished(); }
		});
		fileChooser.setControlButtonsAreShown(false);
		exporterPanel = new ExporterPanel(exporters, exporterSelected);
		add(exporterPanel, BorderLayout.NORTH);
		add(fileChooser);
		add(new ExportToolBar(this), BorderLayout.SOUTH);
		pack();
	}

	public static ExportDialog 
	create(JComponent parent, List<Exporter> exporters, Exporter exporterSelected) {
		Container topLevelAncestor = parent.getTopLevelAncestor();
		return topLevelAncestor instanceof JFrame ?
				new ExportDialog((JFrame) topLevelAncestor, exporters, exporterSelected) :
					topLevelAncestor instanceof JDialog ?
							new ExportDialog((JDialog) topLevelAncestor, exporters, exporterSelected) :
								new ExportDialog(exporters, exporterSelected);		
	}

	public void showExportDialog() {
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	public void finished() { dispose(); }
	public JFileChooser fileChooser() { return fileChooser; }
	public Exporter exporter() { return exporterPanel.exporterSelected(); }
	
	static protected class ExporterPanel extends JPanel {

		private static final long serialVersionUID = -8681141556907652708L;
		protected Map<ButtonModel, Exporter> buttonToExporter = new HashMap<ButtonModel, Exporter>();
		protected Map<Exporter, ButtonModel> exporterToButton = new HashMap<Exporter, ButtonModel>();
	
		protected ButtonGroup buttonGroup = new ButtonGroup();
 		
		public ExporterPanel(List<Exporter> exporters, Exporter initialSelection) {
			add(new JLabel("Format: "));
			for(Exporter exporter : exporters) {
				JRadioButton button = new JRadioButton(exporter.label());
				add(button);
				ButtonModel model = button.getModel();
				buttonToExporter.put(model, exporter);
				exporterToButton.put(exporter, model);
				buttonGroup.add(button);
				if(exporter.equals(initialSelection)) { 
					buttonGroup.setSelected(model, true);
				}
			}
		}

		public Exporter exporterSelected() { return buttonToExporter.get(buttonGroup.getSelection()); }

		public void selectExporter(Exporter exporter) {
			buttonGroup.setSelected(exporterToButton.get(exporter), true);
		}
	}
	
	static protected class ExportToolBar extends ToolBar {
		private static final long serialVersionUID = -1766287628531669896L;
		
		static protected class CancelAction extends SpecAction {
			
			private static final long serialVersionUID = -9126068580382992664L;
			protected ExportDialog dialog;
			
			public CancelAction(ExportDialog dialogNew) {
				super(new ActionSpecs("Cancel", "Cancel", "Cancel Save", 
						"files/forward.gif"));
				dialog = dialogNew;
			}
			
			public void actionPerformed(ActionEvent event) { dialog.finished(); }
			
		}

		static protected class RefreshAction extends SpecAction {
			
			private static final long serialVersionUID = -9126068580382992664L;
			protected ExportDialog dialog;
			
			public RefreshAction(ExportDialog dialogNew) {
				super(new ActionSpecs("Refresh", "Refresh", "Refresh file view", 
						"files/forward.gif"));
				dialog = dialogNew;
			}
			
			public void actionPerformed(ActionEvent event) { 
				dialog.fileChooser().rescanCurrentDirectory(); 
			}
			
		}

		static protected class BrowserAction extends SpecAction {
			
			private static final long serialVersionUID = -9126068580382992664L;
			protected ExportDialog dialog;
			
			public BrowserAction(ExportDialog dialogNew) {
				super(new ActionSpecs("Show", "Show", "Show in new dialog", 
						"files/forward.gif"));
				dialog = dialogNew;
			}

			public void actionPerformed(ActionEvent event) { 
				Exporter exporter = dialog.exporter();
				if(exporter != null) {
					SimpleLargeTextDialog textDialog;
					try {
						textDialog = new SimpleLargeTextDialog("", exporter.convertToString(), dialog);
						textDialog.showDialog();
					} catch (Exporter.ExporterException e) {
						e.printStackTrace();
					}
				} 
			}
		}

		static protected class SaveAction extends SpecAction {
			
			private static final long serialVersionUID = -9126068580382992664L;
			protected ExportDialog dialog;
			
			public SaveAction(ExportDialog dialogNew) {
				super(new ActionSpecs("Save", "Save", "Save stage", 
						"files/forward.gif"));
				dialog = dialogNew;
			}
			
			public void actionPerformed(ActionEvent event) {
//				try { JavaHacks.updateJFileChooserFromTextFiled(dialog.fileChooser()); } 
//				catch (JavaHackException e) { CatchUtil.handle(e, CatchUtil.JustPrint); }
				dialog.fileChooser().approveSelection();
				File file = dialog.fileChooser().getSelectedFile();
				if(file != null) {
					Exporter exporter = dialog.exporter();
					if(exporter != null) {
						try {
							exporter.writeToFile(file);
							dialog.finished();
						} catch (Exporter.ExporterException e) {
							CatchUtil.handle(e);
						}						
					}
				}
			}
			
		}
		
		public ExportToolBar(ExportDialog panelParent) {
			add(new CancelAction(panelParent));
			add(new RefreshAction(panelParent));
			add(new BrowserAction(panelParent));
			add(new SaveAction(panelParent));
		}
		
	}

}
