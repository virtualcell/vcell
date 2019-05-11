package cbit.vcell.clientdb;

import org.vcell.util.DataAccessException;

public class ServerRejectedSaveException extends DataAccessException {
	public ServerRejectedSaveException(String message) {
		super(message);
	}
}
