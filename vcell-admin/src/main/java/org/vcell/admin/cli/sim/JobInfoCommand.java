package org.vcell.admin.cli.sim;

import cbit.vcell.message.server.htc.HtcProxy;
import cbit.vcell.message.server.htc.slurm.SlurmProxy;
import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "job-info", description = "show job status and scheduler decisions for active jobs")
public class JobInfoCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(JobInfoCommand.class);

    @Option(names = {"--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

//    @Option(names = { "-o", "--outputDir"}, description = "directory where scan report is stored (default is current directory)", required = false)
//    private File outputDir;

    @Option(names = {"-s", "--simulation-id"}, description = "id of single simulation to process, (should be used with --username)")
    private KeyValue singleSimID;

    @Option(names = {"-u", "--username"}, description = "username to restrict scan")
    private String username;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            PropertyLoader.loadProperties();
            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
//                if (outputDir == null) {
//                    outputDir = new File(".");
//                } else if (!outputDir.exists()) {
//                    throw new RuntimeException("Output directory doesn't exist!");
//                }
                JobAdmin jobAdmin = cliDatabaseService.getJobAdmin();
                User[] quotaExemptUsers = jobAdmin.getQuotaExemptUsers();
                VCellServerID serverID = VCellServerID.getSystemServerID();

                HtcProxy htcProxy = SlurmProxy.createRemoteProxy();
                HtcProxy.PartitionStatistics partitionStatistics = htcProxy.getPartitionStatistics();
                int userQuotaOde = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.maxOdeJobsPerUser));
                int userQuotaPde = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.maxPdeJobsPerUser));

                jobAdmin.run(partitionStatistics, userQuotaOde, userQuotaPde, serverID, quotaExemptUsers, username, singleSimID);
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
