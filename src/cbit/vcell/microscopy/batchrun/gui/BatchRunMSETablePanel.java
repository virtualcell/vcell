package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.AdvancedTablePanel;
import cbit.vcell.microscopy.gui.estparamwizard.NumericTableCellRenderer;
import cbit.vcell.microscopy.gui.estparamwizard.HyperLinkLabel;
import cbit.vcell.microscopy.gui.estparamwizard.StyleTable;

public class BatchRunMSETablePanel extends AdvancedTablePanel
{
    private BatchRunMSEPanel parent;
    private JLabel modelLable;
    private HyperLinkLabel hypDetail;
    private BatchRunMSETableModel mseTableModel = null;
    private FRAPBatchRunWorkspace batchRunWorkspace = null;

    public BatchRunMSETablePanel(BatchRunMSEPanel arg_parent) 
    {
    	super();
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel headingLabel = new JLabel("Documents under Selected ROIs");
        headingLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Less Details", new HyperLinkListener(), 0);
        hypDetail.setHorizontalAlignment(JLabel.RIGHT);

        modelLable = new JLabel("Documents");
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
        //create table model
        mseTableModel = new BatchRunMSETableModel();
        //by default, expend this table
        if (table == null) setupTable();
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 1;
        gc.gridwidth = 2;
        gc.weightx = 1.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        //By default, if you don't place a table in a scroll pane, the table  header  is not  shown. You need to explicitly display it.
        add(table.getTableHeader(), gc); 
        gc.weighty = 1.0;
        gc.gridy = 2;
        gc.fill = GridBagConstraints.BOTH;
        add(table, gc);
        setDetail(true);
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
            if(table != null)
            {
            	table.getTableHeader().setVisible(true);
            	table.setVisible(true);
            }
        }
        else {
            if (table != null) {
            	table.getTableHeader().setVisible(false);
            	table.setVisible(false);
            }
            if(batchRunWorkspace != null)
            {
            	modelLable.setText(batchRunWorkspace.getFrapStudies().size() + " Documents");
            }
            modelLable.setVisible(true);
        }
        parent.repaint();
    }

    private void setupTable() {
    	
    	TableSorter sorter = new TableSorter(mseTableModel);
        table = new StyleTable(sorter);
        table.setCellSelectionEnabled(true);
        sorter.setTableHeader(table.getTableHeader());

        TableColumn nameCol = table.getColumnModel().getColumn(BatchRunMSETableModel.COLUMN_FILE_NAME);
        nameCol.setCellRenderer(new ResultsParamTableRenderer());
        TableCellRenderer mseRenderer = new  NumericTableCellRenderer(8);//double precision 8 digits
        for (int i = 1; i < table.getColumnCount(); i++) {
        	TableColumn col = table.getColumnModel().getColumn(i);
        	col.setPreferredWidth(0);
        	col.setCellRenderer(mseRenderer);
        }
        
        table.addMouseListener(evtHandler);
    }

    public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
    	this.batchRunWorkspace = batchRunWorkspace;
		mseTableModel.setBatchRunWorkspace(batchRunWorkspace);
	}
    
    public void updateTableData()
    {
    	mseTableModel.fireTableDataChanged();
    }
}
