/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.VCellApiClient.RpcDestination;
import org.vcell.api.client.VCellApiRpcRequest;
import org.vcell.api.common.AccessTokenRepresentation;
import org.vcell.api.common.events.DataJobEventRepresentation;
import org.vcell.api.common.events.EventWrapper;
import org.vcell.api.common.events.ExportEventRepresentation;
import org.vcell.api.common.events.SimulationJobStatusEventRepresentation;
import org.vcell.util.AuthenticationException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import com.google.gson.Gson;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.rmi.event.MessageEvent;
import cbit.rmi.event.SimulationJobStatusEvent;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.VCellConnectionFactory;

public class RemoteProxyVCellConnectionFactory implements VCellConnectionFactory {

	private static final List<String> vcellguestAllowed;
	//MUST keep sycnhronized with org.vcell.rest.rpc.RpcRestlet
	static {
		String[] temp =  new String[] {
				"getVCInfoContainer",
				"getBioModelXML",
				"getMathModelXML",
				"getSimulationStatus",
				"getParticleDataExists",
				"getMesh",
				"getDataSetTimes",
				"getDataIdentifiers",
				"getPreferences",
				"getSimDataBlock",
				"doDataOperation",//Read post processing data
				"getODEData",
				"getNFSimMolecularConfigurations",
		};
		Arrays.sort(temp);
		vcellguestAllowed = Collections.unmodifiableList(Arrays.asList(temp));
	}
	private UserLoginInfo userLoginInfo;
	private final String apihost;
	private final Integer apiport;
	private final VCellApiClient vcellApiClient;
	private final static AtomicLong lastProcessedEventTimestamp = new AtomicLong(0);
	private final static Logger lg = LogManager.getLogger(RemoteProxyVCellConnectionFactory.class);
	
	private RpcSender rpcSender = new RemoteProxyRpcSender();
	
	public class RemoteProxyRpcSender implements RpcSender {
		@Override
		public Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, int timeoutMS,
				String[] specialProperties, Object[] specialValues, UserLoginInfo userLoginInfo) throws Exception {
			if(User.isGuest(userLoginInfo.getUserName()) && !vcellguestAllowed.contains(vcRpcRequest.getMethodName())) {
				User.throwGuestException(vcRpcRequest.getMethodName());
			}
			final RpcDestination rpcDestination;
			if (queue.equals(VCellQueue.DataRequestQueue)) {
				rpcDestination = RpcDestination.DataRequestQueue;
			}else if (queue.equals(VCellQueue.DbRequestQueue)) {
				rpcDestination = RpcDestination.DbRequestQueue;
			}else if (queue.equals(VCellQueue.SimReqQueue)) {
				rpcDestination = RpcDestination.SimReqQueue;
			}else {
				throw new RuntimeException("RpcDestination "+queue.getName()+" not implemented for VCellApi RPC");
			}

			VCellApiRpcRequest apiRpcRequest = new VCellApiRpcRequest(
					userLoginInfo.getUserName(), 
					rpcDestination, vcRpcRequest.getMethodName(), vcRpcRequest.getArguments());
			return vcellApiClient.sendRpcMessage(rpcDestination,apiRpcRequest,returnRequired,timeoutMS,specialProperties,specialValues);
		}

