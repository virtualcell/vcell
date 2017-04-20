package edu.northwestern.at.utils.math.rootfinders;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;

/** Find interval bracketing a root.
 */

public class BracketRoot
{
	/** Find interval bracketing a root.
	 *
	 *	@param	bracket				Array containing initial estimate for
	 *								of bracketing interval.
	 *
	 *								bracket[0] is left hand estimate.
	 *								bracket[1] is right hand estimate.
	 *
	 *	@param	function			Function whose root is to be bracketed.
	 *
	 *	@param	maxIter				Maximum number of iterations to try
	 *								expanding bracket.
	 *
	 *	@param	expansionFactor		Factor by which to expand bracket interval
	 *								on each iteration.
	 *
	 *	@return						True if bracket successfully found
	 *								The new bracket will be placed in
	 *								"bracket".
	 *
	 *								False if bracket could not be found within
	 *								maxIter attempts.
	 */

	public static boolean bracketRoot
	(
		double bracket[],
		MonadicFunction function,
		int maxIter,
		double expansionFactor
	)
	{
								// Check for valid initial bracket.

		if	(	( bracket == null ) || ( bracket.length < 2 ) ||
				( bracket[ 0 ] == bracket[ 1 ] ) )
		{
			throw new IllegalArgumentException( "initial bracket bad" );
		}
								// Compute function at initial
								// left and right bracket points.

		double fLeft	= function.f( bracket[ 0 ] );
		double fRight	= function.f( bracket[ 1 ] );

		for ( int iter = 1; iter <= maxIter; iter++ )
		{
								// If the left and right function
								// values are of different sign,
								// a zero is bracketed between them.

			if ( ( fLeft * fRight ) < 0.0 ) return true;

								// Determine which end of the bracket
								// to expand.

			if ( Math.abs( fLeft ) < Math.abs( fRight ) )
			{
				bracket[ 0 ]	=
					expansionFactor * ( bracket[ 0 ] - bracket[ 1 ] );

				fLeft			= function.f( bracket[ 0 ] );
			}
			else
			{
				bracket[ 1 ]	=
					expansionFactor * ( bracket[ 1 ] - bracket[ 0 ] );

				fRight			= function.f( bracket[ 1 ] );
			}
		}

		return false;
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


