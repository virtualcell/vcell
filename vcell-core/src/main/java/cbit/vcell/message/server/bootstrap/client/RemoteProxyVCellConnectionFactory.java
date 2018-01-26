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

import org.apache.http.client.ClientProtocolException;
import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.VCellApiClient.RpcDestination;
import org.vcell.api.client.VCellApiRpcRequest;
import org.vcell.api.common.AccessTokenRepresentation;
import org.vcell.util.AuthenticationException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.VCellConnectionFactory;

public class RemoteProxyVCellConnectionFactory implements VCellConnectionFactory {

	private UserLoginInfo userLoginInfo;
	private final String apihost;
	private final Integer apiport;
	private final SessionLog sessionLog;
	private final VCellApiClient vcellApiClient;
	
	private RpcSender rpcSender = new RemoteProxyRpcSender();
	
	public class RemoteProxyRpcSender implements RpcSender {
		@Override
		public Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, int timeoutMS,
				String[] specialProperties, Object[] specialValues, UserLoginInfo userLoginInfo) throws Exception {
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
	this.sessionLog = new StdoutSessionLog("remoteProxy");
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
	return new LocalVCellConnectionMessaging(userLoginInfo,sessionLog,rpcSender);
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
