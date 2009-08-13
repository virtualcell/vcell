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

public class MSETablePanel extends JPanel
{

	private StyleTable table;

    private MSEPanel parent;

    private JLabel lessLable;

    private HyperLinkLabel hypDetail;

    private JScrollPane scrTable;

    JPopupMenu popupMenu;

    final String[] columnTitle = {"Models",  "Bleached", "Ring_1", "Ring_4", "Ring_7", "Sum of Error"};
    float[] prefColumnWidth = new float[]{0.25f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f};

    TableColumn[] columns = new TableColumn[columnTitle.length];
    String[][] fakeTable = new String[][]{{"Diff_1", "0.0092", "0.01055", "0.0067", "0.00138", "0.0279"},
    		                            {"Diff_2","0.00923","0.010556","0.006777", "0.001388", "0.027951"},
    		                            {"DB","0.00923","0.010556","0.006777", "0.001388", "0.027951"}};

    public MSETablePanel(MSEPanel arg_parent/*may need to pass in frapstudy as parameter*/) 
    {
    	this.parent = arg_parent;
        final GridBagLayout gridBagLayout = new GridBagLayout();
//        gridBagLayout.columnWidths = new int[] {0,0};
        setLayout(gridBagLayout);
//        setBackground(Color.white);
        setBorder(new EmptyBorder(5, 0, 10, 0));

        JLabel headingLabel = new JLabel("Models under Selected ROIs");
        headingLabel.setFont(/*((Font)VirtualFrapLoader.loadProperty("report.table.subheading.font"))*/new Font("Tahoma", Font.BOLD, 11));
        hypDetail = new HyperLinkLabel("Detail", new HyperLinkListener(), 0);
        hypDetail.setHorizontalAlignment(JLabel.RIGHT);
//
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
        MSETable errTable = new MSETable();
        table.setModel(errTable);
//        StyleTableEditor errEditor = new StyleTableEditor();
//        errEditor.setErrorListener(this);
//        DefaultCellEditor  txtEditor = new DefaultCellEditor(errEditor);
        DefaultCellEditor  txtEditor = new DefaultCellEditor(new JTextField());
        TableCellRenderer txtRenderer = new  PlainTableCellRenderer(new Font("Arial", Font.PLAIN, 11));
        for (int i = 0; i < errTable.getColumnCount(); i++) {
            TableCellRenderer renderer;
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



    private class MSETable extends AbstractTableModel {


        public MSETable() {

        }

        public int getColumnCount() {
            return columnTitle.length;
        }

        public int getRowCount() {
           return 3;
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
}


    