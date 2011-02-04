package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.relationship.RelationshipModel;
import org.vcell.relationship.RelationshipObject;
import org.vcell.sybil.gui.pcsearch.test.PCKeywordQueryPanel;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.http.uniprot.UniProtConstants;
import org.vcell.sybil.util.miriam.XRefToURN;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import uk.ac.ebi.miriam.lib.MiriamLink;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamResource;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.model.SpeciesContext;
/**
 * Insert the type's description here.
 * Creation date: (2/3/2003 2:07:01 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class SpeciesPropertiesPanel extends DocumentEditorSubPanel {
	private SpeciesContext fieldSpeciesContext = null;
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	private BioModelEditorPathwayTableModel tableModel = null; 
	
	private JTextArea annotationTextArea;
	private JButton pathwayDBJButton = null;
	private JSortTable table; 
	private JLabel pathwayLinkLabel = null; 
	private JTextArea pathwayLinkTextArea = null; 
	private JButton pathwayObjectJButton = null; 
	private JTextField textFieldSearch = null;
	private JEditorPane PCLinkValueEditorPane = null;
	private JTextField nameTextField = null;
	
	public void saveSelectedXRef(final XRef selectedXRef, final MIRIAMQualifier miriamQualifier) {
		AsynchClientTask task1 = new AsynchClientTask("retrieving metadata", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				String urn = XRefToURN.createURN(selectedXRef.db(), selectedXRef.id());
				try {
					MiriamManager miriamManager = bioModel.getModel().getVcMetaData().getMiriamManager();
					MiriamResource resource = miriamManager.createMiriamResource(urn);
					String urnstr = resource.getMiriamURN(); 
					if (urnstr != null && urnstr.toLowerCase().contains("uniprot")) {
						String prettyName = UniProtConstants.getNameFromID(urnstr);	
						if (prettyName != null) {
							miriamManager.setPrettyName(resource, prettyName);
						}
					}

					Set<MiriamResource> miriamResources = new HashSet<MiriamResource>();
					miriamResources.add(resource);
					miriamManager.addMiriamRefGroup(getSpeciesContext().getSpecies(), miriamQualifier, miriamResources);
					
					MiriamLink link = new MiriamLink();
					if (!link.isLibraryUpdated()) {
						System.err.println("MirianLink library is not up to date!");
					}
					String pcLink = resource.getMiriamURN();
					if (pcLink != null && pcLink.length() > 0) {
						String[] locations = link.getLocations(pcLink);
						if (locations != null){
							for(String url : locations) {
								try {
									miriamManager.addStoredCrossReferencedLink(resource, new URL(url));
								} catch (MalformedURLException e) {
									e.printStackTrace(System.out);
								}
							}
						}
					}
				} catch (URNParseFailureException e) {
					e.printStackTrace();
					DialogUtils.showErrorDialog(SpeciesPropertiesPanel.this, e.getMessage());
				}
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("displaying metadata", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				updatePCLink();
			}
		};
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] { task1, task2 });
	}

	private void updatePCLink() {
		try {
			StringBuffer buffer = new StringBuffer("<html>");
			MiriamManager miriamManager = bioModel.getModel().getVcMetaData().getMiriamManager();
			Map<MiriamRefGroup,MIRIAMQualifier> refGroups = miriamManager.getAllMiriamRefGroups(getSpeciesContext().getSpecies());
			if (refGroups != null && refGroups.size()>0) {
				for (MiriamRefGroup refGroup : refGroups.keySet()){
					Set<MiriamResource> miriamResources = refGroup.getMiriamRefs();
					for (MiriamResource resource : miriamResources){
						String urn = resource.getMiriamURN();
						String preferredName = ""; 
						if (urn != null && urn.length() > 0) {
							String prettyName = miriamManager.getPrettyName(resource);
							if (prettyName != null) {
								preferredName = "[" + prettyName + "]";	
							}
							String prettyResourceName = urn.replaceFirst("urn:miriam:", "");
							buffer.append("&#x95;&nbsp;" + prettyResourceName + "&nbsp;<b>" + preferredName + "</b><br>");
							List<URL> linkURLs = miriamManager.getStoredCrossReferencedLinks(resource);
							for (URL url : linkURLs) {
								buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;-&nbsp;<a href=\"" + url.toString() + "\">" + url.toString() + "</a><br>");
							}
						}
					}
				}
			}
			buffer.append("</html>");
			getPCLinkValueEditorPane().setText(buffer.toString());
			getPCLinkValueEditorPane().setCaretPosition(0);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}

	}

	private class EventHandler implements java.awt.event.ActionListener, HyperlinkListener, FocusListener, PropertyChangeListener, ListSelectionListener, DocumentListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getPathwayDBbutton()) { 
				showPCKeywordQueryPanel();
			} else if (e.getSource() == getPathwayObjectbutton()) {
				
				if(bioModel.getPathwayModel() == null){
					changeFreeTextPathwayLink();
					changeFreeTextAnnotation();
				}else{
					changeFreeTextAnnotation();
					changeFreeTextPathwayLink();
					linkToBioPaxObjects();
				}
			} else if (e.getSource() == nameTextField) {
				changeName();
			}
		};
		// @Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == EventType.ACTIVATED) {
				URL link = e.getURL();
				if (link != null) {
					DialogUtils.browserLauncher(SpeciesPropertiesPanel.this, link.toExternalForm(), "failed to launch", false);
				}
			}
		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == annotationTextArea) {
				changeFreeTextAnnotation();
			} else if (e.getSource() == pathwayLinkTextArea) {
				changeFreeTextPathwayLink();
			} else if (e.getSource() == nameTextField) {
				changeName();
			}
		}
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == fieldSpeciesContext) {
				updateInterface();
				changeFreeTextPathwayLink();
			}
		}
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				getPathwayObjectbutton().setEnabled(table.getSelectedRowCount() > 0);
			}
		}
		public void insertUpdate(DocumentEvent e) {
			searchTable();
		}
		public void removeUpdate(DocumentEvent e) {
			searchTable();
		}
		public void changedUpdate(DocumentEvent e) {
			searchTable();
		}
	}

/**
 * EditSpeciesDialog constructor comment.
 */
