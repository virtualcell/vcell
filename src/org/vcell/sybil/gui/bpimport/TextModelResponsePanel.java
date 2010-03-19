package org.vcell.sybil.gui.bpimport;

/*   TextResponsePanel  --- by Oliver Ruebenacker, UCHC --- March 2009 to January 2010
 *   Panel to display the response to one request by simple conversion to String
 */

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.SBBoxFactory;
import org.vcell.sybil.util.http.pathwaycommons.search.PCTextModelResponse;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class TextModelResponsePanel extends ResponsePanel {
	
	private static final long serialVersionUID = 5569708707128075620L;

	protected EntitySelectionTable table;
	
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
		tabPanel.addTab("Selection", new JScrollPane(table));
		tabPanel.addTab("Source", new JScrollPane(textArea));		
	}
	
	public PCTextModelResponse response() { return (PCTextModelResponse) super.response(); }
	public Set<Resource> selectedEntities() { return table.getModel().selectedEntities(); }
	public boolean requestsSmelting() { 
		return true;
	}
	
}