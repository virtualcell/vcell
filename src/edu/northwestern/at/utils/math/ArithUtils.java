package edu.northwestern.at.utils.math;

/*	Please see the license information in the header below. */

/**	Basic numeric operations not included in standard Java run-time library.
 *
 *	<p>
 *	The binomial methods are modified from those in the Colt library.
 *	</p>
 *
 *	<p>
 *	The following methods for trigonometric functions come from the
 *	Sfun class written by Visual Numerics.
 *	</p>
 *
 *	<ul>
 *	<li>acosh</li>
 *	<li>asinh</li>
 *	<li>atanh</li>
 *	<li>cot</li>
 *	<li>cosh</li>
 *	<li>sinh</li>
 *	<li>tanh</li>
 *	</ul>
 *
 *	<p>
 *	These methods are covered by the following license.
 *	</p>
 *
 * -------------------------------------------------------------------------
 *	$Id: Sfun.java,v 1.1.1.1 1999/03/05 21:43:39 brophy Exp $
 * -------------------------------------------------------------------------
 * Copyright (c) 1997 - 1998 by Visual Numerics, Inc. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is freely
 * granted by Visual Numerics, Inc., provided that the copyright notice
 * above and the following warranty disclaimer are preserved in human
 * readable form.
 *
 * Because this software is licensed free of charge, it is provided
 * "AS IS", with NO WARRANTY.  TO THE EXTENT PERMITTED BY LAW, VNI
 * DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO ITS PERFORMANCE, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * VNI WILL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE
 * OF OR INABILITY TO USE THIS SOFTWARE, INCLUDING BUT NOT LIMITED TO DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, PUNITIVE, AND EXEMPLARY DAMAGES, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * -------------------------------------------------------------------------
 */

public class ArithUtils
{
	/** The smallest relative spacing for doubles.*/

	public final static double EPSILON_SMALL = Constants.MACHEPS / 2.0D;

	/** The largest relative spacing for doubles. */

	public final static double EPSILON_LARGE = Constants.MACHEPS;

	// Series on [0,0.0625]

	private static final double	COT_COEF[] =
	{
		.240259160982956302509553617744970e+0,
		-.165330316015002278454746025255758e-1,
		-.429983919317240189356476228239895e-4,
		-.159283223327541046023490851122445e-6,
		-.619109313512934872588620579343187e-9,
		-.243019741507264604331702590579575e-11,
		-.956093675880008098427062083100000e-14,
		-.376353798194580580416291539706666e-16,
		-.148166574646746578852176794666666e-18
	};

	// Series on the interval [0,1]

	private static final double	SINH_COEF[] =
	{
		0.1730421940471796,
		0.08759422192276048,
		0.00107947777456713,
		0.00000637484926075,
		0.00000002202366404,
		0.00000000004987940,
		0.00000000000007973,
		0.00000000000000009
	};

	// Series on [0,1]

	private static final double	TANH_COEF[] =
	{
		-.25828756643634710,
		-.11836106330053497,
		.009869442648006398,
		-.000835798662344582,
		.000070904321198943,
		-.000006016424318120,
		.000000510524190800,
		-.000000043320729077,
		.000000003675999055,
		-.000000000311928496,
		.000000000026468828,
		-.000000000002246023,
		.000000000000190587,
		-.000000000000016172,
		.000000000000001372,
		-.000000000000000116,
		.000000000000000009
	};

	// Series on the interval [0,1]

	private static final double	ASINH_COEF[] =
	{
		 -.12820039911738186343372127359268e+0,
		-.58811761189951767565211757138362e-1,
		.47274654322124815640725249756029e-2,
		-.49383631626536172101360174790273e-3,
		.58506207058557412287494835259321e-4,
		-.74669983289313681354755069217188e-5,
		.10011693583558199265966192015812e-5,
		-.13903543858708333608616472258886e-6,
		.19823169483172793547317360237148e-7,
		-.28847468417848843612747272800317e-8,
		.42672965467159937953457514995907e-9,
		-.63976084654366357868752632309681e-10,
		.96991686089064704147878293131179e-11,
		-.14844276972043770830246658365696e-11,
		.22903737939027447988040184378983e-12,
		-.35588395132732645159978942651310e-13,
		.55639694080056789953374539088554e-14,
		-.87462509599624678045666593520162e-15,
		.13815248844526692155868802298129e-15,
		-.21916688282900363984955142264149e-16,
		.34904658524827565638313923706880e-17
	};

