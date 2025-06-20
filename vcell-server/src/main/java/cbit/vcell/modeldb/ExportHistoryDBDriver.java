package cbit.vcell.modeldb;

import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionableType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExportHistoryDBDriver{
    public static final ExportHistoryTable exportHistoryTable = ExportHistoryTable.table;
    public static final BioModelTable bioModelTable = BioModelTable.table;
    public static final PublicationTable publicationTable = PublicationTable.table;
    public static final UserTable userTable = UserTable.table;
    public static final BioModelSimulationLinkTable bioModelSimLinkTable = BioModelSimulationLinkTable.table;
    public static final BioModelSimContextLinkTable bioModelSimContextLinkTable = BioModelSimContextLinkTable.table;
    public static final SimulationTable simTable = SimulationTable.table;
    public static final SimContextTable simContextTable = SimContextTable.table;

    /**
     * LocalDBManager constructor comment.
     */
    public ExportHistoryDBDriver(DatabaseSyntax dbSyntax, KeyFactory keyFactory) {

    }


    public void addExportHistory(Connection con, User user, String values)
            throws SQLException, DependencyException, PermissionException, DataAccessException, ObjectNotFoundException {
        PreparedStatement statement = con.prepareStatement("INSERT INTO F VALUES (?, ?, ?, ?)");
        statement.setString(1, values);
        statement.execute();
    }
}
