package cbit.vcell.microscopy.gui.estparamwizard;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;

import java.awt.*;
import java.awt.event.*;

public class MSETablePanel extends JPanel
{

	private StyleTable table;

    private MSEPanel parent;

    private JLabel lessLable;

    private HyperLinkLabel hypDetail;

    private JScrollPane scrTable;
    MSETableModel mseTableModel;

    JPopupMenu popupMenu;

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
        hypDetail = new HyperLinkLabel("Less Detail", new HyperLinkListener(), 0);
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
        //create table model
        mseTableModel = new MSETableModel();
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
        table.setModel(mseTableModel);

        DefaultCellEditor  mseEditor = new DefaultCellEditor(new JTextField());
        TableCellRenderer mseRenderer = new  AnalysisTableRenderer(8);//double precision 8 digits
        TableColumn[] columns = new TableColumn[MSETableModel.NUM_COLUMNS];
        for (int i = 0; i < mseTableModel.getColumnCount(); i++) {
            columns[i] = new TableColumn(i, 0, mseRenderer, mseEditor);
            table.addColumn(columns[i]);
        }
//        table.getTableHeader().addMouseListener(new TableMouseListener());

        scrTable = new JScrollPane(table);
        scrTable.setAutoscrolls(true);
    }

    public void setFrapWorkspace(FRAPWorkspace frapWorkspace)
	{
		mseTableModel.setFrapWorkspace(frapWorkspace);
		lessLable.setText(frapWorkspace.getFrapStudy().getSelectedModels().size() + " Models");
	}
}


    