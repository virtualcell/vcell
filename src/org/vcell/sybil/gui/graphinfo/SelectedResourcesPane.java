package org.vcell.sybil.gui.graphinfo;

/*   SelectedResourcesPane  --- by Oliver Ruebenacker, UCHC --- February 2008 to March 2010
 *   Creates a panel to display the selected Resources of a graph
 */

import java.awt.Dimension; 
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.sybil.models.graph.GraphModelSelectionInfo;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SBWrapper;
import org.vcell.sybil.util.table.ResourcesTableModel;

import com.hp.hpl.jena.rdf.model.Resource;

public class SelectedResourcesPane extends JPanel
implements GraphModelSelectionInfo.Listener, ListSelectionListener {

	public static class Table extends JTable {
		private static final long serialVersionUID = 7946719652978679311L;
		public Table(ResourcesTableModel model) { super(model); }
		public ResourcesTableModel getModel() { return (ResourcesTableModel) super.getModel(); }
	}

	private static final long serialVersionUID = -4664538180745479527L;
	
	protected SBBox box;
	protected ResourcesTableModel tableModel = new ResourcesTableModel();
	protected JLabel headerLabel = new JLabel();
	protected JTextArea emptyLabel = new JTextArea();
	protected Table table = new Table(tableModel);
	protected JScrollPane scrollPane = new JScrollPane(table);
	protected boolean isEmpty = true;
	protected GraphModelSelectionInfo dataRelations;
	
	public SelectedResourcesPane(SBBox box, String headerText, String emptyText) {
		this.box = box;
		headerLabel.setText(headerText);
		headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		table.setTableHeader(null);
		table.setDefaultRenderer(SBWrapper.class, new SBWrapperRenderer());
		emptyLabel.setText(emptyText);
		emptyLabel.setEditable(false);
		emptyLabel.setLineWrap(true);
		emptyLabel.setWrapStyleWord(true);
		emptyLabel.setMinimumSize(new Dimension(0, 0));
		emptyLabel.setPreferredSize(new Dimension(0, 0));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(headerLabel);
		add(emptyLabel);
		table.getSelectionModel().addListSelectionListener(this);
		revalidate();
		repaint();
	}
	
	public void setDataRelations(GraphModelSelectionInfo dataRelations) {
		if(dataRelations != this.dataRelations) {
			if(dataRelations != null) { dataRelations.listeners().remove(this); }
			this.dataRelations = dataRelations;			
			if(dataRelations != null) { 
				dataRelations.listeners().add(this); 
			}
			update();
		}
	}
	
	public Table table() { return table; }
	public GraphModelSelectionInfo dataRelations() { return dataRelations; }
	
	public void dataChanged() { update(); }

	public void selectionChanged(Resource resource) {
		if(resource != dataRelations.selectedResource()) { dataRelations.setSelectedResource(resource); }
	}

	public void update() {
		if(dataRelations != null) {
			Set<Resource> resources = dataRelations.resources();
			Set<SBWrapper> sbs = new HashSet<SBWrapper>();
			for(Resource resource : resources) {
				sbs.add(new SBWrapper(box, resource));
			}
			tableModel.setResources(sbs);
			if(table.getSelectionModel().getMinSelectionIndex() == -1) {
				table.getSelectionModel().addSelectionInterval(0, 0);
			}
			if(resources.size() > 0 && isEmpty) { 
				remove(emptyLabel);
				add(scrollPane);
				isEmpty = false;
				revalidate();
				repaint();
			} else if(resources.size() == 0 && !isEmpty){ 
				remove(scrollPane);
				add(emptyLabel); 
				isEmpty = true;
				revalidate();
				repaint();
			}
		} else if(!isEmpty) {
			remove(scrollPane);
			add(emptyLabel);
			isEmpty = true;
			revalidate();
			repaint();
		}
	}

	public void valueChanged(ListSelectionEvent event) {
		Object source = event.getSource();
		if(source == table || source == table.getSelectionModel()) {
			int ind = table.getSelectionModel().getLeadSelectionIndex();
			SBWrapper sbItem = tableModel.getSBItem(ind);
			Resource resource = sbItem != null ? sbItem.resource() : null;
			dataRelations.setSelectedResource(resource);
		}
	}

}
