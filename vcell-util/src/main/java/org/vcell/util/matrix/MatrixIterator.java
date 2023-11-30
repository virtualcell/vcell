package org.vcell.util.matrix;

import java.util.ListIterator;

public interface MatrixIterator<T> extends ListIterator<T> {
    default void add(T e){
        throw new UnsupportedOperationException("This call is holistically inappropriate in the context of a matrix");
    }

    boolean hasNextColumn();

    T getNextColumnValue();

    boolean hasNextRow();

    T getNextRowValue();

    boolean hasPreviousColumn();

    T getPreviousColumnValue();

    boolean hasPreviousRow();

    T getPreviousRowValue();


}
