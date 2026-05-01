package org.jlibsedml.components.task;

import org.jlibsedml.SedMLElementFactory;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;
import org.jlibsedml.components.SedGeneralClass;

public abstract class AbstractTask extends SedBase {

    public AbstractTask(SId id, String name) {
        super(id, name);
        if(SedMLElementFactory.getInstance().isStrictCreation()){
            SedGeneralClass.checkNoNullArgs(id);
        }
    }

    public AbstractTask clone() throws CloneNotSupportedException {
        return (AbstractTask) super.clone();
    }
}
