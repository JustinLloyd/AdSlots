import java.applet.* ;
import java.awt.* ;
import java.awt.Toolkit ;
import java.net.* ;
import java.util.* ;
import java.awt.Graphics ;
import java.lang.Runtime ;


/****************************************************************
*                                                               *
* NAME: RippleCount (class)										*
*																*
* Implements a "ripple" count, incrementing the units until it	*
* equals the final units digit, then incrementing the tens		*
* until it equals the final tens value, and so on.				*
* It does the inverse when decrementing							*
*                                                               *
****************************************************************/

public class RippleCount
	{
	private long	lRippleCount,
					lEndCount ;


	/****************************************************************
    *                                                               *
    * NAME: RippleCount (constructor)								*
	* Initialises the RippleCount to the starting value and ending	*
	* value															*
    *                                                               *
    ****************************************************************/

	public RippleCount(double dStart, double dEnd)
		{
		lRippleCount = (long)(dStart * 1000.0) ;
		lEndCount = (long)(dEnd * 1000.0) ;
		}


	/****************************************************************
    *                                                               *
    * NAME: Step (method)											*
	* RET:	double	-- New value of counter							*
	*																*
	* Increments or decrements the counter by an appropriate amount	*
	* that induces the "ripple"										*
    *                                                               *
    ****************************************************************/

	public double Step()
		{
		if (lRippleCount < lEndCount)
			{
			if ((lRippleCount % 10) != (lEndCount % 10))
				lRippleCount++ ;
			else if ((lRippleCount / 10 % 10) != (lEndCount / 10 % 10))
				lRippleCount += 10 ;
			else if ((lRippleCount / 100 % 10) != (lEndCount / 100 % 10))
				lRippleCount += 100 ;
			else if ((lRippleCount / 1000 % 10) != (lEndCount / 1000 % 10))
				lRippleCount += 1000 ;
			else if ((lRippleCount / 10000 % 10) != (lEndCount / 10000 % 10))
				lRippleCount += 10000 ;
			else if ((lRippleCount / 100000 % 10) != (lEndCount / 100000 % 10))
				lRippleCount += 100000 ;
			else if ((lRippleCount / 1000000 % 10) != (lEndCount / 1000000 % 10))
				lRippleCount += 1000000 ;
			else if ((lRippleCount / 10000000 % 10) != (lEndCount / 10000000 % 10))
				lRippleCount += 10000000 ;
			else if ((lRippleCount / 100000000 % 10) != (lEndCount / 100000000 % 10))
				lRippleCount += 100000000 ;

			}
		else if (lRippleCount > lEndCount)
			{
			if ((lRippleCount / 100000000) != 0)
				lRippleCount -= 100000000 ;
			else if ((lRippleCount / 10000000) != 0)
				lRippleCount -= 10000000 ;
			else if ((lRippleCount / 1000000) != 0)
				lRippleCount -= 1000000 ;
			else if ((lRippleCount / 100000) != 0)
				lRippleCount -= 100000 ;
			else if ((lRippleCount / 10000) != 0)
				lRippleCount -= 10000 ;
			else if ((lRippleCount / 1000) != 0)
				lRippleCount -= 1000 ;
			else if ((lRippleCount / 100) != 0)
				lRippleCount -= 100 ;
			else if ((lRippleCount / 10) != 0)
				lRippleCount -= 10 ;
			else if ((lRippleCount % 10) != 0)
				lRippleCount-- ;

			}

		return ((double)(lRippleCount / 1000.0)) ;
		}

	
	/****************************************************************
    *                                                               *
    * NAME: Value (method)											*
	* RET:	double	--	Current value of the counter				*
	*																*
	* Retrieves the current value of the counter					*
    *                                                               *
    ****************************************************************/

	public double Value()
		{
		return ((double)(lRippleCount / 1000.0)) ;
		}


	/****************************************************************
    *                                                               *
    * NAME: Finished (method)										*
	* RET:	boolean	--	TRUE = Has the counter finish?				*
	*																*
	* Determines whether the counter has reached the end			*
    *                                                               *
    ****************************************************************/

	public boolean Finished()
		{
		return (lRippleCount == lEndCount) ;
		}

	}