		@Override
		public MessageEvent[] getMessageEvents() throws IOException {
			long previousTimestamp = lastProcessedEventTimestamp.get();
			long latestTimestapRetrieved = previousTimestamp;
			EventWrapper[] eventWrappers = vcellApiClient.getEvents(previousTimestamp);
			if (eventWrappers == null || eventWrappers.length==0) {
				return new MessageEvent[0];
			}
			for (EventWrapper eventWrapper : eventWrappers) {
				latestTimestapRetrieved = Math.max(eventWrapper.timestamp, latestTimestapRetrieved);
			}
			if (!lastProcessedEventTimestamp.compareAndSet(previousTimestamp, latestTimestapRetrieved)) {
				throw new RuntimeException("concurrent update failure of event timestamp - aborting processing of retrieved events");
			}

			ArrayList<MessageEvent> messageEvents = new ArrayList<MessageEvent>();
			Gson gson = new Gson();
			for (EventWrapper eventWrapper : eventWrappers) {
				if (lg.isTraceEnabled()) lg.trace("received event: ("+eventWrapper.id+", "+eventWrapper.timestamp+", "+eventWrapper.userid+", "+eventWrapper.eventJSON+")");
				switch (eventWrapper.eventType) {
				case SimJob:{
					SimulationJobStatusEventRepresentation simJobStatusEventRep = 
							gson.fromJson(eventWrapper.eventJSON, SimulationJobStatusEventRepresentation.class);
					SimulationJobStatusEvent simJobStatusEvent = SimulationJobStatusEvent.fromJsonRep(this, simJobStatusEventRep);
					messageEvents.add(simJobStatusEvent);
					break;
				}
				case ExportEvent:{
					ExportEventRepresentation exportEventRep = gson.fromJson(eventWrapper.eventJSON, ExportEventRepresentation.class);
					ExportEvent exportEvent = ExportEvent.fromJsonRep(this, exportEventRep);
					messageEvents.add(exportEvent);
					break;
				}
				case DataJob:{
					DataJobEventRepresentation dataJobEventRep = gson.fromJson(eventWrapper.eventJSON, DataJobEventRepresentation.class);
					DataJobEvent dataJobEvent = DataJobEvent.fromJsonRep(this, dataJobEventRep);
					messageEvents.add(dataJobEvent);
					break;
				}
				default:{
					throw new RuntimeException("unsupported event type: "+eventWrapper.eventType);
				}
				}
			}
			return messageEvents.toArray(new MessageEvent[0]);
		}
	}
	
	@SuppressWarnings("serial")
	public static class RemoteProxyException extends Exception {

		public RemoteProxyException(String message, Exception e) {
			super(message,e);
		}
		
	}

public RemoteProxyVCellConnectionFactory(String apihost, Integer apiport, UserLoginInfo userLoginInfo) throws ClientProtocolException, IOException {
	this.apihost = apihost;
	this.apiport = apiport;
	this.userLoginInfo = userLoginInfo;
	boolean bIgnoreCertProblems = true;
	boolean bIgnoreHostMismatch = true;
	try {
		this.vcellApiClient = new VCellApiClient(this.apihost, this.apiport, bIgnoreCertProblems, bIgnoreHostMismatch);
	} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
		e.printStackTrace();
		throw new RuntimeException("VCellApiClient configuration exception: "+e.getMessage(),e);
	}
	
	AccessTokenRepresentation accessTokenRep = this.vcellApiClient.authenticate(userLoginInfo.getUserName(), userLoginInfo.getDigestedPassword().getString(),true);
	userLoginInfo.setUser(new User(accessTokenRep.userId, new KeyValue(accessTokenRep.getUserKey())));
}

public void changeUser(UserLoginInfo userLoginInfo) {
	this.userLoginInfo = userLoginInfo;
}

public VCellConnection createVCellConnection() throws AuthenticationException, ConnectionException {
	return new LocalVCellConnectionMessaging(userLoginInfo,rpcSender);
}

public static String getVCellSoftwareVersion(String apihost, Integer apiport) {
	boolean bIgnoreCertProblems = true;
	boolean bIgnoreHostMismatch = true;
	try {
		VCellApiClient tempApiClient = new VCellApiClient(apihost, apiport, bIgnoreCertProblems, bIgnoreHostMismatch);
		String serverSoftwareVersion = tempApiClient.getServerSoftwareVersion();
		return serverSoftwareVersion;
	} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
		e.printStackTrace();
		throw new RuntimeException("VCellApiClient configuration exception: "+e.getMessage(),e);
	} catch (IOException e) {
		e.printStackTrace();
		throw new RuntimeException("VCellApiClient communication exception while retrieving server software version: "+e.getMessage(),e);
	}
}

public VCellApiClient getVCellApiClient() {
	return vcellApiClient;
}
}
