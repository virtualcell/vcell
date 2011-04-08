package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.persistence.PathwayReader;
import org.vcell.sybil.util.http.pathwaycommons.search.DataSource;
import org.vcell.sybil.util.http.pathwaycommons.search.Hit;
import org.vcell.sybil.util.http.pathwaycommons.search.Organism;
import org.vcell.sybil.util.http.pathwaycommons.search.Pathway;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.text.StringUtil;
import org.vcell.sybil.util.xml.DOMUtil;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cbit.util.xml.XmlUtil;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.BioModelNode;

@SuppressWarnings("serial")
public class BioModelEditorPathwayCommonsPanel extends DocumentEditorSubPanel {

	public static class PathwayStringObject {
		private String stringObject;
		public PathwayStringObject(String so) {
			this.stringObject = so;
		}
		public final String getStringObject() {
			return stringObject;
		}
	}
	private static final String defaultBaseURL = "http://www.pathwaycommons.org/pc/webservice.do";
	public static class PathwayData {
		private PathwayModel pathwayModel;
		
		private PathwayData(PathwayModel pathwayModel) {
			super();
			this.pathwayModel = pathwayModel;
		}
		public PathwayModel getPathwayModel() {
			return pathwayModel;
		}
	}
	enum PathwayCommonsKeyword {
		cmd,
		version,
		output,
		q,
		maxHits,
		search,
		get_record_by_cpath_id,
		
		xml,
		biopax
	}
	enum PathwayCommonsVersion {
		v2("2.0"),
		v3("3.0");		
		String name;
		PathwayCommonsVersion(String n) {
			name = n;
		}
	}
	private JTextField searchTextField;
	private JButton searchButton = null;
	private JTree responseTree = null;
	private ResponseTreeModel responseTreeModel = null;
	private JButton showPathwayButton = null;
	private JButton gotoPathwayButton = null;
	private EventHandler eventHandler = new EventHandler();

	private class ResponseTreeModel extends DefaultTreeModel {
		private BioModelNode rootNode = null;
		
		ResponseTreeModel() {
			super(new BioModelNode(new PathwayStringObject("search results"), true), true);
			rootNode = (BioModelNode)root;
		}
		
		void addSearchResponse(String searchText, Element searchResponse) {
			rootNode.removeAllChildren();
			if (searchResponse == null) {
				rootNode.setUserObject(new PathwayStringObject("no pathways found for \"" + searchText + "\""));				
			} else {
				List<Element> hitElements = DOMUtil.childElements(searchResponse, "search_hit");
				int pathwayCount = 0;
				for(Element hitElement : hitElements) { 
					Hit hit = new Hit(hitElement);
					
					// pathway
					List<Pathway> pathwayList = hit.pathways();
					int numPathways = pathwayList.size();
					pathwayCount += numPathways;
					BioModelNode hitNode = new BioModelNode(hit, true);
					if (numPathways > 0) {
						for (Pathway pathway: pathwayList) {
							hitNode.add(new BioModelNode(pathway, false));
						}
						rootNode.add(hitNode);
					}
				}
				rootNode.setUserObject(new PathwayStringObject(pathwayCount + " pathways found for \"" + searchText + "\""));
			}
			nodeStructureChanged(rootNode);
			TreePath path = new TreePath(rootNode.getPath());
			responseTree.expandPath(path);
			responseTree.setSelectionPath(path);
		}
		
	}
	