	// Series on the interval [0,0.25]

	private static final double	ATANH_COEF[] =
	{
		.9439510239319549230842892218633e-1,
		.4919843705578615947200034576668e-1,
		.2102593522455432763479327331752e-2,
		.1073554449776116584640731045276e-3,
		.5978267249293031478642787517872e-5,
		.3505062030889134845966834886200e-6,
		.2126374343765340350896219314431e-7,
		.1321694535715527192129801723055e-8,
		.8365875501178070364623604052959e-10,
		.5370503749311002163881434587772e-11,
		.3486659470157107922971245784290e-12,
		.2284549509603433015524024119722e-13,
		.1508407105944793044874229067558e-14,
		.1002418816804109126136995722837e-15,
		.6698674738165069539715526882986e-17,
		.4497954546494931083083327624533e-18
	};

	/**	Return the inverse (arc) hyperbolic cosine of a double.
	 *
	 *	@param	x	Double whose inverse hyperbolic cosine is desired.
	 *
	 *	@return		The inverse hyperbolic cosine of x.
	 *
	 *	<p>
	 *	If x is NaN or less than one, the result is NaN.
	 *	</p>
	 *
	 *	<p>
	 *	This method is a modified version of the one in the
	 *	Visual Numerics Sfun class.
	 *	</p>
	 */

	public static double acosh( double x )
	{
		double ans;

		if ( Double.isNaN( x ) || ( x < 1 ) )
		{
			ans	= Double.NaN;
		}
								// 94906265.62 = 1.0/Math.sqrt(EPSILON_SMALL)

		else if ( x < 94906265.62 )
		{
			ans	= safeLog( x + Math.sqrt( x * x - 1.0D ) );
		}
		else
		{
			ans	= 0.69314718055994530941723212145818D + safeLog( x );
		}

		return ans;
	}

	/**	Check if two doubles are equal to machine precision.
	 *
	 *	@param	a	First double.
	 *	@param	b	Second double.
	 *
	 *	@return		True if a and b are equal to machine precision.
	 */

	public static boolean areEqual( double a , double b )
	{
		return areEqual( a , b , Constants.MACHEPS );
	}

	/**	Check if two doubles are equal to specified tolerance.
	 *
	 *	@param	a			First double.
	 *	@param	b			Second double.
	 *	@param	tolerance	Tolerance.
	 *
	 *	@return				True if a and b are equal to specified tolerance.
	 */

	public static boolean areEqual( double a , double b , double tolerance )
	{
		boolean result	= ( a == b );

		if ( !result )
		{
			if ( a == 0.0D )
			{
				result	= ( Math.abs( b ) <= tolerance );
			}
			else if ( b == 0.0D )
			{
				result	= ( Math.abs( a ) <= tolerance );
			}
			else
			{
				result	= ( fuzzyCompare( a , b , tolerance ) == 0 );
			}
		}

		return result;
	}

	/**	Return the inverse (arc) hyperbolic sine of a double.
	 *
	 *	@param	x	The value whose inverse hyperbolic sine is desired.
	 *
	 *	@return		The inverse hyperbolic sine of x.
	 *
	 *	<p>
	 *	If x is NaN, the result is NaN.
	 *	</p>
	 *
	 *	<p>
	 *	This method is a modified version of the one in the
	 *	Visual Numerics Sfun class.
	 *	</p>
	 */

	public static double asinh( double x )
	{
		double	ans;

		double	y	= Math.abs( x );

		if ( Double.isNaN( x ) )
		{
			ans = Double.NaN;
		}
								// 1.05367e-08 = Math.sqrt(EPSILON_SMALL)

		else if ( y <= 1.05367e-08 )
		{
			ans	= x;
		}
		else if ( y <= 1.0D )
		{
			ans	=
				x *
				( 1.0D +
					Polynomial.evaluateChebyschev(
						ASINH_COEF , 2.0D * x * x - 1.0D ) );
		}
								// 94906265.62 = 1/Math.sqrt(EPSILON_SMALL)

		else if ( y < 94906265.62D )
		{
			ans	= safeLog( y + Math.sqrt( y * y + 1.0D ) );
		}
		else
		{
			ans	= 0.69314718055994530941723212145818D + safeLog( y );
		}

		if ( x < 0.0D ) ans = -ans;

		return ans;
	}