public SpeciesPropertiesPanel() {
	super();
	initialize();
}

private BioModel getBioModel() {
	return bioModel;
}

/**
 * Gets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @return The speciesContext property value.
 * @see #setSpeciesContext
 */
public SpeciesContext getSpeciesContext() {
	return fieldSpeciesContext;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}

/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	annotationTextArea.addFocusListener(eventHandler);
	nameTextField.addFocusListener(eventHandler);
	pathwayLinkTextArea.addFocusListener(eventHandler);
	getPathwayObjectbutton().addActionListener(eventHandler);
	getPathwayDBbutton().addActionListener(eventHandler);
	getPCLinkValueEditorPane().addHyperlinkListener(eventHandler);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("SpeciesEditorPanel");
		setLayout(new GridBagLayout());
		
		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		nameTextField.addActionListener(eventHandler);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 4;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel label = new JLabel("<html><u>Select only one species to edit properties</u></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;		
		label = new JLabel("Species Name");
		add(label, gbc);
		
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 3;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_START;		
		add(nameTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Annotation"), gbc);

		annotationTextArea = new javax.swing.JTextArea("", 1, 30);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setEditable(false);
		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.2;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(jsp, gbc);

		gridy ++;
		pathwayLinkLabel = new JLabel();
		pathwayLinkLabel.setName("pathwayLinkLabel");
		pathwayLinkLabel.setText("<html><center>Associated<br>BioPax<br>Objects</center></html>");
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(pathwayLinkLabel, gbc);
		
		
		pathwayLinkTextArea = new javax.swing.JTextArea("", 1, 30);
		pathwayLinkTextArea.setLineWrap(false);
		pathwayLinkTextArea.setWrapStyleWord(true);
		pathwayLinkTextArea.setEditable(false);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.2;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		JScrollPane pathwayLinkScollPane = new JScrollPane(pathwayLinkTextArea);
		add(pathwayLinkScollPane, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = gridy;
		add(getPathwayObjectbutton(), gbc);
	
		table = new JSortTable();
		tableModel = new BioModelEditorPathwayTableModel();
		table.setModel(tableModel);
		table.disableUneditableForeground();
		table.getSelectionModel().addListSelectionListener(eventHandler);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 0.2;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(table.getEnclosingScrollPane(), gbc);
		
		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);

		textFieldSearch = new JTextField(15);
		textFieldSearch.addActionListener(eventHandler);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0;
		gbc.gridx = 2; 
		gbc.gridy = gridy;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);
		add(textFieldSearch, gbc);
	
		setBackground(Color.white);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void changeFreeTextAnnotation() {
	try{
		if (getSpeciesContext() == null) {
			return;
		}
		// set text from annotationTextField in free text annotation for species in vcMetaData (from model)
		if(bioModel.getModel() != null && bioModel.getModel().getVcMetaData() != null){
			VCMetaData vcMetaData = bioModel.getModel().getVcMetaData();
			vcMetaData.setFreeTextAnnotation(getSpeciesContext().getSpecies(), annotationTextArea.getText());	
		}
	} catch(Exception e){
		e.printStackTrace(System.out);
		PopupGenerator.showErrorDialog(this,"Edit Species Error\n"+e.getMessage(), e);
	}
}

public synchronized void addActionListener(ActionListener l) {
    listenerList.add(ActionListener.class, l);
}

private void searchTable() {
	String searchText = textFieldSearch.getText();
	tableModel.setSearchText(searchText);
}

public void setBioModel(BioModel model) {
	bioModel = model;
}

/**
 * Sets the speciesContext property (cbit.vcell.model.SpeciesContext) value.
 * @param newValue The new value for the property.
 * @see #getSpeciesContext
 */
void setSpeciesContext(SpeciesContext newValue) {
	if (fieldSpeciesContext == newValue) {
		return;
	}
	SpeciesContext oldValue = fieldSpeciesContext;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(eventHandler);
	}
	// commit the changes before switch to another species
	changeName();
	changeFreeTextAnnotation();
	changeFreeTextPathwayLink(); 
	
	fieldSpeciesContext = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(eventHandler);
	}
	updateInterface();
}

