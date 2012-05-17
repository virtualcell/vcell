package edu.northwestern.at.utils.math;

/*	Please see the license information at the end of this file. */

/**	Machine-dependent arithmetic constants.
 */

public class Constants
{
	/** Machine epsilon.  Smallest double floating point number
	 *	such that (1 + MACHEPS) > 1 .
	 */

	public static final double MACHEPS		= determineMachineEpsilon();

	/* Machine precision in decimal digits . */

	public static final int	MAXPREC			= determineMaximumPrecision();

	/**	Maximum logarithm value. */

	public static final double MAXLOG		= 7.09782712893383996732E2;

	/** Minimum logarithm value. */

	public static final double MINLOG		= -7.451332191019412076235E2;

	/** Square root of 2. */

	public static final double SQRT2		= Math.sqrt( 2.0D );

	/** ( Square root of 2 ) / 2 . */

	public static final double SQRT2DIV2	= SQRT2 / 2.0D;

	/** Square root of PI. */

	public static final double SQRTPI		= Math.sqrt( Math.PI );

	/** Natural log of PI. */

	public static final double LNPI			= Math.log( Math.PI );

	/* LN(10) .            */

	public static final double	LN10		= Math.log( 10.0D );

	/* 1 / LN(10)             */

//	public static final double	LN10INV		= 1.0D / LN10;
	public static final double	LN10INV		= 0.43429448190325182765D;

	/* LN(2)                  */

	public static final double	LN2			= Math.log( 2.0D );

	/* 1 / LN(2)              */

	public static final double	LN2INV		= 1.0D / LN2;

	/* LN( Sqrt( 2 * PI ) ) */

	public static final double	LNSQRT2PI	=
		Math.log( Math.sqrt( 2.0D * Math.PI ) );

	/** Determine machine epsilon.
	 *
	 *	@return		The machine epsilon as a double.
	 *				The machine epsilon MACHEPS is the
	 *				smallest number such that (1 + MACHEPS) == 1 .
	 */

	public static double determineMachineEpsilon()
    {
        double d1 = 1.3333333333333333D;
        double d3;
        double d4;

        for( d4 = 0.0D; d4 == 0.0D; d4 = Math.abs( d3 - 1.0D ) )
        {
            double d2 = d1 - 1.0D;
            d3 = d2 + d2 + d2;
        }

        return d4;
    }

	/** Determine maximum double floating point precision.
	 *
	 *	@return		Maximum number of digits of precision
	 *				for double precision floating point.
	 */

	public static int determineMaximumPrecision()
    {
								//	Get machine epsilon.

    	double	macheps	= determineMachineEpsilon();

								//	Maximum digits of precision
								//	is given by the negative of
								//	of the base 10 exponent of
								//	of the machine precision.
		double	digits	=
			ArithUtils.trunc(
				Math.log( macheps ) / Math.log( 10.0D ) );

    	return -new Long( Math.round( digits ) ).intValue();
    }

	/**	This class is non-instantiable but inheritable.
	 */

	protected Constants()
	{
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


