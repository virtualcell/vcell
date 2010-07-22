package cbit.vcell.microscopy.batchrun.gui.chooseModelWizard;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;

public class ModelTypesPanel extends JPanel
{
	private JCheckBox diffOneCheckBox = null;
	private JCheckBox diffTwoCheckBox = null;
//	private JCheckBox diffBindingCheckBox = null;
	
	public ModelTypesPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,7,7,7,7,7,7,7,0,7};
		setLayout(gridBagLayout);

		final JLabel choosePossibleModelLabel = new JLabel();
		choosePossibleModelLabel.setFont(new Font("", Font.BOLD | Font.ITALIC, 16));
		choosePossibleModelLabel.setForeground(new Color(0, 0, 128));
		choosePossibleModelLabel.setText("Choose a Model Type");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(choosePossibleModelLabel, gridBagConstraints);

		final JLabel chooseOneOrLabel = new JLabel();
		chooseOneOrLabel.setText("Choose a model that will be evaluated across all the FRAP datasets.");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.gridy = 4;
		gridBagConstraints_1.gridx = 0;
		add(chooseOneOrLabel, gridBagConstraints_1);

		final JLabel selectedModelsWillLabel = new JLabel();
		selectedModelsWillLabel.setText("The estimation results will be the estimated parameters for each FRAP dataset");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 5;
		gridBagConstraints_2.gridx = 0;
		add(selectedModelsWillLabel, gridBagConstraints_2);

		final JLabel diffusionWithOneLabel = new JLabel();
		diffusionWithOneLabel.setText("and the average and standard deviation of the estimated parameters.");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 6;
		gridBagConstraints_3.gridx = 0;
		add(diffusionWithOneLabel, gridBagConstraints_3);

		diffOneCheckBox = new JCheckBox();
		diffOneCheckBox.setSelected(true);
		diffOneCheckBox.setText("Diffusion with One Diffusing Component");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.gridy = 13;
		gridBagConstraints_5.gridx = 0;
		add(diffOneCheckBox, gridBagConstraints_5);

		diffTwoCheckBox = new JCheckBox();
		diffTwoCheckBox.setText("Diffusion with Two Diffusing Components");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.gridy = 14;
		gridBagConstraints_6.gridx = 0;
		add(diffTwoCheckBox, gridBagConstraints_6);

//		diffBindingCheckBox = new JCheckBox();
//		diffBindingCheckBox.setText("Diffusion plus Binding");
//		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
//		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
//		gridBagConstraints_7.gridy = 15;
//		gridBagConstraints_7.gridx = 0;
		
		//put checkboxes into a button group
		ButtonGroup bg = new ButtonGroup();
		bg.add(diffOneCheckBox);
		bg.add(diffTwoCheckBox);
//		bg.add(diffBindingCheckBox);
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
//		if(diffBindingCheckBox.isSelected())
//		{
//			result[FRAPModel.IDX_MODEL_DIFF_BINDING] = true;
//		}
		return result;
	}
	
	public void clearAllSelected()
	{
		diffOneCheckBox.setSelected(false);
		diffTwoCheckBox.setSelected(false);
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
	
//	public void setDiffBindingSelected(boolean bSelected)
//	{
//		diffBindingCheckBox.setSelected(bSelected);
//	}
}
