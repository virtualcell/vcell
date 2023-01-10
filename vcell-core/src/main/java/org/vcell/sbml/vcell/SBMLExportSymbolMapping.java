package org.vcell.sbml.vcell;

import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.EventAssignment;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SBMLExportSymbolMapping {
    public final Map<AssignmentRule, Expression> assignmentRuleToVcmlExpressionMap = new LinkedHashMap<>();	// key = assignment rule, value = expression needing replacement of vcml ste with sbml sid

    public final Map<org.sbml.jsbml.RateRule, Expression> rateRuleToVcmlExpressionMap = new LinkedHashMap<> ();				// key = rate rule, value = expression needing replacement of vcml ste with sbml sid

    public final Map<SBMLSymbolMapping.SBaseWrapper<EventAssignment>, Expression> eventAssignmentToVcmlExpressionMap = new LinkedHashMap<> (); // key = EventAssignment wrapper, value = expression needing replacement of vcml ste with sbml sid

    private final Map<SymbolTableEntry, String> symbolTableEntryToSidMap = new LinkedHashMap<> ();  // key = vcell ste name, value = sbml entity id	ReservedSymbol rs = vcBioModel.getModel().getReservedSymbolByName(name);
    public final Map<Structure, String> structureToSidMap = new LinkedHashMap<> ();  // key = vcell ste name, value = sbml entity id	ReservedSymbol rs = vcBioModel.getModel().getReservedSymbolByName(name);

    public final Map<org.sbml.jsbml.InitialAssignment, Expression> initialAssignmentToVcmlExpressionMap = new LinkedHashMap<> ();	// key = initial assignment, value = expression needing replacement of vcml ste with sbml sid

    public final Set<String> reservedSymbolSet = new LinkedHashSet<>();			// the vcell reserved symbols

    public String getSid(SymbolTableEntry ste) {
        if (ste instanceof ProxyParameter){
            ste = ((ProxyParameter)ste).getTarget();
        }
        return symbolTableEntryToSidMap.get(ste);
    }

    public void putSteToSidMapping(SymbolTableEntry ste, String sid) {
        if (ste instanceof ProxyParameter){
            ste = ((ProxyParameter)ste).getTarget();
        }
        symbolTableEntryToSidMap.put(ste, sid);
    }

    public void clear() {
        symbolTableEntryToSidMap.clear();
    }
}
