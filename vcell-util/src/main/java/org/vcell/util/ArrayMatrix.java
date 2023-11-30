package org.vcell.util;

public class ArrayMatrix<T> {
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
    public void ensureCapacity(int minLength, int minWidth){
        int actualWidth = this.data.length;
        int actualLength = (actualWidth == 0 || this.data[0] == null) ? 0 : this.data[0].length;

        if (minWidth <= actualWidth && minLength <= actualLength) return;
        int newWidth = Math.max(2*actualWidth, minWidth);
        int newLength = Math.max(2*actualLength, minLength);

        T[][] newData = (T[][]) new Object[newWidth][newLength];
        for (int i = 0; i < actualWidth; i++){
            if (this.data[i] == null) continue;
            System.arraycopy(this.data[i], 0, newData[i], 0, actualWidth);
        }
        this.data = newData;
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

    /**
     * Checks if the matrix is empty
     */
    public boolean isEmpty(){
        return this.length == 0 && this.width == 0;
    }

    /**
     * Determines the coordinates of a given equivalent element, or null if not found
     * @param element the element to look for
     * @return the coordinates in a vcell Pair object
     */
    public Pair<Integer, Integer> coordinatesOf(T element){
        /*
        for (int i = 0; i < this.width; i++){
            for (int j = 0; j < this.length; j++){
                if (this.argumentsAreEqual(this.data[i][j], element)) return new Pair<>(i,j);
            }
        }
        return null;
        */
        return this.coordinatesOf(element, true, false);
    }

    /**
     * Determines the coordinates of a given equivalent element, or null if not found
     * @param element the element to look for
     * @param traverseRows whether to go row by row (alternatively, column by column)
     * @param reverseSearch whether to start at bottom right of matrix (alternative, top left, the normal way)
     * @return the coordinates in a vcell Pair object
     */
    public Pair<Integer, Integer> coordinatesOf(T element, boolean traverseRows, boolean reverseSearch){
        UpdateFunction updater = reverseSearch ? (i) -> i - 1 : (i) -> i + 1;
        int startIndexLength = reverseSearch ? this.length - 1 : 0;
        int startIndexWidth = reverseSearch ? this.width - 1 : 0;
        int startDim1Index = traverseRows ? startIndexWidth : startIndexLength;
        int startDim2Index = traverseRows ? startIndexLength : startIndexWidth;
        int dim1size = traverseRows ? this.width : this.length;
        int dim2size = traverseRows ? this.length : this.width;
        ConditionFunction condition1 = (i) -> reverseSearch ? -1 < i : i < dim1size;
        ConditionFunction condition2 = (i) -> reverseSearch ? -1 < i : i < dim2size;

        for (int i = startDim1Index; condition1.check(i); i = updater.update(i)){
            for (int j = startDim2Index; condition2.check(j); j = updater.update(j)){
                if (this.argumentsAreEqual((reverseSearch ? this.data[j][i] : this.data[i][j]), element))
                    return new Pair<>(i,j);
            }
        }
        return null;
    }

    /**
     * Retrieves the value at the given coordinate pair
     * @param coordinate the pair (in (row, column) order to use to look up the value
     * @throws IndexOutOfBoundsException if the row / column index is invalid
     * @return the value at the provided coordinate
     */
    public T valueAt(Pair<Integer, Integer> coordinate){
        return this.valueAt(coordinate, true);
    }

    /**
     * Retrieves the value at the given coordinate pair
     * @param coordinate the pair to use to look up the value
     * @param row_mise if the pair is in (row,column) order; else, (column,row) order
     * @throws IndexOutOfBoundsException if the row / column index is invalid
     * @return the value at the provided coordinate
     */
    public T valueAt(Pair<Integer, Integer> coordinate, boolean row_mise){
        if (row_mise) return this.valueAt(coordinate.one, coordinate.two);
        return this.valueAt(coordinate.two, coordinate.one);
    }

    /**
     * Retrieves the value at the given coordinate pair
     * @param row the row index to look up
     * @param column the column index to look up
     * @throws IndexOutOfBoundsException if the row / column index is invalid
     * @return the value at the provided row and column indices.
     */
    public T valueAt(int row, int column){
        if (row < 0 || row >= this.width) throw new IndexOutOfBoundsException("Invalid row index");
        if (column < 0 || column >= this.length) throw new IndexOutOfBoundsException("Invalid column index");
        return this.data[row][column];
    }

    /**
     * Determines if a given element equals an entry in the matrix
     * @param element the element to look for
     * @return whether the element exists in the matrix or not
     */
    public boolean contains(T element){
        return this.coordinatesOf(element) != null;
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
        } catch(CloneNotSupportedException e){
            throw new RuntimeException(e);
        }
        return clone;
    }

    /**
     * Returns an object matrix (2D array) with a copy of the contents of this ArrayMatrix
     * @return a 2D array representation of this ArrayMatrix
     */
    @SuppressWarnings("unchecked")
    public Object[][] to2DArray(){
        T[][] array = (T[][])new Object[this.width][this.length];
        for (int i = 0; i < this.width; i++) System.arraycopy(data[i], 0, array[i], 0, this.length);
        return array;
    }

    /**
     *
     * @return
     */
    public T[][] to2DArray(T[][] array2D){
        if ((array2D == null || array2D.length < this.width) ||
                (array2D.length > 0 && (array2D[0] == null || array2D[0].length < this.length))){
            // Break down into multiple create new instance calls
        } else {
            // Array copy over
        }
        return array2D;
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
}
