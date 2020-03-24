import java.applet.*;
import java.awt.*;
import GifToJavaConverterFrame;
import java.io.*;
import java.util.*;
import java.awt.image.*;

public class GifToJavaConverter extends Applet implements Runnable
{
	private Thread	 m_GifToJavaConverter = null;
	
	final static int MAX_NUM_IMAGES=50;
	int ImagesLoaded=0;
	Image gifImage[] = new Image[MAX_NUM_IMAGES];

	MediaTracker tracker;

	FileOutputStream fout;

	// STANDALONE APPLICATION SUPPORT:
	//		m_fStandAlone will be set to true if applet is run standalone
	//--------------------------------------------------------------------------
	private boolean m_fStandAlone = false;

	public static void main(String args[])
	{
		GifToJavaConverterFrame frame = new GifToJavaConverterFrame("GifToJavaConverter");

		// Must show Frame before we size it so insets() will return valid values
		//----------------------------------------------------------------------
		frame.show();
        frame.hide();
		frame.resize(frame.insets().left + frame.insets().right  + 150,
					 frame.insets().top  + frame.insets().bottom + 200);

		GifToJavaConverter applet_GifToJavaConverter = new GifToJavaConverter();

		frame.add("Center", applet_GifToJavaConverter);
		applet_GifToJavaConverter.m_fStandAlone = true;
		applet_GifToJavaConverter.init();
		applet_GifToJavaConverter.start();
        frame.show();
	}


	public GifToJavaConverter()
	{
	}

	public String getAppletInfo()
	{
		return "Name: GifToJavaConverter\r\n" +
		       "Author: Jason Brooks\r\n" +
		       "Version 0.1";
	}


	public void init()
	{
    	resize(150, 200);
		show();

		String TileName = "White+Yellow+Orange+Purple+Green+Blue+Red+TopLeft+Top+TopRight+Right+BottomRight+Bottom+BottomLeft+Left";

		tracker = new MediaTracker(this);
		StringTokenizer st=new StringTokenizer(TileName,"+");

		// Now initiate loading of the images.
		// ImagesLoaded keeps count of how many images have been loaded
		//                   and must be unique for each new image.

		String[] Image_Name=new String[MAX_NUM_IMAGES];

		try
		{
			fout=new FileOutputStream("GraphicsData.java");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		printToFile("// Graphics Data Array Generated\r\n\r\n");
		printToFile("int Image[][]={");

		while(st.hasMoreTokens() && ImagesLoaded<MAX_NUM_IMAGES)
		{
			Image_Name[ImagesLoaded]="Images/"+st.nextToken();
			
			//if (m_fStandAlone)
    		gifImage[ImagesLoaded] = Toolkit.getDefaultToolkit().getImage(Image_Name[ImagesLoaded]+".gif");
    		//	else
    		//		gifImage[ImagesLoaded] = getImage(getDocumentBase(), 
			//												  Image_Name[ImagesLoaded]+".gif");

			//gifImage[ImagesLoaded]=getImage(getDocumentBase(),
			//										Image_Name[ImagesLoaded]+
			//										".gif");
			tracker.addImage(gifImage[ImagesLoaded],
							   ImagesLoaded++,16,16);
		}
		try
		{
			tracker.waitForAll();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		int [][]pixels=new int[MAX_NUM_IMAGES][];

		try
		{
			for(int i=0;i<ImagesLoaded;i++)
			{
				int iw=gifImage[i].getWidth(null);
				int ih=gifImage[i].getHeight(null);
				pixels[i]=new int[iw*ih];
				PixelGrabber pg=new PixelGrabber(gifImage[i],0,0,iw,ih,pixels[i],0,iw);
				pg.grabPixels();
				PixelWrite(pixels[i],i);
				//for(int j=0;j<(iw*ih);j++) System.out.print(""+pixels[i][j]);
			}
		}
		catch (InterruptedException e)
		{
		}
		try
		{
			fout.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	void PixelWrite(int [] data, int i)
	{
		printToFile("\r\n			{");

		int len=data.length;
		int point=0;
		int count=0;
		String temp="";

		while(len>0)
		{
			temp+="0x"+Integer.toHexString(data[point++]);
			count++;
			len--;
			if(count<8 && len>0) temp+=", ";
			else
			{
				if (len>0) temp+=",";
				printToFile(temp+"\r\n");
				count=0;
				temp="			";
			}
		}
		printToFile(temp+"},");
	}

	void printToFile(String s)
	{
		try
		{
			for(int i=0;i<s.length();i++)
			{
				fout.write((int)s.charAt(i));
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void destroy()
	{
	}

	public void paint(Graphics g)
	{
		g.drawString("Running: " + Math.random(), 10, 20);
	}

	public void start()
	{
		if (m_GifToJavaConverter == null)
		{
			m_GifToJavaConverter = new Thread(this);
			m_GifToJavaConverter.start();
		}
	}
	
	public void stop()
	{
		if (m_GifToJavaConverter != null)
		{
			m_GifToJavaConverter.stop();
			m_GifToJavaConverter = null;
		}
	}

	public void run()
	{
		/*while (true)
		{
			try
			{
				repaint();
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				stop();
			}
		}*/
		stop();
	}


}
