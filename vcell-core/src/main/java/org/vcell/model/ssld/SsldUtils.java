package org.vcell.model.ssld;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.model.*;
import org.vcell.model.rbm.*;
import org.vcell.solver.langevin.LangevinLngvWriter;

import java.util.*;
import java.io.BufferedReader;
import java.io.*;
import java.util.prefs.Preferences;

import static org.vcell.solver.langevin.LangevinLngvWriter.*;

public class SsldUtils {

    private class Mapping {

        final SsldModel ssldModel;
        final BioModel bioModel;

        Map<Molecule, MolecularType> moleculeToMolecularTypeMap = new LinkedHashMap<>();
        Map<MolecularType, Molecule> molecularTypeToMoleculeMap = new LinkedHashMap<>();
        Map<SiteType, MolecularComponent> typeToComponentMap = new LinkedHashMap<>();
        Map<MolecularComponent, SiteType> componentToTypeMap = new LinkedHashMap<>();

        Map<Reaction, ReactionRule> reactionToReactionRuleMap = new LinkedHashMap<>();
        Map<ReactionRule, Reaction> reactionRuleToReactionMap = new LinkedHashMap<>();
        Map<ReactionRule, ReactionRuleSpec.Subtype> reactionRuleToSubtype = new LinkedHashMap<>();

        public Mapping(SsldModel ssldModel, BioModel bioModel) {
            this.ssldModel = ssldModel;
            this.bioModel = bioModel;
        }

        void put(Molecule ssldMolecule, MolecularType mt) {
            moleculeToMolecularTypeMap.put(ssldMolecule, mt);
            molecularTypeToMoleculeMap.put(mt, ssldMolecule);
        }
        Molecule get(MolecularType mt) {
            return molecularTypeToMoleculeMap.get(mt);
        }
        MolecularType get(Molecule ssldMolecule) {
            return moleculeToMolecularTypeMap.get(ssldMolecule);
        }

        void put(SiteType st, MolecularComponent mc) {
            typeToComponentMap.put(st, mc);
            componentToTypeMap.put(mc, st);
        }
        SiteType get(MolecularComponent mc) {
            return componentToTypeMap.get(mc);
        }
        MolecularComponent get(SiteType st) {
            return typeToComponentMap.get(st);
        }

        void put(Reaction r, ReactionRule rr) {
            reactionToReactionRuleMap.put(r, rr);
            reactionRuleToReactionMap.put(rr, r);
        }
        Reaction get(ReactionRule rr) {
            return reactionRuleToReactionMap.get(rr);
        }
        ReactionRule get(Reaction r) {
            return reactionToReactionRuleMap.get(r);
        }

        void put(ReactionRule rr, ReactionRuleSpec.Subtype st) {
            reactionRuleToSubtype.put(rr, st);
        }
        ReactionRuleSpec.Subtype getSubtype(ReactionRule rr) {
            return reactionRuleToSubtype.get(rr);
        }

    }


