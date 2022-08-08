package org.vcell.sbml.vcell;

import cbit.vcell.mapping.BioEvent;
import cbit.vcell.model.EditableSymbolTableEntry;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sbml.jsbml.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SBMLSymbolMapping {

    private final static Logger logger = LogManager.getLogger(SBMLSymbolMapping.class);
    private final Map<SBase, EditableSymbolTableEntry> sbase_to_initial_ste_map = new LinkedHashMap<>();
    private final Map<SBase, EditableSymbolTableEntry> sbase_to_runtime_ste_map = new LinkedHashMap<>();
    private final Map<Compartment, Structure> compartment_to_structure_map = new LinkedHashMap<>();
    private final Map<Event, BioEvent> event_to_bioevent_map = new LinkedHashMap<>();
    private final Map<SBase, Expression> sbase_to_assignmentRuleSbmlExpression_map = new LinkedHashMap<>();
    private final Map<SBase, Expression> sbase_to_initialAssignmentSbmlExpression_map = new LinkedHashMap<>();
    private final Map<SBase, Expression> sbase_to_rateRuleSbmlExpression_map = new LinkedHashMap<>();
    private final Map<SBase, Double> sbase_to_sbmlValue_map = new LinkedHashMap<>();

    //
    // only used locally in SBMLImporter, could replace with a local map.
    // Or, could add an SBML Species to VCell SpeciesContext map for use in SEDML processing?
    //
    private final Map<org.sbml.jsbml.Species, Species> sbmlSpecies_to_vcSpecies_map = new LinkedHashMap<>();

    public SBMLSymbolMapping() {
        super();
    }

    public EditableSymbolTableEntry getInitialSte(SBase sbase) {
        return sbase_to_initial_ste_map.get(sbase);
    }

    public EditableSymbolTableEntry getRuntimeSte(SBase sbase) {
        return sbase_to_runtime_ste_map.get(sbase);
    }

    public SBase getSBase(EditableSymbolTableEntry ste, SymbolContext symbolContext) {
        final SBase[] matches;
        switch (symbolContext) {
            case RUNTIME: {
                matches = sbase_to_runtime_ste_map.entrySet().stream().filter(entry -> entry.getValue() == ste)
                        .map(Map.Entry::getKey).toArray(SBase[]::new);
                break;
            }
            case INITIAL: {
                matches = sbase_to_initial_ste_map.entrySet().stream().filter(entry -> entry.getValue() == ste)
                        .map(Map.Entry::getKey).toArray(SBase[]::new);
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

    public void putInitial(SBase sbase, EditableSymbolTableEntry ste) {
        EditableSymbolTableEntry s = sbase_to_initial_ste_map.get(sbase);
        if (s != null && s != ste) {
            throw new RuntimeException("sbmlSid is already bound to initial ste " + ste.getName());
        } else {
            sbase_to_initial_ste_map.put(sbase, ste);
        }
    }

    public void putRuntime(SBase sbase, EditableSymbolTableEntry ste) {
        EditableSymbolTableEntry s = sbase_to_runtime_ste_map.get(sbase);
        if (s != null && s != ste) {
            throw new RuntimeException("sbmlSid is already bound to runtime ste " + ste.getName());
        } else {
            sbase_to_runtime_ste_map.put(sbase, ste);
        }
    }

    public void putStructure(Compartment compartment, Structure structure) {
        Structure s = compartment_to_structure_map.get(compartment);
        if (s != null && s != structure) {
            throw new RuntimeException("sbmlSid is already bound to a structure " + structure.getName());
        } else {
            compartment_to_structure_map.put(compartment, structure);
        }
    }

    public Structure getStructure(Compartment compartment){
        return compartment_to_structure_map.get(compartment);
    }

    @Deprecated
    public void putBioEvent(Event event, BioEvent vcEvent) {
        BioEvent e = event_to_bioevent_map.get(event);
        if (e != null && e != vcEvent) {
            throw new RuntimeException("sbmlSid is already bound to an Event " + vcEvent.getName());
        } else {
            event_to_bioevent_map.put(event, vcEvent);
        }
    }

    @Deprecated
    public BioEvent getBioEvent(Event sbmlEvent){
        return event_to_bioevent_map.get(sbmlEvent);
    }

    /**
     * note that this only maps to Species not SpeciesContext - should be moved as a local map in SBMLImporter
     */
    @Deprecated
    public Species getVCellSpecies(org.sbml.jsbml.Species sbmlSpecies) {
        return sbmlSpecies_to_vcSpecies_map.get(sbmlSpecies);
    }

    /**
     * note that this only maps to Species not SpeciesContext - should be moved as a local map in SBMLImporter
     */
    @Deprecated
    public void putVCellSpecies(org.sbml.jsbml.Species sbmlSpecies, cbit.vcell.model.Species vcSpecies) {
        cbit.vcell.model.Species s = sbmlSpecies_to_vcSpecies_map.get(sbmlSpecies);
        if (s != null && s != vcSpecies) {
            throw new RuntimeException("sbmlSid is already bound to a species " + vcSpecies.getCommonName());
        } else {
            sbmlSpecies_to_vcSpecies_map.put(sbmlSpecies, vcSpecies);
        }
    }

    /**
     * note that this only maps to Species not SpeciesContext - should be moved as a local map in SBMLImporter
     */
    public Species[] getVCellSpeciesArray() {
        return this.sbmlSpecies_to_vcSpecies_map.values().toArray(new Species[0]);
    }


    public EditableSymbolTableEntry getSte(SBase sbase, SymbolContext symbolContext) {
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

    public void putAssignmentRuleSbmlExpression(SBase targetSBase, Expression sbmlAssignmentRuleMathExpr) {
        Expression e = sbase_to_assignmentRuleSbmlExpression_map.get(targetSBase);
        if (e != null) {
            if (e.compareEqual(sbmlAssignmentRuleMathExpr)) {
                logger.info("assignRule targetSBase " + targetSBase + " is already bound to the same SBML expression " + e.infix());
            } else {
                logger.warn("assignRule targetSBase " + targetSBase + " is already bound to another SBML expression," +
                        " previous=" + e.infix()+", new="+sbmlAssignmentRuleMathExpr);
            }
        } else {
            logger.trace("assignRule targetSBase " + targetSBase + " mapped to SBML expression " + sbmlAssignmentRuleMathExpr.infix());
            sbase_to_assignmentRuleSbmlExpression_map.put(targetSBase, sbmlAssignmentRuleMathExpr);
        }
    }

    public void putInitialAssignmentSbmlExpression(SBase targetSBase, Expression sbmlInitialAssignmentRuleExpr) {
        Expression e = sbase_to_initialAssignmentSbmlExpression_map.get(targetSBase);
        if (e != null) {
            if (e.compareEqual(sbmlInitialAssignmentRuleExpr)) {
                logger.info("initialAssign targetSBase " + targetSBase + " is already bound to the same SBML expression " + e.infix());
            } else {
                logger.warn("initialAssign targetSBase " + targetSBase + " is already bound to another SBML expression," +
                        " previous=" + e.infix() + ", new=" + sbmlInitialAssignmentRuleExpr.infix());
            }
        } else {
            logger.trace("initialAssign targetSBase " + targetSBase + " mapped to SBML expression " + sbmlInitialAssignmentRuleExpr.infix());
            sbase_to_initialAssignmentSbmlExpression_map.put(targetSBase, sbmlInitialAssignmentRuleExpr);
        }
    }

    /**
     * this is to capture the value="5.0" attributes of SBML Elements for later processing
     * @param targetSBase
     * @param sbmlValue
     */
    public void putSbmlValue(SBase targetSBase, Double sbmlValue) {
        Double v = sbase_to_sbmlValue_map.get(targetSBase);
        if (v != null) {
            if (v.equals(sbmlValue)) {
                logger.info("sbase value targetSBase " + targetSBase + " is already bound to the same SBML value " + v);
            } else {
                logger.warn("sbase value targetSBase " + targetSBase + " is already bound to another SBML value," +
                        " previous=" + v + ", new=" + sbmlValue);
            }
        } else {
            logger.trace("sbase value targetSBase " + targetSBase + " mapped to SBML expression " + sbmlValue);
            sbase_to_sbmlValue_map.put(targetSBase, sbmlValue);
        }
    }
    public Double getSbmlValue(SBase sbase) {
        return sbase_to_sbmlValue_map.get(sbase);
    }

    public void putRateRuleSbmlExpression(SBase targetSBase, Expression sbmlRateRuleExpr) {
        Expression e = sbase_to_rateRuleSbmlExpression_map.get(targetSBase);
        if (e != null) {
            if (e.compareEqual(sbmlRateRuleExpr)) {
                logger.info("targetSBase " + targetSBase + " is already bound to the same SBML expression " + e.infix());
            } else {
                logger.warn("targetSBase " + targetSBase + " is already bound to another SBML expression," +
                        " previous=" + e.infix() + ", new=" + sbmlRateRuleExpr.infix());
            }
        } else {
            sbase_to_rateRuleSbmlExpression_map.put(targetSBase, sbmlRateRuleExpr);
        }
    }

    public Expression getRuleSBMLExpression(SBase sbase, SymbolContext symbolContext) {
        switch (symbolContext) {
            case INITIAL: {
                //
                // for INITIAL context, favor initial assignment rule over assignment rule
                //
                Expression sbmlExpression = sbase_to_initialAssignmentSbmlExpression_map.get(sbase);
                if (sbmlExpression == null) {
                    sbmlExpression = sbase_to_assignmentRuleSbmlExpression_map.get(sbase);
                }
                if (sbmlExpression == null) {
                    Double sbmlValue = sbase_to_sbmlValue_map.get(sbase);
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
                Expression sbmlExpression = sbase_to_assignmentRuleSbmlExpression_map.get(sbase);
                if (sbmlExpression == null) {
                    Expression initialSbmlExpr = sbase_to_initialAssignmentSbmlExpression_map.get(sbase);
                    if (initialSbmlExpr != null) {
                        if (sbase instanceof Variable && !((Variable) sbase).isConstant()) {
                            sbmlExpression = initialSbmlExpr;
                            if (sbmlExpression == null) {
                                Double sbmlValue = sbase_to_sbmlValue_map.get(sbase);
                                if (sbmlValue != null){
                                    sbmlExpression = new Expression(sbmlValue);
                                }
                            }
                        } else {
                            logger.warn("ignoring initial assignment '" + initialSbmlExpr.infix() + "' for sbase " + sbase.getId() + " in RUNTIME context (it is not constant species)");
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

    public Expression getRateRuleSBMLExpression(SBase sbase) {
        return sbase_to_rateRuleSbmlExpression_map.get(sbase);
    }

    public Set<SBase> getSbmlValueTargets() {
        return sbase_to_sbmlValue_map.keySet();
    }

    public Set<SBase> getInitialAssignmentTargets() {
        return sbase_to_initialAssignmentSbmlExpression_map.keySet();
    }

    public Set<SBase> getAssignmentRuleTargets() {
        return sbase_to_assignmentRuleSbmlExpression_map.keySet();
    }

}
