package org.vcell.sbml.vcell;

import cbit.vcell.mapping.BioEvent;
import cbit.vcell.model.*;
import cbit.vcell.model.Species;
import cbit.vcell.parser.Expression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sbml.jsbml.*;

import java.util.*;
import java.util.stream.Collectors;

public class SBMLSymbolMapping {

    public interface DummySymbolTableEntry extends EditableSymbolTableEntry {
    }

    public static class SBaseWrapper<T extends SBase> {
        private T internalSBase = null;
        public SBaseWrapper(T object) {
            if (object == null){
                throw new IllegalArgumentException("null object");
            }
            this.internalSBase = object;
        }
        @Override
        public boolean equals(Object object) {
            if (object instanceof SBaseWrapper){
                return (internalSBase == ((SBaseWrapper)object).internalSBase);
            }else{
                return false;
            }
        }
        T getSBase() {
            return internalSBase;
        }
        @Override
        public int hashCode() {
            //
            // equals() has been overridden to support reference equates
            // hashCode could be overridden, but as long as it gives the same value every time (immutability), it will work.
            // because equals() will only match the same object.
            //
            return System.identityHashCode(internalSBase);  // either uses Object.hashCode or based on value
        }
    }


    private final static Logger logger = LogManager.getLogger(SBMLSymbolMapping.class);
    private final Map<SBaseWrapper<SBase>, EditableSymbolTableEntry> sbase_to_initial_ste_map = new LinkedHashMap<>();
    private final Map<SBaseWrapper<SBase>, EditableSymbolTableEntry> sbase_to_runtime_ste_map = new LinkedHashMap<>();
    private final Map<SBaseWrapper<Compartment>, Structure> compartment_to_structure_map = new LinkedHashMap<>();
    private final Map<SBaseWrapper<Event>, BioEvent> event_to_bioevent_map = new LinkedHashMap<>();
    private final Map<SBaseWrapper<SBase>, Expression> sbase_to_assignmentRuleSbmlExpression_map = new LinkedHashMap<>();
    private final Map<SBaseWrapper<SBase>, Expression> sbase_to_initialAssignmentSbmlExpression_map = new LinkedHashMap<>();
    private final Map<SBaseWrapper<SBase>, Expression> sbase_to_rateRuleSbmlExpression_map = new LinkedHashMap<>();
    private final Map<SBaseWrapper<SBase>, Double> sbase_to_sbmlValue_map = new LinkedHashMap<>();

    private final Map<SBaseWrapper<Reaction>, ReactionStep> sbmlReaction_to_vcellReactionStep_map = new LinkedHashMap<>();

    //
    // only used locally in SBMLImporter, could replace with a local map.
    // Or, could add an SBML Species to VCell SpeciesContext map for use in SEDML processing?
    //
    private final Map<SBaseWrapper<org.sbml.jsbml.Species>, Species> sbmlSpecies_to_vcSpecies_map = new LinkedHashMap<>();

    SBMLSymbolMapping() {
        super();
    }

    public void putReactionMapping(Reaction sbmlReaction, ReactionStep vcReaction) {
        sbmlReaction_to_vcellReactionStep_map.put(new SBaseWrapper<>(sbmlReaction), vcReaction);
    }

    public void getVCellReactionStep(Reaction _sbmlReaction) {
        SBaseWrapper<SBase> sbaseWrapper = new SBaseWrapper<>(_sbmlReaction);
        sbmlReaction_to_vcellReactionStep_map.get(sbaseWrapper);
    }

    public List<Reaction> getMappedSbmlReactions(){
        return sbmlReaction_to_vcellReactionStep_map.keySet().stream()
                .map(reactionSBaseWrapper -> (Reaction)reactionSBaseWrapper.internalSBase)
                .collect(Collectors.toList());
    }

    EditableSymbolTableEntry getInitialSte(SBase _sbase) {
        SBaseWrapper<SBase> sbaseWrapper = new SBaseWrapper<>(_sbase);
        return sbase_to_initial_ste_map.get(sbaseWrapper);
    }

