import java.awt.*;

class GifToJavaConverterFrame extends Frame
{
	public GifToJavaConverterFrame(String str)
	{
		super (str);
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
