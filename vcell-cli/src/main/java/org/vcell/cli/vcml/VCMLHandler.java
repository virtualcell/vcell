package org.vcell.cli.vcml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlHelper;
import org.jdom.Element;
import org.vcell.model.bngl.ASTModel;
import org.vcell.model.bngl.BngUnitSystem;
import org.vcell.model.bngl.gui.BNGLDebuggerPanel;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.util.BeanUtils;
import org.vcell.util.document.VCDocument;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class VCMLHandler {
    public static String outputDir = null;

    public static VCDocument convertVcmlToVcDocument(File vcmlFilePath) throws Exception {

        ExternalDocInfo documentInfo = new ExternalDocInfo(vcmlFilePath, true);

        File file = documentInfo.getFile();
        if (file != null && !file.getName().isEmpty() && file.getName().endsWith("bngl")) {

            BngUnitSystem bngUnitSystem = new BngUnitSystem(BngUnitSystem.BngUnitOrigin.DEFAULT);
            String fileText;
            try {
                fileText = BeanUtils.readBytesFromFile(file, null);
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
            Reader reader = documentInfo.getReader();
            boolean bException = true;
            while (bException) {
                try {
                    BioModel bioModel = createDefaultBioModelDocument(bngUnitSystem);
                    SimulationContext ruleBasedSimContext = bioModel.addNewSimulationContext("temp NFSim app",
                            SimulationContext.Application.RULE_BASED_STOCHASTIC);
                    List<SimulationContext> appList = new ArrayList<SimulationContext>();
                    appList.add(ruleBasedSimContext);

                    Model.RbmModelContainer rbmModelContainer = bioModel.getModel().getRbmModelContainer();
                    RbmUtils.reactionRuleLabelIndex = 0;
                    RbmUtils.reactionRuleNames.clear();
                    ASTModel astModel = RbmUtils.importBnglFile(reader);
                    if (astModel.hasUnitSystem()) {
                        bngUnitSystem = astModel.getUnitSystem();
                    }
                    if (astModel.hasCompartments()) {
                        Structure struct = bioModel.getModel().getStructure(0);
                        if (struct != null) {
                            bioModel.getModel().removeStructure(struct);
                        }
                    }
                    RbmUtils.BnglObjectConstructionVisitor constructionVisitor = null;
                    if (!astModel.hasMolecularDefinitions()) {
                        System.out.println("Molecular Definition Block missing.");
                        constructionVisitor = new RbmUtils.BnglObjectConstructionVisitor(bioModel.getModel(), appList,
                                bngUnitSystem, false);
                    } else {
                        constructionVisitor = new RbmUtils.BnglObjectConstructionVisitor(bioModel.getModel(), appList,
                                bngUnitSystem, true);
                    }
                    astModel.jjtAccept(constructionVisitor, rbmModelContainer);
                    bException = false;
                } catch (final Exception e) {
                    e.printStackTrace(System.out);
                    BNGLDebuggerPanel panel = new BNGLDebuggerPanel(fileText, e);

                    // inserting <potentially> corrected DocumentInfo
                    fileText = panel.getText();
                }
            }

        }


        VCDocument doc = null;
        XMLSource xmlSource = documentInfo.createXMLSource();
        org.jdom.Element rootElement = xmlSource.getXmlDoc().getRootElement();
        String xmlType = rootElement.getName();
        String modelXmlType = null;

        if (xmlType.equals(XMLTags.VcmlRootNodeTag)) {
            // For now, assuming that <vcml> element has only one child (biomodel, mathmodel
            // or geometry).
            // Will deal with multiple children of <vcml> Element when we get to model
            // composition.
            @SuppressWarnings("unchecked")
            List<Element> childElementList = rootElement.getChildren();
            // TODO: Check for all child elements
            Element modelElement = childElementList.get(0); // assuming first child is the biomodel,
            // mathmodel or geometry.
            modelXmlType = modelElement.getName();
        }
        if (xmlType.equals(XMLTags.BioModelTag) || (xmlType.equals(XMLTags.VcmlRootNodeTag)
                && modelXmlType.equals(XMLTags.BioModelTag))) {
            doc = XmlHelper.XMLToBioModel(xmlSource);
        }

        return doc;

    }

    private static BioModel createDefaultBioModelDocument(BngUnitSystem bngUnitSystem) throws PropertyVetoException {
        BioModel bioModel = new BioModel(null);
        bioModel.setName("BioModel");

        Model model;
        if (bngUnitSystem == null) {
            model = new Model("model");
        } else {
            model = new Model("model", bngUnitSystem.createModelUnitSystem());
        }
        bioModel.setModel(model);

        model.createFeature();
        return bioModel;
    }


}
