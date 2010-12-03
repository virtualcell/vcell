package cbit.vcell.mapping.gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.mapping.MicroscopeMeasurement;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.MicroscopeMeasurement.ConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ProjectionZKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ExperimentalPSF;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import org.vcell.util.gui.ButtonGroupCivilized;

public class MicroscopeMeasurementPanel extends javax.swing.JPanel {
	private JTextField nameTextField;
	private JRadioButton rdbtnZprojection = null;
	private JRadioButton rdbtnExperimental = null;
	JComboBox pointSpreadFunctionsComboBox = null;
	private MicroscopeMeasurement microscopeMeasurement = null;
	private SimulationContext simulationContext = null;
	private AllPointSpreadFunctionsComboModel pointSpreadFunctionsComboModel = new AllPointSpreadFunctionsComboModel();
	private AllSpeciesContextListModel allSpeciesContextListModel = new AllSpeciesContextListModel();
	private FluorescenceSpeciesContextListModel fluorescenceSpeciesContextListModel = new FluorescenceSpeciesContextListModel();
	private JButton removeButton;
	private JButton addButton;
	private JList allSpeciesContextList;
	private JList fluorescentSpeciesContextList;
	private ListCellRenderer speciesContextCellRenderer = new DefaultListCellRenderer(){
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof SpeciesContext && component instanceof JLabel){
				SpeciesContext sc = (SpeciesContext)value;
				((JLabel)component).setText(sc.getName());
			}
			return component;
		}
	};

	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	public void setSimulationContext(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
		allSpeciesContextListModel.setSimulationContext(simulationContext);
		pointSpreadFunctionsComboModel.setSimulationContext(simulationContext);
	}

	public MicroscopeMeasurement getMicroscopeMeasurement() {
		return microscopeMeasurement;
	}

	public void setMicroscopeMeasurement(MicroscopeMeasurement microscopeMeasurement) {
		this.microscopeMeasurement = microscopeMeasurement;
		fluorescenceSpeciesContextListModel.setMicroscopeMeasurement(microscopeMeasurement);
		allSpeciesContextListModel.setMicroscopyMeasurement(microscopeMeasurement);
		pointSpreadFunctionsComboModel.setMicroscopyMeasurement(microscopeMeasurement);
		refreshButtons();
	}

	public MicroscopeMeasurementPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		try {
			setName("MicroscopeMeasurementPanel");
			GridBagLayout gridBagLayout = new GridBagLayout();
			setLayout(gridBagLayout);
			
			JPanel middlePanel = new JPanel();
			GridBagConstraints gbc_middle = new GridBagConstraints();
			gbc_middle.weighty = 1.0;
			gbc_middle.weightx = 1.0;
			gbc_middle.fill = GridBagConstraints.BOTH;
			gbc_middle.gridx = 0;
			gbc_middle.gridy = 1;
			GridBagLayout gbl_middle = new GridBagLayout();
			middlePanel.setLayout(gbl_middle);
			
			JLabel label = new JLabel("all species");
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 0;
			gbc_label.gridy = 0;
			middlePanel.add(label, gbc_label);
			
			JLabel label_1 = new JLabel("fluorescent species");
			GridBagConstraints gbc_label_1 = new GridBagConstraints();
			gbc_label_1.insets = new Insets(0, 0, 5, 0);
			gbc_label_1.gridx = 2;
			gbc_label_1.gridy = 0;
			middlePanel.add(label_1, gbc_label_1);
			
			allSpeciesContextList = new JList();
			allSpeciesContextList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					refreshButtons();
				}
			});
			GridBagConstraints gbc_allSpeciesContextList = new GridBagConstraints();
			gbc_allSpeciesContextList.insets = new Insets(0, 0, 0, 5);
			gbc_allSpeciesContextList.weighty = 1.0;
			gbc_allSpeciesContextList.weightx = 1.0;
			gbc_allSpeciesContextList.fill = GridBagConstraints.BOTH;
			gbc_allSpeciesContextList.gridx = 0;
			gbc_allSpeciesContextList.gridy = 1;
			middlePanel.add(allSpeciesContextList, gbc_allSpeciesContextList);
			allSpeciesContextList.setModel(allSpeciesContextListModel );
			allSpeciesContextList.setCellRenderer(speciesContextCellRenderer);
			
			JPanel buttonPanel = new JPanel();
			GridBagConstraints gbc_panel_buttons = new GridBagConstraints();
			gbc_panel_buttons.insets = new Insets(0, 0, 0, 5);
			gbc_allSpeciesContextList.weighty = 0.1;
			gbc_allSpeciesContextList.weightx = 1.0;
			gbc_allSpeciesContextList.fill = GridBagConstraints.BOTH;
			gbc_panel_buttons.gridx = 1;
			gbc_panel_buttons.gridy = 1;
			GridBagLayout gbl_buttons = new GridBagLayout();
			buttonPanel.setLayout(gbl_buttons);
			
			removeButton = new JButton("<<");
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			GridBagConstraints gbc_removeButton = new GridBagConstraints();
			gbc_removeButton.weightx = 1.0;
			gbc_removeButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_removeButton.anchor = GridBagConstraints.NORTHWEST;
			gbc_removeButton.gridx = 0;
			gbc_removeButton.gridy = 1;
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					removeButtonActionPerformed();
				}
			});
			buttonPanel.add(removeButton, gbc_removeButton);
			
			addButton = new JButton(">>");
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.weightx = 1.0;
			gbc_button.fill = GridBagConstraints.HORIZONTAL;
			gbc_button.anchor = GridBagConstraints.NORTHWEST;
			gbc_button.gridx = 0;
			gbc_button.gridy = 0;
			buttonPanel.add(addButton, gbc_button);
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addButtonActionPerformed();
				}
			});
			middlePanel.add(buttonPanel, gbc_panel_buttons);
			
			fluorescentSpeciesContextList = new JList();
			fluorescentSpeciesContextList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					refreshButtons();
				}
			});
			GridBagConstraints gbc_fluorescentSpeciesContextList = new GridBagConstraints();
			gbc_fluorescentSpeciesContextList.fill = GridBagConstraints.BOTH;
			gbc_fluorescentSpeciesContextList.weighty = 1.0;
			gbc_fluorescentSpeciesContextList.weightx = 1.0;
			gbc_fluorescentSpeciesContextList.gridx = 2;
			gbc_fluorescentSpeciesContextList.gridy = 1;
			middlePanel.add(fluorescentSpeciesContextList, gbc_fluorescentSpeciesContextList);
			add(middlePanel, gbc_middle);
			fluorescentSpeciesContextList.setModel(fluorescenceSpeciesContextListModel);
			fluorescentSpeciesContextList.setCellRenderer(speciesContextCellRenderer);
			
			JPanel bottomPanel = new JPanel();
			GridBagConstraints gbc_bottom = new GridBagConstraints();
			gbc_bottom.weightx = 1.0;
			gbc_bottom.weighty = 0.1;
			gbc_bottom.fill = GridBagConstraints.BOTH;
			gbc_bottom.gridx = 0;
			gbc_bottom.gridy = 2;
			GridBagLayout gbl_bottom = new GridBagLayout();
			bottomPanel.setLayout(gbl_bottom);
			add(bottomPanel, gbc_bottom);
			
			JLabel lblConvolutionKernel = new JLabel("point spread function");
			GridBagConstraints gbc_lblConvolutionKernel = new GridBagConstraints();
			gbc_lblConvolutionKernel.gridx = 0;
			gbc_lblConvolutionKernel.gridy = 0;
			bottomPanel.add(lblConvolutionKernel, gbc_lblConvolutionKernel);
			
			
			
			
			rdbtnZprojection = new JRadioButton("z-projection");
			GridBagConstraints gbc_rdbtnZprojection = new GridBagConstraints();
			gbc_rdbtnZprojection.gridx = 1;
			gbc_rdbtnZprojection.gridy = 0;
			bottomPanel.add(rdbtnZprojection, gbc_rdbtnZprojection);
			rdbtnZprojection.setSelected(true);
			rdbtnZprojection.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rdbtnZprojectionActionPerformed(e);
				}
			});
			
			rdbtnExperimental = new JRadioButton("experimental");
			GridBagConstraints gbc_rdbtnExperimental = new GridBagConstraints();
			gbc_rdbtnExperimental.gridx = 1;
			gbc_rdbtnExperimental.gridy = 1;
			bottomPanel.add(rdbtnExperimental, gbc_rdbtnExperimental);
			rdbtnExperimental.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rdbtnExperimentalActionPerformed(e);
				}
			});
		
			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(rdbtnZprojection);
			buttonGroup.add(rdbtnExperimental);
			
			pointSpreadFunctionsComboBox = new JComboBox();
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.gridx = 2;
			gbc_comboBox.gridy = 1;
			bottomPanel.add(pointSpreadFunctionsComboBox, gbc_comboBox);
			pointSpreadFunctionsComboBox.setModel(pointSpreadFunctionsComboModel);
			pointSpreadFunctionsComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pointSpreadFunctionsComboBoxActionPerformed(e);
				}
			});
			pointSpreadFunctionsComboBox.setEnabled(false);
			
			JPanel topPanel = new JPanel();
			GridBagConstraints gbc_top = new GridBagConstraints();
			gbc_top.weightx = 1.0;
			gbc_top.weighty = 0.1;
			gbc_top.fill = GridBagConstraints.BOTH;
			gbc_top.gridx = 0;
			gbc_top.gridy = 0;
			add(topPanel, gbc_top);
			
			JLabel lblFluoescenceFunctionName = new JLabel("fluoescence function name");
			topPanel.add(lblFluoescenceFunctionName);
			
			nameTextField = new JTextField();
			topPanel.add(nameTextField);
			nameTextField.setColumns(10);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	

	protected void pointSpreadFunctionsComboBoxActionPerformed(ActionEvent e) {
		if(pointSpreadFunctionsComboModel.getSize() == 0) {
			rdbtnZprojection.setSelected(true);
			rdbtnExperimental.setEnabled(false);
			pointSpreadFunctionsComboBox.setEnabled(false);
			createMicroscopeMeasurementProjectionJ();	// new MicroscopeMeasurement object using ProjectionZ kernel
			return;
		} else {
//			createMicroscopeMeasurementExperimental();	// new MicroscopeMeasurement object using ExperimentalPSF kernel
			rdbtnExperimental.setEnabled(true);
		}
	}

	// new MicroscopeMeasurement object using ProjectionJ kernel
	private void createMicroscopeMeasurementProjectionJ() {
		simulationContext.setMicroscopeMeasurement(new MicroscopeMeasurement("fluor",
				new ProjectionZKernel(),new Expression(0.0)));
		microscopeMeasurementAddAllFluorescentSpecies();
	}
	// new MicroscopeMeasurement object using ExperimentalPSF kernel
	private void createMicroscopeMeasurementExperimental() {
		String psfName = (String)pointSpreadFunctionsComboBox.getSelectedItem();
		for (DataSymbol dataSymbol : simulationContext.getDataContext().getDataSymbols()){
			if (dataSymbol.getName().equals(psfName)){
				simulationContext.setMicroscopeMeasurement(new MicroscopeMeasurement("fluor",
						new ExperimentalPSF(dataSymbol),new Expression(0.0)));
			}
		}
		microscopeMeasurementAddAllFluorescentSpecies();
	}
	private void microscopeMeasurementAddAllFluorescentSpecies() {
		for(int i=0; i<fluorescenceSpeciesContextListModel.size(); i++) {
			SpeciesContext sc = (SpeciesContext)fluorescenceSpeciesContextListModel.getElementAt(i);
			simulationContext.getMicroscopeMeasurement().addFluorescentSpecies(sc);
		}
	}
	
	protected void rdbtnZprojectionActionPerformed(ActionEvent e) {
		createMicroscopeMeasurementProjectionJ();	// new MicroscopeMeasurement object using ProjectionZ kernel
		pointSpreadFunctionsComboBox.setEnabled(false);
	}
	protected void rdbtnExperimentalActionPerformed(ActionEvent e) {
		createMicroscopeMeasurementExperimental();	// new MicroscopeMeasurement object using ExperimentalPSF kernel
		pointSpreadFunctionsComboBox.setEnabled(true);
	}

	protected void refreshButtons() {
		boolean bFluorescentSpeciesSelected = fluorescentSpeciesContextList.getSelectedValue()!=null;
		boolean bNonFluorescentSpeciesSelected = allSpeciesContextList.getSelectedValue()!=null;
		removeButton.setEnabled(bFluorescentSpeciesSelected);
		addButton.setEnabled(bNonFluorescentSpeciesSelected);
	}

	protected void addButtonActionPerformed() {
		SpeciesContext selectedSpeciesContext = (SpeciesContext)allSpeciesContextList.getSelectedValue();
		if (selectedSpeciesContext!=null){
			getMicroscopeMeasurement().addFluorescentSpecies(selectedSpeciesContext);
		}
	}

	protected void removeButtonActionPerformed() {
		SpeciesContext selectedSpeciesContext = (SpeciesContext)fluorescentSpeciesContextList.getSelectedValue();
		if (selectedSpeciesContext!=null){
			getMicroscopeMeasurement().removeFluorescentSpecies(selectedSpeciesContext);
		}
	}

	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION --------- in MicroscopeMeasurementPanel");
		exception.printStackTrace(System.out);
	}
	
	
}
