package cbit.sql;

import cbit.vcell.modeldb.AdminDBTopLevel;
import org.vcell.db.ConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.sql.SQLException;

public class ServerStartUpTasks {
    private static String vcellSupportID = null;


    public static void executeStartUpTasks(ConnectionFactory connectionFactory) throws SQLException, DataAccessException {
        AdminDBTopLevel adminDBTopLevel = new AdminDBTopLevel(connectionFactory);
        User user = adminDBTopLevel.getVCellSupportUser(true);
        vcellSupportID = user.getID().toString();
    }

    public static String getVCellSupportID() {
        return vcellSupportID;
    }

}
