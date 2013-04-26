/****************************************************************
*                                                               *
* NAME: PotType (class)											*
*                                                               *
****************************************************************/

class PotType
	{
	double	dLevel,
			dProbability ;


	/****************************************************************
    *                                                               *
    * NAME: PotType (constructor)									*
    *                                                               *
    ****************************************************************/

	public PotType()
		{
		}


	/****************************************************************
    *                                                               *
    * NAME: PotType (constructor)									*
    *                                                               *
    ****************************************************************/

	public PotType(double dNewLevel, double dNewProbability)
		{
		dProbability = dNewProbability ;
		dLevel = dNewLevel ;
		}


	/****************************************************************
    *                                                               *
    * NAME: SetLevel (method)										*
    *                                                               *
    ****************************************************************/

	public void SetLevel(double dNewLevel)
		{
		dLevel = dNewLevel ;
		}


	/****************************************************************
    *                                                               *
    * NAME: SetProbability (method)									*
    *                                                               *
    ****************************************************************/

	public void SetProbability(double dNewProbability)
		{
		dProbability = dNewProbability ;
		}


    /****************************************************************
    *                                                               *
    * NAME: SetEntry (method)										*
    *                                                               *
    ****************************************************************/

	public void SetEntry(double dNewLevel, double dNewProbability)
		{
		dLevel = dNewLevel ;
		dProbability = dNewProbability ;
		}

	}


/****************************************************************
*                                                               *
* NAME: PotTable (class)										*
*                                                               *
****************************************************************/

class PotTable
	{
	int	nPotDropEntries ;

    PotType	Pot[] ;


    /****************************************************************
    *                                                               *
    * NAME: PotTable (constructor)									*
    *                                                               *
    ****************************************************************/

	public PotTable(int nEntries)
		{
		Pot = new PotType[nEntries] ;
		for (int i = 0; i < nEntries; i++)
			Pot[i] = new PotType() ;

		nPotDropEntries = nEntries ;
		}


	/****************************************************************
    *                                                               *
    * NAME: SetEntries (method)										*
    *                                                               *
    ****************************************************************/

	public void SetEntries(int nEntries)
		{
		Pot = new PotType[nEntries] ;
		for (int i = 0; i < nEntries; i++)
			Pot[i] = new PotType() ;

		nPotDropEntries = nEntries ;
		}

	}


/****************************************************************
*                                                               *
* NAME: PayoutLine (class)										*
*                                                               *
****************************************************************/

class PayoutLine
	{
	int	nReelSymbol[] = new int[3] ;

	double	dProbability,
			dPayout ;

	long	lWins ;


	/****************************************************************
    *                                                               *
    * NAME: PayoutLine (constructor)								*
    *                                                               *
    ****************************************************************/

	public PayoutLine(int nReel1, int nReel2, int nReel3, double dNewProbability, double dNewPayout)
		{
		nReelSymbol[0] = nReel1 ;
		nReelSymbol[1] = nReel2 ;
		nReelSymbol[2] = nReel3 ;
		dProbability = dNewProbability ;
		dPayout = dNewPayout ;
		lWins = 0 ;
		}


	/****************************************************************
    *                                                               *
    * NAME: SetLine (method)										*
    *                                                               *
    ****************************************************************/

	public void SetLine(int nReel1, int nReel2, int nReel3, double dNewProbability, double dNewPayout)
		{
		nReelSymbol[0] = nReel1 ;
		nReelSymbol[1] = nReel2 ;
		nReelSymbol[2] = nReel3 ;
		dProbability = dNewProbability ;
		dPayout = dNewPayout ;
		lWins = 0 ;
		}


    /****************************************************************
    *                                                               *
    * NAME: Set (method)											*
    *                                                               *
    ****************************************************************/
	public void IncrementWin()
		{
		lWins++ ;
		}

	}


/****************************************************************
*                                                               *
* NAME: Machine (class)											*
*                                                               *
****************************************************************/

