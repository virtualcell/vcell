package org.jlibsedml;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubTask {
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

    private String order;
    private String taskId = new String();     // id of the Task which is a SubTask of the current RepeatedTask
    private Map<String, SubTask> dependentTasks = new HashMap<String, SubTask> ();
    private RepeatedTask ownerTask = null;
    private Logger log = LoggerFactory.getLogger(SubTask.class);
    
    public SubTask(String taskId) {
        this.order = null;
        this.taskId = taskId;
    }
    public SubTask(String order, String taskId) {
        this(taskId);
        if(order == null) {
            return;
        }
        try {
            Integer i = Integer.parseInt(order);    // we just check whether can be parsed to an int
            this.order = order;
        } catch (NumberFormatException e) {
            log.warn("SubTask: order is not an Integer: " + order);
            this.order = null;
        }
    }
    
    @Override
    public String toString() {
        return "SubTask ["
        + "getTaskId()=" + getTaskId()
        + ", getOrder()=" + getOrder()
        + ", dependentTasks.size()=" + dependentTasks.size()
        + "]";
    }

    public String getTaskId() {
        return taskId;
    }
    public String getOrder() {
        return order;
    }
    public void addDependentTask(SubTask dependentTask) {
        if(dependentTask == null || dependentTask.getTaskId() == null || dependentTask.getTaskId().equals("")) {
            log.warn("dependentTask cant't be null, key can't be null, key can't be empty string");
            log.warn("   ...dependent task not added to list");
            return;     // dependentTask cant't be null, key can't be null, key can't be ""
        }
        if(this.getTaskId().equals(dependentTask.getTaskId())) {
            log.warn("'this' subTask cannot be a dependentTask for itself");
            log.warn("   ...dependent task " + dependentTask.getTaskId() + " not added to list");
            return;     // "this" subTask cannot be a dependentTask for itself
        }
        if(ownerTask != null && ownerTask.getId().equals(dependentTask.getTaskId())) {
            log.warn("the RepeatedTask which owns this subTask cannot be a dependentTask for itself");
            log.warn("   ...dependent task " + dependentTask.getTaskId() + " not added to list");
            return;     // the RepeatedTask which owns this subTask cannot be a dependentTask for itself
        }
        if(!dependentTasks.containsKey(dependentTask.getTaskId())) {  // no duplicates
            dependentTasks.put(dependentTask.getTaskId(), dependentTask);
        } else {
            log.warn("dependent task already in dependent task list");
            log.warn("   ...dependent task " + dependentTask.getTaskId() + " not added to list");
            return;
        }
    }
    public Map<String, SubTask> getDependentTasks() {
        return dependentTasks;
    }
    public void removeOwnerFromDependentTasksList(RepeatedTask repeatedTask) {
        this.ownerTask = repeatedTask;
        if(dependentTasks != null && !dependentTasks.isEmpty()) {
            for(SubTask dt : dependentTasks.values()) {
                if(ownerTask.getId().equals(dt.getTaskId())) {
                    dependentTasks.remove(dt.getTaskId());
                    log.warn("the RepeatedTask which owns this subTask cannot be a dependentTask for itself");
                    log.warn("   ...dependent task " + dt.getTaskId() + " removed from list");
                    return;
                }
            }
        }
    }
}
