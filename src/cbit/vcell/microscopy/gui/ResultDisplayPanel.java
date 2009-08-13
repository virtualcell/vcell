package cbit.vcell.microscopy.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import cbit.vcell.microscopy.EstimatedParameterTableRenderer;


public class ResultDisplayPanel extends JPanel
{
	private JLabel oneDiffComponentLabel = null;
	private JLabel twoDiffComponentLabel = null;
	private JLabel reacBindingLabel = null;
	private JPanel titlePanel = null;
	private JPanel modelPanel = null;
	private JPanel tablePanel = null;
	private JTable resultTable = null;
	private JPanel buttonPanel = null;
	private JButton show2DResultButton = null;
	private JButton runSimButton = null;
	ResultDisplayPanel()
	{
		initial();
	}
	private void initial()
	{
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowHeights = new int[] {0,7,0,0,7,0,0,0,7,7,7,7,7,7,0,0,0,0,7,7,0,7,7,7};
		setLayout(gridBagLayout);
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.ipady = 0;
		gridBagConstraints1.ipadx = 0;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.ipadx = 18;
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.gridheight = 3;
		gridBagConstraints2.ipady = 0;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.ipadx = 5;
		gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 10;
		gridBagConstraints2.gridheight = 4;
//		gridBagConstraints1.ipady = 0;
//		gridBagConstraints1.ipadx = 0;
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 22;
//		gridBagConstraints2.gridheight = 3;
//		gridBagConstraints2.ipady = 0;
//		gridBagConstraints2.ipadx = 0;
		
		add(getTitlePanel(), gridBagConstraints1);
		add(getBestModelPanel(), gridBagConstraints2);
		add(getTablePanel(), gridBagConstraints3);
		add(getButtonPanel(), gridBagConstraints4);
	}
	
	private JPanel getTitlePanel()
	{
		if(titlePanel == null)
		{
			titlePanel = new JPanel();
			JLabel titleLabel = new JLabel("Data Analysis Results");
			titleLabel.setFont(new Font("arial", Font.BOLD+Font.ITALIC, 14));
			titleLabel.setForeground(new Color(0,0,128));
			titlePanel.add(titleLabel, BorderLayout.WEST);
		}
		return titlePanel;
	}
	
	public JPanel getBestModelPanel()
	{
		if(modelPanel == null)
		{
			modelPanel = new JPanel();
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,7,0,7};
			gridBagLayout.columnWidths = new int[] {7,0,0,0,0,0,7,0,7,0,7,7};
			modelPanel = new JPanel(gridBagLayout);
			modelPanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243),1), "Best Model")); 
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.gridy = 0;
//			gridBagConstraints1.ipady = 0;
//			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 2;
			gridBagConstraints2.gridy = 2;
//			gridBagConstraints2.ipady = 0;
//			gridBagConstraints2.ipadx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
					
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridwidth = 2;
			gridBagConstraints3.gridx = 9;
			gridBagConstraints3.gridy = 0;
//			gridBagConstraints3.ipady = 0;
//			gridBagConstraints3.ipadx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.CENTER;
			
			modelPanel.add(getOneDiffComLabel(), gridBagConstraints1);
			modelPanel.add(getTwoDiffComLabel(), gridBagConstraints2);
			modelPanel.add(getReacBindingLabel(), gridBagConstraints3);
		}	
		return modelPanel;
	}
	
	public JLabel getOneDiffComLabel()
	{
		if(oneDiffComponentLabel == null)
		{
			oneDiffComponentLabel = new JLabel("Pure Diffusion (1 Component)");
			oneDiffComponentLabel.setFont(new Font("arial", Font.PLAIN, 12));
		}
		return oneDiffComponentLabel;
	}
	
	public JLabel getTwoDiffComLabel()
	{
		if(twoDiffComponentLabel == null)
		{
			twoDiffComponentLabel = new JLabel("Pure Diffusion (2 Components)");
			twoDiffComponentLabel.setFont(new Font("arial", Font.PLAIN, 12));
		}
		return twoDiffComponentLabel;
	}
	
	public JLabel getReacBindingLabel()
	{
		if(reacBindingLabel == null)
		{
			reacBindingLabel = new JLabel("Reaction plus Binding");
			reacBindingLabel.setFont(new Font("arial", Font.PLAIN, 12));
		}
		return reacBindingLabel;
	}
	
	public void highLightOneDiffLabel()
	{
		oneDiffComponentLabel.setBorder(new LineBorder(Color.black, 2));
	}
	
	public void highLightTwoDiffLabel()
	{
		twoDiffComponentLabel.setBorder(new LineBorder(Color.black, 2));
	}
	
	public void highLightReacBindingLabel()
	{
		reacBindingLabel.setBorder(new LineBorder(Color.black, 2));
	}
	
	private JPanel getTablePanel()
	{
		if(tablePanel == null)
		{
			final GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,0,0,7,7};
			tablePanel = new JPanel(gridBagLayout);
			tablePanel.setBorder(new TitledBorder(new LineBorder(new Color(153, 186,243),1), "Best Parameters"));//new Color(153, 186,243)
			JScrollPane tableScroll = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tableScroll.setName("");
			tableScroll.setViewportView(getRestultTable());
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.ipady = 235;
			gridBagConstraints1.ipadx = 300;
			gridBagConstraints1.gridheight = 3;
			gridBagConstraints1.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
//			gridBagConstraints1.ipady = 0;
//			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;

			tablePanel.add(tableScroll, gridBagConstraints1);
		}
		return tablePanel;
	}
	 
	private JPanel getButtonPanel()
	{
		if(buttonPanel == null)
		{
			buttonPanel = new JPanel(new BorderLayout());
			buttonPanel.add(getShowResultButton(), BorderLayout.EAST);
			setResultsButtonEnabled(false); //enable it after loading frap data/doc(if sim data exists).
			buttonPanel.add(getRunSimButton(), BorderLayout.WEST);
			buttonPanel.add(new JLabel("             "), BorderLayout.CENTER); //used to nicely arrange buttons
		}
		return buttonPanel;
	}
	
	public JButton getShowResultButton()
	{
		if(show2DResultButton == null)
		{
			show2DResultButton = new JButton("View Spatial Results");
		}
		return show2DResultButton;
	}
	
	public JButton getRunSimButton()
	{
		if(runSimButton == null)
		{
			runSimButton = new JButton("Run Spatial Simulation");
		}
		return runSimButton;
	}
	
	public JTable getRestultTable()
	{
		if(resultTable == null)
		{
			resultTable = new JTable();
//			resultTable.setModel();//set table model
		}
		return resultTable;
	}
	
	public void setResultsButtonEnabled(boolean enabled)
	{
		getShowResultButton().setEnabled(enabled);
	}
}
