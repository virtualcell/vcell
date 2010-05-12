package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.vcell.util.gui.DialogUtils;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.microscopy.gui.estparamwizard.AnalysisTableRenderer;
import cbit.vcell.microscopy.gui.estparamwizard.EstParams_OneDiffComponentPanel;
import cbit.vcell.microscopy.gui.estparamwizard.EstParams_TwoDiffComponentPanel;
import cbit.vcell.microscopy.gui.estparamwizard.HyperLinkLabel;
import cbit.vcell.microscopy.gui.estparamwizard.StyleTable;

public class BatchRunResultsParamTablePanel extends JPanel implements PropertyChangeListener
{
	private JTable table_param;
	private JTable table_stat;
    private BatchRunResultsParameterPanel parent;
    private JLabel lessLable;
    private HyperLinkLabel hypDetail;
    private JScrollPane scrTable_param;
    private JScrollPane scrTable_stat;
    private BatchRunResultsParamTableModel resultsTableModel = null;
    private BatchRunResultsStatTableModel statTableModel = null;
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    private EstParams_OneDiffComponentPanel oneDiffComponentPanel = null;
	private EstParams_TwoDiffComponentPanel twoDiffComponentPanel = null;
   
    public BatchRunResultsParamTablePanel(BatchRunResultsParameterPanel arg_parent) 
    {
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0,0};
        setLayout(gridBagLayout);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel lblConfHeading = new JLabel("Documents");
        lblConfHeading.setFont(new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Less Detail", new HyperLinkListener(), 0);
        hypDetail.setHorizontalAlignment(JLabel.RIGHT);
        
        lessLable = new JLabel("Many VFRAP Docs");
        lessLable.setOpaque(true);
        lessLable.setBackground(new Color(166, 166, 255));
        lessLable.setBorder(BorderFactory.createEmptyBorder(1,5,1,1));

        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.gridx = 0;
        gc1.gridy = 0;
        gc1.weightx = 1.0;
        gc1.anchor = GridBagConstraints.WEST;
        gc1.fill = GridBagConstraints.HORIZONTAL;
        add(lblConfHeading, gc1);

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
        add(lessLable, gc3);
        //create table model
        resultsTableModel = new BatchRunResultsParamTableModel();
        statTableModel = new BatchRunResultsStatTableModel();
        //by default, expend this table
        setDetail(true);
   }



