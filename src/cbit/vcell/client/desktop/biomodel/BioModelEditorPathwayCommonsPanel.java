package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.vcell.sybil.util.http.pathwaycommons.search.PCKeywordResponse;
import org.vcell.sybil.util.http.pathwaycommons.search.Pathway;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.http.uniprot.UniProtExtractor;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest.Response;
import org.vcell.sybil.util.http.uniprot.box.UniProtBox;
import org.vcell.sybil.util.http.uniprot.box.imp.UniProtBoxImp;
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

import com.hp.hpl.jena.rdf.model.Model;

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
	private static final String uniport = "uniprot";
	public static class PathwayData {
		private String xml;
		private PathwayModel pathwayModel;
		
		private PathwayData(String xml, PathwayModel pathwayModel) {
			super();
			this.xml = xml;
			this.pathwayModel = pathwayModel;
		}
		public final String getXml() {
			return xml;
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
	private EventHandler eventHandler = new EventHandler();
	private int mouseOverRow = -1;
	private static UniProtBox uniProtBox = new UniProtBoxImp();		

	private class ResponseTreeModel extends DefaultTreeModel {
		private BioModelNode rootNode = null;
		private Map<String, BioModelNode> searchMap = new HashMap<String, BioModelNode>();
		
		ResponseTreeModel() {
			super(new BioModelNode(new PathwayStringObject("search results"), true), true);
			rootNode = (BioModelNode)root;
		}
		
		private void createHitChildNode(BioModelNode hitNode, List<String> childElementList, String childName) {
			BioModelNode namesNode = null;
			if (childElementList.size() > 0) {
				if (childElementList.size() == 1) {
					namesNode = new BioModelNode(new PathwayStringObject("1 " + childName + ": " + childElementList.get(0)), true);
				} else {
					namesNode = new BioModelNode(new PathwayStringObject(childElementList.size() + " " + childName + "s"), true);
				}
				for (String element : childElementList) {
					namesNode.add(new BioModelNode(new PathwayStringObject(element), false));
				}
				hitNode.add(namesNode);
			}
		}
			
		private boolean obsolete(XRef xRef) {
			if (xRef.db().equalsIgnoreCase(uniport) &&
					PCKeywordResponse.uniProtBox().entryIsObsolete(xRef.id())) {
				return true;
			}
			return false;
		}
		
		void addSearchResponse(String searchText, Element searchResponse) {
			BioModelNode searchNode = searchMap.get(searchText);
			if (searchNode != null) {
				rootNode.remove(searchNode);
			}
			if (searchResponse == null) {
				searchNode = new BioModelNode(new PathwayStringObject("Keyword \"" + searchText + "\": no matches"));				
			} else {
				List<Hit> hitList = new ArrayList<Hit>();
				List<Element> hitElements = DOMUtil.childElements(searchResponse, "search_hit");
				for(Element hitElement : hitElements) { 
					Hit hit = new Hit(hitElement);
					hitList.add(hit); 
					for(XRef xref : hit.xRefs()) {
						if(xref.db().equalsIgnoreCase("uniprot")) {
							UniProtRDFRequest uniProtRequest = new UniProtRDFRequest(xref.id());
							Response uniProtResponse = uniProtRequest.response();
							if(uniProtResponse instanceof UniProtRDFRequest.ModelResponse) {
								Model model = ((UniProtRDFRequest.ModelResponse) uniProtResponse).model();
								uniProtBox.add(UniProtExtractor.extractBox(model));
							}
						}
					}
				}
				int totalNumHits = Integer.parseInt(searchResponse.getAttribute("total_num_hits"));
				searchNode = new BioModelNode(new PathwayStringObject("Keyword \"" + searchText + "\": " + hitList.size() + " of " + totalNumHits + " matches"), true);				
				for (Hit hit : hitList) {
					BioModelNode hitNode = new BioModelNode(hit, true);
					createHitChildNode(hitNode, hit.names(), "name");
					createHitChildNode(hitNode, hit.synonyms(), "synonym");
					createHitChildNode(hitNode, hit.descriptions(), "description");
					createHitChildNode(hitNode, hit.excerpts(), "excerpt");
					DataSource dataSource = hit.dataSource();
					if (dataSource != null) {
						hitNode.add(new BioModelNode(dataSource, false));
					}
					Organism organism = hit.organism();
					if (organism != null) {
						hitNode.add(new BioModelNode(organism, false));
					}
					// xref
					List<XRef> xrefList = hit.xRefs();
					BioModelNode xrefsNode = null;
					int numXRefs = xrefList.size();
					if (numXRefs == 0) {
						xrefsNode = new BioModelNode(new PathwayStringObject("no cross references"), false);
					} else {					
						List<XRef> obsoleteXRefs = new ArrayList<XRef>();
						xrefsNode = new BioModelNode(null, true);
						for(XRef xRef : hit.xRefs()) { 
							if (obsolete(xRef)) {
								obsoleteXRefs.add(xRef);
							} else {
								xrefsNode.add(new BioModelNode(xRef, false));
							}
						}
						int numObsoletes = obsoleteXRefs.size();
						xrefsNode.setUserObject(new PathwayStringObject(numXRefs + " cross reference(s) " + (numObsoletes == 0 ? "" : "(" + numObsoletes + " obsolete(s))")));
						for(XRef xRef : obsoleteXRefs) {							
							xrefsNode.add(new BioModelNode(xRef, false));
						}
					}					
					hitNode.add(xrefsNode);
					// pathway
					List<Pathway> pathwayList = hit.pathways();
					BioModelNode pathwaysNode = null;
					int numPathways = pathwayList.size();
					if (numPathways == 0) {
						pathwaysNode = new BioModelNode(new PathwayStringObject("no pathways"), false);
					} else {
						pathwaysNode = new BioModelNode(new PathwayStringObject(numPathways + " pathway(s)"), true);
						for (Pathway pathway: pathwayList) {
							pathwaysNode.add(new BioModelNode(pathway, false));
						}
					}
					hitNode.add(pathwaysNode);
					searchNode.add(hitNode);
				}
			}
			rootNode.insert(searchNode, 0);
			searchMap.put(searchText, searchNode);
			nodeStructureChanged(rootNode);
			TreePath path = new TreePath(searchNode.getPath());
			responseTree.expandPath(path);
			responseTree.setSelectionPath(path);
		}
		
	}
	
	private class ResonseTreeCellRenderer extends DefaultTreeCellRenderer {
		
		private Font mouseOverFont = null;
		private Font regularFont = null;
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, 
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus){
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (regularFont == null) {
				regularFont = getFont();
				mouseOverFont = regularFont.deriveFont(Font.BOLD);
			}
			if (row == mouseOverRow){
				if (!sel) {
					setForeground(Color.BLUE);
					setFont(mouseOverFont);
				}
			} else {
				setFont(regularFont);
			}
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
					if (hit.primaryID() != null && hit.primaryID().trim().length() > 0) {
						stringBuilder.append("," + hit.primaryID().trim()); 
					}
					if (hit.entityType() != null && hit.entityType().trim().length() > 0) {
						stringBuilder.append("," + hit.entityType().trim()); 
					}
					if (hit.organism() != null && hit.organism().speciesName().trim().length() > 0) {
						stringBuilder.append("," + hit.organism().speciesName().trim()); 
					}
					labelText =  name + (stringBuilder.length() == 0 ? "" : "(" + stringBuilder.toString() + ")");
				} else if (userObject instanceof Pathway){
					Pathway pathway = (Pathway)userObject;
					labelText = pathway.primaryId() + ":  " + pathway.name() + "  [" + pathway.dataSource().name() + "]";
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
	
	public void showPathway() {
		
		AsynchClientTask task1 = new AsynchClientTask("searching", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Pathway pathway = computeSelectedPathway();
				URL url = new URL(defaultBaseURL + "?" 
						+ PathwayCommonsKeyword.cmd + "=" + PathwayCommonsKeyword.get_record_by_cpath_id 
						+ "&" + PathwayCommonsKeyword.version + "=" + PathwayCommonsVersion.v2.name 
						+ "&" + PathwayCommonsKeyword.q + "=" + pathway.primaryId()
						+ "&" + PathwayCommonsKeyword.output + "=" + PathwayCommonsKeyword.biopax);
				
				URLConnection connection = url.openConnection();
				String xmlText = StringUtil.textFromInputStream(connection.getInputStream());
				PathwayReader pathwayReader = new PathwayReader();
				org.jdom.Document jdomDocument = XmlUtil.stringToXML(xmlText,null);
				PathwayModel pathwayModel = pathwayReader.parse(jdomDocument.getRootElement());
				pathwayModel.reconcileReferences();
				PathwayData pathwayData = new PathwayData(xmlText, pathwayModel);
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
		showPathwayButton.setEnabled(computeSelectedPathway() != null);
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
						+ "&" + PathwayCommonsKeyword.output + "=" + PathwayCommonsKeyword.xml);
//				System.out.println("url=" + url);
				URLConnection connection = url.openConnection();
				Document document = DOMUtil.parse(connection.getInputStream());				
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
		searchTextField.setText("egfr");
		searchTextField.addActionListener(eventHandler);
		searchButton = new JButton("Search");
		searchButton.addActionListener(eventHandler);
		showPathwayButton = new JButton("Show Pathway");
		showPathwayButton.addActionListener(eventHandler);
		showPathwayButton.setEnabled(false);
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
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(searchPanel, gbc);	

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(responseTree), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(showPathwayButton, gbc);
		
		responseTree.getSelectionModel().addTreeSelectionListener(eventHandler);
		responseTree.setCellRenderer(new ResonseTreeCellRenderer());
		
		responseTree.addMouseMotionListener(new MouseMotionAdapter() { 
		    public void mouseMoved(MouseEvent e) { 	
		    	mouseOverRow = -1;
		    	TreePath path = responseTree.getPathForLocation(e.getX(), e.getY());
		    	String url = generateURL(path);
				if (url != null) {
    				mouseOverRow = responseTree.getClosestRowForLocation(e.getX(), e.getY());
    				responseTree.setToolTipText("Double click to launch the web page " + url);
    				responseTree.repaint();
		    	} else {
		    		responseTree.setToolTipText(null);
		    	}
		    } 
		});
		
		responseTree.addMouseListener( new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() <= 1) {
					return;
				}
				TreePath path = responseTree.getPathForLocation(e.getX(), e.getY());
		    	if (path != null){
		    		String url = generateURL(path);
		    		if (url != null) {
		    			DialogUtils.browserLauncher(BioModelEditorPathwayCommonsPanel.this, url, "failed to open " + url, false);
		    		}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				mouseOverRow = -1;
				repaint();
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
	
	private String generateURL(TreePath treePath) {
		if (treePath == null) {
			return null;
		}
		Object selectedObject = treePath.getLastPathComponent();
		String url = null;
		if (selectedObject instanceof BioModelNode) {
			selectedObject = ((BioModelNode) selectedObject).getUserObject();
		}
		if (selectedObject instanceof Organism) {
			Organism organism = (Organism) selectedObject;
			url = "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=" + organism.ncbiOrganismId();
		} else if (selectedObject instanceof Pathway) {
			Pathway pathway = (Pathway) selectedObject;
			url = "http://www.pathwaycommons.org/pc/record2.do?id=" + pathway.primaryId();
		} else if (selectedObject instanceof XRef) {
			XRef xref = (XRef) selectedObject;
			url = xref.url();
		}
		if (url != null) {
			url = url.trim();
			if (url.length() > 0) {
				return url;
			}
		}
		return null;
	}

}
