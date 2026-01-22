package org.jlibsedml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.Variable;
import org.jlibsedml.components.model.Model;
import org.jlibsedml.components.task.AbstractTask;
import org.jlibsedml.components.task.SubTask;
import org.jlibsedml.components.task.Task;
import org.jlibsedml.execution.IModelResolver;
import org.jlibsedml.extensions.XMLUtils;

/*
 * Helper class to add multiple simple datagenerators / variables to the SEDML file
 */
public class XpathGeneratorHelper {
    private final static Logger lg = LogManager.getLogger(XpathGeneratorHelper.class);

    public XpathGeneratorHelper(SedMLDataContainer sedml) {
        super();
        this.sedml = sedml;
    }

    private final SedMLDataContainer sedml;


    public boolean addIdentifiersAsDataGenerators(final AbstractTask task, final String attributeIdentifierName,
                                           boolean allOrNothing, final IModelResolver modelResolver, final IdName... idNameList) {
        XMLUtils utils = new XMLUtils();

        try {
            Set<Task> potentialBaseTasks = this.sedml.getBaseTasks(task.getId());
            if (potentialBaseTasks.size() != 1) throw new IllegalArgumentException("Cannot make data generators for repeated task with multiple different base tasks!");
            Task baseTask = potentialBaseTasks.stream().findFirst().orElse(null);
            SedBase elementFound = this.sedml.getSedML().searchInModelsFor(baseTask.getModelReference());
            if (!(elementFound instanceof Model modelFound)) throw new IllegalArgumentException("provided task has invalid model reference!");
            String modelStrRep = modelResolver.getModelXMLFor(modelFound.getSourceURI());
            if (modelStrRep == null) return false;

            Document doc = utils.readDoc(new ByteArrayInputStream(modelStrRep.getBytes()));

            List<AllOrNothingConfig> configs = new ArrayList<>();
            for (IdName idn : idNameList) {
                String id = idn.getId();
                Element toIdentify = this.findElement(doc, id);
                if (toIdentify == null && !allOrNothing) {
                    continue;
                } else if (toIdentify == null) {
                    return false;
                }

                String xpath = utils.getXPathForElementIdentifiedByAttribute(toIdentify,
                        doc, toIdentify.getAttribute(attributeIdentifierName));
                XPathTarget targ = new XPathTarget(xpath);
                // build up collection to execute, so as to satisfy all-or-nothing criteria.
                configs.add(new AllOrNothingConfig(targ, idn));
            }
            for (AllOrNothingConfig cfg : configs) {
                this.sedml.createIdentityDataGeneratorForSpecies(new Variable(new SId(cfg.id.getId()), cfg.id.getName(), task.getId(), cfg.targ.toString()));
            }

        } catch (URISyntaxException | JDOMException | IOException e) {
            lg.error(e);
            return false;
        }

        return true;
    }


    class AllOrNothingConfig {
        public AllOrNothingConfig(XPathTarget targ, IdName id) {
            super();
            this.targ = targ;
            this.id = id;
        }

        XPathTarget targ;
        IdName id;
    }

    private Element findElement(Document doc, String id) {
        Iterator it = doc.getDescendants(new ElementFilter() {
            @Override
            public Element filter(Object obj) {
                Element el = super.filter(obj);
                if (el != null && el.getAttribute("id") != null && el.getAttribute("id").getValue().equals(id)) {
                    return el;
                }
                return null;
            }
        });
        if (it.hasNext()) {
            return (Element) it.next();
        } else {
            return null;
        }
    }
}
