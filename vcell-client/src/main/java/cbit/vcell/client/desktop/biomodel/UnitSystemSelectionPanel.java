package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.model.ModelUnitSystem;


public class UnitSystemSelectionPanel extends JPanel {
	
	private JTextField lengthTextField = null;
	private JTextField areaTextField = null;
	private JTextField volumeTextField = null;
	private JTextField timeTextField = null;
	private JTextField volumeSubstanceTextField = null;
	private JTextField membraneSubstanceTextField = null;
	private JTextField lumpedReactionSubstanceTextField = null;	
	
	private ButtonGroup buttonGroup = null;
	private JRadioButton defaultVCellUnitsButton = null;
	private JRadioButton arbitraryVCellUnitsButton = null;
	private JRadioButton arbitrarySBMLUnitsButton = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private boolean bSbmlCompatible = false;
	
	private class InternalEventHandler implements ActionListener, FocusListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == defaultVCellUnitsButton){
				ModelUnitSystem defaultUnits = ModelUnitSystem.createDefaultVCModelUnitSystem();
				initialize(defaultUnits);
				updateEnable();
			}else if (e.getSource() == arbitrarySBMLUnitsButton){
				ModelUnitSystem sbmlUnits = ModelUnitConverter.createSbmlModelUnitSystem();
				initialize(sbmlUnits);
//				membraneSubstanceTextField.setText(volumeSubstanceTextField.getText());
//				lumpedReactionSubstanceTextField.setText(volumeSubstanceTextField.getText());
				updateEnable();
			}else if (e.getSource() == arbitraryVCellUnitsButton){
				updateEnable();
			}else if (e.getSource() == volumeSubstanceTextField){
				if (buttonGroup.isSelected(arbitrarySBMLUnitsButton.getModel())){
					membraneSubstanceTextField.setText(volumeSubstanceTextField.getText());
					lumpedReactionSubstanceTextField.setText(volumeSubstanceTextField.getText());
				}
			}
		}

		public void focusLost(FocusEvent e) {
			if (e.getSource() == volumeSubstanceTextField && buttonGroup.isSelected(arbitrarySBMLUnitsButton.getModel())){
				membraneSubstanceTextField.setText(volumeSubstanceTextField.getText());
				lumpedReactionSubstanceTextField.setText(volumeSubstanceTextField.getText());
			}
		}

		public void focusGained(FocusEvent e) {
			
		}
		
	}
	
	public UnitSystemSelectionPanel(){
		super();
		initialize();
	}
	
	public UnitSystemSelectionPanel(boolean argbSbmlCompatible){
		super();
		bSbmlCompatible = argbSbmlCompatible;
		initialize();
	}


	private void initialize(){
		JPanel inputPanel = new JPanel(new GridBagLayout());
		
		//------------------------------------- row 0 ... "type"    "unit"     "VCell default"
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("type",JLabel.RIGHT), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("unit",JLabel.CENTER), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("VCell default",JLabel.LEFT), gbc);

		//------------------------------------- row 1 ... "length unit" [ um ]  (default 'um')
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("length",JLabel.RIGHT), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		lengthTextField = new JTextField("um");
		inputPanel.add(lengthTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("[um]",JLabel.LEFT), gbc);

		//------------------------------------- row 2 ... "area unit" [ um2 ]  (default 'um2')
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("area",JLabel.RIGHT), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		areaTextField = new JTextField("um2");
		inputPanel.add(areaTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("[um2]",JLabel.LEFT), gbc);

		//------------------------------------- row 3 ... "volume unit" [ um3 ]  (default 'um3')
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("volume",JLabel.RIGHT), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		volumeTextField = new JTextField("um3");
		inputPanel.add(volumeTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("[um3]",JLabel.LEFT), gbc);

		//------------------------------------- row 4 ... "time unit" [ s ]  (default 's')
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("time",JLabel.RIGHT), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		timeTextField = new JTextField("s");
		inputPanel.add(timeTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("[s]",JLabel.LEFT), gbc);

		//------------------------------------- row 5 ... "volume species substance unit" [ uM.um3 ]  (default 's')
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("volume species substance",JLabel.RIGHT), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		volumeSubstanceTextField = new JTextField("uM.um3");
		inputPanel.add(volumeSubstanceTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("[uM.um3]",JLabel.LEFT), gbc);

		//------------------------------------- row 6 ... "membrane species substance unit" [ molecules.um-2 ]  (default 'molecules.um-2')
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("membrane species substance",JLabel.RIGHT), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		membraneSubstanceTextField = new JTextField("molecules");
		inputPanel.add(membraneSubstanceTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("[molecules]",JLabel.LEFT), gbc);

		//------------------------------------- row 6 ... "lumped reaction substance unit" [ molecules.s-1 ]  (default 'molecules.s-1')
		gridy++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("lumped reaction substance",JLabel.RIGHT), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(4,4,4,4);
		lumpedReactionSubstanceTextField = new JTextField("molecules");
		inputPanel.add(lumpedReactionSubstanceTextField, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.2;
		gbc.insets = new Insets(4,4,4,4);
		inputPanel.add(new JLabel("[molecules]",JLabel.LEFT), gbc);
		
		lengthTextField.setColumns(12); // make sure leaves enough space for text boxes.

		lengthTextField.setEnabled(false);
		areaTextField.setEnabled(false);
		volumeTextField.setEnabled(false);
		timeTextField.setEnabled(false);
		volumeSubstanceTextField.setEnabled(false);
		membraneSubstanceTextField.setEnabled(false);
		lumpedReactionSubstanceTextField.setEnabled(false);
		
		volumeSubstanceTextField.addActionListener(eventHandler);
		volumeSubstanceTextField.addFocusListener(eventHandler);


		// ------------------------- select type of unit system (vcell default, general SBML, general VCell)
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonGroup = new ButtonGroup();
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4,4,4,4);
		defaultVCellUnitsButton = new JRadioButton("default");
		defaultVCellUnitsButton.addActionListener(eventHandler);
		buttonPanel.add(defaultVCellUnitsButton,gbc);
		buttonGroup.add(defaultVCellUnitsButton);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4,4,4,4);
		arbitrarySBMLUnitsButton = new JRadioButton("sbml compatable");
		arbitrarySBMLUnitsButton.addActionListener(eventHandler);
		buttonPanel.add(arbitrarySBMLUnitsButton,gbc);
		buttonGroup.add(arbitrarySBMLUnitsButton);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(4,4,4,4);
		arbitraryVCellUnitsButton = new JRadioButton("general");
		arbitraryVCellUnitsButton.addActionListener(eventHandler);
		buttonPanel.add(arbitraryVCellUnitsButton,gbc);
		buttonGroup.add(arbitraryVCellUnitsButton);
		
		buttonGroup.setSelected(defaultVCellUnitsButton.getModel(), true);
		
		setLayout(new BorderLayout());
		
		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
	}
	
	public ModelUnitSystem createModelUnitSystem(){
		return ModelUnitSystem.createVCModelUnitSystem(volumeSubstanceTextField.getText(),membraneSubstanceTextField.getText(),lumpedReactionSubstanceTextField.getText(),volumeTextField.getText(),areaTextField.getText(),lengthTextField.getText(),timeTextField.getText());
	}
	
	public void initialize(ModelUnitSystem modelUnitSystem){
		
		if (modelUnitSystem.compareEqual(ModelUnitSystem.createDefaultVCModelUnitSystem())){
			defaultVCellUnitsButton.setSelected(true);
		}else if (modelUnitSystem.compareEqual(ModelUnitConverter.createSbmlModelUnitSystem())) {
			arbitrarySBMLUnitsButton.setSelected(true);
		}else{
			arbitraryVCellUnitsButton.setSelected(true);
		}
		
		// if (bSbmlCompatible flag is set, disable the radio buttons (this is used while invoking this panel in BNGOutputPanel to set units on BNG generated sbml)
		if (bSbmlCompatible) {
			defaultVCellUnitsButton.setEnabled(false);
			arbitrarySBMLUnitsButton.setEnabled(false);
			arbitraryVCellUnitsButton.setEnabled(false);
		}
		
		lengthTextField.setText(modelUnitSystem.getLengthUnit().getSymbol());
		areaTextField.setText(modelUnitSystem.getAreaUnit().getSymbol());
		volumeTextField.setText(modelUnitSystem.getVolumeUnit().getSymbol());
		timeTextField.setText(modelUnitSystem.getTimeUnit().getSymbol());
		volumeSubstanceTextField.setText(modelUnitSystem.getVolumeSubstanceUnit().getSymbol());
		membraneSubstanceTextField.setText(modelUnitSystem.getMembraneSubstanceUnit().getSymbol());
		lumpedReactionSubstanceTextField.setText(modelUnitSystem.getLumpedReactionSubstanceUnit().getSymbol());
		
		updateEnable();
	}
	
	private void updateEnable(){
		if (buttonGroup.isSelected(defaultVCellUnitsButton.getModel())){
			lengthTextField.setEnabled(false);
			areaTextField.setEnabled(false);
			volumeTextField.setEnabled(false);
			timeTextField.setEnabled(false);
			volumeSubstanceTextField.setEnabled(false);
			membraneSubstanceTextField.setEnabled(false);
			lumpedReactionSubstanceTextField.setEnabled(false);
		}else if (buttonGroup.isSelected(arbitrarySBMLUnitsButton.getModel())){
			lengthTextField.setEnabled(true);
			areaTextField.setEnabled(true);
			volumeTextField.setEnabled(true);
			timeTextField.setEnabled(true);
			volumeSubstanceTextField.setEnabled(true);
			membraneSubstanceTextField.setEnabled(false);
			lumpedReactionSubstanceTextField.setEnabled(false);
		}else if (buttonGroup.isSelected(arbitraryVCellUnitsButton.getModel())){
			lengthTextField.setEnabled(true);
			areaTextField.setEnabled(true);
			volumeTextField.setEnabled(true);
			timeTextField.setEnabled(true);
			volumeSubstanceTextField.setEnabled(true);
			membraneSubstanceTextField.setEnabled(true);
			lumpedReactionSubstanceTextField.setEnabled(true);
		}
	}
	
	public static void main(String[] args){
		try {
			javax.swing.JFrame jframe = new javax.swing.JFrame();
			UnitSystemSelectionPanel unitSystemPanel = new UnitSystemSelectionPanel();
			jframe.setContentPane(unitSystemPanel);
			jframe.setSize(500,500);
			jframe.addWindowListener(new WindowListener() {
				public void windowOpened(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void windowDeactivated(WindowEvent e) {}
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
				public void windowClosed(WindowEvent e) {}
				public void windowActivated(WindowEvent e) {}
			});
			jframe.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
