package org.jlibsedml.components.task;


import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private String order;
    
    public SubTask(SId task) {
        this(null, null, null, task);
    }

    public SubTask(SId id, String name, SId task) {
        this(id, name, null, task);
    }

    public SubTask(String order, SId task) {
        this(null, null, order, task);
    }

    public SubTask(SId id, String name, String order, SId task) {
        super(id, name);
        this.order = order;
        SedGeneralClass.checkNoNullArgs(task);
        this.task = task;
        if(order == null) return;
        try {
            Integer i = Integer.parseInt(order);    // we just check whether it can be parsed to an int
            this.order = order;
        } catch (NumberFormatException e) {
            log.warn("SubTask: order is not an Integer: " + order);
            this.order = null;
        }
    }

    @Override
    public boolean accept(SEDMLVisitor visitor) {
        return true;
    }

    public SId getTask() {
        return this.task;
    }
    public String getOrder() {
        return this.order;
    }

    @Override
    public boolean equals(Object obj){
        if (null == obj) return false;
        if (!(obj instanceof SubTask subTask)) return false;
        return  this.getId().equals(subTask.getId())
                && this.getName().equals(subTask.getName())
                && this.getTask().equals(subTask.getTask())
                && this.getOrder().equals(subTask.getOrder());
    }

    public int hashCode(){
        return (this.getClass().getSimpleName() + "::" + this.getId().string() + "::" + this.getName() + "::" +
                this.getTask().string()+ "::" + this.getOrder()).hashCode();
    }

//    public void addDependentTask(SubTask dependentTask) {
//        if(dependentTask == null || dependentTask.getTask() == null || dependentTask.getTask().equals("")) {
//            log.warn("dependentTask cant't be null, key can't be null, key can't be empty string");
//            log.warn("   ...dependent task not added to list");
//            return;     // dependentTask cant't be null, key can't be null, key can't be ""
//        }
//        if(this.getTask().equals(dependentTask.getTask())) {
//            log.warn("'this' subTask cannot be a dependentTask for itself");
//            log.warn("   ...dependent task " + dependentTask.getTask() + " not added to list");
//            return;     // "this" subTask cannot be a dependentTask for itself
//        }
//        if(ownerTask != null && ownerTask.getId().equals(dependentTask.getTask())) {
//            log.warn("the RepeatedTask which owns this subTask cannot be a dependentTask for itself");
//            log.warn("   ...dependent task " + dependentTask.getTask() + " not added to list");
//            return;     // the RepeatedTask which owns this subTask cannot be a dependentTask for itself
//        }
//        if(!dependentTasks.containsKey(dependentTask.getTask())) {  // no duplicates
//            dependentTasks.put(dependentTask.getTask(), dependentTask);
//        } else {
//            log.warn("dependent task already in dependent task list");
//            log.warn("   ...dependent task " + dependentTask.getTask() + " not added to list");
//            return;
//        }
//    }
//    public Map<String, SubTask> getDependentTasks() {
//        return dependentTasks;
//    }
//    public void removeOwnerFromDependentTasksList(RepeatedTask repeatedTask) {
//        this.ownerTask = repeatedTask;
//        if(dependentTasks != null && !dependentTasks.isEmpty()) {
//            for(SubTask dt : dependentTasks.values()) {
//                if(ownerTask.getId().equals(dt.getTask())) {
//                    dependentTasks.remove(dt.getTask());
//                    log.warn("the RepeatedTask which owns this subTask cannot be a dependentTask for itself");
//                    log.warn("   ...dependent task " + dt.getTask() + " removed from list");
//                    return;
//                }
//            }
//        }
//    }

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.SUBTASK_TAG;
    }
}
