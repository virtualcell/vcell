package org.jlibsedml.components.listOfConstructs;

import org.jlibsedml.SEDMLVisitor;
import org.jlibsedml.components.SId;
import org.jlibsedml.components.SedBase;

import java.util.*;

public abstract class ListOf<T extends SedBase> extends SedBase {
    // Since IDs should be unique...ironically we need a Set<T>;
    // also, since we sometimes want ID -> Object mappings, we'll use Map<String, T>
    // BUT sometimes there won't be IDs, so...we need both.
    protected final Map<SId, T> contentIdMapping;
    protected final Set<T> contents;


    /**
     * Constructor to create empty list
     */
    public ListOf() {
        this(null, null);
    }

    /**
     * Constructor to create empty list (with id and name as options)
     */
    public ListOf(SId id, String name) {
        this(id, name, Collections.emptyList());
    }

    /**
     * Constructor allowing elements to be passed in.
     *
     * @param elements the elements to add
     */
    public ListOf(List<T> elements) {
        this(null, null, elements);
    }

    /**
     * Constructor allowing elements to be passed in.
     *
     * @param elements the elements to add
     */
    public ListOf(SId id, String name, List<T> elements) {
        this(id, name, elements, null);
    }

    protected ListOf(SId id, String name, List<T> elements, Comparator<? super T> comparatorToUseForContents) {
        super(id, name);
        // LinkedHashSet -> preserves insertion order, treeSet-> allows for sort-as-you-go (and we want different ordering).
        this.contents = comparatorToUseForContents == null ? new LinkedHashSet<>() : new TreeSet<>(comparatorToUseForContents);
        this.contentIdMapping = new HashMap<>();
        for (T element : elements) {
            this.addContent(element); // We want to invoke the add method, for overrides.
            SId sid = element.getId();
            if (sid == null) continue;
            this.contentIdMapping.put(sid, element);
        }
    }

    public boolean isEmpty() {
        return this.contents.isEmpty();
    }

    public int size() {
        return this.contents.size();
    }

    public boolean containsContent(SId content) {
        return this.contentIdMapping.containsKey(content);
    }

    /**
     * Gets the contents as an unmodifiable list
     *
     * @return an unmodifiable {@link List} of generic type <code>T</code>
     */
    public List<T> getContents() {
        return this.contents.stream().toList();
    }

    public T getContentById(SId contentId) {
        if (contentId == null) throw new IllegalArgumentException("`null` is not a valid id.");
        return this.contentIdMapping.get(contentId);
    }

    public void addContent(T content) {
        if (null == content) return;
        SId contentId = content.getId();
        if (null != contentId) {
            if (this.contentIdMapping.containsKey(contentId)) return; // Do not override what we have
            this.contentIdMapping.put(contentId, content);
        }
        this.contents.add(content);
    }

    public void removeContent(T content) {
        if (null == content) return;
        this.contents.remove(content);
    }

    @Override
    public SedBase searchFor(SId idOfElement) {
        SedBase elementFound = super.searchFor(idOfElement);
        if (elementFound != null) return elementFound;
        // shortcut check to save time
        if (this.contentIdMapping.containsKey(idOfElement))
            return this.contentIdMapping.get(idOfElement);
        // Now we have to check children
        for (SId key : this.contentIdMapping.keySet()) {
            elementFound = this.contentIdMapping.get(key).searchFor(idOfElement);
            if (elementFound != null) return elementFound;
        }
        return null;
    }

    /**
     * Returns the parameters that are used in <code>this.equals()</code> to evaluate equality.
     * Needs to be returned as `member_name=value.toString(), ` segments, and it should be appended to a `super` call to this function.
     * <br\>
     * e.g.: `super.parametersToString() + ", " + String.format(...)`
     *
     * @return the parameters and their values, listed in string form
     */
    @Override
    public String parametersToString() {
        // SEE ORIGINAL PARENT!!
        List<String> params = new ArrayList<>();
        for (T content : this.getContents()) {
            if (content.getId() != null) params.add(content.getIdAsString());
            else params.add(content.toString());
        }
        return super.parametersToString() + ", values=[" + String.join(", ", params) + ']';
    }

    protected static class GeneralListOfComparator<N extends SedBase> implements Comparator<N> {
        public int compare(N o1, N o2) {
            if (o1 == o2) return 0;
            SId id1 = o1.getId(), id2 = o2.getId();
            if (null == id1 && null == id2) return 0;
            if (id2 == null) return -1;
            if (id1 == null) return 1;
            return id1.string().compareTo(id2.string());
        }
    }
}
