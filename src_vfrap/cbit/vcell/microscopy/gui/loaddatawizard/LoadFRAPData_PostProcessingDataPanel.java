package cbit.vcell.microscopy.gui.loaddatawizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;

import cbit.image.gui.SourceDataInfo;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.FieldDataWindowManager;
import cbit.vcell.client.TopLevelWindowManager.OpenModelInfoHolder;
import cbit.vcell.client.server.PDEDataManager;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.solver.DataProcessingOutput;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import javax.swing.JComboBox;

public class LoadFRAPData_PostProcessingDataPanel extends JPanel {
	private DocumentWindowManager documentWindowManager;
	private JLabel mainLabel;
	private DataProcessingOutput dataProcessingOutput;
	private String variableName;
	private JList list;
	private ListSelectionListener listSelectionListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()){
				variableName = (String)list.getSelectedValue();
				if(wizardParent != null){
					wizardParent.setNextFinishButtonEnabled(variableName != null);
				}
				if(variableName != null && dataProcessingOutput != null && dataProcessingOutput.getDataGenerators().get(variableName) != null){
					Vector<SourceDataInfo> sourceDataInfoV = dataProcessingOutput.getDataGenerators().get(variableName);
					int lastSliceSelected = (Integer)comboBox.getSelectedItem();
					comboBox.setEnabled(true);
					comboBox.removeAllItems();
					if(sourceDataInfoV.get(0).getZSize() > 1){
						for (int i = 0; i < sourceDataInfoV.get(0).getZSize(); i++) {
							((DefaultComboBoxModel)comboBox.getModel()).addElement(i);
						}
						comboBox.setSelectedItem(lastSliceSelected);
					}else{
						((DefaultComboBoxModel)comboBox.getModel()).addElement(0);
						comboBox.setEnabled(false);
					}
					
				}
			}
		}
	};
	private static final String MAIN_MESSAGE = "Select Post Processing Data variable:";
	public LoadFRAPData_PostProcessingDataPanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JButton btnNewButton = new JButton("Choose Post Processing Data source...");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					dataProcessingOutput = null;
					list.removeListSelectionListener(listSelectionListener);
					list.setListData(new Object[0]);
					
					final OpenModelInfoHolder openModelInfoHolder =
						FieldDataWindowManager.selectOpenModelsFromDesktop(LoadFRAPData_PostProcessingDataPanel.this,documentWindowManager.getRequestManager(),true,"Select Simulation having Post Processing Data.",true);
					if(openModelInfoHolder == null){
						DialogUtils.showWarningDialog(LoadFRAPData_PostProcessingDataPanel.this, "At least 1 BioModel or MathModel that contains a simulation with Post Processing Data must be opened before continuing.");
						return;
					}
					final VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(openModelInfoHolder.simInfo.getAuthoritativeVCSimulationIdentifier(), openModelInfoHolder.jobIndex);
					AsynchClientTask dataManagerTask = new AsynchClientTask("Getting Data Info...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							PDEDataManager dataManager = (PDEDataManager)documentWindowManager.getRequestManager().getDataManager(null,vcSimulationDataIdentifier,!openModelInfoHolder.isCompartmental);
							dataProcessingOutput = dataManager.getDataProcessingOutput();
						}
					};
					AsynchClientTask updateListTask = new AsynchClientTask("Updating List...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							if(dataProcessingOutput.getDataGenerators() == null || dataProcessingOutput.getDataGenerators().size() == 0){
								throw new Exception("No image based Post Processing Data found in '"+openModelInfoHolder.simInfo.getSimulationVersion().getName()+"'");
							}
							String[] listData = dataProcessingOutput.getDataGenerators().keySet().toArray(new String[0]);
							try{
								list.removeListSelectionListener(listSelectionListener);
								list.setListData(listData);
							}finally{
								list.addListSelectionListener(listSelectionListener);
							}
						}
					};
					ClientTaskDispatcher.dispatch(LoadFRAPData_PostProcessingDataPanel.this, new Hashtable<String, Object>(), new AsynchClientTask[] {dataManagerTask,updateListTask},true,false,false,null,true);
				}catch(UserCancelException e2){
					//ignore
				}catch(Exception e2){
					e2.printStackTrace();
					DialogUtils.showErrorDialog(LoadFRAPData_PostProcessingDataPanel.this, e2.getMessage());
				}
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(4, 0, 5, 0);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 0;
		add(btnNewButton, gbc_btnNewButton);
		
		mainLabel = new JLabel(MAIN_MESSAGE);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(4, 4, 0, 4);
		gbc_lblNewLabel.weightx = 1.0;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		add(mainLabel, gbc_lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 4, 4, 4);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		add(scrollPane, gbc_scrollPane);
		
		list = new JList();
		scrollPane.setViewportView(list);
		
		lblNewLabel = new JLabel("Select 2D Z-slice:");
		GridBagConstraints gbc_lblNewLabel2 = new GridBagConstraints();
		gbc_lblNewLabel2.gridx = 0;
		gbc_lblNewLabel2.gridy = 3;
		add(lblNewLabel, gbc_lblNewLabel2);
		
		comboBox = new JComboBox();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 4, 4, 4);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 4;
		add(comboBox, gbc_comboBox);
		((DefaultComboBoxModel)comboBox.getModel()).addElement(0);
		comboBox.setEnabled(false);
	}
	
	public String getSelectedVariableName(){
		return variableName;
	}
	public int getSelectedSlice(){
		return (Integer)comboBox.getSelectedItem();
	}
	public void setDocumentWindowManager(DocumentWindowManager documentWindowManager){
		this.documentWindowManager = documentWindowManager;
	}
	public DataProcessingOutput getDataProcessingOutput() {
		return dataProcessingOutput;
	}
	private Wizard wizardParent;
	private JComboBox comboBox;
	private JLabel lblNewLabel;
	public void setWizard(Wizard wizardParent){
		this.wizardParent = wizardParent;
		if(wizardParent != null){
			if(list.getModel().getSize() == 0 || list.getSelectedValue() == null){
				wizardParent.setNextFinishButtonEnabled(false);
			}else{
				wizardParent.setNextFinishButtonEnabled(true);
			}
		}
	}
}
