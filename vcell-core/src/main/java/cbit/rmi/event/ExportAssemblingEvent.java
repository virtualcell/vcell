package cbit.rmi.event;
import org.vcell.api.common.events.ExportEventRepresentation;
import org.vcell.api.common.events.ExportTimeSpecs;
import org.vcell.api.common.events.ExportVariableSpecs;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;

public class ExportAssemblingEvent extends ExportEvent {

    public ExportAssemblingEvent(Object source, long jobID, User user, VCDataIdentifier vcdID, String format){
        super(source, jobID, user, vcdID, ExportEvent.EXPORT_ASSEMBLING, format,
                null, null, null, null);
    }

    public ExportAssemblingEvent(Object source, long jobID, User user, String dataIdString,
                               KeyValue dataKey, String format){
        super(source, jobID, user, dataIdString, dataKey, ExportEvent.EXPORT_ASSEMBLING,
                format, null, null, null, null);
    }

    @Override
    public boolean isSupercededBy(MessageEvent messageEvent) {
        return false;
    }
}
