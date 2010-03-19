package org.vcell.sybil.workers.bpimport;

/*   ImportAcceptWorker  --- by Oliver Ruebenacker, UCHC --- March 2009 to March 2010 2010
 *   Perform Pathway Commons keyword search in the background
 */

import java.util.Set;

import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.models.io.selection.ModelSelector;
import org.vcell.sybil.models.io.selection.ModelSelectorSimple;
import org.vcell.sybil.rdf.smelt.BioPAX2Smelter;
import org.vcell.sybil.util.http.pathwaycommons.search.PCTextModelResponse;
import org.vcell.sybil.util.state.SystemWorker;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class ImportAcceptWorker extends SystemWorker {

	public static interface Client {
		public PCTextModelResponse response();
		public FileManager fileManager();
		public boolean requestsSmelting();
		public Set<Resource> selectedResources();
	}
	
	protected Client client;
	protected FileEvent event;
	
	public ImportAcceptWorker(Client clientNew) { 
		client = clientNew; 
	}

	public Model doConstruct() { 
		Model model = client.response().model();
		if(client.requestsSmelting()) { model = new BioPAX2Smelter().smelt(model); }
		Set<Resource> selectedResources = client.selectedResources();
		// ModelSelector selector = new ModelSelectorOld();
		ModelSelector selector = new ModelSelectorSimple();
		Model modelSelection = selector.createSelection(model, selectedResources);
		event = client.fileManager().importData(client.response().text(), modelSelection, "Imported Data");		
		return modelSelection; 
	}

	public void doFinished() { 
		if(event != null) { client.fileManager().listeners().fileEvent(event); }
	}

	public String getNonSwingTaskName() { return "start processing selected data"; }
	public String getSwingTaskName() { return "finish processing selected data"; }
	
}
