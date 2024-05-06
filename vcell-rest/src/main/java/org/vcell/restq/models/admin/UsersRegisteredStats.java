package org.vcell.restq.models.admin;

import cbit.vcell.modeldb.AdminDBTopLevel;

public record UsersRegisteredStats(int last1Week, int last1Month, int last3Months, int last6Months, int last12Months) {
    public static UsersRegisteredStats fromDbUsersRegisteredStats(AdminDBTopLevel.DbUsersRegisteredStats dbUsersRegisteredStats) {
        return new UsersRegisteredStats(
                dbUsersRegisteredStats.last1Week(),
                dbUsersRegisteredStats.last1Month(),
                dbUsersRegisteredStats.last3Months(),
                dbUsersRegisteredStats.last6Months(),
                dbUsersRegisteredStats.last12Months());
    }
}