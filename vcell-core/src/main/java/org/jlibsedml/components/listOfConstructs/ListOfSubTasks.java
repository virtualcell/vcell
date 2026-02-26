package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.task.SubTask;

public class ListOfSubTasks extends ListOf<SubTask> {
    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.REPEATED_TASK_SUBTASKS_LIST;
    }

    public ListOfSubTasks clone() throws CloneNotSupportedException {
        return (ListOfSubTasks) super.clone();
    }
}
