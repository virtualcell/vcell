package cbit.vcell.client.desktop;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.vcell.util.VCAssert;
import org.vcell.util.gui.MomentoButton;
import org.vcell.util.gui.MomentoRadioButton;

import cbit.vcell.solver.SolverDescription;

/**
 * Quick Fix dialog for situations where current parameters don't support selected solver
 * @author GWeatherby
 *
 */
@SuppressWarnings("serial")
public class QuickFixSimulation extends JDialog {

	public enum CloseAction {
		FIX_ALL("Fix All"),
		FIX("Fix"),
		CANCEL("Cancel");
		final String label;

		private CloseAction(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label; 
		}
	}
	
	private final JPanel contentPanel = new JPanel();
	/**
	 * buttons for available solvers
	 */
	private final Collection<MomentoRadioButton<SolverDescription> > radioButtons;
	/**
	 * the one the user selected
	 */
	private SolverDescription selectedSolver;
	/**
	 * user decision
	 */
	private CloseAction closeAction;
	private JButton btnFix;
	private JButton btnAll; 

	/**
	 * Create the dialog.
	 * @param activated parent 
	 * @param useFixAll should "fix all" button appear?
	 * @param message what to say about the situation
	 * @param goodSolvers  options to list / select
	 */
	public QuickFixSimulation(Window activated, boolean useFixAll, String message, Collection<SolverDescription> goodSolvers) {
		super(activated);
		setModalityType(ModalityType.DOCUMENT_MODAL);
		selectedSolver = null;
		closeAction = CloseAction.CANCEL; 
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			ActionListener listener = ae -> dialogButtonEvent(ae);
			{
				if (useFixAll) {
					btnAll = buttonFor(CloseAction.FIX_ALL);
					btnAll.setToolTipText("Fix all simulations with this Error (not implemented yet)");
					btnAll.addActionListener(listener);
					buttonPane.add(btnAll);
				}
			}
			{
				btnFix =  buttonFor(CloseAction.FIX);
				btnFix.setToolTipText("Fix just this sim");
				btnFix.addActionListener(listener);
				buttonPane.add(btnFix);
			}
			{
				JButton cancelButton = buttonFor(CloseAction.CANCEL);
				cancelButton.addActionListener(listener);
				buttonPane.add(cancelButton);
				getRootPane().setDefaultButton(cancelButton);
			}
		}
		{
			JLabel lblNewLabel = new JLabel(message);
			contentPanel.add(lblNewLabel);
		}
		{
			switch (goodSolvers.size()) {
			case 1:
				selectedSolver = goodSolvers.iterator().next();
				JLabel onlySolver = new JLabel(selectedSolver.getShortDisplayLabel());
				contentPanel.add(onlySolver);
				radioButtons = null;
				break;
			default:
				radioButtons = new ArrayList<>(); 
				ButtonGroup theGroup = new ButtonGroup( );
				ActionListener listener = ae -> solverButtonEvent(ae);
				for (SolverDescription sd : goodSolvers) {
					SolverRadio sr = new SolverRadio(sd);
					radioButtons.add(sr);
					theGroup.add(sr);
					contentPanel.add(sr);
					sr.addActionListener(listener);
				}
				enableFixButtons(false);
			}
		}
		pack( );
	}
	
	/**
	 * @return option user choose, or {@link CloseAction#CANCEL} if they just close dialog
	 */
	public CloseAction getCloseAction() {
		return closeAction;
	}

	/**
	 * @return selected solver 
	 */
	public SolverDescription getSelectedSolver() {
		return selectedSolver;
	}
	/**
	 * create button for specfic action
	 * @param ca not null
	 */
	private static JButton buttonFor(CloseAction ca) {
		return new CloseActionButton(ca); 
	}
	
	/**
	 * add Strong type safety to generic
	 */
	private static class CloseActionButton extends MomentoButton<CloseAction> {
		public CloseActionButton(CloseAction data) {
			super(data,data.label); 
		}
	}
	/**
	 * record action, close dialog
	 * @param e
	 */
	private void dialogButtonEvent(ActionEvent e) {
		Object src = e.getSource();
		VCAssert.ofType(src,CloseActionButton.class);
		CloseActionButton btn = (CloseActionButton) src;
		closeAction = btn.getUserData();
		setVisible(false);
	}
	
	
	/**
	 * add Strong type safety to generic
	 */
	private static class SolverRadio extends MomentoRadioButton<SolverDescription> {
		public SolverRadio(SolverDescription data) {
			super(data, data.getDisplayLabel());
		}
	}
	
	/**
	 * null safe enable of "fix" button
	 * @param state
	 */
	private void enableFixButtons(boolean state) {
		if (btnAll != null) {
			btnAll.setEnabled(state);
		}
		btnFix.setEnabled(state);
	}
	
	/**
	 * {@link SolverRadio} event handler 
	 * @param e
	 */
	private void solverButtonEvent(ActionEvent e)  {
		Object src = e.getSource();
		VCAssert.ofType(src,SolverRadio.class);
		SolverRadio sr = (SolverRadio) src;
		selectedSolver = sr.getUserData(); 
		enableFixButtons(true);
	}
}
