package org.vcell.documentation;

import org.jdom.Element;

import java.io.IOException;

public interface DocumentWriter {
    void writePages(Documentation documentation);

    void buildHtmlIndex(Documentation documentation, Element root) throws IOException;

    void writeDefinitions(Documentation documentation);
}
