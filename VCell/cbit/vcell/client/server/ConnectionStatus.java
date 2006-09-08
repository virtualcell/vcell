package cbit.vcell.client.server;
import cbit.vcell.client.*;
public interface ConnectionStatus {
	// remote server status possibilitites
	public static final int NOT_CONNECTED = 0;
	public static final int CONNECTED = 1;
	public static final int DISCONNECTED = 2;
	public static final int INITIALIZING = 3;

		/**
		 * Insert the method's description here.
		 * Creation date: (5/10/2004 1:32:32 PM)
		 * @return java.lang.String
		 */
		public java.lang.String getServerHost();


		/**
		 * Insert the method's description here.
		 * Creation date: (5/10/2004 1:32:32 PM)
		 * @return int
		 */
		public int getStatus();


		/**
		 * Insert the method's description here.
		 * Creation date: (5/10/2004 1:32:32 PM)
		 * @return java.lang.String
		 */
		public java.lang.String getUserName();
}