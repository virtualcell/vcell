package org.vcell.restq.services;

import cbit.vcell.modeldb.AdminDBTopLevel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.errors.exceptions.PermissionWebException;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.sql.SQLException;

@ApplicationScoped
public class AdminRestService {

    private final AdminDBTopLevel adminDBTopLevel;

    @Inject
    public AdminRestService(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException {
        try {
            adminDBTopLevel = new AdminDBTopLevel(agroalConnectionFactory);
        } catch (SQLException e) {
            throw new DataAccessException("database error during initialization", e);
        }
    }

    public String getUsageSummaryHtml(User vcUser) throws DataAccessException, SQLException, PermissionWebException {
        User.SpecialUser specialUser = adminDBTopLevel.getUser(vcUser.getName(), true);
        if (specialUser.isAdmin()){
            return adminDBTopLevel.getBasicStatistics();
        }else{
            throw new PermissionWebException("not authorized");
        }
    }

}
