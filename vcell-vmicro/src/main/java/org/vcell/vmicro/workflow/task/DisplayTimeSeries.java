package org.vcell.vmicro.workflow.task;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.plot.PlotData;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.io.FieldData;
import cbit.vcell.field.io.FieldDataShape;
import cbit.vcell.field.io.FieldDataSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyException;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.DataSetControllerProvider;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solvers.CartesianMesh;
import org.vcell.solver.nfsim.NFSimMolecularConfigurations;
import org.vcell.util.*;
import org.vcell.util.document.*;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vmicro.op.display.DisplayTimeSeriesOp;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import javax.swing.*;
import java.awt.event.WindowListener;
import java.io.IOException;

public class DisplayTimeSeries extends Task {
	
	//
	// inputs
	//
	public final DataInput<ImageTimeSeries> imageTimeSeries;
	public final DataInput<String> title;
		
	//
	// outputs
	//
	public final DataOutput<Boolean> displayed;

	
	public DisplayTimeSeries(String id){
		super(id);
		imageTimeSeries = new DataInput<ImageTimeSeries>(ImageTimeSeries.class,"imageTimeSeries", this);
		title = new DataInput<String>(String.class,"title",this,true);
		displayed = new DataOutput<Boolean>(Boolean.class,"displayed",this);
		addInput(imageTimeSeries);
		addInput(title);
		addOutput(displayed);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		// set input
		ImageTimeSeries<Image> imageDataset = context.getData(imageTimeSeries);
		String titleString = "no title - not connected";
		titleString = context.getDataWithDefault(title,"no title");
		WindowListener listener = null;
		
		// do op
		DisplayTimeSeriesOp op = new DisplayTimeSeriesOp();
		op.displayImageTimeSeries(imageDataset, titleString, listener);
		
		// set output
		context.setData(displayed,true);
	}
	
	public static void displayImageTimeSeries(final ImageTimeSeries<Image> imageTimeSeries, String title, WindowListener windowListener) throws ImageException, IOException {
		

		ISize size = imageTimeSeries.getISize();
		int dimension = (size.getZ()>0)?(3):(2);
		Extent extent = imageTimeSeries.getExtent();
		Origin origin = imageTimeSeries.getAllImages()[0].getOrigin();
		double filterCutoffFrequency = 0.5; // don't care ... no surfaces
		VCImage vcImage = new VCImageUncompressed(null, new byte[size.getXYZ()], extent, size.getX(), size.getY(), size.getZ());
		RegionImage regionImage = new RegionImage(vcImage, dimension, extent, origin, filterCutoffFrequency);
		final CartesianMesh mesh = CartesianMesh.createSimpleCartesianMesh(origin,extent,size,regionImage);
		
		final DataIdentifier dataIdentifier = new DataIdentifier("var", VariableType.VOLUME, new Domain("domain"), false, "var");
		final DataSetController dataSetController = new DataSetController() {
			
			@Override
			public ExportEvent makeRemoteFile(OutputContext outputContext, ExportSpecs exportSpecs) throws DataAccessException,	RemoteProxyException {
				throw new RuntimeException("not yet implemented");
			}
			
			@Override
			public TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext, VCDataIdentifier vcdataID,	TimeSeriesJobSpec timeSeriesJobSpec) throws RemoteProxyException, DataAccessException {
				throw new RuntimeException("not yet implemented");
			}
			
			@Override
			public SimDataBlock getSimDataBlock(OutputContext outputContext, VCDataIdentifier vcdataID, String varName, double time) throws RemoteProxyException, DataAccessException {
				double timePoint = time;
				double[] timePoints = getDataSetTimes(vcdataID);
				int index=-1;
				for (int i=0;i<timePoints.length;i++){
					if (timePoint == timePoints[i]){
						index=i;
						break;
					}
				}
				double[] data = imageTimeSeries.getAllImages()[index].getDoublePixels();
				PDEDataInfo pdeDataInfo = new PDEDataInfo(null, null, varName, time, 0);
				VariableType varType = VariableType.VOLUME;
				return new SimDataBlock(pdeDataInfo, data, varType );
			}
			
			@Override
			public boolean getParticleDataExists(VCDataIdentifier vcdataID)	throws DataAccessException, RemoteProxyException {
				return false;
			}
			
			@Override
			public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdataID, double time) throws DataAccessException, RemoteProxyException {
				return null;
			}
			
