package cbit.vcell.model.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;

import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.UtilCancelException;

import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
/**
 * Insert the type's description here.
 * Creation date: (6/24/2002 11:34:37 AM)
 * @author: Anuradha Lakshminarayana
 */
@SuppressWarnings("serial")
public class KineticsTypeTemplatePanel extends javax.swing.JPanel {
	private static final String PROPERTY_NAME_REACTION_STEP = "reactionStep";
	private ReactionStep reactionStep = null;
	private javax.swing.JComboBox kineticsTypeComboBox = null;
	private JButton jToggleButton = null;
	private ParameterTableModel ivjParameterTableModel = null;
	private ScrollTable ivjScrollPaneTable = null;

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
	
class IvjEventHandler implements java.beans.PropertyChangeListener, ActionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == KineticsTypeTemplatePanel.this && (evt.getPropertyName().equals(PROPERTY_NAME_REACTION_STEP))) { 
				getParameterTableModel().setKinetics(getKinetics());
				getKineticsTypeComboBox().setSelectedItem(getKineticType(getKinetics()));
				updateToggleButtonLabel();
			}			
		}
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getKineticsTypeComboBox()) 
				updateKineticChoice((KineticsDescription)getKineticsTypeComboBox().getSelectedItem());
		}
	}
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
/**
 * KineticsTypeTemplatePanel constructor comment.
 */
	private boolean bEditable = true;
	
	public KineticsTypeTemplatePanel() {
		this(true);
	}
	
public KineticsTypeTemplatePanel(boolean editable) {
	super();
	this.bEditable = editable;
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2006 10:10:52 PM)
 */
public void cleanupOnClose() {
	getParameterTableModel().setKinetics(null);
}


/**
 * Gets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @return The kinetics property value.
 * @see #setKinetics
 */
public Kinetics getKinetics() {
	return reactionStep == null ? null : reactionStep.getKinetics();
}

private ScrollTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new ScrollTable();
			ivjScrollPaneTable.setValidateExpressionBinding(false);
			ivjScrollPaneTable.setModel(getParameterTableModel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}