	/**	Returns the inverse (arc) hyperbolic tangent of a double.
	 *
	 *	@param	x	The value whose inverse hyperbolic tangent is desired.
	 *
	 *	@return		 The arc hyperbolic tangent of x.
	 *
	 *	<p>
	 *	If x is NaN or |x|>1, the result is NaN.
	 *	</p>
	 *
	 *	<p>
	 *	This method is a modified version of the one in the
	 *	Visual Numerics Sfun class.
	 *	</p>
	 */

	public static double atanh( double x )
	{
		double	ans;

		double	y	= Math.abs( x );

		if ( Double.isNaN( x ) )
		{
			ans = Double.NaN;
		}
								// 1.82501e-08 = Math.sqrt(3.0*EPSILON_SMALL)

		else if ( y < 1.82501e-08 )
		{
			ans	= x;
		}
		else if ( y <= 0.5D )
		{
			ans	=
				x *
				( 1.0D +
					Polynomial.evaluateChebyschev(
						ATANH_COEF , 8.0D * x * x - 1.0D ) );
		}
		else if ( y < 1.0D )
		{
			ans	= 0.5D * safeLog( ( 1.0D + x ) / ( 1.0D - x ) );
		}
		else if ( y == 1.0D )
		{
			ans	= x * Double.POSITIVE_INFINITY;
		}
		else
		{
			ans	= Double.NaN;
		}

		return ans;
	}

	/**	Efficiently returns the binomial coefficient, often also referred to as "n over k" or "n choose k".
	 *
	 *	<p>
	 *	The binomial coefficient is defined as <tt>(n * n-1 * ... * n-k+1 ) / ( 1 * 2 * ... * k )</tt>.
	 *	</p>
	 *
	 *	<ul>
	 *	<li>k<0<tt>: <tt>0</tt>.</li>
	 *	<li>k==0<tt>: <tt>1</tt>.</li>
	 *	<li>k==1<tt>: <tt>n</tt>.</li>
	 *	<li>else: <tt>(n * n-1 * ... * n-k+1 ) / ( 1 * 2 * ... * k )</tt>.</li>
	 *	</ul>
	 *
	 *	@return		The binomial coefficient.
	 */

	public static double binomial( double n , long k )
	{
		if ( k < 0 ) return 0.0D;
		if ( k == 0 ) return 1.0D;
		if ( k == 1 ) return (double)n;

								// Compute binomial(n,k) =
								// (n * n-1 * ... * n-k+1 ) / ( 1 * 2 * ... * k )

		double a		= n - k + 1;
		double b		= 1.0D;
		double binomial	= 1.0D;

		for ( long i = k; i-- > 0; )
		{
			binomial *= (a++) / (b++);
		}

		return binomial;
	}

	/**	Efficiently returns the binomial coefficient, often also referred to as "n over k" or "n choose k".
	 *
	 *	<p>
	 *	The binomial coefficient is defined as
	 *	</p>
	 *	<ul>
	 *	<li>k<0<tt>: <tt>0</tt>.</li>
	 *	<li>k==0 || k==n<tt>: <tt>1</tt>.</li>
	 *	<li>k==1 || k==n-1<tt>: <tt>n</tt>.</li>
	 *	<li>else: <tt>(n * n-1 * ... * n-k+1 ) / ( 1 * 2 * ... * k )</tt>.</li>
	 *	</ul>
	 *
	 *	@return 		The binomial coefficient.
	 */

