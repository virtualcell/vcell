package org.vcell.admin.cli.tools;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;

@Command(name = "users-query", description = "query vcell users")
public class UsersQueryCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(UsersQueryCommand.class);

    @Option(names = {"-o", "--outputFile"}, required = true)
    private File outputFilePath;

    @Option(names = {"-s", "--start"}, required = true, description = "start date for query (format: yyyy-MM-dd)")
    Date startDate;

    @Option(names = {"-n", "--notify"}, description = "filter users by notify=true")
    private boolean notify;

    @Option(names = {"-d", "--debug"}, required = false, defaultValue = "false", description = "full application debug mode")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel();

        LoggerContext config = (LoggerContext) (LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            PropertyLoader.loadProperties(new String[]{ PropertyLoader.dbDriverName, PropertyLoader.dbConnectURL,
                    PropertyLoader.dbUserid, PropertyLoader.dbPasswordValue});

            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                long pastTime_days = (new Date().getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
                System.out.println("pastTime_days: " + pastTime_days + " days since " + startDate);
                AdminDBTopLevel.DbUserSimCount[] userSimCounts = cliDatabaseService.getUserSimCount((int) pastTime_days);
                // filter out users by notify flag
                if (notify) {
                    userSimCounts = Arrays.stream(userSimCounts)
                            .filter(userSimCount -> userSimCount.notify_value() == AdminDBTopLevel.NotifyValue.on)
                            .toArray(AdminDBTopLevel.DbUserSimCount[]::new);
                }
                // sort by userid
                Arrays.sort(userSimCounts, (o1, o2) -> o1.userid().compareTo(o2.userid()));
                try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
                    // write out as a csv file
                    // header
                    fileWriter.write("userid,userkey,email,firstname,lastname,notify,simcount");
                    fileWriter.write("\n");
                    for (AdminDBTopLevel.DbUserSimCount userSimCount : userSimCounts) {
                        fileWriter.write(userSimCount.userid() + ",");
                        fileWriter.write(userSimCount.userkey().toString() + ",");
                        fileWriter.write(userSimCount.email() + ",");
                        fileWriter.write(userSimCount.firstname() + ",");
                        fileWriter.write(userSimCount.lastname() + ",");
                        fileWriter.write(userSimCount.notify_value() + ",");
                        fileWriter.write(userSimCount.simCount() + "");
                        fileWriter.write("\n");
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace(System.err);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
