package edu.northwestern.at.utils.math.randomnumbers;

/*	Please see the license information at the end of this file. */

/**	Provides methods for generating random numbers from a variety
 *	of statistical distributions.
 *
 *	<p>
 *	The Mersenne Twister algorithm is used to generate the uniform
 *	random numbers used by all methods in this class.  The Mersenne
 *	Twister method provides an efficient portable means of generating
 *	random numbers with a long period of 2<sup>19937</sup>-1.
 *	This class uses an implementation of Mesenne Twister written
 *	by Luc Maisonobe.
 *	</p>
 */

public class RandomVariable
{
	/** Initialize Mersenne Twister generator. */

	private static MersenneTwister rnd	= new MersenneTwister();

	/** Generate a uniformly distributed random number between 0 and 1.
	 *
	 *	@return      A double between 0 and 1.
	 *
	 *	<p>
	 *	To use a different base random number generator, all you need to do
	 *	is override this method in a subclass.
	 *	</p>
	 */

	public static double rand()
	{
		return rnd.nextDouble();
	}

	/** Generate a random integer within a specific range.
	 *
	 *	@param	min		Minimum value for the random integer.
	 *	@param	max		Maximum value for the random integer.
	 *
	 *	@return			An integer between min and max.
	 */

	public static int randInt( int min , int max )
	{
		return min +
			new Double(
				Math.floor( ( max - min + 1 ) * rand() ) ).intValue();
	}

	/** Generate a random number from a beta random variable.
	 *
	 *	@param	a	First parameter of the Beta random variable.
	 *	@param	b	Second parameter of the Beta random variable.
	 *
	 *	@return		Beta distributed random number.
	 */

	public static double beta( double a , double b )
	{
		double x;
		double y;

		do
		{
			x	= Math.pow( rand() , 1.0D / a );
			y	= Math.pow( rand() , 1.0D / b );
		}
		while ( ( x + y ) > 1.0D );

		return x / ( x + y );
	}

	/** Generate a random boolean value from a binomial random variable.
	 *
	 *	@param	p	Probability for binomial.
	 *
	 *	@return		true or false.
	 */

	public static boolean binomial( double p )
	{
		return ( rand() < p );
	}

	/** Generate a random integer value from a binomial random variable.
	 *
	 *	@param	p	Probability for binomial.
	 *
	 *	@return		0 or 1.
	 */

	public static int binomialInteger( double p )
	{
		return ( rand() < p ) ? 0 : 1;
	}

	/** Generate a random number from a Cauchy random variable.
	 *
	 *	@param	median	Median of the Weibull random variable
	 *	@param	stddev	Second parameter of the Cauchy random variable.
	 *
	 *	@return			Cauchy distributed random number.
	 */

	public static double cauchy( double median , double stddev )
	{
		return stddev * Math.tan( Math.PI * ( rand() - 0.5D ) ) + median;
	}

	/** Generate a random number from a  chisquare random variable.
	 *
	 *	@param	df	Degrees of freedom of the chisquare random variable.
	 *
	 *	@return		Random number.
	 *
	 *	<p>
	 *	We compute the random chisquare value as the sum of "df" random normal
	 *	deviates squared.
	 *	</p>
	 */

	public static double chisquare( int df )
	{
		double result	= 0.0D;

		for ( int i = 0; i < df; i++ )
		{
			double norm = normal( 0 , 1 );

			result	+= norm * norm;
		}

		return result;
	}

	/** Generate a random number from a discrete random variable.
	 *
	 *	@param	values	Discrete values.
	 *	@param	pValues	Probability of each value.
	 *
	 *	@return			Random number.
	 */

	public static double dirac( double[] values , double[] pValues )
	{
		double[] cumulativePDF	= new double[ values.length ];

								// Compute cumulative probability distribution.

		cumulativePDF[ 0 ]	= pValues[ 0 ];

		for ( int i = 1 ; i < values.length ; i++ )
		{
			cumulativePDF[ i ]	= cumulativePDF[ i - 1 ] + pValues[ i ];
		}

		double random	= rand();
		double result	= 0;

								//	Seleect discrete value corresponding
								//  to matching position in cumulative PDF.

		for ( int i = 0 ; i < values.length - 1 ; i++ )
		{
			if	(	( random > cumulativePDF[ i ] ) &&
					( random < cumulativePDF[ i + 1 ] ) )
			{
				result	= values[ i ];
			}
		}

		return result;
	}

	/** Generate a random number from an exponential random variable.
	 *
	 *	@param	lambda	Parameter of the exponential random variable.
	 *
	 *	@return			Exponentially distributed random number.
	 *
	 *	<p>
	 *	The mean of the exponential distribution is 1/lambda and
	 *	the variance is 1/lambda^2 .
	 *	</p>
	 */

	public static double exponential( double lambda )
	{
		return -1.0D / lambda * Math.log( rand() );
	}

	/**	Generate a random number from a gamma distributed random variable.
	 *
	 *	@param	alpha	First parameter of gamma distribution.
	 *	@param	beta	Second parameter of gamma distribution.
	 *
	 *	@return			Gamma distributed random number.
	 */

