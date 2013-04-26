import java.awt.* ;
import java.lang.Runtime ;


/****************************************************************
*                                                               *
* NAME:	Button (class)											*
* Implements a two-state button class							*
*                                                               *
****************************************************************/

public class GraphicButton extends Canvas
	{
	private	boolean	bEnabled = true,
					bState = false ;								// button is initially 'off'

	private	Image	imageButtonUp,									// image of unpressed button
					imageButtonHover,								// mouse is hovering over button
					imageButtonDown ;								// image of depressed button

	public GraphicButton(int x, int y, Image imageUp, Image imageHover, Image imageDown)
		{
		imageButtonUp = imageUp ;
		imageButtonHover = imageHover ;
		imageButtonDown = imageDown ;
		move(x, y) ;
		resize(imageUp.getWidth(this), imageUp.getHeight(this)) ;
//		paint(getGraphics()) ;
		}


	public GraphicButton(Image imageUp, Image imageHover, Image imageDown)
		{
		imageButtonUp = imageUp ;
		imageButtonHover = imageHover ;
		imageButtonDown = imageDown ;
		resize(imageUp.getWidth(this), imageUp.getHeight(this)) ;
//		paint(getGraphics()) ;
		}
	
	
	public void action()
		{
		}


	public void paint()
		{
		paint(getGraphics()) ;
		}


    public void paint(Graphics graphics)
		{
		if (graphics != null)
			{
			if (bState)
				graphics.drawImage(imageButtonDown, 0, 0, this) ;
			else
				graphics.drawImage(imageButtonUp, 0, 0, this) ;

			}


		}


	public boolean mouseEnter(Event evt, int x, int y)
		{
		if (bEnabled)
			{
			}

		return (false) ;
		}

	public boolean mouseExit(Event evt, int x, int y)
		{
		return (false) ;
		}


	public boolean mouseDown(Event event, int x, int y)
		{
		if (bEnabled)
			{
			Down() ;
			return(true) ;
			}

		return (false) ;
		}


	public boolean mouseUp(Event event, int x, int y)
		{
		if (bEnabled)
			{
			Up() ;
			if (inside(x, y))
				{
				event.id = Event.ACTION_EVENT ;
				event.arg = (Object)this ;

				return (action(event, event.arg)) ;
				}
		
			}

		return(false) ;
		}


  public boolean Flip()
		{
		bState = !bState ;											// flip state of button
		paint(getGraphics()) ;										// repaint button

		return (bState) ;											// return the new state
		}


	public boolean Up()
		{
		boolean	bPreviousState = bState ;							// save previous state

		bState = false ;											// set state 'off'
		paint(getGraphics()) ;										// repaint button

		return (bPreviousState) ;									// return previous state
		}


	public boolean Down()
		{
		boolean	bPreviousState = bState ;							// save previous state

		bState = true ;												// set state 'on'
		paint(getGraphics()) ;										// repaint button

		return (bPreviousState) ;									// return previous state
		}

	
	public boolean State()
		{
		return (bState) ;											// return current state
		}


	public Image Image()
		{
		return (bState ? imageButtonDown : imageButtonUp) ;			// return image displayed
		}

	public void disable()
		{
		bEnabled = false ;
		}

	public void enable()
		{
		bEnabled = true ;
		}


	}
