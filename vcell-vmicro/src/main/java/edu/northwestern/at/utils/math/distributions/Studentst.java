package edu.northwestern.at.utils.math.distributions;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;
import edu.northwestern.at.utils.math.rootfinders.*;

/**	Student's t distribution functions.
 */

public class Studentst
{
	/** Compute probability for Student t distribution.
	 *
	 *	@param	t	Percentage point of Student t distribution
	 *
	 *	@param	df	Degrees of freedom
	 *
	 *	@return		The corresponding probability for the
	 *				Student t distribution.
	 *
	 *	@throws		IllegalArgumentException
	 *                  if df <= 0
	 *
	 *	<p>
	 *	The probability is computed using the following
	 *	relationship between the incomplete beta distribution
	 *	and Student's t:
	 *	</p>
	 *
	 *	<p>
	 *	tprob(t) = incompleteBeta( df/(df*t*t), df/2, 0.5 )
	 *	</p>
	 *
	 *	<p>
	 *	The result is accurate to about 14 decimal digits.
	 *	</p>
	 */

	public static double t( double t , double df )
		throws IllegalArgumentException
	{
		double result	= 0.0D;

		if ( df > 0.0D )
		{
			result	=
				Beta.incompleteBeta
				(
					df / ( df + t * t ) ,
					df / 2.0D ,
					0.5D ,
					Constants.MAXPREC
				);
		}
		else
		{
			throw new IllegalArgumentException( "df <= 0" );
		}

		return result;
	}

	/** Compute percentage point for Student t distribution.
	 *
	 *	@param	p	Probability level for percentage point
	 *	@param	df	Degrees of freedom
	 *
	 *	@return		The corresponding percentage point of
	 *				the Student t distribution.
	 *
	 *	@throws		IllegalArgumentException
	 *					p < 0 or p > 1 or df <= 0
     *
	 *	@throws		ArithmeticException
	 *					if incomplete beta evaluation fails
	 *
	 *	<p>
	 *	The percentage point is computed using the inverse
	 *	incomplete beta distribution.  This allows for fractional
	 *	degrees of freedom.
	 *	</p>
	 */

	public static double tInverse( double p , double df )
		throws ArithmeticException, IllegalArgumentException
	{
		double result	= 0.0D;

		if ( df > 0.0D )
		{
			if ( ( p >= 0.0D ) && ( p <= 1.0D ) )
			{
				result	=
					Beta.incompleteBetaInverse( 1.0D - p , 0.5D , df / 2.0D );

            	if ( ( result >= 0.0D ) && ( result < 1.0D ) )
            	{
            		result  = Math.sqrt( result * df / ( 1.0D - result ) );
            	}
            	else
            	{
					throw new ArithmeticException(
						"inverse incomplete beta evaluation failed" );
            	}
			}
			else
			{
				throw new IllegalArgumentException( "p < 0 or p > 1" );
			}
		}
		else
		{
			throw new IllegalArgumentException( "df <= 0" );
		}

		return result;
	}

	/**	Make class non-instantiable but inheritable.
	 */

	protected Studentst()
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


