package org.vcell.sybil.gui.bpimport;

/*   ImportPanel  --- by Oliver Ruebenacker, UCHC --- March 2009 to March 2010
 *   Panel for data import
 */

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.gui.ButtonFormatter;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.search.PCKeywordRequest;

public class ImportPanel extends JPanel implements RequestPanel.Container, ImportManager.Searcher {
	
	private static final long serialVersionUID = -6031284954844640137L;
	protected ImportManager importManager;
	protected boolean tabPanelAdded = false;
	protected int requestCount = 0;
	protected long timeLastRequest = 0;
	protected long minTimeBetweenRequests = 500;
	protected JLabel emptyLabel = new JLabel("Enter a keyword above and click the search button");
	protected JTabbedPane tabPanel = new JTabbedPane();
	protected JTextField searchTextField = new JTextField();
	
	public ImportPanel(FileManager fileManNew) {
		importManager = new ImportManager(this, fileManNew); 
		setLayout(new BorderLayout());
		JToolBar entryBar = new JToolBar();
		entryBar.setOrientation(JToolBar.HORIZONTAL);
		entryBar.add(new JLabel("Enter keyword: "));
		SearchAction searchAction = new SearchAction(this);
		searchTextField.addActionListener(searchAction);
		searchTextField.setMinimumSize(new Dimension(200, 20));
		searchTextField.setPreferredSize(new Dimension(300, 30));
		entryBar.add(searchTextField);
		JButton buttonSearch = new JButton(searchAction);
		ButtonFormatter.format(buttonSearch);
		entryBar.add(buttonSearch);
		add(entryBar, "North");
		searchTextField.requestFocusInWindow();
		emptyLabel.setHorizontalAlignment(JLabel.CENTER);
		add(emptyLabel);
	}
	
	public FileManager fileManager() { return importManager.fileManager(); }
	public void focusOnInput() { searchTextField.requestFocus(); }
	public String searchText() { return searchTextField.getText(); }
	public void removePanel(RequestPanel panel) { tabPanel.remove(panel); }
		
	public void performSearch(PathwayCommonsRequest request, boolean requestSmelting) { 
		if(System.currentTimeMillis() - timeLastRequest > minTimeBetweenRequests) {
			++requestCount;
			RequestPanel requestPanel = new RequestPanel(this, importManager, request);
			tabPanel.addTab(requestPanel.request().shortTitle(), requestPanel);
			tabPanel.setSelectedComponent(requestPanel);
			// wei's code
			// adding tool tips
			int idx = tabPanel.getTabCount()-1;
			tabPanel.setToolTipTextAt(idx, requestPanel.request().description());
			//done
			if(!tabPanelAdded) { 
				remove(emptyLabel);
				add(tabPanel); 
				tabPanelAdded = true;
			}
			timeLastRequest = System.currentTimeMillis();	
			revalidate();
		}
	}
	
	public PathwayCommonsRequest request() { return new PCKeywordRequest(searchText()); }
	
	static protected class SearchAction extends SpecAction {
		
		private static final long serialVersionUID = -9126068580382992664L;
		protected ImportPanel panel;
		
		public SearchAction(ImportPanel panelNew) {
			super(new ActionSpecs("Search", "Search", 
					"Perform keyword search using Pathway Commons "));
			panel = panelNew;
		}
		
		public void actionPerformed(ActionEvent event) { 
			panel.performSearch(panel.request(), false); 
		}

	}

}