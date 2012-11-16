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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.GuiConstants;
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
//				if (cmd.startsWith("AddBox")) {
//					StringTokenizer st = new StringTokenizer(cmd);
//					st.nextToken();
//					int index = Integer.parseInt(st.nextToken());
//					addBox(index);
//				} else if (e.getActionCommand().startsWith("DeleteBox")) {
//					StringTokenizer st = new StringTokenizer(cmd);
//					st.nextToken();
//					deleteBox(Integer.parseInt(st.nextToken()), (JButton)e.getSource());
//				}
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
		};
	};
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton addLevelButton;
	private JButton deleteLevelButton;
	private ArrayList<JPanel> levelPanels = new ArrayList<JPanel>();
//	private ArrayList<JButton> addBoxButtons = new ArrayList<JButton>();
//	private ArrayList< ArrayList<JButton> > deleteBoxButtons = new ArrayList< ArrayList<JButton> >();
//	private ArrayList< ArrayList<JTextField> > boxTextFields = new ArrayList< ArrayList<JTextField> >();
	private List<JComboBox> refinementRatioComboBoxes = new ArrayList<JComboBox>();
	private SolverTaskDescription solverTaskDescription = null;
	private JPanel mainPanel = null;
	private JComboBox maxBoxSizeComboBox = null;
	private JTextField fillRatioTextField;
	public ChomboSolverSpecPanel() {
		super("Chombo Options");
		initialize();
	}

	private void initialize() 
	{
		setMaximumSize(new Dimension(500, 150));
		setPreferredSize(new Dimension(500, 150));
		maxBoxSizeComboBox = new JComboBox();
		maxBoxSizeComboBox.addItem(new Integer(8));
		maxBoxSizeComboBox.addItem(new Integer(16));
		maxBoxSizeComboBox.addItem(new Integer(32));
		maxBoxSizeComboBox.addItem(new Integer(64));
		maxBoxSizeComboBox.addItem(new Integer(128));
		
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
		JLabel label = new JLabel("Refinement Levels");
		toolbar.add(label);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(getAddLevelButton());
		toolbar.add(getDeleteLevelButton());
		
		mainPanel  = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel(new BorderLayout());
		p.add(toolbar, BorderLayout.NORTH);
		JScrollPane scroll = new JScrollPane(mainPanel);
		scroll.setBorder(null);
		p.add(scroll, BorderLayout.CENTER);
		p.setBorder(GuiConstants.TAB_PANEL_BORDER);
		
//		mainPanel.add(toolbar);
		
		getContentPanel().setLayout(new BorderLayout(0, 2));
		getContentPanel().add(northPanel, BorderLayout.NORTH);
		getContentPanel().add(p, BorderLayout.CENTER);
		
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
		
//		deleteBoxButtons.add(new ArrayList<JButton>());
//		boxTextFields.add(new ArrayList<JTextField>());
		
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
		
//		smallPanel.add(Box.createHorizontalGlue());
//		JButton button = new JButton("Add a Box");
//		button.setEnabled(false);
//		button.setActionCommand("AddBox " + (levelPanels.size() - 1));
//		addBoxButtons.add(button);
//		button.addActionListener(ivjEventHandler);
//		smallPanel.add(button);
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
		
//		for (int j = 0; j < deleteBoxButtons.get(lastIndex).size(); j ++) {
//			deleteBoxButtons.get(lastIndex).get(j).removeActionListener(ivjEventHandler);
//			boxTextFields.get(lastIndex).get(j).removeFocusListener(ivjEventHandler);
//		}
//		deleteBoxButtons.get(lastIndex).clear();
//		boxTextFields.get(lastIndex).clear();
		JComboBox comboBox = refinementRatioComboBoxes.remove(lastIndex);
		comboBox.removeActionListener(ivjEventHandler);
//		
//		addBoxButtons.remove(lastIndex).removeActionListener(ivjEventHandler);
		if (solverTaskDescription.getChomboSolverSpec().getNumRefinementLevels() == 0) {
			getDeleteLevelButton().setEnabled(false);
		}
		updateUI();
	}

//	private void addBox(int levelIndex) {	
//		RefinementLevel rfl = chomboSolverSpec.getRefinementLevel(levelIndex);
//		ISize lo = new ISize(0,0,0);
//		ISize hi = new ISize(0,0,0);
//		rfl.addBox(new ChomboBox(lo, hi));	
//		
//		addBox(levelIndex, null);
//	}

