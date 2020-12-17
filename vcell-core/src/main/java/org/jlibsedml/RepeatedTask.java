package org.jlibsedml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepeatedTask extends AbstractTask {
    Logger   log = LoggerFactory.getLogger(RepeatedTask.class);
    private boolean resetModel = false;
    private String range = new String();
    
    private Map<String, Range> ranges = new HashMap<String, Range> ();
    private List<SetValue> changes = new ArrayList<SetValue> ();
    private Map<String, SubTask> subTasks = new HashMap<String, SubTask> ();
    
    public boolean getResetModel() {
        return resetModel;
    }
    public void setResetModel(boolean resetModel) {
        this.resetModel = resetModel;
    }
    public String getRange() {
        return range;
    }
    public void setRange(String range) {
        this.range = range;
    }
    public Range getRange(String rangeId) {
        return ranges.get(rangeId);
    }
    public void addRange(Range range) {
        if(!ranges.containsKey(range.getId())) {
            ranges.put(range.getId(), range);
        } else {
            log.warn("range already in ranges list");
            log.warn("   ...range " + range.getId() + " not added to list");
        }
    }
    
    public Map<String, Range> getRanges() {
        return ranges;
    }
    public void addChange(SetValue change) {
        changes.add(change);
    }
    public List<SetValue> getChanges() {
        return changes;
    }
    public void addSubtask(SubTask subTask) {
        if(subTask == null || subTask.getTaskId() == null || subTask.getTaskId().equals("")) {
            log.warn("subtask cant't be null, key can't be null, key can't be empty string");
            log.warn("   ...subtask " + subTask.getTaskId() + " not added to list");
            return;     // subtask cant't be null, key can't be null, key can't be ""
        }
        if(this.getId().equals(subTask.getTaskId())) {
            log.warn("'this' repeated task cannot be a subtask for itself");
            log.warn("   ...subtask " + subTask.getTaskId() + " not added to list");
            return;     // "this" repeated task cannot be a subtask for itself
        }
        if(!subTasks.containsKey(subTask.getTaskId())) {        // no duplicates
            subTasks.put(subTask.getTaskId(), subTask);
            subTask.removeOwnerFromDependentTasksList(this);    // this repeated task cannot depend on itself
        } else {
            log.warn("subtask already in subtasks list");
            log.warn("...subtask {} not added to list",subTask.getTaskId());
            return;
        }
    }
    public Map<String, SubTask> getSubTasks() {
        return subTasks;
    }

    public RepeatedTask(String id, String name, boolean resetModel, String range) {
        super(id, name);
        this.resetModel = resetModel;
        this.range = range;
    }
    
    @Override
    public String toString() {
        return "Repeated Task ["
        + "name=" + getName()
        + ", getId()=" + getId()
        + ", resetModel=" + resetModel
        + ", ranges.size()=" + ranges.size()
        + ", changes.size()=" + changes.size()
        + ", subTasks.size()=" + subTasks.size()
        + "]";
    }
    
     @Override
        public String getElementName() {
            return SEDMLTags.REPEATED_TASK_TAG;
        }

     @Override
     public boolean accept(SEDMLVisitor visitor) {
         return visitor.visit(this);
     }
    @Override
    public String getModelReference() {
       throw new UnsupportedOperationException("Not supported by RepeatedTask");
    }
    @Override
    public String getSimulationReference() {
        throw new UnsupportedOperationException("Not supported by Repeated task");
    }
    
   
    
}
