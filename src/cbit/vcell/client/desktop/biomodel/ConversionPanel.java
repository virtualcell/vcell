package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.vcell.pathway.BioPaxObject;
import org.vcell.relationship.PathwayMapping;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayImportSelectionTool;

@SuppressWarnings("serial")
public class ConversionPanel extends DocumentEditorSubPanel implements PathwayImportSelectionTool {
	private List<BioPaxObject> bioPaxObjects= null;
	private EventHandler eventHandler = new EventHandler();
	private BioModel bioModel = null;
	private BioModelEditorConversionTableModel tableModel = null; 
	private EditorScrollTable table = null;
	private JTextField textFieldSearch = null;
	private JButton bringItInButton = null;
	private JButton resetButton = null;
	
	private class EventHandler implements ActionListener, DocumentListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if(e.getSource() == bringItInButton){
				bringItIn();
			}else if(e.getSource() == resetButton){
				resetValues();
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
	
public ConversionPanel() {
	super();
	initialize();
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

private void initialize() {
	try {
		setName("KineticsTypeTemplatePanel");
		setLayout(new GridBagLayout());
			
		table = new EditorScrollTable();
		tableModel = new BioModelEditorConversionTableModel(table);
		table.setModel(tableModel);
		tableModel.setIssueManager(issueManager);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 8;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		table.setPreferredScrollableViewportSize(new Dimension(400,200));
		add(table.getEnclosingScrollPane(), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 9;
		gbc.gridy = gridy;
		// add toolTipText for each table cell
		table.addMouseMotionListener(new MouseMotionAdapter() { 
		    public void mouseMoved(MouseEvent e) { 	
		            Point p = e.getPoint(); 
		            int row = table.rowAtPoint(p);
		            int column = table.columnAtPoint(p);
		            table.setToolTipText(String.valueOf(table.getValueAt(row,column)));


		    } 
		});

		gridy ++;	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);

		textFieldSearch = new JTextField(70);
		textFieldSearch.addActionListener(eventHandler);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 0, 4, 4);
		add(textFieldSearch, gbc);
		
		setBackground(Color.white);		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void searchTable() {
	String searchText = textFieldSearch.getText();
	tableModel.setSearchText(searchText);
}

// done
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ConversionPanel aKineticsTypeTemplatePanel;
		aKineticsTypeTemplatePanel = new ConversionPanel();
		frame.setContentPane(aKineticsTypeTemplatePanel);
		frame.setSize(aKineticsTypeTemplatePanel.getSize());
		frame.setSize(500, 500);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Sets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @param kinetics The new value for the property.
 * @see #getKinetics
 */
public void setBioPaxObjects(List<BioPaxObject> newValue) {
	if (bioPaxObjects == newValue) {
		return;
	}
	bioPaxObjects = newValue;
	tableModel.setBioPaxObjects(newValue);
}

public void setBioModel(BioModel newValue) {
	if (bioModel == newValue) {
		return;
	}
	bioModel = newValue;
	tableModel.setBioModel(newValue);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	// TODO Auto-generated method stub
	
}

public BioModelEditorConversionTableModel getTableModel(){
	return tableModel;
}

public void showSelectionDialog() {
    int returnCode = DialogUtils.showComponentOKCancelDialog(this, this, "Import into Physiology");
	if (returnCode == JOptionPane.OK_OPTION) {
		bringItIn();
	}
}

public void bringItIn(){
	if(bioModel == null){
		return;
	}
	if(bioModel.getRelationshipModel() == null){
		return;
	}
	PathwayMapping pathwayMapping = new PathwayMapping();
	try{
		
		// function I:
		// pass the table rows that contains user edited values to create Vcell object
		pathwayMapping.createBioModelEntitiesFromBioPaxObjects(bioModel, tableModel.getTableRows());
		// function II:
		// pass the bioPax objects to generate Vcell objects
		// pathwayMapping.createBioModelEntitiesFromBioPaxObjects(bioModel, tableModel.getBioPaxObjects().toArray());
		
		if (selectionManager != null){
			selectionManager.setActiveView(new ActiveView(null,DocumentEditorTreeFolderClass.REACTION_DIAGRAM_NODE, ActiveViewID.reaction_diagram));
//			selectionManager.setSelectedObjects(new Object[]{selectedBioPaxObjects});
		}
	}catch(Exception e)
	{
		e.printStackTrace(System.out);
		DialogUtils.showErrorDialog(this, "Errors occur when converting pathway objects to VCell bioModel objects.\n" + e.getMessage());
	}
}

private void resetValues(){
	
}



//private void goToPathway(){
//	if(bioModel == null){
//		return;
//	}
//	if(bioModel.getRelationshipModel() == null){
//		return;
//	}
//	ArrayList<BioPaxObject> selectedBioPaxObjects = new ArrayList<BioPaxObject>();
//	for(RelationshipObject re: bioModel.getRelationshipModel().getRelationshipObjects(bioModelEntityObject)){
//		selectedBioPaxObjects.add(re.getBioPaxObject());
//	}
//	if (selectionManager != null){
//		selectionManager.setActiveView(new ActiveView(null,DocumentEditorTreeFolderClass.PATHWAY_NODE, ActiveViewID.pathway));
//		selectionManager.setSelectedObjects(new Object[]{selectedBioPaxObjects});
//	}
//}
}
