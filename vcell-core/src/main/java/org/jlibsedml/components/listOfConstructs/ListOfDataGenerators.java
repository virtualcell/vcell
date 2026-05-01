package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.Variable;
import org.jlibsedml.components.dataGenerator.DataGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ListOfDataGenerators extends ListOf<DataGenerator> {
    private Map<SId, Set<SId>> variableIdToDataGenerators = new HashMap<>();

    public ListOfDataGenerators clone() throws CloneNotSupportedException {
        ListOfDataGenerators clone = (ListOfDataGenerators) super.clone();
        Map<SId, Set<SId>> clonedVariableIdToDataGenerators = new HashMap<>();
        for (SId id : this.variableIdToDataGenerators.keySet()) {
            Set<SId> cloneSet = new HashSet<>();
            for (SId dataGenId : this.variableIdToDataGenerators.get(id)) {
                cloneSet.add(new SId(dataGenId.string()));
            }
            clonedVariableIdToDataGenerators.put(new SId(id.string()), cloneSet);
        }
        clone.variableIdToDataGenerators = clonedVariableIdToDataGenerators;
        return clone;
    }

    /**
     * Ease-of-use function to help when auto-creating "identity" DataGenerators
     * @param var a variable to potentially be referenced by a new data generator
     * @return true, if an identity data-generator already exists, else false
     */
    public boolean appropriateIdentityDataGeneratorAlreadyExistsFor(Variable var){
        if (!this.variableIdToDataGenerators.containsKey(var.getId())) return false;
        for (SId dataGenId : this.variableIdToDataGenerators.get(var.getId())) {
            DataGenerator targetDataGenerator = this.contentIdMapping.get(dataGenId);
            if (!targetDataGenerator.containsVariable(var)) continue;
            // Identity DataGenerator => data generator that does no modification to a variable, just "renames" it as a DataGenerator
            if (targetDataGenerator.getVariables().size() > 1) continue;
            if (!targetDataGenerator.getParameters().isEmpty()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void addContent(DataGenerator dataGenerator) {
        if (null == dataGenerator) return;
        SId contentId = dataGenerator.getId();
        if (null != contentId) {
            if (this.contentIdMapping.containsKey(contentId)) return; // Do not override what we have
            this.contentIdMapping.put(contentId, dataGenerator);
        }
        for (Variable var: dataGenerator.getVariables()){
            SId varId = var.getId();
            if (varId == null) continue;
            if (!this.variableIdToDataGenerators.containsKey(varId))
                this.variableIdToDataGenerators.put(varId, new HashSet<>());
            this.variableIdToDataGenerators.get(varId).add(contentId);
        }
        this.contents.add(dataGenerator);
    }

    @Override
    public void removeContent(DataGenerator content) {
        if (null == content) return;
        if (content.getId() != null) for (Variable var: content.getVariables()){
            if (!this.variableIdToDataGenerators.containsKey(var.getId())) continue;
            this.variableIdToDataGenerators.get(var.getId()).remove(content.getId());
        }
        this.contents.remove(content);
    }

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.DATA_GENERATORS;
    }
}
