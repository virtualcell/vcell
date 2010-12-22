package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdom.DataConversionException;
import org.jdom.Element;
import org.vcell.sbml.test.BiomodelsDB_TestSuite;
import org.vcell.util.gui.CollapsiblePanel;

import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesServiceLocator;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.xml.XMLInfo;

@SuppressWarnings("serial")
public class BioModelsNetPanel extends BioModelEditorSubPanel {
	private static final String BIO_MODELS_NET = "BioModels.net";	

	private class BioModelsNetTreeCellRenderer extends DefaultTreeCellRenderer  {
		public BioModelsNetTreeCellRenderer() {
			super();
			setPreferredSize(new Dimension(250,30));
			setBorder(new EmptyBorder(0, 2, 0, 0));
		}
		public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			String text = null;
			if (value instanceof BioModelNode) {
		        BioModelNode node = (BioModelNode)value;
		        Object userObj = node.getUserObject();
		    	if (userObj instanceof BioModelsNetModelInfo) {
		    		text = ((BioModelsNetModelInfo) userObj).getName();
		    	} else if (userObj instanceof String) {
		    		text = (String)userObj;
		    	}
			}
			setText(text);
			return this;
		}
	}
	private class BioModelsNetTreeModel extends DefaultTreeModel {
		private BioModelNode rootNode = null;
		
		private BioModelsNetTreeModel() {
			super(new BioModelNode(BIO_MODELS_NET, true),true);			
			rootNode = (BioModelNode)root;
			populateRootNode();
		}

		private void populateRootNode() {
			try {
				List<BioModelsNetModelInfo> infoList = readVCellCompatibleBioModels_ID_Name_Hash();
				Collections.sort(infoList, new Comparator<BioModelsNetModelInfo>() {
	
					public int compare(BioModelsNetModelInfo o1, BioModelsNetModelInfo o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
				for (BioModelsNetModelInfo bioModelsNetInfo : infoList) {
					BioModelNode node = new BioModelNode(bioModelsNetInfo, false);
					rootNode.add(node);
				}
				nodeStructureChanged(rootNode);
			} catch (Exception ex) {
				rootNode.setUserObject("Failed to load BioModels.net databases.");
			}
		}

		public void select(BioModelsNetModelInfo bioModelsNetInfo) {
			BioModelNode node = rootNode.findNodeByUserObject(bioModelsNetInfo);
			if (node != null) {
				tree.setSelectionPath(new TreePath(node.getPath()));
			}			
		}
	}
	
	private static final String BIOMODELS_DATABASE_URL = "http://www.ebi.ac.uk/biomodels-main/";
	private DocumentWindowManager documentWindowManager = null;
	private JTextField searchTextField;
	private JButton searchButton = null;
	private JButton showAllButton = null;
	private JButton importButton = null;
	private EventHandler eventHandler = new EventHandler();
	private JTree tree = null;
	private BioModelsNetTreeModel treeModel = null;	
	
	private class EventHandler implements ActionListener, TreeSelectionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == importButton) {
				importFromBioModelsNet();
			}
		}

		public void valueChanged(TreeSelectionEvent e) {
			if (e.getSource() == tree.getSelectionModel()) {
				treeSelectionChanged();
			}
		}
		
	}
	public BioModelsNetPanel() {
		super();
		initialize();
	}
	
	public void importFromBioModelsNet() {
		Object obj = tree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode)obj;
		Object userObject = selectedNode.getUserObject();
		if (!(userObject instanceof BioModelsNetModelInfo)) {
			return;
		}
		final BioModelsNetModelInfo bioModelsNetInfo = (BioModelsNetModelInfo) userObject;
		AsynchClientTask task1 = new AsynchClientTask("Importing " + bioModelsNetInfo.getName(), AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				BioModelsWebServicesServiceLocator bioModelsWebServicesServiceLocator =	new BioModelsWebServicesServiceLocator();
				BioModelsWebServices bioModelsWebServices = bioModelsWebServicesServiceLocator.getBioModelsWebServices();
				String bioModelSBML = bioModelsWebServices.getModelSBMLById(bioModelsNetInfo.getId());
				XMLInfo xmlInfo = new XMLInfo(bioModelSBML, bioModelsNetInfo.getName());
				if (xmlInfo != null) {
					hashTable.put("xmlInfo", xmlInfo);
				}
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("Opening",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				XMLInfo xmlInfo = (XMLInfo) hashTable.get("xmlInfo");
				if (xmlInfo == null) {
					return;
				}
				documentWindowManager.getRequestManager().openDocument(xmlInfo, documentWindowManager, true);
			}
		};
		ClientTaskDispatcher.dispatch(documentWindowManager.getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, false);
	}

	public void treeSelectionChanged() {
		importButton.setEnabled(false);
		
		Object obj = tree.getLastSelectedPathComponent();
		if (obj == null || !(obj instanceof BioModelNode)) {
			return;
		}
		BioModelNode selectedNode = (BioModelNode)obj;
		Object userObject = selectedNode.getUserObject();
		if (userObject instanceof BioModelsNetModelInfo) {
			importButton.setEnabled(true);
			setSelectedObjects(new Object[] {userObject});
		}
	}

	private void initialize() {
		searchTextField = new JTextField(10);
		searchButton = new JButton("Search");
		searchButton.addActionListener(eventHandler);
		showAllButton = new JButton("Show All");
		showAllButton.addActionListener(eventHandler);
		importButton = new JButton("Import");
		importButton.addActionListener(eventHandler);
		importButton.setEnabled(false);
		tree = new JTree();
		tree.setCellRenderer(new BioModelsNetTreeCellRenderer());
		treeModel = new BioModelsNetTreeModel();
		tree.setModel(treeModel);
		tree.getSelectionModel().addTreeSelectionListener(eventHandler);
				
		JPanel searchPanel = new CollapsiblePanel("Search", false);
		searchPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		searchPanel.add(searchTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,4,0,0);
		searchPanel.add(searchButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,4,0,0);
		searchPanel.add(showAllButton, gbc);	
		
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
		add(new JScrollPane(tree), gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		add(importButton, gbc);
	}

	public void setDocumentWindowManager(DocumentWindowManager newValue) {
		this.documentWindowManager = newValue;
	}
	
	private List<BioModelsNetModelInfo> readVCellCompatibleBioModels_ID_Name_Hash() throws IOException, DataConversionException {
		List<BioModelsNetModelInfo> vcellCompatibleBioModelsList = new ArrayList<BioModelsNetModelInfo>();

		final String BIOMODELSNET_INFO_FILENAME = "/bioModelsNetInfo.xml";
		InputStream tableInputStream = getClass().getResourceAsStream(BIOMODELSNET_INFO_FILENAME);
		if (tableInputStream==null){
			throw new FileNotFoundException(BIOMODELSNET_INFO_FILENAME+" not found");
		}
		//Process the Info files
		org.jdom.input.SAXBuilder saxparser = new org.jdom.input.SAXBuilder(false);
		org.jdom.Document doctable = null;
		try {
			doctable = saxparser.build(tableInputStream);
		} catch (org.jdom.JDOMException e) {
			e.printStackTrace();
			throw new java.io.IOException("An error occurred when trying to parse the rules file ");
		}
		Iterator<Element> ruleiterator = doctable.getRootElement().getChildren().iterator();
		while (ruleiterator.hasNext()) {
			Element temp = (Element) ruleiterator.next();
			//System.out.println(temp.getAttributeValue("TagName") + ":" + temp.getAttributeValue("AttrName"));
			boolean bSupported = temp.getAttribute(BiomodelsDB_TestSuite.SUPPORTED_ATTRIBUTE_NAME).getBooleanValue();
			if(bSupported){
				String id = temp.getAttributeValue(BiomodelsDB_TestSuite.ID_ATTRIBUTE_NAME);
				String name = temp.getAttributeValue(BiomodelsDB_TestSuite.MODELNAME_ATTRIBUTE_NAME);
				vcellCompatibleBioModelsList.add(new BioModelsNetModelInfo(id, name, BIOMODELS_DATABASE_URL + "/" + id));
			}
		}

		return vcellCompatibleBioModelsList;
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof BioModelsNetModelInfo)) {
			tree.clearSelection();
			return;
		}
		BioModelsNetModelInfo bioModelsNetModelInfo = (BioModelsNetModelInfo) selectedObjects[0];
		treeModel.select(bioModelsNetModelInfo); 
	}
}