	public static double binomial( long n , long k )
	{
		if ( k < 0 ) return 0.0D;
		if ( ( k == 0 ) || ( k == n ) ) return 1.0D;
		if ( ( k == 1 ) || ( k == ( n - 1 ) ) ) return n;

		// try quick version and see whether we get numeric overflows.
		// factorial(..) is O(1); requires no loop; only a table lookup.
		if (n > k)
		{
			int max	= Factorial.longFactorials.length +
				Factorial.doubleFactorials.length;

			if (n < max)
			{ // if (n! < inf && k! < inf)
				double n_fac = Factorial.factorial((int) n);
				double k_fac = Factorial.factorial((int) k);
				double n_minus_k_fac = Factorial.factorial((int) (n - k));
				double nk = n_minus_k_fac * k_fac;

				if (nk != Double.POSITIVE_INFINITY)
				{ // no numeric overflow?
					// now this is completely safe and accurate
					return n_fac / nk;
				}
			}
			if ( k > ( n / 2 ) ) k = n - k; // quicker
		}

		// binomial(n,k) = (n * n-1 * ... * n-k+1 ) / ( 1 * 2 * ... * k )

		long a			= n - k + 1;
		long b			= 1;
		double binomial = 1.0D;

		for ( long i = k ; i-- > 0; )
		{
			binomial	*= ( (double)( a++)) / (b++);
		}

		return binomial;
	}

	/**	Return the hyperbolic cosine of a double.
	 *
	 *	@param	x	The value whose hyperbolic cosine is desired.
	 *
	 *	@return		The hyperbolic cosine of x.
	 *
	 *	<p>
	 *	If x is NaN, the result is NaN.
	 *	</p>
	 *
	 *	<p>
	 *	This method is a modified version of the one in the
	 *	Visual Numerics Sfun class.
	 *	</p>
	 */

	static public double cosh( double x )
	{
		double	ans;

		double	y	= Math.exp( Math.abs( x ) );

		if ( Double.isNaN( x ) )
		{
			ans	= Double.NaN;
		}
		else if ( Double.isInfinite( x ) )
		{
			ans	= x;
		}
								// 94906265.62 = 1.0/Math.sqrt(EPSILON_SMALL)

		else if ( y < 94906265.62D )
		{
			ans	= 0.5D * ( y + 1.0D / y );
		}
		else
		{
			ans	= 0.5D * y;
		}

		return ans;
	}

	/**	Return the contangent of a double.
	 *
	 *	@param	x	The number whose cotangent is desired.
	 *
	 *	@return		The cotangent.
	 *
	 *	<p>
	 *	This method is a modified version of the one in the
	 *	Visual Numerics Sfun class.
	 *	</p>
	 */

	public static double cot( double x )
	{
		double ans, ainty, ainty2, prodbg, y, yrem;
		double pi2rec = 0.011619772367581343075535053490057; //  2/PI - 0.625

		y	= Math.abs( x );

								// 4.5036e+15 = 1.0/EPSILON_LARGE

		if ( y > 4.5036e+15 )
		{
			return Double.NaN;
		}

		// Carefully compute
		// Y * (2/PI) = (AINT(Y) + REM(Y)) * (.625 + PI2REC)
		//		= AINT(.625*Y) + REM(.625*Y) + Y*PI2REC  =  AINT(.625*Y) + Z
		//		= AINT(.625*Y) + AINT(Z) + REM(Z)

		ainty  = (int)y;
		yrem   = y - ainty;
		prodbg = 0.625D * ainty;
		ainty  = (int)prodbg;
		y      = ( prodbg - ainty ) + 0.625D * yrem + y * pi2rec;
		ainty2 = (int)y;
		ainty  = ainty + ainty2;
		y      = y - ainty2;

		int ifn	= (int)( ainty % 2.0 );

		if ( ifn == 1 ) y	= 1.0D - y;

		if ( y == 0.0D )
		{
			ans = Double.POSITIVE_INFINITY;
		}
								// 1.82501e-08 = Math.sqrt(3.0*EPSILON_SMALL)

		else if ( y <= 1.82501e-08 )
		{
			ans = 1.0D / y;
		}

		else if ( y <= 0.25D )
		{
			ans =
				( 0.5D +
					Polynomial.evaluateChebyschev(
						COT_COEF , 32.0D * y * y - 1.0D ) ) / y;
		}

		else if ( y <= 0.5D )
		{
			ans =
				(	0.5D +
					Polynomial.evaluateChebyschev(
						COT_COEF , 8.0D * y * y - 1.0D ) ) / ( 0.5D * y );

			ans	= ( ans * ans - 1.0D ) * 0.5D / ans;
		}

		else
		{
	        ans =
	        	(	0.5D +
	        		Polynomial.evaluateChebyschev(
	        			COT_COEF , 2.0D * y * y - 1.0D ) ) / ( 0.25D * y );

//$$$PIB$$$ Is one of the following two lines bogus?

		    ans	= ( ans * ans - 1.0D ) * 0.5D / ans;
			ans	= ( ans * ans - 1.0D ) * 0.5D / ans;
		}

		if ( x != 0.0D ) ans = sign( ans , x );

		if ( ifn == 1 ) ans = -ans;

		return ans;
	}

