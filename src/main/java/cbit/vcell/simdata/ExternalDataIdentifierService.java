package cbit.vcell.simdata;

import java.util.HashMap;
import java.util.Vector;

import org.vcell.service.VCellService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.User;

public interface ExternalDataIdentifierService extends VCellService {
	HashMap<User, Vector<ExternalDataIdentifier>> getAllExternalDataIdentifiers() throws DataAccessException;
}
