package edu.northwestern.at.utils.math.distributions;

/*	Please see the license information at the end of this file. */
import edu.northwestern.at.utils.math.Constants;

/**	Gamma distribution functions.
 */

public class Gamma
{
	/** Maximum value for which gamma which can be computed. */

	public static final double MAXGAM = 171.624376956302725D;

	/**	Gamma function.
	 *
	 *	@param	x	Value for which Gamma is to be computed.
	 *
	 *	@return		Value of gamma function if computable.
	 *					The value Double.POSITIVE_INFINITY is returned for
	 *					bad values of x or when overflow would occur.
	 *
	 *	<p>
	 *	The method used is presented in "An Overview of Software
	 *	Development for Special Functions" by W. J. Cody in
	 *	<strong>Lecture Notes in Mathematics</strong>,
	 *	506, Numerical Analysis Dundee, 1975, G. A. Watson
	 *	(ed.), Springer Verlag, Berlin, 1976.
	 *	</p>
	 *
	 *	<p>
	 *	Also see Hart et al, "Computer Approximations",
	 *	Wiley(1968), p. 130F.
	 *	</p>
	 *
	 *	<p>
	 *	This java code is a fairly straightforward translation of
	 *	the freely available Fortran function GAMMA by Cody and Stolz.
	 *	</p>
	 */

	public static double gamma( double x )
	{
		final double[] p =
		{
			-1.71618513886549492533811D,
			24.7656508055759199108314D,
			-379.804256470945635097577D,
			629.331155312818442661052D,
			866.966202790413211295064D,
			-31451.2729688483675254357D,
			-36144.4134186911729807069D,
			66456.1438202405440627855D
		};

		final double[] q =
		{
			-30.8402300119738975254353D,
			315.350626979604161529144D,
			-1015.15636749021914166146D,
			-3107.77167157231109440444D,
			22538.1184209801510330112D,
			4755.84627752788110767815D,
			-134659.959864969306392456D,
			-115132.259675553483497211D
		};

		final double[] c =
		{
			-0.001910444077728D,
			8.4171387781295e-4,
			-5.952379913043012e-4,
			7.93650793500350248e-4,
			-0.002777777777777681622553D,
			0.08333333333333333331554247D,
			0.0057083835261D
		};

		final double XBIG = 171.624D;
		final double XMININ = 2.23e-308;

								//	If argument is not a number,
								//	return that.

		if ( Double.isNaN( x ) )
		{
			return x;
        }

		boolean parity;

		int n;

		double fact;
		double xden;
		double xnum;
		double y;
		double z;
		double y1;
		double res;
		double sum;
		double ysq;

		parity	= false;
		fact	= 1.0D;
		n		= 0;
		y		= x;

		if ( y <= 0.0D )
		{
								//	Argument is negative.
			y	= -x;
			y1	= (double)((int)y);
			res	= y - y1;

			if ( res != 0.0D )
			{
				if ( y1 != (double)((int)( y1 * 0.5D ) ) * 2.0D )
				{
					parity = true;
				}

				fact	= -Math.PI / Math.sin( Math.PI * res );
				y		+= 1.0D;
			}
			else
			{
				return Double.POSITIVE_INFINITY;
//				return Double.MAX_VALUE;
			}
		}
								//	Argument is positive.

		if ( y < Constants.MACHEPS )
		{
								//	Argument is less than machine epsilon.

			if ( y >= XMININ )
			{
				res	= 1.0D / y;
			}
			else
			{
				return Double.POSITIVE_INFINITY;
//				return Double.MAX_VALUE;
			}
		}
		else if ( y < 12.0D )
		{
			y1	= y;

			if ( y < 1.0D )
			{
								//	0.0 < argument < 1.0
				z	= y;
				y	+= 1.0D;
			}
			else
			{
								//	1.0 < argument < 12.0.
								//	Reduce argument if necessary

				n	= (int)( y - 1.0D );
				y	-= (double)n;
				z	= y - 1.0D;
			}

								//	Evaluate approximation for
								//	1.0 < argument < 2.0
			xnum	= 0.0D;
			xden	= 1.0D;

			for ( int i = 0; i < 8; i++ )
			{
				xnum	= ( xnum + p[ i ] ) * z;
				xden	= xden * z + q[ i ];
			}

			res	= xnum / xden + 1.0D;

			if ( y1 < y )
			{
								//	Adjust result for 0.0 < argument < 1.0
				res /= y1;
			}
			else if ( y1 > y )
			{
								// Adjust result for  2.0 < argument < 12.0

				for ( int i = 1; i <= n; i++ )
				{
					res	*= y;
					y	+= 1.0D;
				}
			}
		}
		else
		{
								// Evaluate for argument >= 12.0
			if ( y <= XBIG )
			{
				ysq	= y * y;
				sum	= c[ 6 ];

				for ( int i = 0; i < 6; i++ )
				{
					sum	= sum / ysq + c[ i ];
				}

				sum = sum / y - y + Constants.LNSQRT2PI;
				sum	+= ( y - 0.5D ) * Math.log( y );

				res	= Math.exp( sum );
			}
			else
			{
				return Double.POSITIVE_INFINITY;
//				return Double.MAX_VALUE;
			}
		}
								// Perform final adjustments.
		if ( parity )
		{
			res	= -res;
		}

		if ( fact != 1.0D )
		{
			res	= fact / res;
		}

		return res;
	}

