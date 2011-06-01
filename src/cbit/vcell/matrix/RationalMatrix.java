/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.matrix;

/**
 * Insert the type's description here.
 * Creation date: (5/4/2003 2:19:18 PM)
 * @author: Jim Schaff
 */
public interface RationalMatrix {
/**
 * Insert the method's description here.
 * Creation date: (5/4/2003 2:23:19 PM)
 * @return cbit.vcell.matrix.RationalMatrix
 * @param matrix cbit.vcell.matrix.RationalMatrix
 */
RationalMatrix findNullSpace() throws MatrixException;
/**
 * Insert the method's description here.
 * Creation date: (5/6/2003 6:54:06 AM)
 * @return int
 */
int gaussianElimination() throws MatrixException;
/**
 * Insert the method's description here.
 * Creation date: (5/4/2003 2:20:26 PM)
 * @return cbit.vcell.matrix.RationalNumber
 * @param row int
 * @param col int
 */
RationalNumber get_elem(int row, int col);
/**
 * Insert the method's description here.
 * Creation date: (5/4/2003 2:26:47 PM)
 * @return int
 */
int getNumCols();
/**
 * Insert the method's description here.
 * Creation date: (5/4/2003 2:27:00 PM)
 * @return int
 */
int getNumRows();
/**
 * Insert the method's description here.
 * Creation date: (5/5/2003 11:33:31 AM)
 */
void identity() throws MatrixException;
/**
 * Insert the method's description here.
 * Creation date: (5/4/2003 2:21:57 PM)
 * @param i int
 * @param j int
 * @param value long
 */
void set_elem(int i, int j, long value);
/**
 * Insert the method's description here.
 * Creation date: (5/4/2003 2:21:57 PM)
 * @param i int
 * @param j int
 * @param value long
 */
void set_elem(int i, int j, long num, long den);
/**
 * Insert the method's description here.
 * Creation date: (5/4/2003 2:39:38 PM)
 */
void show();
/**
 * Insert the method's description here.
 * Creation date: (5/6/2003 7:00:38 AM)
 * @return cbit.vcell.matrix.RationalNumber[]
 */
RationalNumber[] solveLinear() throws MatrixException;
/**
 * Insert the method's description here.
 * Creation date: (5/5/2003 11:34:43 AM)
 */
void zero();
}