	private static final String PATHWAY_QUERY_URL = "http://www.pathwaycommons.org/pc/record2.do?id=";
	private class ResponseTreeCellRenderer extends DefaultTreeCellRenderer {
		
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, 
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus){
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			String labelText = null;
			if (value instanceof BioModelNode) {
				BioModelNode bioModelNode = (BioModelNode)value;
				Object userObject = bioModelNode.getUserObject();
				if (userObject instanceof PathwayStringObject) {
					labelText = ((PathwayStringObject)userObject).getStringObject();
				} else if (userObject instanceof XRef) {
					XRef xRef = (XRef)userObject;
					labelText = xRef.db() + ":" + xRef.id();
					String url = xRef.url();
					if(StringUtil.notEmpty(url)) {
						labelText += ", " + url;
					}
				} else if (userObject instanceof Hit){
					Hit hit = (Hit) userObject;
					Iterator<String> nameIter = hit.names().iterator();
					String name = "no name";					
					if(nameIter.hasNext()) { 
						name = nameIter.next(); 
					} else {
						Iterator<String> synonymIter = hit.synonyms().iterator();
						if (synonymIter.hasNext()) { 
							name = synonymIter.next(); 
						}
					}
					StringBuilder stringBuilder = new StringBuilder();
					if (hit.entityType() != null && hit.entityType().trim().length() > 0) {
						stringBuilder.append(hit.entityType().trim()); 
					}
					if (hit.organism() != null && hit.organism().speciesName().trim().length() > 0) {
						stringBuilder.append("," + hit.organism().speciesName().trim()); 
					}
					labelText = hit.pathways().size() + " pathways for " + name + (stringBuilder.length() == 0 ? "" : "(" + stringBuilder.toString() + ")");
				} else if (userObject instanceof Pathway){
					Pathway pathway = (Pathway)userObject;
					labelText = pathway.name() + "  [" + pathway.dataSource().name() + "]";
					setToolTipText("Double-click to import pathway");
				} else if (userObject instanceof DataSource){
					DataSource dataSource = (DataSource)userObject;
					labelText = "Data source: " + dataSource.name() + " (" + dataSource.primaryId() + ")";
				} else if (userObject instanceof Organism) {
					Organism organism = (Organism)userObject;
					labelText = "Organism: (" + organism.speciesName() + ", " + organism.commonName() + ", " 
						+ organism.ncbiOrganismId() + ")";
				}
				if (labelText != null) {
					setText(labelText);
				}
			}
			return this;
		}
	};
	
	private class EventHandler implements ActionListener, TreeSelectionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == searchButton || e.getSource() == searchTextField) {
				search();
			} else if (e.getSource() == showPathwayButton) {
				showPathway();
			} else if (e.getSource() == gotoPathwayButton) {
				gotoPathway();
			}
		}

		public void valueChanged(TreeSelectionEvent e) {
			treeSelectionChanged();			
		}		
	}
	
	public BioModelEditorPathwayCommonsPanel() {
		super();
		initialize();
	}
	
	public void gotoPathway() {
		Pathway pathway = computeSelectedPathway();
		if (pathway != null) {
			String url = PATHWAY_QUERY_URL + pathway.primaryId();
			if (url != null) {
				DialogUtils.browserLauncher(BioModelEditorPathwayCommonsPanel.this, url, "failed to open " + url, false);
			}
		}
	}
	
	public void showPathway() {
		final Pathway pathway = computeSelectedPathway();
		AsynchClientTask task1 = new AsynchClientTask("Importing pathway '" + pathway.name() + "'", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Pathway pathway = computeSelectedPathway();
				URL url = new URL(defaultBaseURL + "?" 
						+ PathwayCommonsKeyword.cmd + "=" + PathwayCommonsKeyword.get_record_by_cpath_id 
						+ "&" + PathwayCommonsKeyword.version + "=" + PathwayCommonsVersion.v2.name 
						+ "&" + PathwayCommonsKeyword.q + "=" + pathway.primaryId()
						+ "&" + PathwayCommonsKeyword.output + "=" + PathwayCommonsKeyword.biopax);
				
				URLConnection connection = url.openConnection();

				PathwayReader pathwayReader = new PathwayReader();
				org.jdom.Document jdomDocument = XmlUtil.readXML(connection.getInputStream());
//				String xmlText = XmlUtil.xmlToString(jdomDocument, false);
				
//				String xmlText = StringUtil.textFromInputStream(connection.getInputStream());
//				PathwayReader pathwayReader = new PathwayReader();
//				org.jdom.Document jdomDocument = XmlUtil.stringToXML(xmlText, null);
				
//				String xmlText = StringUtil.textFromInputStream(connection.getInputStream(), "UTF-8");
//				PathwayReader pathwayReader = new PathwayReader();
//				org.jdom.Document jdomDocument = XmlUtil.stringToXML(xmlText, "UTF-8");
				
				PathwayModel pathwayModel = pathwayReader.parse(jdomDocument.getRootElement());
				pathwayModel.reconcileReferences();
				PathwayData pathwayData = new PathwayData(pathwayModel);
				hashTable.put("pathwayData", pathwayData);
				pathwayModel.refreshParentMap();
			}
		};
		
		AsynchClientTask task2 = new AsynchClientTask("showing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				PathwayData pathwayData = (PathwayData) hashTable.get("pathwayData");
				if (pathwayData != null) {
					setActiveView(new ActiveView(null, DocumentEditorTreeFolderClass.PATHWAY_NODE, null));
					setSelectedObjects(new Object[] {pathwayData});
				}
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);
	}

	public Pathway computeSelectedPathway() {
		Object object = responseTree.getLastSelectedPathComponent();
		if (object == null || !(object instanceof BioModelNode)) {
			return null;
		}
		Object userObject = ((BioModelNode)object).getUserObject();
		if (userObject instanceof Pathway) {
			return (Pathway)userObject;
		}
		return null;
	}
	public void treeSelectionChanged() {		
		boolean selected = computeSelectedPathway() != null;
		showPathwayButton.setEnabled(selected);
		gotoPathwayButton.setEnabled(selected);
	}

	public void search() {
		String text = searchTextField.getText();
		if (text == null || text.length() == 0) {
			return;
		}
		text = text.trim();
		if (text.length() == 0) {
			return;
		}
		final String searchText = text;
		AsynchClientTask task1 = new AsynchClientTask("searching", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				URL url = new URL(defaultBaseURL + "?" 
						+ PathwayCommonsKeyword.cmd + "=" + PathwayCommonsKeyword.search 
						+ "&" + PathwayCommonsKeyword.version + "=" + PathwayCommonsVersion.v2.name 
						+ "&" + PathwayCommonsKeyword.q + "=" + searchText
						+ "&" + PathwayCommonsKeyword.maxHits + "=" + 14
						+ "&" + PathwayCommonsKeyword.output + "=" + PathwayCommonsKeyword.xml);
//				System.out.println("url=" + url);
				URLConnection connection = url.openConnection();
				Document document = DOMUtil.parse(connection.getInputStream());		

//				org.jdom.Document d = XmlUtil.readXML(connection.getInputStream());
				
				Element errorElement = DOMUtil.firstChildElement(document, "error");
				if (errorElement != null) { 
//					String xml = DOMUtil.firstChildContent(document, "error");
					throw  new RuntimeException(errorElement.getTextContent()); 
				}
				Element searchResponse = DOMUtil.firstChildElement(document, "search_response");
				if (searchResponse != null) {
//					String xml = DOMUtil.firstChildContent(document, "search_response");
//					System.out.println(xml);
					hashTable.put("searchResponse", searchResponse);
				}
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("showing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Element searchResponse = (Element) hashTable.get("searchResponse");
				if (searchResponse != null) {
					responseTreeModel.addSearchResponse(searchText, searchResponse);
				}
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);
	}
	
	private void initialize() {
		searchTextField = new JTextField(10);
		searchTextField.addActionListener(eventHandler);
		searchButton = new JButton("Search");
		searchButton.addActionListener(eventHandler);
		showPathwayButton = new JButton("Import Pathway");
		showPathwayButton.addActionListener(eventHandler);
		showPathwayButton.setEnabled(false);
		gotoPathwayButton = new JButton("Open WebLink");
		gotoPathwayButton.addActionListener(eventHandler);
		gotoPathwayButton.setEnabled(false);
		responseTree = new JTree();
		responseTreeModel = new ResponseTreeModel();
		responseTree.setModel(responseTreeModel);
		ToolTipManager.sharedInstance().registerComponent(responseTree);
		
		CollapsiblePanel searchPanel = new CollapsiblePanel("Search", true);
		searchPanel.getContentPanel().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		searchPanel.getContentPanel().add(searchTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,4,0,0);
		searchPanel.getContentPanel().add(searchButton, gbc);
		
		setPreferredSize(new Dimension(475, 300));
		setLayout(new GridBagLayout());
		int gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(searchPanel, gbc);	

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(4,4,4,4);
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(responseTree), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,1);
		add(showPathwayButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,1,4,4);
		add(gotoPathwayButton, gbc);
		
		responseTree.getSelectionModel().addTreeSelectionListener(eventHandler);
		responseTree.setCellRenderer(new ResponseTreeCellRenderer());	
		
		responseTree.addMouseListener( new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() <= 1) {
					return;
				}
				showPathway();
			}
		});
		// done
		responseTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			
			public void valueChanged(TreeSelectionEvent e) {
				Object obj = responseTree.getLastSelectedPathComponent();
				if (obj == null || !(obj instanceof BioModelNode)) {
					return;
				}
				BioModelNode selectedNode = (BioModelNode)obj;
				Object userObject = selectedNode.getUserObject();
				setSelectedObjects(new Object[] {userObject});
			}
		});
	}

	public static boolean isPathwayObject(Object object) {
		return  object instanceof Pathway ||  object instanceof DataSource
				||  object instanceof PathwayStringObject ||  object instanceof XRef
				||  object instanceof Organism;
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
	}
}
