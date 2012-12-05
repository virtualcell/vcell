package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.persistence.PathwayIOUtil;
import org.vcell.pathway.persistence.RDFXMLContext;
import org.vcell.sybil.util.http.pathwaycommons.search.Pathway;
import org.vcell.util.BeanUtils;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayCommonsKeyword;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayCommonsVersion;
import cbit.vcell.client.desktop.biomodel.BioModelEditorPathwayCommonsPanel.PathwayData;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;

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

	JComboBox categoryList1 = new JComboBox(criteriaStrings);
	JComboBox categoryList2 = new JComboBox(criteriaStrings);
	JTextField entityName1 = new JTextField("");
	JTextField entityName2 = new JTextField("11427");
	private JButton showPathwayButton = null;
	String command = "";
	private EventHandler eventHandler = new EventHandler();

	
	private class EventHandler implements ActionListener, KeyListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == showPathwayButton) {
				showPathway();
			}
		}
		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
	}
	
	
	
	public BioModelEditorSabioPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		setLayout(new BorderLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		// ------- panel 1 ---------------
		JLabel categoryLabel1 = new JLabel("Category");
		categoryList1.setSelectedIndex(4);		// select SabioReactionID
		categoryList1.addActionListener(eventHandler);
		
		JLabel entityLabel1 = new JLabel("Entity");
		entityName1.addActionListener(eventHandler);
		
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createTitledBorder(" Search "));
		panel1.setLayout(new GridBagLayout());
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(categoryLabel1, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(categoryList1, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(entityLabel1, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel1.add(entityName1, gbc);
				
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
		
		// ------- button ------------------
		JPanel p1 = new JPanel();
		p1.setLayout(new GridBagLayout());
		
		showPathwayButton = new JButton("Preview");
		showPathwayButton.addActionListener(eventHandler);
		showPathwayButton.setEnabled(true);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.insets = new Insets(2,4,4,160);
		p1.add(showPathwayButton, gbc);

		// ------- the panels -------------
		add(p,BorderLayout.NORTH);
		add(p1,BorderLayout.SOUTH);

//		setLayout(new BorderLayout());
//		GridBagConstraints gbc = new GridBagConstraints();
//		
//		JPanel p = new JPanel();
//		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
//
//		// ------- panel 1 ---------------
//		JLabel categoryLabel1 = new JLabel("Category");
//		categoryList1.setSelectedIndex(3);		// select SabioReactionID
//		categoryList1.addActionListener(eventHandler);
//		
//		JLabel entityLabel1 = new JLabel("Entity");
//		entityName1.addActionListener(eventHandler);
//		
//		panel1 = new JPanel();
//		panel1.setBorder(BorderFactory.createTitledBorder("Search Criteria:"));
//		panel1.setLayout(new GridBagLayout());
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		panel1.add(categoryLabel1, gbc);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		panel1.add(categoryList1, gbc);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		panel1.add(entityLabel1, gbc);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 1;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		panel1.add(entityName1, gbc);
//				
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(4,4,4,1);
//		p.add(panel1);
//		
//		
//		// ------- panel 2 -----------------
//		JLabel categoryLabel2 = new JLabel("Category");
//		categoryList2.setSelectedIndex(0);
////		categoryList2.setSelectedIndex(21);		// select CellularLocation
//		categoryList2.addActionListener(eventHandler);
//		
//		JLabel entityLabel2 = new JLabel("Entity");
//		entityName2.addActionListener(eventHandler);
//		
//		panel2 = new JPanel();
//		panel2.setBorder(BorderFactory.createTitledBorder("Additional search criteria:"));
//		panel2.setLayout(new GridBagLayout());
//		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		panel2.add(categoryLabel2, gbc);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		panel2.add(categoryList2, gbc);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		panel2.add(entityLabel2, gbc);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 1;
//		gbc.gridy = 1;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		panel2.add(entityName2, gbc);
//				
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.insets = new Insets(4,4,4,1);
//		p.add(panel2, gbc);
//		
//		// ------- button ------------------
//		JPanel p1 = new JPanel();
//		p1.setLayout(new GridBagLayout());
//		
//		showPathwayButton = new JButton("Preview");
//		showPathwayButton.addActionListener(eventHandler);
//		showPathwayButton.setEnabled(true);
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 1;
//		gbc.weightx = 1.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.SOUTHWEST;
//		gbc.insets = new Insets(2,4,4,160);
//		p1.add(showPathwayButton, gbc);
//
//		// ------- the panels -------------
//		add(p,BorderLayout.NORTH);
//		add(p1,BorderLayout.SOUTH);
	}
	
	
	public void showPathway() {
		String category1 = (String)categoryList1.getSelectedItem();
		String category2 = (String)categoryList2.getSelectedItem();
		
		String entity1 = entityName1.getText();
		String entity2 = entityName2.getText();

		command = "";
		// http://sabiork.h-its.org/sabioRestWebServices/searchKineticLaws/biopax?q=
		// %28Pathway:JAK-STAT%20AND%20CellularLocation:cytosol%29
		
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
				org.jdom.Document jdomDocument = BeanUtils.getJDOMDocument(url, getClientTaskStatusSupport());
				org.jdom.Element rootElement = jdomDocument.getRootElement();
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
		ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2}, true,true,null);
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		// TODO Auto-generated method stub

	}

}
