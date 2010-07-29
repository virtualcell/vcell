package cbit.vcell.microscopy.gui.estparamwizard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import cbit.plot.Plot2DPanel;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.modelopt.gui.MultisourcePlotPane;

public class SubPlotPanel extends JPanel
{
    private SummaryPlotPanel parent;
    private JLabel modelLable;
    private HyperLinkLabel hypDetail;
    private MultisourcePlotPane plotPane;
    private FRAPSingleWorkspace frapWorkspace;

    public SubPlotPanel(SummaryPlotPanel arg_parent) 
    {
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel headingLabel = new JLabel("Plots under Selected ROIs");
        headingLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Details", new HyperLinkListener(), 0);
        hypDetail.setHorizontalAlignment(JLabel.RIGHT);

        modelLable = new JLabel("3 Models");
        modelLable.setOpaque(true);
        modelLable.setBackground(new Color(166, 166, 255));
        modelLable.setBorder(BorderFactory.createEmptyBorder(1,5,1,1));

        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.gridx = 0;
        gc1.gridy = 0;
        gc1.weightx = 1.0;
        gc1.anchor = GridBagConstraints.WEST;
        gc1.fill = GridBagConstraints.HORIZONTAL;
        add(headingLabel, gc1);

        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.fill = GridBagConstraints.HORIZONTAL;
        gc2.gridx = 1;
        gc2.gridy = 0;
        gc2.anchor = GridBagConstraints.EAST;
        add(hypDetail, gc2);

        GridBagConstraints gc3 = new GridBagConstraints();
        gc3.anchor = GridBagConstraints.WEST;
        gc3.gridx = 0;
        gc3.gridy = 1; 
        gc3.gridwidth = 2;
        gc3.weightx = 1.0;
        gc3.fill = GridBagConstraints.BOTH;
        add(modelLable, gc3);
        //add plot pane
        plotPane = new MultisourcePlotPane();
        plotPane.setPreferredSize(new Dimension(660,460));
        plotPane.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        plotPane.setVisible(false);//by default it is not visible
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridwidth = 2;
        gc.weightx = 1.0;
        gc.weighty = 1.0;
        gc.gridy = 2;
        gc.fill = GridBagConstraints.BOTH;
        add(plotPane, gc);
   }



    private class HyperLinkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            boolean isDetail = hypDetail.getText().equals("Details");
            setDetail(isDetail);
            hypDetail.setText(isDetail? "Less Details" : "Details");
        }

    }

    public void setDetail(boolean isDetail) {
        if (isDetail) {
            modelLable.setVisible(false);
            if(plotPane != null)
            {
            	plotPane.setVisible(true);
            }
        }
        else {
        	if(plotPane != null)
        	{
        		plotPane.setVisible(false);
        	}
            modelLable.setVisible(true);
        }
        parent.repaint();
    }

    public void setPlotData(DataSource[] argDataSources)
    {
    	//the following paragraph of code is just to get selected color for selected ROIs
		//and make them the same as we show on ChooseModel_RoiForErrorPanel/RoiForErrorPanel
		int validROISize = FRAPData.VFRAP_ROI_ENUM.values().length-2;//double valid ROI colors (not include cell and background)
		//need to know how many models(1,2,3) are selected to generate color sets, total 1(exp) + number of selected models
		int numColorSet = frapWorkspace.getWorkingFrapStudy().getSelectedModels().size() + 1;
		Color[] fullColors = Plot2DPanel.generateAutoColor(validROISize*numColorSet, getBackground(), new Integer(0));
		boolean[] selectedROIs = frapWorkspace.getWorkingFrapStudy().getSelectedROIsForErrorCalculation();
		int selectedROICounter = 0;
		for (int i=0; i<selectedROIs.length; i++)
		{
			if(selectedROIs[i])
			{
				selectedROICounter++;
			}
		}
		Color[] selectedColors = new Color[selectedROICounter*numColorSet];//ROI is a comparison of exp and several opt/sim results, color set should be number of models +1
		int selectedColorIdx = 0;
		for(int i=0; i<selectedROIs.length; i++)
		{
			if(selectedROIs[i] && i>2) //skip cell and background ROIs
			{
				for(int j = 0; j < numColorSet; j++)//len is numColorSet, exp + all opt/sim 
				{
					selectedColors[selectedColorIdx+selectedROICounter*j] = fullColors[i-2+validROISize*j];
				}
				selectedColorIdx++;
			}
			else if(selectedROIs[i] && i==0)// bleached ROI
			{
				for(int j = 0; j < numColorSet; j++)//len is numColorSet, exp + all opt/sim 
				{
					selectedColors[selectedColorIdx+selectedROICounter*j] = fullColors[i+validROISize*j];
				}
				selectedColorIdx++;
			}
		}
    	//above code trying to get selected color for exp plots and multiple opt/sim plots, which corresponding to the colors in 
    	//ChooseModel_RoiForErrorPanel/RoiForErrorPanel. However, since the numColors are different with the colors in ChooseModel_RoiForErrorPanel/RoiForErrorPanel,
    	//we cannot always get the same colors as in those panels. (only when there is a single model selected)
    	
    	plotPane.setDataSources(argDataSources, selectedColors);
    	plotPane.selectAll();
    }
    
    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
		this.frapWorkspace = frapWorkspace;
		modelLable.setText(frapWorkspace.getWorkingFrapStudy().getSelectedModels().size() + " Models");
	}
}
