package org.vcell.sybil.gui.port;

/*   CompartmentPanel  --- by Oliver Ruebenacker, UCHC --- August 2008 to February 2010
 *   An extension of JPanel to edit relationships between compartments
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.gui.dialog.ExportDialog;
import org.vcell.sybil.models.bpimport.table.CompartmentTableModel;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.models.views.SBView.ExporterList;
import org.vcell.sybil.util.gui.ToolBar;
import org.vcell.sybil.workers.port.StageCompartmentsWorker;

public class CompartmentPanel extends JPanel {

	private static final long serialVersionUID = -1130536216477510597L;

	protected CompartmentTable table;
	protected JScrollPane tablePane = new JScrollPane();
	protected PortPanel parent;
	protected SBWorkView view;

	public CompartmentPanel(PortPanel parent, FileManager fileMan) {
		this.parent = parent;
		view = fileMan.view();
		this.setLayout(new BorderLayout());
		add(tablePane);
		add(new CompartmentsToolBar(view, this), BorderLayout.SOUTH);
	}

	public SBWorkView view() { return view; }
	
	protected static class CompartmentsToolBar extends ToolBar {

		private static final long serialVersionUID = -6154028842726732906L;

		static protected class SavePreviousAction extends SpecAction {

			private static final long serialVersionUID = -8827130429477195317L;
			protected CompartmentPanel panel;
			protected ExportDialog dialog;

			public SavePreviousAction(CompartmentPanel panelNew) {
				super(new ActionSpecs("Reset and Save", "Reset and Save",
						"Reset input and save as SBPAX",
				"files/forward.gif"));
				panel = panelNew;
			}

			public void actionPerformed(ActionEvent event) {
				ExporterList exporterList = panel.view().exporterList();
				ExportDialog dialog = 
					ExportDialog.create(panel, exporterList.exporters(), exporterList.exporterDefault());
				dialog.showExportDialog();
			}

		}

		static protected class SaveNextAction extends SpecAction  {

			private static final long serialVersionUID = -8827130429477195317L;
			protected CompartmentPanel panel;

			public SaveNextAction(CompartmentPanel panelNew) {
				super(new ActionSpecs("Save", "Save",
						"Save as SBPAX",
				"files/forward.gif"));
				panel = panelNew;
			}

			public void actionPerformed(ActionEvent event) {
				if(panel.table != null) {
					ExportDialog.Acceptor acceptor = new ExportDialog.Acceptor(panel);
					panel.table.model().locations();
					StageCompartmentsWorker worker = new StageCompartmentsWorker(panel.view(), acceptor);
					worker.run(panel);

				}
			}

		}

		static protected class SBMLAction extends SpecAction  {

			private static final long serialVersionUID = -8827130429477195317L;
			protected CompartmentPanel panel;

			public SBMLAction(CompartmentPanel panelNew) {
				super(new ActionSpecs("Export SBML",
						"Export SBML",
						"Export SBML",
				"files/forward.gif"));
				panel = panelNew;
			}

			public void actionPerformed(ActionEvent event) {
				if(panel.table != null) {
					panel.table.model().locations();
					panel.parent.setPortTarget(PortPanel.targetSBML);
					StageCompartmentsWorker worker = 
						new StageCompartmentsWorker(panel.view(), panel.parent);
					worker.run(panel);
				}
			}

		}

		static protected class BioModelAction extends SpecAction {

			private static final long serialVersionUID = -8827130429477195317L;
			protected CompartmentPanel panel;

			public BioModelAction(CompartmentPanel panelNew) {
				super(new ActionSpecs("Process for BioModel",
						"Process and proceed",
						"Process input and proceed to populate BioModel",
				"files/forward.gif"));
				panel = panelNew;
			}

			public void actionPerformed(ActionEvent event) {
				if(panel.table != null) {
					panel.table.model().locations();
					panel.parent.setPortTarget(PortPanel.targetBioModel);
					StageCompartmentsWorker worker = 
						new StageCompartmentsWorker(panel.view(), panel.parent);
					worker.run(panel);					
				}
			}

		}
		
		public CompartmentsToolBar(SBWorkView view, CompartmentPanel panelParent) {
			setName("toolBarInput");
			add(new SavePreviousAction(panelParent));
			add(new SaveNextAction(panelParent));
			add(new SBMLAction(panelParent));
			if(view.bioModel() != null) { 
				add(new BioModelAction(panelParent)); 
			}
		}

	}

	public void setTableModel(CompartmentTableModel tableModel) {
		remove(tablePane);
		table = new CompartmentTable(tableModel);
		tablePane = new JScrollPane(table);
		add(tablePane);
		table.revalidate();
	}

}