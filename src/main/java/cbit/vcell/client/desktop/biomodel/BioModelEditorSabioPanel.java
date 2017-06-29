package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdom.Document;
import org.jdom.Element;
import org.vcell.pathway.BioPAXUtil;
import org.vcell.pathway.Control;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PathwayModel.SBVocabularyEx;
import org.vcell.pathway.persistence.PathwayIOUtil;
import org.vcell.pathway.persistence.RDFXMLContext;
import org.vcell.pathway.sbo.SBOListEx;
import org.vcell.pathway.sbo.SBOTerm;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBVocabulary;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.util.xml.XmlUtil;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayStringObject;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.BioModelNode;

@SuppressWarnings("serial")
public class BioModelEditorSabioPanel extends DocumentEditorSubPanel {

	private JPanel panel1 = null;
	private CollapsiblePanel panel2 = null;
	
	static final String[] criteriaStrings = { "EntryID", "Pathway", "KeggReactionID", "SabioReactionID", "AnyRole", 
			"Substrate", "Product", "Inhibitor", "Catalyst", "Cofactor", "Activator", "OtherModifier", 
			"PubChemID", "KeggID", "ChebiID", "SabioCompoundID", "Enzymename", "ECNumber", "UniprotID", 
			"Tissue", "Organism", "CellularLocation", "Parametertype", "KineticMechanismType", 
			"AssociatedSpecies", "Title", "Author", "Year", "PubmedID", "ExperimentID", 
			"EnzymeType", "InfosourceTyp", "HasKineticData", "IsRecombinant", "pHValueRange", 
			"TemperatureRange", "DateSubmitted" };

	JComboBox categoryList2 = new JComboBox(criteriaStrings);
	JTextField entityName1 = new JTextField("insulin");		// other search: stat*, stat1
//	JTextField entityName2 = new JTextField("11427");
	JTextField entityName2 = new JTextField("");
	private JButton searchSabioDatabaseButton = null;
	String command = "";
	
	private JTree responseTree = null;
	private ResponseTreeModel responseTreeModel = null;
	List<SBEntity> kineticLawsList = new ArrayList<SBEntity>();
	private EventHandler eventHandler = new EventHandler();
	
