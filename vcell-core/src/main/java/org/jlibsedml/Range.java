package org.jlibsedml;

public abstract class Range extends AbstractIdentifiableElement{

    public Range(String id) {
        super(id, "");
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