//	private void addBox(int levelIndex, ChomboBox box) {
//		JPanel panel = levelPanels.get(levelIndex);
//		JPanel smallPanel = new JPanel();
//		ArrayList<JTextField> textFields = boxTextFields.get(levelIndex);
//		ArrayList<JButton> buttons = deleteBoxButtons.get(levelIndex);
//		
//		JTextField textField = new JTextField();	
//		textFields.add(textField);	
//		textField.setColumns(15);
//		if (box != null) {
//			textField.setText(box.toString());
//		}
//		textField.addFocusListener(ivjEventHandler);
//		smallPanel.add(new JLabel("Box " + textFields.size()));
//		smallPanel.add(textField);
//		
//		JButton button = new JButton("Delete");
//		button.setActionCommand("DeleteBox " + levelIndex);
//		button.addActionListener(ivjEventHandler);
//		smallPanel.add(button);
//		buttons.add(button);
//		panel.add(smallPanel);
//		
//		updateUI();
//	}

//	private void deleteBox(int levelIndex, JButton delButton) {
//		JPanel panel = levelPanels.get(levelIndex);
//		ArrayList<JTextField> textFields = boxTextFields.get(levelIndex);
//		ArrayList<JButton> buttons = deleteBoxButtons.get(levelIndex);
//
//		int boxIndex = 0;
//		for (JButton button : buttons) {
//			if (button == delButton) {
//				break;
//			}
//			boxIndex ++;
//		}
//		
//		JTextField textField = textFields.get(boxIndex);
//		panel.remove(textField.getParent());
//		
//		RefinementLevel rfl = chomboSolverSpec.getRefinementLevel(levelIndex);
//		rfl.deleteBox(boxIndex);	
//		
//		textFields.remove(boxIndex).removeFocusListener(ivjEventHandler);
//		buttons.remove(boxIndex).removeActionListener(ivjEventHandler);
//		updateUI();
//	}

//	private void boxTextField_focusLost(JTextField source) {
//		int levelIndex = 0;	
//
//		JTextField theTextField = null;
//		for (JTextField textField : refinementRatioTextFields) {
//			if (textField == source){
//				RefinementLevel rfl = chomboSolverSpec.getRefinementLevel(levelIndex);
//				rfl.setRefineRatio(Integer.parseInt(source.getText()));
//				return;
//			}
//			levelIndex ++;
//		}
//		
//		
//		levelIndex = 0;
//		int boxIndex = 0;
//		for (ArrayList<JTextField> textFields : boxTextFields) {
//			for (JTextField textField : textFields) {
//				if (textField == source) {
//					theTextField = textField;
//					break;
//				}
//				boxIndex ++;
//			}
//			if (theTextField != null) {
//				break;
//			}
//			levelIndex ++;
//			boxIndex = 0;
//		}
//		RefinementLevel rfl = chomboSolverSpec.getRefinementLevel(levelIndex);
//
//		String text = theTextField.getText();
//		if (text == null || text.trim().length() == 0) {
//			return;
//		}
//		StringTokenizer st = new StringTokenizer(text);
//		if (st.countTokens() != solverTaskDescription.getSimulation().getMathDescription().getGeometry().getDimension()*2) {
//			DialogUtils.showErrorDialog(source, "Wrong number of values, must be 2 * dimension");
//		}
//		try {
//			rfl.set(boxIndex, ChomboBox.fromString(text));
//		} catch (Exception ex) {
//			DialogUtils.showErrorDialog(source, "Wrong format");
//			source.requestFocus();
//		}
//	}

	private void updateDisplay() {
		if (!solverTaskDescription.getSolverDescription().isChomboSolver()) {
			setVisible(false);
			return;
		}
		setVisible(true);
		maxBoxSizeComboBox.setSelectedItem(solverTaskDescription.getChomboSolverSpec().getMaxBoxSize());
		fillRatioTextField.setText(solverTaskDescription.getChomboSolverSpec().getFillRatio() + "");
		for (int i = 0; i < levelPanels.size(); i ++) {
			remove(levelPanels.get(i));
//			addBoxButtons.get(i).removeActionListener(ivjEventHandler);
//			for (int j = 0; j < deleteBoxButtons.get(i).size(); j ++) {
//				deleteBoxButtons.get(i).get(j).removeActionListener(ivjEventHandler);
//				boxTextFields.get(i).get(j).removeFocusListener(ivjEventHandler);
//			}
//			deleteBoxButtons.get(i).clear();
//			boxTextFields.get(i).clear();
		}
		levelPanels.clear();
//		addBoxButtons.clear();
//		deleteBoxButtons.clear();
//		boxTextFields.clear();
		refinementRatioComboBoxes.clear();
		
		if (solverTaskDescription.getChomboSolverSpec() != null) {
			int numLevels = solverTaskDescription.getChomboSolverSpec().getNumRefinementLevels();
			for (int i = 0; i < numLevels; ++ i) 
			{
				addLevel(i);
//				RefinementLevel rfl = chomboSolverSpec.getRefinementLevel(i);
//				int numBoxes = rfl.getNumBoxes();
//				for (int j = 0; j < numBoxes; j ++) {
//					addBox(i, rfl.getBox(j));
//				}
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
}