	private class EventHandler implements ActionListener, KeyListener, TreeSelectionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == searchSabioDatabaseButton) {
				populateKineticLawsTree();
			}
		}
		public void valueChanged(TreeSelectionEvent e) {
			treeSelectionChanged();		
		}
		public void keyPressed(KeyEvent e) {
		}
		public void keyReleased(KeyEvent e) {
		}
		public void keyTyped(KeyEvent e) {
		}
	}

	private class ResponseTreeModel extends DefaultTreeModel {
		private BioModelNode rootNode = null;
		ResponseTreeModel() {
			super(new BioModelNode(new PathwayStringObject("Search results"), true), true);
			rootNode = (BioModelNode)root;
		}
		void filterTree() {
			int pathwayCount = 0;
			rootNode.removeAllChildren();
			for (SBEntity kl : kineticLawsList) {
				BioModelNode node = new BioModelNode(kl, false);
				rootNode.add(node);
				pathwayCount++;
			}
			rootNode.setUserObject(new PathwayStringObject("alabala"));
			nodeStructureChanged(rootNode);
			TreePath path = new TreePath(rootNode.getPath());
			responseTree.expandPath(path);
			responseTree.setSelectionPath(path);
		}
		void addSearchResponse(String searchText, PathwayData searchResponse) {
			rootNode.removeAllChildren();
			if (searchResponse == null) {
				rootNode.setUserObject(new PathwayStringObject("no pathways found for \"" + searchText + "\""));				
			} else {
				kineticLawsList.clear();
				PathwayModel pathwayModel = searchResponse.getPathwayModel();
				Set<Control> controls = BioPAXUtil.getAllControls(pathwayModel);
				Iterator<Control> iter = controls.iterator();
			    while (iter.hasNext()) {
			    	Control control = iter.next();
					ArrayList<SBEntity> sbEntities = control.getSBSubEntity();
					for(SBEntity sbE : sbEntities) {
						// the following if clause may not be needed, we KNOW that 
						// the only SBSubEntities allowed in a control are kinetic laws
						if(sbE.getID().contains("kineticLaw")) {
							kineticLawsList.add(sbE);
						}
					}
			    }
			}
			filterTree();
		}
	}
	private class ResponseTreeCellRenderer extends DefaultTreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, 
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus){
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			String labelText = null;
			if (value instanceof BioModelNode) {
				BioModelNode bioModelNode = (BioModelNode)value;
				Object userObject = bioModelNode.getUserObject();
				if (userObject instanceof SBEntity) {
					SBEntity sbE = (SBEntity)userObject;
					labelText = sbE.getID();
										
					if (labelText != null) {
						labelText = labelText.substring(labelText.lastIndexOf('#')+1);
						String sDetails = "";
						ArrayList<SBVocabulary> sbTerms = sbE.getSBTerm();
						for(SBVocabulary sbv : sbTerms) {
							String str1 = sbv.getID();	// type of kinetic law
							str1 = str1.substring(str1.lastIndexOf('#')+1);
							SBOTerm sboT = SBOListEx.sboMap.get(str1);
							String inferred = "";
							if(sbv instanceof SBVocabularyEx) {
								inferred += " (inferred) ";
							}
							String name = sboT.getName();
//							System.out.println(" " + name);
							if(name.contains("rate law")) {
								name = name.substring(0, name.indexOf("rate law"));
							}
							sDetails += "<font color=\"#006600\">" + name + "</font>" + " " +
								"<font color=\"#DD0000\">" + inferred + "</font>" +
								"<font color=\"#660000\"> [" + str1 + "] </font>";
							//sDetails += "<br>";
						}
						labelText = "<html>" + labelText + " " + sDetails + "</html>";
						setText(labelText);
					}
				}
			}
			return this;
		}
	};

	public BioModelEditorSabioPanel() {
		super();
		initialize();
	}
	
	public SBEntity computeSelectedKineticLaw() {
		Object object = responseTree.getLastSelectedPathComponent();
		if (object == null || !(object instanceof BioModelNode)) {
			return null;
		}
		Object userObject = ((BioModelNode)object).getUserObject();
		if (userObject instanceof SBEntity) {
			return (SBEntity)userObject;
		}
		return null;
	}
	
	private void initialize() {
		responseTree = new JTree();
		responseTreeModel = new ResponseTreeModel();
		responseTree.setModel(responseTreeModel);
		ToolTipManager.sharedInstance().registerComponent(responseTree);

		setLayout(new BorderLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		// ------- panel 1 ---------------
		JLabel categoryLabel1 = new JLabel("Role: ");
		
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createTitledBorder(" Search "));
		panel1.setLayout(new GridBagLayout());
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(categoryLabel1, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(entityName1, gbc);

		gbc = new GridBagConstraints();
		searchSabioDatabaseButton = new JButton("Search");
		searchSabioDatabaseButton.addActionListener(eventHandler);
		searchSabioDatabaseButton.setEnabled(true);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(2,4,4,4);
		panel1.add(searchSabioDatabaseButton, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		p.add(panel1);
		
		// ------- panel 2 -----------------
		panel2 = new CollapsiblePanel(" Advanced Search ", false, BorderFactory.createLineBorder(new Color(213,223,229)));
		panel2.getContentPanel().setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,1);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		
		JLabel categoryLabel2 = new JLabel("Category");
		categoryList2.setSelectedIndex(3);
		categoryList2.addActionListener(eventHandler);
		
		JLabel entityLabel2 = new JLabel("Entity");
		entityName2.addActionListener(eventHandler);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2,2,2,0);
		panel2.getContentPanel().add(categoryLabel2, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0,2,2,0);
		panel2.getContentPanel().add(categoryList2, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2,2,2,0);
		panel2.getContentPanel().add(entityLabel2, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0,0,2,2);
		panel2.getContentPanel().add(entityName2, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		p.add(panel2, gbc);
		
		// ------- tree ------------------
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(new JScrollPane(responseTree));

		// ------- the panels -------------
		add(p,BorderLayout.NORTH);
		add(p1,BorderLayout.CENTER);

		ResponseTreeCellRenderer renderer = new ResponseTreeCellRenderer();
		renderer.setLeafIcon(VCellIcons.kineticLawIcon);	// also available kineticLaw2Icon more colorful
		responseTree.setCellRenderer(renderer);	
		responseTree.setRootVisible(false);
		
		responseTree.getSelectionModel().addTreeSelectionListener(eventHandler);
//		responseTree.addMouseListener( new MouseAdapter(){
//			public void mouseClicked(MouseEvent e){
//				if (e.getClickCount() <= 1) {
//					return;
//				}
//				treeSelectionChanged();	// for double click
//			}
//		});
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
	
	public void populateKineticLawsTree() {
//		String category1 = (String)categoryList1.getSelectedItem();
		String category1 = "AnyRole";
		String category2 = (String)categoryList2.getSelectedItem();
		String entity1 = entityName1.getText();
		String entity2 = entityName2.getText();
		command = "";	// http://sabiork.h-its.org/sabioRestWebServices/searchKineticLaws/biopax?q=%28Pathway:JAK-STAT%20AND%20CellularLocation:cytosol%29
		if(entity1.isEmpty() && entity2.isEmpty()) {
			DialogUtils.showWarningDialog(this, "No search criteria specified.");
			return;
		}
		if(!entity1.isEmpty() && entity2.isEmpty()) {
			command += category1 + ":" + entity1;
		} else if(entity1.isEmpty() && !entity2.isEmpty()) {
			command += category2 + ":" + entity2;
		} else {	// both entities present
			command += "%28" + category1 + ":" + entity1 + "%20AND%20" + category2 + ":" + entity2 + "%29";
		}
		AsynchClientTask task1 = new AsynchClientTask("Importing Kinetic Laws", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(final Hashtable<String, Object> hashTable) throws Exception {
				final URL url = new URL("http://sabiork.h-its.org/sabioRestWebServices/searchKineticLaws/biopax?q=" + command);
				System.out.println(url.toString());				
				String ERROR_CODE_TAG = "error_code";
				Document jdomDocument = XmlUtil.getJDOMDocument(url, getClientTaskStatusSupport());
				Element rootElement = jdomDocument.getRootElement();
				String errorCode = rootElement.getChildText(ERROR_CODE_TAG);
				if (errorCode != null){
					throw new RuntimeException("Failed to access " + url + " \n\nPlease try again.");
				}
				PathwayModel pathwayModel = PathwayIOUtil.extractPathwayFromJDOM(jdomDocument, new RDFXMLContext(), 
						getClientTaskStatusSupport());
				PathwayData pathwayData = new PathwayData("Sabio Kinetic Laws", pathwayModel);
				hashTable.put("pathwayData", pathwayData);
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("showing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				PathwayData pathwayData = (PathwayData) hashTable.get("pathwayData");
				if (pathwayData != null) {
					responseTreeModel.addSearchResponse("text", pathwayData);
				}
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, true, true, null);
	}
	public void treeSelectionChanged() {
		System.out.println("treeSelectionChanged");
		final SBEntity kineticLaw = computeSelectedKineticLaw();
		if (kineticLaw == null) {
			return;
		}
		String kineticLawID = kineticLaw.getID();
		final String kls = new String("kineticLaw");
		if(!kineticLawID.contains(kls)) {
			return;
		}
		kineticLawID = kineticLawID.substring(kineticLawID.lastIndexOf(kls)+kls.length());
		command = "EntryID:" + kineticLawID;

		AsynchClientTask task1 = new AsynchClientTask("Importing Kinetic Laws", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(final Hashtable<String, Object> hashTable) throws Exception {
				final URL url = new URL("http://sabiork.h-its.org/sabioRestWebServices/searchKineticLaws/biopax?q=" + command);
//				final URL url = new URL("http://sabiork.h-its.org/sabioRestWebServices/searchKineticLaws/biopax?q=EntryID:33107");
				System.out.println(url.toString());
				String ERROR_CODE_TAG = "error_code";
				Document jdomDocument = XmlUtil.getJDOMDocument(url, getClientTaskStatusSupport());
				Element rootElement = jdomDocument.getRootElement();
				String errorCode = rootElement.getChildText(ERROR_CODE_TAG);
				if (errorCode != null){
					throw new RuntimeException("Failed to access " + url + " \n\nPlease try again.");
				}
				PathwayModel pathwayModel = PathwayIOUtil.extractPathwayFromJDOM(jdomDocument, new RDFXMLContext(), 
						getClientTaskStatusSupport());
				PathwayData pathwayData = new PathwayData("Sabio Kinetic Laws", pathwayModel);
				hashTable.put("pathwayData", pathwayData);
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("showing", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				PathwayData pathwayData = (PathwayData) hashTable.get("pathwayData");
				if (pathwayData != null) {
					setSelectedObjects(new Object[] {pathwayData});
				}
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, true, true, null);
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
	}
}
