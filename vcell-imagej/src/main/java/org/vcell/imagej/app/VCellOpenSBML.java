package org.vcell.imagej.app;

import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.scijava.ItemIO;
import org.scijava.app.AppService;
import org.scijava.command.Command;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.DefaultDisplay;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.display.TextDisplay;
import org.scijava.log.LogService;
import org.scijava.module.DefaultMutableModule;
import org.scijava.module.DefaultMutableModuleInfo;
import org.scijava.module.DefaultMutableModuleItem;
import org.scijava.module.MutableModuleInfo;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.SciJavaPlugin;
import org.scijava.text.TextService;
import org.scijava.ui.UIService;
import org.scijava.ui.UserInterface;
import org.scijava.ui.swing.viewer.SwingDisplayPanel;
import org.scijava.ui.swing.viewer.SwingDisplayWindow;
import org.scijava.ui.swing.widget.SwingInputHarvester;
import org.scijava.ui.swing.widget.SwingInputPanel;
import org.scijava.ui.viewer.AbstractDisplayViewer;
import org.scijava.ui.viewer.DisplayWindow;
import org.vcell.imagej.app.RunVCellSimFromSBML.Sim;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImageJ;

@Plugin(type = Command.class, menuPath = "Plugins>VCell>Open SBML xml",initializer = "initParameters")
public class VCellOpenSBML implements Command {

	@Parameter()
	private File sbmlModelFile;

	@Parameter 
	private TextService textService; 

	@Parameter 
	private DisplayService dispService; 

	@Parameter
	private AppService appService;

	@Parameter
	private UIService uiService;

	@Parameter 
	private LogService logService; 

	@Parameter(type = ItemIO.OUTPUT, label="sbml") 
	private String sbmlText; 

//	private static String[][] DEFAULT_SIM_PARAMS = new String[][] {{"SimDurationTime",".2"},{"SimStepTime",".1"},{"SimResults",Sim.SIMULATED_FRAP}};
	@Override
	public void run() {
		try {
    		SBMLDocument sbmlDocument = null;
			sbmlDocument = SBMLReader.read(sbmlModelFile);
			for (int i = 0; i < sbmlDocument.getModel().getCompartmentCount(); i++) {
				Compartment compartment = sbmlDocument.getModel().getCompartment(i);
				System.out.println(compartment);
			}
//			if(true){return;}
			//Get editable parameters
			//make module non-modal
    		MutableModuleInfo mmi = new DefaultMutableModuleInfo(){
				@Override
				public boolean isInteractive() {
					return true;
				}
    		};
    		mmi.setLabel(sbmlModelFile.getName());
    		
    		DefaultMutableModuleItem<Double> dmi = null;
			List<org.sbml.jsbml.Parameter> paramList = sbmlDocument.getModel().getListOfParameters();
			for (org.sbml.jsbml.Parameter param:paramList) {
				if(param.isConstant()){
					System.out.println(param.getId()+" "+param.getName()+" "+param.getValue());
		    		dmi = new DefaultMutableModuleItem<>(mmi, param.getId()+" "+param.getName(), Double.class);
		    		dmi.setDefaultValue((Double.isFinite(param.getValue())?param.getValue():0));
		    		mmi.addInput(dmi);
				}
			}
			List<Reaction> reactList = sbmlDocument.getModel().getListOfReactions();
			for (Reaction react:reactList) {
				List<LocalParameter> locParamList = react.getKineticLaw().getListOfLocalParameters();
				for(LocalParameter locParam:locParamList){
					System.out.println(locParam.getId()+" "+locParam.getName()+" "+locParam.getValue());
		    		dmi = new DefaultMutableModuleItem<>(mmi, locParam.getId()+" "+locParam.getName(), Double.class);
		    		dmi.setDefaultValue((Double.isFinite(locParam.getValue())?locParam.getValue():0));
		    		mmi.addInput(dmi);
				}
			}
    		final DefaultMutableModule dmm = new DefaultMutableModule(mmi);
    		final SwingInputHarvester sih = new SwingInputHarvester();
    		sih.setContext(appService.getContext());
    		final SwingInputPanel paramPanel = sih.createInputPanel();
    		sih.buildPanel(paramPanel, dmm);
//Display<?> disp = dispService.createDisplay(paramPanel);
//    		sih.harvest(dmm);
			
    		makeMyDisplay(paramPanel,sbmlModelFile,dmm,sih,paramPanel);
//    		sih.processResults(paramPanel, dmm);
//    		for(String paramName:dmm.getInputs().keySet()){
//    			System.out.println(paramName+" = "+dmm.getInput(paramName));
//    		}


//			//Get sbml text and display
////			sbmlText = textService.open(sbmlModelFile);
//			//Wait for display?? and set new title to filename
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
////					Display<?> disp = null;
//					while(dispService.getDisplays()==null || dispService.getDisplays().size()==0){
//						try {
//							Thread.sleep(20);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					};
////					disp.setName(sbmlModelFile.getName());
//		    		List<Display<?>> displays = dispService.getDisplays();
//		    		for(Display<?> disp:dispService.getDisplays()){
//		    			System.out.println(disp);
//		    		}
//
//				}
//			}).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logService.error(e);
		}

	}

