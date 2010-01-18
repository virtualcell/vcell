package cbit.vcell.xml.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.xml.XMLMetaData;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.Structure;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.gui.MiriamTreeModel.IdentifiableNode;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;

import com.hp.hpl.jena.rdf.model.Statement;

public class MIRIAMAnnotationEditor extends JPanel implements ActionListener{
	public static final String[] MIRIAM_ANNOT_COLUMNS = new String[] {"Model Component","Component Name","Annotation Scheme","Annotation Qualifier","Authoritative Identifier"};
	//SBML tag types
	private static String TYPE_ID_SPECIES = "species";
	private static String TYPE_ID_MODEL = "model";
	private static String TYPE_ID_REACTION = "reaction";
	private static String TYPE_ID_COMPARTMENT = "compartment";
	String[] DATE_QUALIFIERS = new String[] {
			"Created",
			"Valid",
			"Available",
			"Issued",
			"Modified",
			"Date Accepted",
			"Date Copyrighted",
			"Date Submitted"
	};
	String[] BIOMODNET_QUALIFERS = new String[]{
			"hasPart (bio)",
			"hasVersion (bio)",
			"is (bio)",
			"is (model)",
			"isDescribedBy (bio)",
			"isDescribedBy (model)",
			"isHomologTo (bio)",
			"isPartOf (bio)",
			"isVersionOf (bio)"
	};
	
	String[][] PROVIDER_TYPES = new String[][]{
			new String[] {TYPE_ID_SPECIES},
			new String[] {TYPE_ID_SPECIES},
			new String[] {TYPE_ID_MODEL},
			new String[] {TYPE_ID_REACTION},
			new String[] {TYPE_ID_MODEL},
			new String[] {TYPE_ID_SPECIES},
			new String[] {TYPE_ID_REACTION},
			new String[] {TYPE_ID_SPECIES},
			new String[] {TYPE_ID_REACTION},
			new String[] {TYPE_ID_SPECIES},
			new String[] {TYPE_ID_MODEL,TYPE_ID_COMPARTMENT,TYPE_ID_SPECIES,TYPE_ID_REACTION},
			new String[] {TYPE_ID_SPECIES},
			new String[] {TYPE_ID_MODEL,TYPE_ID_REACTION},
			new String[] {TYPE_ID_REACTION},
			new String[] {TYPE_ID_MODEL},
			new String[] {TYPE_ID_MODEL},
			new String[] {TYPE_ID_MODEL},
			new String[] {TYPE_ID_MODEL,TYPE_ID_REACTION},
			new String[] {TYPE_ID_MODEL},
			new String[] {TYPE_ID_SPECIES},
			new String[] {TYPE_ID_MODEL},
	};
	String[] KNOWN_IDENTITY_PROVIDERS = new String[] {
			"http://pir.georgetown.edu/pirsf/",
			"http://www.bind.ca/",
			"http://www.doi.org/",
			"http://www.ebi.ac.uk/IntEnz/",
			"http://www.ebi.ac.uk/biomodels/",
			"http://www.ebi.ac.uk/chebi/",
			"http://www.ebi.ac.uk/intact/",
			"http://www.ebi.ac.uk/interpro/",
			"http://www.ec-code.org/",
			"http://www.ensembl.org/",
			"http://www.geneontology.org/",
			"http://www.genome.jp/kegg/compound/",
			"http://www.genome.jp/kegg/pathway/",
			"http://www.genome.jp/kegg/reaction/",
			"http://www.ncbi.nlm.nih.gov/PubMed/",
			"http://www.ncbi.nlm.nih.gov/Taxonomy/",
			"http://www.pubmed.gov/",
			"http://www.reactome.org/",
			"http://www.taxonomy.org/",
			"http://www.uniprot.org/",
			"http://www.who.int/classifications/icd/"
	};

	private JTree jTreeMIRIAM = null;
	private JScrollPane jScrollPane = null;
	private JPanel jPanel = null;
	private JButton jButtonEdit = null;
	private JButton jButtonAdd = null;
	private JButton jButtonDelete = null;
	
	private Vector<ActionListener> actionListenerV = new Vector<ActionListener>();
	public static final String ACTION_EDIT ="Edit Annotation ...";  //  @jve:decl-index=0:
	public static final String ACTION_ADD ="Add Annotation ...";  //  @jve:decl-index=0:
	public static final String ACTION_DELETE ="Delete Annotation";
	
