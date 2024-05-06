package org.vcell.restq.models.admin;

import cbit.vcell.modeldb.AdminDBTopLevel;

public record UserSimCount(String username, int simCount) {
    public static UserSimCount fromDbUserSimCount(AdminDBTopLevel.DbUserSimCount dbUserSimCount) {
        return new UserSimCount(dbUserSimCount.username(), dbUserSimCount.simCount());
    }
}