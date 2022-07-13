package org.vcell.cli;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.server.SimulationJobStatusPersistent;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCInfoContainer;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class CLIDatabaseService implements AutoCloseable {
    private ConnectionFactory conFactory = null;

    public CLIDatabaseService() throws SQLException {
        conFactory = DatabaseService.getInstance().createConnectionFactory();
    }

    public List<BioModelInfo> queryPublicBioModels() throws SQLException, DataAccessException {
        AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
        VCInfoContainer vcic = adminDbTopLevel.getPublicOracleVCInfoContainer(false);
        return Arrays.asList(vcic.getBioModelInfos());
    }

    public SimulationJobStatusPersistent[] querySimulationJobStatus(KeyValue simKey) throws SQLException, DataAccessException {
        AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);
        SimulationJobStatusPersistent[] statuses = adminDbTopLevel.getSimulationJobStatusArray(simKey, false);
        return statuses;
    }

    public void close() throws SQLException {
        conFactory.close();
    }
}