	public static class MyViewer extends AbstractDisplayViewer<MyTest>{

		@Override
		public boolean isCompatible(UserInterface ui) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean canView(Display<?> d) {
			// TODO Auto-generated method stub
			return d instanceof MyTest;
		}
		
	}
	
	public static class MyTest extends AbstractDisplay<SwingInputPanel>{
		private File sbmlFile;
		private SwingInputHarvester swingInputHarvester;
		private DefaultMutableModule defaultMutableModule;
		private SwingInputPanel swingInputPanel;
		public MyTest(){
			super(SwingInputPanel.class);
		}
//		public MyTest(Class<SwingInputPanel> type,File sbmlFile) {
//			super(type);
//			this.sbmlFile = sbmlFile;
//		}
		public File getSbmlFile() {
			return sbmlFile;
		}
		public void setSbmlFile(File sbmlFile) {
			this.sbmlFile = sbmlFile;
		}
		public SwingInputHarvester getSwingInputHarvester() {
			return swingInputHarvester;
		}
		public void setSwingInputHarvester(SwingInputHarvester swingInputHarvester) {
			this.swingInputHarvester = swingInputHarvester;
		}
		public DefaultMutableModule getDefaultMutableModule() {
			return defaultMutableModule;
		}
		public void setDefaultMutableModule(DefaultMutableModule defaultMutableModule) {
			this.defaultMutableModule = defaultMutableModule;
		}
		public SwingInputPanel getSwingInputPanel() {
			return swingInputPanel;
		}
		public void setSwingInputPanel(SwingInputPanel swingInputPanel) {
			this.swingInputPanel = swingInputPanel;
		}
		
	}

	private void makeMyDisplay(SwingInputPanel paramPanel,File sbmlFile,DefaultMutableModule dmm,SwingInputHarvester sih,SwingInputPanel sip) {
		final MyTest myTest = new MyTest();
		myTest.setSbmlFile(sbmlFile);
		myTest.setContext(dispService.getContext());
		myTest.add(paramPanel);
		
		final MyTest disp = myTest;//(MyTest)dispService.createDisplay(paramPanel);
		disp.setSbmlFile(sbmlFile);
		disp.setDefaultMutableModule(dmm);
		disp.setSwingInputHarvester(sih);
		disp.setSwingInputPanel(sip);
		disp.setName(sbmlFile.getName());
//		System.out.println(disp);
//		if(true){return;}
		UserInterface defaultUI = uiService.getDefaultUI();
		final DisplayWindow createDisplayWindow = defaultUI.createDisplayWindow(disp);
		
		SwingDisplayPanel sdp = new SwingDisplayPanel() {
			
//    			{
//     				getDisplay().getContext().inject(this);
//    			}

			@Override
			public void setLabel(String s) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void redraw() {
				// TODO Auto-generated method stub
				System.out.println("redraw");
			}
			
			@Override
			public void redoLayout() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public DisplayWindow getWindow() {
				// TODO Auto-generated method stub
				return createDisplayWindow;
			}
			
			@Override
			public Display<?> getDisplay() {
				// TODO Auto-generated method stub
				return disp;
			}
		};

		sdp.add(paramPanel.getComponent());
		
		
		createDisplayWindow.setContent(sdp);
		createDisplayWindow.setTitle(disp.getName());
//		System.out.println(createDisplayWindow);
		createDisplayWindow.pack();
		createDisplayWindow.showDisplay(true);
		
	}

	public static void main(String[] args) {
		final ImageJ ij = new ImageJ();
		ij.launch(args);
	}

	private void initParameters() {
		sbmlModelFile = new File(RunVCellSimFromSBML.class.getResource("ImageJ_FRAP.xml").getFile());
	}
}
