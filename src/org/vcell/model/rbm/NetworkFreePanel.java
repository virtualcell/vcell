package org.vcell.model.rbm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.model.rbm.common.NetworkConstraintsEntity;
import org.vcell.util.BeanUtils;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.BioModelEditor;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.RbmTableRenderer;
import cbit.vcell.client.desktop.biomodel.SelectionManager;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.desktop.biomodel.SimulationConsolePanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CreateBNGOutputSpec;
import cbit.vcell.client.task.ReturnBNGOutput;
import cbit.vcell.client.task.RunBioNetGen;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.NetworkTransformer;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;
import cbit.vcell.mapping.gui.NetworkConstraintsTableModel;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.Species;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.bionetgen.BNGExecutorService;
import cbit.vcell.server.bionetgen.BNGInput;
import cbit.vcell.solvers.ApplicationMessage;

// we should use WindowBuilder Plugin (add it to Eclipse IDE) to speed up panel design
// can choose absolute layout and place everything exactly as we see fit
@SuppressWarnings("serial")
public class NetworkFreePanel extends JPanel implements ApplicationSpecificationsPanel.Specifier {

	private EventHandler eventHandler = new EventHandler();
	private SimulationContext fieldSimulationContext;
	private IssueManager fieldIssueManager;
	private SelectionManager fieldSelectionManager;
	
	private JLabel speciesLabel;
	private JLabel reactionsLabel;
	private JLabel molecularTypesLabel;
	private JLabel observablesLabel;

	private static String infoText = "<html>Create a new Rule-Based VCell BioModel. <br>"
			+ "Existing Molecules, Rules, Species consisting of Molecules and Observables will be transferred to the new BioModel"
			+ "</html>";

	private JButton createModelButton;
	
