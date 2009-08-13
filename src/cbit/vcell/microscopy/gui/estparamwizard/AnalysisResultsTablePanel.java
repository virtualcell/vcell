package cbit.vcell.microscopy.gui.estparamwizard;



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import cbit.vcell.microscopy.gui.VirtualFrapLoader;

import java.awt.*;
import java.awt.GridBagConstraints;
import java.awt.event.*;

/**
 * Species information panel
 */
public class AnalysisResultsTablePanel extends JPanel /*implements ActionListener*/ 
{
    private StyleTable table;

    private AnalysisResultsPanel parent;

    private JLabel lessLable;

    private HyperLinkLabel hypDetail;

    private JScrollPane scrTable;

    JPopupMenu popupMenu;

    final String[] columnTitle = {"Parameter",  "Diff_1", "Diff_2", "DB"};
    float[] prefColumnWidth = new float[]{0.25f, 0.5f, 0.5f, 0.15f};

    TableColumn[] columns = new TableColumn[columnTitle.length];
    String[][] fakeTable = new String[][]{{"Primary Diff. Rate", "5.149", "5.387", "5.386"},
    		                            {"Primary Fraction","0.952","0.92","0.925"},
    		                            {"Secondary Diff. Rate","0","0.75","0.755"},
    		                            {"Secondary Fraction","0","0.04","0.04"},
    		                            {"Bleach Monitor Rate","5.8e-4","6.2e-4","6.1e-4"},
    		                            {"Reaction on Rate","0","0","4.49e-4"},
    		                            {"Reaction off Rate","0","0","0.0103"},
    		                            {"Immobile Fraction","0.048","0.034","0.0335"},
    		                            {"Description","Diffusion with one diffusing component","Diffusion with two diffusing components","Diffusion plus binding reaction"}};

    public AnalysisResultsTablePanel(AnalysisResultsPanel arg_parent/*may need to pass in frapstudy as parameter*/) 
    {
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0,0};
        setLayout(gridBagLayout);
//        setBackground(Color.white);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel lblConfHeading = new JLabel("Models");
        lblConfHeading.setFont(/*((Font)VirtualFrapLoader.loadProperty("report.table.subheading.font"))*/new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Less Detail", new HyperLinkListener(), 0);
        hypDetail.setHorizontalAlignment(JLabel.RIGHT);
        
        lessLable = new JLabel("3 Models");
        lessLable.setOpaque(true);
        lessLable.setBackground(/*((Color)VirtualFrapLoader.loadProperty("report.table.color"))*/new Color(166, 166, 255));
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
        table = new StyleTable();
        table.setAutoCreateColumnsFromModel(false);
        AnalysisTable anaTable = new AnalysisTable();
        table.setModel(anaTable);
//        StyleTableEditor errEditor = new StyleTableEditor();
//        errEditor.setErrorListener(this);
//        DefaultCellEditor  txtEditor = new DefaultCellEditor(errEditor);
        DefaultCellEditor  txtEditor = new DefaultCellEditor(new JTextField());
        TableCellRenderer txtRenderer =
                new  PlainTableCellRenderer(new Font("Arial", Font.PLAIN, 11));
        for (int i = 0; i < anaTable.getColumnCount(); i++) {
            TableCellRenderer renderer = null;
            DefaultCellEditor editor;
            renderer = txtRenderer;
            editor = txtEditor;
            
            int w = (int) (prefColumnWidth[i]);
            columns[i] = new TableColumn(i, w, renderer, editor);
            table.addColumn(columns[i]);
            if (i > 4) table.removeColumn(columns[i]);
        }
//        table.getTableHeader().addMouseListener(new TableMouseListener());

        scrTable = new JScrollPane(table);
    }



    private class AnalysisTable extends AbstractTableModel {


        public AnalysisTable() {

        }

        public int getColumnCount() {
            return columnTitle.length;
        }

        public int getRowCount() {
           return 9;
        }

        public int getAligment(int columnIndex) {
           return JLabel.CENTER;
        }

        public Object getValueAt(int rowIndex, int columnIndex) 
        {
        	if (fakeTable != null) 
        	{
        		return fakeTable[rowIndex][columnIndex];
        	}
        	return null;
        }

        public Class getColumnClass(int c) {
            return fakeTable[0][c].getClass();
        }

        public String getColumnName(int column) {
            return columnTitle[column];
        }

        public boolean isCellEditable(int rowIndex,int columnIndex) 
        {
            return false;
        }

         public void setValueAt(Object aValue,int rowIndex, int columnIndex) 
         {
         }
    }

    /*private class TableMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                popupMenu.show(e.getComponent(),
                           e.getX(), e.getY());
            }

        }

    }

    public void modelChanged() {
        lessLable.setText("3 Models");
    }

	public void error(TransformerException exception)
			throws TransformerException {
		// TODO Auto-generated method stub
		
	}

	public void fatalError(TransformerException exception)
			throws TransformerException {
		// TODO Auto-generated method stub
		
	}

	public void warning(TransformerException exception)
			throws TransformerException {
		// TODO Auto-generated method stub
		
	}*/

}

	
	