	/**	Calculate log of Gamma function.
	 *
	 *	@param	y		Gamma distribution parameter.
	 *
	 *	@return			Log gamma for specified parameter.
	 *					The value Double.POSITIVE_INFINITY is returned for
	 *					y <= 0.0 or when overflow would occur.
	 *
	 *	<p>
	 *	Minimax polynomial approximations are used over the
	 *	intervals [-inf,0], [0,.5], [.5,1.5], [1.5,4.0],
	 *	[4.0,12.0], [12.0,+inf].
	 *	</p>
	 *
	 *	<p>
	 *	See Hart et al, "Computer Approximations",
	 *	Wiley(1968), p. 130F, and also
	 *	Cody and Hillstrom, "Chebyshev approximations for
	 *	the natural logarithm of the Gamma function",
	 *	Mathematics of Computation, 21, April, 1967, P. 198F.
	 *	</p>
	 *
	 *	<p>
	 *	The minimax coefficients for y > 12 come from Hart et al.
	 *	The other coefficients come from unpublished work by
	 *	W. J. Cody and L. Stoltz at Argonne National Laboratory.
	 *	This java code is a fairly straightforward translation of
	 *	the freely available Fortran function ALGAMA by Cody and Stolz.
	 *	</p>
	 */

	public static double logGamma( double y )
	{
	    final double PNT68	= 0.6796875D;

		/*	Largest double value for which log(gamma(x)) can be represented.
		 */

		final double XBIG	= 2.5E305;

		/*	Approximate fourth root of XBIG.
		 */

		final double FRTBIG	= 2.25E76;

		/*	Numerator and denominator coefficients for rational minimax
	 	 *	approximation over (0.5,1.5).
		 */

		final double d1	=	-5.772156649015328605195174E-1;

    	final double[] p1 =
	    {
			4.945235359296727046734888E0,
			2.018112620856775083915565E2,
			2.290838373831346393026739E3,
			1.131967205903380828685045E4,
			2.855724635671635335736389E4,
			3.848496228443793359990269E4,
			2.637748787624195437963534E4,
			7.225813979700288197698961E3
    	};

	    final double[] q1 =
    	{
			6.748212550303777196073036E1,
			1.113332393857199323513008E3,
			7.738757056935398733233834E3,
			2.763987074403340708898585E4,
			5.499310206226157329794414E4,
			6.161122180066002127833352E4,
			3.635127591501940507276287E4,
			8.785536302431013170870835E3
    	};

		/*	Numerator and denominator coefficients for rational minimax
	 	 *	Approximation over (1.5,4.0).
	 	 */

		final double d2	=	4.227843350984671393993777E-1;

		final double[] p2 =
		{
			4.974607845568932035012064E0,
			5.424138599891070494101986E2,
			1.550693864978364947665077E4,
			1.847932904445632425417223E5,
			1.088204769468828767498470E6,
			3.338152967987029735917223E6,
			5.106661678927352456275255E6,
			3.074109054850539556250927E6
		};

		final double[] q2 =
		{
			1.830328399370592604055942E2,
			7.765049321445005871323047E3,
			1.331903827966074194402448E5,
			1.136705821321969608938755E6,
			5.267964117437946917577538E6,
			1.346701454311101692290052E7,
			1.782736530353274213975932E7,
			9.533095591844353613395747E6
		};

		/*	Numerator and denominator coefficients for rational minimax
 	 	 *	Approximation over (4.0,12.0).
 	 	 */

		final double d4	= 1.791759469228055000094023D;

		final double[] p4 =
		{
			1.474502166059939948905062E4,
			2.426813369486704502836312E6,
			1.214755574045093227939592E8,
			2.663432449630976949898078E9,
			2.940378956634553899906876E10,
			1.702665737765398868392998E11,
			4.926125793377430887588120E11,
			5.606251856223951465078242E11
		};

		final double[] q4 =
		{
			2.690530175870899333379843E3,
			6.393885654300092398984238E5,
			4.135599930241388052042842E7,
			1.120872109616147941376570E9,
			1.488613728678813811542398E10,
			1.016803586272438228077304E11,
			3.417476345507377132798597E11,
			4.463158187419713286462081E11
		};

		/*	Coefficients for minimax approximation over (12, INF).
 		 */

		final double[] c =
		{
			-1.910444077728E-03,
			8.4171387781295E-04,
			-5.952379913043012E-04,
			7.93650793500350248E-04,
			-2.777777777777681622553E-03,
			8.333333333333333331554247E-02,
			5.7083835261E-03
		};

		double	res;
		double	corr;
		double	ysq;
		double	xden;
		double	xnum;
		double	xm1;
		double	xm2;
		double	xm4;
								//	If argument is not a number,
								//	return that.

		if ( Double.isNaN( y ) )
		{
		    return y;
    	}

		if ( ( y > 0.0 ) && ( y <= XBIG ) )
		{
			if ( y <= Constants.MACHEPS )
			{
				res = -Math.log( y );
			}
			else if ( y <= 1.5D )
			{
								// MACHEPS < x <= 1.5

				if ( y < PNT68 )
				{
					corr	= -Math.log( y );
					xm1		= y;
				}
				else
				{
					corr	= 0.0D;
					xm1		= ( y - 0.5D ) - 0.5D;
				}

				if ( ( y <= 0.5D ) || ( y >= PNT68 ) )
				{
					xden	= 1.0D;
					xnum	= 0.0D;

					for ( int i = 0; i < 8; i++ )
					{
						xnum	= xnum * xm1 + p1[ i ];
						xden	= xden * xm1 + q1[ i ];
					}

					res	= corr + ( xm1 * ( d1 + xm1 * ( xnum / xden ) ) );
				}
				else
				{
					xm2		= ( y - 0.5D ) - 0.5D;
					xden	= 1.0D;
					xnum	= 0.0D;

					for ( int i = 0; i < 8; i++ )
					{
						xnum	= xnum * xm2 + p2[ i ];
						xden	= xden * xm2 + q2[ i ];
					}

					res	= corr + xm1 * ( d2 + xm2 * ( xnum / xden ) );
				}
			}
        	else if ( y <= 4.0D )
        	{
								// .5 .LT. X .LE. 4.0

				xm2		= y - 2.0D;
				xden	= 1.0D;
				xnum	= 0.0D;

				for ( int i = 0; i < 8; i++ )
				{
					xnum	= xnum * xm2 + p2[ i ];
					xden	= xden * xm2 + q2[ i ];
				}

				res	= xm2 * ( d2 + xm2 * ( xnum / xden ) );
			}
			else if ( y <= 12.0D )
			{
								//	4.0 < x < 12.0

				xm4		= y - 4.0D;
				xden	= -1.0D;
				xnum	= 0.0D;

				for ( int i = 0; i < 8; i++ )
				{
					xnum	= xnum * xm4 + p4[ i ];
					xden	= xden * xm4 + q4[ i ];
				}

				res	= d4 + xm4 * ( xnum / xden );
			}
			else
			{
								//	Evaluate for argument .GE. 12.0
				res	= 0.0D;

				if ( y <= FRTBIG )
				{
					res	= c[ 6 ];

					ysq	= y * y;

					for ( int i = 0; i < 6; i++ )
					{
						res	= res / ysq + c[ i ];
					}
				}

				res		= res / y;
				corr	= Math.log( y );

				res		= res + Constants.LNSQRT2PI - 0.5D * corr;
				res		= res + y * ( corr - 1.0D );
			}
		}
		else
		{
								// Return largest possible positive value
								// for bad arguments.

			res = Double.POSITIVE_INFINITY;
//			res = Double.MAX_VALUE;
		}

		return res;
	}

