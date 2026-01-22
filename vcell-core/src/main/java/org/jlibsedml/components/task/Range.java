package org.jlibsedml.components.task;

import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;

public abstract class Range extends SedBase {

    public Range(SId id) {
        this(id, null);
    }

    public Range(SId id, String name) {
        super(id, name);
    }
    /**
     * Gets the number of elements in this range
     * @return the number of elements.
     */
    public abstract int getNumElements();
    
    /**
     * Gets the element in this range at the specified index.
     * @param index A zero-based index into the range.
     * @return The value of the range at the given index.
     * @throws IllegalArgumentException if <code>index</code> is invalid - 
     *  negative, or  larger than <code> getNumElements() -1 </code>.
     */
    public abstract double getElementAt(int index);
}
