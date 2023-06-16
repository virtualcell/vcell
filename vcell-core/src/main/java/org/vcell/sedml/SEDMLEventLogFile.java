package org.vcell.sedml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class SEDMLEventLogFile implements SEDMLEventLog {
    private final File logFile;

    public SEDMLEventLogFile(File logFile) {
        this.logFile = logFile;
    }

    @Override
    public void writeEntry(String entry) {
        try {
            Files.write(logFile.toPath(), (entry + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