    EditableSymbolTableEntry getRuntimeSte(SBase _sbase) {
        SBaseWrapper<SBase> sbaseWrapper = new SBaseWrapper<>(_sbase);
        return sbase_to_runtime_ste_map.get(sbaseWrapper);
    }

    SBase getSBase(EditableSymbolTableEntry ste, SymbolContext symbolContext) {
        final SBase[] matches;
        switch (symbolContext) {
            case RUNTIME: {
                matches = sbase_to_runtime_ste_map.entrySet().stream().filter(entry -> entry.getValue() == ste)
                        .map(Map.Entry::getKey).map(sbaseWrapper -> sbaseWrapper.internalSBase).toArray(SBase[]::new);
                break;
            }
            case INITIAL: {
                matches = sbase_to_initial_ste_map.entrySet().stream().filter(entry -> entry.getValue() == ste)
                        .map(Map.Entry::getKey).map(sbaseWrapper -> sbaseWrapper.internalSBase).toArray(SBase[]::new);
                break;
            }
            default:
                throw new RuntimeException("unexpected symbolContext " + symbolContext);
        }
        if (matches.length == 1) {
            return matches[0];
        } else if (matches.length == 0) {
            return null;
        } else {
            throw new RuntimeException("multiple SBase objects map to ste " + ste);
        }
    }

    void putInitial(SBase _sbase, EditableSymbolTableEntry ste) {
        SBaseWrapper<SBase> sbaseWrapper = new SBaseWrapper<>(_sbase);
        EditableSymbolTableEntry s = sbase_to_initial_ste_map.get(sbaseWrapper);
        if (s != null && s != ste) {
            throw new RuntimeException("sbmlSid is already bound to initial ste " + ste.getName());
        } else {
            sbase_to_initial_ste_map.put(sbaseWrapper, ste);
        }
    }

    void putRuntime(SBase _sbase, EditableSymbolTableEntry ste) {
        SBaseWrapper<SBase> sbaseWrapper = new SBaseWrapper<>(_sbase);
        EditableSymbolTableEntry s = sbase_to_runtime_ste_map.get(sbaseWrapper);
        if (s != null && s != ste) {
            throw new RuntimeException("sbmlSid is already bound to runtime ste " + s.getClass().getSimpleName()+"("+s.getName()+"), trying to bind to " + ste.getClass().getSimpleName()+"("+ste.getName()+")");
        } else {
            sbase_to_runtime_ste_map.put(sbaseWrapper, ste);
        }
    }

    void replaceRuntime(SBase _sbase, EditableSymbolTableEntry ste) {
        SBaseWrapper<SBase> sbaseWrapper = new SBaseWrapper<>(_sbase);
        EditableSymbolTableEntry s = sbase_to_runtime_ste_map.get(sbaseWrapper);
        if (s == null) {
            throw new RuntimeException("calling replaceRuntime for sbmlSid " + _sbase.getId() + " not mapped prior, trying to replace with " + ste.getClass().getSimpleName()+"("+s.getName()+"), trying to rebind to " + ste.getClass().getSimpleName()+"("+ste.getName()+")");
        } else {
            sbase_to_runtime_ste_map.put(sbaseWrapper, ste);
        }
    }

    Set<SBaseWrapper<SBase>> getAllSbaseWrappers() {
        LinkedHashSet<SBaseWrapper<SBase>> allInitialSBase = new LinkedHashSet<>(sbase_to_initial_ste_map.keySet());
        LinkedHashSet<SBaseWrapper<SBase>> allRuntimeSBase = new LinkedHashSet<>(sbase_to_runtime_ste_map.keySet());
        allInitialSBase.addAll(allRuntimeSBase);
        return allInitialSBase;
    }

    public Set<SBase> getAllSbase() {
        Set<SBase> allSBase = getAllSbaseWrappers().stream().map(wrapper -> wrapper.internalSBase).collect(Collectors.toSet());
        return allSBase;
    }