private void showPCKeywordQueryPanel() {
	 PCKeywordQueryPanel aPCKeywordQueryPanel = new PCKeywordQueryPanel();
	 int returnVal = DialogUtils.showComponentOKCancelDialog(this, aPCKeywordQueryPanel, "Search External Database");
	 
	 if (returnVal == JOptionPane.OK_OPTION) {
		 saveSelectedXRef(aPCKeywordQueryPanel.getSelectedXRef(),aPCKeywordQueryPanel.getMiriamQualifier());
	 }
}

private void refreshImportBioPaxObjectTable() {
	if (bioModel.getPathwayModel() == null) {
		return;
	}
	PathwayModel selectedPhysicalEntities = selectedPhysicalEntities(bioModel.getPathwayModel());
	tableModel.setPathwayModel(selectedPhysicalEntities);
	GuiUtils.flexResizeTableColumns(table);
}

private PathwayModel selectedPhysicalEntities(PathwayModel pathwayModel) {
	PathwayModel pModel = new PathwayModel();
	for(BioPaxObject bpObject : pathwayModel.getBiopaxObjects()){
		if(bpObject instanceof PhysicalEntity){
			pModel.add(bpObject);
		}
	}
	return pModel;
}

// link the selected BioPax Objects to the Vcell species
private void linkToBioPaxObjects(){
	if (fieldSpeciesContext == null){
		return;
	}else{
		for (int i = 0; i < table.getRowCount(); i ++) {
			EntitySelectionTableRow entitySelectionTableRow = tableModel.getValueAt(i);
			if (entitySelectionTableRow.selected()) { 
				RelationshipObject reObject = new RelationshipObject();
				reObject.setBioPaxObjecte(entitySelectionTableRow.getBioPaxObject());
				reObject.setSpecies(fieldSpeciesContext.getSpecies());
				bioModel.getRelationshipModel().addRelationshipObject(reObject);
			}
		}
		changeFreeTextAnnotation();
		changeFreeTextPathwayLink(); 
	}
}

/**
 * Comment
 */
