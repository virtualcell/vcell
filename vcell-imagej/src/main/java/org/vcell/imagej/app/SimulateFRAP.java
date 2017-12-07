

package org.vcell.imagej.app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.ext.spatial.CompressionKind;
import org.sbml.jsbml.ext.spatial.DataKind;
import org.sbml.jsbml.ext.spatial.InterpolationKind;
import org.sbml.jsbml.ext.spatial.SampledField;
import org.scijava.ItemIO;
import org.scijava.app.AppService;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.CommandInfo;
import org.scijava.command.CommandModule;
import org.scijava.io.ByteArrayByteBank;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.task.TaskService;
import org.scijava.ui.viewer.DisplayViewer;
import org.scijava.ui.viewer.DisplayWindow;
import org.scijava.util.FileUtils;
import org.vcell.vcellij.SimulationServiceImpl;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationService;
import org.vcell.vcellij.api.VariableInfo;

import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.display.ImageDisplay;
import net.imagej.display.OverlayService;
import net.imagej.overlay.TextOverlay;
import net.imagej.plugins.commands.display.AutoContrast;
import net.imagej.plugins.commands.zoom.ZoomIn;
import net.imagej.ui.swing.viewer.image.SwingImageDisplayPanel;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ColorTable8;
import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Pair;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import net.miginfocom.swing.MigLayout;

@Plugin(type = Command.class, menuPath = "Plugins>VCell>Simulate FRAP")
public class SimulateFRAP<T extends RealType<T>, B extends BooleanType<B>> implements Command {

	@Parameter
	private LogService log;

	@Parameter
	private StatusService statusService;

	@Parameter
	private TaskService taskService;

	@Parameter
	private AppService appService;

	@Parameter
	private SimulationService simService;
	
//	@Parameter
//	private VCellService vcellService;

//	@Parameter
//	private RandomAccessibleInterval<T> image;
//
//	// TODO - for the case where we have more than two classes, we probably
//	// want to use a LabelingMapping<String> which identifies the regions
//	// corresponding to each domain/class in the geometry.
//	@Parameter
//	private RandomAccessibleInterval<B> mask;

//	// FIXME: Grab these from the current selection of the image.
//	// To do that, we need to use ImagePlus instead of RAI<T> for the image.
//	@Parameter
//	private double bleachX = 322;
//	@Parameter
//	private double bleachY = 241;
//	@Parameter
//	private double bleachWidth = 33;
//	@Parameter
//	private double bleachHeight = 21;
//	// FIXME: Grab this from the active ImageDisplay's current time index.
//	// Or from the ImagePlus's current time index -- whichever is easier.
//	@Parameter
//	private double bleachTimeIndex = 5;

	@Parameter(type = ItemIO.OUTPUT)
	private RunVCellSimFromSBML.Sim simResult;

	@Parameter(type = ItemIO.OUTPUT)
	private Object result;
	
//	@Parameter(type = ItemIO.OUTPUT)
//	private SimulationInfo simInfo;
	
	@Override
	public void run() {
		try {
			URL modelFileURL = SimulateFRAP.class.getResource("ImageJ_FRAP.xml");
			simResult = RunVCellSimFromSBML.simulate(new File(modelFileURL.getFile()), 2, .1, appService, taskService, simService, statusService);
			result = simResult.image();
		}
		catch (final Exception e) {
			log.error(e);
		}
		System.out.println("============= COMPLETE ===============");
	}

	
	private static int FIXED_X = 150;
	
