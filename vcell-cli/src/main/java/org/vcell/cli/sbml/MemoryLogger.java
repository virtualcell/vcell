package org.vcell.cli.sbml;

import cbit.util.xml.VCLogger;

import java.util.ArrayList;
import java.util.List;

public class MemoryLogger extends VCLogger {
    public final List<String> highPriority = new ArrayList<>();
    public final List<String> medPriority = new ArrayList<>();
    public final List<String> lowPriority = new ArrayList<>();

    @Override
    public boolean hasMessages() {
        return false;
    }

    @Override
    public void sendAllMessages() {
    }

    @Override
    public void sendMessage(Priority p, ErrorType et, String message)
            throws Exception {
        String msg = p + " " + et + ": "  + message;
        if (p == Priority.HighPriority) {
            highPriority.add(msg);
        } else if (p == Priority.MediumPriority) {
            medPriority.add(msg);
        } else if (p == Priority.LowPriority) {
            lowPriority.add(msg);
        }
        System.err.println(p + " " + et + ": "  + message);
    }

}