private void updateInterface() {
	boolean bNonNullSpeciesContext = fieldSpeciesContext != null && bioModel != null;
	annotationTextArea.setEditable(bNonNullSpeciesContext);
	nameTextField.setEditable(bNonNullSpeciesContext);
	pathwayDBJButton.setEnabled(bNonNullSpeciesContext);
	pathwayObjectJButton.setEnabled(bNonNullSpeciesContext && (table.getSelectedRowCount() > 0));
	if (bNonNullSpeciesContext) {
		nameTextField.setText(getSpeciesContext().getName());
		annotationTextArea.setText(bioModel.getModel().getVcMetaData().getFreeTextAnnotation(getSpeciesContext().getSpecies()));
		changeFreeTextPathwayLink();
		updatePCLink();		
	} else {
		annotationTextArea.setText(null);
		getPCLinkValueEditorPane().setText(null);
		nameTextField.setText(null);
	}
}

	// add relationship to BioPax objects
	private JButton getPathwayObjectbutton() {
		if (pathwayObjectJButton == null) {
			try {
				pathwayObjectJButton = new javax.swing.JButton();
				pathwayObjectJButton.setName("pathwayObjectJButton");
				pathwayObjectJButton.setText("<html><center>Add Links to<br>BioPax Objects</center></html>");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return pathwayObjectJButton;
	}

private JButton getPathwayDBbutton() {
	if (pathwayDBJButton == null) {
		try {
			pathwayDBJButton = new javax.swing.JButton();
			pathwayDBJButton.setName("pathwayDBJButton");
			pathwayDBJButton.setText("<html><center>Add Links<br> to Databases</center></html>");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pathwayDBJButton;
}

	private JEditorPane getPCLinkValueEditorPane() {
		if (PCLinkValueEditorPane == null) {
			PCLinkValueEditorPane = new JEditorPane();
			PCLinkValueEditorPane.setContentType("text/html");
			PCLinkValueEditorPane.setEditable(false);
			PCLinkValueEditorPane.setBackground(UIManager.getColor("TextField.inactiveBackground"));
			PCLinkValueEditorPane.setText(null);
		}
		return PCLinkValueEditorPane;
	}

	private void changeName() {
		if (fieldSpeciesContext == null) {
			return;
		}
		String newName = nameTextField.getText();
		if (newName == null || newName.length() == 0) {
			nameTextField.setText(getSpeciesContext().getName());
			return;
		}
		if (newName.equals(fieldSpeciesContext.getName())) {
			return;
		}
		try {
			getSpeciesContext().setName(newName);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
			DialogUtils.showErrorDialog(SpeciesPropertiesPanel.this, e1.getMessage());
		}
	}

	// display linked biopax objects in JTextArea
	private void changeFreeTextPathwayLink() {
		bioModel = getBioModel();
		if(bioModel == null){
			pathwayLinkTextArea.setText("BioModel is null!");
		}else{
			String linkedBioPaxObjects = showLinkedBioPaxObjects();
			if(linkedBioPaxObjects == null)linkedBioPaxObjects = "None";
			pathwayLinkTextArea.setText(linkedBioPaxObjects);
			if(bioModel.getPathwayModel().size() == 0){
				pathwayLinkTextArea.setToolTipText("Import selected pathway objects first before adding links!");
				textFieldSearch.setText(null);
				textFieldSearch.setEditable(false);
			}else{
				pathwayLinkTextArea.setToolTipText("Select Biopax objects to link from the table.");
				textFieldSearch.setEditable(true);
			}
		}
	}

	// display the linked biopax objects 
	public String showLinkedBioPaxObjects(){
		bioModel = getBioModel();
		String linkedBioPaxObjectContext = null;
		if(bioModel != null){
  			if(fieldSpeciesContext != null){
  				if(fieldSpeciesContext.getSpecies() != null && bioModel.getRelationshipModel() != null 
  						&& bioModel.getRelationshipModel().getRelationshipObjects(fieldSpeciesContext.getSpecies()) != null){
  					if(bioModel.getRelationshipModel().getRelationshipObjects(fieldSpeciesContext.getSpecies()).size() >0){
						linkedBioPaxObjectContext = "";
						for(RelationshipObject reObject : bioModel.getRelationshipModel().getRelationshipObjects(fieldSpeciesContext.getSpecies())){
							if(((PhysicalEntity)reObject.getBioPaxObject()).getName().size() > 0){
								linkedBioPaxObjectContext += "\n- "+((PhysicalEntity)reObject.getBioPaxObject()).getName().get(0);
							}else if(((PhysicalEntity)reObject.getBioPaxObject()).getID() != null){
								linkedBioPaxObjectContext += "\n- "+((PhysicalEntity)reObject.getBioPaxObject()).getID();
							}else{
								linkedBioPaxObjectContext += "\n- Unnamed BioPax Object";
							}
						}
					}
				}
			}
		}
		if(linkedBioPaxObjectContext != null){
			linkedBioPaxObjectContext = linkedBioPaxObjectContext.substring(1);
		}
		return linkedBioPaxObjectContext;
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1) {
			return;
		}
		if (selectedObjects[0] instanceof SpeciesContext) {
			setSpeciesContext((SpeciesContext) selectedObjects[0]);
			refreshImportBioPaxObjectTable();
		} else {
			setSpeciesContext(null);
		}		
	}

}