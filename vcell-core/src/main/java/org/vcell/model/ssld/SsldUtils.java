package org.vcell.model.ssld;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.*;
import cbit.vcell.parser.Expression;
import org.vcell.model.rbm.*;
import org.vcell.solver.langevin.LangevinLngvWriter;

import java.util.*;
import java.io.BufferedReader;
import java.io.*;
import java.util.prefs.Preferences;

import static org.vcell.model.ssld.TransitionReaction.ANY_STATE;
import static org.vcell.model.ssld.TransitionReaction.ANY_STATE_STRING;
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

        // since we don't accept duplicates, all these maps need cleaning after each reaction
        // because the same molecule may be part of many reactions
        Map<Molecule, ReactantPattern> reactantToReactantPatternOne = new LinkedHashMap<>();   // these are unidirectional
        Map<Molecule, ProductPattern> reactantToProductPatternOne = new LinkedHashMap<>();
        // for binding, the molecule may be the same for both reactants, and we'd have duplicated entries
        Map<Molecule, ReactantPattern> reactantToReactantPatternTwo = new LinkedHashMap<>();   // these are unidirectional
        Map<Molecule, ProductPattern> reactantToProductPatternTwo = new LinkedHashMap<>();
        Map<Molecule, ReactantPattern> conditionToReactantPattern = new LinkedHashMap<>();
        Map<Molecule, ProductPattern> conditionToProductPattern = new LinkedHashMap<>();

        public Mapping(SsldModel ssldModel, BioModel bioModel) {
            this.ssldModel = ssldModel;
            this.bioModel = bioModel;
        }

        void cleanReactionsMaps() {
            reactantToReactantPatternOne.clear();
            reactantToProductPatternOne.clear();
            reactantToReactantPatternTwo.clear();
            reactantToProductPatternTwo.clear();
            conditionToReactantPattern.clear();
            conditionToProductPattern.clear();
        }

        void put(Molecule ssldMolecule, MolecularType mt) {
            if(moleculeToMolecularTypeMap.containsKey(ssldMolecule)) {
                throw new RuntimeException("moleculeToMolecularTypeMap: Duplicate Key");
            }
            if(molecularTypeToMoleculeMap.containsKey(mt)) {
                throw new RuntimeException("molecularTypeToMoleculeMap: Duplicate Key");
            }
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
            if(typeToComponentMap.containsKey(st)) {
                throw new RuntimeException("typeToComponentMap: Duplicate Key");
            }
            if(componentToTypeMap.containsKey(mc)) {
                throw new RuntimeException("componentToTypeMap: Duplicate Key");
            }
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
            if(reactionToReactionRuleMap.containsKey(r)) {
                throw new RuntimeException("reactionToReactionRuleMap: Duplicate Key");
            }
            if(reactionRuleToReactionMap.containsKey(rr)) {
                throw new RuntimeException("reactionRuleToReactionMap: Duplicate Key");
            }
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
            if(reactionRuleToSubtype.containsKey(rr)) {
                throw new RuntimeException("reactionRuleToSubtype: Duplicate Key");
            }
            reactionRuleToSubtype.put(rr, st);
        }
        ReactionRuleSpec.Subtype getSubtype(ReactionRule rr) {
            return reactionRuleToSubtype.get(rr);
        }


        // -------------------------------------------------------------------------------
        void putMoleculeToReactantPatternOne(Molecule ssldMolecule, ReactantPattern rp) {
            if(reactantToReactantPatternOne.containsKey(ssldMolecule)) {
                throw new RuntimeException("reactantToReactantPatternOne: Duplicate Key");
            }
            reactantToReactantPatternOne.put(ssldMolecule, rp);
        }
        ReactantPattern getReactantPatternOneFromMolecule(Molecule ssldMolecule) {
            return reactantToReactantPatternOne.get(ssldMolecule);
        }
        void putMoleculeToProductPatternOne(Molecule ssldMolecule, ProductPattern pp) {
            if(reactantToProductPatternOne.containsKey(ssldMolecule)) {
                throw new RuntimeException("reactantToProductPatternOne: Duplicate Key");
            }
            reactantToProductPatternOne.put(ssldMolecule, pp);
        }
        ProductPattern getProductPatternOneFromMolecule(Molecule ssldMolecule) {
            return reactantToProductPatternOne.get(ssldMolecule);
        }

        // -------------------------------------------------------------------------------
        void putMoleculeToReactantPatternTwo(Molecule ssldMolecule, ReactantPattern rp) {
            if(reactantToReactantPatternTwo.containsKey(ssldMolecule)) {
                throw new RuntimeException("reactantToReactantPatternTwo: Duplicate Key");
            }
            reactantToReactantPatternTwo.put(ssldMolecule, rp);
        }
        ReactantPattern getReactantPatternTwoFromMolecule(Molecule ssldMolecule) {
            return reactantToReactantPatternTwo.get(ssldMolecule);
        }
        void putMoleculeToProductPatternTwo(Molecule ssldMolecule, ProductPattern pp) {
            if(reactantToProductPatternTwo.containsKey(ssldMolecule)) {
                throw new RuntimeException("reactantToProductPatternTwo: Duplicate Key");
            }
            reactantToProductPatternTwo.put(ssldMolecule, pp);
        }
        ProductPattern getProductPatternTwoFromMolecule(Molecule ssldMolecule) {
            return reactantToProductPatternTwo.get(ssldMolecule);
        }

        // -----------------------------------------------------------------
        void putCondition(Molecule ssldMolecule, ReactantPattern rp) {
            if(conditionToReactantPattern.containsKey(ssldMolecule)) {
                throw new RuntimeException("conditionToReactantPattern: Duplicate Key");
            }
            conditionToReactantPattern.put(ssldMolecule, rp);
        }
        ReactantPattern getConditionReactantPattern(Molecule ssldMolecule) {
            return conditionToReactantPattern.get(ssldMolecule);
        }
        void putCondition(Molecule ssldMolecule, ProductPattern pp) {
            if(conditionToProductPattern.containsKey(ssldMolecule)) {
                throw new RuntimeException("conditionToProductPattern: Duplicate Key");
            }
            conditionToProductPattern.put(ssldMolecule, pp);
        }
        ProductPattern getConditionProductPattern(Molecule ssldMolecule) {
            return conditionToProductPattern.get(ssldMolecule);
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

        // ---------- Molecular Types
        for(Molecule ssldMolecule : ssldModel.getMolecules()) {
            MolecularType mt = new MolecularType(ssldMolecule.getName(), model);
            for(Site ssldSite : ssldMolecule.getSiteArray()) {
                SiteType ssldType = ssldSite.getType();
                MolecularComponent mc = new MolecularComponent(ssldType.getName());

                // site index starts at 1 in vcell, while it starts at 0 in ssld
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
        // ---------- Species Contexts
        // TODO: make these

        // ---------- Transition Reactions
        for(TransitionReaction ssldReaction : ssldModel.getTransitionReactions()) {
            m.cleanReactionsMaps();
            boolean reversible = false;
            Molecule ssldTransitionMolecule = ssldReaction.getMolecule();
            Molecule ssldConditionalMolecule = ssldReaction.getConditionalMolecule();   //
            String location = ssldTransitionMolecule.getLocation(); // reaction happens in the same structure, no transport
            Structure structure = model.getStructure(location);

            double ssldKf = ssldReaction.getRate();
            Expression kf = new Expression(ssldKf);
            ReactionRule reactionRule = new ReactionRule(model, ssldReaction.getName(), structure, reversible);
            RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
            reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
            reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate, kf);

            SpeciesPattern spReactant = getSpeciesPattern(ssldConditionalMolecule, ssldTransitionMolecule, m);
            SpeciesPattern spProduct = getSpeciesPattern(ssldConditionalMolecule, ssldTransitionMolecule, m);
            ReactantPattern rp = new ReactantPattern(spReactant, structure);
            ProductPattern pp = new ProductPattern(spProduct, structure);

            if(ssldConditionalMolecule != null) {
                m.putCondition(ssldConditionalMolecule, rp);
                m.putCondition(ssldConditionalMolecule, pp);
            }
            m.putMoleculeToReactantPatternOne(ssldTransitionMolecule, rp);
            m.putMoleculeToProductPatternOne(ssldTransitionMolecule, pp);
            // at this point everything is trivial
            // now we start adjusting the binding sites
            adjustTransitionReactionSites(ssldReaction, m);

            reactionRule.addReactant(rp);
            reactionRule.addProduct(pp);
            bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            m.put(ssldReaction, reactionRule);
        }
        // ---------- BindingReaction
        for(BindingReaction ssldReaction : ssldModel.getBindingReactions()) {
            m.cleanReactionsMaps();
//            boolean reversible = ssldReaction.getkoff() != 0 ? true : false;
            boolean reversible = true;      // in springsalad, binding reactions must be reversible
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

            double ssldKf = ssldReaction.getkon();
            Expression kf = new Expression(ssldKf);
            double ssldKr = ssldReaction.getkoff();
            Expression kr = new Expression(ssldKr);
            ReactionRule reactionRule = new ReactionRule(model, ssldReaction.getName(), structure, reversible);
            RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
            reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
            reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate, kf);
            reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionReverseRate, kr);

            SpeciesPattern spReactantOne = getSpeciesPattern(ssldReactantOne, m);
            SpeciesPattern spReactantTwo = getSpeciesPattern(ssldReactantTwo, m);
            SpeciesPattern spProduct = getSpeciesPattern(ssldReactantOne, ssldReactantTwo, m);

            ReactantPattern rpOne = new ReactantPattern(spReactantOne, model.getStructure(locationOne));
            ReactantPattern rpTwo = new ReactantPattern(spReactantTwo, model.getStructure(locationTwo));
            ProductPattern pp = new ProductPattern(spProduct, structure);

            m.putMoleculeToReactantPatternOne(ssldReactantOne, rpOne);
            m.putMoleculeToReactantPatternTwo(ssldReactantTwo, rpTwo);
            m.putMoleculeToProductPatternOne(ssldReactantOne, pp);
            m.putMoleculeToProductPatternTwo(ssldReactantTwo, pp);
            // at this point everything is trivial
            // now we start adjusting the binding sites
            adjustReactantSite(ssldReaction, 0, m);
            adjustReactantSite(ssldReaction, 1, m);
            adjustProductSite(ssldReaction, m);

            reactionRule.addReactant(rpOne);
            reactionRule.addReactant(rpTwo);
            reactionRule.addProduct(pp);
            bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            m.put(ssldReaction, reactionRule);
        }

        // ---------- Decay reactions
        // in vcell creation and destruction reactions are separate
        // ssls decay reactions are being stored in the molecule rather than the model
        Map<Molecule, DecayReaction> ssldMoleculeToCreationReactions = new LinkedHashMap<>();
        Map<Molecule, DecayReaction> ssldMoleculeToDestructionReactions = new LinkedHashMap<>();
        for(Molecule ssldMolecule : ssldModel.getMolecules()) {
            DecayReaction ssldReaction = ssldMolecule.getDecayReaction();
            if(ssldReaction != null && ssldReaction.getCreationRate() != 0) {
                ssldMoleculeToCreationReactions.put(ssldMolecule, ssldReaction);
            }
            if(ssldReaction != null && ssldReaction.getDecayRate() != 0) {
                ssldMoleculeToDestructionReactions.put(ssldMolecule, ssldReaction);
            }
        }
        if(!ssldMoleculeToCreationReactions.isEmpty()) {
            MolecularType mtSource = new MolecularType(SpeciesContextSpec.SourceMoleculeString, model);
            model.getRbmModelContainer().addMolecularType(mtSource, false);
            for (Map.Entry<Molecule, DecayReaction> entry : ssldMoleculeToCreationReactions.entrySet()) {
                // mtSource -> ssldMolecule  (Creation Reaction)
                Molecule ssldMolecule = entry.getKey();
                DecayReaction ssldReaction = entry.getValue();
                double ssldRate = ssldReaction.getCreationRate();
                Expression kf = new Expression(ssldRate);
                String suffix = ""; // if a ssld molecule has both rates, we need to add a suffix to reaction names (because we make 2 distinct reactions)
                if(ssldReaction.getDecayRate() != 0) {
                    suffix = "_Creation";
                }

                String location = ssldMolecule.getLocation();
                Structure structure = model.getStructure(location);
                boolean reversible = false;
                ReactionRule reactionRule = new ReactionRule(model, ssldReaction.getName() + suffix, structure, reversible);
                RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
                reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
                reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate, kf);

                SpeciesPattern spReactant = new SpeciesPattern();
                MolecularTypePattern mtpReactant = new MolecularTypePattern(mtSource);
                spReactant.addMolecularTypePattern(mtpReactant);
                ReactantPattern rp = new ReactantPattern(spReactant, structure);
                reactionRule.addReactant(rp);

                SpeciesPattern spProduct = getSpeciesPattern(ssldMolecule, m);
                // TODO: sp must not be ambiguous! it must be a concrete species! all sites must be unbound and with specified states
                ProductPattern pp = new ProductPattern(spProduct, structure);
                reactionRule.addProduct(pp);
                bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            }
        }
        if(!ssldMoleculeToDestructionReactions.isEmpty()) {
            MolecularType mtSink = new MolecularType(SpeciesContextSpec.SinkMoleculeString, model);
            model.getRbmModelContainer().addMolecularType(mtSink, false);
            for (Map.Entry<Molecule, DecayReaction> entry : ssldMoleculeToDestructionReactions.entrySet()) {
                // ssldMolecule -> mtSink (Decay reaction)
                Molecule ssldMolecule = entry.getKey();
                DecayReaction ssldReaction = entry.getValue();
                double ssldRate = ssldReaction.getDecayRate();
                Expression kf = new Expression(ssldRate);
                String suffix = "";
                if(ssldReaction.getCreationRate() != 0) {
                    suffix = "_Decay";
                }

                String location = ssldMolecule.getLocation();
                Structure structure = model.getStructure(location);
                boolean reversible = false;
                ReactionRule reactionRule = new ReactionRule(model, ssldReaction.getName() + suffix, structure, reversible);
                RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
                reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
                reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate, kf);

                SpeciesPattern spReactant = getSpeciesPattern(ssldMolecule, m);
                // TODO: sp must not be ambiguous! it must be a concrete species! all sites must be unbound and with specified states
                ReactantPattern rp = new ReactantPattern(spReactant, structure);
                reactionRule.addReactant(rp);

                SpeciesPattern spProduct = new SpeciesPattern();
                MolecularTypePattern mtpProduct = new MolecularTypePattern(mtSink);
                spProduct.addMolecularTypePattern(mtpProduct);
                ProductPattern pp = new ProductPattern(spProduct, structure);
                reactionRule.addProduct(pp);
                bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            }
        }


        return bioModel;
    }

    private void adjustTransitionReactionSites(TransitionReaction ssldReaction, Mapping m) {
        Molecule ssldConditionalMolecule = ssldReaction.getConditionalMolecule();
        Molecule ssldTransitionMolecule = ssldReaction.getMolecule();

        int transitionMoleculeIndexInSpeciesPattern;
        if(ssldConditionalMolecule != null) {
            if(!ssldReaction.getCondition().equals(TransitionReaction.BOUND_CONDITION)) {
                throw new RuntimeException("Inconsistent transition reaction");
            }
            if(ssldTransitionMolecule != ssldConditionalMolecule) {
                transitionMoleculeIndexInSpeciesPattern = 0;
            } else {
                transitionMoleculeIndexInSpeciesPattern = 1;
            }
            SiteType conditionalType = ssldReaction.getConditionalType();
            State ssldConditionalState = ssldReaction.getConditionalState();

            SpeciesPattern spReactant = m.getConditionReactantPattern(ssldConditionalMolecule).getSpeciesPattern();
            MolecularTypePattern mtpConditionReactant = spReactant.getMolecularTypePatterns(ssldConditionalMolecule.getName()).get(0);
            MolecularComponentPattern mcpConditionReactant = mtpConditionReactant.getMolecularComponentPattern(conditionalType.getName());
            if(ssldConditionalState != TransitionReaction.ANY_STATE) {
                String ssldConditionalStateName = ssldConditionalState.getName();
                ComponentStateDefinition csdConditionReactant = mcpConditionReactant.getMolecularComponent().getComponentStateDefinition(ssldConditionalStateName);
                ComponentStatePattern cspConditionalReactant = new ComponentStatePattern(csdConditionReactant);
                mcpConditionReactant.setComponentStatePattern(cspConditionalReactant);
            }
            mcpConditionReactant.setBondType(MolecularComponentPattern.BondType.Exists);
            mcpConditionReactant.setBond(null);

            SpeciesPattern spProduct = m.getConditionProductPattern(ssldConditionalMolecule).getSpeciesPattern();
            MolecularTypePattern mtpConditionProduct = spProduct.getMolecularTypePatterns(ssldConditionalMolecule.getName()).get(0);
            MolecularComponentPattern mcpConditionProduct = mtpConditionProduct.getMolecularComponentPattern(conditionalType.getName());
            if(ssldConditionalState != TransitionReaction.ANY_STATE) {
                String ssldConditionalStateName = ssldConditionalState.getName();
                ComponentStateDefinition csdConditionProduct = mcpConditionProduct.getMolecularComponent().getComponentStateDefinition(ssldConditionalStateName);
                ComponentStatePattern cspConditionalProduct = new ComponentStatePattern(csdConditionProduct);
                mcpConditionProduct.setComponentStatePattern(cspConditionalProduct);
            }
            mcpConditionProduct.setBondType(MolecularComponentPattern.BondType.Exists);
            mcpConditionProduct.setBond(null);
        } else {
            transitionMoleculeIndexInSpeciesPattern = 0;
        }
        SiteType ssldSiteType = ssldReaction.getType();
        State ssldInitialState = ssldReaction.getInitialState();
        State ssldFinalState = ssldReaction.getFinalState();

        SpeciesPattern spReactant = m.getReactantPatternOneFromMolecule(ssldTransitionMolecule).getSpeciesPattern();
        MolecularTypePattern mtpTransitionReactant = spReactant.getMolecularTypePatterns(ssldTransitionMolecule.getName()).get(transitionMoleculeIndexInSpeciesPattern);
        MolecularComponentPattern mcpTransitionReactant = mtpTransitionReactant.getMolecularComponentPattern(ssldSiteType.getName());
        String ssldInitialStateName = ssldInitialState.getName();
        ComponentStateDefinition csdInitial = mcpTransitionReactant.getMolecularComponent().getComponentStateDefinition(ssldInitialStateName);
        ComponentStatePattern cspTransitionReactant = new ComponentStatePattern(csdInitial);
        mcpTransitionReactant.setComponentStatePattern(cspTransitionReactant);
        // bond is already set to Any
        if(ssldReaction.getCondition().equals(TransitionReaction.NO_CONDITION)) {
            for(MolecularComponentPattern mcp : mtpTransitionReactant.getComponentPatternList()) {
                if(mcp == mcpTransitionReactant) {
                    continue;
                }
                mcp.setBondType(MolecularComponentPattern.BondType.None);
            }
        }

        SpeciesPattern spProduct = m.getProductPatternOneFromMolecule(ssldTransitionMolecule).getSpeciesPattern();
        MolecularTypePattern mtpTransitionProduct = spProduct.getMolecularTypePatterns(ssldTransitionMolecule.getName()).get(transitionMoleculeIndexInSpeciesPattern);
        MolecularComponentPattern mcpTransitionProduct = mtpTransitionProduct.getMolecularComponentPattern(ssldSiteType.getName());
        String ssldFinalStateName = ssldFinalState.getName();
        ComponentStateDefinition csdFinal = mcpTransitionProduct.getMolecularComponent().getComponentStateDefinition(ssldFinalStateName);
        ComponentStatePattern cspTransitionProduct = new ComponentStatePattern(csdFinal);
        mcpTransitionProduct.setComponentStatePattern(cspTransitionProduct);
        System.out.println("  " + ssldReaction.getCondition());
        if(ssldReaction.getCondition().equals(TransitionReaction.NO_CONDITION)) {
            for(MolecularComponentPattern mcp : mtpTransitionProduct.getComponentPatternList()) {
                if(mcp == mcpTransitionProduct) {
                    continue;
                }
                mcp.setBondType(MolecularComponentPattern.BondType.None);
            }
        }
    }



    private void adjustProductSite(BindingReaction ssldReaction, Mapping m) {
        Molecule ssldMoleculeOne = ssldReaction.getMolecule(0);
        SiteType ssldSiteTypeOne = ssldReaction.getType(0);
        State ssldStateOne = ssldReaction.getState(0);
        SpeciesPattern spProduct = m.getProductPatternOneFromMolecule(ssldMoleculeOne).getSpeciesPattern();
        List<MolecularTypePattern> mtpListOne = spProduct.getMolecularTypePatterns(ssldMoleculeOne.getName());
        MolecularTypePattern mtpProductOne = mtpListOne.get(0);
        MolecularComponentPattern mcpProductOne = mtpProductOne.getMolecularComponentPattern(ssldSiteTypeOne.getName());
        String ssldStateOneName = ssldStateOne.getName();
        if(!ssldStateOneName.contentEquals(ANY_STATE_STRING)) {
            ComponentStateDefinition csd = mcpProductOne.getMolecularComponent().getComponentStateDefinition(ssldStateOneName);
            ComponentStatePattern cspProductOne = new ComponentStatePattern(csd);
            mcpProductOne.setComponentStatePattern(cspProductOne);
        }
        Molecule ssldMoleculeTwo = ssldReaction.getMolecule(1);
        SiteType ssldSiteTypeTwo = ssldReaction.getType(1);
        State ssldStateTwo = ssldReaction.getState(1);
        SpeciesPattern spProductTwo = m.getProductPatternTwoFromMolecule(ssldMoleculeTwo).getSpeciesPattern();
        if(spProduct != spProductTwo) {     // we load spProductTwo just to verify consistency
            throw new RuntimeException("Instances of the same species pattern should be equal");
        }
        List<MolecularTypePattern> mtpListTwo = spProduct.getMolecularTypePatterns(ssldMoleculeTwo.getName());
        MolecularTypePattern mtpProductTwo;
        if(ssldMoleculeOne == ssldMoleculeTwo) {
            mtpProductTwo = mtpListTwo.get(1);
        } else {
            mtpProductTwo = mtpListTwo.get(0);
        }
        MolecularComponentPattern mcpProductTwo = mtpProductTwo.getMolecularComponentPattern(ssldSiteTypeTwo.getName());
        String ssldStateTwoName = ssldStateTwo.getName();
        if(!ssldStateTwoName.contentEquals(ANY_STATE_STRING)) {
            ComponentStateDefinition csd = mcpProductTwo.getMolecularComponent().getComponentStateDefinition(ssldStateTwoName);
            ComponentStatePattern cspProductTwo = new ComponentStatePattern(csd);
            mcpProductTwo.setComponentStatePattern(cspProductTwo);
        }
        // we know for sure that this is the only explicit bond in each of the 2 molecular type patterns of the product
        int bondId = 1;     // correct would be:  sp.nextBondId();
        mcpProductOne.setBondId(bondId);    // this also sets the BondType to Specified
        mcpProductTwo.setBondId(bondId);
        // very abusive way of bypassing the fact that the Bond constructor is private
        mcpProductOne.setBond(SpeciesPattern.generateBond(mtpProductTwo, mcpProductTwo));
        mcpProductTwo.setBond(SpeciesPattern.generateBond(mtpProductOne, mcpProductOne));
    }
    private void adjustReactantSite(BindingReaction ssldReaction, int reactantIndex, Mapping m) {
        Molecule ssldMolecule = ssldReaction.getMolecule(reactantIndex);
        SiteType ssldSiteType = ssldReaction.getType(reactantIndex);
        State ssldState = ssldReaction.getState(reactantIndex);
        SpeciesPattern spReactant;
        if(reactantIndex == 0) {
            spReactant = m.getReactantPatternOneFromMolecule(ssldMolecule).getSpeciesPattern();
        } else {
            spReactant = m.getReactantPatternTwoFromMolecule(ssldMolecule).getSpeciesPattern();
        }
        MolecularTypePattern mtpReactant = spReactant.getMolecularTypePatterns(ssldMolecule.getName()).get(0);
        MolecularComponentPattern mcpReactant = mtpReactant.getMolecularComponentPattern(ssldSiteType.getName());
        String ssldStateName = ssldState.getName();
        if(!ssldStateName.contentEquals(ANY_STATE_STRING)) {
            ComponentStateDefinition csd = mcpReactant.getMolecularComponent().getComponentStateDefinition(ssldStateName);
            ComponentStatePattern cspReactant = new ComponentStatePattern(csd);
            mcpReactant.setComponentStatePattern(cspReactant);
        }
        mcpReactant.setBondType(MolecularComponentPattern.BondType.None);
        mcpReactant.setBond(null);
    }

    private SpeciesPattern getSpeciesPattern(Molecule moleculeOne, Molecule moleculeTwo, Mapping m) {
        if(moleculeOne == null) {
            // binding reaction products have 2 molecules
            // conditional transition reactions have 2 molecules, condition being the first
            // all the rest have one molecule
            return getSpeciesPattern(moleculeTwo, m);
        }
        SpeciesPattern sp = new SpeciesPattern();
        MolecularTypePattern mtpOne = getMolecularTypePattern(moleculeOne, m);      // condition molecule
        sp.addMolecularTypePattern(mtpOne);
        MolecularTypePattern mtpTwo = getMolecularTypePattern(moleculeTwo, m);      // transition molecule
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
