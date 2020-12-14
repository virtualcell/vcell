package org.jlibsedml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.AbstractFilter;
import org.jlibsedml.execution.IModelResolver;
import org.jlibsedml.extensions.XMLUtils;

/*
 * Helper class to add multiple simple datagenerators / variables to the SEDML file
 */
 class XpathGeneratorHelper {
     
     public XpathGeneratorHelper(SedML sedml) {
        super();
        this.sedml = sedml;
    }

    private final SedML sedml;
     

      boolean addIdentifiersAsDataGenerators (final AbstractTask task, final String attributeIdentifierName,
             boolean allOrNothing, final IModelResolver modelResolver, final IdName ... idNameList) {
        XMLUtils utils = new XMLUtils();
       
        try {
        String model =  modelResolver.getModelXMLFor(sedml.getModelWithId(task.getModelReference()).getSourceURI());
        if (model == null){
            return false;
        }
        Document doc = utils.readDoc(new ByteArrayInputStream(model.getBytes()));
        
       List<AllOrNothingConfig> configs = new ArrayList<AllOrNothingConfig>();
       for (IdName idn:  idNameList){
           String id =idn.getId();
           Element toIdentify= findElement(doc, id);
           if(toIdentify == null && !allOrNothing) {
               continue;
           }else if (toIdentify == null && allOrNothing){
               return false;
           }
           
           String xpath = utils.getXPathForElementIdentifiedByAttribute(toIdentify,
                   doc, toIdentify.getAttribute(attributeIdentifierName));
           XPathTarget targ = new XPathTarget(xpath);
           // build up collection to execute, so as to satisfy all-or-nothing criteria.
           configs.add(new AllOrNothingConfig(targ,  idn)); 
       }
       for (AllOrNothingConfig cfg: configs){
           sedml.addSimpleSpeciesAsOutput(cfg.targ, cfg.id.getId(), cfg.id.getName(), task, true);
       }
      
           
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        
       }
        
        return true;
    }
      
      
    class AllOrNothingConfig {
       public AllOrNothingConfig(XPathTarget targ,  IdName id) {
            super();
            this.targ = targ;
            this.id = id;
        }
    XPathTarget targ;
       IdName id;
    }
     private Element findElement(Document doc, String id) {
         Iterator it = doc.getDescendants(new AttributeFilter("id", id));
         if(it.hasNext()){
            return (Element)it.next();
         } else {
             return null;
         }
    
         
         
      }
      
      class AttributeFilter extends AbstractFilter {
          private final String name, val;
          AttributeFilter (String name, String val) {
              this.name=name;
              this.val=val;
          }
          public boolean matches(Object o) {
              if (o instanceof Element) {
                  Element toExamine = (Element)o;
                  if (toExamine.getAttribute(name) != null && 
                          toExamine.getAttribute(name).getValue().equals(val)){
                      return true;
                  }
              }
              return false;
          }
          
      }
}