    public BioModel fromSsld(SsldModel ssldModel) throws Exception {

        ModelUnitSystem mus = ModelUnitSystem.createDefaultVCModelUnitSystem();
        BioModel bioModel = new BioModel(null);
        bioModel.setName(ssldModel.getSystemName());
        Model model = new Model("model", mus);
        bioModel.setModel(model);

        Mapping m = new Mapping(ssldModel, bioModel);

        // structures
        if(bioModel.getModel().getStructures().length > 0) {
            Structure struct = bioModel.getModel().getStructure(0);
            if (struct != null) {
                bioModel.getModel().removeStructure(struct, true);
            }
        }
        LinkedList<Structure> newstructures = new LinkedList<>();
        Feature intra = new Feature(Structure.SpringStructureEnum.Intracellular.columnName);
        newstructures.add(intra);
        Membrane membr = new Membrane(Structure.SpringStructureEnum.Membrane.columnName);
        newstructures.add(membr);
        Feature extra = new Feature(Structure.SpringStructureEnum.Extracellular.columnName);
        newstructures.add(extra);
        Structure[] structarray = new Structure[newstructures.size()];
        newstructures.toArray(structarray);
        model.setStructures(structarray);

        // molecular types
        for(Molecule ssldMolecule : ssldModel.getMolecules()) {
            MolecularType mt = new MolecularType(ssldMolecule.getName(), model);
            for(Site ssldSite : ssldMolecule.getSiteArray()) {
                SiteType ssldType = ssldSite.getType();
                MolecularComponent mc = new MolecularComponent(ssldType.getName());

                // TODO: site index starts at 1 in vcell, while it starts at 0 in ssld
                // see also LangevinLngvWriter.writeSpeciesInfo()
                // see also MolecularInternalLinkSpec.writeLink()
                mc.setIndex(ssldSite.getIndex()+1);
                for(State ssldState : ssldType.getStates()) {
                    ComponentStateDefinition csd = new ComponentStateDefinition(ssldState.getName());
                    mc.addComponentStateDefinition(csd);
                }
                m.put(ssldType, mc);
                mt.addMolecularComponent(mc);
            }
            // TODO: if membrane molecule, anchor it to membrane
            m.put(ssldMolecule, mt);
            model.getRbmModelContainer().addMolecularType(mt, false);
        }
        // reaction rules
        for(BindingReaction ssldReaction : ssldModel.getBindingReactions()) {
            boolean reversible = ssldReaction.getkoff() != 0 ? true : false;
            Molecule ssldReactantOne = ssldReaction.getMolecule(0);
            Molecule ssldReactantTwo = ssldReaction.getMolecule(1);
            String locationOne = ssldReactantOne.getLocation();
            String locationTwo = ssldReactantTwo.getLocation();
            Structure structure;
            if(locationOne.contentEquals(SystemGeometry.MEMBRANE) || locationTwo.contentEquals(SystemGeometry.MEMBRANE)) {
                // reaction is on the Membrane if at least one reactant is on the Membrane, the product stay on the membrane
                // actually the membrane molecule must be anchored to the membrane
                structure = model.getStructure(SystemGeometry.MEMBRANE);
            } else {
                // both reactants must be in the same compartment, the product will also stay there
                structure = model.getStructure(locationOne);
            }
            ReactionRule reactionRule = new ReactionRule(model, ssldReaction.getName(), structure, reversible);
            RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
            reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
            SpeciesPattern spReactantOne = getSpeciesPattern(ssldReactantOne, m);
            SpeciesPattern spReactantTwo = getSpeciesPattern(ssldReactantTwo, m);
            SpeciesPattern spProduct = getSpeciesPattern(ssldReactantOne, ssldReactantTwo, m);

            Molecule ssldMoleculeOne = ssldReaction.getMolecule(0);
            SiteType ssldSiteTypeOne = ssldReaction.getType(0);
            State ssldStateOne = ssldReaction.getState(0);
            MolecularType mtOne = m.get(ssldMoleculeOne);
            MolecularTypePattern mtpOne = spReactantOne.getMolecularTypePatterns(ssldMoleculeOne.getName()).get(0);
            MolecularComponentPattern mcpOne = mtpOne.getMolecularComponentPattern(ssldSiteTypeOne.getName());
            ComponentStatePattern cspOne = new ComponentStatePattern(new ComponentStateDefinition(ssldStateOne.getName()));
            mcpOne.setComponentStatePattern(cspOne);
            mcpOne.setBondType(MolecularComponentPattern.BondType.None);

            Molecule ssldMoleculeTwo = ssldReaction.getMolecule(1);
            SiteType ssldSiteTypeTwo = ssldReaction.getType(1);
            State ssldStateTwo = ssldReaction.getState(1);
            MolecularType mtTwo = m.get(ssldMoleculeTwo);

            ReactantPattern rpOne = new ReactantPattern(spReactantOne, model.getStructure(locationOne));
            ReactantPattern rpTwo = new ReactantPattern(spReactantTwo, model.getStructure(locationTwo));
            ProductPattern pp = new ProductPattern(spProduct, structure);
            // at this point everything is trivial
            // TODO: now we need to adjust each sp to satisfy the bond condition


            reactionRule.addReactant(rpOne);
            reactionRule.addReactant(rpTwo);
            reactionRule.addProduct(pp);
            bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            m.put(ssldReaction, reactionRule);
        }


        return bioModel;
    }

