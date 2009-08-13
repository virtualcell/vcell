package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;


public class EstParams_ReacBindingInputPanel extends JPanel{
	private JTextArea textArea;
	FRAPReacDiffEstimationGuidePanel reacBindingPanel = null;
	JRadioButton koffButton = new JRadioButton("Off Rate (K_off)");
	JRadioButton bsButton = new JRadioButton("[BS] (C_BS)");
	JTextField koffTextField = new JTextField(8);
	JTextField bsTextField = new JTextField(8);
	JTextField fRadiusTextField = new JTextField(8);
	JTextField bsRadiusTextField = new JTextField(8);
	public EstParams_ReacBindingInputPanel() {
		super();
		setLayout(new BorderLayout());

		JPanel temPanel = new JPanel();
		final JLabel inputParamLabel = new JLabel();
		inputParamLabel.setForeground(new Color(0, 0, 128));
		inputParamLabel.setFont(new Font("", Font.BOLD, 16));
		inputParamLabel.setText("Estimate Parameters for Binding Reaction");
		temPanel.add(inputParamLabel);
		
		add(temPanel, BorderLayout.NORTH);
		add(getReacBindingPanel(), BorderLayout.CENTER);
	}
	
	private FRAPReacDiffEstimationGuidePanel getReacBindingPanel()
	{
		if(reacBindingPanel == null)
		{
			reacBindingPanel = new FRAPReacDiffEstimationGuidePanel();
//			//the mid panel
//			final GridBagLayout gridBagLayout_1 = new GridBagLayout();
//			gridBagLayout_1.rowHeights = new int[] {7,7};
//			gridBagLayout_1.columnWidths = new int[] {0,7,0,0,7,0,7,7,7};
//			reacBindingPanel.setLayout(gridBagLayout_1);
//			TitledBorder tb=new TitledBorder(new LineBorder(Color.gray, 1, false),"Input requried parameters to estimate", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));
//			reacBindingPanel.setBorder(tb);
//
//			final JLabel koffUnitLabel = new JLabel();
//			koffUnitLabel.setText("s-1");
//			final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
//			gridBagConstraints_5.gridwidth = 3;
//			gridBagConstraints_5.gridy = 1;
//			gridBagConstraints_5.gridx = 4;
//			reacBindingPanel.add(koffUnitLabel, gridBagConstraints_5);
//			JLabel fParticleRadiusLabel = new JLabel("F  particle radius (R_f)");
//			
//			final GridBagConstraints gridBagConstraints_koffRadio = new GridBagConstraints();
//			gridBagConstraints_koffRadio.anchor = GridBagConstraints.WEST;
//			gridBagConstraints_koffRadio.fill = GridBagConstraints.HORIZONTAL;
//			gridBagConstraints_koffRadio.insets = new Insets(2, 2, 2, 2);
//			gridBagConstraints_koffRadio.gridy = 1;
//			gridBagConstraints_koffRadio.gridx = 0;
//
//			final JLabel bindingReactionLabel = new JLabel();
//			bindingReactionLabel.setText("Binding Reaction: F + BS = C");
//			bindingReactionLabel.setBorder(new LineBorder(new Color(120,120,188), 1));
//			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
//			gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
//			gridBagConstraints_1.gridwidth = 13;
//			gridBagConstraints_1.gridx = 0;
//			gridBagConstraints_1.gridy = 0;
//			reacBindingPanel.add(bindingReactionLabel, gridBagConstraints_1);
//			reacBindingPanel.add(koffButton, gridBagConstraints_koffRadio);
//			koffButton.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e) {
//					koffTextField.setEnabled(true);
//					bsTextField.setEnabled(false);
//				}
//			});
//			
//			final GridBagConstraints gridBagConstraints_koffTf = new GridBagConstraints();
//			gridBagConstraints_koffTf.anchor = GridBagConstraints.WEST;
//			gridBagConstraints_koffTf.insets = new Insets(2, 2, 2, 0);
//			gridBagConstraints_koffTf.gridy = 1;
//			gridBagConstraints_koffTf.gridx = 1;
//			gridBagConstraints_koffTf.gridwidth = 2;
//			reacBindingPanel.add(koffTextField, gridBagConstraints_koffTf);
//			
//			final GridBagConstraints gridBagConstraints_konLabel = new GridBagConstraints();
//			gridBagConstraints_konLabel.anchor = GridBagConstraints.WEST;
//			gridBagConstraints_konLabel.fill = GridBagConstraints.HORIZONTAL;
//			gridBagConstraints_konLabel.insets = new Insets(2, 2, 2, 2);
//			gridBagConstraints_konLabel.gridy = 1;
//			gridBagConstraints_konLabel.gridx = 9;
//			reacBindingPanel.add(fParticleRadiusLabel, gridBagConstraints_konLabel);
//			
//			final GridBagConstraints gridBagConstraints_bsRadio = new GridBagConstraints();
//			gridBagConstraints_bsRadio.anchor = GridBagConstraints.WEST;
//			gridBagConstraints_bsRadio.fill = GridBagConstraints.HORIZONTAL;
//			gridBagConstraints_bsRadio.insets = new Insets(2, 2, 2, 2);
//			gridBagConstraints_bsRadio.gridy = 2;
//			gridBagConstraints_bsRadio.gridx = 0;
//
//			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
//			gridBagConstraints_3.insets = new Insets(2, 2, 2, 2);
//			gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
//			gridBagConstraints_3.anchor = GridBagConstraints.WEST;
//			gridBagConstraints_3.gridy = 1;
//			gridBagConstraints_3.gridx = 11;
//			reacBindingPanel.add(fRadiusTextField, gridBagConstraints_3);
//
//			final JLabel freeRadiumUnitLabel = new JLabel();
//			freeRadiumUnitLabel.setText("um");
//			final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
//			gridBagConstraints_7.gridy = 1;
//			gridBagConstraints_7.gridx = 12;
//			reacBindingPanel.add(freeRadiumUnitLabel, gridBagConstraints_7);
//			reacBindingPanel.add(bsButton, gridBagConstraints_bsRadio);
//			bsButton.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e) {
//					koffTextField.setEnabled(false);
//					bsTextField.setEnabled(true);
//				}
//
//			});
//			
//			final GridBagConstraints gridBagConstraints_bsTf = new GridBagConstraints();
//			gridBagConstraints_bsTf.anchor = GridBagConstraints.WEST;
//			gridBagConstraints_bsTf.insets = new Insets(2, 2, 2, 2);
//			gridBagConstraints_bsTf.gridy = 2;
//			gridBagConstraints_bsTf.gridx = 1;
//			reacBindingPanel.add(bsTextField, gridBagConstraints_bsTf);
//			
//			
//			ButtonGroup bg2 = new ButtonGroup();
//			bg2.add(koffButton);
//			bg2.add(bsButton);
//			bsButton.setSelected(true);
//			bsTextField.setEnabled(true);
//			koffTextField.setEnabled(false);
//			
//			final JLabel BSUnitLabel = new JLabel();
//			BSUnitLabel.setText(" ");
//			final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
//			gridBagConstraints_6.gridy = 2;
//			gridBagConstraints_6.gridx = 5;
//			reacBindingPanel.add(BSUnitLabel, gridBagConstraints_6);
//
//			final JLabel bsParticleRadiusLabel = new JLabel();
//			bsParticleRadiusLabel.setText("BS particle radius(R_BS)");
//			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
//			gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
//			gridBagConstraints_2.anchor = GridBagConstraints.WEST;
//			gridBagConstraints_2.gridy = 2;
//			gridBagConstraints_2.gridx = 9;
//			reacBindingPanel.add(bsParticleRadiusLabel, gridBagConstraints_2);
//
//			final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
//			gridBagConstraints_4.insets = new Insets(2, 2, 2, 2);
//			gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
//			gridBagConstraints_4.anchor = GridBagConstraints.WEST;
//			gridBagConstraints_4.gridy = 2;
//			gridBagConstraints_4.gridx = 11;
//			reacBindingPanel.add(bsRadiusTextField, gridBagConstraints_4);
//
//			final JLabel bsRadiusUnitLabel = new JLabel();
//			bsRadiusUnitLabel.setText("um");
//			final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
//			gridBagConstraints_8.gridy = 2;
//			gridBagConstraints_8.gridx = 12;
//			reacBindingPanel.add(bsRadiusUnitLabel, gridBagConstraints_8);
//
//			final JLabel inputbsAsLabel = new JLabel();
//			inputbsAsLabel.setText("(Input [BS] as a ratio of total Fluor.)");
//			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
//			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
//			gridBagConstraints.gridwidth = 6;
//			gridBagConstraints.gridy = 3;
//			gridBagConstraints.gridx = 0;
//			reacBindingPanel.add(inputbsAsLabel, gridBagConstraints);
//
//			textArea = new JTextArea();
//			reacBindingPanel.add(textArea, new GridBagConstraints());
		}
		return reacBindingPanel;
	}
}
