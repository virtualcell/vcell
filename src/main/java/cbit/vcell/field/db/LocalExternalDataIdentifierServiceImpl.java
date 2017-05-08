package cbit.vcell.field.db;

import java.util.HashMap;
import java.util.Vector;

import org.scijava.service.AbstractService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.User;

import cbit.vcell.simdata.ExternalDataIdentifierService;

public class LocalExternalDataIdentifierServiceImpl extends AbstractService implements ExternalDataIdentifierService {

	@Override
	public HashMap<User, Vector<ExternalDataIdentifier>> getAllExternalDataIdentifiers() throws DataAccessException {
		return FieldDataDBOperationDriver.getAllExternalDataIdentifiers();
	}

}
