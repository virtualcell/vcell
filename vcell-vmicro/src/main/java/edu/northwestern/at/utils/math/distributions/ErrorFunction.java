package edu.northwestern.at.utils.math.distributions;

/*	Please see the license information at the end of this file. */
import edu.northwestern.at.utils.math.Constants;
import edu.northwestern.at.utils.math.Polynomial;

/**	Gaussian error function distribution functions.
 */

public class ErrorFunction
{
	/** Calculate value of Gaussian error function.
	 *
	 *	@param		x
	 *	@param		jint	Selects type of error function evaluation.
	 *						= 0: error function
	 *						= 1: complement of error function.
	 *						= 2: exp(xx) * complement of error function.
	 *
	 *	@return		Error function value.
	 *
	 *	<p>
	 *	The program returns erfc=0 for x > XBIG (see below);
	 *	erfcx = XINF for x < XNEG;
	 *	and erfcx = 0 for x >= XMAX.
	 *  </p>
	 *
	 *	<p>
	 *	The main computation evaluates near-minimax approximations
	 *	from "Rational Chebyshev approximations for the error function"
	 *	by W. J. Cody, Math. Comp., 1969, PP. 631-638.  This java code
	 *	is a straightforward translation of W. J. Cody's Fortran CALERF
	 *	function.
	 *	</p>
	 */

	protected static double calerf( double x , int jint )
	{
		double result;

		final double XNEG	= -26.628D;
		final double XSMALL	= 1.11E-16;
		final double XBIG	= 26.543D;
		final double XHUGE	= 6.71E7;
		final double XMAX	= 2.53E307;

		final double[] a =
		{
			3.1611237438705656D,
			113.864154151050156D,
			377.485237685302021D,
			3209.37758913846947D,
			0.185777706184603153D
		};

		final double[] b =
		{
			23.6012909523441209D,
			244.024637934444173D,
			1282.61652607737228D,
			2844.23683343917062D
		};

		final double[] c =
		{
			0.564188496988670089D,
			8.88314979438837594D,
			66.1191906371416295D,
			298.635138197400131D,
			881.95222124176909D,
			1712.04761263407058D,
			2051.07837782607147D,
			1230.33935479799725D,
			2.15311535474403846E-8
		};

		final double[] d =
		{
			15.7449261107098347D,
			117.693950891312499D,
			537.181101862009858D,
			1621.38957456669019D,
			3290.79923573345963D,
			4362.61909014324716D,
			3439.36767414372164D,
			1230.33935480374942D
		};

		final double[] p =
		{
			0.305326634961232344D,
			0.360344899949804439D,
			0.125781726111229246D,
			0.0160837851487422766D,
			6.58749161529837803E-4,
			0.0163153871373020978D
		};

		final double[] q =
		{
			2.56852019228982242D,
			1.87295284992346047D,
			0.527905102951428412D,
			0.0605183413124413191D,
			0.00233520497626869185D
		};

		final double SQRPI	= 0.56418958354775628695D;
		final double THRESH	= 0.46875D;

		double xden;
		double xnum;
		double y;
		double del;
		double ysq;

		if ( Double.isNaN( x ) )
		{
			return x;
        }

		y	= Math.abs( x );

		if ( y <= THRESH )
		{
								//	Evaluate  erf  for  |X| <= 0.46875
			ysq	= 0.0D;

			if ( y > XSMALL )
			{
				ysq = y * y;
			}

			xnum	= a[ 4 ] * ysq;
			xden	= ysq;

			for ( int i = 0; i < 3; i++ )
			{
				xnum	= ( xnum + a[ i ] ) * ysq;
				xden	= ( xden + b[ i ] ) * ysq;
			}

			result	= x * ( xnum + a[ 3 ] ) / ( xden + b[ 3 ] );

			if ( jint != 0 )
			{
				result = 1.0 - result;
			}

			if ( jint == 2 )
			{
				result *= Math.exp( ysq );
			}

			return result;
		}
								//	Evaluate erfc for 0.46875 <= |X| <= 4.0
		else if ( y <= 4.0 )
		{
			xnum	= c[ 8 ] * y;
			xden	= y;

			for ( int i = 0; i < 7; i++ )
			{
				xnum	= ( xnum + c[ i ] ) * y;
				xden	= ( xden + d[ i ] ) * y;
			}

			result	= ( xnum + c[ 7 ] ) / ( xden + d[ 7 ] );

			if ( jint != 2 )
			{
				ysq		= (double)((int)( y * 16.0D ) ) / 16.0D;
				del		= ( y - ysq ) * ( y + ysq );

				result	= Math.exp( -ysq * ysq ) * Math.exp( -del ) * result;
			}
		}
								//	Evaluate  erfc  for |X| > 4.0
		else
		{
			result	= 0.0;

			if ( ( jint == 2 ) && ( y >= XHUGE ) && ( y < XMAX ) )
			{
				result = SQRPI / y;
			}
			else if ( ( y < XBIG ) || ( jint == 2 ) && ( y < XMAX ) )
			{
				ysq		= 1.0D / ( y * y );
				xnum	= p[ 5 ] * ysq;
				xden	= ysq;

				for ( int i = 0; i < 4; i++ )
				{
					xnum	= ( xnum + p[ i ] ) * ysq;
					xden	= ( xden + q[ i ] ) * ysq;
				}

				result	= ysq * ( xnum + p[ 4 ] ) / ( xden + q[ 4 ] );
				result	= ( SQRPI - result ) / y;

				if ( jint != 2 )
				{
					ysq = (double)( (int)( y * 16.0D ) ) / 16.0D;
					del	= ( y - ysq ) * ( y + ysq );

					result	=
						Math.exp( -ysq * ysq ) * Math.exp( -del ) * result;
				}
			}
		}
								//	Fix up for negative argument, erf, etc.
		if ( jint == 0 )
		{
			result	= 1.0D - result;
			if ( x < 0.0D )
			{
				result = -result;
			}
		}
		else if ( jint == 1 )
		{
			if ( x < 0.0D )
			{
				result = 2.0D - result;
			}
		}
		else
		{
			if ( x < 0.0D )
			{
				if ( x < XNEG )
				{
					result = Double.POSITIVE_INFINITY;
				}
				else
				{
					ysq	= (double)( (int)( x * 16.0D) ) / 16.0D;
					del	= ( x - ysq ) * ( x + ysq );
					y	= Math.exp( ysq * ysq ) * Math.exp( del );

					result	= 2.0D * y - result;
				}
			}
		}

		return result;
	}

