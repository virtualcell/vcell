

package org.vcell.imagej.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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
import org.scijava.command.CommandModule;
import org.scijava.event.EventHandler;
import org.scijava.io.ByteArrayByteBank;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.task.Task;
import org.scijava.task.TaskService;
import org.scijava.task.event.TaskEvent;
import org.scijava.util.FileUtils;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.vcellij.SimulationServiceImpl;
import org.vcell.vcellij.api.DomainType;
import org.vcell.vcellij.api.SimulationInfo;
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
import net.imagej.table.DefaultGenericTable;
import net.imagej.table.DoubleColumn;
import net.imagej.table.GenericColumn;
import net.imagej.table.GenericTable;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgView;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

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

	// FIXME: Grab these from the current selection of the image.
	// To do that, we need to use ImagePlus instead of RAI<T> for the image.
	@Parameter
	private double bleachX = 322;
	@Parameter
	private double bleachY = 241;
	@Parameter
	private double bleachWidth = 33;
	@Parameter
	private double bleachHeight = 21;
	// FIXME: Grab this from the active ImageDisplay's current time index.
	// Or from the ImagePlus's current time index -- whichever is easier.
	@Parameter
	private double bleachTimeIndex = 5;

	@Parameter(type = ItemIO.OUTPUT)
	private Object result;

	public Object getResult(){
		return result;
	}
	@Override
	public void run() {
		try {
			result = simulate();
		}
		catch (final Exception e) {
			log.error(e);
		}
		System.out.println("============= COMPLETE ===============");
	}

	public static void main(final String... args) throws IOException {
		final ImageJ ij = new ImageJ();
		ij.launch(args);

//		final String datasetPath = "http://imagej.net/images/bridge.gif";
//		final Dataset dataset = ij.scifio().datasetIO().open(datasetPath);
//		ij.ui().show("bridge", dataset);
//
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

	private Object simulate() throws Exception {
		initializeVCell();

//		if (!Intervals.equalDimensions(image, mask)) {
//			throw new IllegalArgumentException("Mask dimensions do not match image");
//		}
//
//		final RandomAccessibleInterval<T> goodImage;
//		final RandomAccessibleInterval<B> goodMask;
//		if (image.numDimensions() < 2 || image.numDimensions() > 3) {
//			throw new IllegalArgumentException("Image must be 2 or 3 dimensional");
//		}
//		if (image.numDimensions() == 2) {
//			log.warn("Projected image into the third dimension");
//			goodImage = Views.addDimension(image, 0, 2); // TODO: double check this; should be dim length 3
//			goodMask = Views.addDimension(mask, 0, 2); // TODO: double check this; should be dim length 3
//		}
//		else {
//			goodImage = image;
//			goodMask = mask;
//		}

		// Configure the model based on the inputs.
		final Model sbmlModel = readModel("ImageJ_FRAP.xml");//"frap.xml");
//		// NB: This model is 3D. We can feed in a 2D plane if we fake Z with the same data three times.
//		final SpatialModelPlugin smPlugin = (SpatialModelPlugin) sbmlModel.getPlugin(SBMLUtils.SBML_SPATIAL_NS_PREFIX);
//		final Geometry geo = smPlugin.getGeometry();
//
//		// TODO: When we have an ImgLabeling, we want to match the domain type ids with the labelings themselves.
//		// So e.g. "cell" maps to domainType "domainType_cell", etc.
//		// In this way, we can accept ImgLabeling that offer mapping of N classes and there is a 1-to-1 mapping.
//		ListOf<org.sbml.jsbml.ext.spatial.DomainType> domainTypes = geo.getListOfDomainTypes();
//
//		final Optional<GeometryDefinition> sfgDef = geo.getListOfGeometryDefinitions().stream().filter(geoDef -> geoDef instanceof SampledFieldGeometry).findFirst();
//		if (!sfgDef.isPresent()) throw new IllegalStateException("No sampled field geometry definition found");
//		SampledFieldGeometry sfg = (SampledFieldGeometry) sfgDef.get();
//		// E.g.: ["cell": 1.0, "ec": 2.0]
//		final Map<String, Double> labelValues = sfg.getListOfSampledVolumes().stream().collect(Collectors.toMap(SampledVolume::getId, SampledVolume::getSampledValue));
//		// TODO: use these label values; they should line up with an input LabelMapping<String>.
//
//		// Feed the image mask into the geometry's domain labeling.
//		ListOf<SampledField> sampledFields = geo.getListOfSampledFields();
//		if (sampledFields == null || sampledFields.isEmpty()) {
//			throw new IllegalStateException("Expected model with one sampled field");
//		}
//		final SampledField domainLabeling = sampledFields.get(0);
//		// FIXME: stop using a prior knowledge of cell and ec labels. Instead, we'll have a LabelingMapping which already knows what's what.
//		final Converter<B, UnsignedByteType> converter = (input, output) -> output.set(input.get() ? labelValues.get("cell").shortValue() : labelValues.get("ec").shortValue());
//		final RandomAccessibleInterval<UnsignedByteType> maskAsLabeling = Converters.convert(goodMask, converter, new UnsignedByteType());
//		populateField(domainLabeling, maskAsLabeling);
//
//		// Feed the image sample values into the geometry as a sampled field.
////		final SampledField sampledField = geo.createSampledField("fieldData");
////		populateField(sampledField, goodImage);

		// Start the simulation.
		final Sim sim = createSimulation(sbmlModel);

		// Wait for the simulation to complete.
		final StatusUpdater statusUpdater = new StatusUpdater(sim.task());
		statusService.context().inject(statusUpdater);
		sim.task().waitFor();
		statusUpdater.clear();

		return sim.image();
	}

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

	// -- Static helper methods --

	// TODO: Consider massaging these into methods of VCellService,
	// and/or a separate static utility class.

	private Sim createSimulation(final Model sbmlModel) throws Exception {
		// TODO: make SimulationServiceImpl a singleton of VCellService.
		final SimulationServiceImpl simService = new SimulationServiceImpl();
		final SimulationSpec simSpec = new SimulationSpec();
		simSpec.setOutputTimeStep(.1);
		simSpec.setTotalTime(5.0);
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
			public SimulationServiceImpl simService() {
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

	private void initializeVCell() {
		VCMongoMessage.enabled = false;
		System.setProperty(PropertyLoader.installationRoot, appService.getApp().getBaseDirectory().getAbsolutePath());
		ResourceUtil.setNativeLibraryDirectory();
//		NativeLoader.setNativeLibraryDirectory("/Users/curtis/code/vcell/vcell/nativelibs/mac64");
		NativeLib.HDF5.load();
	}

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

	public interface Sim {

		SimulationServiceImpl simService();

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
				IntervalView<DoubleType> view = Views.interval(Views.extendBorder(bigImage), new FinalInterval(xCount, 100, zCount, vCount, tCount));
				Img<DoubleType> img = ImgView.wrap(view, bigImage.factory());
				return new ImgPlus<>(img, "Simulated FRAP", new AxisType[] {Axes.X, Axes.Y, Axes.Z, Axes.get("variable"), Axes.TIME});
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

	/**
	 * A dumb class which passes task events on to the {@link StatusService}.
	 * Eventually, this sort of logic will be built in to SciJava Common. But for
	 * the moment, we do it ourselves.
	 */
	private class StatusUpdater {
		private final DecimalFormat formatter = new DecimalFormat("##.##");
		private final Task task;

		private long lastUpdate;

		private StatusUpdater(final Task task) {
			this.task = task;
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

}
