import java.applet.* ;
import java.awt.* ;
import java.awt.Toolkit ;
import java.net.* ;
import java.util.* ;
import java.awt.Graphics ;
import java.lang.Runtime ;


/*
class AuditEntry
	{
	public	int	nReel[] = new int[3] ;

	public	double	dCash,
					dWinnings ;

	}
*/


/****************************************************************
*                                                               *
* NAME:	Slot (class)											*
*                                                               *
****************************************************************/

public class Slot extends Applet implements Runnable
    {
	private final static String OHWOW1 = "Yeah! Yeah! So you're taking a hex dump",
								OHWOW2 = "or you've run this thing through a decompiler",
								OHWOW3 = "and now you're staring at the byte code/Java code.",
								OHWOW4 = "That's very big & clever of you, I _don't_ think",
								OHWOW5 = "I realise this slot machine is vunerable to hacking",
								OHWOW6 = "and reverse engineering, which is why we perform a",
								OHWOW7 = "batch security check on the server side of all entries",
								OHWOW8 = "before we pick any winners." ;

	private	final static String	VERSION = "Virtual Vegas AdSlot Slot Machine v1.11",
								AUTHOR = "Justin Lloyd",
								BUILD_DATE = "11th December, 1996",
								COPYRIGHT = "(c) Copyright 1995, Virtual Vegas" ;

	private final static int	MSG_GAMEOVER		= 0,
								MSG_MEDIA_ERROR		= 1,
								MSG_EMAIL_ERROR		= 2,
								MSG_WAIT			= 3,
								MSG_INTERNAL_ERROR	= 4,
								MSG_OUT_OF_MONEY	= 5,
								MSG_EXPIRED			= 6 ;

	private final static int	GS_LOADING			= 0x0001,
								GS_WAITING			= 0x0002,
								GS_GAMEOVER			= 0x0004,
								GS_SPINUP			= 0x0020,
								GS_SPINNING			= 0x0040,
								GS_PAYOUT			= 0x0080,
								GS_LOSE				= 0x0100,
								GS_CASHOUT			= 0x0200,
								GS_NETWORK			= 0x0400,
								GS_DETAILS			= 0x1000,
								GS_ERROR			= 0x2000 ;
	
	private final static long	GB_NONE			= 0x0000,
								GB_COINS		= 0x0002,
								GB_REEL1		= 0x0004,
								GB_REEL2		= 0x0008,
								GB_REEL3		= 0x0010,
								GB_WINLINE		= 0x0020,
								GB_GAMECOST		= 0x0040,
								GB_BACKGROUND	= 0x0400,
								GB_ALLBITS		= 0xFFFF ;

	// media ID #'s for various graphical elements to be downloaded
	private final static int	MID_IMG_BITMAP			= 1 ;

	private final static int	DIGITS_CASH = 4,					// default # digits for cash LED
								DIGITS_COINS = 1,					// default # digits for coin deposit LED
								DIGITS_WINNINGS = 4,
								DEFAULT_REEL_V = 10 ;				// default reel velocity

	private	final	static	double	MAXIMUM_PAYOUT = 187.5 ;

	private	final	static	String	sEMailHost = "artistdirect.com",
									sEMailBase = "artistdirect.com",
									sEMailName1 = "slot1",
									sEMailName2 = "slot2",
									sEMailName3 = "slot3",
									sEMailName4 = "slot4" ;

    private final static long UPDATERATE = 50 ;						// per frame pause (in milliseconds)

	private	final	static	boolean	DEBUG = false ;

	private	String	sPlayerName = "",
					sPlayerEMail ="" ;

	private	Thread  threadMachine = null ;

    private	boolean bFastGame = false,
//					bDownloadComplete = false,
					bTestGame = false,								// can this machine be put into test mode?
					bAutoPlay = false,								// are we in auto-play mode?
					bWinLineOverlay = true,							// show a "painted" win-line
					bPaint = true ;

	private	Image   imageBitmap,
					imageBackground,
					imageReelStrip,									// strip of symbols
					imageLEDDigits,
					imageMessage,
					imageReelWindow,
					imageButtonCashOutOff,
					imageButtonCashOutOn,
					imageButtonCashOutDown,
					imageButtonSpinOff,
					imageButtonSpinOn,
					imageButtonSpinDown,
					imageButtonBetMaxOff,
					imageButtonBetMaxOn,
					imageButtonBetMaxDown,
					imageButtonBetOneOff,
					imageButtonBetOneOn,
					imageButtonBetOneDown ;

	private	Graphics    gApplet,
						gReelWindow ;								// window that symbols appear in

	private	long	lCleanUpCalls = 0,
					lDownloadTotalTime,
					lDownloadStartTime,
					lGameStartTime,
					lGameTotalTime,
					lGraphicBits = 0,								// which elements to redraw
					lFrame = 0 ;									// # frames displayed so far

	private	int nAuditEntries,
				nAuditReel[][] = new int [2048][3],
				nAuditCoins[] = new int [2048],
				nAuditWin[] = new int [2048],
				nAppletWidth,
				nAppletHeight,
				nGameState,											// slot machine state machine
				nReelBaseV = DEFAULT_REEL_V,						// base reel velocity
				nReelStepper[] = new int[3],						// position of reel stepper motor
				nReelSteps[] = new int[3],							// steps to required symbol
				nReelV[] = new int[3],								// current reel velocity
				nNoCashFrame = 0,
				nNoCashFrames = 20,									// blinking "out of money" animation
				nSymbolWidth = 64,									// width of individual symbol (in pixels)
				nSymbolHeight = 64,									// height of individual symbol (in pixels)
				nReelLength,										// length of all symbols (in pixels)
    			nReelPosition[] = { 0, 82, 160 },
				nReelWindowX = 72,
				nReelWindowY = 295,
				nReelWindowWidth = 64 * 3 + 2 * 16,
				nReelWindowHeight = 65,
				nWinLineX = 60,
    			nWinLineY = nReelWindowY + nReelWindowHeight / 2,
				nWinLineLength = 64 * 3 + 2 * 16 + 15,
    			nLEDCreditsX = 320,
    			nLEDCreditsY = 318,
				nLEDCoinsX = 370,
				nLEDCoinsY = 350,
				nLEDWinningsX = 320,
				nLEDWinningsY = 294,
				nLEDCashDigits = DIGITS_CASH,
				nLEDCoinsDigits = DIGITS_COINS,
				nLEDWinningsDigits = DIGITS_WINNINGS,
				nLEDDigitWidth = 14,
				nLEDDigitHeight = 20 ;

	private	AudioClip	audioNoMoney,
						audioCashOut,
						audioSpin,
						audioWin, audioBigWin,
						audioLose,
						audioPullHandle,
						audioCoin, 
						audioCoins,
						audioNocoin ;

	private	MediaTracker	mtDownload ;

	private	Rectangle   rectAutoPlay=new Rectangle(0, 0, 200, 10),
						rectHandle=new Rectangle(400, 70, 60, 100) ;

	private	GraphicButton	gbSpin,
							gbCashOut,
							gbBetMax,
							gbBetOne ;

	private	RippleCount	rippleCash,
						rippleWinnings ;

	private	LED	ledCash,
				ledCoins,
				ledWinnings ;

	private	MachineLogic	SlotMachine ;


	// player details message box
	private	Label	label1,
					label2,
					label3,
					label4,
					label5,
					label6,
					label7,
					label8 ;

    private	TextField	editEMail,
						editName ;

    private	Button	buttonPlayAgain,
					buttonSend,
					buttonNoThanks ;


	/****************************************************************
    *                                                               *
    * NAME:	init (event)											*
    *                                                               *
    ****************************************************************/

    public void init()
        {
		Graphics	gMessage ;

        int i ;

//		bTestGame = true ;
		gApplet = getGraphics() ;
		nAppletWidth = size().width ;
		nAppletHeight = size().height ;
		nGameState = GS_LOADING ;
        setFont(new Font("Helvetica", Font.PLAIN, 12)) ;
        setBackground(Color.black) ;
        setForeground(Color.white) ;
		ClearScreen() ;
		CreateMachine() ;
		nReelLength = nSymbolHeight * (int)(SlotMachine.SYMBOLS) ;
//	    GetParams() ;
    	RetrieveMedia() ;

    	imageMessage = createImage(size().width, 200) ;
		gMessage =  imageMessage.getGraphics() ;
    	gMessage.setColor(Color.black) ;
	    gMessage.fillRect(0, 0, imageMessage.getWidth(this), imageMessage.getHeight(this)) ;
		gMessage.dispose() ;


		nReelStepper[0] = nReelStepper[1] = nReelStepper[2] = -nReelLength ;
    	requestFocus() ;
	    show() ;
		ClearScreen() ;
        }


	/****************************************************************
    *                                                               *
    * NAME:	imageUpdate (event)										*
    *                                                               *
    ****************************************************************/

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
        {
		Download() ;
//		if (img == imageBackground)
//			repaint(((infoflags & (FRAMEBITS | ALLBITS)) != 0) ? 0 : UPDATERATE, x, y, width, height) ;

		return (true) ;
		}
	

	public void paintAll(Graphics g)
        {
	    paint(g) ;
        }


	/****************************************************************
    *                                                               *
    * NAME:	start (event)											*
    *                                                               *
    ****************************************************************/

    public void start()
        {
        if (threadMachine == null)
            threadMachine = new Thread(this) ;

        threadMachine.start() ;
        }


	/****************************************************************
    *                                                               *
    * NAME:	stop (event)											*
    *                                                               *
    ****************************************************************/

    public void stop()
        {
		imageBitmap = null ;
		imageBackground = null ;
		imageReelStrip = null ;
	    imageLEDDigits = null ;
		imageMessage = null ;
		imageReelWindow = null ;
		imageButtonCashOutOff = null ;
		imageButtonCashOutOn = null ;
		imageButtonCashOutDown = null ;
		imageButtonSpinOff = null ;
		imageButtonSpinOn = null ;
		imageButtonSpinDown = null ;
		imageButtonBetMaxOff = null ;
		imageButtonBetMaxOn = null ;
		imageButtonBetMaxDown = null ;
		imageButtonBetOneOff = null ;
		imageButtonBetOneOn = null ;
		imageButtonBetOneDown = null ;

        if (threadMachine != null)
            {
            threadMachine.stop() ;
            threadMachine = null ;
            }


        }


	/****************************************************************
    *                                                               *
    * NAME:	run (event)												*
    *                                                               *
    ****************************************************************/

    public void run()
        {
//  	long tnew, tdiff, told ;

//	    Date date ;

    	Thread.currentThread().setPriority(Thread.MIN_PRIORITY) ;
		try
			{
			mtDownload.waitForAll() ;
			}

		catch (InterruptedException e)
			{
			Message(MSG_MEDIA_ERROR, true) ;
			nGameState = GS_ERROR ;
			}

		if (mtDownload.isErrorAny())
			{
			Message(MSG_MEDIA_ERROR, true) ;
			nGameState = GS_ERROR ;

			return ;
			}
//		else
//			bDownloadComplete = true ;

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY) ;
		DownloadComplete() ;
//			tnew=System.currentTimeMillis() ;
		lGameStartTime = System.currentTimeMillis() ;
		while (true)
			{
//		        told = System.currentTimeMillis() ;
	        UpdateGame() ;
				{
       			try
					{
					if (nGameState != GS_SPINNING)
//					    Thread.sleep(1) ;
//					else
					    Thread.sleep(50) ;

					}

			    catch (InterruptedException e)
					{
					}

				}

/*
			try
				{
       			Thread.sleep(10) ;
				}

			catch (InterruptedException e)
				{
				}
*/

/*			tnew = System.currentTimeMillis() ;
			tdiff = 5 ;
			if (tdiff > 0)
				{
	        	try
        			Thread.sleep(tdiff) ;

        		catch (InterruptedException e) ;
        		}
*/
			}

        }


	/****************************************************************
    *                                                               *
    * NAME:	update (event)											*
    *                                                               *
    ****************************************************************/

    public void update(Graphics g)
        {
		if (PossibleState(nGameState, (GS_GAMEOVER | GS_LOADING | GS_ERROR | GS_NETWORK)))
			g.drawImage(imageMessage, 0, size().height / 2 - imageMessage.getHeight(this) / 2, null) ;
		else if (!PossibleState(nGameState, GS_DETAILS))
			{
			// redraw parts of graphic image requested
	        if ((lGraphicBits & GB_BACKGROUND) == GB_BACKGROUND)
    			g.drawImage(imageBackground, 0, 0, null) ;

			if ((lGraphicBits & (GB_REEL1 | GB_REEL2 | GB_REEL3 | GB_WINLINE)) != 0)
				{
				if ((lGraphicBits & GB_REEL1) == GB_REEL1)
    				gReelWindow.drawImage(imageReelStrip, nReelPosition[0], nReelStepper[0], null) ;

				if ((lGraphicBits & GB_REEL2) == GB_REEL2)
				    gReelWindow.drawImage(imageReelStrip, nReelPosition[1], nReelStepper[1], null) ;

				if ((lGraphicBits & GB_REEL3) == GB_REEL3)
					gReelWindow.drawImage(imageReelStrip, nReelPosition[2], nReelStepper[2], null) ;

				if ((lGraphicBits & GB_WINLINE) == GB_WINLINE)
				    {
				    gReelWindow.setColor(Color.red) ;
					gReelWindow.drawLine(nWinLineX, nWinLineY, nWinLineX + nWinLineLength, nWinLineY) ;
			    	}

				g.drawImage(imageReelWindow, nReelWindowX, nReelWindowY, this) ;
				}


			}

		lGraphicBits = GB_NONE ;
        }


	/****************************************************************
    *                                                               *
    * NAME:	paint (event)											*
    *                                                               *
    ****************************************************************/

    public void paint(Graphics g)
        {
		lGraphicBits = GB_ALLBITS ;
	    update(g) ;
        }


	/****************************************************************
    *                                                               *
    * NAME:	handleEvent (event)										*
    *                                                               *
    ****************************************************************/

    public boolean handleEvent(Event event)
    	{
        if (event.id == Event.ACTION_EVENT)
			{
			if (event.target == buttonSend)
				{
				clickedbuttonSend() ;

	            return (true) ;
				}
			else if (event.target == buttonNoThanks)
				{
				clickedbuttonNoThanks() ;

				return (true) ;
				}
			else if (event.target == buttonPlayAgain)
				{
				clickedbuttonPlayAgain() ;

				return (true) ;
				}

			}


        return (super.handleEvent(event)) ;
	    }


	/****************************************************************
    *                                                               *
    * NAME:	mouseEnter (event)										*
    *                                                               *
    ****************************************************************/

	public boolean mouseEnter(Event evt, int x, int y)
		{
		this.showStatus(VERSION) ;
		return (true) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	mouseDown (event)										*
    *                                                               *
    ****************************************************************/

	public boolean mouseDown(Event evt, int x, int y)
	    {
		if (bTestGame)
			{
			bAutoPlay = rectAutoPlay.inside(x, y) ;
			if (bAutoPlay)
				System.out.println("Autoplay") ;

			}

		if (nGameState == GS_WAITING)
			{
			// has the handle been pulled?
			if ((rectHandle.inside(x, y)))
				{
				if (SlotMachine.GetCoinsDeposited() == 0)
					{
					if (DepositCoin())
						PullHandle() ;

					}
				else
					PullHandle() ;

				return (true) ;
				}

			}

		return (false) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	action (event)											*
    *                                                               *
    ****************************************************************/
    public boolean action(Event event, Object oWhat)
    	{
		if (nGameState == GS_WAITING)
			{
			// has the handle been pulled?
			if (oWhat == gbSpin)
				{
				if (SlotMachine.GetCoinsDeposited() == 0)
					{
					if (DepositCoin())
						PullHandle() ;

					}
				else
					PullHandle() ;

				}
			else if (oWhat == gbCashOut)
				CashOut() ;
			else if (oWhat == gbBetOne)
				DepositCoin() ;
			else if (oWhat == gbBetMax)
				if (DepositMaxCoin() != -1)
					PullHandle() ;

			return  (true) ;
			}

        return (false) ;
	    }


	/****************************************************************
    *                                                               *
    * NAME:	CreateMachine (method)									*
    *                                                               *
    ****************************************************************/

	private void CreateMachine()
		{
		SlotMachine = new MachineLogic() ;							// create a new slot machine
		SlotMachine.PowerON() ;										// switch machine on
		SlotMachine.SetDipSwitch_DeductBefore(false) ;
		SlotMachine.SetDipSwitch_ClearCoins(false) ;
		SlotMachine.SetRotarySwitch_GameCost() ;					// use default switch setting
		SlotMachine.SetRotarySwitch_PayoutRate(4) ;	// use supplied switch setting
		SlotMachine.SetIndicator_PotLevel(40.0 + (Math.random() * 20.0)) ;	// use supplied switch setting
		SlotMachine.SetRotarySwitch_PotDrop(2) ;
		SlotMachine.SetCash(50.0) ;
		nAuditEntries = 0 ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	GetParams (method)										*
    *                                                               *
    ****************************************************************/
/*
    public void GetParams()
        {
	    String	gparam ;

    	StringTokenizer	stToken ;

    	int	l, t, r, b ;

		if (bUseHTMLParams)
			{
			if ((gparam = getParameter("FASTGAME")) != null)
				bFastGame = true ;

			// the following parameter should be added to the encode/decode class
			if ((gparam = getParameter("PAYOUT")) != null)
				{
				System.out.print("Payout % -- Previous = " + SlotMachine.dPayoutRate + "% ") ;
				SlotMachine.SetRotarySwitch_PayoutRate(Integer.parseInt(gparam)) ;	// use supplied switch setting
				System.out.println(" New = " + SlotMachine.dPayoutRate + "% Option = " + Integer.parseInt(gparam)) ;
				}
			else
				SlotMachine.SetRotarySwitch_PayoutRate() ;			// use default switch setting

			// the following parameter should be added to the encode/decode class
			if ((gparam=getParameter("POTLEVEL"))!=null)
				{
				System.out.print("Pot Level -- Previous = $" + SlotMachine.dPot) ;
				SlotMachine.SetIndicator_PotLevel(Double.valueOf(gparam).doubleValue()) ;	// use supplied switch setting
				System.out.println(" New = $" + SlotMachine.dPot + " Option = " + Double.valueOf(gparam).doubleValue()) ;
				}
			else
				SlotMachine.SetIndicator_PotLevel() ;				// use default switch setting

			if ((gparam=getParameter("POTTABLE"))!=null)
				{
				System.out.print("Pot Table -- Previous = " + SlotMachine.nPotDropTable) ;
				SlotMachine.SetRotarySwitch_PotDrop(Integer.parseInt(gparam)) ;	// use supplied switch setting
				System.out.println("New = " + SlotMachine.nPotDropTable + " Option = " + Integer.parseInt(gparam)) ;
				}
			else
				SlotMachine.SetRotarySwitch_PotDrop() ;					// use default switch setting

			}

		}
*/
	/****************************************************************
    *                                                               *
    * NAME:	PossibleState (method)									*
	* I/P:	nState	-- state machine to consider					*
	*		nMask	-- possible states being looked for				*
	* RET:	boolean	-- TRUE=state is in mask						*
	*																*
	* Consider whether the given state machine is in one of 'n'		*
	* possible states												*
    *                                                               *
    ****************************************************************/

	boolean PossibleState(int nState, int nMask)
		{
		return ((nState & nMask) != 0) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	ReelShowingSymbol (method)								*
	* I/P:	nStepper	-- position of stepper motor				*
	* RET:	Wheter symbol is completley in window					*
    *                                                               *
    * Calculates number of steps to move to completely move the		*
	* symbol into the window										*
    *                                                               *
    ****************************************************************/

	int ReelShowingSymbol(int nStepper)
		{
		return (nStepper % nSymbolHeight) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	ReelSymbol (method)										*
	* I/P:	nStepper	-- position of stepper motor				*
	* RET:	Symbol currently showing in window						*
    *                                                               *
    * Calculates which symbol is currently showing in the window	*
	* based on the stepper motor value								*
    *                                                               *
    ****************************************************************/

	int ReelSymbol(int nStepper)
		{
		return (nStepper/nSymbolHeight) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	RetrieveMedia (method)									*
    *                                                               *
    ****************************************************************/

	public void RetrieveMedia()
        {
    	String	sAudioPullHandle,
				sAudioSpin,
				sAudioPayout,
				sAudioBetOne,
				sAudioBetMax,
				sAudioLose,
				sAudioBuzzer,
				sImageBitmap ;

	    StringTokenizer stToken ;

		String	sImageNames[] = { "all.gif", "All.GIF", "ALL.GIF", "All.gif", "All.Gif" } ;

		// set default parameter list of URL's
//		sImageBitmap = "All.GIF" ;
		
		sAudioSpin = "Spin.au" ;
		sAudioPayout = "Payout.au" ;
		sAudioLose = "Lose.au" ;
		sAudioPullHandle = "Pull.au" ;
		sAudioBetOne = "BetOne.au" ;
		sAudioBetMax = "BetMax.au" ;
		sAudioBuzzer = "Buzzer.au" ;

		lDownloadStartTime = System.currentTimeMillis() ;
		lDownloadTotalTime = 0 ;
		mtDownload=new MediaTracker(this) ;							// create a media tracker
/*
		if (bUseHTMLParams)
			{
			// audio parameters
			if (getParameter("AUDIOSPIN") != null)
				sAudioSpin = getParameter("AUDIOSPIN") ;

			if (getParameter("AUDIOPAYOUT") != null)
				sAudioPayout = getParameter("AUDIOPAYOUT") ;

			if (getParameter("AUDIOLOSE") != null)
				sAudioLose = getParameter("AUDIOLOSE") ;

			if (getParameter("AUDIOPULL") != null)
				sAudioPullHandle = getParameter("AUDIOPULL") ;

			if (getParameter("AUDIOBETONE") != null)
				sAudioBetOne = getParameter("AUDIOBETONE") ;

			if (getParameter("AUDIOBETMAX") != null)
				sAudioBetMax = getParameter("AUDIOBETMAX") ;

			if (getParameter("AUDIOBUZZER") != null)
				sAudioBuzzer = getParameter("AUDIOBUZZER") ;

			}
*/
		// retrieve audio media
   		audioSpin = getAudioClip(getCodeBase(), sAudioSpin) ;
    	audioWin = getAudioClip(getCodeBase(), sAudioPayout) ;
   		audioLose = getAudioClip(getCodeBase(), sAudioLose) ;
	    audioPullHandle = getAudioClip(getCodeBase(), sAudioPullHandle) ;
		audioCoin = getAudioClip(getCodeBase(), sAudioBetOne) ;
		audioCoins = getAudioClip(getCodeBase(), sAudioBetMax) ;
		audioNocoin = getAudioClip(getCodeBase(), sAudioBuzzer) ;

   		Thread.currentThread().setPriority(Thread.MIN_PRIORITY) ;

		// test for existence of media
		boolean bDone = false ;
		for (int nIndex = 0; nIndex < 5 && !bDone; nIndex++)
			{
			MediaTracker mtTestImage ;

			mtTestImage = new MediaTracker(this) ;					// create a media tracker

			imageBitmap = getImage(getCodeBase(), sImageNames[nIndex]) ;
			mtTestImage.addImage(imageBitmap, MID_IMG_BITMAP) ;
			prepareImage(imageBitmap, this) ;
    		mtTestImage.checkAll(true) ;
			try
				{
				Thread.sleep(100) ;
				}

			catch (InterruptedException e)
				{
				}

			if (!mtTestImage.isErrorAny())
				bDone = true ;

			}

		// whether it's success or failure, go ahead and add to the media tracker
		mtDownload.addImage(imageBitmap, MID_IMG_BITMAP) ;
    	mtDownload.checkAll(true) ;
        }


	/****************************************************************
    *                                                               *
    * NAME:	CashOut (method)										*
    *                                                               *
    ****************************************************************/

	private void CashOut()
		{
		DisableButtons() ;
		ledCoins.Off() ;
		ledWinnings.Off() ;
		// play cash out sound
		SlotMachine.Collect() ;
		rippleCash = new RippleCount(SlotMachine.GetCoins(), 0.0) ;
		nGameState = GS_CASHOUT ;
		}



	/****************************************************************
    *                                                               *
    * NAME:	CashingOut (method)										*
    *                                                               *
    ****************************************************************/

	private void CashingOut()
		{
		// play payout audio [waiting for media]
		// play payout animation [waiting for media]
		
		ledCash.Set(rippleCash.Step()) ;
		if (rippleCash.Finished())
			{
//			removeAll() ;
//			ClearScreen() ;

			lGraphicBits |= GB_ALLBITS ;
			CashedOut() ;
			}

		}


	/****************************************************************
    *                                                               *
    * NAME:	CashedOut (method)										*
    *                                                               *
    ****************************************************************/

	private void CashedOut()
		{
		lGameTotalTime = (System.currentTimeMillis() - lGameStartTime) / (long)1000 ;
		if (SlotMachine.GetCoins() > 250)
			Details() ;
		else
			GameOver() ;

		}


	/****************************************************************
    *                                                               *
    * NAME:	DepositCoin (method)									*
	* RET:	false	-- not enough cash to deposit a coin			*
	*		true	-- coin was deposited ok						*
    *                                                               *
    ****************************************************************/

	private boolean DepositCoin()
		{
		if (!SlotMachine.DepositCoin())
			{
			OutOfCash() ;
			nGameState = GS_WAITING ;
//			return (false) ;
			return (true) ;
			}
		else
			{
			if (!bAutoPlay)
				audioCoin.play() ;

			ledCash.Set(SlotMachine.GetCoins()) ;
			ledCoins.Set(SlotMachine.GetCoinsDeposited()) ;
			}

		nGameState = GS_WAITING ;

		return (true) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	DepositMaxCoin (method)									*
	* RET:	-1	-- not enough cash to deposit maximum coins			*
	*		0	-- already deposited maximum # coins				*
	*		1	-- maximum # coins deposited ok						*
    *                                                               *
    ****************************************************************/

	public int DepositMaxCoin()
		{
		int nDepositOK = SlotMachine.DepositMaxCoin() ;

		if (nDepositOK == -1)
			OutOfCash() ;
		else if (nDepositOK == 1)
			{
			if (!bAutoPlay)
				audioCoin.play() ;

			ledCash.Set(SlotMachine.GetCoins()) ;
			ledCoins.Set(SlotMachine.GetCoinsDeposited()) ;
			}

		nGameState = GS_WAITING ;

		return (nDepositOK) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	OutOfCash (method)										*
    *                                                               *
    ****************************************************************/

	private void OutOfCash()
		{
		if (!bAutoPlay)
			audioNocoin.play() ;

		ledCash.Blink(50, 10) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	DisableButtons (method)									*
    *                                                               *
    ****************************************************************/

	private void DisableButtons()
		{
		gbSpin.disable() ;
		gbCashOut.disable() ;
		gbBetMax.disable() ;
		gbBetOne.disable() ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	EnableButtons (method)									*
    *                                                               *
    ****************************************************************/

	private void EnableButtons()
		{
		gbSpin.enable() ;
		gbCashOut.enable() ;
		gbBetMax.enable() ;
		gbBetOne.enable() ;
		}

	/****************************************************************
    *                                                               *
    * NAME:	PayoutCash (method)										*
	*																*
	* Payout the winnings, counting the coins on the LEDs			*
    *                                                               *
    ****************************************************************/

    private void PayoutCash()
        {
        int	i ;

		if (DEBUG)
			System.out.println("PayoutCash") ;

		// play payout audio [waiting for media]
		// play payout animation [waiting for media]

		ledWinnings.Set(rippleWinnings.Step()) ;
		if (rippleWinnings.Finished())
			{
			ledWinnings.Blink(50, 10) ;
			ledCash.Set(rippleCash.Step()) ;
			if (rippleCash.Finished())
				{
				if (!bAutoPlay)
					if (SlotMachine.GetWinnings() >= 100)
						audioWin.play() ;
					else
						audioWin.play() ;

				ledWinnings.On(SlotMachine.GetCoinsWon()) ;
					{
					EnableButtons() ;
					nGameState = GS_WAITING ;
					}

				}

			}

//		Runtime.getRuntime().gc() ;									// garbage collection
        }


	/****************************************************************
    *                                                               *
    * NAME:	UpdateGame (method)										*
	*																*
	* Update various graphical elements, ensure the reels are in	*
	* the correct position, play various sounds and alter the game	*
	* state to what we've got to do next							*
    *                                                               *
    ****************************************************************/

    private void UpdateGame()
        {
		lFrame++ ;
		// because of the asynchronous Image copy in Java
		// we need to wait a little while before disposing
		// of the graphics data
/*
		if (lFrame == 10000)
			{
			imageButtonSpinUp.flush() ;
			imageButtonSpinDown.flush() ;
			imageButtonCashOutUp.flush() ;
			imageButtonCashOutDown.flush() ;
			imageLEDDigits.flush() ;
			gLoading.dispose() ;
			imageLoading.flush() ;
			}

*/

/*
		if (nGameState == GS_WAITING && bAutoPlay)
			{
			if (DepositCoin())
				PullHandle() ;

			}
*/
		if (nGameState == GS_WAITING && SlotMachine.dCashPaid >= MAXIMUM_PAYOUT)
			CashOut() ;

		GameState() ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	DownloadComplete (method)								*
	*																*
	* Track the downloading of the media, once completed, do some	*
	* secondary initialisation and hand off to the main game loop	*
    *                                                               *
    ****************************************************************/

	private void DownloadComplete()
		{
		lDownloadTotalTime = System.currentTimeMillis() - lDownloadStartTime ;
//		System.out.println("Download complete") ;
		imageBackground = createImage(450, 440) ;
		imageBackground.getGraphics().drawImage(imageBitmap, 0, 0, null) ;

		imageReelStrip = createImage(64, 704) ;
		imageReelStrip.getGraphics().drawImage(imageBitmap, -451, -1, null) ;

		imageLEDDigits = createImage(156, 20) ;
		imageLEDDigits.getGraphics().drawImage(imageBitmap, 0, -564, null) ;

		imageButtonCashOutOff = createImage(70, 40) ;
		imageButtonCashOutOff.getGraphics().drawImage(imageBitmap, 0, -441, null) ;

		imageButtonCashOutOn = createImage(70, 40) ;
		imageButtonCashOutOn.getGraphics().drawImage(imageBitmap, 0, -482, null) ;

		imageButtonCashOutDown = createImage(70, 40) ;
		imageButtonCashOutDown.getGraphics().drawImage(imageBitmap, 0, -523, null) ;

		imageButtonBetMaxOff = createImage(68, 40) ;
		imageButtonBetMaxOff.getGraphics().drawImage(imageBitmap, -71, -441, null) ;

		imageButtonBetMaxOn = createImage(68, 40) ;
		imageButtonBetMaxOn.getGraphics().drawImage(imageBitmap, -71, -482, null) ;

		imageButtonBetMaxDown = createImage(68, 40) ;
		imageButtonBetMaxDown.getGraphics().drawImage(imageBitmap, -71, -523, null) ;

		imageButtonBetOneOff = createImage(70, 40) ;
		imageButtonBetOneOff.getGraphics().drawImage(imageBitmap, -140, -441, null) ;

		imageButtonBetOneOn = createImage(70, 40) ;
		imageButtonBetOneOn.getGraphics().drawImage(imageBitmap, -140, -482, null) ;

		imageButtonBetOneDown = createImage(70, 40) ;
		imageButtonBetOneDown.getGraphics().drawImage(imageBitmap, -140, -523, null) ;

		imageButtonSpinOff = createImage(62, 40) ;
		imageButtonSpinOff.getGraphics().drawImage(imageBitmap, -211, -441, null) ;

		imageButtonSpinOn = createImage(62, 40) ;
		imageButtonSpinOn.getGraphics().drawImage(imageBitmap, -211, -482, null) ;

		imageButtonSpinDown = createImage(62, 40) ;
		imageButtonSpinDown.getGraphics().drawImage(imageBitmap, -211, -523, null) ;

		imageReelWindow = createImage(nReelWindowWidth, nReelWindowHeight) ;
	    gReelWindow = imageReelWindow.getGraphics() ;
		gReelWindow.drawImage(imageBackground, -nReelWindowX, -nReelWindowY, null) ;

		ledCash = new LED(nLEDCreditsX, nLEDCreditsY, this, nLEDCashDigits, false, false, imageLEDDigits, nLEDDigitWidth ,nLEDDigitHeight) ;
		ledCoins = new LED(nLEDCoinsX, nLEDCoinsY, this, nLEDCoinsDigits, false, false, imageLEDDigits, nLEDDigitWidth ,nLEDDigitHeight) ;
		ledWinnings = new LED(nLEDWinningsX, nLEDWinningsY, this, nLEDWinningsDigits, false, false, imageLEDDigits, nLEDDigitWidth ,nLEDDigitHeight) ;
		gbCashOut = new GraphicButton(19, 390, imageButtonCashOutOff, imageButtonCashOutOn, imageButtonCashOutDown) ;
		gbBetMax = new GraphicButton(90, 390, imageButtonBetMaxOff, imageButtonBetMaxOn, imageButtonBetMaxDown) ;
		gbBetOne = new GraphicButton(157, 390, imageButtonBetOneOff, imageButtonBetOneOn, imageButtonBetOneDown) ;
		gbSpin = new GraphicButton(227, 390, imageButtonSpinOff, imageButtonSpinOn, imageButtonSpinDown) ;
		// flush out unused graphic data
		DisplayInterface() ;
		nGameState = GS_WAITING ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	Download (method)										*
    *                                                               *
    ****************************************************************/

	public void Download()
		{
		int	nPercentageDownloaded ;

		long	lEstimatedTime ;

//		System.out.println(lFrame) ;
		lFrame++ ;

		if (!mtDownload.checkID(MID_IMG_BITMAP) && (lFrame % 15) == 0)	// still downloading?
			{
			Graphics gMessage = imageMessage.getGraphics() ;
			gMessage.setFont(new Font("Arial", 0, 12)) ;
	        gMessage.setColor(Color.black) ;
   		    gMessage.fillRect(0, 108, 200, 50) ;
	        gMessage.setColor(Color.white) ;
			gMessage.drawString(VERSION, 0, 16) ;
			gMessage.drawString(COPYRIGHT, 0, 32) ;
			gMessage.drawString("Author: " + AUTHOR, 0, 48) ;
			gMessage.drawString("Build: " + BUILD_DATE, 0, 64) ;
			nPercentageDownloaded = (int)((double)(((double)lFrame) / imageBitmap.getHeight(null)) * 100.0) ;
			gMessage.drawString("Graphic data loaded " + nPercentageDownloaded + "%", 12, 128) ;
			lEstimatedTime = (System.currentTimeMillis() - lDownloadStartTime) / nPercentageDownloaded * (100 - nPercentageDownloaded) ;
			gMessage.drawString("Estimated time remaining: " + (int)(lEstimatedTime / 600000) + (int)(lEstimatedTime / 60000 % 10) + ":"  + (int)(lEstimatedTime / 10000 % 10) + (int)(lEstimatedTime / 1000 % 10), 0, 144) ;
			gMessage.dispose() ;
			update(getGraphics()) ;
			}

		}


	/****************************************************************
    *                                                               *
    * NAME:	CleanUp (method)										*
    *                                                               *
    ****************************************************************/

	private void CleanUp()
		{
//		static long lCleanUpCalls = 0 ;

		if (DEBUG)
			System.out.println("CleanUp") ;

		lCleanUpCalls++ ;
		Runtime.getRuntime().gc() ;
//		System.out.println("Cleanup called " + lCleanUpCalls) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	DisplayInterface (method)								*
    *                                                               *
    ****************************************************************/

	private void DisplayInterface()
		{
		removeAll() ;
		setLayout(null) ;
		// LED's
		add(ledCash) ;
		ledCash.On(SlotMachine.GetCoins()) ;
		add(ledCoins) ;
		ledCoins.On() ;
		add(ledWinnings) ;
		ledWinnings.Off() ;

		// buttons
		add(gbCashOut) ;
		gbCashOut.show() ;
		add(gbBetMax) ;
		gbBetMax.show() ;
		add(gbBetOne) ;
		gbBetOne.show() ;
		add(gbSpin) ;
		gbSpin.show() ;
		lGraphicBits = GB_ALLBITS ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	GameState (method)										*
    *                                                               *
    ****************************************************************/

	private void GameState()
		{
        int	i ;

		switch (nGameState)
			{
			case GS_GAMEOVER :
				// bump to a new URL?
				break ;

			case GS_WAITING :
				if ((lFrame % 25) == 0)
					{
					CleanUp() ;
//					System.out.println("Free Memory " + Runtime.getRuntime().freeMemory()) ;
					}

				break ;

			case GS_SPINNING :
				{
				boolean	bFinished = true ;

				for (i = 0; i < 3; i++)
					{
					if (nReelSteps[i] > 0)
						{
						lGraphicBits |= ((GB_REEL1 << i) | GB_WINLINE) ;
						if (nReelV[i] <= nReelSteps[i])
							nReelStepper[i] += nReelV[i] ;
						else
							nReelStepper[i] += nReelSteps[i] ;

						nReelSteps[i] -= nReelV[i] ;
						if (nReelStepper[i] >= 0)
							nReelStepper[i] = -(nReelLength - nReelStepper[i]) ;

						bFinished = false ;
						}

					}

				if (bFinished)
					{
//					Runtime.getRuntime.gc() ;
					if (SlotMachine.GetWinnings() > 0.0)
						{
						rippleWinnings = new RippleCount(0.0, SlotMachine.GetCoinsWon()) ;
						rippleCash = new RippleCount(SlotMachine.GetCoins() - SlotMachine.GetCoinsWon(), SlotMachine.GetCoins()) ;
						nGameState = GS_PAYOUT ;
						}
					else
						nGameState = GS_LOSE ;

					// [note] correct this, "blinking" bug in LED class
					ledWinnings.Set(SlotMachine.GetCoinsWon()) ;
					ledWinnings.On() ;
					}

				}

				break ;

			case GS_PAYOUT :
				PayoutCash() ;
				break ;

			case GS_LOSE :
				// play lose audio [waiting for media]
				nGameState = GS_WAITING ;							// wait for next game
				EnableButtons() ;
				break ;

			case GS_CASHOUT :
				CashingOut() ;
				break ;

			}	

		update(getGraphics()) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	Details (method)										*
    *                                                               *
    ****************************************************************/

	private void Details()
		{
		if (DEBUG)
			System.out.println("Details") ;

		nGameState = GS_DETAILS ;
		hide() ;
		setForeground(Color.black) ;
		setBackground(Color.white) ;
		removeAll() ;
		ClearScreen() ;
		setLayout(null) ;
		if (SlotMachine.dCashPaid >= MAXIMUM_PAYOUT)
			{
			label2 = new Label("Sorry, this slot machine has expired, but...") ;
			add(label2) ;
			label2.reshape(0, 23, 350, 23) ;
			}

		label1 = new Label("Congratulations!") ;
		add(label1) ;
		label1.reshape(0, 46, 350, 23) ;
		label3 = new Label("You're eligible to enter the free prize drawing.") ;
		add(label3) ;
		label3.reshape(0, 69, 350, 23) ;
		label4 = new Label("Please enter some simple details so we know who you are") ;
		add(label4) ;
		label4.reshape(0, 88, 350, 23) ;
		label5 = new Label("and how we can contact you, should you win.") ;
		add(label5) ;
		label5.reshape(0, 107, 350, 23) ;

		label8=new Label("You're cash-out balance for this game is " + SlotMachine.GetCoins() + " credits.") ;
		add(label8) ;
		label8.reshape(0, 130, 400, 23) ;

        label6=new Label("Player Name");
        add(label6);
        label6.reshape(0,161,110,23);

        editName=new TextField(64) ;
        add(editName) ;
		editName.setText(sPlayerName) ;
        editName.reshape(112,161,200,22) ;

        label7=new Label("e-mail Address");
        add(label7);
        label7.reshape(0,201,110,22);

        editEMail=new TextField(64) ;
        add(editEMail) ;
		editEMail.setText(sPlayerEMail) ;
        editEMail.reshape(112,201,200,22) ;

        buttonSend=new Button("I wanna win!") ;
        add(buttonSend);
        buttonSend.reshape(50,273,350,40) ;

		buttonNoThanks=new Button("No Thanks! I've got more toys than I know what to do with") ;
		add(buttonNoThanks) ;
		buttonNoThanks.reshape(50, 320, 350, 26) ;
		show() ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	clickedbuttonSend (method)								*
    *                                                               *
    ****************************************************************/

	private void clickedbuttonSend()
		{
		String	sMsg ;

		long	lHours,
				lMinutes,
				lSeconds ;

		lHours = lGameTotalTime / 3600 ;
		lMinutes = lGameTotalTime / 60 % 60 ;
		lSeconds = lGameTotalTime % 60 ;

		sPlayerName = editName.getText() ;
		sPlayerEMail = editEMail.getText() ;
		setForeground(Color.white) ;
		setBackground(Color.black) ;
		removeAll() ;
		ClearScreen() ;
		nGameState = GS_NETWORK ;
		Message(MSG_WAIT, true) ;
		SendMail smPost = new SendMail() ;
		smPost.SetSenderHost(sEMailBase) ;
		smPost.SetMailHost(sEMailHost) ;
		smPost.SetMessage("Player " + sPlayerName + " (" + sPlayerEMail +") won $" + SlotMachine.GetCoins()) ;
		smPost.AddMessageLine("Graphic download time was " + (int)(lDownloadTotalTime / 1000) + " seconds") ;
		smPost.AddMessageLine("Games played " + (int)(nAuditEntries)) ;
		smPost.AddMessageLine("Game time " + lHours / 10 + lHours % 10 + ":" + lMinutes / 10 + lMinutes % 10 +":" + lSeconds / 10 + lSeconds % 10) ;
		smPost.AddMessageLine("Local time is " + new Date()) ;
//		smPost.AddMessageLine("IP: " + InetAddress.getLocalHost().toString()) ;
		smPost.AddMessageLine("Version: " + VERSION) ;
		try
			{
			smPost.AddMessageLine("Vendor: " + System.getProperty("java.version") + " " + System.getProperty("java.vendor") + " " + System.getProperty("java.vendor.url")) ;
			}

		catch (Exception e)
			{
			}

		try
			{
			smPost.AddMessageLine("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version")) ;
			}

		catch (Exception e)
			{
			}

		for (int i = 0; i < nAuditEntries; i++)
			smPost.AddMessageLine("" + i + " " + nAuditCoins[i] + " " + nAuditReel[i][0] + " " + nAuditReel[i][1] + " " + nAuditReel[i][2] + " " + nAuditWin[i]) ;

		if (SlotMachine.GetCoins() >= 1000)
			{
			smPost.SetRecipient(sEMailName4 + "@" + sEMailBase) ;
			smPost.SetSenderName(sEMailName4) ;
			}
		else if (SlotMachine.GetCoins() >= 750)
			{
			smPost.SetRecipient(sEMailName3 + "@" + sEMailBase) ;
			smPost.SetSenderName(sEMailName3) ;
			}
		else if (SlotMachine.GetCoins() >= 500)
			{
			smPost.SetRecipient(sEMailName2 + "@" + sEMailBase) ;
			smPost.SetSenderName(sEMailName2) ;
			}
		else if (SlotMachine.GetCoins() >= 250)
			{
			smPost.SetRecipient(sEMailName1 + "@" + sEMailBase) ;
			smPost.SetSenderName(sEMailName1) ;
			}

		if (!smPost.Send())
			{
			Message(MSG_EMAIL_ERROR, true) ;
			nGameState = GS_ERROR ;
			PlayAgainOption() ;
			}
		else
			GameOver() ;

		smPost = null ;
		SlotMachine.SetCash(0.0) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	clickedbuttonNoThanks (method)							*
    *                                                               *
    ****************************************************************/

	private void clickedbuttonNoThanks()
		{
		GameOver() ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	clickedbuttonPlayAgain (method)							*
    *                                                               *
    ****************************************************************/

	private void clickedbuttonPlayAgain()
		{
		hide() ;
		removeAll() ;
		ClearScreen() ;
		CreateMachine() ;
		nGameState = GS_WAITING ;
		DisplayInterface() ;
		EnableButtons() ;
		show() ;
		lGraphicBits = GB_ALLBITS ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	GameOver (method)										*
    *                                                               *
    ****************************************************************/

	private void GameOver()
		{
		if (DEBUG)
			System.out.println("GameOver") ;

		nGameState = GS_GAMEOVER ;
		SlotMachine.SetCash(0.0) ;
		hide() ;
		removeAll() ;
		setForeground(Color.white) ;
		setBackground(Color.black) ;
		ClearScreen() ;
		show() ;
		if (SlotMachine.dCashPaid >= MAXIMUM_PAYOUT)
			Message(MSG_EXPIRED, true) ;
		else
			Message(MSG_GAMEOVER, true) ;

		PlayAgainOption() ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	PlayAgainOption (method)								*
    *                                                               *
    ****************************************************************/

	private void PlayAgainOption()
		{
		if (DEBUG)
			System.out.println("PlayAgainOption") ;

		hide() ;
		setLayout(null) ;
		buttonPlayAgain = new Button("Play It Again!") ;
		add(buttonPlayAgain) ;
		buttonPlayAgain.reshape(50, 350, 340, 50) ;
		show() ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	PullHandle (method)										*
    *                                                               *
	* Play a single game of the slot machine as though the spin		*
	* button was depressed, or the handle had been pulled.			*
    *                                                               *
    ****************************************************************/

	private void PullHandle()
	    {
		if (DEBUG)
			System.out.println("PullHandle") ;

//		System.out.println("Before Garbage collection " + Runtime.getRuntime().freeMemory()) ;
//		Runtime.getRuntime().gc() ;							// garbage collection
//		System.out.println("After Garbage collection " + Runtime.getRuntime().freeMemory()) ;
		DisableButtons() ;
		// disable all buttons

		ledWinnings.Off() ;
		if(!SlotMachine.PlayGame())
		    {
			if (!bAutoPlay)
				audioNoMoney.play() ;								// play "NO MONEY" sound

			// collect?
			repaint() ;
			
			return ;
			}

		ledCash.Set(SlotMachine.GetCoins() - SlotMachine.GetCoinsWon()) ;
		ledCoins.Set(SlotMachine.GetCoinsDeposited()) ;
		if (!bAutoPlay)
			audioPullHandle.play() ;								// play "PULL HANDLE" sound

		// calculate amount to spin reels
		for (int i = 0; i < 3; i++)
			{
			nReelV[i] = nReelBaseV ;
			nReelSteps[i] = (SlotMachine.nWindowSymbol[i] * nSymbolHeight) - nReelStepper[i] ;
			nAuditReel[nAuditEntries][i] = SlotMachine.nWindowSymbol[i] ;
			}

		if (nReelSteps[0] >= nReelSteps[1])
			nReelSteps[1] += nReelLength ;

		if (nReelSteps[1] >= nReelSteps[2])
			nReelSteps[2] += nReelLength ;

		if (nReelSteps[1] >= nReelSteps[2])
			nReelSteps[2] += nReelLength ;

		nAuditCoins[nAuditEntries] = (int)SlotMachine.GetCoins() ;
		nAuditWin[nAuditEntries] = (int)SlotMachine.GetCoinsWon() ;
		nAuditEntries++ ;
		if (nAuditEntries >= 2048)
			nAuditEntries = 0 ;

		nGameState = GS_SPINNING ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	Message (method)										*
	* I/P:	lMessage	-- standard message number to display		*
	* I/P:	bUpdate		-- update screen immediately?				*
    *                                                               *
	* Display a pre-defined message in a designated message area,	*
	* updating the screen immediately, if necessary					*
    *                                                               *
    ****************************************************************/

	private void Message(int lMessage, boolean bUpdate)
		{
		String	sLine = "",
				sMsg[] = {
// game over
"Game Over|Thank you for playing",
// media error
"Network Error|Unable to download certain graphic elements|You will be unable to play this game|Virtual Vegas apologises for the inconvenience",
// email error
"Network Error|Unable to submit your details to the server.|You will be unable to claim any prize for this game|Virtual Vegas apologises for the inconvenience",
// wait
"Please Wait|Communicating with the server.",
// internal error
"Internal Error|Unknown function called.|Virtual Vegas apologises for the inconvenience",
// out of money
"You've run out of money||Game Over|Thank you for playing",
// slot has expired
"This slot machine has expired.||Game Over|Thank you for playing",
} ;

		
    	StringTokenizer	stToken ;

		Graphics gMessage = imageMessage.getGraphics() ;

		int	Row = 1 ;

		if (DEBUG)
			System.out.println("Message") ;

		gMessage.setColor(Color.black) ;
		gMessage.fillRect(0, 0, imageMessage.getWidth(null), imageMessage.getHeight(null)) ;
		gMessage.setColor(Color.white) ;
		gMessage.setFont(new Font("Helvetica", Font.BOLD, 20)) ;


		stToken = new StringTokenizer(sMsg[lMessage], "|") ;
		while  (stToken.hasMoreTokens())
			{
    		sLine = stToken.nextElement().toString() ;
			gMessage.drawString(sLine, 0, Row++ * 20) ;
			}

		if (bUpdate)
			update(getGraphics()) ;

		}


	private void ClearScreen()
		{
		gApplet.clearRect(0, 0, nAppletWidth, nAppletHeight) ;
		}

	}
