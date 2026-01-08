package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.output.Output;
import org.jlibsedml.components.output.Plot2D;
import org.jlibsedml.components.output.Plot3D;
import org.jlibsedml.components.output.Report;

import java.util.*;


/**
 * This class is a special case for overrides, because we want to group outputs by type.
 * A Tree-set with a custom comparator is used to keep efficient ordering of outputs.
 */
public class ListOfOutputs extends ListOf<Output> {

    /**
     * Constructor to create empty list
     */
    public ListOfOutputs() {
        this(null, null);
    }

    /**
     * Constructor to create empty list (with id and name as options)
     */
    public ListOfOutputs(SId id, String name) {
        this(id, name, Collections.emptyList());
    }

    /**
     * Constructor allowing elements to be passed in.
     * @param elements the elements to add
     */
    public ListOfOutputs(List<Output> elements) {
        this(null, null, elements);
    }

    /**
     * Constructor allowing elements to be passed in.
     * @param elements the elements to add
     */
    public ListOfOutputs(SId id, String name, List<Output> elements) {
        super(id, name, elements, new OutputComparator());
    }

    /**
     * Gets the contents as an unmodifiable list
     * @return an unmodifiable {@link List} of type {@link Output}
     */
    @Override // Declaring this because technically, List<Output> != List<T> ... even though it is in this class.
    public List<Output> getContents() {
        return this.contents.stream().toList();
    }

    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.OUTPUTS;
    }

    /**
     * Sorts a list of Outputs into the correct order specified in the schema.
     *
     * @author radams
     *
     */
    private static class OutputComparator extends GeneralListOfComparator<Output> {
        private static final Map<Class<? extends Output>, Integer> subclassPriorityMapping;
        static {
            subclassPriorityMapping = new HashMap<>();
            subclassPriorityMapping.put(Plot2D.class, 0);
            subclassPriorityMapping.put(Plot3D.class, 1);
            subclassPriorityMapping.put(Report.class, 2);
        }

        public int compare(Output o1, Output o2) {
            if (o1 == o2) return 0;
            Class<? extends Output> c1 = o1.getClass(), c2 = o2.getClass();
            Integer priority1 = subclassPriorityMapping.get(c1), priority2 = subclassPriorityMapping.get(c2);
            int firstCompare = Integer.compare(priority1, priority2);
            if (firstCompare != 0) return firstCompare;
            return super.compare(o1, o2);
        }

    }
}
