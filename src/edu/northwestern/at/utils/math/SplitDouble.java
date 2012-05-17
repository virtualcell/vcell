package edu.northwestern.at.utils.math;

/*	Please see the license information at the end of this file. */

/**	Splits a double into a normalized base two mantissa and exponent..
 *
 *	<p>
 *	A double value is split into a normalized mantissa in
 *	the range 0.5 to 1.0, and a corresponding base 2 exponent.
 *	</p>
 */

public class SplitDouble
{
	/** The normalized mantissa.*/

	public double mantissa;

	/** The base two exponent. */

	public int exponent;

	/**	Create SplitDouble object.
	 *
	 *	@param	d	The double to split.
	 */

	public SplitDouble( double d )
	{
		if ( d == 0.0D )
		{
			mantissa	= 0.0D;
			exponent	= 0;
		}
		else
		{
			long bits	= Double.doubleToLongBits( d );

			mantissa	=
				Double.longBitsToDouble( ( 0X800FFFFFFFFFFFFFL & bits ) |
				0x3FE0000000000000L );

			exponent	= (int)( ( bits >> 52 ) & 0x7ff ) - 1022;
		}
	}
}

/*
Copyright (c) 2008, 2009 by Northwestern University.
All rights reserved.

Developed by:
   Academic and Research Technologies
   Northwestern University
   http://www.it.northwestern.edu/about/departments/at/

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimers.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimers in the documentation and/or other materials provided
      with the distribution.

    * Neither the names of Academic and Research Technologies,
      Northwestern University, nor the names of its contributors may be
      used to endorse or promote products derived from this Software
      without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/