	/**	Error function.
	 *
	 *	@param	x	Error function argument.
	 *
	 *	@return		Error function value.
	 */

	public static double errorFunction( double x )
	{
		return calerf( x , 0 );
	}

	/**	Error function complement.
	 *
	 *	@param	x	Error function argument.
	 *
	 *	@return		Error function complement value.
	 */

	public static double errorFunctionComplement( double x )
	{
		return calerf( x , 1 );
	}

	/** Inverse error function.
	 *
	 *	@param	z	Probability for which matching percentage point
	 *				of error function is desired.
	 *
	 *	@return		Error function percentage point.
	 *
	 *	<p>
	 *	The percentage point is computed using the relationship
	 *	between the error function and the normal distribution.
	 *	</p>
	 */

	public static double errorFunctionInverse( double z )
	{
		return Normal.normalInverse( 0.5D * z +0.5D ) / Constants.SQRT2;
	}

	/**	Percentage points of error function distribution.
 	 *
  	 *	@param	x	Parameter for which percentage point is desired.
	 *
 	 *	@return		The percentage point of the error function distribution.
 	 *
 	 *	<p>
 	 *	The approximations used here come from in Blair, J. M. et. al.,
 	 *	"Rational Chebyshev Approximations for the Inverse Error
 	 *	Function," <strong>Math Comp.</strong> Vol. 30, No. 136, Oct. 1976,
 	 *	pp. 827-830.
 	 *	</p>
 	 */

