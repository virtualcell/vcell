package cbit.vcell.microscopy.gui.choosemodelwizard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import cbit.vcell.microscopy.FRAPModel;

@SuppressWarnings("serial")
public class ChooseModel_ModelTypesPanel extends JPanel
{
	private JCheckBox diffOneCheckBox = null;
	private JCheckBox diffTwoCheckBox = null;
	private JCheckBox koffCheckBox = null;
//	private JCheckBox diffBindingCheckBox = null;
	
	public ChooseModel_ModelTypesPanel() {
		super();
		setLayout(new GridBagLayout());

		final JLabel choosePossibleModelLabel = new JLabel();
		choosePossibleModelLabel.setFont(new Font("", Font.BOLD | Font.ITALIC, 16));
		choosePossibleModelLabel.setForeground(new Color(0, 0, 128));
		choosePossibleModelLabel.setText("Choose Possible Model Types");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(choosePossibleModelLabel, gridBagConstraints);

		final JLabel chooseOneOrLabel = new JLabel();
		chooseOneOrLabel.setText("Choose one or more possible model types that may fit your data. A comparison of the ");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.gridy = 4;
		gridBagConstraints_1.gridx = 0;
		add(chooseOneOrLabel, gridBagConstraints_1);

		final JLabel selectedModelsWillLabel = new JLabel();
		selectedModelsWillLabel.setText("selected models will be given after  'Parameter Estimation'.  The possible models are");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 5;
		gridBagConstraints_2.gridx = 0;
		add(selectedModelsWillLabel, gridBagConstraints_2);

		final JLabel diffusionWithOneLabel = new JLabel();
		diffusionWithOneLabel.setText("listed below :");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 6;
		gridBagConstraints_3.gridx = 0;
		add(diffusionWithOneLabel, gridBagConstraints_3);

		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(450, 3));
		separator.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.RAISED),new EtchedBorder(EtchedBorder.LOWERED)));
		GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 8;
		gridBagConstraints_8.gridx = 0;
		add(separator, gridBagConstraints_8);
		
		JLabel diffustionOnlyLabel = new JLabel("Diffusion only models :");
		diffustionOnlyLabel.setFont(new Font("", Font.BOLD, 12));
		diffustionOnlyLabel.setForeground(new Color(0, 0, 128));
		GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.gridy = 13;
		gridBagConstraints_7.gridx = 0;
		add(diffustionOnlyLabel, gridBagConstraints_7);
		
		diffOneCheckBox = new JCheckBox();
		diffOneCheckBox.setSelected(true);
		diffOneCheckBox.setText("Diffusion with One Diffusing Component");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.gridy = 14;
		gridBagConstraints_5.gridx = 0;
		add(diffOneCheckBox, gridBagConstraints_5);

		diffTwoCheckBox = new JCheckBox();
		diffTwoCheckBox.setText("Diffusion with Two Diffusing Components");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.gridy = 15;
		gridBagConstraints_6.gridx = 0;
		add(diffTwoCheckBox, gridBagConstraints_6);
		
		JLabel reactionOnlyLabel = new JLabel("Reaction only models :");
		reactionOnlyLabel.setFont(new Font("", Font.BOLD, 12));
		reactionOnlyLabel.setForeground(new Color(0, 0, 128));
		GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.anchor = GridBagConstraints.WEST;
		gridBagConstraints_9.gridy = 19;
		gridBagConstraints_9.gridx = 0;
		add(reactionOnlyLabel, gridBagConstraints_9);
		
		koffCheckBox = new JCheckBox();
		koffCheckBox.setText("Finding reaction off rate");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.anchor = GridBagConstraints.WEST;
		gridBagConstraints_10.gridy = 20;
		gridBagConstraints_10.gridx = 0;
		add(koffCheckBox, gridBagConstraints_10);
		
		/*diffBindingCheckBox = new JCheckBox();
		diffBindingCheckBox.setText("Diffusion plus Binding");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.gridy = 15;
		gridBagConstraints_7.gridx = 0;
		add(diffBindingCheckBox, gridBagConstraints_7);*/
	}
	
	public boolean[] getModelTypes()
	{
		boolean[] result = new boolean[FRAPModel.NUM_MODEL_TYPES];
		if(diffOneCheckBox.isSelected())
		{
			result[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] = true;
		}
		if(diffTwoCheckBox.isSelected())
		{
			result[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] = true;
		}
		if(koffCheckBox.isSelected())
		{
			result[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] = true;
		}
		return result;
	}
	
	public void clearAllSelected()
	{
		diffOneCheckBox.setSelected(false);
		diffTwoCheckBox.setSelected(false);
		koffCheckBox.setSelected(false);
//		diffBindingCheckBox.setSelected(false);
	}
	
	public void setDiffOneSelected(boolean bSelected)
	{
		diffOneCheckBox.setSelected(bSelected);
	}
	
	public void setDiffTwoSelected(boolean bSelected)
	{
		diffTwoCheckBox.setSelected(bSelected);
	}
	
	public void setReactionOffRateSelected(boolean bSelected)
	{
		koffCheckBox.setSelected(bSelected);
	}
//	public void setDiffBindingSelected(boolean bSelected)
//	{
//		diffBindingCheckBox.setSelected(bSelected);
//	}
}
