package org.vcell.sybil.gui.bpimport;

/*   RequestPanel  --- by Oliver Ruebenacker, UCHC --- March 2009 to January 2010
 *   Panel to display the results of one request
 */

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.gui.ButtonFormatter;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.vcell.sybil.util.http.pathwaycommons.search.PCKeywordResponse;
import org.vcell.sybil.util.http.pathwaycommons.search.PCTextModelResponse;
import org.vcell.sybil.util.http.pathwaycommons.search.PCTextResponse;
import org.vcell.sybil.workers.bpimport.ImportAcceptWorker;
import org.vcell.sybil.workers.input.PathwayCommonsWorker;

import com.hp.hpl.jena.rdf.model.Resource;

public class RequestPanel extends JPanel implements PathwayCommonsWorker.Client {
	
	public static interface Container { public void removePanel(RequestPanel panel); }
	
	private static final long serialVersionUID = -6581309802815958453L;
	protected Container container;
	protected ImportManager importManager;
	protected PathwayCommonsRequest request;
	protected JLabel busyLabel = new JLabel("Handling request, please wait");
	protected JToolBar toolBar = new JToolBar();
	
	public RequestPanel(Container containerNew, ImportManager importManNew, 
			PathwayCommonsRequest requestNew) {
		container = containerNew;
		importManager = importManNew;
		request = requestNew;
		setLayout(new BorderLayout());
		new PathwayCommonsWorker(this).run(this);
		busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// set a waiting cursor
		add(busyLabel);
		JButton buttonRemove = new JButton(new RemoveAction(this));
		toolBar.add(buttonRemove);
		ButtonFormatter.format(buttonRemove);
		add(toolBar, "South"); // wei's code
	}
	
	public FileManager fileMan() { return importManager.fileManager(); }
	public PathwayCommonsRequest request() { return request; }
	public void removeThis() { container.removePanel(this); }
	
	public void setResponse(PathwayCommonsResponse response) { 
		ResponsePanel responsePanel;
		if(response instanceof PCTextModelResponse) {
			toolBar.add(new JLabel("         "));// wei's code
		}
		if(response instanceof PCKeywordResponse) {
			responsePanel = new KeywordResponsePanel(importManager, (PCKeywordResponse) response);
		} else if(response instanceof PCTextModelResponse) {
			TextModelResponsePanel textModelResponsePanel = new TextModelResponsePanel((PCTextModelResponse) response);
			responsePanel = textModelResponsePanel;
			JButton buttonAccept = new JButton(new ImportAction(importManager, textModelResponsePanel));
			ButtonFormatter.format(buttonAccept);
			toolBar.add(buttonAccept);
		} else if(response instanceof PCTextResponse) {
			responsePanel = new TextResponsePanel((PCTextResponse) response);
		} else {
			responsePanel = new DefaultResponsePanel(response);			
		}
		remove(busyLabel);
		add(responsePanel);
		this.setCursor(Cursor.getDefaultCursor());// change the waiting cursor back to default setting
		revalidate();
	}
	
	static protected class RemoveAction extends SpecAction {
		
		private static final long serialVersionUID = -9126068580382992664L;
		protected RequestPanel panel;
		
		public RemoveAction(RequestPanel panelNew) {
			super(new ActionSpecs("Remove These Results", "remove panel with these results", 
					"remove this panel and forget results"));
			panel = panelNew;
		}
		
		public void actionPerformed(ActionEvent event) { panel.removeThis(); }

	}

	static protected class ImportAction extends SpecAction implements ImportAcceptWorker.Client {
		
		private static final long serialVersionUID = -9126068580382992664L;
		
		protected ImportManager importManager;
		protected TextModelResponsePanel panel;
		
		public ImportAction(ImportManager importManNew, TextModelResponsePanel panelNew) {
			super(new ActionSpecs("Import Selected", "Import selected entities", 
					"Import selected entities"));
			importManager = importManNew;
			panel = panelNew;
		}
		
		public void actionPerformed(ActionEvent event) { 
			//wei's code
			// Import Selected verification:
			// give a warning message if user did not select any entity before clicking "Import" button
			if(selectedResources().isEmpty()){
				JOptionPane.showMessageDialog(new JFrame(), "Check a box to select an entity please!!!", 
						"Warning", JOptionPane.WARNING_MESSAGE); 
			}
			else{
				panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// add a waiting cursor
				new ImportAcceptWorker(this).run(panel);
			}
			// done
		}
		public FileManager fileManager() { return importManager.fileManager(); }
		public PCTextModelResponse response() { return panel.response(); }
		public Set<Resource> selectedResources() { 
			return panel.selectedEntities(); 			
		}

		public boolean requestsSmelting() { return panel.requestsSmelting(); }

	}

}