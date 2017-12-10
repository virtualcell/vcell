/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.vcell.imagej.app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.scijava.app.AppService;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.convert.DefaultConverter;
import org.scijava.display.DisplayService;
import org.scijava.event.EventHandler;
import org.scijava.module.DefaultMutableModule;
import org.scijava.module.DefaultMutableModuleInfo;
import org.scijava.module.DefaultMutableModuleItem;
import org.scijava.module.MutableModuleInfo;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.task.Task;
import org.scijava.task.TaskService;
import org.scijava.task.event.TaskEvent;
import org.scijava.ui.UIService;
import org.scijava.ui.swing.widget.SwingInputHarvester;
import org.scijava.ui.viewer.DisplayViewer;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.vcellij.api.DomainType;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationService;
import org.vcell.vcellij.api.SimulationSpec;
import org.vcell.vcellij.api.SimulationState;
import org.vcell.vcellij.api.VariableInfo;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imagej.lut.LUTService;
import net.imagej.ops.OpService;
import net.imagej.plugins.commands.display.AutoContrast;
import net.imagej.plugins.commands.zoom.ZoomIn;
import net.imagej.table.DefaultGenericTable;
import net.imagej.table.DoubleColumn;
import net.imagej.table.GenericColumn;
import net.imagej.table.GenericTable;
import net.imagej.ui.swing.viewer.image.SwingImageDisplayPanel;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.display.ColorTable8;
import net.imglib2.img.Img;
import net.imglib2.img.ImgView;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Pair;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import net.miginfocom.swing.MigLayout;

/**
 *
 */
@Plugin(type = Command.class, menuPath = "Plugins>VCell>Run SBML Simulation",initializer = "initParameters")
public class RunVCellSimFromSBML implements Command {
    //
    // Feel free to add more parameters here...
    //

	@Parameter()
	private File sbmlModelFile;

    @Parameter()
    private double simDuration;

    @Parameter()
    private double simTimeStep;

    @Parameter()
    private String displayName;

    @Parameter
    private OpService opService;

    @Parameter
    private UIService uiService;

    @Parameter
    private CommandService cmdService;

    @Parameter
    private LUTService lutService;

	@Parameter
	private StatusService statusService;

    @Parameter
    private DisplayService displayService;

	@Parameter
	private AppService appService;

	@Parameter
	private SimulationService simService;

	@Parameter
	private TaskService taskService;
	
//	@Parameter(type = ItemIO.OUTPUT)
//	private Object result;

	public static void main(final String... args) throws IOException {
		final ImageJ ij = new ImageJ();
		ij.launch(args);
	}
	
