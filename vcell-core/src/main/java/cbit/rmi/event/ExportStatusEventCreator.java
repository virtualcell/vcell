package cbit.rmi.event;

import cbit.vcell.export.server.ExportSpecs;
import org.vcell.util.document.VCDataIdentifier;

public interface ExportStatusEventCreator {
    ExportEvent fireExportCompleted(long jobID, VCDataIdentifier vcdID, String format, String location, ExportSpecs exportSpecs);

    void addExportListener(ExportListener listener);

    void fireExportAssembling(long jobID, VCDataIdentifier vcdID, String format);

    void fireExportEvent(ExportEvent event);

    void fireExportFailed(long jobID, VCDataIdentifier vcdID, String format, String message);

    void fireExportProgress(long jobID, VCDataIdentifier vcdID, String format, double progress);

    void fireExportStarted(long jobID, VCDataIdentifier vcdID, String format) ;
}