    private SpeciesPattern getSpeciesPattern(Molecule moleculeOne, Molecule moleculeTwo, Mapping m) {
        SpeciesPattern sp = new SpeciesPattern();
        MolecularTypePattern mtpOne = getMolecularTypePattern(moleculeOne, m);
        sp.addMolecularTypePattern(mtpOne);
        MolecularTypePattern mtpTwo = getMolecularTypePattern(moleculeTwo, m);
        sp.addMolecularTypePattern(mtpTwo);
        return sp;
    }
    private SpeciesPattern getSpeciesPattern(Molecule molecule, Mapping m) {
        SpeciesPattern sp = new SpeciesPattern();
        MolecularTypePattern mtp = getMolecularTypePattern(molecule, m);
        sp.addMolecularTypePattern(mtp);
        return sp;
    }
    private MolecularTypePattern getMolecularTypePattern(Molecule molecule, Mapping m) {
        MolecularTypePattern mtp = new MolecularTypePattern(m.get(molecule));


        return mtp;
    }

    public static SsldModel importSsldFile(File file) {

        SsldModel model = new SsldModel();
        model.setFile(file);

        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            sc.useDelimiter("\\*\\*\\*");
            while(sc.hasNext()) {
                String next = sc.next().trim();
                switch(next) {
                    case TIME_INFORMATION:
                        model.systemTimes.loadData(sc.next().trim());
                        break;
                    case SPATIAL_INFORMATION:
                        model.boxGeometry.loadData(sc.next().trim());
                        break;
                    case MOLECULES:
                        model.molecules = Molecule.loadMolecules(sc.next().trim());
                        break;
                    case MOLECULE_FILES:
                        Molecule.loadMoleculesFiles(model, sc.next().trim(), model.molecules);
                        break;
                    case DECAY_REACTIONS:
                        // TODO: looks like we load the reactions but never save them in the model. CHECK this
                        DecayReaction.loadReactions(model, new Scanner(sc.next().trim()));
                        break;
                    case TRANSITION_REACTIONS:
                        model.transitionReactions = TransitionReaction.loadReactions(model, new Scanner(sc.next().trim()));
                        break;
                    case ALLOSTERIC_REACTIONS:
                        model.allostericReactions = AllostericReaction.loadReactions(model, new Scanner(sc.next().trim()));
                        break;
                    case BINDING_REACTIONS:
                        model.bindingReactions = BindingReaction.loadReactions(model, new Scanner(sc.next().trim()));
                        break;
                    case MOLECULE_COUNTERS:
                        MoleculeCounter.loadCounters(model, new Scanner(sc.next().trim()));
                        break;
                    case STATE_COUNTERS:
                        StateCounter.loadCounters(model, new Scanner(sc.next().trim()));
                        break;
                    case BOND_COUNTERS:
                        BondCounter.loadCounters(model, new Scanner(sc.next().trim()));
                        break;
                    case SITE_PROPERTY_COUNTERS:
                        SitePropertyCounter.loadCounters(model, new Scanner(sc.next().trim()));
                        break;
                    case CLUSTER_COUNTERS:
                        Scanner xsc = new Scanner(sc.next().trim());
                        // Skip "Track_Clusters: "
                        xsc.next();
                        model.trackClusters = xsc.nextBoolean();
                        xsc.close();
                        break;
                    case SYSTEM_ANNOTATION:
                        model.systemAnnotation.setAnnotation(sc.next().trim());
                        break;
                    case MOLECULE_ANNOTATIONS:
                        model.loadMoleculeAnnotations(sc.next().trim());
                        break;
                    case REACTION_ANNOTATIONS:
                        model.loadReactionAnnotations(sc.next().trim());
                        break;
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace(System.out);
        } finally {
            if(sc != null) {
                sc.close();
            }
            if(br != null) {
                try {
                    br.close();
                } catch(IOException bioe) {
                    bioe.printStackTrace(System.out);
                }
            }
            if(fr != null) {
                try {
                    fr.close();
                } catch(IOException fioe) {
                    fioe.printStackTrace(System.out);
                }
            }
        }
        return model;
    }

}