	/** Cumulative probability density function for the incomplete gamma function.
	 *
	 *	@param	x		Gamma distribution value
	 *	@param	alpha	Shape parameter
	 *	@param	dPrec	Digits of precision desired (1 < dPrec < Constants.MAXPREC)
	 *	@param	maxIter	Maximum number of iterations allowed
	 *
	 *	@return			Cumulative probability density function value
	 *
	 *	@throws			IllegalArgumentException
	 *						if x < 0 or alpha <= 0
	 *
	 *	<p>
	 *	Either an infinite series summation or a continued fraction
	 *	approximation is used, depending upon the argument range.
	 *	See Bhattacharjee GP (1970) The incomplete gamma integral.
	 *	Applied Statistics, 19: 285-287 (AS32) .  The result is
	 *	accurate to about 14 decimal digits.
	 *  </p>
	 */

	public static double incompleteGamma
	(
		double x,
		double alpha,
		int dPrec,
		int	maxIter
	)
		throws IllegalArgumentException
	{
		final double overflow	= 1.0E+37;
		final double minExp		= -87.0D;

		double[] pn	= new double[ 6 ];
		double gin;

		if ( x == 0.0D )
		{
			return 0.0D;
		}

		if ( ( x < 0.0D ) || ( alpha <= 0.0D ) )
		{
			throw new IllegalArgumentException( "x<0 or alpha<=0" );
		}

		double factor = alpha * Math.log( x ) - x - logGamma( alpha );
/*
		if ( factor < minExp )
		{
			return 1.0D;
		}
*/
		factor	= Math.exp( factor );

                                // Set desired precision for results.

		if ( dPrec > Constants.MAXPREC )
		{
			dPrec	= Constants.MAXPREC;
		}
		else if ( dPrec <= 0 )
		{
			dPrec	= 1;
		}

		double epsz	= Math.pow( 10.0D , -dPrec );

								// Choose between infinite series or
								// continued fraction approximations.

		if ( ( x > 1.0 ) && ( x >= alpha ) )
		{
								// Continued fraction.

			double a		=	1.0D - alpha;
			double b		= 	a + x + 1.0D;

			double term		= 0.0D;

			pn[ 0 ]			= 1.0D;
			pn[ 1 ]			= x;
			pn[ 2 ]			= x + 1.0D;
			pn[ 3 ]			= x * b;

			gin				= pn[ 2 ] / pn[ 3 ];
			int iter		= 0;

			do
			{
				iter++;
				term++;
				a++;

				b			+= 2;

				double an	= a * term;

				pn[ 4 ]		= b * pn[ 2 ] - an * pn[ 0 ];
				pn[ 5 ]		= b * pn[ 3 ] - an * pn[ 1 ];

				if ( pn[ 5 ] != 0 )
				{
					double rn	= pn[ 4 ] / pn[ 5 ];

					double dif	= Math.abs( gin - rn );

					if ( dif <= epsz )
					{
						if ( dif <= ( epsz * rn ) )
						{
							break;
						}
	 				}

					gin	= rn;
				}

				pn[ 0 ]	= pn[ 2 ];
				pn[ 1 ]	= pn[ 3 ];
				pn[ 2 ]	= pn[ 4 ];
				pn[ 3 ]	= pn[ 5 ];

				if ( Math.abs( pn[ 4 ] ) >= overflow )
				{
					pn[ 0 ]	/= overflow;
					pn[ 1 ]	/= overflow;
					pn[ 2 ]	/= overflow;
					pn[ 3 ]	/= overflow;
				}
			}
			while ( iter <= maxIter );

			gin = 1.0 - ( factor * gin );
		}
		else
		{
								// Series expansion.

			int iter		= 0;

			gin				= 1.0D;
			double term		= 1.0D;
			double a		= alpha;

			do
			{
				a++;
				term	*= x / a;
				gin		+= term;
			}
			while ( ( iter <= maxIter ) && ( ( term / gin ) > epsz ) );

			gin	*= factor / alpha;
		}

		return gin;
	}

	/** Cumulative probability density function for the incomplete gamma function.
	 *
	 *	@param	x		Gamma distribution value
	 *	@param	alpha	Shape parameter
	 *
	 *	@return			Cumulative probability density function value
	 *
	 *	@throws			IllegalArgumentException
	 *						if x < 0 or alpha <= 0
	 *
	 *	<p>
	 *	Either an infinite series summation or a continued fraction
	 *	approximation is used, depending upon the argument range.
	 *	See Bhattacharjee GP (1970) The incomplete gamma integral.
	 *	Applied Statistics, 19: 285-287 (AS32) .  The result is
	 *	accurate to about 14 decimal digits.
	 *  </p>
	 */

	public static double incompleteGamma
	(
		double x,
		double alpha
	)
		throws IllegalArgumentException
	{
		return incompleteGamma( x , alpha , Constants.MAXPREC , 1000 );
	}

	/**	Make class non-instantiable but inheritable.
	 */

	protected Gamma()
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


