package org.jlibsedml.components.task;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;
import org.jlibsedml.components.model.Change;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
<listOfSubTasks>
    <subTask task="task1" >
        <listOfDependTasks>
            <dependendTask task="task2" />
        </listOfDependTasks>
    </subTask>
    <subTask task="task2" />
</listOfSubTasks>
*/

public class SubTask extends SedBase {
    private static final Logger log = LoggerFactory.getLogger(SubTask.class);

    private final SId task;     // SubTask is basically a pointer to another task to run repeatedly; `this.task` is the id of that task.
    private final Integer order;
    
    public SubTask(SId task) {
        this(null, null, null, task);
    }

    public SubTask(SId id, String name, SId task) {
        this(id, name, null, task);
    }

    public SubTask(Integer order, SId task) {
        this(null, null, order, task);
    }

    public SubTask(SId id, String name, Integer order, SId task) {
        super(id, name);
        this.order = order;
        SedGeneralClass.checkNoNullArgs(task);
        this.task = task;
        if(order == null) return;

    }

    public SId getTask() {
        return this.task;
    }
    public Integer getOrder() {
        return this.order;
    }

    @Override
    public boolean equals(Object obj){
        if (null == obj) return false;
        if (!(obj instanceof SubTask subTask)) return false;
        return Objects.equals(this.getId(), subTask.getId())
                && Objects.equals(this.getName(), subTask.getName())
                && this.getTask().equals(subTask.getTask())
                && this.getOrder().equals(subTask.getOrder());
    }

    public int hashCode(){
        return (this.getClass().getSimpleName() + "::" + (this.getId() == null ? "<null>" : this.getId().string()) + "::" + this.getName() + "::" +
                this.getTask().string()+ "::" + this.getOrder()).hashCode();
    }

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.SUBTASK_TAG;
    }

    @Override
    public String parametersToString() {
        List<String> params = new ArrayList<>();
        if (this.order != null) params.add(String.format("order={%s}", this.order));
        params.add(String.format("task={%s}", this.task.string()));
        return super.parametersToString() + ", " + String.join(", ", params);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        return super.searchFor(idOfElement);
    }
}
