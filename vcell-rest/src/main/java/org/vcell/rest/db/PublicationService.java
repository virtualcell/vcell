package org.vcell.rest.db;

import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DatabaseServerImpl.OrderBy;
import cbit.vcell.modeldb.PublicationRep;
import cbit.vcell.modeldb.PublicationTable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;
import java.util.ArrayList;

@ApplicationScoped
public class PublicationService {

	private final DatabaseServerImpl databaseServerImpl;

	@Inject
	public PublicationService(OracleAgroalConnectionFactory connectionFactory) throws DataAccessException {
		this.databaseServerImpl = new DatabaseServerImpl(connectionFactory, connectionFactory.getKeyFactory());
	}

	public void publishDirectly(KeyValue[] publishTheseBiomodels, KeyValue[] publishTheseMathmodels, User user) throws SQLException, DataAccessException {
		databaseServerImpl.publishDirectly(publishTheseBiomodels, publishTheseMathmodels, user);
	}

	public KeyValue savePublicationRep(PublicationRep publicationRep, User vcellUser) throws SQLException, DataAccessException{
		return databaseServerImpl.savePublicationRep(publicationRep,vcellUser);
	}

	public PublicationRep getPublicationRep(KeyValue pubKey, User vcellUser) throws SQLException, DataAccessException {
		if (pubKey == null){
			throw new RuntimeException("publication key not specified");
		}
		ArrayList<String> conditions = new ArrayList<>();
		conditions.add("(" + PublicationTable.table.id.getQualifiedColName() + " = " + pubKey + ")");
		String conditionsString = String.join(" AND ", conditions);

		PublicationRep[] publicationReps = databaseServerImpl.getPublicationReps(vcellUser, conditionsString, null);
		if (publicationReps==null || publicationReps.length!=1){
			throw new ObjectNotFoundException("failed to get publication");
		}
		return publicationReps[0];
	}

	public PublicationRep[] getPublicationReps(OrderBy orderBy, User vcellUser) throws SQLException, DataAccessException {
		String conditionsString = "";
        return databaseServerImpl.getPublicationReps(vcellUser, conditionsString, orderBy);
	}

}