public class MachineLogic
	{
	private final static long	COINDEPOSIT_MAX = 2 ;				// maximum # coins that can be desposited

	private final static long	PAYOUTLINES_MAX = 50 ;
/*
	protected final static int	SYM_CHERRY = 0,
								SYM_LOGO = 1,
								SYM_LEMON = 2,
								SYM_ONEBAR = 3,
								SYM_TWOBAR = 4,
								SYM_THREEBAR = 5,
								SYM_ANY = 99,
								SYMBOLS = 6 ;						// # symbols on a reel
*/
	
	protected final static int	SYM_WILD = 0,
								SYM_PURPLE7 = 1,
								SYM_GREEN7 = 2,
								SYM_RED7 = 3,
								SYM_BLUE7 = 4,
								SYM_STAR = 5,
								SYM_CHERRY = 6,
								SYM_ONEBAR = 7,
								SYM_TWOBAR = 8,
								SYM_THREEBAR = 9,
								SYM_ANY = 10,
								SYMBOLS = 10 ;

	private final static double	PAYOUT_MIN = 80.0,
								PAYOUT_MAX = 120.0 ;

	private final static double	POTPROB_MIN = 0.001,
								POTPROB_MAX = 60,
								POTLEV_MIN = 5000,
								POTLEV_MAX = POTLEV_MIN + 300 ;

//	private final static double	GAMECOST_MIN = 0.25,
//								GAMECOST_MAX = 0.25 * 4 ;

	/* "hardware" defaults */
	private final static boolean	HW_DEFAULT_CLEARCOINS = true,
									HW_DEFAULT_DEDUCTBEFORE = true ;

	private final static long	HW_DEFAULT_POTDROP = 0 ;

	private final static double	HW_DEFAULT_JACKPOTADDER = 0.0,
								HW_DEFAULT_PAYOUT = 92.0,
								HW_DEFAULT_GAMECOST = 0.25 ;

	private	String	sSymbol[] = new String[SYMBOLS + 1] ;

	private	boolean	bDeductBefore,									// deduct cash before pull?
					bClearCoins,									// forget last # coins put in
					bDropIt ;

	private double	dWinnings,										// how much money was won on the last pull
					dCash ;											// how much cash the player has

	double	dGameCost,												// cost per game
			dPot,													// how much money is in the pot
			dCashTaken,												// how much money this machine has taken
			dCashPaid,												// how much money has been paid out
			dProfit,												// operator's profit
			dPaidOutRate,											// % of payout this machine has achieved
			dPayoutRate,											// payout % set by operator
			dStake,													// how much money the player has bet
			dJackpotAdder,											// how much to add to the jackpot wins?
			dWinProbability ;

	private int	nCoinsDeposited ;									// # coins deposited

	int	nWinLines,											// how many win lines in the game
		nPotDropTables,										// how many pot tables exist
		nPotDropTable,										// pot table being used
		nWindowSymbol[] = new int [3],						// win line symbols currently showing in window
		nReelCombo[][] = new int[6][3] ;					// possible combinations

	long	lGamesPlayed,
			lGamesWon ;

	private	PayoutLine	PayoutTable[] ;

	private	PotTable	PotDrop[] ;


	/****************************************************************
    *                                                               *
    * NAME:	SetGameCostSwitch (method)								*
	*																*
	* Default hardware setting for the GameCost switch setting		*
    *                                                               *
    ****************************************************************/

	public void SetRotarySwitch_GameCost()
		{
		dGameCost = HW_DEFAULT_GAMECOST ;
		}

	
	/****************************************************************
    *                                                               *
    * NAME:	SetGameCostSwitch (method)								*
	*																*
	* The equivalent of setting the game cost switch in hardware	*
    *                                                               *
    ****************************************************************/

	public void SetRotarySwitch_GameCost(int nSwitchPosition)
		{
		switch (nSwitchPosition)
			{
			case 1 :
				dGameCost = 0.25 ;
				break ;

			case 2 :
				dGameCost = 1 ;
				break ;

			case 3 :
				dGameCost = 5 ;
				break ;

			default :
				dGameCost = HW_DEFAULT_GAMECOST ;
				break ;

			}

		}


	/****************************************************************
    *                                                               *
    * NAME:	SetPayoutRateSwitch (method)							*
	*																*
	* Default hardware setting for the Payout Rate switch			*
    *                                                               *
    ****************************************************************/

	public void SetRotarySwitch_PayoutRate()
		{
		dPayoutRate = HW_DEFAULT_PAYOUT ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	SetPayoutRateSwitch (method)							*
	*																*
	* The equivalent of setting the payout rate switch in hardware	*
    *                                                               *
    ****************************************************************/

	public void SetRotarySwitch_PayoutRate(int nSwitchPosition)
		{
		switch (nSwitchPosition)
			{
			case 1 :
				dPayoutRate = 90.5 ;
				break ;

			case 2 :
				dPayoutRate = 92 ;
				break ;

			case 3 :
				dPayoutRate = 95 ;
				break ;

			case 4 :
				dPayoutRate = 98 ;
				break ;

			default :
				dPayoutRate = HW_DEFAULT_PAYOUT ;
				break ;

			}

		}


	/****************************************************************
    *                                                               *
    * NAME:	SetPotDropSwitch (method)								*
    *                                                               *
    ****************************************************************/

	public void SetRotarySwitch_PotDrop()
		{
		nPotDropTable = (int)HW_DEFAULT_POTDROP ;
		}

		
	/****************************************************************
    *                                                               *
    * NAME:	SetPotDropSwitch (method)								*
    *                                                               *
    ****************************************************************/

	public void SetRotarySwitch_PotDrop(int nSwitchPosition)
		{
		switch (nSwitchPosition)
			{
			case 1 :
				nPotDropTable = 0 ;
				break ;

			case 2 :
				nPotDropTable = 1 ;
				break ;

			case 3 :
				nPotDropTable = 2;
				break ;

			default :
				nPotDropTable = (int)HW_DEFAULT_POTDROP ;
				break ;

			}

		}


	/****************************************************************
    *                                                               *
    * NAME:	SetPotLevelSwitch (method)								*
	*																*
	* Default hardware setting for the Pot Level (how many coins	*
	* are in the machine)											*
    *                                                               *
    ****************************************************************/

	public void SetIndicator_PotLevel()
		{
		dPot = POTLEV_MIN ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	SetPotLevelSwitch (method)								*
	*																*
	* The equivalent of setting the Pot Level (how many coins		*
	* are in the machine) in hardware								*
    *                                                               *
    ****************************************************************/

	public void SetIndicator_PotLevel(double dIndicator)
		{
		if ((dIndicator >= 0) && (dIndicator <= 99.9))
			dPot = (double)POTLEV_MIN + ((POTLEV_MAX - POTLEV_MIN) * (dIndicator / 100.0)) ;
		else
			dPot = (double)POTLEV_MIN ;

		}


	/****************************************************************
    *                                                               *
    * NAME:	SetJackpotSwitch (method)								*
	*																*
	* Default hardware setting for the Jackpot switch setting		*
    *                                                               *
    ****************************************************************/

	public void SetRotarySwitch_JackpotAdder()
		{
		dJackpotAdder = HW_DEFAULT_JACKPOTADDER ;
		}

	
	/****************************************************************
    *                                                               *
    * NAME:	SetJackpotSwitch (method)								*
	*																*
	* The equivalent of setting the Jackpot switch in hardware		*
    *                                                               *
    ****************************************************************/

	public void SetRotarySwitch_JackpotAdder(int nSwitchPosition)
		{
		switch (nSwitchPosition)
			{
			case 1 :
				dJackpotAdder = 500 ;
				break ;

			case 2 :
				dJackpotAdder = 2000 ;
				break ;

			case 3 :
				dJackpotAdder = 3000 ;
				break ;

			default :
				dJackpotAdder = HW_DEFAULT_JACKPOTADDER ;
				break ;

			}

		}

	/****************************************************************
    *                                                               *
    * NAME:	SetDipSwitch_ClearCoins (method)						*
    *                                                               *
    ****************************************************************/

	public void SetDipSwitch_ClearCoins()
		{
		bClearCoins = HW_DEFAULT_CLEARCOINS ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	SetDipSwitch_ClearCoins (method)						*
    *                                                               *
    ****************************************************************/

	public void SetDipSwitch_ClearCoins(boolean bSwitchPosition)
		{
		bClearCoins = bSwitchPosition ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	SetDipSwitch_DeductBefore (method)						*
    *                                                               *
    ****************************************************************/

	public void SetDipSwitch_DeductBefore()
		{
		bDeductBefore = HW_DEFAULT_DEDUCTBEFORE ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	SetDipSwitch_DeductBefore (method)						*
    *                                                               *
    ****************************************************************/

	public void SetDipSwitch_DeductBefore(boolean bSwitchPosition)
		{
		bDeductBefore = bSwitchPosition ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	SetCash (method)										*
	*																*
	* Set the amount of cash the player has, also calculates the	*
	* # of coins													*
    *                                                               *
    ****************************************************************/

	public void SetCash(double dAmount)
		{
		dCash = dAmount ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	SetCoins (method)										*
	*																*
	* Set the # coins a player has, calculates the amount of cash	*
    *                                                               *
    ****************************************************************/

	public void SetCoins(long lAmount)
		{
		dCash = (double)(lAmount * dGameCost) ;
		}


	public double GetCash()
		{
		return (dCash) ;
		}


	public long GetCoins()
		{
		return ((long)(dCash / dGameCost)) ;
		}


	public long GetCoinsWon()
		{
		return ((long)(dWinnings / dGameCost)) ;
		}

	public double GetWinnings()
		{
		return (dWinnings) ;
		}

	public double GetCoinsDeposited()
		{
		return (nCoinsDeposited) ;
		}


	/****************************************************************
    *                                                               *
    * NAME:	SetPotLevels (method)									*
    *                                                               *
    ****************************************************************/

	void SetPotLevels()
		{
		nPotDropTables = 3 ;
		PotDrop = new PotTable[nPotDropTables] ;

		// 1st pot drop table
		PotDrop[0] = new PotTable(6) ;
		PotDrop[0].Pot[0].SetEntry(0.0, 0.001) ;
		PotDrop[0].Pot[1].SetEntry(POTLEV_MIN, 1.0) ;
    	PotDrop[0].Pot[2].SetEntry(POTLEV_MIN + 10.0, 2.0) ;
    	PotDrop[0].Pot[3].SetEntry(POTLEV_MIN + 20, 4.0) ;
		PotDrop[0].Pot[4].SetEntry(POTLEV_MIN + 30, 6.0) ;
		PotDrop[0].Pot[5].SetEntry(POTLEV_MAX, POTPROB_MAX) ;


		// 2nd pot drop table
		PotDrop[1] = new PotTable(6) ;
		PotDrop[1].Pot[0].SetEntry(0.0, 0.001) ;
		PotDrop[1].Pot[1].SetEntry(POTLEV_MIN, 8.0) ;
	    PotDrop[1].Pot[2].SetEntry(POTLEV_MIN + 10.0, 12.0) ;
	    PotDrop[1].Pot[3].SetEntry(POTLEV_MIN + 20.0, 16.0) ;
	    PotDrop[1].Pot[4].SetEntry(POTLEV_MIN + 30.0, 20.0) ;
		PotDrop[1].Pot[5].SetEntry(POTLEV_MIN + 40.0, 24.0) ;
    
    
		// 3rd pot drop table
		PotDrop[2] = new PotTable(6) ;
	    PotDrop[2].Pot[0].SetEntry(0.0, 0.001) ;
	    PotDrop[2].Pot[1].SetEntry(POTLEV_MIN, 4.0) ;
	    PotDrop[2].Pot[2].SetEntry(POTLEV_MIN + 10.0, 6.0) ;
	    PotDrop[2].Pot[3].SetEntry(POTLEV_MIN + 20.0, 8.0) ;
	    PotDrop[2].Pot[4].SetEntry(POTLEV_MIN + 30.0, 12.0) ;
	    PotDrop[2].Pot[5].SetEntry(POTLEV_MAX, 40.0) ;
/*
		for (int j = 0; j < nPotDropTables; j++)
			{
			for (int i = 0; i < PotDrop[j].nPotDropEntries; i++)
				System.out.println(PotDrop[j].Pot[i].dLevel + " " + PotDrop[j].Pot[i].dProbability) ;

			System.out.println("====================") ;
			}
*/
		}


    /****************************************************************
    *                                                               *
    * NAME:	SetWinLines (method)									*
    *                                                               *
    ****************************************************************/

	private void SetWinLines()
		{
		PayoutTable = new PayoutLine[(int)(PAYOUTLINES_MAX)] ;
/*
		PayoutTable[0] = new PayoutLine((int)SYM_ONEBAR, (int)SYM_ONEBAR, (int)SYM_ANY, 0.287150036, 2.0) ;
		PayoutTable[1] = new PayoutLine((int)SYM_TWOBAR, (int)SYM_TWOBAR, (int)SYM_ANY, 0.071787509, 8.0) ;
		PayoutTable[2] = new PayoutLine((int)SYM_THREEBAR, (int)SYM_THREEBAR, (int)SYM_ANY, 0.047858339, 12.0) ;
		PayoutTable[3] = new PayoutLine((int)SYM_LOGO, (int)SYM_LOGO, (int)SYM_ANY, 0.019143336, 30.0) ;
		PayoutTable[4] = new PayoutLine((int)SYM_CHERRY, (int)SYM_CHERRY, (int)SYM_CHERRY, 0.287150036, 2.0) ;
		PayoutTable[5] = new PayoutLine((int)SYM_LEMON, (int)SYM_LEMON, (int)SYM_LEMON, 0.143575018, 4.0) ;
		PayoutTable[6] = new PayoutLine((int)SYM_ONEBAR, (int)SYM_ONEBAR, (int)SYM_ONEBAR, 0.095716679, 6.0) ;
		PayoutTable[7] = new PayoutLine((int)SYM_TWOBAR, (int)SYM_TWOBAR, (int)SYM_TWOBAR, 0.02392917, 24.0) ;
		PayoutTable[8] = new PayoutLine((int)SYM_THREEBAR, (int)SYM_THREEBAR, (int)SYM_THREEBAR, 0.017946877, 32.0) ;
		PayoutTable[9] = new PayoutLine((int)SYM_LOGO, (int)SYM_LOGO, (int)SYM_LOGO, 0.005743001, 100.0) ;

		nWinLines = 10 ;
*/
		// ADSLOTS payout table
		PayoutTable[ 0] = new PayoutLine(SYM_WILD,		SYM_WILD,		SYM_WILD,		0.001,			1000.0) ;
		PayoutTable[ 1] = new PayoutLine(SYM_WILD,		SYM_WILD,		SYM_ANY,		0.039,			20.0) ;
		
		PayoutTable[ 2] = new PayoutLine(SYM_PURPLE7,	SYM_PURPLE7,	SYM_PURPLE7,	0.01,			200.0) ;
		PayoutTable[ 3] = new PayoutLine(SYM_GREEN7,	SYM_GREEN7,		SYM_GREEN7,		0.01,			200.0) ;
		PayoutTable[ 4] = new PayoutLine(SYM_RED7,		SYM_RED7,		SYM_RED7,		0.01,			200.0) ;
		PayoutTable[ 5] = new PayoutLine(SYM_BLUE7,		SYM_BLUE7,		SYM_BLUE7,		0.005,			200.0) ;

		// any coloured 7
		PayoutTable[ 6] = new PayoutLine(SYM_PURPLE7,	SYM_PURPLE7,	SYM_GREEN7,		0.013 / 16.0,	60.0) ;
		PayoutTable[ 7] = new PayoutLine(SYM_PURPLE7,	SYM_PURPLE7,	SYM_RED7,		0.013 / 16.0,	60.0) ;
		PayoutTable[ 8] = new PayoutLine(SYM_PURPLE7,	SYM_PURPLE7,	SYM_BLUE7,		0.013 / 16.0,	60.0) ;
		PayoutTable[ 9] = new PayoutLine(SYM_PURPLE7,	SYM_GREEN7,		SYM_GREEN7,		0.013 / 16.0,	60.0) ;
		PayoutTable[10] = new PayoutLine(SYM_PURPLE7,	SYM_GREEN7,		SYM_RED7,		0.013 / 16.0,	60.0) ;
		PayoutTable[11] = new PayoutLine(SYM_PURPLE7,	SYM_GREEN7,		SYM_BLUE7,		0.013 / 16.0,	60.0) ;
		PayoutTable[12] = new PayoutLine(SYM_PURPLE7,	SYM_RED7,		SYM_RED7,		0.013 / 16.0,	60.0) ;
		PayoutTable[13] = new PayoutLine(SYM_PURPLE7,	SYM_RED7,		SYM_BLUE7,		0.013 / 16.0,	60.0) ;
		PayoutTable[14] = new PayoutLine(SYM_PURPLE7,	SYM_BLUE7,		SYM_BLUE7,		0.013 / 16.0,	60.0) ;
		PayoutTable[15] = new PayoutLine(SYM_GREEN7,	SYM_GREEN7,		SYM_RED7,		0.013 / 16.0,	60.0) ;
		PayoutTable[16] = new PayoutLine(SYM_GREEN7,	SYM_GREEN7,		SYM_BLUE7,		0.013 / 16.0,	60.0) ;
		PayoutTable[17] = new PayoutLine(SYM_GREEN7,	SYM_RED7,		SYM_RED7,		0.013 / 16.0,	60.0) ;
		PayoutTable[18] = new PayoutLine(SYM_GREEN7,	SYM_RED7,		SYM_BLUE7,		0.013 / 16.0,	60.0) ;
		PayoutTable[19] = new PayoutLine(SYM_GREEN7,	SYM_BLUE7,		SYM_BLUE7,		0.013 / 16.0,	60.0) ;
		PayoutTable[20] = new PayoutLine(SYM_RED7,		SYM_RED7,		SYM_BLUE7,		0.013 / 16.0,	60.0) ;
		PayoutTable[21] = new PayoutLine(SYM_RED7,		SYM_BLUE7,		SYM_BLUE7,		0.013 / 16.0,	60.0) ;

		PayoutTable[22] = new PayoutLine(SYM_STAR,		SYM_STAR,		SYM_STAR,		0.1,			40.0) ;

		PayoutTable[23] = new PayoutLine(SYM_THREEBAR,	SYM_THREEBAR,	SYM_THREEBAR,	0.15,			30.0) ;
		PayoutTable[24] = new PayoutLine(SYM_TWOBAR,	SYM_TWOBAR,		SYM_TWOBAR,		0.15,			20.0) ;
		PayoutTable[25] = new PayoutLine(SYM_ONEBAR,	SYM_ONEBAR,		SYM_ONEBAR,		0.1,			10.0) ;

		PayoutTable[26] = new PayoutLine(SYM_CHERRY,	SYM_CHERRY,		SYM_CHERRY,		0.1,			10.0) ;
		PayoutTable[27] = new PayoutLine(SYM_CHERRY,	SYM_CHERRY,		SYM_ANY,		0.154,			5.0) ;
		PayoutTable[28] = new PayoutLine(SYM_CHERRY,	SYM_ANY,		SYM_ANY,		0.386,			2.0) ;
		
		// any bar
		PayoutTable[29] = new PayoutLine(SYM_ONEBAR,	SYM_TWOBAR,		SYM_THREEBAR,	0.154 / 7.0,	5.0) ;
		PayoutTable[30] = new PayoutLine(SYM_THREEBAR,	SYM_THREEBAR,	SYM_ONEBAR,		0.154 / 7.0,	5.0) ;
		PayoutTable[31] = new PayoutLine(SYM_THREEBAR,	SYM_THREEBAR,	SYM_TWOBAR,		0.154 / 7.0,	5.0) ;
		PayoutTable[32] = new PayoutLine(SYM_TWOBAR,	SYM_TWOBAR,		SYM_ONEBAR,		0.154 / 7.0,	5.0) ;
		PayoutTable[33] = new PayoutLine(SYM_TWOBAR,	SYM_TWOBAR,		SYM_THREEBAR,	0.154 / 7.0,	5.0) ;
		PayoutTable[34] = new PayoutLine(SYM_ONEBAR,	SYM_ONEBAR,		SYM_TWOBAR,		0.154 / 7.0,	5.0) ;
		PayoutTable[35] = new PayoutLine(SYM_ONEBAR,	SYM_ONEBAR,		SYM_THREEBAR,	0.154 / 7.0,	5.0) ;
		
		nWinLines = 36 ;
		}

    /****************************************************************
    *                                                               *
    * NAME: IsWinLine (method)										*
    *                                                               *
    ****************************************************************/

	private int IsWinLine(int nReel1, int nReel2, int nReel3)
		{
		double	dWinnings ;

		int	i, j,
			nWinLine ;
    
		nWinLine = -1 ;
		dWinnings = 0.0 ;
		for (i = 0; i < nWinLines; i++)
			{
			for (j = 0; j < 6; j++)
				{
	            if ((nReel1 == PayoutTable[i].nReelSymbol[nReelCombo[j][0]]) || (PayoutTable[i].nReelSymbol[nReelCombo[j][0]] == SYM_ANY))
					if ((nReel2 == PayoutTable[i].nReelSymbol[nReelCombo[j][1]]) || (PayoutTable[i].nReelSymbol[nReelCombo[j][1]] == SYM_ANY))
						if ((nReel3 == PayoutTable[i].nReelSymbol[nReelCombo[j][2]]) || (PayoutTable[i].nReelSymbol[nReelCombo[j][2]] == SYM_ANY))
							{
							if (PayoutTable[i].dPayout > dWinnings)
								{
								nWinLine = i ;
								dWinnings = PayoutTable[i].dPayout ;
								}

							}

				}

			}

		return (nWinLine) ;
		}


    /****************************************************************
    *                                                               *
    * NAME: AdvanceReel (method)									*
    *                                                               *
    ****************************************************************/

	private int AdvanceReel(int nReel, int nAmount)
		{
	    nReel = nReel + nAmount ;
		nReel = nReel % SYMBOLS ;

		return (nReel) ;
		}


    /****************************************************************
    *                                                               *
    * NAME: ForceAnyWin (method)									*
    *                                                               *
    ****************************************************************/

	private int ForceAnyWin()
		{
		double dRandom, dTotal ;

		int i ;
    
	    dTotal = 0.0 ;
		for (i = 0; i < nWinLines; i++)
	        dTotal = dTotal + PayoutTable[i].dProbability ;
        
	    dRandom = (Math.random() * dTotal) ;
		dTotal = 0.0 ;
		for (i = 0; i < nWinLines; i++)
			{
			dTotal = dTotal + PayoutTable[i].dProbability ;
			if (dTotal > dRandom)
				break ;

			}
        
		if (i >= nWinLines)
			i = nWinLines - 1 ;

		return (i) ;
		}


    /****************************************************************
    *                                                               *
    * NAME:	CalculatePaidOutRate (method)							*
    *                                                               *
    ****************************************************************/

	private void CalculatePaidOutRate()
		{
		if (dCashPaid == 0.0)
			dPaidOutRate = 0.0 ;
		else
			dPaidOutRate = dCashPaid / dCashTaken * 100.0 ;

		}


    /****************************************************************
    *                                                               *
    * NAME: WinProbability (method)									*
    *                                                               *
    ****************************************************************/

	private double WinProbability()
		{
	    int	nPoint, i ;

		double	dProbability ;

//		private final static long lNegPot=0 ;
		// debug results
		// note: this should never happen

/*
		if (dPot < 0.0)
			{
	        Debug.Print "Pot went negative "; lNegPot; " times, pot level is now "; dPot
			lNegPot = lNegPot + 1
			}
*/

		dProbability = POTPROB_MIN ;									// default probability of a win
		if (dPot < (POTLEV_MIN + (Math.random() * 10.0) - 5.0))
			bDropIt = false ;
    
		if (!bDropIt)
			{
			for (i = 0; i < PotDrop[nPotDropTable].nPotDropEntries; i++)
	            if (dPot > (PotDrop[nPotDropTable].Pot[i].dLevel + (Math.random() * 10.0) - 5.0))
					dProbability = PotDrop[nPotDropTable].Pot[i].dProbability ;

			}

		if (dPot > (PotDrop[nPotDropTable].Pot[PotDrop[nPotDropTable].nPotDropEntries - 1].dLevel + (Math.random() * 10.0) - 5.0) || bDropIt)
			{
	        dProbability = PotDrop[nPotDropTable].Pot[PotDrop[nPotDropTable].nPotDropEntries - 1].dProbability ;
			bDropIt = true ;
			}

		return (dProbability) ;
		}


	/****************************************************************
    *                                                               *
    * NAME: DecideWinLose (method)									*
    *                                                               *
    ****************************************************************/

	private boolean DecideWinLose()
		{
		dWinProbability = WinProbability() ;
		if ((Math.random() * 100.0) < dWinProbability)
			return (true) ;

		return (false) ;
		}


    /****************************************************************
    *                                                               *
    * NAME: PlayGame (method)										*
    *                                                               *
    ****************************************************************/

	boolean PlayGame()
		{
		int	nReel, nWinLine, nCombo ;
    
		if (dStake < dGameCost)
			return (false) ;

		if (!bDeductBefore)
			dCash = dCash - dStake ;

		dPot = dPot + dStake * (dPayoutRate / 100.0) ;
		dCashTaken = dCashTaken + dStake ;
		dProfit = dProfit + dStake * ((100.0 - dPayoutRate) / 100.0) ;
		lGamesPlayed = lGamesPlayed + 1 ;
		dWinnings = 0.0 ;
		CalculatePaidOutRate() ;
		// should we let the player win?
		if (DecideWinLose())
			{
			// how much should they win?
			nWinLine = ForceAnyWin() ;

			// shuffle the symbol order
			nCombo = (int)(Math.random() * 6) ;
			nWindowSymbol[0] = PayoutTable[nWinLine].nReelSymbol[nReelCombo[nCombo][0]] ;
			nWindowSymbol[1] = PayoutTable[nWinLine].nReelSymbol[nReelCombo[nCombo][1]] ;
			nWindowSymbol[2] = PayoutTable[nWinLine].nReelSymbol[nReelCombo[nCombo][2]] ;

			// roll out any "ANY" symbols
			for (int i = 0; i < 3; i++)
				{
				if (nWindowSymbol[i] == SYM_ANY)
					{
					nWindowSymbol[i] = AdvanceReel(nWindowSymbol[i], (int)(Math.random() * 3) + 1) ;
					while (IsWinLine(nWindowSymbol[0], nWindowSymbol[1], nWindowSymbol[2]) != nWinLine)
						nWindowSymbol[i] = AdvanceReel(nWindowSymbol[i], 1) ;
					
					}


				}
        
	        // payout winnings
			if (nCoinsDeposited == 2 && PayoutTable[nWinLine].dPayout == 1000.0)
				dWinnings = PayoutTable[nWinLine].dPayout * dGameCost * 5.0 ;
			else
				dWinnings = PayoutTable[nWinLine].dPayout * dGameCost * (double)nCoinsDeposited ;

			dCash = dCash + dWinnings ;
			dCashPaid = dCashPaid + dWinnings ;
			PayoutTable[nWinLine].lWins = PayoutTable[nWinLine].lWins + 1 ;
			dPot = dPot - dWinnings ;
			lGamesWon = lGamesWon + 1 ;
//			System.out.print("Win\t") ;
			}
		else
			{
//			System.out.print("Lose\t") ;
			// make 'em lose
			// spin to random reel positions
			nWindowSymbol[0] = (int)(Math.random() * (SYMBOLS - 1)) ;
			nWindowSymbol[1] = (int)(Math.random() * (SYMBOLS - 1)) ;
			nWindowSymbol[2] = (int)(Math.random() * (SYMBOLS - 1)) ;
			// make sure it's not a win line
			while (IsWinLine(nWindowSymbol[0], nWindowSymbol[1], nWindowSymbol[2]) != -1)
				{
				nReel = (int)(Math.random() * 3) ;
				nWindowSymbol[nReel] = AdvanceReel(nWindowSymbol[nReel], 1) ;
				}

			}

//		System.out.println(sSymbol[nWindowSymbol[0]] + "\t" + sSymbol[nWindowSymbol[1]] + "\t" + sSymbol[nWindowSymbol[2]] + " Win $" + dWinnings + " Cash $" + dCash + " Stake $" + dStake + " Pot $" + dPot + " %" + WinProbability()) ;
		if (bClearCoins || (dCash < (dGameCost * nCoinsDeposited)))
			{
			dStake = 0 ;
			nCoinsDeposited = 0 ;
			}
		else
			{
			if (bDeductBefore)
				dCash -= dStake ;
					
			}

		return (true) ;
		}


/*
	boolean DepositQuarter()
		{
		}

	boolean DepositDollar()
		{
		}

	boolean DepositGameCost()
		{
		}

	boolean DepositMaxBet()
		{
		}
*/
    /****************************************************************
    *                                                               *
    * NAME: DepositCoin (method)									*
	* RET:	false	-- not enough cash to deposit a coin			*
	*		true	-- coin was deposited ok						*
    *                                                               *
    ****************************************************************/

	public boolean DepositCoin()
		{
		if (nCoinsDeposited >= COINDEPOSIT_MAX)
			{
			if (bDeductBefore)
				{
				dCash += dStake ;
				dCash -= dGameCost ;
				}

			dStake = dGameCost ;
			nCoinsDeposited = 1 ;

			return (true) ;
			}
		else if ((dCash >= dGameCost) && (nCoinsDeposited < COINDEPOSIT_MAX))
			{
			dStake += dGameCost ;
			if (bDeductBefore)
				dCash -= dGameCost ;

			nCoinsDeposited++ ;

			return (true) ;
			}

		return (false) ;
		}


    /****************************************************************
    *                                                               *
    * NAME: DepositMaxCoin (method)									*
	* RET:	-1	-- not enough cash to deposit maximum coins			*
	*		0	-- already deposited maximum # coins				*
	*		1	-- maximum # coins deposited ok						*
    *                                                               *
    ****************************************************************/

	public int DepositMaxCoin()
		{
		if (nCoinsDeposited == COINDEPOSIT_MAX)
			return (0) ;
		else if ((nCoinsDeposited < COINDEPOSIT_MAX) && (dCash >= (dGameCost * (COINDEPOSIT_MAX - nCoinsDeposited))))
			{
			dStake += (dGameCost * ((int)COINDEPOSIT_MAX - nCoinsDeposited)) ;
			if (bDeductBefore)
				dCash -= (dGameCost * ((int)COINDEPOSIT_MAX - nCoinsDeposited)) ;

			nCoinsDeposited += ((int)COINDEPOSIT_MAX - nCoinsDeposited) ;

			return (1) ;
			}

		return (-1) ;
		}


    /****************************************************************
    *                                                               *
    * NAME: Collect (method)										*
    *                                                               *
    ****************************************************************/

	void Collect()
		{
		if (bDeductBefore)
			dCash += dStake ;

		dStake = 0.0 ;
		nCoinsDeposited = 0 ;
		dWinnings = 0 ;
		}


    /****************************************************************
    *                                                               *
    * NAME: PowerON (method)										*
    *                                                               *
    ****************************************************************/

	void PowerON()
		{
	    // symbol text strings
		sSymbol[SYM_ANY]		= "------" ;
		sSymbol[SYM_WILD]		= "WILD" ;
		sSymbol[SYM_PURPLE7]	= "PURP7" ;
		sSymbol[SYM_GREEN7]		= "GREEN7" ;
		sSymbol[SYM_RED7]		= "RED7" ;
		sSymbol[SYM_BLUE7]		= "BLUE7" ;
		sSymbol[SYM_STAR]		= "STAR" ;
		sSymbol[SYM_THREEBAR]	= "BAR3" ;
		sSymbol[SYM_TWOBAR]		= "BAR2" ;
		sSymbol[SYM_ONEBAR]		= "BAR1" ;
		sSymbol[SYM_CHERRY]		= "CHERRY" ;
/*
		sSymbol[SYM_CHERRY] = "CHERRY" ;
		sSymbol[SYM_LEMON] = "LEMON" ;
		sSymbol[SYM_ONEBAR] = "BAR1" ;
		sSymbol[SYM_TWOBAR] = "BAR2" ;
		sSymbol[SYM_THREEBAR] = "BAR3" ;
		sSymbol[SYM_LOGO] = "LOGO" ;
*/    
		// interface specific values
    
		// possible reel combinations
		nReelCombo[0][0] = 0 ; nReelCombo[0][1] = 1 ; nReelCombo[0][2] = 2 ;
		nReelCombo[1][0] = 0 ; nReelCombo[1][1] = 2 ; nReelCombo[1][2] = 1 ;
		nReelCombo[2][0] = 1 ; nReelCombo[2][1] = 0 ; nReelCombo[2][2] = 2 ;
		nReelCombo[3][0] = 1 ; nReelCombo[3][1] = 2 ; nReelCombo[3][2] = 0 ;
		nReelCombo[4][0] = 2 ; nReelCombo[4][1] = 0 ; nReelCombo[4][2] = 1 ;
		nReelCombo[5][0] = 2 ; nReelCombo[5][1] = 1 ; nReelCombo[5][2] = 0;
    
	    // slot machine specific constants
		SetPotLevels() ;
		SetWinLines() ;
    
		// slot machine specific values
		nCoinsDeposited = 0 ;
		dStake = 0.0 ;
		SetCash(0.0) ;
		bDropIt = false ;
		dProfit = 0.0 ;
		dPaidOutRate = 0.0 ;
		dCashTaken = 0.0 ;
		dCashPaid = 0.0 ;
		lGamesPlayed = 0 ;
		dWinnings = 0.0 ;
		dWinProbability = 0.0 ;
		SetRotarySwitch_JackpotAdder() ;
		SetIndicator_PotLevel() ;
		SetRotarySwitch_PayoutRate() ;
		SetRotarySwitch_PotDrop() ;
		SetRotarySwitch_GameCost() ;
		SetDipSwitch_DeductBefore() ;
		SetDipSwitch_ClearCoins() ;

		// statistical information
		}

	}