    public SBase getMappedSBase(String sid){
        ArrayList<SBaseWrapper<SBase>> matches = new ArrayList<>();
        for (SBaseWrapper<SBase> sbaseWrapper : getAllSbaseWrappers()){
            if (sbaseWrapper.internalSBase.getId().equals(sid)){
                matches.add(sbaseWrapper);
            }
        }
        if (matches.size() == 0){
            return null;
        } else if (matches.size() == 1){
            return matches.get(0).internalSBase;
        } else {
            throw new RuntimeException("found more than one SBase match for sid="+sid+", matched "+matches);
        }
    }

    void putStructure(Compartment _compartment, Structure structure) {
        SBaseWrapper<Compartment> compartmentWrapper = new SBaseWrapper<>(_compartment);
        Structure s = compartment_to_structure_map.get(compartmentWrapper);
        if (s != null && s != structure) {
            throw new RuntimeException("sbmlSid is already bound to a structure " + s.getName()+", trying to bind to structure " + structure.getName());
        } else {
            compartment_to_structure_map.put(compartmentWrapper, structure);
        }
    }

    Structure getStructure(Compartment _compartment){
        SBaseWrapper<Compartment> compartmentWrapper = new SBaseWrapper<>(_compartment);
        return compartment_to_structure_map.get(compartmentWrapper);
    }

    @Deprecated
    void putBioEvent(Event _event, BioEvent vcEvent) {
        SBaseWrapper<Event> eventWrapper = new SBaseWrapper<>(_event);
        BioEvent e = event_to_bioevent_map.get(eventWrapper);
        if (e != null && e != vcEvent) {
            throw new RuntimeException("sbmlSid is already bound to an Event " + e.getName()+", trying to bind to event "+vcEvent.getName());
        } else {
            event_to_bioevent_map.put(eventWrapper, vcEvent);
        }
    }

    @Deprecated
    BioEvent getBioEvent(Event _sbmlEvent){
        SBaseWrapper<Event> eventWrapper = new SBaseWrapper<>(_sbmlEvent);
        return event_to_bioevent_map.get(eventWrapper);
    }

    /**
     * note that this only maps to Species not SpeciesContext - should be moved as a local map in SBMLImporter
     */
    @Deprecated
    Species getVCellSpecies(org.sbml.jsbml.Species _sbmlSpecies) {
        SBaseWrapper<org.sbml.jsbml.Species> speciesWrapper = new SBaseWrapper<>(_sbmlSpecies);
        return sbmlSpecies_to_vcSpecies_map.get(speciesWrapper);
    }

    /**
     * note that this only maps to Species not SpeciesContext - should be moved as a local map in SBMLImporter
     */
    @Deprecated
    void putVCellSpecies(org.sbml.jsbml.Species _sbmlSpecies, cbit.vcell.model.Species vcSpecies) {
        SBaseWrapper<org.sbml.jsbml.Species> speciesWrapper = new SBaseWrapper<>(_sbmlSpecies);
        cbit.vcell.model.Species s = sbmlSpecies_to_vcSpecies_map.get(speciesWrapper);
        if (s != null && s != vcSpecies) {
            throw new RuntimeException("sbmlSid is already bound to a species " + s.getCommonName()+", trying to bind to species "+vcSpecies.getCommonName());
        } else {
            sbmlSpecies_to_vcSpecies_map.put(speciesWrapper, vcSpecies);
        }
    }

    /**
     * note that this only maps to Species not SpeciesContext - should be moved as a local map in SBMLImporter
     */
    Species[] getVCellSpeciesArray() {
        return this.sbmlSpecies_to_vcSpecies_map.values().toArray(new Species[0]);
    }


