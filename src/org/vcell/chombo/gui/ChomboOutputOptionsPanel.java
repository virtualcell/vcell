package org.vcell.chombo.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vcell.chombo.ChomboSolverSpec;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.solver.SolverTaskDescription;

@SuppressWarnings("serial")
public class ChomboOutputOptionsPanel extends JPanel {
	private static final boolean ENABLE_PARALLEL = true;

	private class IvjEventHandler implements ActionListener, PropertyChangeListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if (e.getSource() == chomboOutputCheckBox || e.getSource() == vcellOutputCheckBox) 
			{
				setChomboOutputOptions();
			} 
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (!suppressEvents && evt.getSource() == solverTaskDescription)
			{
				setDisplayForSolverTaskDescription();
			}
		}
	}

	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SolverTaskDescription solverTaskDescription;	
	private ChomboSolverSpec chomboSolverSpec;

	private JCheckBox vcellOutputCheckBox;
	private JCheckBox chomboOutputCheckBox;
	private JFormattedTextField numProcessors = new JFormattedTextField(new DecimalFormat("##"));
	/**
	 * the user's last selection for {@link #chomboOutputCheckBox} when {@link #numProcessors} == 1
	 */
	private boolean lastUserChomboOutput;
	/**
	 * used along with {@link EventSuppressor} to prevent programmatic setting of values executing propertyChange event methods
	 */
	private boolean suppressEvents = false;

	private class FileOptions extends JPanel {
		FileOptions( ) {
			setLayout(new GridBagLayout());
			setBorder(BorderFactory.createTitledBorder(GuiConstants.TAB_PANEL_BORDER, "Save Output Files"));

			vcellOutputCheckBox = new JCheckBox("For VCell Native Viewer");
			chomboOutputCheckBox = new JCheckBox("For VisIt Viewer");
			int gridy = 0;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(4, 4, 4, 4);		
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.anchor = GridBagConstraints.WEST;
			add(vcellOutputCheckBox, gbc);

			++ gridy;
			gbc = new GridBagConstraints();
			gbc.insets = new java.awt.Insets(4, 4, 4, 4);
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.weightx = 1.0;
			gbc.anchor = GridBagConstraints.WEST;
			add(chomboOutputCheckBox, gbc);

			vcellOutputCheckBox.addActionListener(ivjEventHandler);
			chomboOutputCheckBox.addActionListener(ivjEventHandler);
		}
	}

	/**
	 * panel  with {@link ChomboOutputOptionsPanel#numProcessors} in it
	 */
	private  class ProcessorOptions extends JPanel implements PropertyChangeListener {
		public ProcessorOptions() {
			super(new FlowLayout(FlowLayout.LEFT));
			add(new JLabel("Num Processors:"));
			add(numProcessors);
			numProcessors.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
			numProcessors.setColumns(2);
			numProcessors.addPropertyChangeListener("value",this);
		}
		
		/**
		 * update {@link ChomboOutputOptionsPanel#solverTaskDescription} when user input 
		 * number processors change
		 */
		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			if (!suppressEvents) {
				try(EventSuppressor es = new EventSuppressor()) {
					Object o = numProcessors.getValue();
					if (o != null && o instanceof Number) {
						Number nobj = (Number) o;
						final int n = nobj.intValue();
						solverTaskDescription.setNumProcessors(n);
						setNumProcessorsField(n > 1);
					}
					else {
						//don't expect this to happen, but just fallback to last set int if it does
						numProcessors.setValue(Integer.toString(solverTaskDescription.getNumProcessors()));
					}
				}
			}
		}
	}

	/**
	 *  used in try-with-resources block to set {@link ChomboOutputOptionsPanel#suppressEvents} to true.<br>
	 *  Reentrant safe; only last call to {@link AutoCloseable#close()} resets suppressEvents to false
	 */
	private class EventSuppressor implements AutoCloseable {
		final boolean resetOnClose;

		/**
		 * determine if events already suppressed; only resetOnClose if they're not
		 */
		public EventSuppressor() {
			if (suppressEvents) {
				resetOnClose = false;
			}
			else {
				suppressEvents = true;
				resetOnClose = true;
			}
		}

		@Override
		public void close() {
			if (resetOnClose) {
				suppressEvents = false;
			}
		}
	}

	/**
	 * force chombo output if parallel 
	 */
	private void enableGuiElements( ) {
		final boolean isParallel = solverTaskDescription.isParallel();
		chomboOutputCheckBox.setEnabled(!isParallel);
		boolean cState = lastUserChomboOutput;
		if (isParallel) {
			cState = true;
		}
		chomboOutputCheckBox.setSelected(cState);
	}

	public ChomboOutputOptionsPanel() {
		super(new BorderLayout());
		add(new FileOptions(),BorderLayout.CENTER);
		add(new ProcessorOptions(),BorderLayout.EAST);
	}

	private void setChomboOutputOptions() {
		chomboSolverSpec.setSaveVCellOutput(vcellOutputCheckBox.isSelected());
		chomboSolverSpec.setSaveChomboOutput(chomboOutputCheckBox.isSelected());
	}

	/**
	 * if {@link #ENABLE_PARALLEL} is true, set num processors field from current solverTaskDescription 
	 * if {@link #ENABLE_PARALLEL} is false, set solverTaskDescription processors to 1 
	 * @param recordChomboSelectedValue if true, record current state of  {@link #chomboOutputCheckBox} for resetting later
	 */
	private void setNumProcessorsField(boolean recordChomboSelectedValue ) {
		if (!ENABLE_PARALLEL) {
			solverTaskDescription.setNumProcessors(1);
		}
		numProcessors.setValue(new Long(solverTaskDescription.getNumProcessors()));
		if (recordChomboSelectedValue) {
			lastUserChomboOutput = chomboOutputCheckBox.isSelected();
		}
		enableGuiElements();
	}

	/**
	 * setup display for particular {@link #solverTaskDescription}
	 */
	private void setDisplayForSolverTaskDescription() {
		try (EventSuppressor es = new EventSuppressor()) {
			if (!solverTaskDescription.getSolverDescription().isChomboSolver()) {
				chomboSolverSpec = null;
				setVisible(false);
				return;
			}
			chomboSolverSpec = solverTaskDescription.getChomboSolverSpec();
			if (chomboSolverSpec != null)
			{
				setVisible(true);
				vcellOutputCheckBox.setSelected(chomboSolverSpec.isSaveVCellOutput());
	
				final boolean saveChomboFormatFiles = chomboSolverSpec.isSaveChomboOutput();
				lastUserChomboOutput = saveChomboFormatFiles; 
				chomboOutputCheckBox.setSelected(saveChomboFormatFiles);
				setNumProcessorsField(false);
			}
		} 
	}

	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
		SolverTaskDescription oldValue = this.solverTaskDescription;
		if (oldValue == newValue)
		{
			return;
		}
		if (oldValue != null)
		{
			oldValue.removePropertyChangeListener(ivjEventHandler);
		}
		this.solverTaskDescription = newValue;

		setDisplayForSolverTaskDescription();		

		if (solverTaskDescription != null)
		{
			solverTaskDescription.addPropertyChangeListener(ivjEventHandler);
		}
	}

}
