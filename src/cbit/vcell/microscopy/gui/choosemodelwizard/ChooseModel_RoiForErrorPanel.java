package cbit.vcell.microscopy.gui.choosemodelwizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import cbit.vcell.microscopy.gui.ROIImagePanel;

public class ChooseModel_RoiForErrorPanel extends JPanel
{
	private JTable table;
	JPanel centerPanel = null;
	JPanel roiImagePanel = null;
	public ChooseModel_RoiForErrorPanel() {
		super();
		setLayout(new BorderLayout());

		JLabel selectRoisForLabel = new JLabel();
		selectRoisForLabel.setForeground(new Color(0, 0, 128));
		selectRoisForLabel.setFont(new Font("", Font.BOLD | Font.ITALIC, 16));
		selectRoisForLabel.setText("Select ROIs for Error Calculation");
		add(selectRoisForLabel, BorderLayout.NORTH);

		centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,7,7};
		gridBagLayout.rowHeights = new int[] {0,7,7,7,7,7,7,7,7,7,7,7};
		centerPanel.setLayout(gridBagLayout);

		final JLabel selectTheRegionsLabel = new JLabel();
		selectTheRegionsLabel.setForeground(new Color(0, 0, 128));
		selectTheRegionsLabel.setFont(new Font("", Font.BOLD, 12));
		selectTheRegionsLabel.setText("Select the regions that you want to include");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		centerPanel.add(selectTheRegionsLabel, gridBagConstraints);

		roiImagePanel = new ROIImagePanel();
		roiImagePanel.setSize(200,150);
		roiImagePanel.setBackground(Color.black);
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.ipadx = 155;
		gridBagConstraints_12.ipady = 255;
		gridBagConstraints_12.gridheight = 14;
		gridBagConstraints_12.gridy = 0;
		gridBagConstraints_12.gridx = 3;
		centerPanel.add(roiImagePanel, gridBagConstraints_12);

		final JLabel inComputingTheLabel = new JLabel();
		inComputingTheLabel.setForeground(new Color(0, 0, 128));
		inComputingTheLabel.setFont(new Font("", Font.BOLD, 12));
		inComputingTheLabel.setText("in computing the errors in optimization");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.anchor = GridBagConstraints.WEST;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		centerPanel.add(inComputingTheLabel, gridBagConstraints_1);

		final JCheckBox roi_cellCheckBox = new JCheckBox();
		roi_cellCheckBox.setText("ROI_Cell");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.WEST;
		gridBagConstraints_2.gridy = 4;
		gridBagConstraints_2.gridx = 0;
		centerPanel.add(roi_cellCheckBox, gridBagConstraints_2);

		final JCheckBox roi_bleachedCheckBox = new JCheckBox();
		roi_bleachedCheckBox.setText("ROI_Bleached");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.anchor = GridBagConstraints.WEST;
		gridBagConstraints_3.gridy = 5;
		gridBagConstraints_3.gridx = 0;
		centerPanel.add(roi_bleachedCheckBox, gridBagConstraints_3);

		final JCheckBox roi_bleached_ring1CheckBox = new JCheckBox();
		roi_bleached_ring1CheckBox.setText("ROI_Bleached_RING1");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.gridy = 6;
		gridBagConstraints_4.gridx = 0;
		centerPanel.add(roi_bleached_ring1CheckBox, gridBagConstraints_4);

		final JCheckBox roi_bleached_ringCheckBox = new JCheckBox();
		roi_bleached_ringCheckBox.setText("ROI_Bleached_RING2");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.gridy = 7;
		gridBagConstraints_5.gridx = 0;
		centerPanel.add(roi_bleached_ringCheckBox, gridBagConstraints_5);

		final JCheckBox roi_bleached_ring3CheckBox = new JCheckBox();
		roi_bleached_ring3CheckBox.setText("ROI_Bleached_RING3");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.anchor = GridBagConstraints.WEST;
		gridBagConstraints_6.gridy = 8;
		gridBagConstraints_6.gridx = 0;
		centerPanel.add(roi_bleached_ring3CheckBox, gridBagConstraints_6);

		final JCheckBox roi_bleached_ring4CheckBox = new JCheckBox();
		roi_bleached_ring4CheckBox.setText("ROI_Bleached_RING4");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.gridy = 9;
		gridBagConstraints_7.gridx = 0;
		centerPanel.add(roi_bleached_ring4CheckBox, gridBagConstraints_7);

		final JCheckBox roi_bleached_ring5CheckBox = new JCheckBox();
		roi_bleached_ring5CheckBox.setText("ROI_Bleached_RING5");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 10;
		gridBagConstraints_8.gridx = 0;
		centerPanel.add(roi_bleached_ring5CheckBox, gridBagConstraints_8);

		final JCheckBox roi_bleached_ring6CheckBox = new JCheckBox();
		roi_bleached_ring6CheckBox.setText("ROI_Bleached_RING6");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.anchor = GridBagConstraints.WEST;
		gridBagConstraints_9.gridy = 11;
		gridBagConstraints_9.gridx = 0;
		centerPanel.add(roi_bleached_ring6CheckBox, gridBagConstraints_9);

		final JCheckBox roi_bleached_ring7CheckBox = new JCheckBox();
		roi_bleached_ring7CheckBox.setText("ROI_Bleached_RING7");
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.anchor = GridBagConstraints.WEST;
		gridBagConstraints_10.gridy = 12;
		gridBagConstraints_10.gridx = 0;
		centerPanel.add(roi_bleached_ring7CheckBox, gridBagConstraints_10);

		final JCheckBox roi_bleached_ring8CheckBox = new JCheckBox();
		roi_bleached_ring8CheckBox.setText("ROI_Bleached_RING8");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.anchor = GridBagConstraints.WEST;
		gridBagConstraints_11.gridy = 13;
		gridBagConstraints_11.gridx = 0;
		centerPanel.add(roi_bleached_ring8CheckBox, gridBagConstraints_11);
		
		
	}
	
}
