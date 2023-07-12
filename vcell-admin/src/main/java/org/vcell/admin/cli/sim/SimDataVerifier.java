/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.admin.cli.sim;

import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationStateMachine;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * (1) Query database for simulations which satisfy criteria (included user, public/published, previously ran)
 * (2) Check for existence of simulation data for those simulations (try to read data)
 * (3) optionally submit/resubmit the simulations as the original user
 */
public class SimDataVerifier {
	private static final Logger lg = LogManager.getLogger(SimDataVerifier.class);

	private final AdminDBTopLevel adminDbTopLevel;
	private final DatabaseServerImpl dbServerImpl;
	private final SimulationDatabase simulationDatabase;

	public SimDataVerifier(AdminDBTopLevel adminDbTopLevel, DatabaseServerImpl dbServerImpl, SimulationDatabase simulationDatabase) {
		this.adminDbTopLevel = adminDbTopLevel;
		this.dbServerImpl = dbServerImpl;
		this.simulationDatabase = simulationDatabase;
	}

	public void run(File outputDir,
					boolean bRerunLostData, boolean bRunNeverRan, KeyValue singleSimID,
					String singleUsername, String startingUsername, boolean bIgnoreTestAccount,
					EnumSet<SimDataVerifierCommand.ModelVisibility> modelVisibilities
//					,String ampliCredName, String ampliCredPassword
					)
			throws SQLException, IOException, DataAccessException {

		File primaryDataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty));
		File secondaryDataRootDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirInternalProperty));
		if (primaryDataRootDir.equals(secondaryDataRootDir)) {
			secondaryDataRootDir = null;
		}
		long maxMemSize = 1000000;
		Cachetable cacheTable = new Cachetable(MessageConstants.MINUTE_IN_MS * 20, maxMemSize);
		DataSetControllerImpl dataSetController = new DataSetControllerImpl(cacheTable, primaryDataRootDir, secondaryDataRootDir);

		File reportFile = new File(outputDir, "report_"+System.currentTimeMillis()+".txt");
		try (BufferedWriter reportWriter = new BufferedWriter(new FileWriter(reportFile))) {
			//
			// determine the list of users to scan
			//
			List<User> usersToScan = getUsers(singleUsername, startingUsername, bIgnoreTestAccount, modelVisibilities);
			reportWriter.write(usersToScan.size()+" users to scan:\n");
			for (User user : usersToScan){
				reportWriter.write(user.getName()+"\n");
			}

			//
			// process one user at a time
			//
			for (User user : usersToScan) {
				processUser(bRerunLostData, bRunNeverRan, modelVisibilities, singleSimID,
						primaryDataRootDir, secondaryDataRootDir, user, dataSetController, reportWriter);
			}
		}
	}

	private List<User> getUsers(String singleUsername, String startingUsername, boolean bIgnoreTestAccount,
								EnumSet<SimDataVerifierCommand.ModelVisibility> modelVisibilities)
			throws DataAccessException, SQLException {

		if (singleUsername != null){
			User.SpecialUser user = adminDbTopLevel.getUser(singleUsername, true);
			if (user == null){
				throw new RuntimeException("failed to find user "+singleUsername);
			}
			return Arrays.asList(user);
		} else {
			Comparator<User> userComparator = Comparator.comparing((User u) -> u.getName().toLowerCase());
			if (modelVisibilities.contains(SimDataVerifierCommand.ModelVisibility.PRIVATE)) {
				lg.info("getting all users");
				List<User> allUsers = Arrays.stream(adminDbTopLevel.getUserInfos(true))
						.map(ui -> new User(ui.userid, ui.id)).collect(Collectors.toList());

				List<User> usersToScan = selectUserSubsetByName(startingUsername, bIgnoreTestAccount, userComparator, allUsers);
				return usersToScan;
			} else if (modelVisibilities.contains(SimDataVerifierCommand.ModelVisibility.PUBLIC) ||
						modelVisibilities.contains(SimDataVerifierCommand.ModelVisibility.PUBLISHED)) {

				BioModelInfo[] publicBioModelInfos = dbServerImpl.getBioModelInfos(User.tempUser, true);

				final Predicate<BioModelInfo> visibilityFilter;
				if (! modelVisibilities.contains(SimDataVerifierCommand.ModelVisibility.PUBLIC)){
					lg.info("getting all users with published BioModels");
					visibilityFilter = (bioModelInfo -> bioModelInfo.getPublicationInfos()!=null && bioModelInfo.getPublicationInfos().length > 0);
				}else {
					lg.info("getting all users with public BioModels");
					visibilityFilter = (bioModelInfo -> true);
				}

				Set<User> allUsersWithPublicModels = Arrays.stream(publicBioModelInfos)
						.filter(visibilityFilter)
						.map(bmi -> bmi.getVersion().getOwner()).collect(Collectors.toSet());

				List<User> usersToScan = selectUserSubsetByName(startingUsername,bIgnoreTestAccount,userComparator,allUsersWithPublicModels);
				return usersToScan;
			} else {
				throw new RuntimeException("no model visibility was specified");
			}
		}
	}

	private List<User> selectUserSubsetByName(String startingUsername, boolean bIgnoreTestAccount, Comparator<User> userComparator, Collection<User> allUsers) {
		List<User> usersToScan = new ArrayList<>();
		for (User user : allUsers) {
			if (bIgnoreTestAccount && user.getName().equals(User.VCellTestAccountName)) {
				continue;
			}
			if (startingUsername != null) { // accept all users starting with the "startingUser"
				if (user.getName().compareToIgnoreCase(startingUsername) >= 0) {
					usersToScan.add(user);
				}
			} else { // all users
				usersToScan.add(user);
			}
		}
		usersToScan.sort(userComparator);
		return usersToScan;
	}

	private void processUser(boolean bRerunLostData, boolean bRunNeverRan,
							 EnumSet<SimDataVerifierCommand.ModelVisibility> modelVisibilities, KeyValue singleSimID,
							 File primaryDataRootDir, File secondaryDataRootDir, User user,
							 DataSetControllerImpl dataSetController, Writer reportWriter)
			throws DataAccessException, SQLException, IOException {
		if (singleSimID != null){
			String msg = "processing user " + user + ", considering only simID " + singleSimID;
			lg.info(msg);
			reportWriter.write(msg + "\n");
			reportWriter.flush();
			processSimulation(bRerunLostData, bRunNeverRan, primaryDataRootDir, secondaryDataRootDir,
					user, singleSimID, dataSetController, reportWriter);
			reportWriter.flush();
		}else {
			//
			// get list of all simulations with the specified visibility including one or more of (PRIVATE, PUBLIC, PUBLISHED)
			//
			lg.info("processing user " + user);
			reportWriter.write("processing user " + user);
			List<BioModelInfo> bioModelInfos = getVisibleBioModels(modelVisibilities, user);
			Set<KeyValue> simulationKeys = new LinkedHashSet<>();
			for (BioModelInfo bmi : bioModelInfos) {
				KeyValue[] simKeys = simulationDatabase.getSimulationKeysFromBiomodel(bmi.getVersion().getVersionKey());
				simulationKeys.addAll(Arrays.asList(simKeys));
			}

			String msg = "processing user " + user + ", considering " + bioModelInfos.size() + " biomodels and " + simulationKeys.size() + " simulations";
			lg.info(msg);
			reportWriter.write(msg + "\n");
			reportWriter.flush();
			for (KeyValue simKey : simulationKeys) {
				processSimulation(bRerunLostData, bRunNeverRan, primaryDataRootDir, secondaryDataRootDir,
						user, simKey, dataSetController, reportWriter);
				reportWriter.flush();
			}
		}
	}

	private void processSimulation(boolean bRerunLostData, boolean bRunNeverRan,
								   File primaryDataRootDir, File secondaryDataRootDir, User user,
								   KeyValue simKey,
								   DataSetControllerImpl dataSetController,
								   Writer reportWriter)
			throws DataAccessException, SQLException, IOException {

		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, user);
		SimulationRep simulationRep = dbServerImpl.getSimulationRep(simKey);
		//
		// get existing simulation status for this simid and latest taskID
		//
		SimulationStatus simulationStatus = null;
		try {
			simulationStatus = simulationDatabase.getSimulationStatus(simKey);
		}catch (ObjectNotFoundException e){
		}

		if (simulationStatus != null && simulationStatus.getHasData()) {
			// find data on disk
			boolean bAnyDataMissing = false;
			for (int jobIndex = 0; jobIndex < simulationRep.getScanCount(); jobIndex++) {
				try {
					VCDataIdentifier vcDataIdentifier = new VCSimulationDataIdentifier(vcSimID, jobIndex);
					double[] times = dataSetController.getDataSetTimes(vcDataIdentifier);
				} catch (Exception e) {
					String msg = "failed to read or not found: simID='" + vcSimID + ", jobIndex=" + jobIndex+": " + e.getMessage();
					lg.error(msg);
					bAnyDataMissing = true;
				}
			}

			if (bAnyDataMissing) {
				String msg = "data missing for " + vcSimID;
				lg.info(msg);
				reportWriter.write(msg+"\n");
				if (bRerunLostData){
					if (simulationStatus.isActive()){
						msg = "simulation " + vcSimID + " is already active, don't resubmit";
						lg.info(msg);
						reportWriter.write(msg+"\n");
					}else {
						try {
							for (int jobIndex = 0; jobIndex < simulationRep.getScanCount(); jobIndex++) {
								SimulationJobStatus simJobStatus = SimulationStateMachine.saveSimulationStartRequest(vcSimID, jobIndex, simulationDatabase);
								msg = "submitted job: " + simJobStatus;
								lg.info(msg);
								reportWriter.write(msg + "\n");
							}
						} catch (Exception e) {
							msg = "failed to request start for simulation " + vcSimID + ": " + e.getMessage();
							lg.error(msg, e);
							reportWriter.write(msg + "\n");
						}
					}
				}
			} else {
				String msg = "all data found for " + vcSimID;
				lg.info(msg);
				reportWriter.write(msg+"\n");
			}
		}

		if (simulationStatus == null) {
			String msg = "sim never ran: " + vcSimID;
			lg.info(msg);
			reportWriter.write(msg+"\n");
			if (bRunNeverRan) {
				try {
					for (int jobIndex = 0; jobIndex < simulationRep.getScanCount(); jobIndex++) {
						SimulationJobStatus simJobStatus = SimulationStateMachine.saveSimulationStartRequest(vcSimID, jobIndex, simulationDatabase);
						msg = "submitted job: " + simJobStatus;
						lg.info(msg);
						reportWriter.write(msg+"\n");
					}
				} catch (Exception e) {
					msg = "failed to request start for simulation " + vcSimID + ": " + e.getMessage();
					lg.error(msg, e);
					reportWriter.write(msg+"\n");
				}
			}
		}
	}

	private List<BioModelInfo> getVisibleBioModels(EnumSet<SimDataVerifierCommand.ModelVisibility> modelVisibilities,
											User user) throws DataAccessException {

		BioModelInfo[] bioModelInfos = dbServerImpl.getBioModelInfos(user, false);
		List<BioModelInfo> visibleBioModelInfos = new ArrayList<>();
		for (BioModelInfo bmi : bioModelInfos) {
			GroupAccess groupAccess = bmi.getVersion().getGroupAccess();
			boolean bPublished = false;
			if (bmi.getVersion().getVersionKey().equals(bmi.getVersion().getVersionKey())
					&& bmi.getPublicationInfos() != null && bmi.getPublicationInfos().length > 0) {
				bPublished = true;
			}
			boolean bKeepThisBiomodel = false;
			if (groupAccess instanceof GroupAccessAll && bPublished
					&& modelVisibilities.contains(SimDataVerifierCommand.ModelVisibility.PUBLISHED)) {
				bKeepThisBiomodel = true;
			}
			if (groupAccess instanceof GroupAccessAll && !bPublished
					&& modelVisibilities.contains(SimDataVerifierCommand.ModelVisibility.PUBLIC)) {
				bKeepThisBiomodel = true;
			}
			if ((groupAccess instanceof GroupAccessSome || groupAccess instanceof GroupAccessNone)
					&& modelVisibilities.contains(SimDataVerifierCommand.ModelVisibility.PRIVATE)) {
				bKeepThisBiomodel = true;
			}
			if (bKeepThisBiomodel) {
				visibleBioModelInfos.add(bmi);
			}
		}
		return visibleBioModelInfos;
	}


}