private ParameterTableModel getParameterTableModel() {
	if (ivjParameterTableModel == null) {
		try {
			ivjParameterTableModel = new ParameterTableModel(getScrollPaneTable(), bEditable);
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
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getKineticsTypeComboBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("KineticsTypeTemplatePanel");
		setLayout(new java.awt.GridBagLayout());

		int gridy = 0;
		java.awt.GridBagConstraints constraintsKineticTypeTitleLabel = new java.awt.GridBagConstraints();
		constraintsKineticTypeTitleLabel.gridx = 0; constraintsKineticTypeTitleLabel.gridy = gridy;
		constraintsKineticTypeTitleLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsKineticTypeTitleLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsKineticTypeTitleLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		JLabel label = new JLabel("Kinetic type");
		add(label, constraintsKineticTypeTitleLabel);

		java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
		constraintsJComboBox1.gridx = 1; constraintsJComboBox1.gridy = gridy;
		constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBox1.weightx = 1.0;
		constraintsJComboBox1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getKineticsTypeComboBox(), constraintsJComboBox1);
		getKineticsTypeComboBox().setEnabled(bEditable);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = gridy;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
		this.add(getJToggleButton(), gridBagConstraints);
		
		gridy ++;
		java.awt.GridBagConstraints constraintsParameterPanel = new java.awt.GridBagConstraints();
		constraintsParameterPanel.gridx = 0; 
		constraintsParameterPanel.gridy = gridy;
		constraintsParameterPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsParameterPanel.weightx = 1.0;
		constraintsParameterPanel.weighty = 1.0;
		constraintsParameterPanel.gridwidth = 3;
		add(getScrollPaneTable().getEnclosingScrollPane(), constraintsParameterPanel);
				
		initConnections();
		initKineticChoices();
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
		KineticsTypeTemplatePanel aKineticsTypeTemplatePanel;
		aKineticsTypeTemplatePanel = new KineticsTypeTemplatePanel();
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
public void setReactionStep(ReactionStep newValue) {
	ReactionStep oldValue = reactionStep;
	reactionStep = newValue;
	firePropertyChange(PROPERTY_NAME_REACTION_STEP, oldValue, newValue);
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

				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					java.awt.Component component = super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					
					if (value instanceof KineticsDescription) {
						KineticsDescription kineticsDescription = (KineticsDescription)value;
						if (getKinetics()!=null && getKinetics().getReactionStep()!=null){
							if (getKinetics().getReactionStep().getStructure() instanceof Feature){
								if (kineticsDescription.equals(KineticsDescription.General)){
									setText("General ["+MICROMOLAR+"/s]");
								}else if (kineticsDescription.equals(KineticsDescription.MassAction)){
									setText("Mass Action ["+MICROMOLAR+"/s] (recommended for stochastic application)");
								}else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
									setText("General [molecules/s]");
								}else if (kineticsDescription.equals(KineticsDescription.HMM_irreversible)){
									setText("Henri-Michaelis-Menten (Irreversible) ["+MICROMOLAR+"/s]");
								}else if (kineticsDescription.equals(KineticsDescription.HMM_reversible)){
									setText("Henri-Michaelis-Menten (Reversible) ["+MICROMOLAR+"/s]");
								}else{
									setText(kineticsDescription.getDescription());
								}
							}else if (getKinetics().getReactionStep().getStructure() instanceof Membrane){
								if (kineticsDescription.equals(KineticsDescription.General)){
									setText("General [molecules/("+SQUAREMICRON+" s)]");
								}else if (kineticsDescription.equals(KineticsDescription.MassAction)){
									setText("Mass Action [molecules/("+SQUAREMICRON+" s)]");
								}else if (kineticsDescription.equals(KineticsDescription.GeneralLumped)){
									setText("General [molecules/s)]");
								}else if (kineticsDescription.equals(KineticsDescription.HMM_irreversible)){
									setText("Henri-Michaelis-Menten (Irreversible) [molecules/("+SQUAREMICRON+" s)]");
								}else if (kineticsDescription.equals(KineticsDescription.HMM_reversible)){
									setText("Henri-Michaelis-Menten (Reversible) [molecules/("+SQUAREMICRON+" s)]");
								}else{
									setText(kineticsDescription.getDescription());
								}
							}
						}else{
							setText(kineticsDescription.getDescription());
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

private void updateKineticChoice(KineticsDescription newKineticChoice) {
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
	//
	// if same as current kinetics, don't create new one
	//
	if (getKinetics()!=null && getKinetics().getKineticsDescription().equals(newKineticChoice)){
		return;
	}
	if (!getKineticsTypeComboBox().getSelectedItem().equals(newKineticChoice)) {
		getKineticsTypeComboBox().setSelectedItem(newKineticChoice);
	}
	if (reactionStep != null) {
		try {
			if (getKinetics()==null || !getKinetics().getKineticsDescription().equals(newKineticChoice)){
				reactionStep.setKinetics(newKineticChoice.createKinetics(reactionStep));
			}
		} catch (Exception exc) {
			handleException(exc);
		}
	}
}

private void updateToggleButtonLabel(){
	final String MU = "\u03BC";
	final String MICROMOLAR = MU+"M";
	if (getKinetics() instanceof DistributedKinetics){
		getJToggleButton().setText("Convert to [molecules/s]");
		getJToggleButton().setToolTipText("convert kinetics to be in terms of molecules rather than concentration");
	}else if (getKinetics() instanceof LumpedKinetics){
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
				if (getKinetics()!=null && getKinetics().getReactionStep()!=null && getKinetics().getReactionStep().getStructure() instanceof Feature){
					sizeUnits = MU+"m"+CUBED;
				}

				if (getKinetics() instanceof DistributedKinetics){
					try {
						String response = DialogUtils.showInputDialog0(KineticsTypeTemplatePanel.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						reactionStep.setKinetics(LumpedKinetics.toLumpedKinetics((DistributedKinetics)getKinetics(), size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Current Kinetics [pA]: "+e2.getMessage(), e2);
						}else{
							DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Lumped Kinetics [molecules/s]: "+e2.getMessage(), e2);
						}
					}
 				}else if (getKinetics() instanceof LumpedKinetics){
					try {
						String response = DialogUtils.showInputDialog0(KineticsTypeTemplatePanel.this, "enter compartment size ["+sizeUnits+"]", "1.0");
						double size = Double.parseDouble(response);
						reactionStep.setKinetics(DistributedKinetics.toDistributedKinetics((LumpedKinetics)getKinetics(), size));
					} catch (UtilCancelException e1) {
					} catch (Exception e2){
						if (getKinetics().getKineticsDescription().isElectrical()){
							DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Current Density Kinetics [pA/"+MU+"m"+SQUARED+"]: "+e2.getMessage(), e2);
						}else{
							if (getKinetics().getReactionStep().getStructure() instanceof Feature){
								DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Kinetics ["+MU+"M/s]: "+e2.getMessage(), e2);
							}else{
								DialogUtils.showErrorDialog(KineticsTypeTemplatePanel.this,"failed to translate into General Kinetics [molecules/"+MU+"m"+SQUARED+".s]: "+e2.getMessage(), e2);
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

}
