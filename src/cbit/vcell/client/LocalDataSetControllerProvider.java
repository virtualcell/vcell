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
import cbit.vcell.simdata.DataServerImpl;
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
		private User user = null;
		private DataServerImpl dataServerImpl = null;

		public LocalDataSetReader(SessionLog log, User usr, DataSetControllerImpl dsci, ExportServiceImpl esi) {
			super();
			this.log = log;
			this.user = usr;
			this.dataServerImpl = new DataServerImpl(log, dsci, esi);
		}

		public FieldDataFileOperationResults fieldDataFileOperation(FieldDataFileOperationSpec fieldDataFileOperationSpec) throws RemoteException, DataAccessException {
			return dataServerImpl.fieldDataFileOperation(user,fieldDataFileOperationSpec);
		}

		public DataIdentifier[] getDataIdentifiers(OutputContext outputContext,	VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
			return dataServerImpl.getDataIdentifiers(outputContext, user, vcdataID);
			}

		public double[] getDataSetTimes(VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
			return dataServerImpl.getDataSetTimes(user, vcdataID);
		}

		public AnnotatedFunction[] getFunctions(OutputContext outputContext, VCDataIdentifier vcdataID) throws DataAccessException,	RemoteException {
			return dataServerImpl.getFunctions(outputContext,user, vcdataID);
		}

		public PlotData getLineScan(OutputContext outputContext, VCDataIdentifier vcdataID, String varName, double time, SpatialSelection spatialSelection) throws RemoteException, DataAccessException {
			return dataServerImpl.getLineScan(outputContext, user, vcdataID, varName, time, spatialSelection);
			}

		public CartesianMesh getMesh(VCDataIdentifier vcdataID) throws RemoteException, DataAccessException {
			return dataServerImpl.getMesh(user, vcdataID);
			}

		public ODESimData getODEData(VCDataIdentifier vcdataID)	throws DataAccessException, RemoteException {
			return dataServerImpl.getODEData(user, vcdataID);
		}

		public ParticleDataBlock getParticleDataBlock(VCDataIdentifier vcdataID, double time) throws DataAccessException, RemoteException {
			return dataServerImpl.getParticleDataBlock(user, vcdataID, time);
		}

		public DataProcessingOutput getDataProcessingOutput(VCDataIdentifier vcdataID) throws DataAccessException, RemoteException {
			return dataServerImpl.getDataProcessingOutput(user, vcdataID);		}

		public boolean getParticleDataExists(VCDataIdentifier vcdataID) throws DataAccessException, RemoteException {
			return dataServerImpl.getParticleDataExists(user, vcdataID);
			}

		public SimDataBlock getSimDataBlock(OutputContext outputContext, VCDataIdentifier vcdataID, String varName, double time) throws RemoteException, DataAccessException { 		
			return dataServerImpl.getSimDataBlock(outputContext, user, vcdataID, varName, time);		
			}

		public TimeSeriesJobResults getTimeSeriesValues(OutputContext outputContext, VCDataIdentifier vcdataID, TimeSeriesJobSpec timeSeriesJobSpec) throws RemoteException, DataAccessException {
			return dataServerImpl.getTimeSeriesValues(outputContext, user, vcdataID, timeSeriesJobSpec);
		}

		public ExportEvent makeRemoteFile(OutputContext outputContext, ExportSpecs exportSpecs) throws DataAccessException, RemoteException {
			return dataServerImpl.makeRemoteFile(outputContext,user, exportSpecs);
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

