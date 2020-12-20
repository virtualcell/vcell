package org.jlibsedml.modelsupport;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.jlibsedml.execution.IModelResolver;

/**
 * A generic retriever that will return the contents of the specified URI as a URL to an actual
 *  resource, and will not  validate the URL content. 
 * @author radams
 *
 */
public class URLResourceRetriever extends AbstractResourceRetriever implements IModelResolver {

    /**
     * This implementation will try to resolve the URI as a URL and retrieve the resource at that 
     *  location.
     * @param modelURI A non-null URL
     * @return A <code>String</code> of the model content or <code>null</code> if it is not found.
     * @throws IllegalArgumentException if <code>modelURI</code> is <code>null</code>.
     * @see IModelResolver#getModelXMLFor(URI)
     */
    public String getModelXMLFor(URI modelURI) {
        if(modelURI==null) {
            throw new IllegalArgumentException("modelURI was null");
        }
       try {
           String res = getResource(modelURI.toString());
           if (res!=null){
               return new String(res.getBytes(),"UTF-8");
           }else {
               return null;
           }
      
    } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
    }
    }

}
