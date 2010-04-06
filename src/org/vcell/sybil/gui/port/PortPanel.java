package org.vcell.sybil.gui.port;

/*   PortPanel  --- by Oliver Ruebenacker, UCHC --- June 2008 to March 2010
 *   An extension of JPanel to configure and launch port
 */

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.vcell.sybil.gui.dialog.ExportDialog;
import org.vcell.sybil.models.bpimport.PortStage;
import org.vcell.sybil.models.bpimport.table.CompartmentTableModel;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.models.views.SBView.ExporterList;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.workers.port.StageBioModelWorker;
import org.vcell.sybil.workers.port.StageModelsWorker;
import org.vcell.sybil.workers.port.StageSBMLWorker;
import org.vcell.sybil.workers.port.PortWorker.Result;
import org.vcell.sybil.workers.port.PortWorker.ResultAcceptor;

import com.hp.hpl.jena.rdf.model.Resource;

public class PortPanel extends JPanel implements ResultAcceptor {

	public static class PortTarget {};
	
	public static final PortTarget targetSBML = new PortTarget();
	public static final PortTarget targetBioModel = new PortTarget();
	public static final PortTarget targetDefault = targetSBML;
	
	private static final long serialVersionUID = 4425768125564266624L;
	protected JTabbedPane tabPane = new JTabbedPane();
	protected ClassesPanel classesPanel;
	protected ProcessPanel processPanel;
	protected CompartmentPanel compartmentPanel;
	
	protected SBBox.MutableSystemModel systemModel;

	protected FileManager fileManager;
	
	protected PortTarget portTarget = targetDefault;
	
	public PortPanel(FileManager fileMan) {
		this.fileManager = fileMan;
		classesPanel = new ClassesPanel(fileMan.view(), this);
		processPanel = new ProcessPanel(fileMan.view(), this);
		compartmentPanel = new CompartmentPanel(this, fileMan);
		tabPane.addTab("Conversion Criteria", classesPanel);
		tabPane.addTab("Reactions and Participants", processPanel);
		tabPane.addTab("Compartments", compartmentPanel);
		setLayout(new BorderLayout());
		add(tabPane);
		revalidate();
	}

	public void update(SBWorkView view) { 
		setSelectedComp(classesPanel);
	}

	public void setSelectedComp(JComponent comp) { tabPane.setSelectedComponent(comp); }

	public FileManager fileMan() { return fileManager; }
	public SBWorkView view() { return fileManager.view(); }

	public void setPortTarget(PortTarget portTarget) { this.portTarget = portTarget; }

	public void accept(Result result) {
		if(result != null) {
			PortStage id = result.stage();
			if(id == PortStage.stageInit) {
				processPanel.setTableModel(view().tableModel());
				setSelectedComp(processPanel);		
			} else if(id == PortStage.stageProcesses) {
				compartmentPanel.setTableModel(
						new CompartmentTableModel(view().box()));
				setSelectedComp(compartmentPanel);
			} else if(id == PortStage.stageCompartments) {
				Resource systMod = view().box().getData()
				.createResource("http://www.sybil.org/systemModel");
				systemModel = view().box().factories().systemModel().create(systMod);
				new StageModelsWorker(view(), this).run(this);
			} else if(id == PortStage.stageModels) {
				if(portTarget.equals(targetSBML)) {
					new StageSBMLWorker(view(), this).run(this);
				} else if(portTarget.equals(targetBioModel)) {
					if(fileManager.bioModel() != null) { 
						new StageBioModelWorker(view(), this).run(this); 
					}
					else { CatchUtil.handle(new Exception("Don't have a BioModel")); }
				} else {
					CatchUtil.handle(new Exception("Unknown target " + portTarget));				
				}
			} else if(id == PortStage.stageSBML) {
				ExporterList exporterList = result.view().exporterList();
				ExportDialog.create(this, exporterList.exporters(), 
						exporterList.exporterDefault()).showExportDialog();
			} else if(id == PortStage.stageBioModel) {
				// Nothing needs to be done here
			} else {
				CatchUtil.handle(new Exception("Unknown stage ID " + id));
			}
		}
	}

}
