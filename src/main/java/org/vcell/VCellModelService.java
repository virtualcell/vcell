package org.vcell;

import org.apache.commons.io.FilenameUtils;
import org.sbml.jsbml.*;
import org.sbml.jsbml.xml.XMLAttributes;
import org.sbml.jsbml.xml.XMLNode;
import org.sbml.jsbml.xml.XMLTriple;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 * Created by kevingaffney on 7/7/17.
 */
public class VCellModelService {

    private final static String VCELL_PREFIX = "vcell";
    private final static String VCELL_SOURCE = "http://sourceforge.net/projects/vcell";

    public void exportSBML(VCellModel vCellModel, File destination) throws IOException, XMLStreamException {

        String name = destination.getName();
        if (!FilenameUtils.getExtension(name).equalsIgnoreCase("xml")) {
            // TODO: Add extension?
        }

        SBMLDocument sbmlDocument = new SBMLDocument();
        sbmlDocument.setLevelAndVersion(3, 1);

        Model model = sbmlDocument.createModel();
        model.setId(vCellModel.getName());
        model.setName(vCellModel.getName());

//        Annotation annotation = new Annotation();
//
//        XMLNode vCellInfoNode = new XMLNode(
//                new XMLTriple("VCellInfo", null, VCELL_PREFIX),
//                new XMLAttributes());
//        vCellInfoNode.addAttr("xmlns:vcell", "http://sourceforge.net/projects/vcell");
//
//        XMLNode vcmlSpecificNode = new XMLNode(
//                new XMLTriple("VCMLSpecific", "uri", VCELL_PREFIX),
//                new XMLAttributes());
//        vCellInfoNode.addChild(vcmlSpecificNode);
//
//        XMLNode bioModelNode = new XMLNode(
//                new XMLTriple("BioModel", "uri", VCELL_PREFIX),
//                new XMLAttributes());
//        bioModelNode.addAttr("Name", "ImageJ_Generated");
//        bioModelNode.addAttr("KeyValue", "0");
//        vcmlSpecificNode.addChild(bioModelNode);
//
//        XMLNode simulationSpecNode = new XMLNode(
//                new XMLTriple("SimulationSpec", "uri", VCELL_PREFIX),
//                new XMLAttributes());
//        simulationSpecNode.addAttr("Name", "Geometric");
//        simulationSpecNode.addAttr("KeyValue", "0");
//        vcmlSpecificNode.addChild(simulationSpecNode);
//
//        annotation.setNonRDFAnnotation(vCellInfoNode);
//        model.setAnnotation(annotation);

        // UNIT DEFINITIONS

        UnitDefinition substanceDefinition = new UnitDefinition("substance");
        substanceDefinition.addUnit(new Unit(1.0, 0, Unit.Kind.ITEM, 1.0, -1, -1));
        model.addUnitDefinition(substanceDefinition);

        UnitDefinition volumeDefinition = new UnitDefinition("volume");
        volumeDefinition.addUnit(new Unit(1.0, -18, Unit.Kind.METRE, 3.0, -1, -1));
        model.addUnitDefinition(volumeDefinition);

        UnitDefinition areaDefinition = new UnitDefinition("area");
        areaDefinition.addUnit(new Unit(1.0, -12, Unit.Kind.METRE, 2.0, -1, -1));
        model.addUnitDefinition(areaDefinition);

        UnitDefinition lengthDefinition = new UnitDefinition("length");
        lengthDefinition.addUnit(new Unit(1.0, -6, Unit.Kind.METRE, 1.0, -1, -1));
        model.addUnitDefinition(lengthDefinition);

        UnitDefinition timeDefinition = new UnitDefinition("time");
        timeDefinition.addUnit(new Unit(1.0, 0, Unit.Kind.SECOND, 1.0, -1, -1));
        model.addUnitDefinition(timeDefinition);

        UnitDefinition moleculesDefinition = new UnitDefinition("molecules");
        moleculesDefinition.addUnit(new Unit(1.0, 0, Unit.Kind.ITEM, 1.0, -1, -1));
        model.addUnitDefinition(moleculesDefinition);

        UnitDefinition moleculesUm2S1Definition = new UnitDefinition("molecules_um_2_s_1");
        moleculesUm2S1Definition.addUnit(new Unit(1.0, 12, Unit.Kind.DIMENSIONLESS, 1.0, -1, -1));
        moleculesUm2S1Definition.addUnit(new Unit(1.0, 0, Unit.Kind.ITEM, 1.0, -1, -1));
        moleculesUm2S1Definition.addUnit(new Unit(1.0, 0, Unit.Kind.METRE, -2.0, -1, -1));
        moleculesUm2S1Definition.addUnit(new Unit(1.0, 0, Unit.Kind.SECOND, -1.0, -1, -1));
        model.addUnitDefinition(moleculesUm2S1Definition);

        UnitDefinition pAum2Definition = new UnitDefinition("pA_um_2");
        pAum2Definition.addUnit(new Unit(1.0, 0, Unit.Kind.AMPERE, 1.0, -1, -1));
        pAum2Definition.addUnit(new Unit(1.0, 0, Unit.Kind.METRE, -2.0, -1, -1));
        model.addUnitDefinition(pAum2Definition);

        UnitDefinition oneDefinition = new UnitDefinition("_one_");
        oneDefinition.addUnit(new Unit(1.0, 0, Unit.Kind.DIMENSIONLESS, 1.0, -1, -1));
        model.addUnitDefinition(oneDefinition);

        UnitDefinition uMs1Definition = new UnitDefinition("uM_s_1");
        uMs1Definition.addUnit(new Unit(1.0, -3, Unit.Kind.DIMENSIONLESS, 1.0, -1, -1));
        uMs1Definition.addUnit(new Unit(1.0, 0, Unit.Kind.METRE, -3.0, -1, -1));
        uMs1Definition.addUnit(new Unit(1.0, 0, Unit.Kind.MOLE, 1.0, -1, -1));
        uMs1Definition.addUnit(new Unit(1.0, 0, Unit.Kind.SECOND, -1.0, -1, -1));
        model.addUnitDefinition(uMs1Definition);

        UnitDefinition s1uM1Definition = new UnitDefinition("s_1_uM_1");
        uMs1Definition.addUnit(new Unit(1.0, 3, Unit.Kind.DIMENSIONLESS, 1.0, -1, -1));
        uMs1Definition.addUnit(new Unit(1.0, 0, Unit.Kind.METRE, 3.0, -1, -1));
        uMs1Definition.addUnit(new Unit(1.0, 0, Unit.Kind.MOLE, -1.0, -1, -1));
        uMs1Definition.addUnit(new Unit(1.0, 0, Unit.Kind.SECOND, -1.0, -1, -1));
        model.addUnitDefinition(s1uM1Definition);

        // COMPARTMENTS

        Compartment cytCompartment = new Compartment("Cyt", "Cyt", -1, -1);
        cytCompartment.setSpatialDimensions(3);
        cytCompartment.setSize(50000.0);
        cytCompartment.setUnits("um3");
        cytCompartment.setConstant(true);
        // TODO: Add spatial compartment mapping
        model.addCompartment(cytCompartment);

        Compartment ecCompartment = new Compartment("EC", "EC", -1, -1);
        ecCompartment.setSpatialDimensions(3);
        ecCompartment.setSize(50000.0);
        ecCompartment.setUnits("um3");
        ecCompartment.setConstant(true);
        // TODO: Add spatial compartment mapping
        model.addCompartment(ecCompartment);

        Compartment pmCompartment = new Compartment("PM", "PM", -1, -1);
        pmCompartment.setSpatialDimensions(2);
        pmCompartment.setSize(6000.0);
        pmCompartment.setConstant(true);
        // TODO: Add spatial compartment mapping
        model.addCompartment(pmCompartment);

        // SPECIES

        Species cytSpecies = new Species("A", 3, 1);
        cytSpecies.setCompartment(cytCompartment);
        cytSpecies.setInitialConcentration(1.0);
        cytSpecies.setSubstanceUnits("molecules");
        cytSpecies.setHasOnlySubstanceUnits(false);
        cytSpecies.setBoundaryCondition(false);
        cytSpecies.setConstant(false);
        model.addSpecies(cytSpecies);

        Species pmSpecies = new Species("B", 3, 1);
        pmSpecies.setCompartment(pmCompartment);
        pmSpecies.setInitialConcentration(1.0);
        pmSpecies.setSubstanceUnits("molecules");
        pmSpecies.setHasOnlySubstanceUnits(false);
        pmSpecies.setBoundaryCondition(false);
        pmSpecies.setConstant(false);
        model.addSpecies(pmSpecies);

        Species cytActiveSpecies = new Species("A_active", 3, 1);
        cytActiveSpecies.setCompartment(cytCompartment);
        cytActiveSpecies.setInitialConcentration(0.0);
        cytActiveSpecies.setSubstanceUnits("molecules");
        cytActiveSpecies.setHasOnlySubstanceUnits(false);
        cytActiveSpecies.setBoundaryCondition(false);
        cytActiveSpecies.setConstant(false);
        model.addSpecies(cytActiveSpecies);

        Species boundSpecies = new Species("A_B", 3, 1);
        boundSpecies.setCompartment(pmCompartment);
        boundSpecies.setInitialConcentration(0.0);
        boundSpecies.setSubstanceUnits("molecules");
        boundSpecies.setHasOnlySubstanceUnits(false);
        boundSpecies.setBoundaryCondition(false);
        boundSpecies.setConstant(false);
        model.addSpecies(boundSpecies);

        Species lightSpecies = new Species("Light", 3, 1);
        lightSpecies.setCompartment(cytCompartment);
        lightSpecies.setInitialConcentration(0.0);
        lightSpecies.setSubstanceUnits("molecules");
        lightSpecies.setHasOnlySubstanceUnits(false);
        lightSpecies.setBoundaryCondition(false);
        lightSpecies.setConstant(false);
        model.addSpecies(lightSpecies);

        // TODO: List of parameters

        // PARAMETERS

        Parameter cytDiffusion = new Parameter(cytSpecies.getId() + "_diff");
        cytDiffusion.setValue(10.0);
        cytDiffusion.setConstant(true);
        model.addParameter(cytDiffusion);

        Parameter pmDiffusion = new Parameter(pmSpecies.getId() + "_diff");
        pmDiffusion.setValue(0.1);
        pmDiffusion.setConstant(true);

        Parameter vMaxActivation = new Parameter("Vmax_Activation");
        vMaxActivation.setValue(0.5);
        vMaxActivation.setUnits(s1uM1Definition);
        vMaxActivation.setConstant(true);
        model.addParameter(vMaxActivation);

        // REACTIONS

        Reaction activationReaction = new Reaction("Activation", 3, 1);
        activationReaction.setName(activationReaction.getId());
        activationReaction.setReversible(true);
        activationReaction.setCompartment(cytCompartment);

        activationReaction.setAnnotation(simpleReactionAnnotation(cytCompartment));

        // TODO: Add spatial attribute

        SpeciesReference cytSpeciesRef = new SpeciesReference(cytSpecies);
        cytSpeciesRef.setStoichiometry(1.0);
        cytSpeciesRef.setConstant(true);
        activationReaction.addReactant(cytSpeciesRef);

        SpeciesReference cytActiveSpeciesRef = new SpeciesReference(cytActiveSpecies);
        cytActiveSpeciesRef.setStoichiometry(1.0);
        cytActiveSpeciesRef.setConstant(true);
        activationReaction.addProduct(cytActiveSpeciesRef);

        activationReaction.addModifier(new ModifierSpeciesReference(lightSpecies));

        KineticLaw activationLaw = new KineticLaw(activationReaction);
        activationLaw.setMath(ASTNode.times(
                new ASTNode(vMaxActivation),
                new ASTNode(cytSpecies),
                new ASTNode(lightSpecies)));
        activationReaction.setKineticLaw(activationLaw);
        model.addReaction(activationReaction);

        Reaction bindingReaction = new Reaction("Binding", 3, 1);
        bindingReaction.setName(bindingReaction.getId());
        bindingReaction.setReversible(true);
        bindingReaction.setCompartment(pmCompartment);

        bindingReaction.setAnnotation(simpleReactionAnnotation(pmCompartment));

        // TODO: Add spatial attribute

        bindingReaction.addReactant(cytActiveSpeciesRef);

        SpeciesReference pmSpeciesRef = new SpeciesReference(pmSpecies);
        pmSpeciesRef.setStoichiometry(1.0);
        pmSpeciesRef.setConstant(true);
        bindingReaction.addReactant(pmSpeciesRef);

        SpeciesReference boundSpeciesRef = new SpeciesReference(boundSpecies);
        boundSpeciesRef.setStoichiometry(1.0);
        boundSpeciesRef.setConstant(true);
        bindingReaction.addProduct(boundSpeciesRef);

        KineticLaw bindingLaw = new KineticLaw(bindingReaction);
        bindingLaw.setMath(ASTNode.times(
                new ASTNode(cytActiveSpecies),
                new ASTNode(pmSpecies)
        ));
        bindingReaction.setKineticLaw(bindingLaw);
        model.addReaction(bindingReaction);


        sbmlDocument.setModel(model);
        SBMLWriter sbmlWriter = new SBMLWriter();
        String sbmlString = sbmlWriter.writeSBMLToString(sbmlDocument);
        System.out.println(sbmlString);
        sbmlWriter.write(sbmlDocument, destination);
    }

    private Annotation simpleReactionAnnotation(Compartment compartment) {

        Annotation annotation = new Annotation();

        XMLNode vCellInfoNode = new XMLNode(
                new XMLTriple("VCellInfo", null, VCELL_PREFIX),
                new XMLAttributes());
        vCellInfoNode.addAttr("xmlns:" + VCELL_PREFIX, VCELL_SOURCE);
        annotation.setNonRDFAnnotation(vCellInfoNode);

        XMLNode vcmlSpecificNode = new XMLNode(new XMLTriple("VCMLSpecific", null, VCELL_PREFIX));
        vCellInfoNode.addChild(vcmlSpecificNode);

        XMLNode simpleReactionNode = new XMLNode(
                new XMLTriple("SimpleReaction", null, VCELL_PREFIX),
                new XMLAttributes());
        simpleReactionNode.addAttr("Structure", compartment.getId());
        vcmlSpecificNode.addChild(simpleReactionNode);

        XMLNode reactionRateNode = new XMLNode(
                new XMLTriple("ReactionRate", null, VCELL_PREFIX),
                new XMLAttributes());
        reactionRateNode.addAttr("Name", "J");
        vcmlSpecificNode.addChild(reactionRateNode);

        return annotation;
    }
}
