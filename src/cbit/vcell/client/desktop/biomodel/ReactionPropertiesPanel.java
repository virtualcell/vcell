package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.UtilCancelException;

import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.gui.ParameterTableModel;
import cbit.vcell.model.gui.ReactionElectricalPropertiesPanel;

@SuppressWarnings("serial")
public class ReactionPropertiesPanel extends DocumentEditorSubPanel {
	private ReactionStep reactionStep = null;
	private javax.swing.JComboBox kineticsTypeComboBox = null;
	private JButton jToggleButton = null;
	private ParameterTableModel ivjParameterTableModel = null;
	private ScrollTable ivjScrollPaneTable = null;
	private EventHandler eventHandler = new EventHandler();
	private ReactionElectricalPropertiesPanel reactionElectricalPropertiesPanel;
	private JTextArea annotationTextArea = null;
	private JTextField nameTextField = null;
	private JLabel electricalPropertiesLabel;
	
	private final static KineticsDescription[] Simple_Reaction_Kinetic_Types = {
		KineticsDescription.MassAction,
		KineticsDescription.General,
		KineticsDescription.GeneralLumped,
		KineticsDescription.HMM_irreversible,
		KineticsDescription.HMM_reversible
	};
	
	private final static KineticsDescription[] Flux_Reaction_KineticTypes = {
			KineticsDescription.General,
			KineticsDescription.GeneralLumped,
			KineticsDescription.GeneralCurrent,
			KineticsDescription.GeneralCurrentLumped,
			KineticsDescription.GHK,
			KineticsDescription.Nernst
	};
	
	private class EventHandler implements ActionListener, ListSelectionListener, FocusListener, PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getKineticsTypeComboBox()) { 
				updateKineticChoice();
			} else if (e.getSource() == nameTextField) {
				changeName();
			}
		}
		public void valueChanged(ListSelectionEvent e) {
			tableSelectionChanged();			
		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == nameTextField) {
				changeName();
			}
		}
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == reactionStep) {
				updateInterface();
			}			
		}
	}
	
public ReactionPropertiesPanel() {
	super();
	initialize();
}

private void tableSelectionChanged() {
	int[] rows = getScrollPaneTable().getSelectedRows();
	List<Object> selectedObjects = new ArrayList<Object>();
	for (int i = 0; i < rows.length; i++) {
		Parameter object = getParameterTableModel().getValueAt(rows[i]);
		if (!(object instanceof Kinetics.KineticsProxyParameter)) {
			selectedObjects.add(object);
		}
	}
	if (selectedObjects.size() > 0 || rows.length == 0) {
		setSelectedObjects(selectedObjects.toArray());
	}	
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 10:10:52 PM)
 */
public void cleanupOnClose() {
	getParameterTableModel().setReactionStep(null);
}

private ScrollTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new ScrollTable();
			ivjScrollPaneTable.setValidateExpressionBinding(false);
			ivjScrollPaneTable.setModel(getParameterTableModel());
			ivjScrollPaneTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

