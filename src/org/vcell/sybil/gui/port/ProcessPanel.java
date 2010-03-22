package org.vcell.sybil.gui.port;

/*   ProcessPanel  --- by Oliver Ruebenacker, UCHC --- June 2008 to January 2010
 *   An extension of JPanel to select which anyputs belong to which stuff
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.gui.dialog.ExportDialog;
import org.vcell.sybil.util.gui.ToolBar;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.workers.port.StageProcessesWorker;

public class ProcessPanel extends JPanel {

	private static final long serialVersionUID = -1130536216477510597L;

	protected SBWorkView view;
	protected PortPanel parent;
	protected ProcessTable table = new ProcessTable();
	
	public ProcessPanel(SBWorkView view, PortPanel parent) {
		this.view = view;
		this.parent = parent;
		this.setLayout(new BorderLayout());
		add(new JScrollPane(table));
		add(new ProcessToolBar(this), BorderLayout.SOUTH);
	}

	public SBWorkView view() { return view; }
	
	protected static class ProcessToolBar extends ToolBar {
		
		private static final long serialVersionUID = -6154028842726732906L;

		static protected class ResetAndSaveAction extends SpecAction {
			
			private static final long serialVersionUID = -8827130429477195317L;
			protected ProcessPanel panel;
			
			public ResetAndSaveAction(ProcessPanel panelNew) {
				super(new ActionSpecs("Save", "Save", 
						"Save as SBPAX", 
						"files/forward.gif"));
				panel = panelNew;
			}
			
			public void actionPerformed(ActionEvent event) { 
				ExportDialog.Acceptor acceptor = new ExportDialog.Acceptor(panel);
				StageProcessesWorker worker = new StageProcessesWorker(panel.view(), acceptor);
				worker.run(panel);
			}

		}
		
		static protected class NextAction extends SpecAction {
			
			private static final long serialVersionUID = -8827130429477195317L;
			protected ProcessPanel panel;
			
			public NextAction(ProcessPanel panelNew) {
				super(new ActionSpecs("Proceed", "Proceed", 
						"Proceed to next panel", "files/forward.gif"));
				panel = panelNew;
			}
			
			public void actionPerformed(ActionEvent event) { 
				StageProcessesWorker worker = 
					new StageProcessesWorker(panel.view(), panel.parent);
				worker.run(panel);
			}

		}
		
		public ProcessToolBar(ProcessPanel panelParent) {
			setName("toolBarInput");
			add(new ResetAndSaveAction(panelParent));
			add(new NextAction(panelParent));
		}

	}
		
	public void setTableModel(ProcessTableModel modelNew) {
		table.setModel(modelNew);
		table.revalidate();
	}
	
}