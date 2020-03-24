//******************************************************************************
// TetrisFrame.java:	
//
//******************************************************************************
import java.awt.*;

//==============================================================================
// STANDALONE APPLICATION SUPPORT
// 	This frame class acts as a top-level window in which the applet appears
// when it's run as a standalone application.
//==============================================================================
class TetrisFrame extends Frame
{
	public TetrisFrame(String str)
	{
		super (str);
		setResizable(false);
	}

	public boolean handleEvent(Event evt)
	{
		switch (evt.id)
		{
			case Event.WINDOW_DESTROY:
				dispose();
				System.exit(0);
				return true;

			default:
				return super.handleEvent(evt);
		}			 
	}
}