    private class HyperLinkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            boolean isDetail = hypDetail.getText().equals("Detail");
            setDetail(isDetail);
            hypDetail.setText(isDetail? "Less Detail" : "Detail");
        }
    }

    public void setDetail(boolean isDetail) {
        if (isDetail) {
            if (table_stat == null) setupTable_stat();
            if (table_param == null) setupTable_param();
            remove(lessLable);
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridy = 1;
            gc.gridwidth = 2;
            gc.weightx = 1.0;
            gc.fill = GridBagConstraints.HORIZONTAL;
            add(table_stat.getTableHeader(), gc); //table head
            gc.weighty = 1.0;
            gc.gridy = 2;
            gc.fill = GridBagConstraints.BOTH;
            if (table_stat.getModel().getRowCount() > 20) {
                add(scrTable_stat, gc);//table
            }
            else {
                add(table_stat, gc);
            }
            GridBagConstraints gc2 = new GridBagConstraints();
            gc2.gridy = 3;
            gc2.gridwidth = 2;
            gc2.weightx = 1.0;
            gc2.fill = GridBagConstraints.HORIZONTAL;
            add(table_param.getTableHeader(), gc2); //table head
            gc2.weighty = 1.0;
            gc2.gridy = 4;
            gc2.fill = GridBagConstraints.BOTH;
            if (table_param.getModel().getRowCount() > 20) {
                add(scrTable_param, gc2);//table
            }
            else {
                add(table_param, gc2);
            }
        }
        else {
            if (table_param != null) {
                remove(table_param.getTableHeader());
                remove(scrTable_param);
                remove(table_param);
            }
            if (table_stat != null) {
                remove(table_stat.getTableHeader());
                remove(scrTable_stat);
                remove(table_stat);
            }
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridy = 1;
            gc.gridwidth = 2;
            gc.weightx = 1.0;
            gc.fill = GridBagConstraints.HORIZONTAL;
            if(batchRunWorkspace != null)
            {
            	lessLable.setText(batchRunWorkspace.getFrapStudyList().size() + " Documents");
            }
            add(lessLable, gc);
        }
        parent.repaint();
    }

    private void setupTable_param() {

        TableSorter sorter = new TableSorter(resultsTableModel);
        table_param = new StyleTable(sorter);
        table_param.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter.setTableHeader(table_param.getTableHeader());
        
        DefaultCellEditor  resultsEditor = new DefaultCellEditor(new JTextField());
        TableCellRenderer resultsRanderer = new  AnalysisTableRenderer(8); //double precision 8 digits
        for (int i = 0; i < table_param.getColumnCount(); i++) {
        	TableColumn col = table_param.getColumnModel().getColumn(i);
        	col.setPreferredWidth(0);
        	col.setCellRenderer(resultsRanderer);
        	col.setCellEditor(resultsEditor);
        }
        //apply table renderer for name column(override the previous one)
        TableColumn nameCol = table_param.getColumnModel().getColumn(BatchRunResultsParamTableModel.COLUMN_FILE_NAME);
        nameCol.setCellRenderer(new ResultsParamTableRenderer());
        //apply table renderer and table editor for details column(override the previous ones)
        TableColumn detailsCol = table_param.getColumnModel().getColumn(BatchRunResultsParamTableModel.COLUMN_DETAILS);
        detailsCol.setCellRenderer(new ResultsParamTableRenderer());
        ResultsParamTableEditor tableEditor = new ResultsParamTableEditor(table_param);
        tableEditor.addPropertyChangeListener(this);
        detailsCol.setCellEditor(tableEditor);
        scrTable_param = new JScrollPane(table_param);
        scrTable_param.setAutoscrolls(true);
        
    }
    
    private void setupTable_stat() {
        table_stat = new StyleTable();
        table_stat.setAutoCreateColumnsFromModel(false);
        table_stat.setModel(statTableModel);

        DefaultCellEditor statEditor = new DefaultCellEditor(new JTextField());
        TableCellRenderer statRenderer = new  AnalysisTableRenderer(8);//double precision 8 digits
        TableColumn[] columns = new TableColumn[statTableModel.NUM_COLUMNS];
        for (int i = 0; i < statTableModel.getColumnCount(); i++) {

            columns[i] = new TableColumn(i, 0, statRenderer, statEditor);

            table_stat.addColumn(columns[i]);

        }

        scrTable_stat = new JScrollPane(table_stat);
        scrTable_stat.setAutoscrolls(true);
        
    }

    public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
    	this.batchRunWorkspace = batchRunWorkspace;
		resultsTableModel.setBatchRunWorkspace(batchRunWorkspace);
		statTableModel.setBatchRunWorkspace(batchRunWorkspace);
	}
    
    public EstParams_OneDiffComponentPanel getOneDiffComponentPanel() {
    	if(oneDiffComponentPanel == null)
    	{
    		oneDiffComponentPanel = new EstParams_OneDiffComponentPanel();
    		oneDiffComponentPanel.setPreferredSize(new Dimension(980,680));
    		oneDiffComponentPanel.setMaximumSize(new Dimension(980,680));
    		oneDiffComponentPanel.setMinimumSize(new Dimension(980,680));
    		oneDiffComponentPanel.setApplyBatchRunParamButtonVisible(false);
    	}
		return oneDiffComponentPanel;
	}

	public void setOneDiffComponentPanel(EstParams_OneDiffComponentPanel oneDiffComponentPanel) {
		this.oneDiffComponentPanel = oneDiffComponentPanel;
	}
    
	public EstParams_TwoDiffComponentPanel getTwoDiffComponentPanel() {
		if(twoDiffComponentPanel == null)
		{
			twoDiffComponentPanel = new EstParams_TwoDiffComponentPanel();
			twoDiffComponentPanel.setPreferredSize(new Dimension(980,680));
			twoDiffComponentPanel.setMaximumSize(new Dimension(980,680));
			twoDiffComponentPanel.setMinimumSize(new Dimension(980,680));
			twoDiffComponentPanel.setApplyBatchRunParamButtonVisible(false);
		}
		return twoDiffComponentPanel;
	}

	public void setTwoDiffComponentPanel(EstParams_TwoDiffComponentPanel twoDiffComponentPanel) {
		this.twoDiffComponentPanel = twoDiffComponentPanel;
	}

	public void updateTableData()
	{
		resultsTableModel.fireTableDataChanged();
		statTableModel.fireTableDataChanged();
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		
		if(evt.getPropertyName().equals(FRAPBatchRunWorkspace.PROPERTY_CHANGE_BATCHRUN_DETAIL))
		{
			int rowNum = ((Integer)evt.getNewValue()).intValue();
			String fileName = ((File)table_param.getValueAt(rowNum, BatchRunResultsParamTableModel.COLUMN_FILE_NAME)).getAbsolutePath();
			System.out.println("FileName---" + fileName);
			FRAPStudy selectedFrapStudy = batchRunWorkspace.getFRAPStudy(fileName);
			//display estimation result for each selected frapStudy based on model type
			if(batchRunWorkspace.getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT)
			{
				try {
					batchRunWorkspace.getWorkingSingleWorkspace().setFrapStudy(selectedFrapStudy, false, true);
					getOneDiffComponentPanel().setFrapWorkspace(batchRunWorkspace.getWorkingSingleWorkspace());
					getOneDiffComponentPanel().setData(selectedFrapStudy.getFrapOptData(), selectedFrapStudy.getFrapData(),
							                           selectedFrapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters(),
							                           selectedFrapStudy.getFrapData().getImageDataset().getImageTimeStamps(),
							                           selectedFrapStudy.getStartingIndexForRecovery(),
							                           selectedFrapStudy.getSelectedROIsForErrorCalculation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int choice = DialogUtils.showComponentOKCancelDialog(JOptionPane.getFrameForComponent(BatchRunResultsParamTablePanel.this), getOneDiffComponentPanel(), "Estimation Details for "+selectedFrapStudy.getXmlFilename());
				if(choice == JOptionPane.OK_OPTION)
				{
					selectedFrapStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT).setModelParameters(getOneDiffComponentPanel().getCurrentParameters());
					batchRunWorkspace.refreshStatisticsData();
				}
			}
			else if(batchRunWorkspace.getSelectedModel() == FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS)
			{
				try {
					batchRunWorkspace.getWorkingSingleWorkspace().setFrapStudy(selectedFrapStudy, false, true);
					getTwoDiffComponentPanel().setFrapWorkspace(batchRunWorkspace.getWorkingSingleWorkspace());
					getTwoDiffComponentPanel().setData(selectedFrapStudy.getFrapOptData(), selectedFrapStudy.getFrapData(),
							                           selectedFrapStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters(),
							                           selectedFrapStudy.getFrapData().getImageDataset().getImageTimeStamps(),
							                           selectedFrapStudy.getStartingIndexForRecovery(),
							                           selectedFrapStudy.getSelectedROIsForErrorCalculation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int choice = DialogUtils.showComponentOKCancelDialog(JOptionPane.getFrameForComponent(BatchRunResultsParamTablePanel.this), getTwoDiffComponentPanel(), "Estimation Details for "+selectedFrapStudy.getXmlFilename());
				if(choice == JOptionPane.OK_OPTION)
				{
					selectedFrapStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS).setModelParameters(getTwoDiffComponentPanel().getCurrentParameters());
					batchRunWorkspace.refreshStatisticsData();
				}
			}
		}
	}
}

