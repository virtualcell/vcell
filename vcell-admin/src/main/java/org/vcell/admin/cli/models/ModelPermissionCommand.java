package org.vcell.admin.cli.models;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.admin.cli.CLIDatabaseService;
import org.vcell.util.document.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "acl", description = "add or remove user access to a biomodel or mathmodel")
public class ModelPermissionCommand implements Callable<Integer> {

    enum Operation {
        add,
        remove
    };

    private final static Logger logger = LogManager.getLogger(ModelPermissionCommand.class);

    @Option(names = "--biomodel-id", description = "id of single biomodel")
    private KeyValue biomodelID;

    @Option(names = "--mathmodel-id", description = "id of single mathmodel")
    private KeyValue mathmodelID;

    @Option(names = "--owner", required = true, description = "model owner (format is 'userid:key')")
    private User owner;

    @Option(names = "--user", required = true, description = "user to add/remove (format is 'userid:key')")
    private User user;

    @Option(names = "--operation", required = true, description = "operation to perform (add/remove)")
    private Operation operation;

    @Option(names = {"-d", "--debug"}, description = "full application debug mode")
    private boolean bDebug = false;

    @Option(names = {"-h", "--help"}, description = "show this help message and exit", usageHelp = true)
    private boolean help;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel();
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            
            if (biomodelID!=null && mathmodelID!=null) {
                throw new RuntimeException("cannot specify both --biomodel-id and --mathmodel-id");
            }

            if (biomodelID==null && mathmodelID==null) {
                throw new RuntimeException("must specify either --biomodel-id or --mathmodel-id");
            }

            try (CLIDatabaseService cliDatabaseService = new CLIDatabaseService()) {
                if (biomodelID != null){
                    BioModelInfo bioModelInfo = cliDatabaseService.queryBiomodelInfo(owner, biomodelID);
                    if (bioModelInfo == null) {
                        throw new RuntimeException("biomodel key "+biomodelID+" not found as a biomodel owned by "+owner);
                    }
                    GroupAccess oldGroupAccess = bioModelInfo.getVersion().getGroupAccess();
                    GroupAccess newGroupAccess = null;
                    if (operation == Operation.remove) {
                        newGroupAccess = cliDatabaseService.removeGroupAccess(bioModelInfo, user);
                    }else if (operation == Operation.add) {
                        newGroupAccess = cliDatabaseService.addGroupAccess(bioModelInfo, user);
                    }else{
                        throw new RuntimeException("unknown operation "+operation);
                    }
                    System.out.println("biomodel "+biomodelID+" group access changed from "+oldGroupAccess+" to "+newGroupAccess);
                }else if (mathmodelID != null) {
                    MathModelInfo mathModelInfo = cliDatabaseService.queryMathmodelInfo(owner, mathmodelID);
                    if (mathModelInfo == null) {
                        throw new RuntimeException("mathmodel key "+mathmodelID+" not found as a mathmodel owned by "+owner);
                    }
                    GroupAccess oldGroupAccess = mathModelInfo.getVersion().getGroupAccess();
                    GroupAccess newGroupAccess = null;
                    if (operation == Operation.remove) {
                        newGroupAccess = cliDatabaseService.removeGroupAccess(mathModelInfo, user);
                    }else if (operation == Operation.add) {
                        newGroupAccess = cliDatabaseService.addGroupAccess(mathModelInfo, user);
                    }else{
                        throw new RuntimeException("unknown operation "+operation);
                    }
                    System.out.println("mathmodel "+mathmodelID+" group access changed from "+oldGroupAccess+" to "+newGroupAccess);
                }
            }

            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            logger.debug("BioModel XML download completed");
        }
    }

}
