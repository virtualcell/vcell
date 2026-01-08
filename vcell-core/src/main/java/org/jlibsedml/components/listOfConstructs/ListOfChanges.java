package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SedMLTags;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.model.Change;
import org.jlibsedml.components.output.Output;

import java.util.*;

public class ListOfChanges extends ListOf<Change> {

    /**
     * Constructor to create empty list
     */
    public ListOfChanges() {
        this(null, null);
    }

    /**
     * Constructor to create empty list (with id and name as options)
     */
    public ListOfChanges(SId id, String name) {
        this(id, name, Collections.emptyList());
    }

    /**
     * Constructor allowing elements to be passed in.
     * @param elements the elements to add
     */
    public ListOfChanges(List<Change> elements) {
        this(null, null, elements);
    }

    /**
     * Constructor allowing elements to be passed in.
     * @param elements the elements to add
     */
    public ListOfChanges(SId id, String name, List<Change> elements) {
        super(id, name, elements, new ListOfChanges.ChangeComparator());
    }

    /**
     * Gets the contents as an unmodifiable list
     * @return an unmodifiable {@link List} of type {@link Output}
     */
    @Override // Declaring this because technically, List<Output> != List<T> ... even though it is in this class.
    public List<Change> getContents() {
        return this.contents.stream().toList();
    }


    /**
     * Provides a link between the object model and the XML element names
     *
     * @return A non-null <code>String</code> of the XML element name of the object.
     */
    @Override
    public String getElementName() {
        return SedMLTags.CHANGES;
    }

    /**
     * Sorts a list of Changes into the correct order specified in the schema.
     * @author radams
     *
     */
    static class ChangeComparator extends GeneralListOfComparator<Change> {
        static Map<String, Integer> changeKindOrder;
        static {
            ChangeComparator.changeKindOrder = new HashMap<>();
            ChangeComparator.changeKindOrder.put(SedMLTags.CHANGE_ATTRIBUTE_KIND, 1);
            ChangeComparator.changeKindOrder.put(SedMLTags.CHANGE_XML_KIND, 2);
            ChangeComparator.changeKindOrder.put(SedMLTags.ADD_XML_KIND, 3);
            ChangeComparator.changeKindOrder.put(SedMLTags.REMOVE_XML_KIND, 4);
            ChangeComparator.changeKindOrder.put(SedMLTags.COMPUTE_CHANGE_KIND, 5);
            ChangeComparator.changeKindOrder.put(SedMLTags.SET_VALUE_KIND, 6);
        }
        @Override
        public int compare(Change o1, Change o2) {
            if (o1 == o2) return 0;
            int firstCompare = ChangeComparator.changeKindOrder.get(o1.getChangeKind()).compareTo(ChangeComparator.changeKindOrder.get(o2.getChangeKind()));
            if (firstCompare != 0) return firstCompare;
            return super.compare(o1, o2);
        }
    }
}
