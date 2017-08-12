/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.server;
public interface ConnectionStatus {
	// remote server status possibilitites
	public static final int NOT_CONNECTED = 0;
	public static final int CONNECTED = 1;
	public static final int DISCONNECTED = 2;
	public static final int INITIALIZING = 3;

		public java.lang.String getServerHost();
		/**
		 * @return {@link #CONNECTED}, {@link #DISCONNECTED}, {@link #INITIALIZING} or {@link #NOT_CONNECTED}
		 */
		public int getStatus();
		public java.lang.String getUserName();

		/**
		 * @return reconnector
		 */
		public Reconnector getReconnector( );
}
