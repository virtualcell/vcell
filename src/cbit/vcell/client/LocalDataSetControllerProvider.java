package cbit.vcell.client;

import java.rmi.RemoteException;

import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.plot.PlotData;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.client.data.OutputContext;
import cbit.vcell.client.server.DataSetControllerProvider;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.FieldDataFileOperationResults;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.server.DataSetController;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.gui.SpatialSelection;
import cbit.vcell.solver.DataProcessingOutput;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solvers.CartesianMesh;

public class LocalDataSetControllerProvider implements DataSetControllerProvider {

	class LocalDataSetReader implements DataSetController {
		private SessionLog log = null;
		private DataSetControllerImpl dataSetControllerImpl = null;
		// private ExportServiceImpl exportServiceImpl = null;

		public LocalDataSetReader(SessionLog log, DataSetControllerImpl dsControllerImpl) {
			super();
			this.log = log;
			this.dataSetControllerImpl = dsControllerImpl;
			// exportServiceImpl = esl;
		}

		public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws RemoteException, DataAccessException {
			try {
				return dataSetControllerImpl.fieldDataFileOperation(fieldDataFileOperationSpec);
			}catch (DataAccessException e){
				log.exception(e);
				throw e;
			}catch (Exception e){
				log.exception(e);
				throw new DataAccessException("Error FieldDataFileOperation",e);
			}
		}

		public DataIdentifier[] getDataIdentifiers(OutputContext outputContext,	VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
			try {
				return dataSetControllerImpl.getDataIdentifiers(outputContext, vcdataID);
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public double[] getDataSetTimes(VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
			log.print("DataServerImpl.getDataSetTimes()");
			try {
				return dataSetControllerImpl.getDataSetTimes(vcdataID);
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public AnnotatedFunction[] getFunctions(OutputContext outputContext, VCDataIdentifier vcdataID) throws DataAccessException,	RemoteException {
			log.print("DataServerImpl.getFunctions()");
			try {
				return dataSetControllerImpl.getFunctions(outputContext,vcdataID);
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public PlotData getLineScan(OutputContext outputContext, VCDataIdentifier vcdataID, String varName, double time, SpatialSelection spatialSelection) throws RemoteException, DataAccessException {
			try {
				return dataSetControllerImpl.getLineScan(outputContext,vcdataID,varName,time,spatialSelection);
			} catch (DataAccessException e) {
				log.exception(e);
				throw e;
			} catch (Throwable e) {
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public CartesianMesh getMesh(VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
			try {
				return dataSetControllerImpl.getMesh(vcdataID);
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public ODESimData getODEData(VCDataIdentifier vcdataID)	throws DataAccessException, RemoteException {
			try {
				return dataSetControllerImpl.getODEDataBlock(vcdataID).getODESimData();
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdataID, double time) throws DataAccessException, RemoteException {
			try {
				return dataSetControllerImpl.getParticleDataBlock(vcdataID, time);
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public DataProcessingOutput getDataProcessingOutput(VCDataIdentifier vcdataID) throws DataAccessException, RemoteException {
			try {
				return dataSetControllerImpl.getDataProcessingOutput(vcdataID);
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public boolean getParticleDataExists(VCDataIdentifier vcdataID) throws DataAccessException, RemoteException {
			try {
				return dataSetControllerImpl.getParticleDataExists(vcdataID);
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public SimDataBlock getSimDataBlock(OutputContext outputContext, VCDataIdentifier vcdataID, String varName, double time) throws RemoteException, DataAccessException { 		
			try {
				return dataSetControllerImpl.getSimDataBlock(outputContext, vcdataID, varName, time);
			}catch (DataAccessException e){
				log.exception(e);
				throw (DataAccessException)e;
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext, VCDataIdentifier vcdataID, TimeSeriesJobSpec timeSeriesJobSpec) throws RemoteException, DataAccessException {
			try {
				return dataSetControllerImpl.getTimeSeriesValues(outputContext,vcdataID,timeSeriesJobSpec);
			}catch (Throwable e){
				log.exception(e);
				throw new DataAccessException(e.getMessage());
			}
		}

		public ExportEvent makeRemoteFile(OutputContext outputContext, ExportSpecs exportSpecs) throws DataAccessException, RemoteException {
			throw new RuntimeException("Cannot export results!");
		}
	}
	
	private SessionLog log = null;
	private DataSetControllerImpl dataSetControllerImpl = null;
	// private ExportServiceImpl exportServiceImpl = null;
	
	public LocalDataSetControllerProvider(SessionLog log, DataSetControllerImpl dataSetControllerImpl) {
		super();
		this.log = log;
		this.dataSetControllerImpl = dataSetControllerImpl;
		// this.exportServiceImpl = exportServiceImpl;
	}

	public DataSetController getDataSetController() throws DataAccessException {
		LocalDataSetReader localDatasetReader = new LocalDataSetReader(log, dataSetControllerImpl);
		return localDatasetReader;
	}

}

