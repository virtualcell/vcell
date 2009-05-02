package cbit.vcell.model.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.vcell.util.gui.JTableFixed;

import cbit.vcell.model.Model;

/**
 * The panel is used to accommodate the TransformMassActionTable, which 
 * intuitively shows the possible reactions(whose rate laws are not
 * mass actions) transform to Mass Actions.
 * @author Tracy LI
 */
public class TransformMassActionPanel extends JPanel implements ActionListener {
	
	private Model fieldModel = null;
	private JScrollPane tableScrollPane = null;
	private TransformMassActionTableModel transMATableModel = null;
	private JTableFixed transMATable = null;	
	
	public TransformMassActionPanel()
	{
		initialize();
		initConnections();
	}// end of constructor

	public void actionPerformed(ActionEvent arg0) {
		
		
	}
	
	private void initialize() {
		//set panel name, layout and size
		setName("TransformMassActionPanel");
		setLayout(new java.awt.BorderLayout());
		setPreferredSize(new Dimension(700, 250));
		//setSize(800, 250);
		//set border
		EtchedBorder eb=new EtchedBorder(EtchedBorder.LOWERED);
	    setBorder(eb);
	    //add some invisibel labels to make the dialog looks nicer.
	    add(new JLabel("List of all the reactions :"), BorderLayout.NORTH);
	    add(new JLabel(" "), BorderLayout.WEST);
	    add(new JLabel(" "), BorderLayout.EAST);
		//add table scrollPane
		getTableScrollPane().setViewportView(getTable());
		getTableScrollPane().setColumnHeaderView(transMATable.getTableHeader());
		getTableScrollPane().getViewport().setBackingStoreEnabled(true);
		add(getTableScrollPane(), BorderLayout.CENTER);
	}
	
	private void initConnections()
	{
		
	}
	
	private JScrollPane getTableScrollPane()
	{
		if(tableScrollPane == null)
		{
			tableScrollPane = new javax.swing.JScrollPane();
			tableScrollPane.setName("transformMATableScrollPane");
			tableScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			tableScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		}
		return tableScrollPane;
	}
	
	private JTableFixed getTable()
	{
		if(transMATable == null)
		{
			transMATable = new org.vcell.util.gui.JTableFixed()
			{
				//Implement table cell tool tips.
			    public String getToolTipText(MouseEvent e) {
			      String tip = null;
			      java.awt.Point p = e.getPoint();
			      int rowIndex = rowAtPoint(p);
			      int colIndex = columnAtPoint(p);
			      int realColumnIndex = convertColumnIndexToModel(colIndex);

			      if (realColumnIndex == TransformMassActionTableModel.COLUMN_REMARK) { //Remark column
			        tip = getValueAt(rowIndex, colIndex)+"";
			      } 
			      else {
			        //You can omit this part if you know you don't
			        //have any renderers that supply their own tool
			        //tips.
			        tip = super.getToolTipText(e);
			      }
			      return tip;
			    }
			};
			transMATable.setName("ScrollPaneTable1");
			transMATable.setBounds(0, 0, 450, 400);
			//add table model
			transMATable.setModel(getTableModel());
			transMATable.createDefaultColumnsFromModel();
			
			// set cell renderer
			for(int i=0; i<transMATable.getModel().getColumnCount(); i++)
			{
				TableColumn column=transMATable.getColumnModel().getColumn(i);
				column.setCellRenderer(new TransformMassActionTableRenderer(transMATable.getDefaultRenderer(Boolean.class)));
			}
		}
		return transMATable;
	}
	
	private TransformMassActionTableModel getTableModel()
	{
		if(transMATableModel == null)
		{
			transMATableModel = new TransformMassActionTableModel();
			return transMATableModel;
		}
		return transMATableModel;
	}
	
	public Model getModel() {
		return fieldModel;
	}

	public void setModel(Model argModel) {
		fieldModel = argModel;
		getTableModel().setModel(argModel);
	}
	
	public void saveTransformedReactions() throws Exception
	{
		try
		{
			getTableModel().saveTransformedReactions();
		}catch(Exception e)
		{
			throw new Exception(e.getMessage());
		}
	}
	
}