	public static double gamma( double alpha , double beta )
	{
		if ( alpha < 1.0D )
		{
			double b = ( Math.E + alpha ) / Math.E;
			double y;
			double p;

			do
			{
				do
				{
					double u1	= rand();
					p			= b * u1;

					if ( p > 1 )
					{
						break;
					}

					y	= Math.pow( p , 1.0D / alpha );

					double u2	= rand();

					if ( u2 <= Math.exp( -y ) )
					{
						return beta * y;
					}
				}
				while ( true );

				y	= -Math.log( ( beta - p ) / alpha );

				double u2	= rand();

				if ( u2 <= Math.pow( y , alpha - 1 ) )
				{
					return beta * y;
				}
			}
			while ( true );
		}
		else
		{
			double a		= 1.0D / Math.sqrt( 2.0D * alpha - 1.0D );
			double b		= alpha - Math.log( 4.0 );
			double q		= alpha + 1.0D / a;
			double theta	= 4.5D;
			double d		= 1.0D + Math.log( theta );

			do
			{
				double u1	= rand();
				double u2	= rand();
				double v	= a * Math.log( u1 / ( 1.0D - u2 ) );
				double y	= alpha * Math.exp( v );
				double z	= u1 * u1 * u2;
				double w	= b + q * v + y;

				if ( ( ( w + d - theta * z ) >= 0.0D ) && ( w >= Math.log( z ) ) )
				{
					return beta * y;
				}
			}
			while ( true );
		}
	}

	/** Generate a random value from a geometric random variable.
	 *
	 *	@param	p	Probability for geometric random variable.
	 *
	 *	@return		Geometrically distributed random value.
	 */

	public static int geometric( double p )
	{
		return (int)Math.floor( Math.log( rand() ) / Math.log( 1.0D - p ) );
	}

	/** Generate a random number from a lognormal random variable.
	 *
	 *	@param	mean	Mean of the normal random variable.
	 *	@param	stddev	Standard deviation of the normal random variable.
	 *
	 *	@return			A lognormally distributed random number.
	 */

	public static double logNormal( double mean , double stddev )
	{
		return mean + stddev *
			Math.cos( 2.0D * Math.PI * rand() ) *
			Math.sqrt( -2.0 * Math.log( rand() ) );
	}

	/** Generate a random value from a negative binomial variable.
	 *
	 *	@param	s
	 *	@param	p	Probability for negative binomial random variable.
	 *
	 *	@return		Random value from negative binomial distribution.
	 */


	public static int negativebinomial( int s , double p )
	{
		int result = 0;

		for ( int i = 0 ; i < s ; i++ )
		{
			result	+= geometric( p );
		}

		return result;
	}

	/** Generate a random number from a Gaussian (Normal) random variable.
	 *
	 *	@param	mean	Mean of the random variable.
	 *	@param	stddev	Standard deviation of the random variable.
	 *
	 *	@return			Random number from normal distribution.
	 */

	public static double normal( double mean , double stddev )
	{
		return mean + stddev * rand();
	}

	/** Generate a random number from a poisson random variable.
	 *
	 *	@param	lambda	Parameter of the exponential random variable.
	 *
	 *	@return			Random number from Poisson distribution.
	 */

	public static int poisson( double lambda )
	{
		double a	= Math.exp( -lambda );
		double b	= 1.0D;

		int i		= 0;

		do
		{
			b	*= rand();

			if ( b < a )
			{
				return i;
			}

			i++;
		}
		while ( true );
	}

	/** Generate a random number from a symmetric triangular random variable.
	 *
	 *	@param	min		Minimum value of the random variable.
	 *	@param	max		Maximum value of the random variable.
	 *
	 *	@return			Symmetric triangular distributed random variable.
	 */

	public static double triangular( double min , double max )
	{
		return min / 2.0D + ( max - min ) * rand() / 2.0D +
			min / 2.0D + ( max - min ) * rand() / 2.0D;
	}

	/** Generate a random number from a non-symmetric triangular random variable.
	 *
	 *	@param	min		Minimum value of the random variable.
	 *	@param	median	Value of the random variable which has maximum density.
	 *	@param	max		Maximum value of the random variable.
	 *
	 *	@return			Nonsymmetric triangular random variable.
	 */

	public static double triangular( double min , double median , double max )
	{
		double y = rand();

		return ( y < ( ( median - min ) / ( max - min ) ) ) ?
			( min + Math.sqrt( y * ( max - min ) * ( median - min ) ) ) :
			( max - Math.sqrt( ( 1.0D - y ) * ( max - min ) * ( max - median ) ) );
	}

	/** Generate a random number from a uniform random variable.
	 *
	 *	@param	min	Mininum value for the random variable.
	 *	@param	max	Maximum value for the random variable.
	 *
	 *	@return		A random double between min and max.
	 */

	public static double uniform( double min , double max )
	{
		return min + ( max - min ) * rand();
	}

	/** Generate a random number from a Weibull random variable.
	 *
	 *	@param	eta		First parameter of the Weibull random variable.
	 *	@param	beta	Second parameter of the Weibull random variable.
	 *
	 *	@return			Weibull distributed random number.
	 */

	public static double weibull( double eta , double beta )
	{
		return Math.pow( -Math.log( 1.0D - rand() ), 1.0D / beta ) / eta;
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


