/*

	BLITZ
	=====

	A Java Implementation on a Variation on an old gaming theme.
	The object is to use your bomber to destroy the building so
	your plane can lanc safely on the ground.

	The game ends when your plane crashes into the building.

	@author Jason Brooks
	@version 0.1


  <applet code="Blitz" width=352 height=400>
  </applet>
*/

import java.awt.*;
import java.applet.*;
//import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

public class Blitz extends Applet implements 
				   Runnable//, KeyListener//, MouseListener, MouseMotionListener, KeyListener
{
	// Use this to track the loading of the images.
	//      tracker = Tracking Object
	//      tracked = number of objects to track.
	//
	Runtime r = Runtime.getRuntime();

	MediaTracker Building_Tiles,Player_Tiles;
	int building_tiles_count=0,player_tiles_count=0;

	// Define an integer which states what the game is doing.
	// And some constants.

	static final int LOADING_GAME=0,
					 ATTRACT=1,
					 GAME_IN_PROGRESS=2,
					 END_OF_LEVEL=3,
					 END_OF_GAME=4,
					 LEVEL_COMPLETE=5;


	int game_state = LOADING_GAME;

	// Create a Thread engine, which will control the update speed.
	Thread engine;

	// Define a constant to declare the maximum number of images to load.
	static final int MAX_IMAGES = 25;
	static final int MAX_BUILDING_TILES = 6;
	static final int PLANE_BACK = 12;
	static final int PLANE_FRONT = 13;
	static final int BOMB = 14;
	static final int MAN1 = 15;
	static final int MAN2 = 16;
	static final int PLATFORM = 17;
	static final int LEVEL_TIME = 10;

	char keyInput=0;
	int counter=0;
	int wave_count=0;
	int man_state=0;

	int score=0,high_score=250;


	Graphics screen_buff = null;
	// Create an array of pointers to point to new Images
	// Plus their filenames.
	Image img[] = new Image[MAX_IMAGES];
	String Image_Name[]=new String[MAX_IMAGES];

	Image double_buffer=null;

	static final int BOARD_MAX_X=22;
	static final int BOARD_MAX_Y=25;

	int game_board[][]=new int[BOARD_MAX_X][BOARD_MAX_Y];
	int Plane_X[] = new int[2];
	int Plane_Y[] = new int[2];

	// Timed in 1/50 second approx.
	int time_to_move_plane=0;
	int time_to_move_plane_level=0;
	int time_to_move_bomb=0;
	int time_to_move_bomb_original=0;
	int plane_destroyed=0;
	boolean bomb_in_progress = false;
	boolean plane_damaged=false;
	int bits_left_to_explode=0;
	int Bomb_X=0,Bomb_Y=0;


	boolean changed=false;
	boolean await_new_game=false;
	boolean Start_New_Board = true;

	boolean latch=false;


	int Level=0;

	int width=352,height=400;

	// This section initialises the applet.
	public void init()
	{
		// Set up double buffering buffer.
		//Dimension d=getSize();
		int w=width,h=height;

		double_buffer = createImage(w,h);


		// Define Graphic Filename
		String Building_Names="Red+Blue+Yellow+Green+Purple+Orange";
		
		// Define tracking objects
		Building_Tiles = new MediaTracker(this);
		//Player_Tiles = new MediaTracker(this);

		setBackground(Color.black);
		setForeground(Color.blue);

		showStatus("Blitz - By Jason Brooks - mailto:argonaut@netcomuk.co.uk");
		System.out.println("Blitz - By Jason Brooks\nEmail:argonaut@netcomuk.co.uk\nInitialised.");

		resetGameBoard();
		await_new_game=false;

		StringTokenizer st=new StringTokenizer(Building_Names,"+");
		System.out.println(getDocumentBase());

		//URL myhome=new URL("http://www.netcomuk.co.uk/~jbrooks/JavaGames/Blitz/");


		while(st.hasMoreTokens() && building_tiles_count<=MAX_IMAGES)
		{
			// Load Building Parts 0-5
			Image_Name[building_tiles_count]="Images/"+st.nextToken();
			//System.out.println("Name : "+Image_Name[building_tiles_count]+"\n");

			img[building_tiles_count]=getImage(getDocumentBase(),Image_Name[building_tiles_count]+".gif");
			Building_Tiles.addImage(img[building_tiles_count],building_tiles_count);
			
			// Load Building Tops. 6-11
			img[building_tiles_count+MAX_BUILDING_TILES]=getImage(getDocumentBase(),Image_Name[building_tiles_count]+"Top.gif");
			Building_Tiles.addImage(img[building_tiles_count+MAX_BUILDING_TILES],building_tiles_count+MAX_BUILDING_TILES);

			building_tiles_count++;
		}

		Building_Names="PlaneBack+PlaneFront+Bomb+Man1+Man2+Platform";
		
		// Define tracking objects

		StringTokenizer st2=new StringTokenizer(Building_Names,"+");
		building_tiles_count+=6;

		while(st2.hasMoreTokens() && building_tiles_count<=MAX_IMAGES)
		{
			// Load Game Parts s/b 12-17
			Image_Name[building_tiles_count]="Images/"+st2.nextToken();
			img[building_tiles_count]=getImage(getDocumentBase(),Image_Name[building_tiles_count]+".gif");
			Building_Tiles.addImage(img[building_tiles_count],building_tiles_count);
			building_tiles_count++;
		}

		counter=0;
		score=0;
	}

	public void paint(Graphics g)
	{
		if(screen_buff!=null) screen_buff.dispose();
		r.gc();
		screen_buff=g;
		g=double_buffer.getGraphics();

		//Dimension d=getSize();
		//int width=d.width;
		//int height=d.height;


		if(game_state==LOADING_GAME)
		{
			int donecount=0;

			for(int i=0;i<building_tiles_count;i++)
			{
				if(Building_Tiles.checkID(i,true)) donecount++;
			}
			if(Building_Tiles.isErrorAny())
			{
				System.out.println("Image Loading error");
				Object[] list=Building_Tiles.getErrorsAny();
				for(int i=0;i<list.length;i++)
				{
					System.out.println(list[i]);
				}
			}
			intro_screen(donecount,g);
			if(donecount==building_tiles_count)
			{
				game_state=ATTRACT;
				r.gc();
			}

			changed=true;
			await_new_game=false;
		}

		if(game_state==ATTRACT)
		{
			score=0;
			counter++;
			if(counter==20)
			{
				counter=0;
				await_new_game=false;
			}
			Draw_Attract_Screen(g);
			g.setColor(Color.green);
			if(keyInput=='s' || keyInput=='S')
			{
				game_state=GAME_IN_PROGRESS;
				//game_state=END_OF_LEVEL;
				
				Start_New_Board=true;  // Start a fresh game;
				Level=0;
				r.gc();
				//g.drawString("Starting Game Please Wait.....",10,60);
			}
			//g.drawString("Key = "+keyInput,10,80);

			//keyInput=0;

		}

		if(game_state==GAME_IN_PROGRESS)
		{
			if(Start_New_Board)
			{
				Level++;
				Initialise_New_Level(g);
				keyInput=0;
				r.gc();
				//Debug Option
				//resetGameBoard();
			}
			else
			{
				Move_Plane(g);
			}
			if(bomb_in_progress)
			{
				updateBomb(g);
			}
			else
			{
				if(keyInput==' ' && latch==false)
				{
					latch=true;
					keyInput=0;
					bomb_in_progress=true;
					Random r = new Random();
					bits_left_to_explode=2+(int)(4*r.nextDouble());
					keyInput=0;
					Bomb_X=Plane_X[0];
					Bomb_Y=Plane_Y[0]-1;
					drawGamePiece(BOMB,Bomb_X,Bomb_Y,g);
					if(game_board[Bomb_X][Bomb_Y]!=0)
					{
						bits_left_to_explode--;
						game_board[Bomb_X][Bomb_Y]=0;
					}
				}
			}
			changed=true;
		}

		if (game_state==END_OF_LEVEL)
		{
			
			g.setColor(Color.white);
			drawCenteredString("Congratulations You've Completed",width,120,g);
			g.setColor(Color.white);
			drawCenteredString("Level : "+Level,width,160,g);
			g.setColor(Color.white);
			drawCenteredString("Press 'S' to start next level.",width,200,g);
			g.setPaintMode();
			
			WaveMan(g);
			r.gc();

			if( (keyInput=='s') || (keyInput=='S')) // Prepare for next level
			{
				game_state=GAME_IN_PROGRESS;
				Start_New_Board=true;
			}
			changed=true;
		}

		if (game_state==END_OF_GAME)
		{
			displayScores(g);
			{
				g.setColor(Color.white);
				drawCenteredString("Bad Luck Old Bean",width,120,g);
				g.setColor(Color.white);
				drawCenteredString("Game Over.",width,160,g);

				if(high_score==score)
				{
					g.setColor(Color.white);
					drawCenteredString("Congratulations You Achieved A New High Score",width,200,g);
				}
				Level=0;
				if(time_to_move_plane--<0) game_state=ATTRACT;
			}

		}

		if(changed) 
		{
			screen_buff.drawImage(double_buffer,0,0,null);
			changed=false;
		}
		g.dispose();
	}

	private void WaveMan(Graphics g)
	{
		//g.setColor(Color.white);
		//g.drawString("Entered WaveMan : "+wave_count,10,200);
		wave_count--;
		if(wave_count<0)
		{
			//game_board[BOARD_MAX_X-5][1]=MAN1+man_state;
			g.setPaintMode();
			drawGamePiece(MAN1+(man_state&1),BOARD_MAX_X-4,1,g);
			man_state^=1;
			wave_count=20;
			//g.setColor(Color.white);
			//g.drawString("Inside Animation Loop",10,220);
		}
	}

	private void updateBomb(Graphics g)
	{
		if(--time_to_move_bomb<=0)
		{
			eraseGamePiece(Bomb_X,Bomb_Y--,g);
			if(game_board[Bomb_X][Bomb_Y]!=0) // Start Blowing up building
			{
				if((--bits_left_to_explode>0) && (Bomb_Y>0))
				{
					score++;
					if(score>high_score) high_score=score;
					displayScores(g);
					game_board[Bomb_X][Bomb_Y]=0;
				}
				else
				{
					bits_left_to_explode=0;
					bomb_in_progress=false;
				}
			}
			time_to_move_bomb=time_to_move_bomb_original;
			if(Bomb_Y>0 && bomb_in_progress) drawGamePiece(BOMB,Bomb_X,Bomb_Y,g);
		}
	}

	private void Move_Plane(Graphics g)
	{
		int x=Plane_X[1];
		int y=Plane_Y[1];

		if(++x>=BOARD_MAX_X)
		{
			x=0;
			if(--y<=1) y=1;
		}

		if(game_board[x][y]!=0 && game_board[x][y]!=PLATFORM)
		{
			plane_damaged=true;
			// Debug option.
			//drawGameBoard(g);
			time_to_move_plane=600;
			//game_state=END_OF_GAME;
		}
		if(!plane_damaged)
		{

			if(time_to_move_plane--<=0)
			{
				time_to_move_plane=time_to_move_plane_level;
				eraseGamePiece(Plane_X[0],Plane_Y[0],g);
				
				if( (Plane_X[1]>=BOARD_MAX_X-2) && (Plane_Y[1]<2))
				{
					game_state=END_OF_LEVEL;
					wave_count=20;
					man_state=0;
				}

				if(++Plane_X[1]>=BOARD_MAX_X)
				{
					Plane_X[1]=0;
					if(--Plane_Y[1]<1)
					{
						Plane_Y[1]=1;
					}
				}
				if(++Plane_X[0]>=BOARD_MAX_X)
				{
					Plane_X[0]=0;
					if(--Plane_Y[0]<=1)
					{
						Plane_Y[0]=1;
						//if(Plane_X[1]>=BOARD_MAX_X-2)game_state=END_OF_LEVEL;
					}
				}
			
				drawPlane(g);
			};
		}
		else	// Plane must be damaged therefore drop segments.
		{
			int block=0;

			if(game_board[Plane_X[0]][Plane_Y[0]-1]==0)
			{
				eraseGamePiece(Plane_X[0],Plane_Y[0],g);
				Plane_Y[0]--;
			}
			else block++;

			if(game_board[Plane_X[1]][Plane_Y[1]-1]==0)
			{
				eraseGamePiece(Plane_X[1],Plane_Y[1],g);
				Plane_Y[1]--;
			}
			else block++;

			if(block==2) game_state=END_OF_GAME;

			drawPlane(g);
		}

		if(isBoardClear())time_to_move_plane_level=1;

		if(time_to_move_bomb--<=0)
		{
			time_to_move_bomb=time_to_move_bomb_original;
		};
	}

	private void drawPlane(Graphics g)
	{
		drawGamePiece(PLANE_BACK,Plane_X[0],Plane_Y[0],g);
		drawGamePiece(PLANE_FRONT,Plane_X[1],Plane_Y[1],g);
	}

	private void Initialise_New_Level(Graphics g)
	{
		//Dimension d=getSize();
		g.setColor(Color.black);
		g.fillRect(0,0,width-1,height-1);
		randomGameBoard(Level);
		Plane_X[0]=0;
		Plane_X[1]=1;
		Plane_Y[0]=BOARD_MAX_Y-3;
		Plane_Y[1]=BOARD_MAX_Y-3;
		game_board[Plane_X[0]][Plane_Y[0]]=PLANE_BACK+1;
		game_board[Plane_X[1]][Plane_Y[1]]=PLANE_FRONT+1;
		drawGameBoard(g);
		displayScores(g);
		time_to_move_plane_level=(LEVEL_TIME-Level)>0?(LEVEL_TIME-Level):1;
		time_to_move_bomb_original=10;//(LEVEL_TIME-Level)>0?(LEVEL_TIME-Level):1;

		time_to_move_bomb=time_to_move_bomb_original;
		time_to_move_plane=time_to_move_plane_level;

		plane_damaged=false;
		bomb_in_progress=false;
		bits_left_to_explode=0;
		plane_destroyed=0;
		Bomb_X=0;
		Bomb_Y=0;
		Start_New_Board=false;
	}

	public void drawCenteredString(String s, int w, int h, Graphics g)
	{
		FontMetrics fm=g.getFontMetrics();
		int x=(w-fm.stringWidth(s))/2;
		int y=(fm.getAscent()+ (h-(fm.getAscent() + fm.getDescent()))/2);
		g.drawString(s,x,y);

		g.setColor(Color.red);
		g.drawString(s,x+1,y+1);
	}

	private void drawStringXY(String s, int x, int y, Graphics g)
	{
		FontMetrics fm=g.getFontMetrics();

		//int x=(w-fm.stringWidth(s))/2;
		int h=(fm.getAscent());//+ fm.getDescent());
		g.drawString(s,x,y+h);
	}

	private void intro_screen(int donecount, Graphics g)
	{
		//Dimension d=getSize();
		//int width=d.width;
		//int height=d.height;

		setBackground(Color.black);

		g.setColor(Color.black);
		g.fillRect(0,0,width,height);
		g.setColor(Color.red);
		g.drawRect(0,0,width-1,height-1);

		try
		{

			int y=(height-16)/2;
			int x=(width-200)/2;

			//g.drawString("x="+x,10,40);
			//g.drawString("y="+y,10,60);
			g.setColor(Color.blue);
			g.fillRect(x, y, 200, 16);

			g.setColor(Color.white);
			
			drawCenteredString("Loading Game....",width,height,g);

			int percent_bar= donecount==building_tiles_count?200:(200/building_tiles_count)*donecount;

			g.drawString("% Loaded = "+percent_bar/2,10,80);

			g.setXORMode(Color.white);
			g.setColor(Color.yellow);
			g.fillRect(x, y, percent_bar, 16);
			g.setPaintMode();

			displayScores(g);

			if (percent_bar==200)
			{
				g.setColor(Color.black);
				g.fillRect(x, y, percent_bar, 16);
				g.setColor(Color.white);
				Draw_Attract_Screen(g);
				changed=true;
			}
		}
		catch (ArithmeticException e)
		{

		}
	}

	private void Draw_Attract_Screen(Graphics g)
	{
		if(!await_new_game) 
		{
			randomGameBoard(12);
			await_new_game=true;
		}
		drawGameBoard(g);
		displayScores(g);
		g.setColor(Color.blue);
		//Dimension d=getSize();
		drawCenteredString("Click in box and press 'S' to start",width,60,g);
		drawCenteredString("SPACE BAR to drop bombs.",width,100,g);
		changed=true;
		repaint();
	}

	private void randomGameBoard(int bheight)
	{
		int x,y,i,z;
		int col;

		Random r = new Random();

		bheight+=3;
		if(bheight>(BOARD_MAX_Y/2)) bheight=BOARD_MAX_Y/2;

		resetGameBoard();

		for(i=0;i<BOARD_MAX_X;i++)
		{
			z=(int)(bheight*r.nextDouble())+3;
			col=(int)(6 * r.nextDouble())+1;
			for(x=0;x<z;x++) game_board[i][x]=col;
			game_board[i][x]=col+6;
		}
	}

	public boolean keyDown(Event evtObj, int key)
	{
		keyInput=(char)key;

		//if(keyInput==' ') latch=true;
		//changed=true;
		//showStatus("Key Down : "+(char)key);
		return true;
	}

	public boolean keyUp(Event evtObj, int key)
	{
		if((char)key==' ')
		{
			latch=false;
		}
		keyInput=0;
		return true;
	}

	public boolean handleEvent(Event evtObj)
	{
		if(evtObj.id==Event.WINDOW_DESTROY)
		{
			hide();
			return true;
		}
		return super.handleEvent(evtObj);
	}

	private void drawGameBoard(Graphics g)
	{
		int x,y,c;

		//g.setColor(Color.black);
		//g.setPaintMode();
		for(x=0;x<BOARD_MAX_X;x++)
		{
			for(y=0;y<BOARD_MAX_Y;y++)
			{
				c=game_board[x][y];
				if(c!=0) drawGamePiece(c-1,x,y,g);
				
				if(c==0) eraseGamePiece(x,y,g);
					//g.fillRect(x*16,height-(y*16)-33,16,16);
					//g.drawImage( img[1],x*16, height-(y*16)-17,Color.black,null);
			}
			drawGamePiece(PLATFORM,x,0,g);
			//g.drawImage(img[PLATFORM],x*16, height-17,Color.black,null);
		}
	}

	public String getAppletInfo()
	{
		return "Blitz v0.1 By Jason Brooks.\nE-mail : argonaut@netcomuk.co.uk\n(C) 1998 Jason Brooks - All Rights Reserved.";
	}

	private void drawGamePiece(int piece, int x, int y, Graphics g)
	{
		//Dimension d=getSize();
		//int height=d.height;

		g.drawImage( img[piece],x*16, height-(y*16)-17,Color.black,null);
	}

	private void eraseGamePiece(int x, int y, Graphics g)
	{
		g.setPaintMode();
		g.setColor(Color.black);
		//Dimension d=getSize();
		//int height=d.height;
		g.fillRect(x*16,height-(y*16)-17,16,16);
	}

	private void resetGameBoard()
	{
		for(int i=0;i<BOARD_MAX_X;i++)
			for(int t=0;t<BOARD_MAX_Y;t++)
				game_board[i][t]=0;
	}

	private boolean isBoardClear()
	{
		for(int i=0;i<BOARD_MAX_X;i++)
			for(int t=1;t<BOARD_MAX_Y;t++)
		{
			if (game_board[i][t]!=0 && game_board[i][t]!=PLANE_BACK &&
					game_board[i][t]!=PLANE_FRONT &&
					game_board[i][t]!=BOMB) return false;
		}

		return true;
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	private void displayScores(Graphics g)
	{
		//Dimension d = getSize();
		g.setPaintMode();
		g.setColor(Color.black);
		g.fillRect(0,0,width,16);

		g.setColor(Color.yellow);
		drawStringXY("Score : "+score,0,0,g);
		drawStringXY("High Score : "+high_score,width/2,0,g);
		g.setColor(Color.red);
		g.drawLine(0,16,width,16);
	}


	public void start()
	{
		engine=new Thread(this);
		engine.start();
	}

	public void stop()
	{
		engine.stop();
	}

	public void run()
	{
		int counter=0;
		engine.setPriority(Thread.MIN_PRIORITY);
		showStatus("Blitz V0.1 - By Jason Brooks (argonaut@netcomuk.co.uk)");
		while(true)
		{
			//if(changed) repaint();
			repaint();

			try
			{
				counter++;
				if(counter<100) showStatus("Blitz V0.1 - By Jason Brooks (argonaut@netcomuk.co.uk)");
				else
				{
					Date now = new Date(); 
			        showStatus(now.toGMTString());
				}
				if(counter>200)counter=0;

				Thread.sleep(20);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
}