			@Override
			public ODESimData getODEData(VCDataIdentifier vcdataID) throws DataAccessException, RemoteProxyException {
				return null;
			}
			
			@Override
			public CartesianMesh getMesh(VCDataIdentifier vcdataID) throws RemoteProxyException, DataAccessException {
				return mesh;
			}
			
			@Override
			public PlotData getLineScan(OutputContext outputContext, VCDataIdentifier vcdataID, String variable, double time, SpatialSelection spatialSelection) throws RemoteProxyException, DataAccessException {
				throw new RuntimeException("not yet implemented");
			}
			
			@Override
			public AnnotatedFunction[] getFunctions(OutputContext outputContext,VCDataIdentifier vcdataID) throws DataAccessException,	RemoteProxyException {
				return new AnnotatedFunction[0];
			}
			
			@Override
			public double[] getDataSetTimes(VCDataIdentifier vcdataID) throws RemoteProxyException, DataAccessException {
				return imageTimeSeries.getImageTimeStamps();
			}
			
			@Override
			public DataSetTimeSeries getDataSetTimeSeries(VCDataIdentifier vcdataID, String[] variableNames) throws DataAccessException, RemoteProxyException {
				throw new RuntimeException("not yet implemented");
			}
			
			@Override
			public DataSetMetadata getDataSetMetadata(VCDataIdentifier vcdataID) throws DataAccessException, RemoteProxyException {
				throw new RuntimeException("not yet implemented");
			}

			@Override
			public ExternalDataIdentifier analyzeAndCreateFieldData(FieldDataSpec fieldDataSpec) {
				return null;
			}

			@Override
			public ExternalDataIdentifier saveFieldData(FieldData fieldData) {
				return null;
			}

			@Override
			public void deleteFieldData(KeyValue fieldDataKey) {

			}

			@Override
			public FieldDataShape getFieldDataShape(KeyValue fieldDataKey) {
				return null;
			}

			@Override
			public DataIdentifier[] getDataIdentifiers(OutputContext outputContext,	VCDataIdentifier vcdataID) throws RemoteProxyException, DataAccessException {
				return new DataIdentifier[] { dataIdentifier };
			}
			
			@Override
			public DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException, RemoteProxyException {
				throw new RuntimeException("not yet implemented");
			}

			@Override
			public VtuFileContainer getEmptyVtuMeshFiles(VCDataIdentifier vcdataID, int timeIndex) throws RemoteProxyException, DataAccessException {
				throw new RuntimeException("not yet implemented");
			}

			@Override
			public double[] getVtuTimes(VCDataIdentifier vcdataID) throws RemoteProxyException, DataAccessException {
				throw new RuntimeException("not yet implemented");
			}

			@Override
			public double[] getVtuMeshData(OutputContext outputContext, VCDataIdentifier vcdataID,
					VtuVarInfo var, double time) throws RemoteProxyException,
					DataAccessException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public VtuVarInfo[] getVtuVarInfos(OutputContext outputContext,
					VCDataIdentifier vcDataIdentifier)
					throws DataAccessException, RemoteProxyException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public NFSimMolecularConfigurations getNFSimMolecularConfigurations(VCDataIdentifier vcdataID)
					throws RemoteProxyException, DataAccessException {
				// TODO Auto-generated method stub
				return null;
			}
		};
		DataSetControllerProvider dataSetControllerProvider = new DataSetControllerProvider() {
			
			@Override
			public DataSetController getDataSetController() throws DataAccessException {
				return dataSetController;
			}
		};
		VCDataManager vcDataManager = new VCDataManager(dataSetControllerProvider);
		OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
		VCDataIdentifier vcDataIdentifier = new VCDataIdentifier() {
			public User getOwner() {	return new User("nouser",null);		}
			public KeyValue getDataKey() { return null; }
			public String getID()  {	return "mydata";					}
		};
		PDEDataManager pdeDataManager = new PDEDataManager(outputContext, vcDataManager, vcDataIdentifier);
		ClientPDEDataContext myPdeDataContext = new ClientPDEDataContext(pdeDataManager);
		PDEDataViewer pdeDataViewer = new PDEDataViewer();

		JFrame jframe = new JFrame();
		jframe.setTitle(title);
		jframe.getContentPane().add(pdeDataViewer);
		jframe.setSize(1000,600);
		jframe.setVisible(true);
		if (windowListener!=null){
			jframe.addWindowListener(windowListener);
		}
		
		pdeDataViewer.setPdeDataContext(myPdeDataContext);
	}
	
}