	public static void main(final String... args) throws IOException {
		final ImageJ ij = new ImageJ();
		ij.launch(args);
		if(true){return;}
		List<CommandInfo> commandList =  ij.scifio().command().getCommands();
		for(CommandInfo ci:commandList){
			if(ci.getMenuPath().size() >= 2 && ci.getMenuPath().get(0).getName().equalsIgnoreCase("image") && (ci.getMenuPath().get(1).getName().equalsIgnoreCase("zoom") || ci.getMenuPath().get(1).getName().equalsIgnoreCase("adjust"))){
				System.out.println(ci.getDelegateClassName()+" --- "+ci.getMenuPath());
			}
			if((""+ci.getMenuPath().toString()).toLowerCase().contains("lut")){
				System.out.println(ci.getDelegateClassName()+" ------ "+ci.getMenuPath());
			}
			
		}

		
//		final String datasetPath = "http://imagej.net/images/bridge.gif";
//		final Dataset dataset = ij.scifio().datasetIO().open(datasetPath);
//		ij.ui().show("bridge", dataset);
		
		
//		// Make up an arbitrary mask with some constraints.
//		final Img<BitType> bitMask = ArrayImgs.bits(dataset.dimension(0), dataset.dimension(1), dataset.dimension(2));
//		Cursor<BitType> c = bitMask.localizingCursor();
//		while (c.hasNext()) {
//			final BitType bit = c.next();
//			long x = c.getLongPosition(0);
//			long y = c.getLongPosition(1);
//			bit.set(x > 100 & x < 200 & y > 80 & y < 350);
//		}
//		ij.ui().show("mask", bitMask);

		Future<CommandModule> future = ij.command().run(SimulateFRAP.class, true/*, "mask", bitMask*/);
		try{
			CommandModule commandModule = future.get();// wait for command to finish
			Map<String, Object> outputs = commandModule.getOutputs();
			RunVCellSimFromSBML.Sim sim = (RunVCellSimFromSBML.Sim)outputs.get("simResult");
			ImgPlus<RealType> result = (ImgPlus<RealType>) outputs.get("result");
			SimulationInfo simInfo = sim.simInfo();
			
//			SimulateFRAP<DoubleType,?> simulateFRAP = (SimulateFRAP<DoubleType,?>)commandModule.getDelegateObject();
//			System.out.println(simulateFRAP);
			SimulationService simService = ij.getContext().getService(SimulationService.class);
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


			ImageDisplay myDisplay = (ImageDisplay)ij.display().getDisplay(RunVCellSimFromSBML.Sim.SIMULATED_FRAP);
			DisplayViewer<?> myDisplayViewer = ij.ui().getDisplayViewer(myDisplay);
			DisplayWindow displayWindow = myDisplayViewer.getWindow();
			SwingImageDisplayPanel myDisplayPanel =  (SwingImageDisplayPanel)myDisplayViewer.getPanel();
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
				ij.command().run(ZoomIn.class, true);
			}
//			CommandInfo cmdInfo = ij.scifio().command().getCommand(ZoomSet.class);
//			Future<?> future2 = ij.scifio().command().run(cmdInfo, true,"zoomPercent",500,"centerU",38.5,"centerV",38.5);
//			Object obj = future2.get();// wait for command to finish
//			System.out.println(obj);
			
			//Set brightnessContrast
			ij.command().run(AutoContrast.class, true);
			
			ColorTable8 ct = new ColorTable8(RunVCellSimFromSBML.vcellLutData());
			ij.lut().applyLUT(ct, myDisplay);

			//Calculate min/max for each variable over all times
			double[] minVars = new double[c.size()];
			double[] maxVars = new double[c.size()];
			for(int channelIndex=0;channelIndex<c.size();channelIndex++){
				//Define interval for current channel to include all pixels and all times
				IntervalView<RealType> interval = Views.interval(result, new long[] {0,0,0,channelIndex,0}, new long[] {x-1,y-1,z-1,channelIndex,t.size()-1});//x,y,z,c,t
				IterableInterval<RealType> channelData = Views.iterable(interval);
				Pair<RealType<?>, RealType<?>> minMax = ij.op().stats().minMax(channelData);
				minVars[channelIndex] = minMax.getA().getRealDouble();//ij.op().stats().min(dataII).getRealDouble();
				maxVars[channelIndex] = minMax.getB().getRealDouble();//ij.op().stats().max(dataII).getRealDouble();
				System.out.println("var="+c.get(channelIndex).getVariableDisplayName()+" min="+minVars[channelIndex]+" max="+maxVars[channelIndex]);
			}

			
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

	
	private static void createAnnotation(ImageDisplay imageDisplay, List<Double> t, List<VariableInfo> c, int z, OverlayService overlayService){
		String[][] annots = new String[(t==null?1:t.size())][(c==null?1:c.size())];
		ArrayList<TextOverlay> overlayList = new ArrayList<>();
		for (int i = 0; i < annots.length; i++) {
			for (int j = 0; j < annots[i].length; j++) {
				String str = (t != null?t.get(i)+"":"");
				str+=(c !=null?(str.length()==0?"":":")+"("+c.get(j).getVariableDisplayName()+")":"");
				if(str.length() > 0){
					for (int k = 0; k < z; k++) {
						TextOverlay textOverlay = new TextOverlay(overlayService.getContext(),0,0,str);//(0,0, str);
//						text.setNonScalable(true);
//						if(/*imagePlus.isHyperStack()*/imageDisplay.numDimensions() > 2){
//							text.setPosition(j+1,k+1,i+1);
//						}else{
//							text.setPosition(i+j+k+1);// assumes 2 components (ij or jk or ik) are always 0
//						}
						overlayList.add(textOverlay);						
					}
				}
			}
		}
		if(overlayList.size() > 0){
			overlayService.addOverlays(imageDisplay, overlayList);
		}
	}
	
//	private Object simulate() throws Exception {
//		RunVCellSimFromSBML.initializeVCell(appService);
//
////		if (!Intervals.equalDimensions(image, mask)) {
////			throw new IllegalArgumentException("Mask dimensions do not match image");
////		}
////
////		final RandomAccessibleInterval<T> goodImage;
////		final RandomAccessibleInterval<B> goodMask;
////		if (image.numDimensions() < 2 || image.numDimensions() > 3) {
////			throw new IllegalArgumentException("Image must be 2 or 3 dimensional");
////		}
////		if (image.numDimensions() == 2) {
////			log.warn("Projected image into the third dimension");
////			goodImage = Views.addDimension(image, 0, 2); // TODO: double check this; should be dim length 3
////			goodMask = Views.addDimension(mask, 0, 2); // TODO: double check this; should be dim length 3
////		}
////		else {
////			goodImage = image;
////			goodMask = mask;
////		}
//
//		// Configure the model based on the inputs.
//		final Model sbmlModel = readModel("ImageJ_FRAP.xml");//"frap.xml");
////		// NB: This model is 3D. We can feed in a 2D plane if we fake Z with the same data three times.
////		final SpatialModelPlugin smPlugin = (SpatialModelPlugin) sbmlModel.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
////		final Geometry geo = smPlugin.getGeometry();
////
////		// TODO: When we have an ImgLabeling, we want to match the domain type ids with the labelings themselves.
////		// So e.g. "cell" maps to domainType "domainType_cell", etc.
////		// In this way, we can accept ImgLabeling that offer mapping of N classes and there is a 1-to-1 mapping.
////		ListOf<org.sbml.jsbml.ext.spatial.DomainType> domainTypes = geo.getListOfDomainTypes();
////
////		final Optional<GeometryDefinition> sfgDef = geo.getListOfGeometryDefinitions().stream().filter(geoDef -> geoDef instanceof SampledFieldGeometry).findFirst();
////		if (!sfgDef.isPresent()) throw new IllegalStateException("No sampled field geometry definition found");
////		SampledFieldGeometry sfg = (SampledFieldGeometry) sfgDef.get();
////		// E.g.: ["cell": 1.0, "ec": 2.0]
////		final Map<String, Double> labelValues = sfg.getListOfSampledVolumes().stream().collect(Collectors.toMap(SampledVolume::getId, SampledVolume::getSampledValue));
////		// TODO: use these label values; they should line up with an input LabelMapping<String>.
////
////		// Feed the image mask into the geometry's domain labeling.
////		ListOf<SampledField> sampledFields = geo.getListOfSampledFields();
////		if (sampledFields == null || sampledFields.isEmpty()) {
////			throw new IllegalStateException("Expected model with one sampled field");
////		}
////		final SampledField domainLabeling = sampledFields.get(0);
////		// FIXME: stop using a prior knowledge of cell and ec labels. Instead, we'll have a LabelingMapping which already knows what's what.
////		final Converter<B, UnsignedByteType> converter = (input, output) -> output.set(input.get() ? labelValues.get("cell").shortValue() : labelValues.get("ec").shortValue());
////		final RandomAccessibleInterval<UnsignedByteType> maskAsLabeling = Converters.convert(goodMask, converter, new UnsignedByteType());
////		populateField(domainLabeling, maskAsLabeling);
////
////		// Feed the image sample values into the geometry as a sampled field.
//////		final SampledField sampledField = geo.createSampledField("fieldData");
//////		populateField(sampledField, goodImage);
//
//		// Start the simulation.
//		final RunVCellSimFromSBML.Sim sim = RunVCellSimFromSBML.createSimulation(sbmlModel,taskService,simService,2,.1);
//
//		// Wait for the simulation to complete.
//		final RunVCellSimFromSBML.StatusUpdater statusUpdater = new RunVCellSimFromSBML.StatusUpdater(sim.task(),statusService);
//		statusService.context().inject(statusUpdater);
//		sim.task().waitFor();
//		statusUpdater.clear();
//
//		return sim.image();
//	}
	
	private <Z extends RealType<Z>> void populateField(final SampledField sampledField, RandomAccessibleInterval<Z> rai) {
		// TODO: ensure dimensional lengths are not bigger than Integer.MAX_VALUE.
		if (rai.numDimensions() != 3) throw new IllegalStateException("How did you get a non 3D image in here?");
		final int xCount = (int) rai.dimension(0);
		final int yCount = (int) rai.dimension(1);
		final int zCount = (int) rai.dimension(2);
		sampledField.setNumSamples1(xCount);
		sampledField.setNumSamples2(yCount);
		sampledField.setNumSamples3(zCount);
		sampledField.setSamplesLength(xCount * yCount * zCount);
		sampledField.setInterpolationType(InterpolationKind.nearestneighbor);
		sampledField.setDataType(DataKind.UINT8); // VCell likes this.
		sampledField.setSamples(dataAsString(rai));
		sampledField.setCompression(CompressionKind.uncompressed);
	}

	private <Z extends RealType<Z>> String dataAsString(final RandomAccessibleInterval<Z> rai) {
		final StringBuilder sb = new StringBuilder();
		
		final long numElements = //
			rai.dimension(0) * rai.dimension(1) * rai.dimension(2);
		RandomAccess<Z> ra = rai.randomAccess();
		for (long i = 0; i < numElements; i++) {
			// Position the accessor at the given index, with dim #0 moving fastest.
			IntervalIndexer.indexToPosition(i, rai, ra);

			sb.append(ra.get());
			sb.append(" ");
		}
		return sb.toString();
	}

//	// -- Static helper methods --
//
//	// TODO: Consider massaging these into methods of VCellService,
//	// and/or a separate static utility class.
//
//	private Sim createSimulation(final Model sbmlModel) throws Exception {
//		// TODO: make SimulationServiceImpl a singleton of VCellService.
//		final SimulationSpec simSpec = new SimulationSpec();
//		simSpec.setOutputTimeStep(.1);
//		simSpec.setTotalTime(2.0);
//		final Task task = taskService.createTask("Simulate FRAP");
//		task.setProgressMaximum(100); // TODO: Double check this.
//		simInfo = simService.computeModel(sbmlModel, simSpec,
//			new ClientTaskStatusSupport()
//		{
//
//				@Override
//				public void setProgress(final int progress) {
//					task.setProgressValue(progress);
//				}
//
//				@Override
//				public void setMessage(final String message) {
//					task.setStatusMessage(message);
//				}
//
//				@Override
//				public boolean isInterrupted() {
//					return task.isCanceled();
//				}
//
//				@Override
//				public int getProgress() {
//					return (int) task.getProgressValue();
//				}
//
//				@Override
//				public void addProgressDialogListener(
//					final ProgressDialogListener progressDialogListener)
//			{
//					throw new UnsupportedOperationException();
//				}
//			});
//		task.run(() -> {
//			try {
//				while ((simService.getStatus(simInfo).getSimState() != SimulationState.done) && 
//						(simService.getStatus(simInfo).getSimState() != SimulationState.failed))
//				{
//					if (task.isCanceled()) break;
//					Thread.sleep(50);
//				}
//			}
//			catch (final Exception exc) {
//				exc.printStackTrace();
//			}
//		});
//
//		return new Sim() {
//
//			@Override
//			public SimulationService simService() {
//				return simService;
//			}
//
//			@Override
//			public SimulationSpec simSpec() {
//				return simSpec;
//			}
//
//			@Override
//			public SimulationInfo simInfo() {
//				return simInfo;
//			}
//
//			@Override
//			public Task task() {
//				return task;
//			}
//
//		};
//	}
//
//	private void initializeVCell() {
//		VCMongoMessage.enabled = false;
//		System.setProperty(PropertyLoader.installationRoot, appService.getApp().getBaseDirectory().getAbsolutePath());
//		ResourceUtil.setNativeLibraryDirectory();
////		NativeLoader.setNativeLibraryDirectory("/Users/curtis/code/vcell/vcell/nativelibs/mac64");
//		NativeLib.HDF5.load();
//	}

	private static Model readModel(final String resourcePath) throws IOException,
		XMLStreamException
	{
		// TODO: Use org.scijava.io API once there is an InputStream handle.

		// Read the model bytes.
		final ByteArrayByteBank bank = new ByteArrayByteBank();
		try (final InputStream is = //
			SimulateFRAP.class.getResourceAsStream(resourcePath))
		{
			final byte[] buf = new byte[65536];
			while (true) {
				final int r = is.read(buf);
				if (r <= 0) break;
				bank.appendBytes(buf, r);
			}
		}

		// Write the bytes back to a temp file.
		final File tmpFile = File.createTempFile("vcellij", "frap");
		tmpFile.deleteOnExit();
		FileUtils.writeFile(tmpFile, bank.toByteArray());

		// Feed the temp file to the SBML API.
		final SBMLReader reader = new SBMLReader();
		final SBMLDocument document = reader.readSBML(tmpFile);
		return document.getModel();
	}

	// -- Helper classes --

//	public interface Sim {
//
//		public static final String SIMULATED_FRAP = "Simulated FRAP";
//
//		SimulationService simService();
//
//		SimulationSpec simSpec();
//
//		SimulationInfo simInfo();
//
//		Task task();
//
//		/** Gets a 5D image: (X, Y, Z, var, time). */
//		default Object image() {
//			try {
//				final List<Double> timePoints = simService().getTimePoints(simInfo());
//				List<VariableInfo> vars = simService().getVariableList(simInfo());
//				vars = vars.stream().filter(var -> var.getVariableDomainType() == DomainType.VOLUME).collect(Collectors.toList());
//				final long xCount = simService().sizeX(simInfo());
//				final long yCount = simService().sizeY(simInfo());
//				final long zCount = simService().sizeZ(simInfo());
//				final long vCount = vars.size();
//				final long tCount = timePoints.size();
//				// TODO: Make this more efficient. Can we wrap VCell instead of copying?
//				final Img<DoubleType> bigImage = //
//					ArrayImgs.doubles(xCount, yCount, zCount, vCount, tCount);
//				final RandomAccess<DoubleType> ra = bigImage.randomAccess();
//				for (int v = 0; v < vCount; v++) {
//					ra.setPosition(v, 3);
//					VariableInfo var = vars.get(v);
//					for (int t = 0; t < tCount; t++) {
//						ra.setPosition(t, 4);
//						List<Double> data = simService().getData(simInfo(), var, t);
//						int i = 0;
//						for (int z = 0; z < zCount; z++) {
//							ra.setPosition(z, 2);
//							for (int y = 0; y < yCount; y++) {
//								ra.setPosition(y, 1);
//								for (int x = 0; x < xCount; x++) {
//									ra.setPosition(x, 0);
//									ra.get().set(data.get(i++));
//								}
//							}
//						}
//					}
//				}
//				IntervalView<DoubleType> view = Views.interval(Views.extendBorder(bigImage), new FinalInterval(xCount, yCount, zCount, vCount, tCount));
//				Img<DoubleType> img = ImgView.wrap(view, bigImage.factory());
//				return new ImgPlus<>(img, SIMULATED_FRAP, new AxisType[] {Axes.X, Axes.Y, Axes.Z, Axes.get("variable"), Axes.TIME});
//			}
//			catch (Exception exc) {
//				// FIXME
//				exc.printStackTrace();
//				return null;
//			}
//		}
//
//		/** Compiles simulation results into a table. */
//		default GenericTable tableOfResults() {
//			if (!task().isDone()) throw new IllegalStateException(
//				"Simulation is not complete");
//
//			try {
//				final List<VariableInfo> vars = simService().getVariableList(simInfo());
//
//				final Map<String, Double> results = new HashMap<>();
//				final List<Double> timePoints = simService().getTimePoints(simInfo());
////				results.put("timePoints", timePoints);
//
//				final GenericTable table = //
//					new DefaultGenericTable(timePoints.size() + 1, vars.size());
//				GenericColumn nameColumn = new GenericColumn("Variable");
//				for (int t = 0; t < timePoints.size(); t++) {
//					table.add(new DoubleColumn("" + t));
//				}
//
//				for (int v = 0; v < vars.size(); v++) {
//					VariableInfo var = vars.get(v);
//					table.set(0, v, var.getVariableDisplayName());
//					for (int t = 0; t < timePoints.size(); t++) {
//						final List<Double> data = simService().getData(simInfo(), var, t);
////						results.put(var.getVariableDisplayName() + "[" + t + "]", data.get(0));
//						table.set(t + 1, v, data.get(0));
//					}
//				}
//				return table;
//			}
//			catch (final Exception exc) {
//				// FIXME
//				exc.printStackTrace();
//				return null;
//			}
//		}
//
//	}

//	/**
//	 * A dumb class which passes task events on to the {@link StatusService}.
//	 * Eventually, this sort of logic will be built in to SciJava Common. But for
//	 * the moment, we do it ourselves.
//	 */
//	private class StatusUpdater {
//		private final DecimalFormat formatter = new DecimalFormat("##.##");
//		private final Task task;
//
//		private long lastUpdate;
//
//		private StatusUpdater(final Task task) {
//			this.task = task;
//		}
//
//		public void update(final int value, final int max, final String message) {
//			final long timestamp = System.currentTimeMillis();
//			if (timestamp < lastUpdate + 100) return; // Avoid excessive updates.
//			lastUpdate = timestamp;
//
//			final double percent = 100.0 * value / max;
//			statusService.showStatus(value, max, message + ": " + //
//					formatter.format(percent) + "%");
//		}
//
//		public void clear() {
//			statusService.clearStatus();
//		}
//
//		@EventHandler
//		private void onEvent(final TaskEvent evt) {
//			if (task == evt.getTask()) {
//				final int value = (int) task.getProgressValue();
//				final int max = (int) task.getProgressMaximum();
//				final String message = task.getStatusMessage();
//				update(value, max, message);
//			}
//		}
//	}

}
