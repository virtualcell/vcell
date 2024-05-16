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

import cbit.rmi.event.*;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.VCellConnectionFactory;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nimbusds.oauth2.sdk.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.DependencyConstants;
import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.VCellApiClient.RpcDestination;
import org.vcell.api.client.VCellApiRpcRequest;
import org.vcell.api.common.AccessTokenRepresentation;
import org.vcell.api.common.events.*;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.model.AccesTokenRepresentationRecord;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

	private final String apihost;
	private final Integer apiport;
	private final String pathPrefix_v0;
	private final VCellApiClient vcellApiClient;
	private final static AtomicLong lastProcessedEventTimestamp = new AtomicLong(0);
	private final static Logger lg = LogManager.getLogger(RemoteProxyVCellConnectionFactory.class);

	private RpcSender rpcSender = new RemoteProxyRpcSender();
	
	public class RemoteProxyRpcSender implements RpcSender {
		@Override
		public Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, int timeoutMS,
				String[] specialProperties, Object[] specialValues, UserLoginInfo userLoginInfo) throws Exception {
			if(User.isGuest(userLoginInfo.getUserName()) && !vcellguestAllowed.contains(vcRpcRequest.getMethodName())) {
				throw new IllegalArgumentException(User.createGuestErrorMessage(vcRpcRequest.getMethodName()));
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
				case Broadcast:{
					BroadcastEventRepresentation broadcastEventRepresentation = gson.fromJson(eventWrapper.eventJSON, BroadcastEventRepresentation.class);
					VCellMessageEvent vcMessageEvent = new VCellMessageEvent(new Object(), System.currentTimeMillis()+"", new MessageData(broadcastEventRepresentation.message),VCellMessageEvent.VCELL_MESSAGEEVENT_TYPE_BROADCAST , null);
					messageEvents.add(vcMessageEvent);
					break;
				}
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

	@Inject
	public RemoteProxyVCellConnectionFactory(
			@Named(DependencyConstants.VCELL_API_HOST) String apihost,
		  	@Named(DependencyConstants.VCELL_API_PORT) Integer apiport,
		  	@Named(DependencyConstants.VCELL_API_PATH_PREFIX_V0) String pathPrefix_v0) {
		this(apihost, apiport, pathPrefix_v0, apihost, apiport, "/api/v1");
	}

	@Inject
	public RemoteProxyVCellConnectionFactory(
			@Named(DependencyConstants.VCELL_API_HOST) String apihost,
			@Named(DependencyConstants.VCELL_API_PORT) Integer apiport,
			@Named(DependencyConstants.VCELL_API_PATH_PREFIX_V0) String pathPrefix_v0,
			@Named(DependencyConstants.VCELL_QUARKUS_API_HOST) String quarkusApiHost,
			@Named(DependencyConstants.VCELL_QUARKUS_API_PORT) Integer quarkusApiPort,
			@Named(DependencyConstants.VCELL_API_PATH_PREFIX_V1) String pathPrefix_v1) {
		this.apihost = apihost;
		this.apiport = apiport;
		this.pathPrefix_v0 = pathPrefix_v0;
		boolean bIgnoreCertProblems = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems,false);
		boolean bIgnoreHostMismatch = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreHostMismatch,false);;
		boolean isHTTP = PropertyLoader.getBooleanProperty(PropertyLoader.isHTTP,false);
		try {
			this.vcellApiClient = new VCellApiClient(this.apihost, this.apiport, this.pathPrefix_v0,
					quarkusApiHost, quarkusApiPort, pathPrefix_v1,
					isHTTP, bIgnoreCertProblems, bIgnoreHostMismatch);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException("VCellApiClient configuration exception: "+e.getMessage(),e);
		}
	}

public VCellConnection createVCellConnection(UserLoginInfo userLoginInfo) throws ConnectionException {
	try {
		AccessTokenRepresentation accessTokenRep = this.vcellApiClient.authenticate(userLoginInfo.getUserName(), userLoginInfo.getDigestedPassword().getString(),true);
		userLoginInfo.setUser(new User(accessTokenRep.userId, new KeyValue(accessTokenRep.getUserKey())));
		return new LocalVCellConnectionMessaging(userLoginInfo,rpcSender);
	} catch (IOException e) {
		throw new ConnectionException("failed to connect: "+e.getMessage(), e);
	}
}

	@Override
	public VCellConnection createVCellConnectionAuth0(UserLoginInfo userLoginInfo) {
		try {
			AccesTokenRepresentationRecord accessTokenRep = this.getVCellApiClient().getLegacyToken();
			userLoginInfo.setUser(new User(accessTokenRep.getUserId(), new KeyValue(accessTokenRep.getUserKey())));
			return new LocalVCellConnectionMessaging(userLoginInfo,rpcSender);
		} catch (ApiException apiException){
			throw new RuntimeException(apiException);
		}
//		return null;
	}

	@Override
	public boolean isVCellIdentityMappedToAuth0Identity() {
		try{
			return vcellApiClient.isVCellIdentityMapped();
		} catch (ApiException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void mapVCellIdentityToAuth0Identity(UserLoginInfo userLoginInfo) {
		try{
			vcellApiClient.mapUserToAuth0(userLoginInfo.getUserName(), userLoginInfo.getDigestedPassword().getClearTextPassword());
		} catch (ApiException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void auth0SignIn() {
		try{
			vcellApiClient.authenticateWithAuth0();
		} catch (ApiException | URISyntaxException | IOException | ParseException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getAuth0MappedUser() {
        try {
            return vcellApiClient.getVCellUserNameFromAuth0Mapping();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

	public static String getVCellSoftwareVersion(String apihost, Integer apiport, String pathPrefix_v0) {
	boolean bIgnoreCertProblems = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems,false);
	boolean bIgnoreHostMismatch = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreHostMismatch,false);;
	boolean isHTTP = PropertyLoader.getBooleanProperty(PropertyLoader.isHTTP,false);
	try {
		VCellApiClient tempApiClient = new VCellApiClient(apihost, apiport, pathPrefix_v0, isHTTP, bIgnoreCertProblems, bIgnoreHostMismatch);
		String serverSoftwareVersion = tempApiClient.getServerSoftwareVersion();
		return serverSoftwareVersion;
	} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
		throw new RuntimeException("VCellApiClient configuration exception: "+e.getMessage(),e);
	} catch (IOException e) {
		throw new RuntimeException("VCellApiClient communication exception while retrieving server software version: "+e.getMessage(),e);
	}
}

public VCellApiClient getVCellApiClient() {
	return vcellApiClient;
}
}