    public EditableSymbolTableEntry getSte(SBase sbase, SymbolContext symbolContext) {
        // doesn't need an SBaseWrapper, because it doesn't directly use maps.
        if (symbolContext.equals(SymbolContext.RUNTIME)) {
            return getRuntimeSte(sbase);
        } else if (symbolContext.equals(SymbolContext.INITIAL)) {
            EditableSymbolTableEntry ste = getInitialSte(sbase);
            if (ste == null) {
                ste = getRuntimeSte(sbase);
            }
            return ste;
        } else {
            throw new RuntimeException("expecting either " + SymbolContext.INITIAL.name() + " or " + SymbolContext.RUNTIME.name());
        }
    }

    void putAssignmentRuleSbmlExpression(SBase _targetSBase, Expression sbmlAssignmentRuleMathExpr) {
        SBaseWrapper<SBase> targetSBaseWrapper = new SBaseWrapper<>(_targetSBase);
        Expression e = sbase_to_assignmentRuleSbmlExpression_map.get(targetSBaseWrapper);
        if (e != null) {
            if (e.compareEqual(sbmlAssignmentRuleMathExpr)) {
                logger.info("assignRule targetSBase " + _targetSBase + " is already bound to the same SBML expression " + e.infix());
            } else {
                logger.warn("assignRule targetSBase " + _targetSBase + " is already bound to another SBML expression," +
                        " previous=" + e.infix()+", new="+sbmlAssignmentRuleMathExpr);
            }
        } else {
            logger.trace("assignRule targetSBase " + _targetSBase + " mapped to SBML expression " + sbmlAssignmentRuleMathExpr.infix());
            sbase_to_assignmentRuleSbmlExpression_map.put(targetSBaseWrapper, sbmlAssignmentRuleMathExpr);
        }
    }

    void putInitialAssignmentSbmlExpression(SBase _targetSBase, Expression sbmlInitialAssignmentRuleExpr) {
        SBaseWrapper<SBase> targetSBaseWrapper = new SBaseWrapper<>(_targetSBase);
        Expression e = sbase_to_initialAssignmentSbmlExpression_map.get(targetSBaseWrapper);
        if (e != null) {
            if (e.compareEqual(sbmlInitialAssignmentRuleExpr)) {
                logger.info("initialAssign targetSBase " + _targetSBase + " is already bound to the same SBML expression " + e.infix());
            } else {
                logger.warn("initialAssign targetSBase " + _targetSBase + " is already bound to another SBML expression," +
                        " previous=" + e.infix() + ", new=" + sbmlInitialAssignmentRuleExpr.infix());
            }
        } else {
            logger.trace("initialAssign targetSBase " + _targetSBase + " mapped to SBML expression " + sbmlInitialAssignmentRuleExpr.infix());
            sbase_to_initialAssignmentSbmlExpression_map.put(targetSBaseWrapper, sbmlInitialAssignmentRuleExpr);
        }
    }

    /**
     * this is to capture the value="5.0" attributes of SBML Elements for later processing
     */
    void putSbmlValue(SBase _targetSBase, Double sbmlValue) {
        SBaseWrapper<SBase> targetSBaseWrapper = new SBaseWrapper<>(_targetSBase);
        Double v = sbase_to_sbmlValue_map.get(targetSBaseWrapper);
        if (v != null) {
            if (v.equals(sbmlValue)) {
                logger.info("sbase value targetSBase " + _targetSBase + " is already bound to the same SBML value " + v);
            } else {
                logger.warn("sbase value targetSBase " + _targetSBase + " is already bound to another SBML value," +
                        " previous=" + v + ", new=" + sbmlValue);
            }
        } else {
            logger.trace("sbase value targetSBase " + _targetSBase + " mapped to SBML expression " + sbmlValue);
            sbase_to_sbmlValue_map.put(targetSBaseWrapper, sbmlValue);
        }
    }
    Double getSbmlValue(SBase _sbase) {
        SBaseWrapper<SBase> sbaseWrapper = new SBaseWrapper<>(_sbase);
        return sbase_to_sbmlValue_map.get(sbaseWrapper);
    }