	/**	Get exp( x ) - 1.
	 *
	 *	@param	x	The number for which to find exp( x ) - 1.
	 *
	 *	@return		exp( x ) - 1.
	 *
	 *	<p>
	 *	Example:	expm1( 9.995003330835334E-4 ) is 0.001 .
	 *	</p>
	 *
	 *	<p>
	 *	Implements a method suggested by William Kahan.
	 *	</p>
	 */

	public static double expm1( double x )
	{
		double result;

		double u	= Math.exp( x );

		if ( u == 1.0D )
		{
			result	= x;
		}

		else if ( ( u - 1.0D )  == -1.0D )
		{
			result	= -1.0D;
		}

		else
		{
			result	= ( u - 1.0D ) * ( x / Math.log( u ) );
		}

		return result;
	}

	/**	Perform fuzzy comparison of two doubles with specified tolerance.
	 *
	 *	@param	a			First double.
	 *	@param	b			Second double.
	 *	@param	tolerance	Tolerance value.
	 *
	 *	@return				1 if a > b.
	 *						0 if a ~= b.
	 *						-1 if a < b.
	 *
	 *	<p>
	 *	This is an implementation of an algorithm suggested by
	 *	Donald E. Knuth in Section 4.2.2 of
	 *	<em>Seminumerical Algorithms (3rd edition)</em>.
	 *	</p>
	 */

	public static int fuzzyCompare( double a , double b , double tolerance )
	{
								//	Check for exacty equality first
								//	to handle NaNs.

		if ( a == b ) return 0;

								//	Compute difference of a and b.

		double difference	= a - b;

								//	Find exponent for whichever of a or b
								//	has largest absolute value.

		double maxAbs		= Math.max( Math.abs( a ) , Math.abs( b ) );

								//	Get exponent.  Round up to next
								//	power of two.

		int exponent		= new SplitDouble( maxAbs ).exponent;

								//	Form neighborhood of size  2 * delta.

		double delta		= tolerance * Math.pow( 2 , exponent );

								//	Assume a and b at least approximately
								//	equal.

		int result			= 0;

								//	Return 1 if a > b and difference
								//	is outside delta neighborhood.

		if ( difference > delta )
		{
			result	= 1;
		}
								//	Return -1 if a < b and difference is
								//	outside delta neighborhood.

		else if ( difference < -delta )
		{
			result	= -1;
		}
								//	If difference lies between
								//	-delta and delta, a is exactly
								//	or approximately equal to b .
								//	Return 0 in this case.
		return result;
	}

	/**	Safely calculate hypotenuse value.
	 *
	 *	@param	a	One leg of triangle.
	 *	@param	b	Second leg of triangle.
	 *
	 *	@return		Hypotenuse value.
	 *
	 *	<p>
	 *	The hypotenuse value is given mathematically as
	 *	the sqrt( a^2 + b^2 ).  The method implemented
	 *	here reduces the chances of cancellation and
	 *	roundoff error.  If the |a| > |b|, we compute
	 *	the hypotenuse as:
	 *	</p>
	 *
	 *	<p>
	 *  hypotenuse	= |a| * sqrt( 1 + (b/a) * (b/a) )
	 *	</p>
	 *
	 *	<p>
	 *	Otherwise b != 0 compute the hypotenuse as:
	 *	</p>
	 *
	 *	<p>
	 *  hypotenuse	= |b| * sqrt( 1 + (a/b) * (a/b) )
	 *	</p>
	 *
	 *	<p>
	 *	If b is zero, the hypotenuse is zero.
	 *	</p>
	 */