private ParameterTableModel getParameterTableModel() {
	if (ivjParameterTableModel == null) {
		try {
			ivjParameterTableModel = new ParameterTableModel(getScrollPaneTable(), true);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjParameterTableModel;
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
		setLayout(new java.awt.GridBagLayout());
		
		nameTextField = new JTextField();
		nameTextField.setEditable(false);
		nameTextField.addFocusListener(eventHandler);
		nameTextField.addActionListener(eventHandler);
		
		electricalPropertiesLabel = new JLabel("Electrical Properties");
		electricalPropertiesLabel.setVisible(false);

		reactionElectricalPropertiesPanel = new ReactionElectricalPropertiesPanel();
		reactionElectricalPropertiesPanel.setVisible(false);
		
		int gridy = 0;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel label = new JLabel("<html><u>Select only one reaction to edit properties</u></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		add(label, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Reaction Name"), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(nameTextField, gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.anchor = java.awt.GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);		
		add(electricalPropertiesLabel, gbc);
			
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.gridwidth = 2;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(0, 4, 0, 4);
		add(reactionElectricalPropertiesPanel, gbc);
		
		gridy ++;		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		add(new JLabel("Kinetic Type"), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		add(getKineticsTypeComboBox(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(0, 4, 4, 4);
		add(getJToggleButton(), gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 3;
		add(getScrollPaneTable().getEnclosingScrollPane(), gbc);
		
		gridy ++;
		annotationTextArea = new javax.swing.JTextArea(2,20);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setWrapStyleWord(true);
		javax.swing.JScrollPane jsp = new javax.swing.JScrollPane(annotationTextArea);
		CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Add Annotation Here", false);
		collapsiblePanel.setLayout(new GridBagLayout());
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		collapsiblePanel.add(jsp, gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(collapsiblePanel, gbc);
		
		setBackground(Color.white);
		
		getKineticsTypeComboBox().addActionListener(eventHandler);
		getKineticsTypeComboBox().setEnabled(false);
		initKineticChoices();
		
		getScrollPaneTable().getSelectionModel().addListSelectionListener(eventHandler);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ReactionPropertiesPanel aKineticsTypeTemplatePanel;
		aKineticsTypeTemplatePanel = new ReactionPropertiesPanel();
		frame.setContentPane(aKineticsTypeTemplatePanel);
		frame.setSize(aKineticsTypeTemplatePanel.getSize());
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
private void setReactionStep(ReactionStep newValue) {
	if (reactionStep == newValue) {
		return;
	}
	ReactionStep oldValue = reactionStep;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(eventHandler);
	}
	// commit the changes before switch to another reaction step
	changeName();
	reactionStep = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(eventHandler);
	}
	getParameterTableModel().setReactionStep(reactionStep);
	updateInterface();
}

private javax.swing.JComboBox getKineticsTypeComboBox() {
	if (kineticsTypeComboBox == null) {
		try {
			kineticsTypeComboBox = new javax.swing.JComboBox();
			kineticsTypeComboBox.setName("JComboBox1");
			kineticsTypeComboBox.setRenderer(new DefaultListCellRenderer() {
				private final static String MU = "\u03BC";
				private final static String MICROMOLAR = MU+"M";
				private final static String SQUARED = "\u00B2";
				private final static String SQUAREMICRON = MU+"m"+SQUARED;
				private final static String MICRON = MU+"m";

				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					java.awt.Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					
					if (value instanceof KineticsDescription) {
						KineticsDescription kineticsDescription = (KineticsDescription)value;
						setText(kineticsDescription.getDescription());
						if (reactionStep != null){
							if (reactionStep instanceof SimpleReaction) {
								if (reactionStep.getStructure() instanceof Feature){
									if (kineticsDescription.equals(KineticsDescription.General)){
										setText("General ["+MICROMOLAR+"/s]");
									} else if (kineticsDescription.equals(KineticsDescription.MassAction)){
										setText("Mass Action ["+MICROMOLAR+"/s] (recommended for stochastic application)");
									} else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
										setText("General [molecules/s]");
									} else if (kineticsDescription.equals(KineticsDescription.HMM_irreversible)){
										setText("Henri-Michaelis-Menten (Irreversible) ["+MICROMOLAR+"/s]");
									} else if (kineticsDescription.equals(KineticsDescription.HMM_reversible)){
										setText("Henri-Michaelis-Menten (Reversible) ["+MICROMOLAR+"/s]");
									} else{
										setText(kineticsDescription.getDescription());
									}
								}else if (reactionStep.getStructure() instanceof Membrane){
									if (kineticsDescription.equals(KineticsDescription.General)){
										setText("General [molecules/("+SQUAREMICRON+" s)]");
									} else if (kineticsDescription.equals(KineticsDescription.MassAction)){
										setText("Mass Action [molecules/("+SQUAREMICRON+" s)]");
									} else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
										setText("General [molecules/s)]");
									} else if (kineticsDescription.equals(KineticsDescription.HMM_irreversible)){
										setText("Henri-Michaelis-Menten (Irreversible) [molecules/("+SQUAREMICRON+" s)]");
									} else if (kineticsDescription.equals(KineticsDescription.HMM_reversible)){
										setText("Henri-Michaelis-Menten (Reversible) [molecules/("+SQUAREMICRON+" s)]");
									}
								}
							} else if (reactionStep instanceof FluxReaction) {
								if (kineticsDescription.equals(KineticsDescription.General)){
									setText("General Flux Density ("+MICROMOLAR+"-"+MICRON+"/s)");
								} else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
									setText("General Flux (molecules/s)");
								} else if (kineticsDescription.equals(KineticsDescription.GeneralCurrent)){
									setText("General Current Density (pA/"+SQUAREMICRON+")");
								} else if (kineticsDescription.equals(KineticsDescription.GeneralCurrentLumped)){
									setText("General Current (pA)");
								} else if (kineticsDescription.equals(KineticsDescription.GHK)){
									setText("Goldman-Hodgkin-Katz Current Density (pA/"+SQUAREMICRON+") - permeability in "+MICRON+"/s");
								} else if (kineticsDescription.equals(KineticsDescription.Nernst)){
									setText("Nernst Current Density (pA/"+SQUAREMICRON+") - conductance in nS/"+SQUAREMICRON);
								}
							}
						} 
					}

					return component;
				}
				
			});
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return kineticsTypeComboBox;
}

private void updateKineticChoice() {
	KineticsDescription newKineticChoice = (KineticsDescription)getKineticsTypeComboBox().getSelectedItem();
	//
	// if same as current kinetics, don't create new one
	//
	if (reactionStep == null || reactionStep.getKinetics().getKineticsDescription().equals(newKineticChoice)){
		return;
	}
	boolean bFoundKineticType = false;
	KineticsDescription[] kineticTypes = reactionStep instanceof SimpleReaction ? Simple_Reaction_Kinetic_Types : Flux_Reaction_KineticTypes;
	for (int i=0;i<kineticTypes.length;i++){
		if (kineticTypes[i].equals(newKineticChoice)){
			bFoundKineticType = true;
			break;
		}
	}
	if (!bFoundKineticType){
		return;
	}
	
	try {
		reactionStep.setKinetics(newKineticChoice.createKinetics(reactionStep));
	} catch (Exception exc) {
		handleException(exc);
	}
}

private void updateToggleButtonLabel(){
	final String MU = "\u03BC";
	final String MICROMOLAR = MU+"M";
	if (reactionStep.getKinetics() instanceof DistributedKinetics){
		getJToggleButton().setText("Convert to [molecules/s]");
		getJToggleButton().setToolTipText("convert kinetics to be in terms of molecules rather than concentration");
	}else if (reactionStep.getKinetics() instanceof LumpedKinetics){
		getJToggleButton().setText("Convert to ["+MICROMOLAR+"/s]");
		getJToggleButton().setToolTipText("convert kinetics to be in terms of concentration rather than molecules");
	}
}

private KineticsDescription getKineticType(Kinetics kinetics) {
	if (kinetics!=null){
		return kinetics.getKineticsDescription();
	}else{
		return null;
	}
}


private JButton getJToggleButton() {
	if (jToggleButton == null) {
		jToggleButton = new JButton("Convert");
		jToggleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				final String MU = "\u03BC";
				final String SQUARED = "\u00B2";
				final String CUBED = "\u00B3";
				String sizeUnits = MU+"m"+SQUARED;
				Kinetics kinetics = reactionStep.getKinetics();
				if (kinetics!=null && kinetics.getReactionStep()!=null && kinetics.getReactionStep().getStructure() instanceof Feature){
					sizeUnits = MU+"m"+CUBED;
				}

				if (kinetics instanceof DistributedKinetics){
					try {
						String response = DialogUtils.showInputDialog0(ReactionPropertiesPanel.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						reactionStep.setKinetics(LumpedKinetics.toLumpedKinetics((DistributedKinetics)kinetics, size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (kinetics.getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Current Kinetics [pA]: "+e2.getMessage(), e2);
						}else{
							DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Lumped Kinetics [molecules/s]: "+e2.getMessage(), e2);
						}
					}
 				}else if (kinetics instanceof LumpedKinetics){
					try {
						String response = DialogUtils.showInputDialog0(ReactionPropertiesPanel.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						reactionStep.setKinetics(DistributedKinetics.toDistributedKinetics((LumpedKinetics)kinetics, size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (kinetics.getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Current Density Kinetics [pA/"+MU+"m"+SQUARED+"]: "+e2.getMessage(), e2);
						}else{
							if (kinetics.getReactionStep().getStructure() instanceof Feature){
								DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Kinetics ["+MU+"M/s]: "+e2.getMessage(), e2);
							}else{
								DialogUtils.showErrorDialog(ReactionPropertiesPanel.this,"failed to translate into General Kinetics [molecules/"+MU+"m"+SQUARED+".s]: "+e2.getMessage(), e2);
							}
						}
					}
 				}
			}
		});
	}
	return jToggleButton;
}

private void initKineticChoices() {
	KineticsDescription[] kineticTypes = reactionStep == null || reactionStep instanceof SimpleReaction ? Simple_Reaction_Kinetic_Types : Flux_Reaction_KineticTypes;
	javax.swing.DefaultComboBoxModel model = new DefaultComboBoxModel();
	for (int i=0;i<kineticTypes.length;i++){
		model.addElement(kineticTypes[i]);
	}
	getKineticsTypeComboBox().setModel(model);
	
	return;
}

protected void updateInterface() {
	boolean bNonNullReactionStep = reactionStep != null;
	nameTextField.setEditable(bNonNullReactionStep);
	getParameterTableModel().setEditable(bNonNullReactionStep);
	kineticsTypeComboBox.setEnabled(bNonNullReactionStep);
	BeanUtils.enableComponents(reactionElectricalPropertiesPanel, bNonNullReactionStep);
	jToggleButton.setEnabled(bNonNullReactionStep);
	if (bNonNullReactionStep) {
		initKineticChoices();
		boolean bMembrane = reactionStep.getStructure() instanceof Membrane;
		electricalPropertiesLabel.setVisible(bMembrane);
		reactionElectricalPropertiesPanel.setVisible(bMembrane);		
		reactionElectricalPropertiesPanel.setKinetics(reactionStep.getKinetics());
		getKineticsTypeComboBox().setSelectedItem(getKineticType(reactionStep.getKinetics()));
		updateToggleButtonLabel();
		nameTextField.setText(reactionStep.getName());
	} else {
		nameTextField.setText(null);
		electricalPropertiesLabel.setVisible(false);
		reactionElectricalPropertiesPanel.setVisible(false);

	}
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	if (selectedObjects == null || selectedObjects.length != 1) {
		return;
	}
	if (selectedObjects[0] instanceof ReactionStep) {
		setReactionStep((ReactionStep) selectedObjects[0]);
	} else if (selectedObjects[0] instanceof KineticsParameter) {
		KineticsParameter kineticsParameter = (KineticsParameter) selectedObjects[0];
		setReactionStep(kineticsParameter.getKinetics().getReactionStep());
		setTableSelections(selectedObjects, getScrollPaneTable(), getParameterTableModel());
	} else {
		setReactionStep(null);
	}
}

private void changeName() {
	if (reactionStep == null) {
		return;
	}
	String newName = nameTextField.getText();
	if (newName == null || newName.length() == 0) {
		nameTextField.setText(reactionStep.getName());
		return;
	}
	if (newName.equals(reactionStep.getName())) {
		return;
	}
	try {
		reactionStep.setName(newName);
	} catch (PropertyVetoException e1) {
		e1.printStackTrace();
		DialogUtils.showErrorDialog(this, e1.getMessage());
	}
}

}
