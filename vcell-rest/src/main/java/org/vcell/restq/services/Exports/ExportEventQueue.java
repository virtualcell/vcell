package org.vcell.restq.services.Exports;

import cbit.rmi.event.ExportEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.restq.handlers.AdminResource;
import org.vcell.util.document.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class ExportEventQueue {
    private static final Logger lg = LogManager.getLogger(ExportEventQueue.class);
    private final ConcurrentHashMap<User, ArrayList<MessageExportEvent>> exportEventQueue = new ConcurrentHashMap<>();

    public void addExportEvent(ExportEvent exportEvent) {
        User user = exportEvent.getUser();
        if (!exportEventQueue.containsKey(user)) {
            exportEventQueue.put(user, new ArrayList<>());
        }
        exportEventQueue.get(user).add(new MessageExportEvent(exportEvent, Instant.now()));
    }

    public List<MessageExportEvent> getExportEventsPastSpecificTime(User user, Instant time) {
        if (!exportEventQueue.containsKey(user)) {
            lg.trace("No export events for user {}", user.getName());
            return new ArrayList<>();
        }
        ArrayList<MessageExportEvent> events = exportEventQueue.get(user);
        lg.trace("All export events for user {}: {}", user.getName(), events);
        int rp = events.size() - 1;
        int lp = 0;
        int middle = rp / 2;
        while (lp < middle && rp > middle) {
            MessageExportEvent ce = events.get(middle);
            if (ce.timestamp.isBefore(time)) {
                lp = middle;
            } else {
                rp = middle - 1;
            }
            middle = lp + ((rp - lp) / 2);
        }
        if (middle == 0){
            return events;
        }
        return events.subList(middle, events.size());
    }

    record MessageExportEvent(
            ExportEvent exportEvent,
            Instant timestamp
    ){ }
}