	public static double hypot( double a , double b )
	{
		double r;

		if ( Math.abs( a ) > Math.abs( b ) )
		{
			r	= b / a;
			r	= Math.abs( a ) * Math.sqrt( 1.0D + r * r );
		}
		else if ( b != 0 )
		{
			r	= a / b;
			r	= Math.abs( b ) * Math.sqrt( 1.0D + r * r );
		}
		else
		{
			r	= 0.0D;
		}

		return r;
	}

	/**	Check if number is negative zero.
	 *
	 *	@param	x	The number to check.
	 *
	 *	@return		true if x is negative zero, false otherwise.
	 */

	public static boolean isNegativeZero( double x )
	{
		return ( ( x == 0.0D ) && ( ( 1.0D / x ) < 0.0D ) );
	}

	/**	Get log base 2 of a double.
	 *
	 *	@param	x	The number whose log base 2 value is desired.
	 *
	 *	@return		The log base 2 of x.
	 *				If x is <= 0, 0 is returned.
	 *
	 *	<p>
	 *	Example:	log2( 32 ) is 5.0D .
	 *	</p>
	 */

    public static double log2( double x )
    {
    	double result	= 0.0D;

		if ( x > 0.0D )
		{
			result	= safeLog( x ) * Constants.LN2INV;
		}

		return result;
    }

	/**	Get log base 10 of a double.
	 *
	 *	@param	x	The number whose log base 10 value is desired.
	 *
	 *	@return		The log base 10 of x.
	 *				If x is <= 0, 0 is returned.
	 *
	 *	<p>
	 *	Example:	log10( 100.0D ) is 2.0D .
	 *	</p>
	 */

    public static double log10( double x )
    {
    	double result	= 0.0D;

		if ( x > 0.0D )
		{
			result	= safeLog( x ) * Constants.LN10INV;
		}

		return result;
    }

	/**	Get log of a double + 1.
	 *
	 *	@param	x	The number for which we want log( x + 1 ).
	 *
	 *	@return		The log of ( x + 1 ).
	 *
	 *	<p>
	 *	Example:	log1p( 0.001D ) is 9.995003330835334E-4
	 *	</p>
	 *
	 *	<p>
	 *	Implements a method suggested by William Kahan.
	 *	</p>
	 */

	public static double log1p( double x )
	{
		double result;
		double u;
								//	Use log(), corrected to first order
								//	for truncation loss.
		u	= 1.0D + x;

		if ( u == 1.0D )
		{
			result	= x;
		}
		else
		{
			result	= ( Math.log( u ) * ( x / ( u - 1.0D ) ) );
		}

		return result;
	}

	/**	Get the log( exp( logX ) + exp( logY ) ).
	 *
	 *	@param	logX	Log( x )
	 *	@param	logY	Log( y )
	 *
	 *	@return			log( x + y ) .
	 */

	public static double logSumLogs( double logX , double logY )
	{
		return Math.max( logX , logY ) +
			log1p( Math.exp( -Math.abs( logX - logY ) ) );
	}

	/**	Get the log( exp( logX ) - exp( logY ) ).
	 *
	 *	@param	logX	Log( x )
	 *	@param	logY	Log( y )
	 *
	 *	@return			log( x - y ) .
	 */

	public static double logDiffLogs( double logX , double logY )
	{
		return logX + log1p( -Math.exp( logY - logX ) );
	}

    /**	Round double to specified number of decimal places.
     *
     *	@param	x	The double to round.
     *	@param	n	The number of decimal places to round to.
     *
     *	@return		x rounded to n decimal places.
     */

	public static double round( double x , int n )
	{
		double	pow10	= Math.pow( 10 , n );

		return Math.round( x * pow10 ) / pow10;
	}

	/**	Return natural log of a double.
	 *
	 *	@param	x	The number whose natural log is desired.
	 *
	 *	@return		The natural log of x.  If x is zero,
	 *				returns 0.
	 */

	public static double safeLog( double x )
	{
		if ( x == 0.0D )
		{
			return 0.0D;
		}
		else
		{
			return Math.log( x );
		}
	}

