package org.vcell.vmicro.op.display;

import java.awt.Component;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.vcell.client.logicalwindow.LWTopFrame;
import org.vcell.solver.nfsim.NFSimMolecularConfigurations;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vmicro.workflow.data.ImageTimeSeries;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.image.VCImageUncompressed;
import cbit.plot.PlotData;
import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.MessageEvent;
import cbit.vcell.VirtualMicroscopy.Image;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.DataViewerManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.RequestManagerAdapter;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.desktop.TopLevelWindow;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.DataSetControllerProvider;
import cbit.vcell.simdata.ClientPDEDataContext;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataListener;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataSetMetadata;
import cbit.vcell.simdata.DataSetTimeSeries;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataInfo;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solvers.CartesianMesh;

public class DisplayTimeSeriesOp {
	
	@SuppressWarnings("serial")
	public static class TopLevelFrame extends LWTopFrame implements TopLevelWindow {
		private final String menuDesc;
		TopLevelFrame( ) {
			menuDesc = LWTopFrame.nextSequentialDescription("Display Time Series");
		}
		
		@Override
		public String menuDescription() {
			return menuDesc; 
		}

		ChildWindowManager childWindowManager = new ChildWindowManager(this);

		@Override
		public TopLevelWindowManager getTopLevelWindowManager() {
			return null;
		}

		@Override
		public void updateConnectionStatus(ConnectionStatus connectionStatus) {
		}

		@Override
		public void updateMemoryStatus(long freeBytes, long totalBytes) {
		}

		@Override
		public void updateWhileInitializing(int i) {
		}

		@Override
		public ChildWindowManager getChildWindowManager() {
			return childWindowManager;
		}
		
	}
		
	public void displayImageTimeSeries(final ImageTimeSeries<? extends Image> imageTimeSeries, final String title, final WindowListener windowListener) throws ImageException, IOException {
		
		try {
			System.out.println("starting to prepare data for time series viewing");
			final PDEDataViewer pdeDataViewer = new PDEDataViewer();
			DataSetControllerProvider dataSetControllerProvider;
			try {
				dataSetControllerProvider = getDataSetControllerProvider(imageTimeSeries,pdeDataViewer);
			} catch (ImageException | IOException e1) {
				e1.printStackTrace();
				throw new RuntimeException(e1.getMessage(),e1);
			}
			VCDataManager vcDataManager = new VCDataManager(dataSetControllerProvider);
			OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
			final VCDataIdentifier vcDataIdentifier = new VCDataIdentifier() {
				public User getOwner() {	return new User("nouser",null);		}
				public String getID()  {	return "mydata";					}
			};
			PDEDataManager pdeDataManager = new PDEDataManager(outputContext, vcDataManager, vcDataIdentifier);
			final ClientPDEDataContext myPdeDataContext = new ClientPDEDataContext(pdeDataManager);

			final RequestManager requestManager = new RequestManagerAdapter(){
				
			};

			final DataViewerManager dataViewerManager = new DataViewerManager() {
				public void dataJobMessage(DataJobEvent event){
				}
				public void exportMessage(ExportEvent event){
				}
				public void addDataListener(DataListener newListener){
				}
				public UserPreferences getUserPreferences(){
					return null; // getRequestManager().getUserPreferences();
				}
				public void removeDataListener(DataListener newListener){
				}
				public void startExport(Component requester,OutputContext outputContext,ExportSpecs exportSpecs){
					//getLocalRequestManager().startExport(outputContext, FieldDataWindowManager.this, exportSpecs);
				}
				public void simStatusChanged(SimStatusEvent simStatusEvent) {
				}
				public User getUser() {
					return new User("dummy", new KeyValue("123"));
//				return getRequestManager().getDocumentManager().getUser();
				}
				public RequestManager getRequestManager() {
					return requestManager;
				}
			};

			System.out.println("ready to display time series");
			
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					JFrame jframe = new TopLevelFrame();
					jframe.setTitle(title);
					jframe.getContentPane().add(pdeDataViewer);
					jframe.setSize(1000,600);
					jframe.setVisible(true);
					if (windowListener!=null){
						jframe.addWindowListener(windowListener);
					}

					try {
						pdeDataViewer.setDataViewerManager(dataViewerManager);
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
					
					pdeDataViewer.setPdeDataContext(myPdeDataContext);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private DataSetControllerProvider getDataSetControllerProvider(final ImageTimeSeries<? extends Image> imageTimeSeries, final PDEDataViewer pdeDataViewer) throws ImageException, IOException {
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
				pdeDataViewer.dataJobMessage(new DataJobEvent(timeSeriesJobSpec.getVcDataJobID(), MessageEvent.DATA_START, vcdataID, new Double(0), null, null));
				if (!timeSeriesJobSpec.isCalcSpaceStats() && !timeSeriesJobSpec.isCalcTimeStats()){
					int[][] indices = timeSeriesJobSpec.getIndices();
					double[] timeStamps = imageTimeSeries.getImageTimeStamps();
					// [var][dataindex+1][timeindex]
					double[][][] dataValues = new double[1][indices[0].length+1][timeStamps.length];
					for (int timeIndex=0;timeIndex<timeStamps.length;timeIndex++){
						// index 0 is time
						dataValues[0][0][timeIndex] = timeStamps[timeIndex];
					}
					for (int timeIndex=0;timeIndex<timeStamps.length;timeIndex++){
						float[] pixelValues = imageTimeSeries.getAllImages()[timeIndex].getFloatPixels();
						for (int samplePointIndex=0;samplePointIndex<indices[0].length;samplePointIndex++){
							int pixelIndex = indices[0][samplePointIndex];
							dataValues[0][samplePointIndex+1][timeIndex] = pixelValues[pixelIndex];
						}
					}
					TSJobResultsNoStats timeSeriesJobResults = new TSJobResultsNoStats(new String[] { "var" }, indices, timeStamps, dataValues);
					pdeDataViewer.dataJobMessage(new DataJobEvent(timeSeriesJobSpec.getVcDataJobID(), MessageEvent.DATA_COMPLETE, vcdataID, new Double(0), timeSeriesJobResults, null));
					return timeSeriesJobResults;
				}
				return null;
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
			public DataIdentifier[] getDataIdentifiers(OutputContext outputContext,	VCDataIdentifier vcdataID) throws RemoteProxyException, DataAccessException {
				return new DataIdentifier[] { dataIdentifier };
			}
			
			@Override
			public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws RemoteProxyException, DataAccessException {
				throw new RuntimeException("not yet implemented");
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
		
		return dataSetControllerProvider;

	}
	
}
