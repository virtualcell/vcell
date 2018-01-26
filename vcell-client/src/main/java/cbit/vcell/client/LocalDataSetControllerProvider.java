/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import org.vcell.solver.nfsim.NFSimMolecularConfigurations;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;

import cbit.plot.PlotData;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory.RemoteProxyException;
import cbit.vcell.server.DataSetController;
import cbit.vcell.server.DataSetControllerProvider;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.DataSetMetadata;
import cbit.vcell.simdata.DataSetTimeSeries;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.ParticleDataBlock;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solvers.CartesianMesh;

public class LocalDataSetControllerProvider implements DataSetControllerProvider {

	class LocalDataSetReader implements DataSetController {
		private SessionLog log = null;
		private User user = null;
		private DataServerImpl dataServerImpl = null;

		public LocalDataSetReader(SessionLog log, User usr, DataSetControllerImpl dsci, ExportServiceImpl esi) {
			super();
			this.log = log;
			this.user = usr;
			this.dataServerImpl = new DataServerImpl(log, dsci, esi);
		}

		public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws DataAccessException {
			return dataServerImpl.fieldDataFileOperation(user,fieldDataFileOperationSpec);
		}

		public DataIdentifier[] getDataIdentifiers(OutputContext outputContext,	VCDataIdentifier vcdataID) throws DataAccessException {
			return dataServerImpl.getDataIdentifiers(outputContext, user, vcdataID);
			}

		public double[] getDataSetTimes(VCDataIdentifier vcdataID) throws DataAccessException {
			return dataServerImpl.getDataSetTimes(user, vcdataID);
		}

		public AnnotatedFunction[] getFunctions(OutputContext outputContext, VCDataIdentifier vcdataID) throws DataAccessException {
			return dataServerImpl.getFunctions(outputContext,user, vcdataID);
		}

		public PlotData getLineScan(OutputContext outputContext, VCDataIdentifier vcdataID, String varName, double time, SpatialSelection spatialSelection) throws DataAccessException {
			return dataServerImpl.getLineScan(outputContext, user, vcdataID, varName, time, spatialSelection);
			}

		public CartesianMesh getMesh(VCDataIdentifier vcdataID) throws DataAccessException {
			return dataServerImpl.getMesh(user, vcdataID);
			}

		public ODESimData getODEData(VCDataIdentifier vcdataID)	throws DataAccessException {
			return dataServerImpl.getODEData(user, vcdataID);
		}

		public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdataID, double time) throws DataAccessException {
			return dataServerImpl.getParticleDataBlock(user, vcdataID, time);
		}

		public DataOperationResults doDataOperation(DataOperation dataOperation) throws DataAccessException {
			return dataServerImpl.doDataOperation(user, dataOperation);	
		}

		public boolean getParticleDataExists(VCDataIdentifier vcdataID) throws DataAccessException {
			return dataServerImpl.getParticleDataExists(user, vcdataID);
			}

		public SimDataBlock getSimDataBlock(OutputContext outputContext, VCDataIdentifier vcdataID, String varName, double time) throws DataAccessException { 		
			return dataServerImpl.getSimDataBlock(outputContext, user, vcdataID, varName, time);		
			}

		public TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext, VCDataIdentifier vcdataID, TimeSeriesJobSpec timeSeriesJobSpec) throws DataAccessException {
			return dataServerImpl.getTimeSeriesValues(outputContext, user, vcdataID, timeSeriesJobSpec);
		}

		public ExportEvent makeRemoteFile(OutputContext outputContext, ExportSpecs exportSpecs) throws DataAccessException {
			return dataServerImpl.makeRemoteFile(outputContext,user, exportSpecs);
		}

		@Override
		public DataSetMetadata getDataSetMetadata(VCDataIdentifier vcdataID) throws DataAccessException, RemoteProxyException {
			return dataServerImpl.getDataSetMetadata(user, vcdataID);
		}

		@Override
		public DataSetTimeSeries getDataSetTimeSeries(VCDataIdentifier vcdataID, String[] variableNames) throws DataAccessException, RemoteProxyException {
			return dataServerImpl.getDataSetTimeSeries(user, vcdataID, variableNames);
		}

		@Override
		public VtuFileContainer getEmptyVtuMeshFiles(VCDataIdentifier vcdataID, int timeIndex) throws DataAccessException {
			return dataServerImpl.getEmptyVtuMeshFiles(user, vcdataID, timeIndex);
		}
		
		@Override
		public double[] getVtuMeshData(OutputContext outputContext, VCDataIdentifier vcdataID, VtuVarInfo var, double time) throws DataAccessException {
			return dataServerImpl.getVtuMeshData(user, outputContext, vcdataID, var, time);
		}

		@Override
		public VtuVarInfo[] getVtuVarInfos(OutputContext outputContext,	VCDataIdentifier vcdataID) throws DataAccessException {
			return dataServerImpl.getVtuVarInfos(user, outputContext, vcdataID);
		}

		@Override
		public double[] getVtuTimes(VCDataIdentifier vcdataID) throws RemoteProxyException, DataAccessException {
			return dataServerImpl.getVtuTimes(user, vcdataID);
		}
		
		@Override
		public NFSimMolecularConfigurations getNFSimMolecularConfigurations(VCDataIdentifier vcdataID) throws RemoteProxyException, DataAccessException {
			return dataServerImpl.getNFSimMolecularConfigurations(user, vcdataID);
		}

	}
	
	private SessionLog log = null;
	private User user = null;
	private DataSetControllerImpl dataSetControllerImpl = null;
	private ExportServiceImpl exportServiceImpl = null;
	
	public LocalDataSetControllerProvider(SessionLog log, User usr, DataSetControllerImpl dsci, ExportServiceImpl esi) {
		super();
		this.log = log;
		this.user = usr;
		this.dataSetControllerImpl = dsci;
		this.exportServiceImpl = esi;
	}

	public DataSetController getDataSetController() throws DataAccessException {
		LocalDataSetReader localDatasetReader = new LocalDataSetReader(log, user, dataSetControllerImpl, exportServiceImpl);
		return localDatasetReader;
	}

}