	public static double errorFunctionInverseBad( double x )
	{
		boolean negative;
		double result;
								//	CCoefficients for |x| <= 0.75

		final double[] p0 =
		{
			-0.30866886527764497339E2,
			0.20652786402942339589E3,
			-0.52856770835093823310E3,
			0.64880509544036249088E3,
			-0.39205509901971391289E3,
			0.10706278097770074402E3,
			-0.10303488455439678272E2,
			0.15641510821923860000E0
		};

		final double[] q0 =
		{
			-0.28460290173882339383E2,
			0.20518924149238530630E3,
			-0.57617125090127638064E3,
			0.79669388170563770334E3,
			-0.56509305564023424022E3,
			0.19450320483954087700E3,
			-0.27371852306002662877E2,
			1.0D
		};
								//	Coefficients for 0.75 <= x <= 0.9375

		final double[] p1 =
		{
			0.94897362808681080020E-2,
			-0.24758242362823355485E0,
			0.25349389220714893916E1,
			-0.12954198980646771502E2,
			0.34810057749357500873E2,
			-0.47644367129787181802E2,
			0.29631331505876308122E2,
			-0.64200071507209448654E1,
			0.21489185007307062000E0
        };

		final double[] q1 =
        {
			0.67544512778850945940E-2,
			-0.18611650627372178511E0,
			0.20369295047216351160E1,
			-0.11315360624238054876E2,
			0.33880176779595142684E2,
			-0.53715373448862143348E2,
			0.41409991778428888715E2,
			-0.12831383833953226499E2,
			1.0D
		};

		negative	= ( x < 0.0D );

		if ( negative )
		{
			x = -x;
		}

		if ( x >= 1.0D )
		{
			return ( negative ?
				Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY );
		}

		if ( x > 0.9375D )
		{
			result	 = errorFunctionComplementInverseBad( 1.0D - x );
			return ( negative ? -result : result );
		}

		if ( x < 0.75D )
		{
			double xx	= x * x - 0.5625D;

			result	=
				Polynomial.hornersMethod( p0 , xx ) /
				Polynomial.hornersMethod( q0 , xx );
		}
		else
		{
			double xx	= x * x - 0.87890625D;

			result	=
				Polynomial.hornersMethod( p1 , xx ) /
				Polynomial.hornersMethod( q1 , xx );
		}

		return ( negative ? -result : result );
	}

	public static double errorFunctionComplementInverseBad( double x )
	{
		final double BOUNDARY	=
			( 1.0D / Math.sqrt( 100.0D * Constants.LN10 ) );

								//	Coefficients for
								//	0.9375 <= x <= 1 = 10^(-100)
		final double[] p0 =
		{
			0.19953288964537210824E-5,
			0.21273702631785953343E-3,
			0.58975595952407247651E-2,
			0.59481901452735809123E-1,
			0.31328289030932667506E0,
			0.13630199956442260990E1,
			0.34152815205652930673E1,
			0.30184181468933606839E1,
			0.20842433546207339433E1,
			0.85545635026743499993E0,
			0.40273918408712893132E-2,
			-0.15196139115744716810E-3
        };

        final double[] q0 =
        {
			0.19953210379374212953E-5,
			0.21274156963404084598E-3,
			0.59037062023731354671E-2,
			0.59959150110861092334E-1,
			0.32318083080817836442E0,
			0.14378337804749344527E1,
			0.37644571508257969664E1,
			0.44081435698143841173E1,
			0.42508710497182804606E1,
			0.22127469427969785343E1,
			1.0D
        };
								//	Coefficients for
        						//	1 - 10^(-100) <= x <= 1 - 10^(-10000)
        final double[] p1 =
        {
			0.45344689563209398449E-11,
			0.46156006321345332510E-8,
			0.12964481560643197452E-5,
			0.13714329569665128933E-3,
			0.60537914739162189689E-2,
			0.11279046353630280004E0,
			0.82810030904462690215E0,
			0.19507620287580568829E1,
			0.69952990607058154857E0
		};

		final double[] q1 =
		{
			0.45344687377088206782E-11,
			0.46156017600933592558E-8,
			0.12964671850944981712E-5,
			0.13715891988350205065E-3,
			0.60574830550097140404E-2,
			0.11311889334355782064E0,
			0.84001814918178042918E0,
			0.21238242087454993541E1,
			0.15771922386662040545E1,
			1.0D
		};

		boolean negative;
		double result;

		double y	= Math.abs( 1.0D - x );

		if ( y >= 1.0D )
		{
			return ( x > 2.0D ?
				Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY );
		}

		if ( x > 1.0D )
		{
			x			= 2.0D - x;
			negative	= true;
		}
		else
		{
			negative	= false;
		}

		if ( x == 0.0D )
		{
			return (
				negative ?
					Double.NEGATIVE_INFINITY :
					Double.POSITIVE_INFINITY );
		}

		if ( x > 0.0625D )
		{
			result	= errorFunctionInverseBad( 1.0D - x );
			return ( negative ? -result : result );
		}

		y	= 1.0D / Math.sqrt( -Math.log( x ) );

		if ( y > BOUNDARY )
		{
			result	=
				(	Polynomial.hornersMethod( p0 , y ) /
					Polynomial.hornersMethod( q0 , y ) ) / y;
		}
		else
		{
			result	=
				(	Polynomial.hornersMethod( p1 , y ) /
					Polynomial.hornersMethod( q1 , y ) ) / y;
		}

		return ( negative ? -result : result );
	}

	/**	Make class non-instantiable but inheritable.
	 */

	protected ErrorFunction()
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


