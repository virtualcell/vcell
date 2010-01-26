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

import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.estparamwizard.HyperLinkLabel;

public class BatchRunMSETablePanel extends JPanel
{
	private /*StyleTable*/JTable table;

    private BatchRunMSEPanel parent;

    private JLabel lessLable;

    private HyperLinkLabel hypDetail;

    private JScrollPane scrTable;
    
//    MSETableModel mseTableModel;

    float[] prefColumnWidth = new float[]{0.15f, 0.15f, 0.15f, 0.15f, 0.15f, 0.15f, 0.15f, 0.15f, 0.15f, 0.15f, 0.15f};

//    TableColumn[] columns = new TableColumn[MSETableModel.NUM_COLUMNS];

    public BatchRunMSETablePanel(BatchRunMSEPanel arg_parent) 
    {
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
//        gridBagLayout.columnWidths = new int[] {0,0};
        setLayout(gridBagLayout);
//        setBackground(Color.white);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel headingLabel = new JLabel("Models under Selected ROIs");
        headingLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Detail", new HyperLinkListener(), 0);
        hypDetail.setHorizontalAlignment(JLabel.RIGHT);
//
        lessLable = new JLabel("3 Models");
        lessLable.setOpaque(true);
        lessLable.setBackground(new Color(166, 166, 255));
        lessLable.setBorder(BorderFactory.createEmptyBorder(1,5,1,1));

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
        add(lessLable, gc3);
        //create table model
//        mseTableModel = new MSETableModel();
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
//        table.setModel(mseTableModel);
//
//        DefaultCellEditor  mseEditor = new DefaultCellEditor(new JTextField());
//        TableCellRenderer mseRenderer = new  AnalysisTableRenderer(8);//double precision 8 digits
//        for (int i = 0; i < mseTableModel.getColumnCount(); i++) {
//
//            int w = (int) (prefColumnWidth[i]);
//            columns[i] = new TableColumn(i, w, mseRenderer, mseEditor);
//
//            table.addColumn(columns[i]);
//
//        }
//        table.getTableHeader().addMouseListener(new TableMouseListener());

//        scrTable = new JScrollPane(table);
//        scrTable.setAutoscrolls(true);
    	String col[] = {"Batch Files","Bleached", "Ring1", "Ring2",
		        "Ring3", "Ring4", "Ring5",
		        "Ring6", "Ring7", "Ring8", "Sum of Error"};
		String data[][] = {{"batch file1","0","0","0","0","5.8e-4","0","0","0","0","5.8e-4"},
					 	   {"batch file2","0","0","0","0","6.2e-4","0","0","0","0","6.2e-4"},
					 	   {"batch file3","0","0","0","0","6.0e-4","0","0","0","0","6.0e-4"}};
		table = new JTable(data,col);
    }

    public void setFrapWorkspace(FRAPBatchRunWorkspace batchRunWorkspace)
	{
//		mseTableModel.setFrapWorkspace(frapWorkspace);
//		lessLable.setText(frapWorkspace.getFrapStudy().getSelectedModels().size() + " Models");
	}
}
