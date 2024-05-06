package org.vcell.restq.models.admin;

import cbit.vcell.modeldb.AdminDBTopLevel;

import java.util.Arrays;

public record UsageSummary(
        UserSimCount[] simCounts_7Days,
        UserSimCount[] simCounts_30Days,
        UserSimCount[] simCounts_90Days,
        UserSimCount[] simCounts_180Days,
        UserSimCount[] simCounts_365Days,
        UsersRegisteredStats usersRegisteredStats,
        int totalUsers,
        int usersWithSims,
        int biomodelCount,
        int mathmodelCount,
        int publicBiomodelCount,
        int publicMathmodelCount,
        int simCount,
        int publicBiomodelSimCount,
        int publicMathmodelSimCount) {
    public static UsageSummary fromDbUsageSummary(AdminDBTopLevel.DbUsageSummary dbUsageSummary) {
        return new UsageSummary(
                Arrays.stream(dbUsageSummary.simCounts_7Days()).map(UserSimCount::fromDbUserSimCount).toArray(UserSimCount[]::new),
                Arrays.stream(dbUsageSummary.simCounts_30Days()).map(UserSimCount::fromDbUserSimCount).toArray(UserSimCount[]::new),
                Arrays.stream(dbUsageSummary.simCounts_90Days()).map(UserSimCount::fromDbUserSimCount).toArray(UserSimCount[]::new),
                Arrays.stream(dbUsageSummary.simCounts_180Days()).map(UserSimCount::fromDbUserSimCount).toArray(UserSimCount[]::new),
                Arrays.stream(dbUsageSummary.simCounts_365Days()).map(UserSimCount::fromDbUserSimCount).toArray(UserSimCount[]::new),
                UsersRegisteredStats.fromDbUsersRegisteredStats(dbUsageSummary.usersRegisteredStats()),
                dbUsageSummary.totalUsers(),
                dbUsageSummary.usersWithSims(),
                dbUsageSummary.biomodelCount(),
                dbUsageSummary.mathmodelCount(),
                dbUsageSummary.publicBiomodelCount(),
                dbUsageSummary.publicMathmodelCount(),
                dbUsageSummary.simCount(),
                dbUsageSummary.publicBiomodelSimCount(),
                dbUsageSummary.publicMathmodelSimCount());
    }
}

