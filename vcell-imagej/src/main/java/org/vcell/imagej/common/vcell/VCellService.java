
package org.vcell.imagej.common.vcell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.xml.stax.SBMLReader;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.vcell.imagej.app.Datasets;
import org.vcell.imagej.common.gui.Task;
import org.vcell.vcellij.SimulationServiceImpl;
import org.vcell.vcellij.api.SBMLModel;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationSpec;
import org.vcell.vcellij.api.SimulationState;
import org.vcell.vcellij.api.VariableInfo;

import cbit.vcell.client.pyvcellproxy.ThriftDataAccessException;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * Created by kevingaffney on 7/10/17.
 */
@Plugin(type = Service.class)
public class VCellService extends AbstractService {

	@Parameter
	private OpService opService;

	@Parameter
	private DatasetService datasetService;

	public Task<List<Dataset>, SimulationState> runSimulation(
		final VCellModel model, final SimulationSpec simSpec,
		final List<Species> outputSpecies,
		final boolean shouldCreateIndividualDatasets)
	{

		try {
			final SimulationServiceImpl client = new SimulationServiceImpl();
			final Task<List<Dataset>, SimulationState> task = runSimulation(client,
				model, simSpec, outputSpecies, shouldCreateIndividualDatasets,
				opService, datasetService);
			return task;
		}
		catch (final Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public SBMLDocument getSBML(final String vcml, final String applicationName) {
		try {
			final SimulationServiceImpl client = setupClient();
			final SBMLDocument document = getSBML(client, vcml, applicationName);
			return document;
		}
		catch (final Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private SimulationServiceImpl setupClient() {
		final SimulationServiceImpl client = new SimulationServiceImpl();
		return client;
	}

	private static SBMLDocument getSBML(final SimulationServiceImpl client,
		final String vcml, final String applicationName)
		throws ThriftDataAccessException, XMLStreamException, IOException
	{
		String sbml;
		try {
			sbml = client.getSBML(vcml, applicationName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(),e);
		}
		final SBMLReader reader = new SBMLReader();
		final SBMLDocument document = reader.readSBMLFromString(sbml);
		return document;
	}

	private static Task<List<Dataset>, SimulationState> runSimulation(
		final SimulationServiceImpl client, final VCellModel vCellModel,
		final SimulationSpec simSpec, final List<Species> outputSpecies,
		final boolean shouldCreateIndividualDatasets, final OpService opService,
		final DatasetService datasetService) throws IOException,
		XMLStreamException
	{

		final Task<List<Dataset>, SimulationState> task =
			new Task<List<Dataset>, SimulationState>()
		{

				@Override
				protected List<Dataset> doInBackground() throws Exception {

					setSubtask(SimulationState.notRun);

					final File sbmlSpatialFile = new File(vCellModel.getName() + ".xml");
					new SBMLWriter().write(vCellModel.getSbmlDocument(), sbmlSpatialFile);

					final SBMLModel model = new SBMLModel(sbmlSpatialFile);
					final SimulationInfo simulationInfo = client.computeModel(model,
						simSpec);
					try {
						Thread.sleep(500);
					}
					catch (final InterruptedException e) {
						e.printStackTrace();
					}
					setSubtask(SimulationState.running);
					while (client.getStatus(simulationInfo)
						.getSimState() == SimulationState.running)
				{
						System.out.println("waiting for simulation results");
						try {
							Thread.sleep(500);
						}
						catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}

					if (client.getStatus(simulationInfo)
						.getSimState() == SimulationState.failed)
				{
						setSubtask(SimulationState.failed);
						return null;
					}

					final List<Dataset> results = new ArrayList<>();
					final List<VariableInfo> vars = client.getVariableList(
						simulationInfo);
					final List<Double> times = client.getTimePoints(simulationInfo);

					for (final VariableInfo var : vars) {

						if (outputSpecies.stream().anyMatch(species -> species.getId()
							.equals(var.getVariableVtuName())))
					{

							// Get data for first time point and determine dimensions
							List<Double> data = client.getData(simulationInfo, var, 0);
							final int[] dimensions = getDimensions(data, times);
							final Img<DoubleType> img = opService.create().img(dimensions);
							final RandomAccess<DoubleType> imgRA = img.randomAccess();

							// Copy data to the ImgLib2 Img
							for (int t = 0; t < times.size(); t++) {
								data = client.getData(simulationInfo, var, t);
								for (int d = 0; d < data.size(); d++) {
									imgRA.setPosition(new int[] { d, t });
									imgRA.get().set(data.get(d));
								}
							}

							// Create ImageJ Dataset and add to results
							final Dataset dataset = datasetService.create(img);
							dataset.setName(var.getVariableVtuName());
							results.add(dataset);
						}
					}

					// If desired, add all datasets with the same dimensions
					if (!shouldCreateIndividualDatasets && !results.isEmpty()) {

						// First, group datasets according to dimensions
						final List<List<Dataset>> datasetGroups = new ArrayList<>();
						final List<Dataset> initialGroup = new ArrayList<>();
						initialGroup.add(results.get(0));
						datasetGroups.add(initialGroup);

						for (int i = 1; i < results.size(); i++) {
							final Dataset result = results.get(i);
							for (final List<Dataset> datasetGroup : datasetGroups) {
								final Dataset[] datasets = new Dataset[] { datasetGroup.get(0),
									result };
								if (Datasets.areSameSize(datasets, 0, 1)) {
									datasetGroup.add(result);
								}
								else {
									final List<Dataset> newGroup = new ArrayList<>();
									newGroup.add(result);
									datasetGroups.add(newGroup);
								}
							}
						}

						final List<Dataset> summedResults = new ArrayList<>();
						for (final List<Dataset> datasetGroup : datasetGroups) {
							final Img<DoubleType> sum = opService.create().img(datasetGroup
								.get(0));
							for (final Dataset dataset : datasetGroup) {
								@SuppressWarnings("unchecked")
								final RandomAccessibleInterval<DoubleType> current =
									(Img<DoubleType>) dataset.getImgPlus().getImg();
								opService.math().add(sum, sum, current);
							}
							final Dataset result = datasetService.create(sum);
							result.setName(datasetGroup.stream().map(d -> d.getName())
								.collect(Collectors.joining("+")));
							summedResults.add(result);
						}
						return summedResults;
					}

					setSubtask(SimulationState.done);
					return results;
				}
			};

		return task;
	}

	private static int[] getDimensions(final List<Double> data,
		final List<Double> times)
	{
		int[] dimensions = null;
		if (data.size() == 1) {
			dimensions = new int[] { times.size() };
		}
		else {
			dimensions = new int[] { data.size(), times.size() };
		}
		return dimensions;
	}
}
