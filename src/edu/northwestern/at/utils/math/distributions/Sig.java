package edu.northwestern.at.utils.math.distributions;

/*	Please see the license information at the end of this file. */

/**	Point probabilities and percentage points for statistical distributions.
 *
 *	<p>
 *	This class provides "one stop shopping" for point probabilities
 *	percentage points for commonly used statistical distributions.
 *	These include the Beta, F, t, chi-square, and normal distributions.
 *	Both the forward (probability) and inverse (percentage point) functions
 *	are provided.  This class is actually just provides a shim to the
 *	methods in the individual classes for each distribution.
 *	</p>
 *
 *	<p>
 *	Point probability routines
 *	</p>
 *
 *	<ul>
 *	<li>chisquare		-- significance of chi-square</li>
 *	<li>f				-- significance of F</li>
 *	<li>normal			-- significance of normal value</li>
 *	<li>t				-- significance of Student's t</li>
 *	</ul>
 *
 *	<p>
 *	Inverse distributions (percentage points)
 *	</p>
 *
 *	<ul>
 *	<li>chisquareInverse			-- inverse chi-square</li>
 *	<li>fInverse					-- inverse F</li>
 *	<li>normalInverse				-- inverse normal</li>
 *	<li>tInverse					-- inverse t</li>
 *	</ul>
 */

public class Sig
{
	/*	--- Chisquare --- */

	public static double chisquare( double chiSquare , double df )
	{
		return ChiSquare.chisquare( chiSquare , df );
	}

	public static double chisquare( double chiSquare , int df )
	{
		return ChiSquare.chisquare( chiSquare , (double)df );
	}

	public static double chisquareInverse( double p , double df )
	{
		return ChiSquare.chisquareInverse( p , df );
	}

	public static double chisquareInverse( double p , int df )
	{
		return ChiSquare.chisquareInverse( p , (double)df );
	}

	/*	--- Fisher's F --- */

	public static double f( double f , double dfn , double dfd )
	{
		return FishersF.f( f , dfn , dfd  );
	}

	public static double f( double f , int dfn , int dfd )
	{
		return FishersF.f( f , (double)dfn , (double)dfd  );
	}

	public static double fInverse( double p , double dfn , double dfd )
	{
		return FishersF.fInverse( p , dfn , dfd );
	}

	public static double fInverse( double p , int dfn , int dfd )
	{
		return FishersF.fInverse( p , (double)dfn , (double)dfd );
	}

	/*	--- Normal distribution --- */

	public static double normal( double z )
	{
		return Normal.normal( z );
	}

	public static double normalInverse( double p )
	{
		return Normal.normalInverse( p );
	}

	/*	--- Student's t --- */

	public static double t( double t , double df )
	{
		return Studentst.t( t , df );
	}

	public static double t( double t , int df )
	{
		return Studentst.t( t , (double)df );
	}

	public static double tInverse( double p , double df )
	{
		return Studentst.tInverse( p , df );
	}

	public static double tInverse( double p , int df )
	{
		return Studentst.tInverse( p , (double)df );
	}

    public static double sidak( double p , int n )
    {
		double result	= p;

        if ( n > 1 ) result	= 1.0D - Math.pow( 1.0D - p , ( 1.0D / n ) );

		return result;
    }

    public static double bonferroni( double p , int n )
    {
		double result	= p;

        if ( n > 1 ) result	= result / (double)n;

		return result;
    }

	/**	Make class non-instantiable but inheritable.
	 */

	protected Sig()
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


