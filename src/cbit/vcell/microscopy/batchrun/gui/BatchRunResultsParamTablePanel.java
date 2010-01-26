package cbit.vcell.microscopy.batchrun.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.estparamwizard.HyperLinkLabel;
import cbit.vcell.microscopy.gui.estparamwizard.StyleTable;

public class BatchRunResultsParamTablePanel extends JPanel
{
	private /*StyleTable*/JTable table;
    private BatchRunResultsParameterPanel parent;

    private JLabel lessLable;

    private HyperLinkLabel hypDetail;

    private JScrollPane scrTable;

    float[] prefColumnWidth = new float[]{0.25f, 0.5f, 0.5f, 0.15f};

//    TableColumn[] columns = new TableColumn[AnalysisTableModel.NUM_COLUMNS];


    public BatchRunResultsParamTablePanel(BatchRunResultsParameterPanel arg_parent) 
    {
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0,0};
        setLayout(gridBagLayout);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel lblConfHeading = new JLabel("Models");
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
//        anaTableModel = new AnalysisTableModel();
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
            if (table == null) setupTable();
            remove(lessLable);
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridy = 1;
            gc.gridwidth = 2;
            gc.weightx = 1.0;
            gc.fill = GridBagConstraints.HORIZONTAL;
            add(table.getTableHeader(), gc); // absurd to put head and body seperately
            gc.weighty = 1.0;
            gc.gridy = 2;
            gc.fill = GridBagConstraints.BOTH;
            if (table.getModel().getRowCount() > 20) {
                add(scrTable, gc);
            }
            else {
                add(table, gc);
            }
        }
        else {
            if (table != null) {
                remove(table.getTableHeader());
                remove(scrTable);
                remove(table);
            }
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridy = 1;
            gc.gridwidth = 2;
            gc.weightx = 1.0;
            gc.fill = GridBagConstraints.HORIZONTAL;
            add(lessLable, gc);
        }
        parent.repaint();
    }

    private void setupTable() {
//        table = new StyleTable();
//        table.setAutoCreateColumnsFromModel(false);
//        table.setModel(anaTableModel);
//
//        DefaultCellEditor  anaEditor = new DefaultCellEditor(new JTextField());
//        TableCellRenderer anaRenderer = new  AnalysisTableRenderer(8); //double precision 8 digits
//        for (int i = 0; i < anaTableModel.getColumnCount(); i++) {
//            int w = (int) (prefColumnWidth[i]);
//            columns[i] = new TableColumn(i, w, anaRenderer, anaEditor);
//            table.addColumn(columns[i]);
//            if (i > 4) table.removeColumn(columns[i]);
//        }
//
//        scrTable = new JScrollPane(table);
    	String col[] = {"Batch Files","Primary Diff. Rate", "Primary Mob. Frac.", "Secondary Diff. Rate",
    			        "Secondary Mob. Frac.", "Bleach Moni. Rate", "Reaction On Rate",
    			        "Reaction Off Rate", "Immobile Fraction", "Details Link"};
    	String data[][] = {{"batch file1","5.3","0.95","0","0","5.8e-4","0","0","0.05","Details"},
    				 	   {"batch file2","5.2","0.94","0","0","6.2e-4","0","0","0.06","Details"},
    				 	   {"batch file3","5.4","0.96","0","0","6.0e-4","0","0","0.04","Details"},
    				 	   {"Average","5.3","0.95","0","0","5.8e-4","0","0","0.05","Details"}};
    	table = new JTable(data,col);
    }

    public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
//		anaTableModel.setFrapWorkspace(frapWorkspace);
//		lessLable.setText(frapWorkspace.getFrapStudy().getSelectedModels().size() + " Models");
	}
}

