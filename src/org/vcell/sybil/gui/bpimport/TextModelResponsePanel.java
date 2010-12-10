package org.vcell.sybil.gui.bpimport;

/*   TextResponsePanel  --- by Oliver Ruebenacker, UCHC --- March 2009 to January 2010
 *   Panel to display the response to one request by simple conversion to String
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.SBBoxFactory;
import org.vcell.sybil.util.http.pathwaycommons.search.PCTextModelResponse;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class TextModelResponsePanel extends ResponsePanel {
	
	private static final long serialVersionUID = 5569708707128075620L;

	protected EntitySelectionTable table;
	private EntitySelectionTableModel tableModel = null;
	
	public TextModelResponsePanel(PCTextModelResponse responseNew) {
		super(responseNew);
		String responseText = responseNew != null ? responseNew.text() : "null";
		JTextArea textArea = new JTextArea();
		textArea.setText(responseText);
		setLayout(new BorderLayout());
		JTabbedPane tabPanel = new JTabbedPane();
		add(tabPanel);
		Model model = response().model();
		SBBox box = SBBoxFactory.create(model);
		table = new EntitySelectionTable(box);
		// wei's code
		// table filtering
		tableModel = new EntitySelectionTableModel(box);
		table.setModel(tableModel);
//		final TableRowSorter<EntitySelectionTableModel> sorter = 
//				new TableRowSorter<EntitySelectionTableModel> (myModel);
//		table.setRowSorter(sorter);
		JPanel selPanel = new JPanel(new BorderLayout());
		JScrollPane selTable = new JScrollPane(table);
		selPanel.add(selTable, BorderLayout.CENTER);
//		JPanel filPanel = new JPanel(new BorderLayout());
//		JLabel label = new JLabel("        Filter ");
//		filPanel.add(label, BorderLayout.WEST);
//		final JTextField filterText = new JTextField();
//		filPanel.add(filterText, BorderLayout.CENTER);
		
//		JButton button = new JButton("Filter");
//		button.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e){
//				String text = filterText.getText();
//				if(text.length() == 0){
//					sorter.setRowFilter(null);
					//System.out.println("no words to filter out");
//				}else{
//					sorter.setRowFilter(RowFilter.regexFilter(text));
					//System.out.println("filtering");
//				}
//			}
//		});
//		JPanel butPanel = new JPanel(new BorderLayout());
//		butPanel.add(button, BorderLayout.WEST);
//		JButton undo = new JButton("Undo Filtering");
//		undo.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e){
//				sorter.setSortKeys(null);
//				sorter.setRowFilter(null);
//			}
//		});
//		butPanel.add(undo, BorderLayout.EAST);
//		filPanel.add(butPanel, BorderLayout.EAST);
//		selPanel.add(filPanel, BorderLayout.SOUTH);
		tabPanel.addTab("Selection", selPanel);
// wei		tabPanel.addTab("Selection", selTable);
		//done
		tabPanel.addTab("Source", new JScrollPane(textArea));		
	}
	
	public PCTextModelResponse response() { return (PCTextModelResponse) super.response(); }
	public Set<Resource> selectedEntities() { 
		Set<Resource> selectedEntities = new HashSet<Resource>();
		for (int i = 0; i < table.getRowCount(); i ++) {
			EntitySelectionTableRow entitySelectionTableRow = tableModel.getValueAt(i);
			if(entitySelectionTableRow.selected()) { 
				selectedEntities.add(entitySelectionTableRow.thing.resource()); 
			}
		}
		return selectedEntities;
	}
	public boolean requestsSmelting() { 
		return true;
	}
	
}