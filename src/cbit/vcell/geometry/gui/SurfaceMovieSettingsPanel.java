package cbit.vcell.geometry.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cbit.util.NumberUtils;

public class SurfaceMovieSettingsPanel extends JPanel implements ActionListener{
	private final JLabel movieWidthValLabel = new JLabel();
	private final JLabel movieHeightValLabel = new JLabel();
	private final JComboBox fpsComboBox = new JComboBox();
	private final JComboBox formatComboBox = new JComboBox();
	private final JComboBox beginTimeComboBox = new JComboBox();
	private final JComboBox endTimeComboBox = new JComboBox();
	private final JLabel totalFramesValLabel = new JLabel();
	private final JLabel totalDurationValLabel = new JLabel();
	
	public SurfaceMovieSettingsPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,7};
		gridBagLayout.rowHeights = new int[] {7,0,7,7,7,7,7,7};
		setLayout(gridBagLayout);

		final JLabel movieFormatLabel = new JLabel();
		movieFormatLabel.setText("Movie Format:");
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_14.anchor = GridBagConstraints.EAST;
		gridBagConstraints_14.gridx = 0;
		gridBagConstraints_14.gridy = 0;
		add(movieFormatLabel, gridBagConstraints_14);

		formatComboBox.setActionCommand("formatComboBox");
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_15.weightx = 1;
		gridBagConstraints_15.gridy = 0;
		gridBagConstraints_15.gridx = 1;
		add(formatComboBox, gridBagConstraints_15);

		final JLabel movieWidthLabel = new JLabel();
		movieWidthLabel.setText("Movie Width:");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 0;
		add(movieWidthLabel, gridBagConstraints);

		movieWidthValLabel.setText("Movie Width Val");
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.weightx = 1;
		gridBagConstraints_7.gridy = 1;
		gridBagConstraints_7.gridx = 1;
		add(movieWidthValLabel, gridBagConstraints_7);

		final JLabel movieHeightLabel = new JLabel();
		movieHeightLabel.setText("Movie Height:");
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_1.anchor = GridBagConstraints.EAST;
		gridBagConstraints_1.gridy = 2;
		gridBagConstraints_1.gridx = 0;
		add(movieHeightLabel, gridBagConstraints_1);

		movieHeightValLabel.setText("Movie Height Val");
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.weightx = 1;
		gridBagConstraints_8.anchor = GridBagConstraints.WEST;
		gridBagConstraints_8.gridy = 2;
		gridBagConstraints_8.gridx = 1;
		add(movieHeightValLabel, gridBagConstraints_8);

		final JLabel framesPerSecondLabel = new JLabel();
		framesPerSecondLabel.setText("Frames Per Second:");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_2.anchor = GridBagConstraints.EAST;
		gridBagConstraints_2.gridy = 3;
		gridBagConstraints_2.gridx = 0;
		add(framesPerSecondLabel, gridBagConstraints_2);

		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_9.weightx = 1;
		gridBagConstraints_9.gridy = 3;
		gridBagConstraints_9.gridx = 1;
		add(fpsComboBox, gridBagConstraints_9);
		fpsComboBox.addActionListener(this);
		
		final JLabel beginTimeLabel = new JLabel();
		beginTimeLabel.setText("Begin Time:");
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_3.anchor = GridBagConstraints.EAST;
		gridBagConstraints_3.gridy = 4;
		gridBagConstraints_3.gridx = 0;
		add(beginTimeLabel, gridBagConstraints_3);

		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.weightx = 1;
		gridBagConstraints_10.gridy = 4;
		gridBagConstraints_10.gridx = 1;
		add(beginTimeComboBox, gridBagConstraints_10);
		beginTimeComboBox.addActionListener(this);

		final JLabel endTimeLabel = new JLabel();
		endTimeLabel.setText("End Time:");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_4.anchor = GridBagConstraints.EAST;
		gridBagConstraints_4.gridy = 5;
		gridBagConstraints_4.gridx = 0;
		add(endTimeLabel, gridBagConstraints_4);

		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_11.weightx = 1;
		gridBagConstraints_11.gridy = 5;
		gridBagConstraints_11.gridx = 1;
		add(endTimeComboBox, gridBagConstraints_11);
		endTimeComboBox.addActionListener(this);

		final JLabel totalFramesLabel = new JLabel();
		totalFramesLabel.setText("Total Frames:");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_5.anchor = GridBagConstraints.EAST;
		gridBagConstraints_5.gridy = 6;
		gridBagConstraints_5.gridx = 0;
		add(totalFramesLabel, gridBagConstraints_5);

		totalFramesValLabel.setText("Total Frames Val");
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.anchor = GridBagConstraints.WEST;
		gridBagConstraints_12.weightx = 1;
		gridBagConstraints_12.gridy = 6;
		gridBagConstraints_12.gridx = 1;
		add(totalFramesValLabel, gridBagConstraints_12);

		final JLabel totalDurationLabel = new JLabel();
		totalDurationLabel.setText("Movie Duration (sec):");
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(0, 0, 0, 4);
		gridBagConstraints_6.anchor = GridBagConstraints.EAST;
		gridBagConstraints_6.gridy = 7;
		gridBagConstraints_6.gridx = 0;
		add(totalDurationLabel, gridBagConstraints_6);

		totalDurationValLabel.setText("Total Duration Val");
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.anchor = GridBagConstraints.WEST;
		gridBagConstraints_13.weightx = 1;
		gridBagConstraints_13.gridy = 7;
		gridBagConstraints_13.gridx = 1;
		add(totalDurationValLabel, gridBagConstraints_13);
	}

	public void init(int width,int height,double[] allTimes){
		movieWidthValLabel.setText(width+"");
		movieHeightValLabel.setText(height+"");
		try{
			formatComboBox.removeActionListener(this);
			formatComboBox.addItem("QuickTime Movie *.qt");
			formatComboBox.setSelectedIndex(0);

			fpsComboBox.removeActionListener(this);
			for (int i = 1; i <= 30; i++) {
				fpsComboBox.addItem(i+"");
			}
			fpsComboBox.setSelectedIndex(9);
			
			beginTimeComboBox.removeActionListener(this);
			endTimeComboBox.removeActionListener(this);
			beginTimeComboBox.removeAllItems();
			endTimeComboBox.removeAllItems();
			for (int i = 0; i < allTimes.length; i++) {
				beginTimeComboBox.addItem(allTimes[i]+"");
				endTimeComboBox.addItem(allTimes[i]+"");
			}
			beginTimeComboBox.setSelectedIndex(0);
			endTimeComboBox.setSelectedIndex(allTimes.length-1);
		}finally{
			fpsComboBox.addActionListener(this);
			beginTimeComboBox.addActionListener(this);
			endTimeComboBox.addActionListener(this);
		}
		updateGUI();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == beginTimeComboBox){
			if(beginTimeComboBox.getSelectedIndex() > endTimeComboBox.getSelectedIndex()){
				beginTimeComboBox.setSelectedIndex(endTimeComboBox.getSelectedIndex());
			}
			updateGUI();
		}else if(e.getSource() == endTimeComboBox){
			if(endTimeComboBox.getSelectedIndex() < beginTimeComboBox.getSelectedIndex()){
				endTimeComboBox.setSelectedIndex(beginTimeComboBox.getSelectedIndex());
			}
			updateGUI();
		}else if(e.getSource() == fpsComboBox){
			updateGUI();
		}
	}
	private void updateGUI(){
		int totalFrames = (endTimeComboBox.getSelectedIndex()- beginTimeComboBox.getSelectedIndex()+1);
		totalFramesValLabel.setText(totalFrames+"");
		totalDurationValLabel.setText(NumberUtils.formatNumber((double)totalFrames/(double)(fpsComboBox.getSelectedIndex()+1),3)+" seconds");
	}
	public int getFramesPerSecond(){
		return fpsComboBox.getSelectedIndex()+1;
	}
	public int getBeginTimeIndex(){
		return beginTimeComboBox.getSelectedIndex();
	}
	public int getEndTimeIndex(){
		return endTimeComboBox.getSelectedIndex();
	}
}
