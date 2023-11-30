package org.vcell.util.matrix;

import org.vcell.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ArrayMatrix<T> implements Matrix<T> {
    private static final int DEFAULT_DIMENSION_SIZE = 10;

    private final T padValue; // Value to pad the matrix with when needed.
    private int length; // The size of the rows
    private int width; // The size of the columns
    private T[][] data; // in java, this is traditionally row by column

    /**
     * Creates a matrix using arrays of a default initial size. When new data is added, `null`
     * will be used to pad any gaps in the data
     */
    public ArrayMatrix(){
        this(null);
    }

    /**
     * Creates a matrix using arrays of a default initial size. When new data is added, the provided object
     * will be used to pad any gaps in the data
     * @param defaultPad the value to pad gaps in the matrix
     */
    public ArrayMatrix(T defaultPad){
        this(defaultPad, ArrayMatrix.DEFAULT_DIMENSION_SIZE, ArrayMatrix.DEFAULT_DIMENSION_SIZE);
    }

    /**
     * Creates a matrix using arrays of a defined sizes
     * @param initialLength the initial number of columns to prepare
     * @param initialWidth the initial number of rows to prepare
     */
    @SuppressWarnings("unchecked")
    public ArrayMatrix(T defaultPad, int initialLength, int initialWidth){
        if (initialLength < 0) throw new IllegalArgumentException("InitialLength can not be negative");
        if (initialWidth < 0) throw new IllegalArgumentException("InitialWidth can not be negative");
        this.padValue = defaultPad;
        this.data = (T[][]) new Object[initialWidth][initialLength];
        this.length = 0;
        this.width = 0;
    }

    /**
     * Get the length of the matrix
     * @return the length as an int
     */
    public int getLength(){
        return this.length;
    }

    /**
     * Get the width of the matrix
     * @return the width as an int
     */
    public int getWidth(){
        return this.width;
    }

    @Override
    public int size() {
        return this.width * this.length;
    }

    /**
     * Checks if the matrix is empty
     */
    public boolean isEmpty(){
        return this.length == 0 && this.width == 0;
    }

    @Override
    public boolean add(T t) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    /**
     * Determines the coordinates of a given equivalent element, or null if not found
     * @param element the element to look for
     * @return the coordinates in a vcell Pair object
     */
    public Pair<Integer, Integer> coordinatesOf(Object element){
        return this.coordinatesOf(element, true, true);
    }

    /**
     * Determines the coordinates of a given equivalent element, or null if not found
     * @param element the element to look for
     * @param traverseRows whether to go row by row (alternatively, column by column)
     * @param forwardSearch whether to start at bottom right of matrix (alternative, top left, the normal way)
     * @return the coordinates in a vcell Pair object
     */
    public Pair<Integer, Integer> coordinatesOf(Object element, boolean traverseRows, boolean forwardSearch){
        UpdateFunction updater = forwardSearch ? (i) -> i + 1 : (i) -> i - 1;
        int startIndexLength = forwardSearch ? 0 : this.length - 1;
        int startIndexWidth = forwardSearch ? 0 : this.width - 1;
        int startDim1Index = traverseRows ? startIndexWidth : startIndexLength;
        int startDim2Index = traverseRows ? startIndexLength : startIndexWidth;
        int dim1size = traverseRows ? this.width : this.length;
        int dim2size = traverseRows ? this.length : this.width;
        ConditionFunction condition1 = (i) -> forwardSearch ? i < dim1size : -1 < i ;
        ConditionFunction condition2 = (i) -> forwardSearch ? i < dim2size : -1 < i;

        for (int i = startDim1Index; condition1.check(i); i = updater.update(i)){
            for (int j = startDim2Index; condition2.check(j); j = updater.update(j)){
                if (this.argumentsAreEqual((forwardSearch ? this.data[i][j] : this.data[j][i]), element))
                    return new Pair<>(i,j);
            }
        }
        return null;
    }

    /**
     * Retrieves the value at the given coordinate pair
     * @param coordinate the pair (in (row, column) order) to use to look up the value with
     * @throws IndexOutOfBoundsException if the row / column index is invalid
     * @return the value at the provided coordinate
     */
    public T getValue(Pair<Integer, Integer> coordinate){
        return this.getValue(coordinate, true);
    }

    /**
     * Retrieves the value at the given coordinate pair
     * @param coordinate the pair to use to look up the value with
     * @param rowMise if the pair is in (row,column) order; else, (column,row) order
     * @throws IndexOutOfBoundsException if the row / column index is invalid
     * @return the value at the provided coordinate
     */
    public T getValue(Pair<Integer, Integer> coordinate, boolean rowMise){
        if (rowMise) return this.getValue(coordinate.one, coordinate.two);
        return this.getValue(coordinate.two, coordinate.one);
    }

    /**
     * Retrieves the value at the location described by the given row and column indices
     * @param row the row index to look up
     * @param column the column index to look up
     * @throws IndexOutOfBoundsException if the row / column index is invalid
     * @return the value at the provided row and column indices.
     */
    public T getValue(int row, int column){
        if (row < 0 || row >= this.width) throw new IndexOutOfBoundsException("Invalid row index");
        if (column < 0 || column >= this.length) throw new IndexOutOfBoundsException("Invalid column index");
        return this.data[row][column];
    }

    /**
     * Retrieves the value at the location described by the given row-mise coordinate pair
     * @param newValue the value to apply
     * @param coordinate the pair (in (row, column) order) to use to look up the value with
     */
    public void setValue(T newValue, Pair<Integer, Integer> coordinate){
        this.setValue(newValue, coordinate, true);
    }

    /**
     * Applies a new value to the location described by the given coordinate pair
     * @param newValue the value to apply
     * @param coordinate the pair to use to look up the value with
     * @param rowMise if the pair is in (row,column) order; else, (column,row) order
     */
    public void setValue(T newValue, Pair<Integer, Integer> coordinate, boolean rowMise){
        if (rowMise) this.setValue(newValue, coordinate.one, coordinate.two);
        else this.setValue(newValue, coordinate.two, coordinate.one);
    }

    /**
     * Applies a new value to the location described by the given row and column indices
     * @param newValue the value to apply
     * @param row the row index to look up
     * @param column the column index to look up
     */
    public void setValue(T newValue, int row, int column){
        if (row < 0 || row >= this.width) throw new IndexOutOfBoundsException("Invalid row index");
        if (column < 0 || column >= this.length) throw new IndexOutOfBoundsException("Invalid column index");
        this.data[row][column] = newValue;
    }

    /**
     * Determines if a given element equals an entry in the matrix
     * @param element the element to look for
     * @return whether the element exists in the matrix or not
     */
    public boolean contains(Object element){return this.coordinatesOf(element) != null;}

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    /**
     * Remove all values in this ArrayMatrix and make it empty
     */
    public void clear(){
        for (int i = 0; i < this.width; i++){
            Arrays.fill(this.data[i], 0, this.length, null);
        }
        this.width = 0;
        this.length = 0;
    }

    /**
     * Checks if the passed-in object is equivalent to this ArrayMatrix
     * @param o the object to test against
     * @return true if the passed in object is an equivalent matrix
     */
    public boolean equals(Object o){
        if (!(o instanceof ArrayMatrix<?> otherMatrix)) return false;
        if (otherMatrix.length != this.length || otherMatrix.width != this.width) return false;
        for (int i = 0; i < this.width; i++){
            if (!Arrays.equals(otherMatrix.data[i], this.data[i])) return false;
        }
        return true;
    }

    /**
     * Trims the capacity of this List to be equal to its size; this saves memory
     */
    @SuppressWarnings("unchecked")
    public void trimToSize(){
        int actualWidth = this.data.length;
        int actualLength = (actualWidth == 0 || this.data[0] == null) ? 0 : this.data[0].length;
        if (this.width == actualWidth && this.length == actualLength) return;
        if (this.data.length == 0) return;

        T[][] newData = (T[][]) new Object[actualWidth][actualLength];
        for (int i = 0; i < actualWidth; i++){
            if (this.data[i] == null) continue;
            System.arraycopy(this.data[i], 0, newData[i], 0, actualWidth);
        }
        this.data = newData;
    }

    /**
     * Ensures that this matrix will be resized such that at least (minWidth * minLength) elements can fit inside.
     * The resize for the target dimension (if insufficient) will be at least double the current size
     * @param minLength the minimum number of columns to prepare
     * @param minWidth the minimum number of rows to prepare
     */
    @SuppressWarnings("unchecked")
    public void ensureCapacity(int minLength, int minWidth) {
        int actualWidth = this.data.length;
        int actualLength = (actualWidth == 0 || this.data[0] == null) ? 0 : this.data[0].length;

        if (minWidth <= actualWidth && minLength <= actualLength) return;
        int newWidth = Math.max(2 * actualWidth, minWidth);
        int newLength = Math.max(2 * actualLength, minLength);

        T[][] newData = (T[][]) new Object[newWidth][newLength];
        for (int i = 0; i < actualWidth; i++) {
            if (this.data[i] == null) continue;
            System.arraycopy(this.data[i], 0, newData[i], 0, actualWidth);
        }
        this.data = newData;
    }

    /**
     * Returns an object matrix (2D array) with a copy of the contents of this ArrayMatrix
     * @return an `Object`-typed 2D array representation of this ArrayMatrix
     */
    @SuppressWarnings("unchecked")
    public Object[][] to2DArray(){
        T[][] array = (T[][])new Object[this.width][this.length];
        for (int i = 0; i < this.width; i++) System.arraycopy(this.data[i], 0, array[i], 0, this.length);
        return array;
    }

    /**
     * Fills a passed in typed 2DArray with the contents of this Array Matrix, sizing as needed.
     * @return a typed 2D array representation of this ArrayMatrix
     */
    @SuppressWarnings("unchecked")
    public T[][] to2DArray(T[][] array2D){
        // NB: array2D.length => width, array2D[i].length => length; If this is not true, edits are needed!!
        // Check if we need to release objects from passed in array (for GC) and create new 2D array.
        if ((array2D == null || array2D.length < this.width) || (array2D.length > 0 && (array2D[0] == null || array2D[0].length < this.length))){
            if (array2D != null){
                for (int i = 0; i < array2D.length; i++){
                    if (array2D[i] != null) Arrays.fill(array2D[i], 0, array2D[i].length, null);
                    array2D[i] = null;
                }
            }
            array2D = (T[][])Array.newInstance(this.data.getClass().getComponentType().getComponentType(), this.width, this.length);
        } else { // We need to check if the passed in array is too big, and to adjust down if so (kinda similar to above)
            // Is the length too long?
            if ((array2D.length > 0 && array2D[0].length > this.length)){
                for (int i = 0; i < this.width; i++){  // Fill unneeded space with nulls
                    Arrays.fill(array2D[i], this.length, array2D[i].length, null);
                }
            }

            // Is the width too long?
            if (array2D.length > this.width ) {
                for (int i = this.width; i < array2D.length; i++){ // Save as before, but after null array entirely.
                    Arrays.fill(array2D[i], 0, array2D[i].length, null);
                    array2D[i] = null;
                }
            }
        }
        // We're now prepared. If we have null lines, create new arrays to fill them; regardless, copy the data over.
        for (int i = 0; i < this.width; i++){
            if (array2D[i] == null) {
                array2D[i] = (T[])Array.newInstance(array2D.getClass().getComponentType().getComponentType(), this.length);
            }
            System.arraycopy(this.data[i], 0, array2D[i], 0, this.length);
        }
        return array2D;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Object[] toArray() {
        throw new UnsupportedOperationException(
                "This call, while in the Collection's interface, is holistically inappropriate for a Matrix collection");
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException(
                "This call, while in the Collection's interface, is holistically inappropriate for a Matrix collection");
    }

    /**
     * Creates a shallow copy of this object
     * @return the clone of this ArrayMatrix
     */
    @SuppressWarnings("unchecked")
    public Object clone(){
        ArrayMatrix<T> clone;
        try {
            clone = (ArrayMatrix<T>) super.clone();
            clone.data = this.data.clone();
            return clone;
        } catch(CloneNotSupportedException e){
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////
    //     Utility Methods / Classes     //
    ///////////////////////////////////////

    // Used as a null-safe .equals replacement
    private boolean argumentsAreEqual(Object subject, Object target){
        if (subject == null)
            return target == null;
        return subject.equals(target);
    }

    /**
     * What should be performed to 'i' at the end of a loop iteration
     */
    protected interface UpdateFunction {
        Integer update(Integer integer);
    }

    /**
     * Condition for continuing the for-loop
     */
    protected interface ConditionFunction {
        boolean check(Integer value);
    }

    protected static class ArrayMatrixIterator<E> implements MatrixIterator<E> {
        private ArrayMatrix<E> matrix;
        private Pair<Integer, Integer> rowMiseCursorLocation;

        public ArrayMatrixIterator(ArrayMatrix<E> matrix){
            this(matrix, 0, 0);
        }

        public ArrayMatrixIterator(ArrayMatrix<E> matrix, int row, int column){
            this(matrix, new Pair<>(row, column), true, true);
        }

        public ArrayMatrixIterator(ArrayMatrix<E> matrix, Pair<Integer, Integer> cursorLocation,
                                   boolean cursorIsRowMise, boolean traversalIsRowMise){
            if (cursorIsRowMise) this.rowMiseCursorLocation = new Pair<>(cursorLocation.one, cursorLocation.two);
            else this.rowMiseCursorLocation = new Pair<>(cursorLocation.two, cursorLocation.one);
            if (traversalIsRowMise){

            } else {

            }
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public E previous() {
            return null;
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return 0;
        }

        @Override
        public void remove() {

        }

        @Override
        public void set(E e) {

        }

        @Override
        public boolean hasNextColumn() {
            return this.matrix.length > this.rowMiseCursorLocation.two + 1;
        }

        @Override
        public E getNextColumnValue() {
            this.rowMiseCursorLocation =
                    new Pair<>(this.rowMiseCursorLocation.one, this.rowMiseCursorLocation.two + 1);
            return this.matrix.getValue(this.rowMiseCursorLocation);
        }

        @Override
        public boolean hasNextRow() {
            return this.matrix.width > this.rowMiseCursorLocation.one + 1;
        }

        @Override
        public E getNextRowValue() {
            this.rowMiseCursorLocation =
                    new Pair<>(this.rowMiseCursorLocation.one + 1, this.rowMiseCursorLocation.two);
            return this.matrix.getValue(this.rowMiseCursorLocation);
        }

        @Override
        public boolean hasPreviousColumn() {
            return false;
        }

        @Override
        public E getPreviousColumnValue() {
            return null;
        }

        @Override
        public boolean hasPreviousRow() {
            return false;
        }

        @Override
        public E getPreviousRowValue() {
            return null;
        }
    }
}
