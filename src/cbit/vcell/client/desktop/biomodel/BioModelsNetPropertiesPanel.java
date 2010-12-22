package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.vcell.util.gui.DialogUtils;

import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesServiceLocator;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.xml.XMLInfo;

@SuppressWarnings("serial")
public class BioModelsNetPropertiesPanel extends BioModelEditorSubPanel {
	
	private JTextField nameTextField;
	private JTextField idTextField;
	private JLabel linkLabel = null;
	private JButton importButton = null;
	private BioModelsNetModelInfo bioModelsNetModelInfo = null;
	private DocumentWindowManager documentWindowManager = null;
	
	private EventHandler eventHandler = new EventHandler();
	
	private class EventHandler implements ActionListener, MouseListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == importButton) {
				importFromBioModelsNet();
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == linkLabel) {
				DialogUtils.browserLauncher(BioModelsNetPropertiesPanel.this, bioModelsNetModelInfo.getLink(), "Failed to open the link!", false);
			}
			
		}

		public void mousePressed(MouseEvent e) {
			
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
	public BioModelsNetPropertiesPanel() {
		super();
		initialize();
	}
	
	public void importFromBioModelsNet() {		
		AsynchClientTask task1 = new AsynchClientTask("Importing " + bioModelsNetModelInfo.getName(), AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				BioModelsWebServicesServiceLocator bioModelsWebServicesServiceLocator =	new BioModelsWebServicesServiceLocator();
				BioModelsWebServices bioModelsWebServices = bioModelsWebServicesServiceLocator.getBioModelsWebServices();
				String bioModelSBML = bioModelsWebServices.getModelSBMLById(bioModelsNetModelInfo.getId());
				XMLInfo xmlInfo = new XMLInfo(bioModelSBML, bioModelsNetModelInfo.getName());
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


	private void initialize() {
		setBackground(Color.white);
		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		idTextField = new JTextField();
		idTextField.setEditable(false);
		importButton = new JButton("Import");
		importButton.addActionListener(eventHandler);
		importButton.setEnabled(true);
		linkLabel = new JLabel();
		linkLabel.addMouseListener(eventHandler);
		linkLabel.setForeground(Color.blue);
				
		int gridy = 0;
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.weightx = 0.2;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Model Name"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4,4,4,4);
		add(nameTextField, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.weightx = 0.2;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Entry ID"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(idTextField, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.weightx = 0.2;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Link"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4,4,4,4);
		add(linkLabel, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.insets = new Insets(4,4,4,4);
		add(importButton, gbc);
	}

	public void setDocumentWindowManager(DocumentWindowManager newValue) {
		this.documentWindowManager = newValue;
	}

	public final void setBioModelsNetModelInfo(BioModelsNetModelInfo bioModelsNetModelInfo) {
		if (this.bioModelsNetModelInfo == bioModelsNetModelInfo) {
			return;
		}
		this.bioModelsNetModelInfo = bioModelsNetModelInfo;
		if (bioModelsNetModelInfo == null) {
			importButton.setEnabled(false);
			nameTextField.setText(null);
			idTextField.setText(null);
		} else {
			importButton.setEnabled(true);
			nameTextField.setText(bioModelsNetModelInfo.getName());
			idTextField.setText(bioModelsNetModelInfo.getId());
			linkLabel.setText("<html><u>" + bioModelsNetModelInfo.getLink() + "</u></html>");
		}
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof BioModelsNetModelInfo)) {
			return;
		}
		BioModelsNetModelInfo bioModelsNetModelInfo = (BioModelsNetModelInfo) selectedObjects[0];
		setBioModelsNetModelInfo(bioModelsNetModelInfo);
	}
}