	private JPanel jPanelNewIdentifier = null;  //  @jve:decl-index=0:visual-constraint="86,363"
	private JComboBox jComboBoxURI = null;
	private JLabel jLabel2 = null;
	private JTextField jTextFieldFormalID = null;
	private JLabel jLabel3 = null;
	private JComboBox jComboBoxQualifier = null;
	private JLabel jLabel4 = null;
	private JPanel jPanelTimeUTC = null;  //  @jve:decl-index=0:visual-constraint="88,417"
	private JTextField jTextFieldTimeUTC = null;
	private JLabel jLabelTimeUTCEG = null;
	private JLabel jLabelTimeUTC = null;
	private JComboBox jComboBoxTimeUTCType = null;
	private JPanel jPanelCreator = null;  //  @jve:decl-index=0:visual-constraint="646,75"
	private JLabel jLabelGiven = null;
	private JTextField jTextFieldGiven = null;
	private JLabel jLabelFamily = null;
	private JLabel jLabelEmail = null;
	private JLabel JLabelOrganization = null;
	private JTextField jTextFieldFamily = null;
	private JTextField jTextFieldEmail = null;
	private JTextField jTextFieldOrganization = null;
	
	private VCMetaData vcMetaData = null;
	private TreeMap<Identifiable, List<Statement>> miriamDescrHeir = null;
	/**
	 * This method initializes 
	 * 
	 */
	public MIRIAMAnnotationEditor() {
		super();
		initialize();
		initQualifiers();
	}

	public void actionPerformed(ActionEvent e) {
//		if(jTableMIRIAM.getCellEditor() != null){
//			jTableMIRIAM.getCellEditor().stopCellEditing();
//		}
		
		for (int i = 0; i < actionListenerV.size(); i++) {
			actionListenerV.get(i).actionPerformed(e);
		}
	}

