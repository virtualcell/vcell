package cbit.rmi.event;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

public class ExportStartEvent extends ExportEvent {
    public ExportStartEvent(Object source, long jobID, User user, VCDataIdentifier vcdID, String format){
        super(source, jobID, user, vcdID, ExportEvent.EXPORT_START, format,
                null, null, null, null);
    }

    public ExportStartEvent(Object source, long jobID, User user, String dataIdString,
                                 KeyValue dataKey, String format){
        super(source, jobID, user, dataIdString, dataKey, ExportEvent.EXPORT_START,
                format, null, null, null, null);
    }

    @Override
    public boolean isSupercededBy(MessageEvent messageEvent) {
        return false;
    }
}
