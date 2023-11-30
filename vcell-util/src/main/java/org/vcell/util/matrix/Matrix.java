package org.vcell.util.matrix;

import org.vcell.util.Pair;

import java.util.Collection;

public interface Matrix<T> extends Collection<T> {
    /**
     * Get the length of the matrix
     * @return the length as an int
     */
    int getLength();

    /**
     * Get the width of the matrix
     * @return the width as an int
     */
    int getWidth();

    /**
     * Checks if the matrix is empty
     */
    boolean isEmpty();

    /**
     * Determines the coordinates of a given equivalent element, or null if not found
     * @param element the element to look for
     * @return the coordinates in a vcell Pair object
     */
    Pair<Integer, Integer> coordinatesOf(Object element);

    /**
     * Determines the coordinates of a given equivalent element, or null if not found
     * @param element the element to look for
     * @param traverseRows whether to go row by row (alternatively, column by column)
     * @param forwardSearch whether to start at bottom right of matrix (alternative, top left, the normal way)
     * @return the coordinates in a vcell Pair object
     */
    Pair<Integer, Integer> coordinatesOf(Object element, boolean traverseRows, boolean forwardSearch);

    /**
     * Retrieves the value at the given coordinate pair
     * @param coordinate the pair (in (row, column) order) to use to look up the value with
     * @throws IndexOutOfBoundsException if the row / column index is invalid
     * @return the value at the provided coordinate
     */
    T getValue(Pair<Integer, Integer> coordinate);

    /**
     * Retrieves the value at the given coordinate pair
     * @param coordinate the pair to use to look up the value with
     * @param rowMise if the pair is in (row,column) order; else, (column,row) order
     * @throws IndexOutOfBoundsException if the row / column index is invalid
     * @return the value at the provided coordinate
     */
    T getValue(Pair<Integer, Integer> coordinate, boolean rowMise);

    /**
     * Retrieves the value at the location described by the given row and column indices
     * @param row the row index to look up
     * @param column the column index to look up
     * @throws IndexOutOfBoundsException if the row / column index is invalid
     * @return the value at the provided row and column indices.
     */
    T getValue(int row, int column);

    /**
     * Retrieves the value at the location described by the given row-mise coordinate pair
     * @param newValue the value to apply
     * @param coordinate the pair (in (row, column) order) to use to look up the value with
     */
    void setValue(T newValue, Pair<Integer, Integer> coordinate);

    /**
     * Applies a new value to the location described by the given coordinate pair
     * @param newValue the value to apply
     * @param coordinate the pair to use to look up the value with
     * @param rowMise if the pair is in (row,column) order; else, (column,row) order
     */
    void setValue(T newValue, Pair<Integer, Integer> coordinate, boolean rowMise);

    /**
     * Applies a new value to the location described by the given row and column indices
     * @param newValue the value to apply
     * @param row the row index to look up
     * @param column the column index to look up
     */
    void setValue(T newValue, int row, int column);

    /**
     * Determines if a given element equals an entry in the matrix
     * @param element the element to look for
     * @return whether the element exists in the matrix or not
     */
    boolean contains(Object element);

    /**
     * Remove all values in this ArrayMatrix and make it empty
     */
    void clear();

    /**
     * Checks if the passed-in object is equivalent to this ArrayMatrix
     * @param o the object to test against
     * @return true if the passed in object is an equivalent matrix
     */
    boolean equals(Object o);

    /**
     * Trims the capacity of this List to be equal to its size; this saves memory
     */
    void trimToSize();

    /**
     * Ensures that this matrix will be resized such that at least (minWidth * minLength) elements can fit inside.
     * The resize for the target dimension (if insufficient) will be at least double the current size
     * @param minLength the minimum number of columns to prepare
     * @param minWidth the minimum number of rows to prepare
     */
    void ensureCapacity(int minLength, int minWidth);

    /**
     * Returns an object matrix (2D array) with a copy of the contents of this ArrayMatrix
     * @return an `Object`-typed 2D array representation of this ArrayMatrix
     */
     Object[][] to2DArray();

    /**
     * Fills a passed in typed 2DArray with the contents of this Array Matrix, sizing as needed.
     * @return a typed 2D array representation of this ArrayMatrix
     */
    T[][] to2DArray(T[][] array2D);

    /**
     * Creates a shallow copy of this object
     * @return the clone of this Matrix
     */
    Object clone();
}