	private void initParameters() {
		sbmlModelFile = new File(RunVCellSimFromSBML.class.getResource("ImageJ_FRAP.xml").getFile());
//		File sbmlModelFile = ResourceUtil.getVcellHome();

        try {
//    		MutableModuleInfo mmi = new DefaultMutableModuleInfo();
//    		SBMLDocument sbmlDocument = null;
//			sbmlDocument = SBMLReader.read(sbmlModelFile);
//			List<org.sbml.jsbml.Parameter> paramList = sbmlDocument.getModel().getListOfParameters();
//			for (org.sbml.jsbml.Parameter param:paramList) {
//				if(param.isConstant()){
//					System.out.println(param.getId()+" "+param.getName()+" "+param.getValue());
//		    		DefaultMutableModuleItem<Double> dmi = new DefaultMutableModuleItem<>(mmi, param.getId()+" "+param.getName(), Double.class);
//		    		dmi.setDefaultValue((Double.isFinite(param.getValue())?param.getValue():0));
//		    		mmi.addInput(dmi);
//				}
//			}
//			List<Reaction> reactList = sbmlDocument.getModel().getListOfReactions();
//			for (Reaction react:reactList) {
//				List<LocalParameter> locParamList = react.getKineticLaw().getListOfLocalParameters();
//				for(LocalParameter locParam:locParamList){
//					System.out.println(locParam.getId()+" "+locParam.getName()+" "+locParam.getValue());
//		    		DefaultMutableModuleItem<Double> dmi = new DefaultMutableModuleItem<>(mmi, locParam.getId()+" "+locParam.getName(), Double.class);
//		    		dmi.setDefaultValue((Double.isFinite(locParam.getValue())?locParam.getValue():0));
//		    		mmi.addInput(dmi);
//				}
//			}
//    		DefaultMutableModule dmm = new DefaultMutableModule(mmi);
//    		SwingInputHarvester sih = new SwingInputHarvester();
//    		sih.setContext(appService.getContext());
//    		sih.harvest(dmm);

			simDuration = 2;
			simTimeStep = .1;
			displayName = Sim.SIMULATED_FRAP;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    @Override
    public void run() {

    	try{
    		SBMLDocument sbmlDocument = null;
			sbmlDocument = SBMLReader.read(sbmlModelFile);
    		RunVCellSimFromSBML.Sim simResult = simulate(sbmlDocument.getModel(), simDuration, simTimeStep, appService, taskService, simService, statusService);
//    		ImgPlus<RealType> result = (ImgPlus<RealType>)simResult.image();
    		initDisplay((ImgPlus<RealType>)simResult.image(),displayName, simResult.simInfo(), simService, displayService, uiService, cmdService, lutService, opService);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
//        // Get SBML geometry
//        SpatialModelPlugin modelPlugin = (SpatialModelPlugin) document.getModel().getPlugin("spatial");
//        Geometry geometry = modelPlugin.getGeometry();
//        SampledField sampledField = geometry.getListOfSampledFields().get(0);
//
//
//        // Parse pixel values to int
//        String[] imgStringArray = sampledField.getSamples().split(" ");
//        int[] imgArray = new int[imgStringArray.length];
//        for (int i = 0; i < imgStringArray.length; i++) {
//            imgArray[i] = Integer.parseInt(imgStringArray[i]);
//        }
//
//        // Create the image and display
//        int width = sampledField.getNumSamples1();
//        int height = sampledField.getNumSamples2();
//        ArrayImg<UnsignedIntType, IntArray> img = ArrayImgs.unsignedInts(imgArray, width, height);
//        displayService.createDisplay(img);
    }
    
	public static  Sim simulate(Model sbmlModel,double simDuration,double simTimeStep,AppService appService,TaskService taskService,SimulationService simService,StatusService statusService) throws Exception {
    	initializeVCell(appService);
		final RunVCellSimFromSBML.Sim sim = createSimulation(sbmlModel,taskService,simService,simDuration,simTimeStep);

		// Wait for the simulation to complete.
		final StatusUpdater statusUpdater = new StatusUpdater(sim.task(),statusService);
		statusService.context().inject(statusUpdater);
		sim.task().waitFor();
		statusUpdater.clear();
		
		return sim;
	}

	public static void initializeVCell(AppService appService) {
		VCMongoMessage.enabled = false;
		System.setProperty(PropertyLoader.installationRoot, appService.getApp().getBaseDirectory().getAbsolutePath());
		ResourceUtil.setNativeLibraryDirectory();
//		NativeLoader.setNativeLibraryDirectory("/Users/curtis/code/vcell/vcell/nativelibs/mac64");
		NativeLib.HDF5.load();
	}
	
	/**
	 * A dumb class which passes task events on to the {@link StatusService}.
	 * Eventually, this sort of logic will be built in to SciJava Common. But for
	 * the moment, we do it ourselves.
	 */
	public static class StatusUpdater {
		private final DecimalFormat formatter = new DecimalFormat("##.##");
		private final Task task;

		private long lastUpdate;
		private StatusService statusService;
		public StatusUpdater(final Task task,StatusService statusService) {
			this.task = task;
			this.statusService = statusService;
		}

		public void update(final int value, final int max, final String message) {
			final long timestamp = System.currentTimeMillis();
			if (timestamp < lastUpdate + 100) return; // Avoid excessive updates.
			lastUpdate = timestamp;

			final double percent = 100.0 * value / max;
			statusService.showStatus(value, max, message + ": " + //
					formatter.format(percent) + "%");
		}

		public void clear() {
			statusService.clearStatus();
		}

		@EventHandler
		private void onEvent(final TaskEvent evt) {
			if (task == evt.getTask()) {
				final int value = (int) task.getProgressValue();
				final int max = (int) task.getProgressMaximum();
				final String message = task.getStatusMessage();
				update(value, max, message);
			}
		}
	}

	public static Sim createSimulation(final Model sbmlModel,TaskService taskService,SimulationService simService,double simDuration,double simTimeStep) throws Exception {
		// TODO: make SimulationServiceImpl a singleton of VCellService.
		final SimulationSpec simSpec = new SimulationSpec();
		simSpec.setOutputTimeStep(simTimeStep);
		simSpec.setTotalTime(simDuration);
		final Task task = taskService.createTask("Simulate FRAP");
		task.setProgressMaximum(100); // TODO: Double check this.
		final SimulationInfo simInfo = simService.computeModel(sbmlModel, simSpec,
			new ClientTaskStatusSupport()
		{

				@Override
				public void setProgress(final int progress) {
					task.setProgressValue(progress);
				}

				@Override
				public void setMessage(final String message) {
					task.setStatusMessage(message);
				}

				@Override
				public boolean isInterrupted() {
					return task.isCanceled();
				}

				@Override
				public int getProgress() {
					return (int) task.getProgressValue();
				}

				@Override
				public void addProgressDialogListener(
					final ProgressDialogListener progressDialogListener)
			{
					throw new UnsupportedOperationException();
				}
			});
		task.run(() -> {
			try {
				while ((simService.getStatus(simInfo).getSimState() != SimulationState.done) && 
						(simService.getStatus(simInfo).getSimState() != SimulationState.failed))
				{
					if (task.isCanceled()) break;
					Thread.sleep(50);
				}
			}
			catch (final Exception exc) {
				exc.printStackTrace();
			}
		});

		return new Sim() {

			@Override
			public SimulationService simService() {
				return simService;
			}

			@Override
			public SimulationSpec simSpec() {
				return simSpec;
			}

			@Override
			public SimulationInfo simInfo() {
				return simInfo;
			}

			@Override
			public Task task() {
				return task;
			}

		};
	}


	public interface Sim {

		public static final String SIMULATED_FRAP = "Simulated FRAP";

		SimulationService simService();

		SimulationSpec simSpec();

		SimulationInfo simInfo();

		Task task();

		/** Gets a 5D image: (X, Y, Z, var, time). */
		default Object image() {
			try {
				final List<Double> timePoints = simService().getTimePoints(simInfo());
				List<VariableInfo> vars = simService().getVariableList(simInfo());
				vars = vars.stream().filter(var -> var.getVariableDomainType() == DomainType.VOLUME).collect(Collectors.toList());
				final long xCount = simService().sizeX(simInfo());
				final long yCount = simService().sizeY(simInfo());
				final long zCount = simService().sizeZ(simInfo());
				final long vCount = vars.size();
				final long tCount = timePoints.size();
				// TODO: Make this more efficient. Can we wrap VCell instead of copying?
				final Img<DoubleType> bigImage = //
					ArrayImgs.doubles(xCount, yCount, zCount, vCount, tCount);
				final RandomAccess<DoubleType> ra = bigImage.randomAccess();
				for (int v = 0; v < vCount; v++) {
					ra.setPosition(v, 3);
					VariableInfo var = vars.get(v);
					for (int t = 0; t < tCount; t++) {
						ra.setPosition(t, 4);
						List<Double> data = simService().getData(simInfo(), var, t);
						int i = 0;
						for (int z = 0; z < zCount; z++) {
							ra.setPosition(z, 2);
							for (int y = 0; y < yCount; y++) {
								ra.setPosition(y, 1);
								for (int x = 0; x < xCount; x++) {
									ra.setPosition(x, 0);
									ra.get().set(data.get(i++));
								}
							}
						}
					}
				}
				IntervalView<DoubleType> view = Views.interval(Views.extendBorder(bigImage), new FinalInterval(xCount, yCount, zCount, vCount, tCount));
				Img<DoubleType> img = ImgView.wrap(view, bigImage.factory());
				return new ImgPlus<>(img, "blah"/*SIMULATED_FRAP*/, new AxisType[] {Axes.X, Axes.Y, Axes.Z, Axes.get("variable"), Axes.TIME});
			}
			catch (Exception exc) {
				// FIXME
				exc.printStackTrace();
				return null;
			}
		}

		/** Compiles simulation results into a table. */
		default GenericTable tableOfResults() {
			if (!task().isDone()) throw new IllegalStateException(
				"Simulation is not complete");

			try {
				final List<VariableInfo> vars = simService().getVariableList(simInfo());

				final Map<String, Double> results = new HashMap<>();
				final List<Double> timePoints = simService().getTimePoints(simInfo());
//				results.put("timePoints", timePoints);

				final GenericTable table = //
					new DefaultGenericTable(timePoints.size() + 1, vars.size());
				GenericColumn nameColumn = new GenericColumn("Variable");
				for (int t = 0; t < timePoints.size(); t++) {
					table.add(new DoubleColumn("" + t));
				}

				for (int v = 0; v < vars.size(); v++) {
					VariableInfo var = vars.get(v);
					table.set(0, v, var.getVariableDisplayName());
					for (int t = 0; t < timePoints.size(); t++) {
						final List<Double> data = simService().getData(simInfo(), var, t);
//						results.put(var.getVariableDisplayName() + "[" + t + "]", data.get(0));
						table.set(t + 1, v, data.get(0));
					}
				}
				return table;
			}
			catch (final Exception exc) {
				// FIXME
				exc.printStackTrace();
				return null;
			}
		}

	}

	private static int FIXED_X = 150;
//	public static void initDisplay(ImageDisplay display,SimulationInfo simInfo,SimulationService simService,DisplayService displayService,UIService uiService,CommandService cmdService,LUTService lutService,OpService opService) throws IOException {
//		
//		initDisplay(display, simInfo, simService, displayService, uiService, cmdService, lutService, opService);
//	}
	public static void initDisplay(/*ImgPlus<RealType>*/ RandomAccessible<RealType> result,String displayRootName,SimulationInfo simInfo,SimulationService simService,DisplayService displayService,UIService uiService,CommandService cmdService,LUTService lutService,OpService opService) throws IOException {

		try{

			List<Double> t = simService.getTimePoints(simInfo);
			List<VariableInfo> c = simService.getVariableList(simInfo);
			int z = simService.sizeZ(simInfo);
			int y = simService.sizeY(simInfo);
			int x = simService.sizeX(simInfo);
//			long[] dims = new long[result.numDimensions()];
//			result.dimensions(dims);
			System.out.println(t.size()+" "+c.size()+" "+z+" "+y+" "+x);
			

//			List<Display<?>> displays = ij.display().getDisplays();
//			for(Display disp:displays){
//				System.out.println(disp);
//				if(disp instanceof DefaultImageDisplay){
//					for(DataView dataview:((DefaultImageDisplay)disp)){
//						DefaultDataset data = (DefaultDataset)dataview.getData();
//						System.out.println(data.getImgPlus().getName()+" ----- disp == result? ="+(data.getImgPlus() == result));
//					}
//				}
//			}
			
//			IJ.debugMode = true;
			
			//Helpers to match labels with sliders
			HashMap<String, JLabel> labelComponents = new HashMap<>();
			HashMap<String, JScrollBar> scrollbarComponents = new HashMap<>();

			//Update label text when sliders are moved
			AdjustmentListener adjustmentListener = new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent ae) {
					try{
						JScrollBar scrollBar = (JScrollBar)ae.getSource();
						for(String label:scrollbarComponents.keySet()){
							if(scrollbarComponents.get(label) == ae.getSource()){
								final JLabel jlabel = labelComponents.get(label);
								int value = ((JScrollBar)ae.getSource()).getValue();
								String newText = null;
								if(label.equalsIgnoreCase("z")){
									newText = label+"("+value+")";
								}else if(label.equalsIgnoreCase("variable")){
									newText = label+"("+simService.getVariableList(simInfo).get(value).getVariableDisplayName()+")";//simServiceImpl.getVariableList(simInfo).get(value);
								}else if(label.equalsIgnoreCase("time")){
									newText = label+"("+simService.getTimePoints(simInfo).get(value)+")";//simServiceImpl.getTimePoints(simInfo).get(value);
								}
								if(newText != null){
									jlabel.setText(newText);
								}
								break;
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			};

    		//Make sure rootname doesn't match an existing display, if so the change new display name
    		Integer cnt = null;
    		while(displayService.getDisplay( displayRootName+(cnt==null?"":"_"+cnt)) != null){
    			cnt = (cnt==null?1:cnt+1);
    		}
    		String newDisplayName = displayRootName+(cnt==null?"":"_"+cnt);
    		
//			ImageDisplay myDisplay = (ImageDisplay)displayService.getDisplay(newDisplayName);
//			if(myDisplay == null){
//				myDisplay = (ImageDisplay)displayService.createDisplay(newDisplayName,result);
//			}
    		ImageDisplay myDisplay = (ImageDisplay)displayService.createDisplay(newDisplayName,result);
    		//wait for display to be available so we can tweak the gui
			DisplayViewer<?> myDisplayViewer = null;
			cnt=0;
			while(cnt < 50 && (myDisplayViewer = uiService.getDisplayViewer(myDisplay)) == null){
				Thread.sleep(100);
				cnt++;
			}
//			DisplayWindow displayWindow = myDisplayViewer.getWindow();
			SwingImageDisplayPanel myDisplayPanel =  (SwingImageDisplayPanel)myDisplayViewer.getPanel();
			
			//Find labels and sliders used to select z,var,time so we can add listeners to slider and annotate labels when changes occur
			ArrayList<Component> winChildren = new ArrayList<>(Arrays.asList(myDisplayPanel.getComponents()));
			String lastLabelStr = null;
			final MigLayout[] migLayoutHolder = new MigLayout[] {null};
			while(winChildren.size() > 0){
				Component child = winChildren.remove(0);
				System.out.println(child.getClass().getName());
				if(child instanceof Container){
					winChildren.addAll(Arrays.asList(((Container)child).getComponents()));
				}
				if (child instanceof JLabel){
					String text = ((JLabel)child).getText();
					if(text.equalsIgnoreCase("Z") || text.equalsIgnoreCase("variable") || text.equalsIgnoreCase("time")){
						lastLabelStr = text;
						labelComponents.put(((JLabel)child).getText(), (JLabel)child);
						//Store the layoutmanager of the panel that contains the labels and sliders
						migLayoutHolder[0] = (MigLayout)((Container)child.getParent()).getLayout();
						//Set size of 'variable' label so long text doesn't spillover onto slider
						if(text.equalsIgnoreCase("variable")){
							Dimension d = ((JLabel)child).getMinimumSize();
							d.setSize(FIXED_X, d.height);
							((JLabel)child).setMinimumSize(d);
							d = ((JLabel)child).getMaximumSize();
							d.setSize(FIXED_X, d.height);
							((JLabel)child).setMaximumSize(d);
							d = ((JLabel)child).getPreferredSize();
							d.setSize(FIXED_X, d.height);
							((JLabel)child).setPreferredSize(d);
						}
					}
					System.out.println("---label='"+text+"' prnt="+child.getParent().getClass().getName());
					System.out.println("   "+child.getParent().hashCode()+" "+((Container)child.getParent()).getLayout().getClass().getName());
					
				} else if (child instanceof JScrollBar){
					System.out.println("---jscrollbar='"+((JScrollBar)child).getName()+"' prnt="+child.getParent().getClass().getName()+" "+child.getParent().hashCode());
					System.out.println("   "+child.getParent().hashCode());
					if(lastLabelStr != null){
						scrollbarComponents.put(lastLabelStr, (JScrollBar)child);
						((JScrollBar) child).addAdjustmentListener(adjustmentListener);
					}
					lastLabelStr = null;
				}else {
					lastLabelStr = null;
				}
			}
			//Set 'label' column to be fixed so width doesn't change when using 'variable' slider
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					migLayoutHolder[0].setColumnConstraints("[right,"+FIXED_X+":"+FIXED_X+":"+FIXED_X+"]5[fill,grow]");//http://www.miglayout.com/
				}
			});
			//Set labels to initial values of sliders
			for(String label:labelComponents.keySet()){
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						AdjustmentEvent ae = new AdjustmentEvent(scrollbarComponents.get(label), AdjustmentEvent.RESERVED_ID_MAX+1, AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED, scrollbarComponents.get(label).getValue());
						adjustmentListener.adjustmentValueChanged(ae);
					}
				});
			}
			
			//Set initial zoom so data x or y are up to 512
			int biggestDim = Math.max(x, y);
			int numZooms = Math.max(0, (512/biggestDim)-1);
			for(int i=0;i<numZooms;i++){
				cmdService.run(ZoomIn.class, true);
			}
//			CommandInfo cmdInfo = ij.scifio().command().getCommand(ZoomSet.class);
//			Future<?> future2 = ij.scifio().command().run(cmdInfo, true,"zoomPercent",500,"centerU",38.5,"centerV",38.5);
//			Object obj = future2.get();// wait for command to finish
//			System.out.println(obj);
			
			//Set brightnessContrast
			cmdService.run(AutoContrast.class, true);
			
			//Set the grand old BioRad colormap
			ColorTable8 ct = new ColorTable8(vcellLutData());
			lutService.applyLUT(ct, myDisplay);

			//Calculate min/max for each variable over all times
DatasetView dsv = ((DatasetView)myDisplay.getActiveView());
System.out.println("chancnt="+dsv.getChannelCount());
			double[] minVars = new double[c.size()];
			double[] maxVars = new double[c.size()];
			for(int channelIndex=0;channelIndex<c.size();channelIndex++){
				//Define interval for current channel to include all pixels and all times
				IntervalView<RealType> interval = Views.interval(result, new long[] {0,0,0,channelIndex,0}, new long[] {x-1,y-1,z-1,channelIndex,t.size()-1});//x,y,z,c,t
				IterableInterval<RealType> channelData = Views.iterable(interval);
				Pair<RealType<?>, RealType<?>> minMax = opService.stats().minMax(channelData);
				minVars[channelIndex] = minMax.getA().getRealDouble();//ij.op().stats().min(dataII).getRealDouble();
				maxVars[channelIndex] = minMax.getB().getRealDouble();//ij.op().stats().max(dataII).getRealDouble();
				System.out.println("var="+c.get(channelIndex).getVariableDisplayName()+" min="+minVars[channelIndex]+" max="+maxVars[channelIndex]);
			}
//			//Set display range to first channel, 'fluorophore' in ImageJ_FRAP.xml example
//			if(myDisplay.getActiveView() instanceof DatasetView){
//				((DatasetView)myDisplay.getActiveView()).setChannelRanges(minVars[0], maxVars[0]);
//			}

			
//			myDisplay.getActiveView().getContext().inject(new EventTester());
			
//			SwingDisplayWindow myDisplayWindow = (SwingDisplayWindow)myDisplayViewer.getWindow();
//			BeanUtils.printComponentInfo(myDisplayWindow);
//			System.out.println(myDisplay+" -- "+myDisplayViewer+" -- "+ myDisplayWindow);

//			Overlay overlay = new Overlay();
//			String[] imageTitles = WindowManager.getImageTitles();
//			ImagePlus imagePlus = WindowManager.getImage(Sim.SIMULATED_FRAP);
			
//			createAnnotation(myDisplay, t, c, z,ij.overlay());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
//		try {
//			CommandModule commandModule = future.get();// wait for command to finish
//			Map<String, Object> outputs = commandModule.getOutputs();
//			Object moduleResult = outputs.get("result");
//			if(moduleResult instanceof ImgPlus<?>){
//				ImgPlus<?> simImg = (ImgPlus<?>)moduleResult;
//				Iterator<?> iter =  simImg.getImg().iterator();
//				int cnt = 0;
//				while(iter.hasNext()){
//					DoubleType val = (DoubleType)iter.next();
//					if(val.get() !=0 && val.get() != 1){
//						System.out.println(iter.next());
//					}
//					cnt++;
//				}
//				int numdims = ((ImgPlus<?>)moduleResult).numDimensions();
//				long[] dims = new long[numdims];
//				((ImgPlus<?>)moduleResult).dimensions(dims);
//				for(long dimval:dims){
//					System.out.println("dim "+dimval);
//				}
//				System.out.println("total num vals ="+cnt);
//				ij.ui().show(simImg);
//			}
//			System.out.println(moduleResult);
//		} catch (CancellationException e) {
//			// ignore
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public static byte[][] vcellLutData(){
		int[] lutvals = new int[] {
				0,0,128,
				0,0,132,
				0,0,137,
				0,0,141,
				0,0,146,
				0,0,151,
				0,0,155,
				0,0,160,
				0,0,165,
				0,0,169,
				0,0,174,
				0,0,179,
				0,0,183,
				0,0,188,
				0,0,193,
				0,0,197,
				0,0,202,
				0,0,206,
				0,0,211,
				0,0,216,
				0,0,220,
				0,0,225,
				0,0,230,
				0,0,234,
				0,0,239,
				0,0,244,
				0,0,248,
				0,0,253,
				0,3,255,
				0,7,255,
				0,12,255,
				0,16,255,
				0,21,255,
				0,26,255,
				0,30,255,
				0,35,255,
				0,40,255,
				0,44,255,
				0,49,255,
				0,54,255,
				0,58,255,
				0,63,255,
				0,68,255,
				0,72,255,
				0,77,255,
				0,81,255,
				0,86,255,
				0,91,255,
				0,95,255,
				0,100,255,
				0,105,255,
				0,109,255,
				0,114,255,
				0,119,255,
				0,123,255,
				0,128,255,
				0,133,255,
				0,137,255,
				0,142,255,
				0,146,255,
				0,151,255,
				0,156,255,
				0,160,255,
				0,165,255,
				0,170,255,
				0,174,255,
				0,179,255,
				0,184,255,
				0,188,255,
				0,193,255,
				0,198,255,
				0,202,255,
				0,207,255,
				0,211,255,
				0,216,255,
				0,221,255,
				0,225,255,
				0,230,255,
				0,235,255,
				0,239,255,
				0,244,255,
				0,249,255,
				0,253,255,
				0,255,252,
				0,255,247,
				0,255,243,
				0,255,238,
				0,255,233,
				0,255,229,
				0,255,224,
				0,255,220,
				0,255,215,
				0,255,210,
				0,255,206,
				0,255,201,
				0,255,196,
				0,255,192,
				0,255,187,
				0,255,182,
				0,255,178,
				0,255,173,
				0,255,168,
				0,255,164,
				0,255,159,
				0,255,155,
				0,255,150,
				0,255,145,
				0,255,141,
				0,255,136,
				0,255,131,
				0,255,127,
				0,255,122,
				0,255,117,
				0,255,113,
				0,255,108,
				0,255,103,
				0,255,99,
				0,255,94,
				0,255,90,
				0,255,85,
				0,255,80,
				0,255,76,
				0,255,71,
				0,255,66,
				0,255,62,
				0,255,57,
				0,255,52,
				0,255,48,
				0,255,43,
				0,255,38,
				0,255,34,
				0,255,29,
				0,255,25,
				0,255,20,
				0,255,15,
				0,255,11,
				0,255,6,
				0,255,1,
				3,255,0,
				8,255,0,
				13,255,0,
				17,255,0,
				22,255,0,
				27,255,0,
				31,255,0,
				36,255,0,
				40,255,0,
				45,255,0,
				50,255,0,
				54,255,0,
				59,255,0,
				64,255,0,
				68,255,0,
				73,255,0,
				78,255,0,
				82,255,0,
				87,255,0,
				92,255,0,
				96,255,0,
				101,255,0,
				105,255,0,
				110,255,0,
				115,255,0,
				119,255,0,
				124,255,0,
				129,255,0,
				133,255,0,
				138,255,0,
				143,255,0,
				147,255,0,
				152,255,0,
				157,255,0,
				161,255,0,
				166,255,0,
				171,255,0,
				175,255,0,
				180,255,0,
				184,255,0,
				189,255,0,
				194,255,0,
				198,255,0,
				203,255,0,
				208,255,0,
				212,255,0,
				217,255,0,
				222,255,0,
				226,255,0,
				231,255,0,
				236,255,0,
				240,255,0,
				245,255,0,
				249,255,0,
				254,255,0,
				255,251,0,
				255,247,0,
				255,242,0,
				255,237,0,
				255,233,0,
				255,228,0,
				255,223,0,
				255,219,0,
				255,214,0,
				255,209,0,
				255,205,0,
				255,200,0,
				255,196,0,
				255,191,0,
				255,186,0,
				255,182,0,
				255,177,0,
				255,172,0,
				255,168,0,
				255,163,0,
				255,158,0,
				255,154,0,
				255,149,0,
				255,144,0,
				255,140,0,
				255,135,0,
				255,131,0,
				255,126,0,
				255,121,0,
				255,117,0,
				255,112,0,
				255,107,0,
				255,103,0,
				255,98,0,
				255,93,0,
				255,89,0,
				255,84,0,
				255,79,0,
				255,75,0,
				255,70,0,
				255,66,0,
				255,61,0,
				255,56,0,
				255,52,0,
				255,47,0,
				255,42,0,
				255,38,0,
				255,33,0,
				255,28,0,
				255,24,0,
				255,19,0,
				255,14,0,
				255,10,0,
				255,5,0,
				255,0,0,
				255,0,0,
				255,0,0,
				255,0,0,
				255,0,0,
				255,0,0,
				255,0,0,
				255,0,0,
				255,0,0
		};
		byte[][] vcByteLut = new byte[3][256];
		for (int chanIndex = 0; chanIndex < vcByteLut.length; chanIndex++) {
			for (int colorIndex = 0; colorIndex < vcByteLut[chanIndex].length; colorIndex++) {
				vcByteLut[chanIndex][colorIndex] = (byte)(0xFF & lutvals[colorIndex*3+chanIndex]);
			}
			
			
		}
		return vcByteLut;
	}

}
