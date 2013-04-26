import java.applet.* ;
import java.awt.* ;
import java.awt.Graphics ;

/****************************************************************
*                                                               *
* NAME:	LED (class)												*
* Implements an LED strip of numbers							*
*                                                               *
****************************************************************/

public class LED extends Canvas implements Runnable
	{
	private final static int	LS_BLINK_OFF	= 0,
								LS_BLINK_ON		= 1,
								LS_ON			= 2,
								LS_OFF			= 3 ;


	private	boolean	bDecimal,
					bLeadingZeroes ;

	private	Image	imageLEDStrip,
					imageLEDBlank,
					imageDigits ;

	private	int	x, y,
				nBlinkTimes,
				nState,
				nPreviousState,
				nBlinkRate = 50,
				nDigitWidth, nDigitHeight,
				nDigits ;

	private	double	dLEDValue ;

	private	Applet	appParent ;

	private	Thread	threadLED ;


    /****************************************************************
    *                                                               *
    * NAME:	LED (constructor)										*
    *                                                               *
    ****************************************************************/

	public LED(int x, int y, Applet appApplet, int nLEDW, boolean bLeading, boolean bDecPoint, Image imageLEDDigits, int nDigitW, int nDigitH)
		{
		nDigits = nLEDW ;
		imageDigits = imageLEDDigits ;
		bDecimal = bDecPoint ;
		bLeadingZeroes = bLeading ;
		nDigitWidth = nDigitW ;
		nDigitHeight = nDigitH ;
		dLEDValue = 0 ;
		nPreviousState = nState = LS_OFF ;
		appParent = appApplet ;
	    imageLEDStrip = appParent.createImage(nDigitWidth * nDigits, nDigitHeight) ;
	    imageLEDBlank = appParent.createImage(nDigitWidth * nDigits, nDigitHeight) ;
		BlankLED() ;												// create a blank LED
		BuildLED() ;												// build the regular LED
		move(x, y) ;												// move LED to correct position
		resize(nDigitWidth * nDigits, nDigitHeight) ;				// make LED correct size
		}


	/****************************************************************
    *                                                               *
    * NAME:	On (method)												*
	* Draw a lit LED with the default value							*
    *                                                               *
    ****************************************************************/

	public void On()
		{
		StopBlink() ;
		nState = LS_ON ;
		BuildLED() ;
		paint(getGraphics()) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	On (method)												*
	* Draw a lit LED with a newly supplied value					*
    *                                                               *
    ****************************************************************/

	public void On(int nValue)
		{
		On((double)nValue) ;
		}


	public void On(long lValue)
		{
		On((double)lValue) ;
		}


	public void On(double dValue)
		{
		StopBlink() ;
		dLEDValue = dValue ;
		On() ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	Off (method)											*
	* Draw a blank LED, all the lights off							*
    *                                                               *
    ****************************************************************/

	public void Off()
		{
		StopBlink() ;
		nState = LS_OFF ;
		paint(getGraphics()) ;
        }

		
	/****************************************************************
    *                                                               *
    * NAME:	BuildLED (method)										*
	* Write the LED digits into a bitmap so that it can be			*
	* displayed														*
    *                                                               *
    ****************************************************************/

    private void BuildLED()
        {
		boolean	bFirstDigit = false ;

		int	i,
			nQuotient ;

		long	lDividend,
				lDivisor ;

		Image	imageDigit ;

		Graphics	gDigit,
					gLEDStrip ;

    	gLEDStrip = imageLEDStrip.getGraphics() ;

		// create a bitmap for a single digit
		imageDigit = appParent.createImage(nDigitWidth, nDigitHeight) ;
		gDigit = imageDigit.getGraphics() ;

		if (bDecimal)
			lDividend = (long)(dLEDValue * 100.0) ;
		else
			lDividend = (long)dLEDValue ;

		lDivisor = (long)Math.pow(10, nDigits - 1) ;
		for (i = 0; i < nDigits; i++)
			{
			nQuotient = (int)(lDividend / lDivisor) % 10 ;
			lDivisor /= 10 ;
			if (nQuotient > 0)
				bFirstDigit=true ;

			if (bLeadingZeroes || bFirstDigit)
   				gDigit.drawImage(imageDigits, -(nQuotient * nDigitWidth), 0, null) ;
			else
				gDigit.drawImage(imageDigits, -(10 * nDigitWidth), 0, null) ;	// draw "off" digit

			gLEDStrip.drawImage(imageDigit, i * nDigitWidth, 0, null) ;
			}

		if (!bLeadingZeroes && !bFirstDigit)
			{
			gDigit.drawImage(imageDigits, 0, 0, null) ;				// draw "off" digit
			gLEDStrip.drawImage(imageDigit, (i - 1) * nDigitWidth, 0, null) ;
			}

		if (bDecimal)
			{
			gLEDStrip.setColor(new Color(220, 0, 0)) ;
			gLEDStrip.fillRect((nDigits - 2) * nDigitWidth - 2, nDigitHeight - 3, 2, 2) ;
			}

		// garbage collection
	   	gDigit.dispose() ;
	    imageDigit.flush() ;
		gLEDStrip.dispose() ;
		gDigit = null ;
		imageDigit = null ;
		gLEDStrip = null ;
        }


	/****************************************************************
    *                                                               *
    * NAME:	BlankLED (method)										*
	* Draw a blank LED, all the lights off							*
    *                                                               *
    ****************************************************************/

	private void BlankLED()
		{
		int	i ;

		Image	imageDigit ;

		Graphics	gDigit,
					gLEDBlank ;

    	gLEDBlank = imageLEDBlank.getGraphics() ;

		/* create a bitmap for a single digit */
		imageDigit = appParent.createImage(nDigitWidth, nDigitHeight) ;
		gDigit = imageDigit.getGraphics() ;

		/* draw "off" digit in all places */
		for (i = 0; i < nDigits; i++)
			{
			gDigit.drawImage(imageDigits, -(10 * nDigitWidth), 0, null) ;	// draw "off" digit
			gLEDBlank.drawImage(imageDigit, i * nDigitWidth, 0, null) ;
			}

	   	gDigit.dispose() ;
	    imageDigit.flush() ;
		gLEDBlank.dispose() ;
		gDigit = null ;
		imageDigit = null ;
		gLEDBlank = null ;
		}


	public void Set(int nValue)
		{
		Set((double)nValue) ;
		}

	public void Set(long lValue)
		{
		Set((double)lValue) ;
		}

	public void Set(double dValue)
		{
		dLEDValue = dValue ;
		BuildLED() ;
		if (nState == LS_ON)
			paint(getGraphics()) ;

		}


	public double Value()
		{
		return (dLEDValue) ;
		}


	private void StartBlink()
		{
		if (nState != LS_BLINK_OFF && nState != LS_BLINK_ON)
			nPreviousState = nState ;

	    threadLED = new Thread(this) ;
		threadLED.setPriority(Thread.MIN_PRIORITY) ;
		threadLED.start() ;
		}

	private void StopBlink()
		{
		if (threadLED != null)
			{
			nState = nPreviousState ;
			paint() ;
			threadLED.stop() ;
			threadLED = null ;
			}

		}


	public void Blink()
		{
		nBlinkTimes = -1 ;
		nBlinkRate = 100 ;
		StartBlink() ;
		}


	public void Blink(int nDelay, int nTimes)
		{
		if (nDelay >= 20)
			nBlinkRate = nDelay ;
		else
			nBlinkRate = 20 ;

		if (nTimes >= 1 && nTimes <= 1000)
			nBlinkTimes = nTimes ;
		else
			nBlinkTimes = -1 ;

		StartBlink() ;
		}


	public void BlinkRate(int nDelay)
		{
		if (nDelay >= 20)
			nBlinkRate = nDelay ;
		else
			nBlinkRate = 20 ;

		}


	/****************************************************************
    *                                                               *
    * NAME:	BlankLED (method)										*
	* Draw a blank LED, all the lights off							*
    *                                                               *
    ****************************************************************/

	public void run()
		{
		if ((nState == LS_ON) || (nState == LS_BLINK_ON))
			nState = LS_BLINK_OFF ;
		else
			nState = LS_BLINK_ON ;

		while ((nBlinkTimes > 0) || (nBlinkTimes == -1))
			{
			if (nState == LS_BLINK_ON)
				nState = LS_BLINK_OFF ;
			else if (nState == LS_BLINK_OFF)
				{
				nState = LS_BLINK_ON ;
				if (nBlinkTimes != -1)
					nBlinkTimes-- ;

				}

			try
				{
	   		    Thread.sleep(nBlinkRate) ;
				}

			catch (InterruptedException e)
				{
				}

			paint(getGraphics()) ;
			}

		StopBlink() ;
		}


/*
	public void start()
		{
		run() ;
		}

	public void stop()
		{
		}
*/
	public void paint()
		{
		paint(getGraphics()) ;
		}


	public void paint(Graphics g)
		{
		if (g != null)
			{
			if (nState == LS_ON || nState == LS_BLINK_ON)
				g.drawImage(imageLEDStrip, 0, 0, this) ;
			else if (nState == LS_OFF || nState == LS_BLINK_OFF)
				g.drawImage(imageLEDBlank, 0, 0, this) ;

			}

		}

	}