    void putRateRuleSbmlExpression(SBase _targetSBase, Expression sbmlRateRuleExpr) {
        SBaseWrapper<SBase> targetSBaseWrapper = new SBaseWrapper<>(_targetSBase);
        Expression e = sbase_to_rateRuleSbmlExpression_map.get(targetSBaseWrapper);
        if (e != null) {
            if (e.compareEqual(sbmlRateRuleExpr)) {
                logger.info("targetSBase " + _targetSBase + " is already bound to the same SBML expression " + e.infix());
            } else {
                logger.warn("targetSBase " + _targetSBase + " is already bound to another SBML expression," +
                        " previous=" + e.infix() + ", new=" + sbmlRateRuleExpr.infix());
            }
        } else {
            sbase_to_rateRuleSbmlExpression_map.put(targetSBaseWrapper, sbmlRateRuleExpr);
        }
    }

    public Expression getRuleSBMLExpression(SBase _sbase, SymbolContext symbolContext) {
        SBaseWrapper<SBase> sbaseWrapper = new SBaseWrapper<>(_sbase);
        switch (symbolContext) {
            case INITIAL: {
                //
                // for INITIAL context, favor initial assignment rule over assignment rule
                //
                Expression sbmlExpression = sbase_to_initialAssignmentSbmlExpression_map.get(sbaseWrapper);
                if (sbmlExpression == null) {
                    sbmlExpression = sbase_to_assignmentRuleSbmlExpression_map.get(sbaseWrapper);
                }
                if (sbmlExpression == null) {
                    Double sbmlValue = sbase_to_sbmlValue_map.get(sbaseWrapper);
                    if (sbmlValue != null){
                        sbmlExpression = new Expression(sbmlValue);
                    }
                }
                return sbmlExpression;
                //break;
            }
            case RUNTIME: {
                //
                // for RUNTIME context, favor initial assignment rule over initial assignment rule
                // TODO JCS generalize this logic for all INITIAL/RUNTIME lookups
                //
                Expression sbmlExpression = sbase_to_assignmentRuleSbmlExpression_map.get(sbaseWrapper);
                if (sbmlExpression == null) {
                    Expression initialSbmlExpr = sbase_to_initialAssignmentSbmlExpression_map.get(sbaseWrapper);
                    if (initialSbmlExpr != null) {
                        if (_sbase instanceof Variable && !((Variable) _sbase).isConstant()) {
                            sbmlExpression = initialSbmlExpr;
                            if (sbmlExpression == null) {
                                Double sbmlValue = sbase_to_sbmlValue_map.get(sbaseWrapper);
                                if (sbmlValue != null){
                                    sbmlExpression = new Expression(sbmlValue);
                                }
                            }
                        } else {
                            logger.warn("ignoring initial assignment '" + initialSbmlExpr.infix() + "' for sbase " + _sbase.getId() + " in RUNTIME context (it is not constant species)");
                        }
                    }
                }
                return sbmlExpression;
                //break;
            }
            default:
                throw new RuntimeException("unexpected symbolContext " + symbolContext);
        }
    }

    Expression getRateRuleSBMLExpression(SBase _sbase) {
        SBaseWrapper<SBase> sbaseWrapper = new SBaseWrapper<>(_sbase);
        return sbase_to_rateRuleSbmlExpression_map.get(sbaseWrapper);
    }

    Set<SBase> getSbmlValueTargets() {
        return sbase_to_sbmlValue_map.keySet().stream()
                .map(sbaseWrapper -> sbaseWrapper.internalSBase).collect(Collectors.toSet());
    }

    Set<SBase> getInitialAssignmentTargets() {
        return sbase_to_initialAssignmentSbmlExpression_map.keySet().stream()
                .map(sbaseWrapper -> sbaseWrapper.internalSBase).collect(Collectors.toSet());
    }

    Set<SBase> getAssignmentRuleTargets() {
        return sbase_to_assignmentRuleSbmlExpression_map.keySet().stream()
                .map(sbaseWrapper -> sbaseWrapper.internalSBase).collect(Collectors.toSet());
    }

}
