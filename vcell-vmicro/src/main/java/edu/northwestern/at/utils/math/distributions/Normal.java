package edu.northwestern.at.utils.math.distributions;

/*	Please see the license information at the end of this file. */
import edu.northwestern.at.utils.math.Constants;
import edu.northwestern.at.utils.math.Polynomial;

/**	Normal distribution functions. */

public class Normal
{
	/** Compute probability for normal distribution.
	 *
	 *	@param	z	Percentage point of normal distribution.
	 *
	 *	@return		The corresponding probabiity for the
	 *				Student t distribution.
	 *
	 *	<p>
	 *	Uses the relationship between the Normal and Gaussian Error
	 *	distributions.
	 *	</p>
	 */

	public static double normal( double z )
	{
		if ( Double.isNaN( z ) )
		{
			return z;
        }

		double	result;

		if ( z >= 0.0D )
		{
      		result	=
  				( 1.0D +
  					ErrorFunction.errorFunction(
  						z / Constants.SQRT2 ) ) / 2.0D;
        }
        else
        {
			result	=
				ErrorFunction.errorFunctionComplement(
					-z / Constants.SQRT2 ) / 2.0D;
		}

		return result;
    }

	/** Compute percentage point for normal distribution.
	 *
	 *	@param	p	Probability value.
	 *
	 *	@return		The corresponding approximate percentage point for the
	 *				normal distribution.
	 *
	 *	<p>
	 *	See Wichura, M. J. (1988) Algorithm AS 241: The Percentage Points of
	 *	the Normal Distribution. Applied Statistics, 37, 477-484.
	 *	The result is generally accurate to about 10-12 decimal digits.
	 *	We improve the result from Wichura's estimate using two iterations
	 *	of a Taylor series, generally resulting in about 15 decimal digits
	 *	of accuracy.  See Kennedy, W. J. and Gentle, James E.
	 *	_Statistical Computing_, Marcel Dekker, 1980, pp. 94 for
	 *	a discussion of the Taylor series improvement.
	 *	</p>
	 */

	public static double normalInverse( double p )
		throws IllegalArgumentException
	{
		final double a[] =
		{
			3.3871328727963666080e0,
			1.3314166789178437745e+2,
			1.9715909503065514427e+3,
			1.3731693765509461125e+4,
			4.5921953931549871457e+4,
			6.7265770927008700853e+4,
			3.3430575583588128105e+4,
			2.5090809287301226727e+3
		};

		final double b[] =
		{
			1.0000000000000000000e0,
			4.2313330701600911252e+1,
			6.8718700749205790830e+2,
			5.3941960214247511077e+3,
			2.1213794301586595867e+4,
			3.9307895800092710610e+4,
			2.8729085735721942674e+4,
			5.2264952788528545610e+3
		};

		final double c[] =
		{
			1.42343711074968357734e0,
			4.63033784615654529590e0,
			5.76949722146069140550e0,
			3.64784832476320460504e0,
			1.27045825245236838258e0,
			2.41780725177450611770e-1,
			2.27238449892691845833e-2,
			7.74545014278341407640e-4
		};

		final double d[] =
		{
			1.00000000000000000000e0,
			2.05319162663775882187e0,
			1.67638483018380384940e0,
			6.89767334985100004550e-1,
			1.48103976427480074590e-1,
			1.51986665636164571966e-2,
			5.47593808499534494600e-4,
			1.05075007164441684324e-9
		};

		final double e[] =
		{
			6.65790464350110377720e0,
			5.46378491116411436990e0,
			1.78482653991729133580e0,
			2.96560571828504891230e-1,
			2.65321895265761230930e-2,
			1.24266094738807843860e-3,
			2.71155556874348757815e-5,
			2.01033439929228813265e-7
		};

		final double f[] =
		{
			1.00000000000000000000e0,
			5.99832206555887937690e-1,
			1.36929880922735805310e-1,
			1.48753612908506148525e-2,
			7.86869131145613259100e-4,
			1.84631831751005468180e-5,
			1.42151175831644588870e-7,
			2.04426310338993978564e-15
		};

		final double SPLIT1 = 0.425;
		final double CONST1 = 0.180625; // = SPLIT1 * SPLIT1
		final double SPLIT2 = 5.0;
		final double CONST2 = 1.6;

		double z;

		if ( p > 1.0 )
		{
			throw new IllegalArgumentException( "p>1" );
		}

		else if ( p < 0.0 )
		{
			throw new IllegalArgumentException( "p<0" );
		}

		else if ( p == 1.0 )
		{
//			z	= Double.MAX_VALUE;
			z	= Double.POSITIVE_INFINITY;
		}
		else if ( p == 0.0 )
		{
//			z	= Double.MIN_VALUE;
			z	= Double.NEGATIVE_INFINITY;
		}
        else
        {
			double q = p - 0.5;

			if ( Math.abs( q ) <= SPLIT1 )
			{
				double r	= CONST1 - q * q;
				z			=
					q * Polynomial.hornersMethod( a , r ) /
						Polynomial.hornersMethod( b , r );
			}
			else
			{
				double r	= ( q < 0.0 ) ? p : 1.0 - p;

				r			= Math.sqrt( -Math.log( r ) );

				if ( r <= SPLIT2 )
				{
					r	-= CONST2;
					z	=
						Polynomial.hornersMethod( c , r ) /
							Polynomial.hornersMethod( d , r );
				}
				else
				{
					r	-= SPLIT2;
					z	=
						Polynomial.hornersMethod( e , r ) /
							Polynomial.hornersMethod( f , r );
				}

				if ( q < 0.0 ) z = -z;
			}
								//	Improve the approximation
								//	using two Taylor series iteration.

			for ( int i = 0 ; i < 2; i++ )
			{
				double p1		= Sig.normal( z );

				double phi		=
					Math.sqrt( 1.0 / ( 2.0 * Math.PI ) ) *
						Math.exp( -( z * z ) / 2.0 );

				double z2		= ( p - p1 ) / phi;

				double x3		= ( 2.0D * ( z * z ) + 1.0D ) * z2 / 3.0D;
				double x2		= ( x3 + z ) * z2 / 2.0D;
				double x1		= ( ( x2 + 1.0 ) * z2 );

				z				+= x1;
			}
		}

		return z;
	}

	public static double normalInverseBad( final double p )
	{
		double result =
			Constants.SQRT2 *
				ErrorFunction.errorFunctionInverseBad( 2.0D * p - 1.0D );

		return result;
	}

	/**	Make class non-instantiable but inheritable.
	 */

	protected Normal()
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


