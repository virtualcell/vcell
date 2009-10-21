package cbit.vcell.microscopy.gui.loaddatawizard;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.NumberUtils;

import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.microscopy.DataVerifyInfo;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPStudy;

public class LoadFRAPData_SummaryPanel extends JPanel
{
	public final String loadSuccessInfo = "Data loaded. Please varify/modify the following information.";
	public final String loadFailedInfo= "Data loading failed.";
	private JTextField imgSizeY = null;
	private JTextField imgSizeX = null;
	private JComboBox eTimeCombo = null;
	private JComboBox sTimeCombo = null;
	private JLabel totTimeLabel = null;
	private JPanel timePanel = null;
	private JPanel sizePanel = null;
	private JLabel loadInfo = null;
	public LoadFRAPData_SummaryPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,7,0,7,7,7,7};
		setLayout(gridBagLayout);

		loadInfo = new JLabel();
		loadInfo.setForeground(new Color(0, 0, 128));
		loadInfo.setFont(new Font("", Font.BOLD | Font.ITALIC, 15));
		loadInfo.setText(loadSuccessInfo);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridx = 0;
		add(loadInfo, gridBagConstraints1);
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridy = 4;
		gridBagConstraints2.gridheight = 2;
		gridBagConstraints2.gridx = 0;
		add(getTimePanel(), gridBagConstraints2);
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.ipadx = 175;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.gridy = 10;
		gridBagConstraints3.gridheight = 4;
		gridBagConstraints3.gridx = 0;
		add(getSizePanel(), gridBagConstraints3);
	}

	public JPanel getTimePanel()
	{
		if(timePanel == null)
		{
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,7,7};
			gridBagLayout.columnWidths = new int[] {7,7,7,0,0,7,0,7,7,7,0,0,7};
			timePanel = new JPanel(gridBagLayout);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 1;
			timePanel.add(new JLabel("Image starts at:"), gridBagConstraints1);

			sTimeCombo = new JComboBox();
			final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
			gridBagConstraints_5.gridy = 0;
			gridBagConstraints_5.gridx = 3;
			timePanel.add(sTimeCombo, gridBagConstraints_5);
			sTimeCombo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					updateTimeLabel();
				}
			});

			final JLabel sLabel1 = new JLabel();
			sLabel1.setText(" s");
			final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
			gridBagConstraints_7.gridy = 0;
			gridBagConstraints_7.gridx = 4;
			timePanel.add(sLabel1, gridBagConstraints_7);

			final JLabel eTimeLabel = new JLabel();
			eTimeLabel.setText("Image ends at:");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.gridx = 8;
