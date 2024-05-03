package org.vcell.documentation;

import org.jdom.Element;

import java.io.IOException;

public interface DocumentWriter {
    void writePages(Documentation documentation);

    void writeDefinitions(Documentation documentation);

    void processTOC(Documentation documentation) throws Exception;

    void generateHelpMap(Documentation documentation) throws Exception;

    void copyHelpSet() throws IOException;

    void generateHelpSearch() throws Exception;
}
