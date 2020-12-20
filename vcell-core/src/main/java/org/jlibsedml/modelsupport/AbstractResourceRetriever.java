package org.jlibsedml.modelsupport;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jlibsedml.webtools.WebFile;

abstract class AbstractResourceRetriever {

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
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }
}