//			gridBagConstraints3.gridwidth = 2;
			timePanel.add(eTimeLabel, gridBagConstraints3);

			eTimeCombo = new JComboBox();
			final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
			gridBagConstraints_6.gridy = 0;
			gridBagConstraints_6.gridx = 10;
			timePanel.add(eTimeCombo, gridBagConstraints_6);
			eTimeCombo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					updateTimeLabel();
				}
			});
			
			final JLabel sLabel2 = new JLabel();
			sLabel2.setText(" s");
			final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
			gridBagConstraints_8.gridy = 0;
			gridBagConstraints_8.gridx = 12;
			timePanel.add(sLabel2, gridBagConstraints_8);

			final JLabel tolTimeLabel = new JLabel();
			tolTimeLabel.setText("Image duration:");
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridx = 1;
			timePanel.add(tolTimeLabel, gridBagConstraints);

			totTimeLabel = new JLabel();
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.anchor = GridBagConstraints.WEST;
			gridBagConstraints_1.gridy = 1;
			gridBagConstraints_1.gridx = 3;
			timePanel.add(totTimeLabel, gridBagConstraints_1);

			final JLabel sLabel3 = new JLabel();
			sLabel3.setText(" s");
			final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
			gridBagConstraints_9.gridy = 1;
			gridBagConstraints_9.gridx = 4;
			timePanel.add(sLabel3, gridBagConstraints_9);
			timePanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243), 1),"Adjust FRAP Dataset"));
		}
		return timePanel;
	}
	
	public JPanel getSizePanel()
	{
		if(sizePanel == null)
		{
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,7,7};
//			gridBagLayout.rowHeights = new int[] {0,7,7};
//			gridBagLayout.columnWidths = new int[] {0,7,7,0,7};
			gridBagLayout.columnWidths = new int[] {7,7,7};
			sizePanel = new JPanel(gridBagLayout);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.gridx = 1;
			sizePanel.add(new JLabel("Image size X "), gridBagConstraints1);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.gridx = 1;

			imgSizeX = new JTextField(12);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 2;
			sizePanel.add(imgSizeX, gridBagConstraints);

			final JLabel umLabel1 = new JLabel();
			umLabel1.setText(" um");
			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.gridy = 0;
			gridBagConstraints_2.gridx = 4;
			sizePanel.add(umLabel1, gridBagConstraints_2);
			sizePanel.add(new JLabel("Image size Y "), gridBagConstraints2);

			imgSizeY = new JTextField(12);
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.gridy = 2;
			gridBagConstraints_1.gridx = 2;
			sizePanel.add(imgSizeY, gridBagConstraints_1);

			final JLabel umLabel2 = new JLabel();
			umLabel2.setText(" um");
			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
			gridBagConstraints_3.gridy = 2;
			gridBagConstraints_3.gridx = 4;
			sizePanel.add(umLabel2, gridBagConstraints_3);
			sizePanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243), 1),"Adjust FRAP Image Size"));
		}
		return sizePanel;
	}
	
	public void setLoadInfo(final FRAPStudy frapStudy)
	{
		if(frapStudy!=null && frapStudy.getFrapData()!=null)
		{
			BeanUtils.enableComponents(getTimePanel(), true);
			BeanUtils.enableComponents(getTimePanel(), true);
			//title
			loadInfo.setText(loadSuccessInfo);
			//time
			sTimeCombo.removeAllItems();
			eTimeCombo.removeAllItems();
			double[] timeSteps = frapStudy.getFrapData().getImageDataset().getImageTimeStamps();
			for(int i =0 ; i<timeSteps.length; i++)
			{
				sTimeCombo.addItem(timeSteps[i] + "");
				eTimeCombo.addItem(timeSteps[i] + "");
			}
			sTimeCombo.setSelectedIndex(0);
			eTimeCombo.setSelectedIndex(timeSteps.length-1);
			totTimeLabel.setText((timeSteps[timeSteps.length-1] - timeSteps[0])+""); 
			ImageDataset imgDataset = frapStudy.getFrapData().getImageDataset();
			imgSizeX.setText(NumberUtils.formatNumber(imgDataset.getExtent().getX(), 15));
			imgSizeY.setText(NumberUtils.formatNumber(imgDataset.getExtent().getY(), 15));
		}
		else
		{
			//title
			loadInfo.setText(loadFailedInfo);
			//time 
			sTimeCombo.removeAllItems();
			sTimeCombo.addItem("     N/A     ");
			eTimeCombo.removeAllItems();
			eTimeCombo.addItem("     N/A     ");
			totTimeLabel.setText("");
			imgSizeX.setText("");
			imgSizeY.setText("");
			BeanUtils.enableComponents(getTimePanel(), false);
			BeanUtils.enableComponents(getTimePanel(), false);
		}
	}
	
	public String checkInputValidity()
	{
		if(Double.parseDouble((String)sTimeCombo.getSelectedItem()) > Double.parseDouble((String)eTimeCombo.getSelectedItem()))
		{
			return "Starting time should NOT be greater than ending time.";
		}
		try{
			Double.parseDouble(imgSizeX.getText());
		}catch(NumberFormatException e)
		{
			return "Image size X input error " + e.getMessage();
		}
		try{
			Double.parseDouble(imgSizeY.getText());
		}catch(NumberFormatException e)
		{
			return "Image size Y input error " + e.getMessage();
		}
		return "";
	}
	
	public DataVerifyInfo saveDataInfo()
	{
		if(loadInfo.getText().equals(loadSuccessInfo)) //loaded successfully
		{
			return new DataVerifyInfo(Double.parseDouble((String)sTimeCombo.getSelectedItem()), Double.parseDouble((String)eTimeCombo.getSelectedItem()),
									  Double.parseDouble(imgSizeX.getText()), Double.parseDouble(imgSizeY.getText()), 
									  sTimeCombo.getSelectedIndex(), eTimeCombo.getSelectedIndex());
		}
		return null;
	}
	
	private void updateTimeLabel() {
		String stString = (String)sTimeCombo.getSelectedItem();
		String etString = (String)eTimeCombo.getSelectedItem();
		if(stString != null && etString != null)
		{
			totTimeLabel.setText(( Double.parseDouble(etString) - Double.parseDouble(stString) )+"");
		}
		else
		{
			totTimeLabel.setText("");
		}
		
	}
	
}