	/**	Return sign of an integer.
	 *
	 *	@param	n	Number whose sign is desired.
	 *
	 *	@return		-1 if n < 0, 0 if n isn 0, 1 if n > 0.
	 */

	public static int sign( int n )
	{
		if ( n > 0 )
		{
			return 1;
		}
		else if ( n < 0 )
		{
			return -1;
        }

		return 0;
	}

	/**	Return sign of a double.
	 *
	 *	@param	d	double whose sign is desired.
	 *
	 *	@return		-1 if d < 0, 0 if d is 0, 1 if d > 0.
	 */

	public static int sign( double d )
	{
		if ( d > 0.0D )
		{
			return 1;
		}
		else if ( d < 0.0D )
		{
			return -1;
        }

		return 0;
	}

	/**	Return the value of a double with the sign of another double.
	 *
	 *	@param	x	First double.
	 *	@param	y	Second double.
	 *
	 *	@return		x with the sign of y.
	 */

	public static double sign( double x , double y )
	{
		double abs_x	= ( ( x < 0 ) ? -x : x );

		return ( y < 0.0 ) ? -abs_x : abs_x;
	}

	/**	Compute hyperbolic sine of a double.
	 *
	 *	@param	x	The number whose hyperbolic sine is desired.
	 *
	 *	@return		The hyperbolic sine of x.
	 *
	 *	<p>
	 *	This method is a modified version of the one in the
	 *	Visual Numerics Sfun class.
	 *	</p>
	 */

	public static double sinh( double x )
	{
		double	ans;

		double	y	= Math.abs( x );

		if ( Double.isNaN( x ) )
		{
			ans = Double.NaN;
		}

		else if ( Double.isInfinite( y ) )
		{
			return x;
		}
								// 2.58096e-08 = Math.sqrt( 6.0 * EPSILON_SMAL L)

		else if ( y < 2.58096e-08 )
		{
			ans	= x;
		}

		else if ( y <= 1.0D )
		{
			ans	=
				x *
				( 1.0D +
					Polynomial.evaluateChebyschev(
						SINH_COEF , 2.0D * x * x - 1.0D ) );
		}

		else
		{
			y	= Math.exp( y );

								// 94906265.62 = 1.0/Math.sqrt(EPSILON_SMALL)

			if ( y >= 94906265.62D )
			{
				ans	= sign( 0.5D * y , x );
			}
			else
			{
				ans	= sign( 0.5D * ( y - 1.0D / y ) , x );
			}
		}

		return ans;
	}

	/** Return the integer portion of a double as a double.
	 *
	 *	@param	x	The double whose integer portion is to be found.
	 *
	 *	@return		The integer portion of x.
	 *
	 *	<p>
	 *	Example:	trunc( 30.12345D ) is 30.0D .
	 *	</p>
	 */

	public static double trunc( double x )
	{
		long lx	= (long)x;
		return (double)lx;
	}


	/**	Return the hyperbolic tangent of a double.
	 *
	 *	@param	x	The value whose hyperbolic tangent is desired.
	 *
	 *	@return		The hyperbolic tangent of x.
	 *
	 *	<p>
	 *	This method is a modified version of the one in the
	 *	Visual Numerics Sfun class.
	 *	</p>
	 */

	public static double tanh( double x )
	{
		double	ans, y;

		y	= Math.abs( x );

		if ( Double.isNaN( x ) )
		{
			ans = Double.NaN;
		}
								// 1.82501e-08 = Math.sqrt(3.0*EPSILON_SMALL)

		else if ( y < 1.82501e-08 )
		{
			ans	= x;
		}
		else if ( y <= 1.0D )
		{
			ans	=
				x *
				( 1.0D +
					Polynomial.evaluateChebyschev(
						TANH_COEF , 2.0D * x * x - 1.0D ) );
		}
								// 7.977294885 = -0.5*Math.log(EPSILON_SMALL)

		else if ( y < 7.977294885 )
		{
			y	= Math.exp( y );
			ans	= sign( ( y - 1.0D / y ) / ( y + 1.0D / y ) , x );
		}
		else
		{
			ans = sign( 1.0D , x );
		}

		return ans;
	}

	/* Don't allow instantiation but allow overrides. */

	protected ArithUtils()
	{
	}
}

