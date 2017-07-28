package org.vcell;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

public class ListPanel<T> extends JPanel {
	private static final long serialVersionUID = -3212611169866383870L;
	
	private JList<T> list;
	
	public ListPanel(List<T> items) {
        setLayout(new BorderLayout());
        list = new JList<>();
        list.setModel(getListModel(items));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);
    }
	
	public void updateList(List<T> items) {
        list.setModel(getListModel(items));
    }

    public T getSelectedItem() {
        return list.getSelectedValue();
    }
    
    public JList<T> getList() {
    	return list;
    }
    
	private ListModel<T> getListModel(List<T> items) {
        DefaultListModel<T> listModel = new DefaultListModel<>();
        for (T item : items) {
            listModel.addElement(item);
        }
        return listModel;
    }
}