	private class EventHandler implements FocusListener, ActionListener, PropertyChangeListener {

		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == getCreateModelButton()) {
				createModel();
			}
		}

		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
		}
		
		public void propertyChange(java.beans.PropertyChangeEvent event) {
			if(event.getSource() instanceof Model && event.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST)) {
				refreshInterface();
			}
		}
	}
	
	ApplicationMessage getApplicationMessage() {
		return new ApplicationMessage(ApplicationMessage.PROGRESS_MESSAGE, 2, 1, "some progress error", "some progress message");
	}
	public NetworkFreePanel() {
		super();
		initialize();
	}
	
	@Override
	public ActiveViewID getActiveView() {
		return ActiveViewID.network_free_setting;
	}
	/**
	 * no-op 
	 */
	@Override
	public void setSearchText(String s) {
		
	}

	private JButton getCreateModelButton() {
		if (createModelButton == null) {
			createModelButton = new javax.swing.JButton(" Create new Rule-Based VCell BioModel ");
			createModelButton.setName("CreateModelButton");
		}
		return createModelButton;
	}

	private void initialize() {
		speciesLabel = new JLabel("3");
		reactionsLabel = new JLabel("4");
		molecularTypesLabel = new JLabel("2");
		observablesLabel = new JLabel("6");

		Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 3, 2, 1);
		add(mainPanel, gbc);
		
		JPanel top = new JPanel();
		TitledBorder titleTop = BorderFactory.createTitledBorder(loweredEtchedBorder, " Statistics ");
		titleTop.setTitleJustification(TitledBorder.LEFT);
		titleTop.setTitlePosition(TitledBorder.TOP);
		top.setBorder(titleTop);
		top.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);	//  top, left, bottom, right 
		mainPanel.add(top, gbc);

		JPanel bottom = new JPanel();
		TitledBorder titleBottom = BorderFactory.createTitledBorder(loweredEtchedBorder, " Network-free ");
		titleBottom.setTitleJustification(TitledBorder.LEFT);
		titleBottom.setTitlePosition(TitledBorder.TOP);
		bottom.setBorder(titleBottom);
		bottom.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 2, 2, 3);
		mainPanel.add(bottom, gbc);
		
		// ------------------------------------------- Populating the top group box ---------------
		int gridy = 0;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);	//  top, left, bottom, right 
		top.add(new JLabel("Species: "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		top.add(speciesLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 10);
		top.add(new JLabel(""), gbc);
		
		gridy++;	// -------------------------------------------
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		top.add(new JLabel("Reactions: "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		top.add(reactionsLabel, gbc);
		
		gridy++;	// -------------------------------------------
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		top.add(new JLabel("Molecules: "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		top.add(molecularTypesLabel, gbc);
		
		gridy++;	// -------------------------------------------
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		top.add(new JLabel("Observables: "), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		top.add(observablesLabel, gbc);
		
		// ------------------------------------------- Populating the bottom group box ---------------
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 10);
		bottom.add(new JLabel(infoText), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 2;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(4, 4, 4, 10);
		bottom.add(getCreateModelButton(), gbc);
		
		getCreateModelButton().addActionListener(eventHandler);

	}
	
	public void setSimulationContext(SimulationContext simulationContext) {
		if(simulationContext == null) {
			return;
		}
		if(fieldSimulationContext != null) {
			fieldSimulationContext.removePropertyChangeListener(eventHandler);
		}
		fieldSimulationContext = simulationContext;
		fieldSimulationContext.addPropertyChangeListener(eventHandler);
		
		Model m = fieldSimulationContext.getModel();
		if(m != null) {
			m.removePropertyChangeListener(eventHandler);
			m.addPropertyChangeListener(eventHandler);
		}
		refreshInterface();
	}
	public SimulationContext getSimulationContext() {
		return fieldSimulationContext;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		fieldSelectionManager = selectionManager;
	}
	public void setIssueManager(IssueManager issueManager) {
		fieldIssueManager = issueManager;
	}
	
	private void refreshInterface() {
		String speciesText = "";
		String reactionsText = "";
		
		int s1 = 0;
		int s2 = 0;
		for(SpeciesContext sc : fieldSimulationContext.getModel().getSpeciesContexts()) {
			if(sc.hasSpeciesPattern()) {
				s1++;
			} else {
				s2++;
			}
		}
		int s3 = fieldSimulationContext.getModel().getNumSpeciesContexts();
		speciesText = s3 + "   ( out of which " + s1 + " have species pattern and " + s2 + " do not ).";

		int r1 = fieldSimulationContext.getModel().getNumReactions();
		int r2 = fieldSimulationContext.getModel().getRbmModelContainer().getReactionRuleList().size();
		reactionsText = (r1+r2) + "   ( out of which " + r1 + " are simple reactions and " + r2 + " are reaction rules ).";
		
		speciesLabel.setText(speciesText);
		reactionsLabel.setText(reactionsText);
		molecularTypesLabel.setText(fieldSimulationContext.getModel().getRbmModelContainer().getMolecularTypeList().size()+"");
		observablesLabel.setText(fieldSimulationContext.getModel().getRbmModelContainer().getObservableList().size()+"");
		
		createModelButton.setEnabled(true);
	}
	

	
	private void createModel() {

		refreshInterface();
		
//		DocumentWindow dw = (DocumentWindow)BeanUtils.findTypeParentOfComponent(this, DocumentWindow.class);
//		BioModelWindowManager bmwm = (BioModelWindowManager)(dw.getTopLevelWindowManager());
//		RequestManager rm = dw.getTopLevelWindowManager().getRequestManager();
//			
//		rm.createBioModelFromApplication(bmwm, "Test", fieldSimulationContext);
	}

	@Override		// for testing/debugging purposes
	public void setVisible(boolean bVisible) {
		super.setVisible(bVisible);
	}
	
	public void activateConsole() {
		boolean found = false;
		Container parent = getParent();
		while(parent != null) {
			parent = parent.getParent();
			if(parent instanceof BioModelEditor) {
				found = true;
				break;
			}
		}
		if(found) {
			System.out.println("Parent Found");
			BioModelEditor e = (BioModelEditor)parent;
//			e.getRightBottomTabbedPane().setSelectedComponent(e.getSimulationConsolePanel());
			Component[] cList = e.getRightBottomTabbedPane().getComponents();
			for(Component c : cList) {
				if(c instanceof SimulationConsolePanel) {
					e.getRightBottomTabbedPane().setSelectedComponent(c);
					break;
				}
			}
		}
	}


}
