package org.vcell.chombo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SolverTaskDescription;

import com.lowagie.text.Font;

@SuppressWarnings("serial")
public class ChomboSolverSpecPanel extends CollapsiblePanel {

	private final static String CMD_REF_RATIO = "RefRatio";
	private class IvjEventHandler implements ActionListener, FocusListener, PropertyChangeListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getAddLevelButton()) {
				addLevelButton_actionPerformed();
			} else if (e.getSource() == getDeleteLevelButton()) {
				deleteLevelButton_actionPerformed();
			} else if (e.getSource() == maxBoxSizeComboBox)
			{
				setMaxBoxSize();
			}
			else {
				String cmd = e.getActionCommand();	
				
				if (cmd.startsWith(CMD_REF_RATIO)) {
					StringTokenizer st = new StringTokenizer(cmd);
					st.nextToken();
					int index = Integer.parseInt(st.nextToken());
					setRefRatio(index);
				}
			}
		};
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == solverTaskDescription && evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION)) {
				updateDisplay();
			}
			else if (evt.getSource() == solverTaskDescription.getChomboSolverSpec())
			{
				if (evt.getPropertyName().equals(ChomboSolverSpec.PROPERTY_NAME_MAX_BOX_SIZE))
				{
					maxBoxSizeComboBox.setSelectedItem(solverTaskDescription.getChomboSolverSpec().getMaxBoxSize());
				}
			}
		}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			if (e.getSource() == fillRatioTextField)
			{
				if (fillRatioTextField.getText() == null || fillRatioTextField.getText().trim().length() == 0)
				{
					fillRatioTextField.setText(solverTaskDescription.getChomboSolverSpec().getFillRatio() + "");
				}
				else
				{
					setFillRatio();
				}
			}
			else if (e.getSource() == roiTextField)
			{
				try {
					setRefinementRoiExpression();
				} catch (ExpressionException e1) {
					//DialogUtils.showErrorDialog(ChomboSolverSpecPanel.this, e1.getMessage(), e1);
				}
			}
		};
	};
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton addLevelButton;
	private JButton deleteLevelButton;
	private ArrayList<JPanel> levelPanels = new ArrayList<JPanel>();
	private List<JComboBox> refinementRatioComboBoxes = new ArrayList<JComboBox>();
	private SolverTaskDescription solverTaskDescription = null;
	private JPanel mainPanel = null;
	private JComboBox maxBoxSizeComboBox = null;
	private JTextField fillRatioTextField;
	private JTextField roiTextField = null;
	
	public ChomboSolverSpecPanel() {
		super("Chombo Options");
		initialize();
	}

	private void initialize() 
	{
		setMaximumSize(new Dimension(500, 150));
		setPreferredSize(new Dimension(500, 200));
		maxBoxSizeComboBox = new JComboBox();
		maxBoxSizeComboBox.addItem(new Integer(8));
		maxBoxSizeComboBox.addItem(new Integer(16));
		maxBoxSizeComboBox.addItem(new Integer(32));
		maxBoxSizeComboBox.addItem(new Integer(64));
		maxBoxSizeComboBox.addItem(new Integer(128));
		maxBoxSizeComboBox.addItem(new Integer(256));
		maxBoxSizeComboBox.addItem(new Integer(512));
		maxBoxSizeComboBox.addItem(new Integer(1024));
		maxBoxSizeComboBox.addItem(new Integer(2048));
		maxBoxSizeComboBox.addItem(new Integer(4096));
		
		fillRatioTextField = new JTextField(10);
		fillRatioTextField.addFocusListener(ivjEventHandler);
		
		JPanel northPanel = new JPanel(new GridBagLayout());
		northPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		northPanel.add(new JLabel("Max Box Size"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		northPanel.add(maxBoxSizeComboBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);		
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.EAST;
		northPanel.add(new JLabel("Fill Ratio"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		northPanel.add(fillRatioTextField, gbc);
		
		JToolBar toolbar = new JToolBar();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
		toolbar.setFloatable(false);
		JLabel label = new JLabel("Levels");
		toolbar.add(label);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		toolbar.add(Box.createHorizontalStrut(10));
		toolbar.add(getAddLevelButton());
		toolbar.add(getDeleteLevelButton());
		
		JPanel roiPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(2, 5, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		roiPanel.add(new JLabel("Refinement ROI"), gbc);
		
		roiTextField = new JTextField();
		roiTextField.addFocusListener(ivjEventHandler);
		roiTextField.setEnabled(false);
		roiTextField.setVerifyInputWhenFocusTarget(true);
		roiTextField.setInputVerifier(new InputVerifier() {
			
			@Override
			public boolean verify(JComponent input) {
				return false;
			}

			@Override
			public boolean shouldYieldFocus(JComponent input) {		
				String roi = roiTextField.getText();
				try {
					solverTaskDescription.getChomboSolverSpec().setRefinementRoiExpression(roi);
					roiTextField.setBorder(UIManager.getBorder("TextField.border"));
					return true;
				} catch (ExpressionException ex) {
					ex.printStackTrace(System.out);
					DialogUtils.showErrorDialog(ChomboSolverSpecPanel.this, "Invalid ROI: " + ex.getMessage());
					roiTextField.setBorder(GuiConstants.ProblematicTextFieldBorder);
					javax.swing.SwingUtilities.invokeLater(new Runnable() { 
				    public void run() { 
				      roiTextField.requestFocus();
				    } 	
					});
				}
				return false;
			}
		});
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(2, 5, 0, 50);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.9;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		roiPanel.add(roiTextField, gbc);		
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(2, 5, 0, 50);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
		label = new JLabel("(If no ROI is provided, all membrane elements will be refined.)");
		label.setFont(label.getFont().deriveFont(Font.ITALIC, label.getFont().getSize() - 1));
		roiPanel.add(label, gbc);		
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(2, 5, 0, 0);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0.1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		roiPanel.add(Box.createGlue(), gbc);		
		
		mainPanel  = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		CollapsiblePanel refinementPanel = new CollapsiblePanel("Mesh Refinement");
		refinementPanel.getContentPanel().setLayout(new GridBagLayout());
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		refinementPanel.getContentPanel().add(roiPanel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(5, 30, 0, 30);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		refinementPanel.getContentPanel().add(new JSeparator(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.insets = new java.awt.Insets(0, 5, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		refinementPanel.getContentPanel().add(toolbar, gbc);
		
		JScrollPane scroll = new JScrollPane(mainPanel);
		scroll.setBorder(null);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		refinementPanel.getContentPanel().add(scroll, gbc);
		
		getContentPanel().setLayout(new BorderLayout(0, 2));
		getContentPanel().add(northPanel, BorderLayout.NORTH);
		getContentPanel().add(refinementPanel, BorderLayout.CENTER);
		
		maxBoxSizeComboBox.addActionListener(ivjEventHandler);
		getAddLevelButton().addActionListener(ivjEventHandler);
		getDeleteLevelButton().addActionListener(ivjEventHandler);
	}
	
	private JButton getAddLevelButton() {
		if (addLevelButton == null) {
			try {
				addLevelButton = new JButton(VCellIcons.refLevelNewIcon);
				addLevelButton.setToolTipText("Add a Refinement Level");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}	
		}
		return addLevelButton;
	}

	private JButton getDeleteLevelButton() {
		if (deleteLevelButton == null) {
			try {
				deleteLevelButton = new JButton(VCellIcons.refLevelDeleteIcon);
				deleteLevelButton.setToolTipText("Delete a Refinement Level");
				deleteLevelButton.setEnabled(false);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return deleteLevelButton;
	}
	
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION --------- in SolverTaskDescriptionPanel");
		exception.printStackTrace(System.out);
	}	

	private void addLevelButton_actionPerformed() {	
		solverTaskDescription.getChomboSolverSpec().addRefinementLevel(new RefinementLevel());
		addLevel(solverTaskDescription.getChomboSolverSpec().getNumRefinementLevels()-1);
	}

	private void addLevel(int levelIndex) {
		RefinementLevel rfl = solverTaskDescription.getChomboSolverSpec().getRefinementLevel(levelIndex);
		getDeleteLevelButton().setEnabled(true);
		roiTextField.setEnabled(true);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		levelPanels.add(panel);
		
		JPanel smallPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		smallPanel.add(new JLabel("Level " + (levelIndex+1) + " : refinement ratio "));
		JComboBox comboBox = new JComboBox();
		comboBox.addItem(new Integer(2));
		comboBox.addItem(new Integer(4));
		comboBox.addItem(new Integer(8));
		comboBox.addItem(new Integer(16));
		comboBox.setActionCommand(CMD_REF_RATIO + " " + levelIndex);
		comboBox.setSelectedItem(rfl.getRefineRatio());
		comboBox.addActionListener(ivjEventHandler);
		refinementRatioComboBoxes.add(comboBox);
		smallPanel.add(comboBox);
		
		panel.add(smallPanel, BorderLayout.CENTER);
		
		mainPanel.add(panel);
		
		updateUI();
	}

	private void deleteLevelButton_actionPerformed() {
		int lastIndex = solverTaskDescription.getChomboSolverSpec().getNumRefinementLevels() - 1;
		
		solverTaskDescription.getChomboSolverSpec().deleteRefinementLevel();
		JPanel panel = levelPanels.get(lastIndex);
		levelPanels.remove(panel);
		mainPanel.remove(panel);
		
		JComboBox comboBox = refinementRatioComboBoxes.remove(lastIndex);
		comboBox.removeActionListener(ivjEventHandler);

		if (solverTaskDescription.getChomboSolverSpec().getNumRefinementLevels() == 0) {
			getDeleteLevelButton().setEnabled(false);
			roiTextField.setEnabled(false);
		}
		updateUI();
	}

	private void updateDisplay() {
		if (!solverTaskDescription.getSolverDescription().isChomboSolver()) {
			setVisible(false);
			return;
		}
		setVisible(true);
		maxBoxSizeComboBox.setSelectedItem(solverTaskDescription.getChomboSolverSpec().getMaxBoxSize());
		fillRatioTextField.setText(solverTaskDescription.getChomboSolverSpec().getFillRatio() + "");
		if (solverTaskDescription.getChomboSolverSpec().getRefinementRoiExpression() != null)
		{
			roiTextField.setText(solverTaskDescription.getChomboSolverSpec().getRefinementRoiExpression().infix());
		}
		for (int i = 0; i < levelPanels.size(); i ++) {
			remove(levelPanels.get(i));
		}
		levelPanels.clear();
		refinementRatioComboBoxes.clear();
		
		if (solverTaskDescription.getChomboSolverSpec() != null) {
			int numLevels = solverTaskDescription.getChomboSolverSpec().getNumRefinementLevels();
			if (numLevels >= 1)
			{
				roiTextField.setEnabled(true);
			}
			for (int i = 0; i < numLevels; ++ i) 
			{
				addLevel(i);
			}
		}
	}

	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
		SolverTaskDescription oldValue = this.solverTaskDescription;
		/* Stop listening for events from the current object */
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(ivjEventHandler);
			if (oldValue.getChomboSolverSpec() != null)
			{
				oldValue.getChomboSolverSpec().removePropertyChangeListener(ivjEventHandler);
			}
		}
		this.solverTaskDescription = newValue;
		if (newValue != null)
		{
			newValue.addPropertyChangeListener(ivjEventHandler);
			if (newValue.getChomboSolverSpec() != null)
			{
				newValue.getChomboSolverSpec().addPropertyChangeListener(ivjEventHandler);
			}
		}
		updateDisplay();		
	}
	
	private void setMaxBoxSize() {
		try
		{
			solverTaskDescription.getChomboSolverSpec().setMaxBoxSize((Integer)maxBoxSizeComboBox.getSelectedItem());
		}
		catch (PropertyVetoException ex)
		{
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}
	}
	
	private void setRefRatio(int levelIndex) {
		RefinementLevel rfl = solverTaskDescription.getChomboSolverSpec().getRefinementLevel(levelIndex);
		JComboBox comboBox = refinementRatioComboBoxes.get(levelIndex);
		rfl.setRefineRatio((Integer)comboBox.getSelectedItem());
	}
	
	private void setFillRatio() {
		double fillRatio = Double.parseDouble(fillRatioTextField.getText());
		solverTaskDescription.getChomboSolverSpec().setFillRatio(fillRatio);
	}
	
	private void setRefinementRoiExpression() throws ExpressionException {
		String roi = roiTextField.getText();
		solverTaskDescription.getChomboSolverSpec().setRefinementRoiExpression(roi);
	}
}