	public void addActionListener(ActionListener actionListiner){
		if(!actionListenerV.contains(actionListiner)){
			actionListenerV.add(actionListiner);
		}
	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.fill = GridBagConstraints.NONE;
        gridBagConstraints5.insets = new Insets(4, 4, 4, 4);
        gridBagConstraints5.gridy = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = GridBagConstraints.BOTH;
        gridBagConstraints1.weighty = 1.0;
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(627, 333));
        this.add(getJScrollPane(), gridBagConstraints1);
        this.add(getJPanel(), gridBagConstraints5);
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTreeMIRIAM());
		}
		return jScrollPane;
	}

	private JTree getJTreeMIRIAM() {
		if (jTreeMIRIAM == null) {
			try {
				DefaultTreeSelectionModel ivjLocalSelectionModel;
				ivjLocalSelectionModel = new DefaultTreeSelectionModel();
				ivjLocalSelectionModel.setSelectionMode(1);
				jTreeMIRIAM = new JTree();
				jTreeMIRIAM.setName("JTree1");
				jTreeMIRIAM.setToolTipText("");
				jTreeMIRIAM.setBounds(0, 0, 357, 405);
				jTreeMIRIAM.setMinimumSize(new java.awt.Dimension(100, 72));
				jTreeMIRIAM.setSelectionModel(ivjLocalSelectionModel);
				jTreeMIRIAM.setRowHeight(0);
				
				// Add cellRenderer
				DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer() {
					@Override
					public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
							boolean leaf, int row, boolean hasFocus) {
						super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
								row, hasFocus);
						setBackgroundSelectionColor(Color.LIGHT_GRAY);
						if (value instanceof LinkNode) {
							LinkNode ln = (LinkNode)value;
							String predicate = ln.getPredicatePrefix();
							String link = ln.getLink();
							String text = ln.getText();
							if (link != null) {
								setToolTipText("Double-click to open link");
								setText("<html><font color='black'>" + predicate + "</font>" + 
								"&nbsp;&nbsp;&nbsp;&nbsp;<a href=" + link + ">" + text + "</a></html>");
							} 
						} else {
							setToolTipText(null);
							String text = getText();
							setText("<html><font color='black'>" + text + "</font></html>");
						}
						return this;
					}
				};
				jTreeMIRIAM.setCellRenderer(dtcr);
				
//				// add tree selection listener
//				class MiriamTreeSelectionListener implements TreeSelectionListener {			
//					public void valueChanged(TreeSelectionEvent e) {
//						Object node = jTreeMIRIAM.getLastSelectedPathComponent();
//						if (node instanceof LinkNode) {
//							String link = ((LinkNode)node).getLink();
//							if (link != null) {
//								DialogUtils.browserLauncher(jTreeMIRIAM, link, "failed to launch", false);
//							}
//						}
//					}
//				};
//				jTreeMIRIAM.addTreeSelectionListener(new MiriamTreeSelectionListener());

				MouseListener mouseListener = new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						if(e.getClickCount() == 2) {
							Object node = jTreeMIRIAM.getLastSelectedPathComponent();
							if (node instanceof LinkNode) {
								String link = ((LinkNode)node).getLink();
								if (link != null) {
									DialogUtils.browserLauncher(jTreeMIRIAM, link, "failed to launch", false);
								}
							}
						}
					} 
				};
				jTreeMIRIAM.addMouseListener(mouseListener);

			} catch (java.lang.Throwable ivjExc) {
				ivjExc.printStackTrace(System.out);
			}
		}
		return jTreeMIRIAM;
	}
	
	/**
	 * This method initializes jButtonEdit
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonEdit() {
		if (jButtonEdit == null) {
			jButtonEdit = new JButton();
			jButtonEdit.setText(ACTION_EDIT);
			jButtonEdit.setEnabled(false);
			jButtonEdit.addActionListener(this);
		}
		return jButtonEdit;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 3;
			gridBagConstraints4.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jPanel = new JPanel();
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[] {0,0,0,0,0,7};
			jPanel.setLayout(gridBagLayout);
			jPanel.add(getJButtonEdit(), gridBagConstraints);
			jPanel.add(getJButtonAdd(), gridBagConstraints2);
			jPanel.add(getJButtonDelete(), gridBagConstraints4);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButtonAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setText(ACTION_ADD);
			jButtonAdd.addActionListener(this);
		}
		return jButtonAdd;
	}

	/**
	 * This method initializes jButtonDelete	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonDelete() {
		if (jButtonDelete == null) {
			jButtonDelete = new JButton();
			jButtonDelete.setText(ACTION_DELETE);
			jButtonDelete.addActionListener(this);
		}
		return jButtonDelete;
	}

	public Identifiable getSelectedIdentifiable(){
		Object treeNode = jTreeMIRIAM.getLastSelectedPathComponent();
		if (treeNode instanceof IdentifiableNode) {
			Identifiable identifiable = ((IdentifiableNode)treeNode).getIdentifiable();
			return identifiable;
		} else if (treeNode instanceof LinkNode) {
			// treeNode = jTreeMIRIAM.getSelectionPath().getParentPath().getPathComponent(0);
			return null;
		}
		return null;
	}
	
	public List<Statement> getSelectedStatements(){
		Object treeNode = jTreeMIRIAM.getLastSelectedPathComponent();
		if (treeNode instanceof IdentifiableNode) {
			Identifiable identifiable = ((IdentifiableNode)treeNode).getIdentifiable();
			if (miriamDescrHeir != null) {
				List<Statement> stmtsList = miriamDescrHeir.get(identifiable);
				return stmtsList;
			} else {
				DialogUtils.showErrorDialog(this, "No statement list for " + identifiable.toString());
			}
		}
		return null;
	}
	
	public void setBioModel(BioModel bioModel) {
		vcMetaData = bioModel.getVCMetaData();
		createMiriamDescriptionHeirarchy(bioModel);

		// set tree model on jTableMIRIAM here, since we have access to miriamDescrHeir here
		jTreeMIRIAM.setModel(new MiriamTreeModel(new DefaultMutableTreeNode("MODEL ANNOTATION",true), miriamDescrHeir, vcMetaData));
	}

	private void editDescriptiveHeirarchy(List<Statement> descrHeir,String newValue){
		if(descrHeir == null){
			return;
		}
		if(descrHeir instanceof TextDescriptiveHeirarchy){
			((TextDescriptiveHeirarchy)descrHeir).getText().setText(newValue);
		}else if(descrHeir instanceof AttributeDescriptiveHeirarchy){
			((AttributeDescriptiveHeirarchy)descrHeir).getAttribute().setValue(newValue);
		}else{
			throw new IllegalAccessError("Error editing unknown DescriptiveHeirarchy type.");
		}
	}
	
	private URI getSelectedURI(){
		// String detailInfo = (String)getJTableMIRIAM().getValueAt(jTableMIRIAM.getSelectedRow(), 4);
		String detailInfo = "ok.........";
		if (detailInfo != null) {
			System.out.println(detailInfo);
			URL detailURL = null;
			try {
				detailURL = new URL(detailInfo);
				return new URI(detailURL.toString());
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	/**
	 * This method initializes jPanelNewIdentifier	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelNewIdentifier() {
		if (jPanelNewIdentifier == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.insets = new Insets(0, 20, 0, 4);
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.gridx = 4;
			jLabel4 = new JLabel();
			jLabel4.setText("Qualifier");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints7.gridx = 5;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 0.0;
			gridBagConstraints7.insets = new Insets(4, 0, 4, 4);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.insets = new Insets(0, 20, 0, 4);
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.gridx = 2;
			jLabel3 = new JLabel();
			jLabel3.setText("Immortal ID");
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints51.gridy = 0;
			gridBagConstraints51.weightx = 1.0;
			gridBagConstraints51.gridx = 3;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.insets = new Insets(0, 4, 0, 4);
			gridBagConstraints41.gridy = 0;
			gridBagConstraints41.gridx = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Identitiy Provider");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.NONE;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 0.0D;
			gridBagConstraints3.insets = new Insets(4, 0, 4, 0);
			jPanelNewIdentifier = new JPanel();
			jPanelNewIdentifier.setLayout(new GridBagLayout());
			jPanelNewIdentifier.setPreferredSize(new Dimension(725, 37));
			jPanelNewIdentifier.setBorder(BorderFactory.createLineBorder(SystemColor.windowBorder, 2));
			jPanelNewIdentifier.add(getJComboBoxURI(), gridBagConstraints3);
			jPanelNewIdentifier.add(jLabel2, gridBagConstraints41);
			jPanelNewIdentifier.add(getJTextFieldFormalID(), gridBagConstraints51);
			jPanelNewIdentifier.add(jLabel3, gridBagConstraints6);
			jPanelNewIdentifier.add(getJComboBoxQualifier(), gridBagConstraints7);
			jPanelNewIdentifier.add(jLabel4, gridBagConstraints12);
		}
		return jPanelNewIdentifier;
	}

	/**
	 * This method initializes jComboBoxURI	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxURI() {
		if (jComboBoxURI == null) {
			jComboBoxURI = new JComboBox();
		}
		return jComboBoxURI;
	}

	/**
	 * This method initializes jTextFieldFormalID	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldFormalID() {
		if (jTextFieldFormalID == null) {
			jTextFieldFormalID = new JTextField();
			jTextFieldFormalID.setText("NewID");
		}
		return jTextFieldFormalID;
	}

	/**
	 * This method initializes jComboBoxQualifier	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxQualifier() {
		if (jComboBoxQualifier == null) {
			jComboBoxQualifier = new JComboBox();
		}
		return jComboBoxQualifier;
	}

	private void initQualifiers(){
//		jListFormalID.setListData(new String[] {"No Formal Identifiers currently defined"});
		((DefaultComboBoxModel)getJComboBoxURI().getModel()).removeAllElements();
		for (int i = 0; i < KNOWN_IDENTITY_PROVIDERS.length; i++) {
			((DefaultComboBoxModel)getJComboBoxURI().getModel()).addElement(KNOWN_IDENTITY_PROVIDERS[i]);
		}
		for (int i = 0; i < BIOMODNET_QUALIFERS.length; i++) {
			((DefaultComboBoxModel)getJComboBoxQualifier().getModel()).addElement(BIOMODNET_QUALIFERS[i]);
		}
		
		((DefaultComboBoxModel)getJComboBoxTimeUTCType().getModel()).removeAllElements();
		for (int i = 0; i < DATE_QUALIFIERS.length; i++) {
			((DefaultComboBoxModel)getJComboBoxTimeUTCType().getModel()).addElement(DATE_QUALIFIERS[i]);
		}
	}
	public void addIdentifierDialog() throws URISyntaxException{
		if(PopupGenerator.showComponentOKCancelDialog(MIRIAMAnnotationEditor.this, getJPanelNewIdentifier(), "Define New Formal Identifier") == JOptionPane.OK_OPTION){
			String propertyID = (String)jComboBoxQualifier.getSelectedItem();
			URI propertyNamespace = null;
			if(propertyID.endsWith("(bio)")){
				propertyNamespace = new URI(XMLTags.BMBIOQUAL_NAMESPACE_URI);
			}else if(propertyID.endsWith("(model)")){
				propertyNamespace = new URI(XMLTags.BMMODELQUAL_NAMESPACE_URI);
			}
			propertyID = propertyID.substring(0,propertyID.indexOf(" ("));
			String objectNamespace = (String)jComboBoxURI.getSelectedItem();
			String objectID = jTextFieldFormalID.getText();
			vcMetaData.addRDFStatement(getSelectedIdentifiable(),new URI(propertyNamespace+"/"+propertyID),new URI(objectNamespace+"/"+objectID));
		}
	}

	
	public void addTimeUTCDialog(){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		getJTextFieldTimeUTC().setText(sdf.format(new Date()));
		if(PopupGenerator.showComponentOKCancelDialog(MIRIAMAnnotationEditor.this, getJPanelTimeUTC(), "Define New Date") == JOptionPane.OK_OPTION){
			vcMetaData.addDateToAnnotation(getSelectedIdentifiable(),
					getJTextFieldTimeUTC().getText(),
					(String)getJComboBoxTimeUTCType().getSelectedItem());
//			String qualifierName = (String)jComboBoxQualifier.getSelectedItem();
//			URI qualifierURI = null;
//			if(qualifierName.endsWith("(bio)")){
//				qualifierURI = new URI(XMLTags.BMBIOQUAL_NAMESPACE_URI);
//			}else if(qualifierName.endsWith("(model)")){
//				qualifierURI = new URI(XMLTags.BMMODELQUAL_NAMESPACE_URI);
//			}
//			qualifierName = qualifierName.substring(0,qualifierName.indexOf(" ("));
//			Element newID =
//				MIRIAMHelper.createRDFIdentifier((String)jComboBoxURI.getSelectedItem(), jTextFieldFormalID.getText());
//			MIRIAMHelper.addIdentifierToAnnotation(
//					newID,
//					getSelectedMIRIAMAnnotatable().getMIRIAMAnnotation(),
//					qualifierName,
//					qualifierURI);
		}
	}

	public void addCreatorDialog(){
		if(PopupGenerator.showComponentOKCancelDialog(MIRIAMAnnotationEditor.this, getJPanelCreator(), "Define New Creator") == JOptionPane.OK_OPTION){
			Identifiable identifiable = getSelectedIdentifiable();
			String familyName = getJTextFieldFamily().getText();
			String givenName = getJTextFieldGiven().getText();
			String email = getJTextFieldEmail().getText();
			String organization = getJTextFieldOrganization().getText();
			vcMetaData.addCreatorToAnnotation(identifiable,familyName,givenName,email,organization);
//			MIRIAMHelper.addCreatorToAnnotation(
//					getSelectedMIRIAMAnnotatable(),
//					getJTextFieldFamily().getText(),
//					getJTextFieldGiven().getText(),
//					getJTextFieldEmail().getText(),
//					getJTextFieldOrganization().getText());
			
//			MIRIAMHelper.addDateToAnnotation(
//					getSelectedMIRIAMAnnotatable(),
//					getJTextFieldTimeUTC().getText(),
//					(String)getJComboBoxTimeUTCType().getSelectedItem());
		}
	}

	/**
	 * This method initializes jPanelTimeUTC	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelTimeUTC() {
		if (jPanelTimeUTC == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.NONE;
			gridBagConstraints13.gridy = 2;
			gridBagConstraints13.weightx = 0.0;
			gridBagConstraints13.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints13.gridx = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.fill = GridBagConstraints.NONE;
			gridBagConstraints9.gridy = 0;
			jLabelTimeUTC = new JLabel();
			jLabelTimeUTC.setText("Enter W3C-DTF compliant Data-Time");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints10.gridwidth = 2;
			gridBagConstraints10.fill = GridBagConstraints.NONE;
			gridBagConstraints10.gridy = 1;
			jLabelTimeUTCEG = new JLabel();
			jLabelTimeUTCEG.setText("yyyy-MM-dd'T'HH:mm:ssZ (Z is signed hour offset from GMT)");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 2;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints8.gridx = 1;
			jPanelTimeUTC = new JPanel();
			jPanelTimeUTC.setLayout(new GridBagLayout());
			jPanelTimeUTC.setSize(new Dimension(735, 95));
			jPanelTimeUTC.add(getJTextFieldTimeUTC(), gridBagConstraints8);
			jPanelTimeUTC.add(jLabelTimeUTCEG, gridBagConstraints10);
			jPanelTimeUTC.add(jLabelTimeUTC, gridBagConstraints9);
			jPanelTimeUTC.add(getJComboBoxTimeUTCType(), gridBagConstraints13);
		}
		return jPanelTimeUTC;
	}

	/**
	 * This method initializes jTextFieldTimeUTC	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldTimeUTC() {
		if (jTextFieldTimeUTC == null) {
			jTextFieldTimeUTC = new JTextField();
		}
		return jTextFieldTimeUTC;
	}

	/**
	 * This method initializes jComboBoxTimeUTCType	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxTimeUTCType() {
		if (jComboBoxTimeUTCType == null) {
			jComboBoxTimeUTCType = new JComboBox();
		}
		return jComboBoxTimeUTCType;
	}

	/**
	 * This method initializes jPanelCreator	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelCreator() {
		if (jPanelCreator == null) {
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.gridy = 3;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints22.gridx = 1;
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints20.gridy = 2;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints20.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints19.gridy = 1;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints19.gridx = 1;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints18.anchor = GridBagConstraints.EAST;
			gridBagConstraints18.gridy = 3;
			JLabelOrganization = new JLabel();
			JLabelOrganization.setText("Organization");
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints17.anchor = GridBagConstraints.EAST;
			gridBagConstraints17.gridy = 2;
			jLabelEmail = new JLabel();
			jLabelEmail.setText("Email");
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints16.anchor = GridBagConstraints.EAST;
			gridBagConstraints16.gridy = 1;
			jLabelFamily = new JLabel();
			jLabelFamily.setText("Family Name");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints15.gridy = 0;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints15.gridx = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints14.anchor = GridBagConstraints.EAST;
			gridBagConstraints14.gridy = 0;
			jLabelGiven = new JLabel();
			jLabelGiven.setText("Given Name");
			jPanelCreator = new JPanel();
			jPanelCreator.setLayout(new GridBagLayout());
			jPanelCreator.setSize(new Dimension(331, 147));
			jPanelCreator.add(jLabelGiven, gridBagConstraints14);
			jPanelCreator.add(getJTextFieldGiven(), gridBagConstraints15);
			jPanelCreator.add(jLabelFamily, gridBagConstraints16);
			jPanelCreator.add(jLabelEmail, gridBagConstraints17);
			jPanelCreator.add(JLabelOrganization, gridBagConstraints18);
			jPanelCreator.add(getJTextFieldFamily(), gridBagConstraints19);
			jPanelCreator.add(getJTextFieldEmail(), gridBagConstraints20);
			jPanelCreator.add(getJTextFieldOrganization(), gridBagConstraints22);
		}
		return jPanelCreator;
	}

	/**
	 * This method initializes jTextFieldGiven	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldGiven() {
		if (jTextFieldGiven == null) {
			jTextFieldGiven = new JTextField();
		}
		return jTextFieldGiven;
	}

	/**
	 * This method initializes jTextFieldFamily	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldFamily() {
		if (jTextFieldFamily == null) {
			jTextFieldFamily = new JTextField();
		}
		return jTextFieldFamily;
	}

	/**
	 * This method initializes jTextFieldEmail	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldEmail() {
		if (jTextFieldEmail == null) {
			jTextFieldEmail = new JTextField();
		}
		return jTextFieldEmail;
	}

	/**
	 * This method initializes jTextFieldOrganization	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldOrganization() {
		if (jTextFieldOrganization == null) {
			jTextFieldOrganization = new JTextField();
		}
		return jTextFieldOrganization;
	}

	private void createMiriamDescriptionHeirarchy(final BioModel bioModel){
		miriamDescrHeir =
			new TreeMap<Identifiable, List<Statement>>(
					new Comparator<Identifiable>(){
						public int compare(Identifiable o1, Identifiable o2) {
							VCID vcid1 = bioModel.getVCID(o1);
							VCID vcid2 = bioModel.getVCID(o2);
							return vcid1.toASCIIString().compareTo(vcid2.toASCIIString());
						}}
			);
		List<Statement> statements = vcMetaData.getStatements(bioModel);
		miriamDescrHeir.put(bioModel,statements);

		Species[] speciesArr = bioModel.getModel().getSpecies();
		for (int i = 0; i < speciesArr.length; i++) {
			statements = vcMetaData.getStatements(speciesArr[i]);
			miriamDescrHeir.put(speciesArr[i],statements);
		}
		Structure[] structArr = bioModel.getModel().getStructures();
		for (int i = 0; i < structArr.length; i++) {
			statements = vcMetaData.getStatements(structArr[i]);
			miriamDescrHeir.put(structArr[i],statements);
		}
		ReactionStep[] reactArr = bioModel.getModel().getReactionSteps();
		for (int i = 0; i < reactArr.length; i++) {
			statements = vcMetaData.getStatements(reactArr[i]);
			miriamDescrHeir.put(reactArr[i],statements);
		}
	}
	
	private Vector<DescriptiveHeirarchy> traverse(Vector<DescriptiveHeirarchy> descrHeirV,Object content){
		if(content instanceof Text){
			TextDescriptiveHeirarchy textDescrHeir = null;
			if(((Text)content).getText() != null && ((Text)content).getText().trim().length() > 0){//!((Text)content).getText().equals("\n")){
				textDescrHeir = new TextDescriptiveHeirarchy((Text)content);
				getDescriptiveHeirarchy(textDescrHeir,((Text)content).getParent());
				if(textDescrHeir.getHeirarchy().size() > 0){
					descrHeirV.add(textDescrHeir);
				}
			}
			return descrHeirV;
		}
		if(content instanceof Element){
			List children = ((Element)content).getContent();
			if(children != null && children.size() > 0){
				for(int i=0;i<children.size();i+= 1){
					traverse(descrHeirV,children.get(i));
				}
				return descrHeirV;
			}
//			if(isDescriptiveScheme(((Element)content).getNamespace())){
				List<Attribute> attributes = ((Element)content).getAttributes();
				if(attributes != null && attributes.size() > 0){
					for(int i=0;i<attributes.size();i+= 1){
						AttributeDescriptiveHeirarchy attrDescrHeir =
							new AttributeDescriptiveHeirarchy(attributes.get(i));
						getDescriptiveHeirarchy(attrDescrHeir,attributes.get(i).getParent());
						if(attrDescrHeir.getHeirarchy().size()>0){
							descrHeirV.add(attrDescrHeir);
						}
					}
					return descrHeirV;
				}
//			}
		}
		return descrHeirV;
	}
	
	private void getDescriptiveHeirarchy(DescriptiveHeirarchy descriptiveHeirarchy,Element element){
		if(element != null){
			if(isListElement(element)){
				descriptiveHeirarchy.setListElement(element);
			}
			if(descriptiveHeirarchy.getTypeElement() == null && isTypeElement(element)){
				descriptiveHeirarchy.setTypeElement(element);
			}
			if(isDescriptiveScheme(element.getNamespace())){
				descriptiveHeirarchy.getHeirarchy().add(element);
			}
			getDescriptiveHeirarchy(descriptiveHeirarchy, element.getParent());
		}
	}
	private static boolean isDescriptiveScheme(Namespace nameSpace){
		return
		nameSpace.getURI().equals(XMLTags.VCARD_NAMESPACE_URI) ||
		nameSpace.getURI().equals(XMLTags.DUBCORE_NAMESPACE_URI) ||
		nameSpace.getURI().equals(XMLTags.DUBCORETERMS_NAMESPACE_URI) ||
		nameSpace.getURI().equals(XMLTags.BMBIOQUAL_NAMESPACE_URI) ||
		nameSpace.getURI().equals(XMLTags.BMMODELQUAL_NAMESPACE_URI);
		
	}

	private static boolean isListElement(Element element){
		return
			element.getNamespaceURI().equals(XMLMetaData.rdfNameSpace.getURI())
			&&
			element.getName().equals("li");
	}
	private static boolean isTypeElement(Element element){
		return
			element.getName().equals("model") ||
			element.getName().equals("reaction") ||
			element.getName().equals("species") ||
			element.getName().equals("compartment");
	}

	//	private Vector<IdentifiableMetaData> getTableFormattedData(TreeMap<Identifiable, List<Statement>> miriamDescrHeir){
	//	Vector<IdentifiableMetaData> rowV = new Vector<IdentifiableMetaData>();
	////	if(mirimaDescrHeir.size() > 0){
	//		Set<Identifiable> keys = miriamDescrHeir.keySet();
	//		Iterator<Identifiable> iter = keys.iterator();
	//		while(iter.hasNext()){
	//			Identifiable identifiable = iter.next();
	//			List<Statement> descrHeirV = miriamDescrHeir.get(identifiable);
	//			String modelComponentType = 
	//				(identifiable instanceof BioModel?"BioModel":"")+
	//				(identifiable instanceof Species?"Species":"")+
	//				(identifiable instanceof Structure?"Structure":"")+
	//				(identifiable instanceof ReactionStep?"ReactionStep":"");
	//			String modelComponentName =
	//				(identifiable instanceof BioModel?((BioModel)identifiable).getName():"")+
	//				(identifiable instanceof Species?((Species)identifiable).getCommonName():"")+
	//				(identifiable instanceof Structure?((Structure)identifiable).getName():"")+
	//				(identifiable instanceof ReactionStep?((ReactionStep)identifiable).getName():"");
	//			int descHeirSize = (descrHeirV==null)?(0):descrHeirV.size();
	//			{
	//			IdentifiableMetaData miriamTableRow = new IdentifiableMetaData();
	//			miriamTableRow.statements = null;
	//			miriamTableRow.identifiable = identifiable;
	//			OpenRegistry.OpenEntry registryEntry = vcMetaData.getRegistry().forObject(identifiable);
	//			Resource resource = registryEntry.resource();
	//			miriamTableRow.rowData =
	//				new String[] {
	//					modelComponentType,
	//					modelComponentName+"<"+resource+">",
	////					modelComponentName,
	//					(descHeirSize == 0?"-----":null),
	//					(descHeirSize == 0?"-----":null),
	//					(descHeirSize == 0?"None Defined":null)
	//				};
	//			rowV.add(miriamTableRow);
	//			}
	//			Model rdfModel = vcMetaData.getRdf();
	//			for (int i = 0; i < descHeirSize; i++) {
	//				Statement statement = descrHeirV.get(i);
	//				String[] row = new String[MIRIAM_ANNOT_COLUMNS.length];
	//				Triple triple = statement.asTriple();
	//				row[0] = null;
	//				row[1] = null;
	//				row[2] = triple.getPredicate().getNameSpace();
	//				row[3] = triple.getPredicate().toString().substring(triple.getPredicate().getNameSpace().length());
	//				row[4] = null;
	//				Node objectNode = triple.getObject();
	//				RDFNode objectRDFNode = statement.getObject();
	//				System.out.println("object = "+objectNode.toString());
	//				System.out.println("isBlank() "+objectNode.isBlank());
	//				System.out.println("isConcrete() "+objectNode.isConcrete());
	//				System.out.println("isLiteral() "+objectNode.isLiteral());
	//				System.out.println("isURI() "+objectNode.isURI());
	//				System.out.println("isVariable() "+objectNode.isVariable());
	//				System.out.println("instanceof Resource "+(objectNode instanceof Resource));
	//				int count = 0;
	//				while (count++<30 && !(objectNode.isURI() || objectNode.isLiteral())){
	//					StmtIterator bagQueryIter = rdfModel.listStatements(
	//							(Resource)objectRDFNode, 
	//							rdfModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
	//							rdfModel.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag"));
	//					if (bagQueryIter.hasNext()){ // this is a bag, get contents of bag
	//						final Resource finalObject = (Resource)objectRDFNode; 
	//						Selector bagContentsSelector = new Selector() {
	//							public RDFNode getObject() { return null; }
	//							public Property getPredicate() { return null; }
	//							public Resource getSubject() { return null; }
	//							public boolean isSimple() { return false; }
	//							public boolean test(Statement arg0) {
	//								if (!arg0.getSubject().equals(finalObject)) { return false; }
	//								Property property = arg0.getPredicate();
	//								String propertyString = property.toString();
	//								final String BagMemberPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#_";
	//								if (propertyString.startsWith(BagMemberPrefix)){
	//									try {
	//										String rest = propertyString.substring(BagMemberPrefix.length());
	//										int index = Integer.parseInt(rest);
	//										return true;
	//									}catch (NumberFormatException e){
	//									}
	//								}
	//								return false;
	//							}	
	//						};
	//						StmtIterator bagIter = rdfModel.listStatements(bagContentsSelector);
	//						StringBuffer buffer = new StringBuffer("<html>");
	//						int bagCount=0;
	//						while (bagIter.hasNext()){
	//							Statement stmt = bagIter.nextStatement();
	//							if (bagCount>0){
	//								buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
	//							}
	//							String urn = stmt.getObject().toString();
	//							String link = urn;
	//							String text = urn;
	//							if (urn.startsWith("urn:miriam:biomodels.db:")) {
	//								link = urn.replaceFirst("urn:miriam:biomodels.db:", "http://www.ebi.ac.uk/biomodels-main/");
	//								text = urn.replaceFirst("urn:miriam:biomodels.db:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							} else if (urn.startsWith("urn:miriam:pubmed:")){
	//								link = urn.replaceFirst("urn:miriam:pubmed:", "http://www.ncbi.nlm.nih.gov/pubmed/");
	//								text = urn.replaceFirst("urn:miriam:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							} else if (urn.startsWith("urn:miriam:obo.go:")) {
	//								link = urn.replaceFirst("urn:miriam:obo.go:", "http://www.ebi.ac.uk/ego/GTerm?id=");
	//								text = urn.replaceFirst("urn:miriam:obo.go:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							} else if (urn.startsWith("urn:miriam:biomodels.db:")) {
	//								link = urn.replaceFirst("urn:miriam:biomodels.db:", "http://www.ebi.ac.uk/biomodels-main/");
	//								text = urn.replaceFirst("urn:miriam:biomodels.db:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							} else if (urn.startsWith("urn:miriam:biomodels.db:")) {
	//								link = urn.replaceFirst("urn:miriam:biomodels.db:", "http://www.ebi.ac.uk/biomodels-main/");
	//								text = urn.replaceFirst("urn:miriam:biomodels.db:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							} else if (urn.startsWith("urn:miriam:reactome:")) {
	//								link = urn.replaceFirst("urn:miriam:reactome:", "http://www.reactome.org/cgi-bin/eventbrowser_st_id?FROM_REACTOME=1&ST_ID=");
	//								text = urn.replaceFirst("urn:miriam:reactome:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							} else if (urn.startsWith("urn:miriam:ec-code:")) {
	//								link = urn.replaceFirst("urn:miriam:ec-code:", "http://www.ebi.ac.uk/intenz/query?cmd=SearchEC&ec=");
	//								text = urn.replaceFirst("urn:miriam:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							} else if (urn.startsWith("urn:miriam:taxonomy:")) {
	//								link = urn.replaceFirst("urn:miriam:taxonomy:", "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=");
	//								text = urn.replaceFirst("urn:miriam:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							} else if (urn.startsWith("urn:miriam:interpro:")) {
	//								link = urn.replaceFirst("urn:miriam:interpro:", "http://www.ebi.ac.uk/interpro/DisplayIproEntry?ac=");
	//								text = urn.replaceFirst("urn:miriam:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							} else if (urn.startsWith("urn:miriam:kegg.pathway:")) {
	//								link = urn.replaceFirst("urn:miriam:kegg.pathway:", "http://www.genome.ad.jp/dbget-bin/www_bget?pathway+");
	//								text = urn.replaceFirst("urn:miriam:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							}  else if (urn.startsWith("urn:miriam:uniprot:")) {
	//								link = urn.replaceFirst("urn:miriam:uniprot:", "http://www.ebi.uniprot.org/entry/");
	//								text = urn.replaceFirst("urn:miriam:", "");
	//								buffer.append("<a href="+link+">"+text+"</a>");
	//							}else {
	//								buffer.append(urn+" ");
	//							}
	//							bagCount++;
	//						}
	//						buffer.append("</html>");
	//						row[4] = buffer.toString();
	//					}else{ // not a bag
	//						StmtIterator iter2 = rdfModel.listStatements((Resource)objectRDFNode, null, (RDFNode)null);
	//						if (iter2.hasNext()){
	//							Statement stmt = iter2.nextStatement();
	//							objectNode = stmt.asTriple().getObject();
	//							objectRDFNode = stmt.getObject();
	//						}
	//					}
	//				}
	//				if (count>=30){
	//					System.out.println("GAVE UP TRYING TO RESOLVE STATEMENTS (LOOK FOR INFINITE RECURSION)");
	//				}
	//				if (row[4]==null){
	//					row[4] = objectNode.toString();
	//				}
	//				{
	//				IdentifiableMetaData miriamTableRow = new IdentifiableMetaData();
	//				miriamTableRow.identifiable = identifiable;
	//				miriamTableRow.rowData = row;
	//				rowV.add(miriamTableRow);
	//				}
	////				rowV.add(row);
	//			}
	//			
	//		}
	//		return rowV;//rowV.toArray(new String[0][]);
	//}

	
}  //  @jve:decl-index=0:visual-constraint="10,10"
