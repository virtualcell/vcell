package cbit.vcell.microscopy.gui.estparamwizard;



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.gui.AdvancedTablePanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.ExpressionException;
import cbit.gui.ScopedExpression;

import java.awt.*;
import java.awt.event.*;

/**
 * Species information panel
 */
public class AnalysisResultsTablePanel extends AdvancedTablePanel 
{
    private AnalysisTableModel anaTableModel;
    private AnalysisResultsPanel parent;
    private JLabel modelLable;
    private HyperLinkLabel hypDetail;
    private JScrollPane scrTable;

    public AnalysisResultsTablePanel(AnalysisResultsPanel arg_parent/*may need to pass in frapstudy as parameter*/) 
    {
    	super();
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0,0};
        setLayout(gridBagLayout);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel lblConfHeading = new JLabel("Models");
        lblConfHeading.setFont(new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Less Details", new HyperLinkListener(), 0);
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
        add(modelLable, gc3);
        //create table model
        anaTableModel = new AnalysisTableModel();
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
            remove(modelLable);
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
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridy = 1;
            gc.gridwidth = 2;
            gc.weightx = 1.0;
            gc.fill = GridBagConstraints.HORIZONTAL;
            
            add(modelLable, gc);
        }
        parent.repaint();
    }

    private void setupTable() {
        table = new StyleTable();
        table.setCellSelectionEnabled(true);
        table.setAutoCreateColumnsFromModel(false);
        table.setModel(anaTableModel);

        TableCellRenderer anaRenderer = new  NumericTableCellRenderer(8); //double precision 8 digits
        TableColumn[] columns = new TableColumn[AnalysisTableModel.NUM_COLUMNS];
        for (int i = 0; i < anaTableModel.getColumnCount(); i++) {
            columns[i] = new TableColumn(i, 0, anaRenderer, null);
            table.addColumn(columns[i]);
        }
        table.addMouseListener(evtHandler);
        scrTable = new JScrollPane(table);
        scrTable.setAutoscrolls(true);
    }

    public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace)
	{
		anaTableModel.setFrapWorkspace(frapWorkspace);
		modelLable.setText(frapWorkspace.getWorkingFrapStudy().getSelectedModels().size() + " Models");
	}
}

	
	
