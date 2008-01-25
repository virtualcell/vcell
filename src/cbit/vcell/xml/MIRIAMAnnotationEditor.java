package cbit.vcell.xml;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import cbit.gui.DialogUtils;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.server.ClientServerManager;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.xml.MIRIAMHelper.DescriptiveHeirarchy;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.BorderFactory;

import org.jdom.Element;

import uk.ac.ebi.miriam.lib.MiriamLink;

import java.awt.SystemColor;

public class MIRIAMAnnotationEditor extends JPanel implements ActionListener{
	
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

	private JScrollPane jScrollPane = null;
	private JTable jTableMIRIAM = null;
	private JButton jButtonOK = null;
	private JPanel jPanel = null;
	private JButton jButtonAdd = null;
	private JButton jButtonDelete = null;
	
	private Vector<ActionListener> actionListenerV = new Vector<ActionListener>();
	public static final String ACTION_OK ="OK";  //  @jve:decl-index=0:
	public static final String ACTION_ADD ="Add Annotation...";  //  @jve:decl-index=0:
	public static final String ACTION_EDIT ="Edit...";
	public static final String ACTION_DELETE ="Delete Annotation";
	
	Vector<MIRIAMHelper.MIRIAMTableRow> rowData;  //  @jve:decl-index=0:
	private JButton jButtonEditAnnotation = null;
	Vector<Integer> rowMapV;
	private JButton jButtonCopy = null;
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
	private JButton jButtonDetails = null;
	private MiriamLink miriamLink = null;
	/**
	 * This method initializes 
	 * 
	 */
	public MIRIAMAnnotationEditor() {
		super();
		initialize();
		initQualifiers();
		jButtonEditAnnotation.setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
		if(jTableMIRIAM.getCellEditor() != null){
			jTableMIRIAM.getCellEditor().stopCellEditing();
		}
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
			jScrollPane.setViewportView(getJTableMIRIAM());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTableMIRIAM	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTableMIRIAM() {
		if (jTableMIRIAM == null) {
			jTableMIRIAM = new JTable();
			jTableMIRIAM.getSelectionModel().addListSelectionListener(
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent e) {
						if(e.getValueIsAdjusting()){
							return;
						}
						jButtonAdd.setEnabled(false);
						jButtonDelete.setEnabled(false);
						jButtonEditAnnotation.setEnabled(false);
						jButtonDetails.setEnabled(false);
						if(getSelectedDescriptionHeirarchy() == null){
						//if(jTableMIRIAM.getSelectedRow() != -1 && rowData != null && rowData.get(rowMapV.get(jTableMIRIAM.getSelectedRow())).descriptiveHeirarchy == null){
							jButtonAdd.setEnabled(true);
						}
						if(getSelectedDescriptionHeirarchy() != null){
						//if(jTableMIRIAM.getSelectedRow() != -1 && rowData != null && rowData.get(rowMapV.get(jTableMIRIAM.getSelectedRow())).descriptiveHeirarchy != null){
							jButtonDelete.setEnabled(true);
							jButtonEditAnnotation.setEnabled(true);
							jButtonDetails.setEnabled(getSelectedURI() != null);
						}
					}
				}
			);
			jTableMIRIAM.getTableHeader().setReorderingAllowed(false);
		}
		return jTableMIRIAM;
	}

	/**
	 * This method initializes jButtonOK	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText(ACTION_OK);
			jButtonOK.addActionListener(this);
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 4;
			gridBagConstraints21.gridy = 0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.gridy = 0;
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
			jPanel.add(getJButtonOK(), gridBagConstraints);
			jPanel.add(getJButtonAdd(), gridBagConstraints2);
			jPanel.add(getJButtonDelete(), gridBagConstraints4);
			jPanel.add(getJButtonEditAnnotation(), gridBagConstraints11);
			jPanel.add(getJButtonCopy(), gridBagConstraints21);

			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.gridy = 0;
			gridBagConstraints_1.gridx = 5;
			jPanel.add(getJButtonDetails(), gridBagConstraints_1);
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

	public MIRIAMAnnotatable getSelectedMIRIAMAnnotatable(){
		int selectedrow = jTableMIRIAM.getSelectedRow();
		if(selectedrow != -1&& rowData != null && rowMapV != null){
			return rowData.get(rowMapV.get(selectedrow)).miriamAnnotatable;
			//return rowData.get(selectedrow).miriamAnnotatable;
		}
		return null;
	}
	
	public MIRIAMHelper.DescriptiveHeirarchy getSelectedDescriptionHeirarchy(){
		int selectedrow = jTableMIRIAM.getSelectedRow();
		if(selectedrow != -1 && rowData != null && rowMapV != null){
			return rowData.get(rowMapV.get(selectedrow)).descriptiveHeirarchy;
			//return rowData.get(selectedrow).descriptiveHeirarchy;
		}
		return null;
	}
	
	public void setMIRIAMAnnotation(TreeMap<MIRIAMAnnotatable, Vector<MIRIAMHelper.DescriptiveHeirarchy>> mirimaDescrHeir){
		DefaultTableModel tableModel = new DefaultTableModel(){
		    public boolean isCellEditable(int row, int column) {
		    	return false;
//		        return
//		        	(column == (MIRIAMHelper.MIRIAM_ANNOT_COLUMNS.length-1))
//		        	&&
//		        	(rowData.get(row).descriptiveHeirarchy != null);
		    }
		};

		rowData = MIRIAMHelper.getTableFormattedData(mirimaDescrHeir);
//		String[][] rowArr= new String[rowData.size()][];
//		for (int i = 0; i < rowArr.length; i++) {
//			rowArr[i] = rowData.get(i).rowData;
//		}
		Vector<String[]> rowArrV = new Vector<String[]>();
		rowMapV = new Vector<Integer>();
		HashMap<Integer, Vector<DescriptiveHeirarchy>> creatorsH =
			new HashMap<Integer, Vector<DescriptiveHeirarchy>>();
//		Vector<DescriptiveHeirarchy> latestCreator = null;
		for (int i = 0; i < rowData.size(); i++) {
			if(rowData.get(i).descriptiveHeirarchy != null && rowData.get(i).descriptiveHeirarchy.isCreatorChild()){
				Vector<DescriptiveHeirarchy> latestCreator = new Vector<DescriptiveHeirarchy>();
				creatorsH.put(rowArrV.size(),latestCreator);
				rowArrV.add(new String[] {null,null,null,"Creator "+(creatorsH.size()),rowData.get(i).rowData[4]});
				rowMapV.add(i);
				latestCreator.add(rowData.get(i).descriptiveHeirarchy);
				int index = i+1;
				while(rowData.get(index).descriptiveHeirarchy != null && rowData.get(index).descriptiveHeirarchy.isCreatorChild()){
					if(rowData.get(index).descriptiveHeirarchy.isSameCreator(latestCreator.firstElement())){
						rowArrV.lastElement()[4]+= ","+rowData.get(index).rowData[4];
						latestCreator.add(rowData.get(index).descriptiveHeirarchy);
						index+= 1;
						i+= 1;
					}else{
//						i=index-1;
						break;
					}
				}
//				if(!bIsCreator){//first time
//					latestCreator = new Vector<DescriptiveHeirarchy>();
//					creatorsH.put(rowArrV.size(),latestCreator);
//					rowArrV.add(new String[] {null,null,null,"Creator "+(creatorsH.size()),rowData.get(i).rowData[4]});
//				}else{
//					rowArrV.lastElement()[4]+= ","+rowData.get(i).rowData[4];
//				}
//				latestCreator.add(rowData.get(i).descriptiveHeirarchy);
//				bIsCreator = true;
			}
			else{
//				bIsCreator = false;
//				latestCreator = null;
				rowArrV.add(rowData.get(i).rowData);
				rowMapV.add(i);
			}
		}
		tableModel.setDataVector(rowArrV.toArray(new String[0][]), MIRIAMHelper.MIRIAM_ANNOT_COLUMNS);
		jTableMIRIAM.setModel(tableModel);
		jTableMIRIAM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tableModel.addTableModelListener(
			new TableModelListener(){
				public void tableChanged(TableModelEvent e) {
					DescriptiveHeirarchy descrHeir = rowData.get(e.getFirstRow()).descriptiveHeirarchy;
					MIRIAMHelper.editDescriptiveHeirarchy(
							descrHeir, (String)jTableMIRIAM.getValueAt(e.getFirstRow(), e.getColumn()));
				}	
			}
		);		
		jTableMIRIAM.getSelectionModel().setSelectionInterval(0, 0);
	}

	/**
	 * This method initializes jButtonEditAnnotation	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonEditAnnotation() {
		if (jButtonEditAnnotation == null) {
			jButtonEditAnnotation = new JButton();
			jButtonEditAnnotation.setText("Edit Annotation...");
			jButtonEditAnnotation.addActionListener(this);
		}
		return jButtonEditAnnotation;
	}

	/**
	 * This method initializes jButtonCopy	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCopy() {
		if (jButtonCopy == null) {
			jButtonCopy = new JButton();
			jButtonCopy.setText("Copy All");
			jButtonCopy.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					StringBuffer sb = new StringBuffer();
					Vector dataVector = ((DefaultTableModel)jTableMIRIAM.getModel()).getDataVector();
					for (Iterator iter = dataVector.iterator(); iter.hasNext();) {
						Vector<String> element = (Vector<String>) iter.next();
						for (int i = 0; i < element.size(); i++) {
							sb.append((i>0?" ":"")+element.get(i));
						}
						sb.append("\n");
					}
					VCellTransferable.sendToClipboard(sb.toString());
				}
			});
		}
		return jButtonCopy;
	}

	private JButton getJButtonDetails() {
		if (jButtonDetails == null) {
			jButtonDetails = new JButton();
			jButtonDetails.setText("Details...");
			jButtonDetails.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					detailAction();
				}
			});
		}
		return jButtonDetails;
	}

	private void detailAction(){
		URI detailURI = getSelectedURI();
		if (detailURI != null) {
			if (miriamLink == null) {
				miriamLink = new MiriamLink();
			}
			String[] urlArr = miriamLink.getDataEntries(detailURI.toString());
			if (urlArr != null && urlArr.length > 0) {
				for (int i = 0; i < urlArr.length; i++) {
					System.out.println(urlArr[i]);
				}
				PopupGenerator.browserLauncher(urlArr[0],urlArr[0],false);
			}
		}
	}
	private URI getSelectedURI(){
		String detailInfo = (String)getJTableMIRIAM().getValueAt(jTableMIRIAM.getSelectedRow(), 4);
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
//		JScrollPane jsp = new JScrollPane(jPanelNewIdentifier);
//		jsp.setPreferredSize(new Dimension(800,40));
		if(PopupGenerator.showComponentOKCancelDialog(MIRIAMAnnotationEditor.this, getJPanelNewIdentifier(), "Define New Formal Identifier") == JOptionPane.OK_OPTION){
			String qualifierName = (String)jComboBoxQualifier.getSelectedItem();
			URI qualifierURI = null;
			if(qualifierName.endsWith("(bio)")){
				qualifierURI = new URI(XMLTags.BMBIOQUAL_NAMESPACE_URI);
			}else if(qualifierName.endsWith("(model)")){
				qualifierURI = new URI(XMLTags.BMMODELQUAL_NAMESPACE_URI);
			}
			qualifierName = qualifierName.substring(0,qualifierName.indexOf(" ("));
			Element newID =
				MIRIAMHelper.createRDFIdentifier((String)jComboBoxURI.getSelectedItem(), jTextFieldFormalID.getText());
			MIRIAMHelper.addIdentifierToAnnotation(
					newID,
					getSelectedMIRIAMAnnotatable(),
					qualifierName,
					qualifierURI);
		}
	}

	
	public void addTimeUTCDialog(){
//		JScrollPane jsp = new JScrollPane(jPanelNewIdentifier);
//		jsp.setPreferredSize(new Dimension(800,40));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		getJTextFieldTimeUTC().setText(sdf.format(new Date()));
		if(PopupGenerator.showComponentOKCancelDialog(MIRIAMAnnotationEditor.this, getJPanelTimeUTC(), "Define New Date") == JOptionPane.OK_OPTION){
			MIRIAMHelper.addDateToAnnotation(
					getSelectedMIRIAMAnnotatable(),
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
			MIRIAMHelper.addCreatorToAnnotation(
					getSelectedMIRIAMAnnotatable(),
					getJTextFieldFamily().getText(),
					getJTextFieldGiven().getText(),
					getJTextFieldEmail().getText(),
					getJTextFieldOrganization().getText());
			
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
}  //  @jve:decl-index=0:visual-constraint="10,10"
