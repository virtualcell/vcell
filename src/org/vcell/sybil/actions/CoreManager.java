package org.vcell.sybil.actions;

/*   CoreManager  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Provides an environment in which the models live
 */

import org.vcell.sybil.util.ui.UIComponent;
import org.vcell.sybil.util.ui.UIFrameSpace;
import org.vcell.sybil.util.ui.UIGraphSpace;
import org.vcell.sybil.util.ui.UIImportSpace;
import org.vcell.sybil.util.ui.UIPortSpace;
import org.vcell.sybil.util.ui.UIScrollSpace;
import org.vcell.sybil.util.ui.UITabbedSpace;
import org.vcell.sybil.util.ui.UITextSpace;
import org.vcell.sybil.util.ui.UserInterface;
import org.vcell.sybil.workers.files.FileInheritedWorker;
import org.vcell.sybil.workers.files.FileNewWorker;

import org.vcell.sybil.actions.graph.GraphManager;
import org.vcell.sybil.models.graph.GraphModel;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.models.updater.EvaluatorUpdater;
import org.vcell.sybil.models.updater.PortSpaceUpdater;
import org.vcell.sybil.models.updater.TextSpaceUpdater;
import org.vcell.sybil.models.updater.UIGlobalNameUpdater;

import cbit.vcell.biomodel.BioModel;

public class CoreManager {
	
	public static class DataDisplayer implements FileEvent.Listener {

		protected UITabbedSpace tabSpace;
		protected UIComponent dataComp;
		
		public DataDisplayer(UITabbedSpace tabSpace, UIComponent dataComp) {
			this.tabSpace = tabSpace;
			this.dataComp = dataComp;
		}
		
		public void displayData() { tabSpace.selectTab(dataComp); }
		public void fileEvent(FileEvent event) {  if(event.thereIsNewData()) { displayData(); } }
		
	}

	protected FileManager fileManager;
	protected UserInterface ui;
	protected UITabbedSpace tabSpace;
	protected UIImportSpace importSpace;
	protected UITabbedSpace tabSpaceData;
	protected UITabbedSpace tabSpaceAdvanced;
	protected UITabbedSpace tabSpaceSchemas;
	protected GraphManager graphManager;
	protected UIPortSpace portSpace;
	protected EvaluatorUpdater evaluatorUpdater;
 	
    public CoreManager(BioModel bioModel) {
		fileManager = new FileManager(bioModel);
		fileManager.listeners().add(new EvaluatorUpdater(fileManager.evaluator()));
	}

	protected void setUI(UserInterface newUI, GraphManager modSysGraphNew) {
		ui = newUI;
		graphManager = modSysGraphNew;
		tabSpace = ui().createTabbedSpace();
		importSpace = ui().createImportSpace();
		tabSpaceData = ui().createTabbedSpace();
		DataDisplayer dataDisplayer = new DataDisplayer(tabSpace, tabSpaceData);
		fileManager.listeners().add(dataDisplayer);
		tabSpaceSchemas = ui().createTabbedSpace();
		tabSpaceAdvanced = ui().createTabbedSpace();
		tabSpace.addTab("Import", importSpace);
		tabSpace.addTab("Data", tabSpaceData);
		portSpace = ui().createPortSpace();
		fileManager.evaluator().listeners().add(new PortSpaceUpdater(portSpace));
		tabSpace.addTab("BioPAX to SBML Conversion", portSpace);
		tabSpace.addTab("Advanced", tabSpaceAdvanced);
		tabSpaceAdvanced.addTab("Schemas", tabSpaceSchemas);
		tabSpaceData.addTab("Graph", graphManager.graphSpace());
		UITextSpace sourceSpace = ui.createTextSpace();
		fileManager.listeners().add(new TextSpaceUpdater(sourceSpace));
		UIScrollSpace sourceScrollSpace = ui.createScrollSpace(sourceSpace);
		tabSpaceData.addTab("BioPAX Source", sourceScrollSpace);		
		fileManager.evaluator().listeners().add(graphManager.graphModelUpdater());
		ui().frameSpace().add(tabSpace);
		fileManager.listeners().add(new UIGlobalNameUpdater(ui));
	}
	
	public void postInit() {
		// TODO how do we know data is inherited?
		if(fileManager.box().data() == null || fileManager.box().data().size() < 10) { 
			new FileNewWorker(fileManager).run(RequesterProvider.requester(this)); 
		}
		else { 
			fileManager.box().data().write(System.out, "N3");
			new FileInheritedWorker(fileManager).run(RequesterProvider.requester(this)); 
		}
		importSpace.requestFocusForThis();
	}

	public UserInterface ui() { return ui; }
	public FileManager fileManager() { return fileManager; }

	public GraphModel.Listener graph() { return graphManager.graphSpace().graph(); }
	public UIPortSpace portSpace() { return portSpace; }	
	public UIFrameSpace frameSpace() { return ui().frameSpace(); }
	public UIGraphSpace graphSpace() { return graphManager.graphSpace(); }
	public UIImportSpace importSpace() { return importSpace; }	

} // end class 
