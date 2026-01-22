package org.jlibsedml.components.task;

import java.util.ArrayList;
import java.util.List;

import org.jlibsedml.*;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.listOfConstructs.ListOfRanges;
import org.jlibsedml.components.listOfConstructs.ListOfRepeatedTaskChanges;
import org.jlibsedml.components.listOfConstructs.ListOfSubTasks;
import org.jlibsedml.components.model.Change;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepeatedTask extends AbstractTask {
    private static final Logger logger = LoggerFactory.getLogger(RepeatedTask.class);
    private boolean resetModel;
    private SId range;

    private final ListOfRanges ranges = new ListOfRanges();
    private final ListOfRepeatedTaskChanges changes = new ListOfRepeatedTaskChanges();
    private final ListOfSubTasks subTasks = new ListOfSubTasks();

    public RepeatedTask(SId id, String name, boolean resetModel, SId range) {
        super(id, name);
        this.resetModel = resetModel;
        this.range = range;
    }

    public boolean getResetModel() {
        return this.resetModel;
    }
    public void setResetModel(boolean resetModel) {
        this.resetModel = resetModel;
    }

    public SId getRange() {
        return this.range;
    }
    public void setRange(SId range) {
        this.range = range;
    }

    public Range getRange(SId rangeId) {
        return this.ranges.getContentById(rangeId);
    }

    public ListOfRanges getListOfRanges() {
        return this.ranges;
    }

    public List<Range> getRanges() {
        return this.ranges.getContents();
    }

    public void addRange(Range range) {
        this.ranges.addContent(range);
    }

    public void removeRange(Range range) {
        this.ranges.removeContent(range);
    }

    public ListOfRepeatedTaskChanges getListOfChanges() {
        return this.changes;
    }
    public List<SetValue> getChanges() {
        return this.changes.getContents();
    }
    public void addChange(SetValue change) {
        this.changes.addContent(change);
    }

    public ListOfSubTasks getListOfSubTasks() {
        return this.subTasks;
    }
    public List<SubTask> getSubTasks() {
        return this.subTasks.getContents();
    }
    public void addSubtask(SubTask subTask) {
        if (subTask == null ) throw new IllegalArgumentException("subTask cannot be null");
        if (subTask.getTask() == null || subTask.getTask().string().isEmpty()) {
            logger.warn("subtask cant't be null, key can't be null, key can't be empty string");
            logger.warn("   ...subtask " + subTask.getTask().string() + " not added to list");
            return;     // subtask can't be null, key can't be null, key can't be ""
        }
        if(this.getId().equals(subTask.getTask())) {
            logger.warn("'this' repeated task cannot be a subtask for itself");
            logger.warn("   ...subtask " + subTask.getTask() + " not added to list");
            return;     // "this" repeated task cannot be a subtask for itself
        }
        this.subTasks.addContent(subTask);
    }
    
     @Override
    public String getElementName() {
        return SedMLTags.REPEATED_TASK_TAG;
    }

    @Override
    public String parametersToString() {
        List<String> params = new ArrayList<>(), rangeParams = new ArrayList<>(),
                changesParams = new ArrayList<>(), subTasksParams = new ArrayList<>();
        params.add(String.format("resetModel=%b", this.getResetModel()));
        for (Range r : this.ranges.getContents()) rangeParams.add(r.getId() != null ? r.getId().string() : '{' + r.parametersToString() + '}');
        for (SetValue setVal : this.changes.getContents()) changesParams.add(setVal.getId() != null ? setVal.getId().string() : '{' + setVal.parametersToString() + '}');
        for (SubTask subTask : this.subTasks.getContents()) subTasksParams.add(subTask.getId() != null ? subTask.getId().string() : '{' + subTask.parametersToString() + '}');
        params.add(String.format("ranges=[%s]", String.join(", ",  rangeParams)));
        params.add(String.format("changes=[%s]", String.join(", ",  changesParams)));
        params.add(String.format("subTasks=[%s]", String.join(", ",  subTasksParams)));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        SedBase elementFound = super.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        for (Range range : this.getRanges()) {
            elementFound = range.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        for (Change c : this.getChanges()) {
            elementFound = c.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        for (SubTask st : this.getSubTasks()) {
            elementFound = st.searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        return elementFound;
    }
}
