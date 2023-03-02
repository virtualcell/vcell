package org.jlibsedml.modelsupport;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jlibsedml.webtools.WebFile;

abstract class AbstractResourceRetriever {
    private final static Logger lg = LogManager.getLogger(AbstractResourceRetriever.class);

    String getResource(final String url) {

        try {
            WebFile webResource = new WebFile(url);
            Object content = webResource.getContent();
            if (content instanceof String) {
                return (String) content;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            lg.error(e);
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            lg.error(e);
            return null;
        }

    }
}
