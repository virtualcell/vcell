/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.sbpax.util.StringUtil;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.persistence.PathwayIOUtil;
import org.vcell.pathway.persistence.RDFXMLContext;
import org.vcell.sybil.util.http.pathwaycommons.search.DataSource;
import org.vcell.sybil.util.http.pathwaycommons.search.Hit;
import org.vcell.sybil.util.http.pathwaycommons.search.Organism;
import org.vcell.sybil.util.http.pathwaycommons.search.Pathway;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.xml.DOMUtil;
import org.vcell.util.BeanUtils;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cbit.gui.TextFieldAutoCompletion;
import cbit.util.xml.XmlUtil;
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
		private String topLevelPathwayName;
		
		public PathwayData(String pathwayName, PathwayModel pathwayModel) {
			super();
			this.topLevelPathwayName = pathwayName;
			this.pathwayModel = pathwayModel;
		}
		public PathwayModel getPathwayModel() {
			return pathwayModel;
		}
		public String getTopLevelPathwayName() {
			return topLevelPathwayName;
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
	private TextFieldAutoCompletion searchTextField;
	private TextFieldAutoCompletion filterTextField;
	private Set<String> searchTextList = new HashSet<String>();
	private JButton searchButton = null;
	private JButton sortButton = null;
	private boolean bAscending = true;
	private JTree responseTree = null;
	private ResponseTreeModel responseTreeModel = null;
	List<Pathway> pathwaysList = new ArrayList<Pathway>();
	private JButton showPathwayButton = null;
	private JButton gotoPathwayButton = null;
	private EventHandler eventHandler = new EventHandler();

	private class ResponseTreeModel extends DefaultTreeModel {
		private BioModelNode rootNode = null;
		
		ResponseTreeModel() {
			super(new BioModelNode(new PathwayStringObject("search results"), true), true);
			rootNode = (BioModelNode)root;
		}
		
		void sortTree() {
			sortButton.setIcon( bAscending ? VCellIcons.sortDownIcon : VCellIcons.sortUpIcon );
			if(bAscending) {
				Collections.sort(pathwaysList);
			} else {
				Collections.sort(pathwaysList, Collections.reverseOrder());
			}
			filterTree();
		}
		void filterTree() {
			int pathwayCount = 0;
			rootNode.removeAllChildren();
			for (Pathway pathway : pathwaysList) {
				if(!pathway.filterOut(filterTextField.getText())) {
					BioModelNode node = new BioModelNode(pathway, false);
					rootNode.add(node);
					pathwayCount++;
				}
			}
			if (pathwayCount == 0) {
				String shortInfo = "<html>No pathways found for <b>" + searchTextField.getText() + "</b>";
				if(!(filterTextField.getText().contentEquals(""))) {
					shortInfo += ", filtered by \'" + filterTextField.getText() + "\'";
				}
				shortInfo += "</html>";
				rootNode.setUserObject(new PathwayStringObject(shortInfo));				
			} else {
				String shortInfo = "<html>" + pathwayCount + " pathways found for <b>" + searchTextField.getText() + "</b>";
				if(!(filterTextField.getText().contentEquals(""))) {
					shortInfo += ", filtered by \'" + filterTextField.getText() + "\'";
				}
				shortInfo += "</html>";
				rootNode.setUserObject(new PathwayStringObject(shortInfo));
			}
			nodeStructureChanged(rootNode);
			TreePath path = new TreePath(rootNode.getPath());
			responseTree.expandPath(path);
			responseTree.setSelectionPath(path);
		}
		void addSearchResponse(String searchText, Element searchResponse) {
			rootNode.removeAllChildren();
			if (searchResponse == null) {
				rootNode.setUserObject(new PathwayStringObject("no pathways found for \"" + searchText + "\""));				
			} else {
//				pathwaysList = new ArrayList<Pathway>();
				pathwaysList.clear();
				List<Element> hitElements = DOMUtil.childElements(searchResponse, "search_hit");
				int pathwayCount = 0;
				for(Element hitElement : hitElements) {
					Hit hit = new Hit(hitElement);
					List<Pathway> pL = hit.pathways();		// pathway
					int numPathways = pL.size();
					if (numPathways > 0) {
						for (Pathway pathway: pL) {
							pathway.setOrganism(hit.organism());
							if(!pathwaysList.contains(pathway)) {
								pathwaysList.add(pathway);
							}
						}
					}
				}
			}
			sortTree();
		}
	}
	
	private static final String PATHWAY_1_QUERY_URL = "http://www.pathwaycommons.org/pc/webservice.do" + 
		"?version=3.0&snapshot_id=GLOBAL_FILTER_SETTINGS&record_type=PATHWAY&q=glycolysis&format=html&cmd=get_by_keyword";
	private static final String PATHWAY_2_QUERY_URL = "http://www.pathwaycommons.org/pc/webservice.do?cmd=search&version=2.0" + 
		"&q=insulin&maxHits=14&output=xml";
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
					labelText = hit.pathways().size() + " pathways for " + name + (stringBuilder.length() == 0 ? 
										"" : "(" + stringBuilder.toString() + ")");
				} else if (userObject instanceof Pathway){
					Pathway pathway = (Pathway)userObject;
					// <font color="red">This is some text!</font>
					// <font color="#990000">
					// <html><body><p style="color:red">This is some text!</p></body></html>
					// <u> = underlined,  <b> = bold
					String dbName = pathway.dataSource().name();
					if(dbName.contains("Interaction Database")) {
						dbName = dbName.replace("Interaction Database", "Db");
					}
					labelText = "<html>" + pathway.name() + "  [<font color=\"#007700\">" + dbName + "</font>, " + 
										"<font color=\"#770000\">" + pathway.getOrganism().speciesName() + "</font>]</html>";
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
	
	private class EventHandler implements ActionListener, KeyListener, TreeSelectionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == searchButton || e.getSource() == searchTextField) {
				search();
			} else if (e.getSource() == sortButton) {
				bAscending = !bAscending;
				responseTreeModel.sortTree();
			} else if (e.getSource() == showPathwayButton) {
				showPathway();
			} else if (e.getSource() == gotoPathwayButton) {
				gotoPathway();
			}
		}

		public void valueChanged(TreeSelectionEvent e) {
			treeSelectionChanged();			
		}

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
			if(e.getSource() == filterTextField) {
				responseTreeModel.filterTree();
			}
		}

		public void keyTyped(KeyEvent e) {
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
				DialogUtils.browserLauncher(BioModelEditorPathwayCommonsPanel.this, url, "failed to open " + url);
			}
		}
	}
	
	public void showPathway() {
		final Pathway pathway = computeSelectedPathway();
		if (pathway == null) {
			return;
		}
		AsynchClientTask task1 = new AsynchClientTask("Importing pathway '" + pathway.name() + "'", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(final Hashtable<String, Object> hashTable) throws Exception {
				final URL url = new URL(defaultBaseURL + "?" 
						+ PathwayCommonsKeyword.cmd + "=" + PathwayCommonsKeyword.get_record_by_cpath_id 
						+ "&" + PathwayCommonsKeyword.version + "=" + PathwayCommonsVersion.v2.name 
						+ "&" + PathwayCommonsKeyword.q + "=" + pathway.primaryId()
						+ "&" + PathwayCommonsKeyword.output + "=" + PathwayCommonsKeyword.biopax);
				
				System.out.println(url.toString());				
				String ERROR_CODE_TAG = "error_code";
//				String ERROR_MSG_TAG = "error_msg";
				org.jdom.Document jdomDocument = XmlUtil.getJDOMDocument(url, getClientTaskStatusSupport());
				org.jdom.Element rootElement = jdomDocument.getRootElement();
				String errorCode = rootElement.getChildText(ERROR_CODE_TAG);
				if (errorCode != null){
					throw new RuntimeException("Failed to access " + url + " \n\nPlease try again.");
				}
				
//						String xmlText = StringUtil.textFromInputStream(connection.getInputStream());
//						PathwayReader pathwayReader = new PathwayReader();
//						org.jdom.Document jdomDocument = XmlUtil.stringToXML(xmlText, null);
				
//						String xmlText = StringUtil.textFromInputStream(connection.getInputStream(), "UTF-8");
//						PathwayReader pathwayReader = new PathwayReader();
//						org.jdom.Document jdomDocument = XmlUtil.stringToXML(xmlText, "UTF-8");
				
				PathwayModel pathwayModel = PathwayIOUtil.extractPathwayFromJDOM(jdomDocument, new RDFXMLContext(), 
						getClientTaskStatusSupport());
				PathwayData pathwayData = new PathwayData(pathway.name(), pathwayModel);
				hashTable.put("pathwayData", pathwayData);
			}
		};
		
		AsynchClientTask task2 = new AsynchClientTask("showing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				PathwayData pathwayData = (PathwayData) hashTable.get("pathwayData");
				if (pathwayData != null) {
//					setActiveView(new ActiveView(null, DocumentEditorTreeFolderClass.PATHWAY_NODE, null));
					setSelectedObjects(new Object[] {pathwayData});
				}
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, true,true,null);
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
		searchTextList.add(searchText);
		searchTextField.setAutoCompletionWords(searchTextList);
		AsynchClientTask task1 = new AsynchClientTask("searching", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				URL url = new URL(defaultBaseURL + "?" 
						+ PathwayCommonsKeyword.cmd + "=" + PathwayCommonsKeyword.search 
						+ "&" + PathwayCommonsKeyword.version + "=" + PathwayCommonsVersion.v2.name 
						+ "&" + PathwayCommonsKeyword.q + "=" + URLEncoder.encode(searchText, "UTF-8")
						+ "&" + PathwayCommonsKeyword.maxHits + "=" + 14
						+ "&" + PathwayCommonsKeyword.output + "=" + PathwayCommonsKeyword.xml);
				System.out.println(url);
				String responseContent = BeanUtils.downloadBytes(url, getClientTaskStatusSupport());
				Document document = DOMUtil.parse(responseContent);	

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
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false,true,null);
	}
	
	private void initialize() {
		searchTextField = new TextFieldAutoCompletion();
		searchTextField.addActionListener(eventHandler);
		searchTextField.putClientProperty("JTextField.variant", "search");

		filterTextField = new TextFieldAutoCompletion();
		filterTextField.addActionListener(eventHandler);
		filterTextField.addKeyListener(eventHandler);
		filterTextField.putClientProperty("JTextField.variant", "filter");

		searchButton = new JButton("Search");
		searchButton.addActionListener(eventHandler);
		sortButton = new JButton("Sort");
		sortButton.addActionListener(eventHandler);
		showPathwayButton = new JButton("Preview");
		showPathwayButton.addActionListener(eventHandler);
		showPathwayButton.setEnabled(false);
		gotoPathwayButton = new JButton("Open Web Link");
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
		CollapsiblePanel filterPanel = new CollapsiblePanel("Filter", true);
		filterPanel.getContentPanel().setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(2,2,2,2);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		filterPanel.getContentPanel().add(filterTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,4,0,0);
		filterPanel.getContentPanel().add(sortButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		filterPanel.expand(true);
		add(filterPanel, gbc);	

//		JPanel optionsField = new JPanel(new GridLayout());
//		ButtonGroup buttonGroup = new ButtonGroup();
//		JRadioButton o1, o2, o3;
//		o1 = new JRadioButton(" Entity ");
//		buttonGroup.add(o1);
//		optionsField.add(o1);
//		o2 = new JRadioButton(" Database ");
//		buttonGroup.add(o2);
//		optionsField.add(o2);
//		o3 = new JRadioButton(" Organism ");
//		buttonGroup.add(o3);
//		optionsField.add(o3);
//		o3.setSelected(true);
//		
//		gridy ++;
//		CollapsiblePanel optionsPanel = new CollapsiblePanel("Group by... ", true);
//		optionsPanel.getContentPanel().setLayout(new GridBagLayout());
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.LINE_START;
//		optionsPanel.getContentPanel().add(optionsField, gbc);
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = gridy;
//		gbc.weightx = 1.0;
//		gbc.gridwidth = GridBagConstraints.REMAINDER;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(4,4,4,4);
//		optionsPanel.expand(false);
//		add(optionsPanel, gbc);	

		
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
		
		sortButton.setIcon( bAscending ? VCellIcons.sortDownIcon : VCellIcons.sortUpIcon );
		
		ResponseTreeCellRenderer renderer = new ResponseTreeCellRenderer();
//		renderer.setLeafIcon(null);
//		renderer.setClosedIcon(null);
//		renderer.setOpenIcon(null);
		renderer.setLeafIcon(VCellIcons.pathwayLeafIcon);
		responseTree.setCellRenderer(renderer);	
		
		responseTree.setRootVisible(false);
		
		responseTree.getSelectionModel().addTreeSelectionListener(eventHandler);
		responseTree.addMouseListener( new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() <= 1) {
					return;
				}
				showPathway();
			}
		});

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
