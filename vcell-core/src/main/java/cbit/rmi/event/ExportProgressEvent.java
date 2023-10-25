package cbit.rmi.event;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

public class ExportProgressEvent extends ExportEvent {

    public ExportProgressEvent(Object source, long jobID, User user, VCDataIdentifier vcdID, String format, Double progress){
        super(source, jobID, user, vcdID, ExportEvent.EXPORT_PROGRESS,
                format, null, progress, null, null);
    }

    public ExportProgressEvent(Object source, long jobID, User user, String dataIdString,
                               KeyValue dataKey, String format,  Double progress){
        super(source, jobID, user, dataIdString, dataKey, ExportEvent.EXPORT_PROGRESS,
                format, null, progress, null, null);
    }
    @Override
    public boolean isSupercededBy(MessageEvent messageEvent) {
        if (!(messageEvent instanceof ExportProgressEvent exportEvent)) return false;
        return this.getProgress() < exportEvent.getProgress();
    }
}
