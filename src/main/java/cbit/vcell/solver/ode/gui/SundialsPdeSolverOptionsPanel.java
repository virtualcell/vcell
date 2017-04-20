/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.SundialsPdeSolverOptions;

@SuppressWarnings("serial")
public class SundialsPdeSolverOptionsPanel extends CollapsiblePanel {
	
	private SolverTaskDescription solverTaskDescription = null;	
	private JComboBox ivjJComboBoxMaxOrder = null;	
	private JButton maxOrderHelpButton = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	private class IvjEventHandler implements java.awt.event.ItemListener, java.beans.PropertyChangeListener, ActionListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == solverTaskDescription && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION))) {
				refresh();
			}
			if (evt.getSource() == solverTaskDescription && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SUNDIALS_SOLVER_OPTIONS))) {
				refresh();
			}
		}

		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				setNewOptions();
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == maxOrderHelpButton) {
				DialogUtils.showInfoDialog(SundialsPdeSolverOptionsPanel.this, "Max order for BDF method", "<html>cvode includes an algorithm, " +
						"stald (STAbility Limit Detection), which provides protection against " +
						"potentially unstable behavior of the BDF multistep integration methods is certain situations, as " +
						"described below. " +
						"<p>When the BDF option is selected, cvode uses Backward Differentiation Formula methods of orders" +
						"1 to 5. At order 1 or 2, the BDF method is A-stable, meaning that for any complex constant \u03BB in the " +
						"open left half-plane, the method is unconditionally stable (for any step size) for the standard scalar " +
						"model problem y' = \u03BBy. For an ODE system, this means that, roughly speaking, as long as all modes " +
						"in the system are stable, the method is also stable for any choice of step size, at least in the sense of" +
						"a local linear stability analysis. " +
						"<p>At orders 3 to 5, the BDF methods are not A-stable, although they are <i>stiffly</i> stable. In each" +
						"case, in order for the method to be stable at step size <i>h</i> on the scalar model problem, the product \u03BBh? " +
						"must lie in <i>a region of absolute stability</i>. That region excludes a portion of the left half-plane that is " +
						"concentrated near the imaginary axis. The size of that region of instability grows as the order increases " +
						"from 3 to 5. What this means is that, when running BDF at any of these orders, if an eigenvalue \u03BB of " +
						"the system lies close enough to the imaginary axis, the step sizes <i>h</i> for which the method is stable are " +
						"limited (at least according to the linear stability theory) to a set that prevents h\u03BB from leaving the " +
						"stability region. The meaning of <i>close enough</i> depends on the order. At order 3, the unstable region " +
						"is much narrower than at order 5, so the potential for unstable behavior grows with order." +
						"<p>System eigenvalues that are likely to run into this instability are ones that correspond to weakly" +
						"damped oscillations. A pure undamped oscillation corresponds to an eigenvalue on the imaginary axis." +
						"Problems with modes of that kind call for different considerations, since the oscillation generally must" +
						"be followed by the solver, and this requires step sizes (h ~ 1/\u03BD, where \u03BD is the frequency) that are" +
						"stable for BDF anyway. But for a weakly damped oscillatory mode, the oscillation in the solution is" +
						"eventually damped to the noise level, and at that time it is important that the solver not be restricted" +
						"to step sizes on the order of 1/\u03BD. It is in this situation that the new option may be of great value." +
						"<p>In terms of partial differential equations, the typical problems for which the stability limit detection" +
						" option is appropriate are ODE systems resulting from semi-discretized PDEs " +
						"(i.e., PDEs discretized in space) with advection and di?usion, but with advection dominating over diffusion. " +
						"Diffusion alone produces pure decay modes, while advection tends to produce undamped oscillatory modes. A mix of" +
						"the two with advection dominant will have weakly damped oscillatory modes.</html>");	
			}
			
		}
	}
	
	public SundialsPdeSolverOptionsPanel() {
		super("Advanced Solver Options", false);
		initialize();		
	}
	
	private void initialize() {
		try {
			FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
			layout.setVgap(0);
			getContentPanel().setLayout(layout);
			JLabel label = new JLabel("Max Order for BDF method (advection only) ");
			getContentPanel().add(label);
			getContentPanel().add(getMaxOrderButton());
			getJComboBoxMaxOrder().setPreferredSize(new Dimension((int)label.getPreferredSize().getWidth(), (int)getJComboBoxMaxOrder().getPreferredSize().getHeight()));
			getContentPanel().add(getJComboBoxMaxOrder());			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);		
		}
	}
	
	/**
	 * Return the JTextField2 property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JComboBox getJComboBoxMaxOrder() {
		if (ivjJComboBoxMaxOrder == null) {
			try {
				ivjJComboBoxMaxOrder = new javax.swing.JComboBox();
				ivjJComboBoxMaxOrder.addItem(1);
				ivjJComboBoxMaxOrder.addItem(2);
				ivjJComboBoxMaxOrder.addItem(3);
				ivjJComboBoxMaxOrder.addItem(4);
				ivjJComboBoxMaxOrder.addItem(5);
				ivjJComboBoxMaxOrder.addItemListener(ivjEventHandler);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJComboBoxMaxOrder;
	}

	private javax.swing.JButton getMaxOrderButton() {
		if (maxOrderHelpButton == null) {
			try {
				maxOrderHelpButton = new JButton(" ? ");
				Font font = maxOrderHelpButton.getFont().deriveFont(Font.BOLD);
				Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
				maxOrderHelpButton.setFont(font);
				maxOrderHelpButton.setBorder(border);
				maxOrderHelpButton.addActionListener(ivjEventHandler);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return maxOrderHelpButton;
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
	
	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
		SolverTaskDescription oldValue = solverTaskDescription;
		/* Stop listening for events from the current object */
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(ivjEventHandler);
		}
		solverTaskDescription = newValue;

		/* Listen for events from the new object */
		if (newValue != null) {
			newValue.addPropertyChangeListener(ivjEventHandler);
		}		
		solverTaskDescription = newValue;
		refresh();
	}
	
	/**
	 * Update parameters for stochastic simulations,
	 * including using customized seed or not, customized seed, using tractory or histogram, number of trials (for all, and below four paras for hybrid only)
	 * Epsilon : minimum number of molecus required for approximation as a continuous Markow process,
	 * Lambda : minimum rate of reaction required for approximation to a continuous Markov process,
	 * MSR Tolerance : Maximum allowed effect of slow reactions per numerical integration of the SDEs,
	 * SDE Tolerance : Maximum allowed value of the drift and diffusion errors
	 */
	private void setNewOptions(){
		if(!isVisible()){
			return;
		}
		try{
			int order = (Integer) ivjJComboBoxMaxOrder.getSelectedItem();
			solverTaskDescription.setSundialsPdeSolverOptions(new SundialsPdeSolverOptions(order));			
		} catch(Exception e){
			PopupGenerator.showErrorDialog(this, e.getMessage(), e);
		}
	}
	
	private void refresh() {
		
		if (solverTaskDescription != null) {
			if (!solverTaskDescription.getSolverDescription().equals(SolverDescription.SundialsPDE) || !solverTaskDescription.getSimulation().getMathDescription().hasVelocity()) {
				setVisible(false);
				return;
			}
		}
			
		setVisible(true);
		SundialsPdeSolverOptions sso = solverTaskDescription.getSundialsPdeSolverOptions();	
		ivjJComboBoxMaxOrder.setSelectedItem(sso.getMaxOrderAdvection());
	}
	
	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			javax.swing.JFrame frame = new javax.swing.JFrame();
			SundialsPdeSolverOptionsPanel aSundialsPdeSolverOptionsPanel;
			aSundialsPdeSolverOptionsPanel = new SundialsPdeSolverOptionsPanel();
			frame.setContentPane(aSundialsPdeSolverOptionsPanel);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			frame.pack();
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
}
