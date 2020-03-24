//******************************************************************************
// Tetris.java:	Applet
//
//******************************************************************************
import java.applet.*;
import java.awt.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.awt.image.*;

import TetrisFrame;

//==============================================================================
// Main Class for applet Tetris
//
//==============================================================================
public class Tetris extends Applet implements Runnable
{
	// Thread that will control the game.
	private Thread	 m_Tetris = null;
	private KeyboardControl Key_Control = null;

	Runtime runtime = Runtime.getRuntime();

	// Define some constants for in game

	static final int MAX_NUM_IMAGES		= 20;
	static final int BLOCK_SIZE_X		= 16;
	static final int BLOCK_SIZE_Y		= 16;
	static final int GAME_LOADING		= 0,
					 GAME_ATTRACT		= 1,
					 GAME_IN_PROGRESS	= 2,
					 GAME_OVER			= 3;

	static final int KEY_LEFT			= 1,
					 KEY_RIGHT			= 2,
					 KEY_UP				= 3,
					 KEY_DOWN			= 4;

	static final int GAME_BOARD_X		= 10,
					 GAME_BOARD_Y		= 20,
					 GAME_BOARD_OFFSET_X= 0,
					 GAME_BOARD_OFFSET_Y= 0;

	static final int GRAPHICS_SIZE_X	= 320,
					 GRAPHICS_SIZE_Y	= 368;

	static final int NUM_BORDER_TILES	= 8;
	

	//int	 GAME_BLOCKS[][];

	int GAME_NAME[]={ 7,7,7,0,0,7,0,0,7,7,0,7,0,7,0,
					  3,3,3,3,0,0,3,3,0,3,0,0,3,3,3,
					  2,2,2,0,2,0,0,2,0,0,2,0,0,2,0,
					  5,5,0,5,0,5,5,5,0,5,0,5,5,0,5,
					  4,4,4,0,4,0,0,4,0,0,4,0,4,4,4,
					  0,6,6,6,0,0,0,6,0,0,0,6,6,6,0};


static final int Imagedata[][]={
			{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363,
			0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0x0,
			0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363,
			0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece,
			0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xffcecece, 0xff636363, 0xff636363, 0x0,
			0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff,
			0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0x0,
			0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff,
			0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0x0
			},
			{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309,
			0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309,
			0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00,
			0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xffffff00, 0xfff7e309, 0xfff7e309, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2,
			0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0x0,
			0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2,
			0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0xfff6f8d2, 0x0
			},
			{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300,
			0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0x0,
			0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300,
			0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331,
			0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffff6331, 0xffce6300, 0xffce6300, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63,
			0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0x0,
			0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63,
			0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0xffff9c63, 0x0
			},
			{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063,
			0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0x0,
			0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063,
			0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce,
			0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xffff00ce, 0xff9c0063, 0xff9c0063, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce,
			0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0x0,
			0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce,
			0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0xffff9cce, 0x0
			},
			{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331,
			0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0x0,
			0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331,
			0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31,
			0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff00ce31, 0xff006331, 0xff006331, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c,
			0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0x0,
			0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c,
			0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0xff9cff9c, 0x0
			},
			{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c,
			0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0x0,
			0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c,
			0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff,
			0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff3131ff, 0xff00319c, 0xff00319c, 0x0,
			0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff,
			0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0x0,
			0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff,
			0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0xff63ceff, 0x0
			},
			{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000,
			0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000,
			0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000,
			0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xffff0000, 0xff9c0000, 0xff9c0000, 0x0,
			0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363,
			0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0x0,
			0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363,
			0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0xffff6363, 0x0
			},
			{0xff000000, 0xff000000, 0xff000000, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000000, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000000, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff
			},
			{0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff,
			0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff,
			0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff,
			0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff,
			0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff
			},
			{0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000000, 0xff000000, 0xff000000,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000000,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000000,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080
			},
			{0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080
			},
			{0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000000,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000000,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000000, 0xff000000, 0xff000000
			},
			{0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff,
			0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff,
			0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff,
			0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff,
			0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080
			},
			{0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff, 0xff8080ff, 0xff4040ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff000080,
			0xff000000, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000000, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000000, 0xff000000, 0xff000000, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff000080
			},
			{0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff4040ff, 0xff4040ff,
			0xff8080ff, 0xff8080ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff,
			0xff000080, 0xff000080, 0xff000080, 0xff4040ff, 0xff000080, 0xff4040ff, 0xff4040ff, 0xff8080ff,
			0xff4040ff, 0xff8080ff, 0xff8080ff, 0xffc0c0ff, 0xff8080ff, 0xffc0c0ff, 0xffc0c0ff, 0xffc0c0ff
}};

	int GAME_BLOCKS[][]		={ 
							{2,2,		// Width, Height 0=Back Ground.SQUARE
							1,1,1,1,	// Initial Rotation
							1,1,1,1,	// First Rotation
							1,1,1,1,	// Second Rotation
							1,1,1,1},	// Third Rotation

							{3,3,	// L Shape
							2,0,0,2,0,0,2,2,0,	// Initial Rotation
							0,0,0,2,2,2,2,0,0,	// First Rotation
							2,2,0,0,2,0,0,2,0,	// Second Rotation
							0,0,0,0,0,2,2,2,2},	// Third Rotation
							
							{3,3,	// T Shape
							0,0,0,0,3,0,3,3,3,	// Initial Rotation
							3,0,0,3,3,0,3,0,0,	// First Rotation
							0,0,0,3,3,3,0,3,0,	// Second Rotation
							0,0,3,0,3,3,0,0,3},	// Third Rotation

							{3,3,	// Reverse L Shape
							0,4,0,0,4,0,4,4,0,	// Initial Rotation
							0,0,0,4,0,0,4,4,4,	// First Rotation
							4,4,0,4,0,0,4,0,0,	// Second Rotation
							0,0,0,4,4,4,0,0,4},	// Third Rotation
							
							{3,3,	// Staggered Left
							0,0,0,5,5,0,0,5,5,	// Initial Rotation
							0,5,0,5,5,0,5,0,0,	// First Rotation
							0,0,0,5,5,0,0,5,5,	// Second Rotation
							0,5,0,5,5,0,5,0,0},	// Third Rotation

							{3,3,	// Staggered Right
							0,0,0,0,6,6,6,6,0,	// Initial Rotation
							6,0,0,6,6,0,0,6,0,	// First Rotation
							0,0,0,0,6,6,6,6,0,	// Second Rotation
							6,0,0,6,6,0,0,6,0},	// Third Rotation

							{4,4,	// Straight
							0,7,0,0,0,7,0,0,0,7,0,0,0,7,0,0,	// Initial Rotation
							0,0,0,0,0,0,0,0,0,0,0,0,7,7,7,7,	// First Rotation
							0,0,7,0,0,0,7,0,0,0,7,0,0,0,7,0,	// Second Rotation
							0,0,0,0,0,0,0,0,0,0,0,0,7,7,7,7}};  // Third Rotation



	int BORDER_BLOCK=0;
	private int[][] tetrisGameBoard = new int[GAME_BOARD_X+4][GAME_BOARD_Y+4];

	Image[] tetrisBlock=new Image[MAX_NUM_IMAGES];
	Image borders[]= new Image[NUM_BORDER_TILES];
	MediaTracker GameTiles;	
	private int Game_Tiles_Loaded = 0;	// How many tiles are being loaded and tracked?
	Image doubleBuffer = null;			// Double buffering Image Layer.
	private int GameStatus=0;
	Graphics screen_buff=null;
	
	static volatile char keyInput,oldKeyInput;			// Key that's been struck
	boolean latch=false;
	boolean changed=false;

	int currentPiece=0;
	int nextPiece=0;
	int currentRotation=0;

	boolean MouseClick=false;
	boolean Game_Focus=false;
	boolean StartNewGame=false;

	Random rand=new Random();
	int GamePieceX=0;
	int GamePieceY=0;
	int GameSpeed=0;
	int LinesCompleted=0;
	int currentLevel=0;
	final static int SLOWEST_GAME_SPEED=20;
	int CurrentGameSpeedCounter=0;
	int CurrentGameSpeed=SLOWEST_GAME_SPEED;

	private int SCORE_TABLE_X=0;
	int Score=0;
	int NextLevel=0;
	int TotalLinesCompleted=0;

	String PlayerName="Firestarter";
	String PlayerIPAddress="";

	int[] High_Score={10000,9000,8000,7000,6000,5000,4000,3000,2000,1000};
	String [] High_Score_Names={"Jason",
								"Kay",
								"Justin",
								"Helen",
								"Nathan",
								"Clare",
								"Rob",
								"Emma",
								"Tony",
								"Glynis"};

	int jbcounter=0;

	boolean ScreenDrawn=false;

	InetAddress hostAddress;
	String ThisHostName;
	
	// STANDALONE APPLICATION SUPPORT:
	//		m_fStandAlone will be set to true if applet is run standalone
	//--------------------------------------------------------------------------
	private boolean m_fStandAlone = false;

	public static void main(String args[])
	{
		// Create Toplevel Window to contain applet Tetris
		//----------------------------------------------------------------------
		TetrisFrame frame = new TetrisFrame("Tetris");

		// Must show Frame before we size it so insets() will return valid values
		//----------------------------------------------------------------------
		frame.show();
        frame.hide();
		frame.resize(frame.insets().left + frame.insets().right  + GRAPHICS_SIZE_X,
					 frame.insets().top  + frame.insets().bottom + GRAPHICS_SIZE_Y);

		// The following code starts the applet running within the frame window.
		// It also calls GetParameters() to retrieve parameter values from the
		// command line, and sets m_fStandAlone to true to prevent init() from
		// trying to get them from the HTML page.
		//----------------------------------------------------------------------
		Tetris applet_Tetris = new Tetris();

		frame.add("Center", applet_Tetris);
		applet_Tetris.m_fStandAlone = true;
		applet_Tetris.init();
		applet_Tetris.start();
        frame.show();
	}

	// Tetris Class Constructor
	//--------------------------------------------------------------------------
	public Tetris()
	{
		// TODO: Add constructor code here
	}

	public String getAppletInfo()
	{
		return "Name: Tetris\r\n" +
		       "Author: Jason Brooks\r\n" +
		       "Version: 0.1";
	}

	public void init()
	{
		setBackground(Color.black);
		setForeground(Color.white);
    	resize(320, 400);
		resetGameVariables();
		GameStatus=GAME_LOADING;
		doubleBuffer = createImage(GRAPHICS_SIZE_X,GRAPHICS_SIZE_Y);

		/*try
		{
			FileOutputStream fout = new FileOutputStream("GraphicsJava.java");
			fout.write((int)'J');
			fout.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}*/

		// Get Host An Player Name.
		/*try
		{
			hostAddress = InetAddress.getLocalHost();

			String ThisHostName=hostAddress.getHostName();
			PlayerName=ThisHostName;
		}
		catch (UnknownHostException e)
		{
			System.out.println("Unknown Host: "+e);
		}

		try
		{
			String ThisIPAddress=hostAddress.getHostAddress();

			System.out.println("Welcome "+ThisHostName+" At "+ThisIPAddress);
			System.out.println("You are playing Jetris v0.1 written by Jason Brooks");
			System.out.println("E:Mail argonaut@netcomuk.co.uk");
			PlayerIPAddress=ThisIPAddress;
		}
		catch (UnknownHostException e)
		{
			System.out.println("Unknown Host: "+e);
			PlayerIPAddress="Unknown.";
		}*/

		

		/*String TileName = "White+Yellow+Orange+Purple+Green+Blue+Red";

		GameTiles = new MediaTracker(this);
		StringTokenizer st=new StringTokenizer(TileName,"+");*/

		// Now initiate loading of the images.
		// Game_Tiles_Loaded keeps count of how many images have been loaded
		//                   and must be unique for each new image.

		/*String[] Image_Name=new String[MAX_NUM_IMAGES];

		while(st.hasMoreTokens() && Game_Tiles_Loaded<MAX_NUM_IMAGES)
		{
			Image_Name[Game_Tiles_Loaded]="Images/"+st.nextToken();
			
			if (m_fStandAlone)
    				tetrisBlock[Game_Tiles_Loaded] = Toolkit.getDefaultToolkit().getImage(Image_Name[Game_Tiles_Loaded]+".gif");
    			else
    				tetrisBlock[Game_Tiles_Loaded] = getImage(getDocumentBase(), 
															  Image_Name[Game_Tiles_Loaded]+".gif");

			//tetrisBlock[Game_Tiles_Loaded]=getImage(getDocumentBase(),
			//										Image_Name[Game_Tiles_Loaded]+
			//										".gif");
			GameTiles.addImage(tetrisBlock[Game_Tiles_Loaded],
							   Game_Tiles_Loaded++,16,16);
		}

		//Image[] borderBlock=new Image[8];
		String BorderName="TopLeft+Top+TopRight+Right+BottomRight+Bottom+BottomLeft+Left";
		st=new StringTokenizer(BorderName,"+");
		*/
		//BORDER_BLOCK=Game_Tiles_Loaded;
		BORDER_BLOCK=7;
		/*
		while(st.hasMoreTokens() && Game_Tiles_Loaded<MAX_NUM_IMAGES)
		{
			Image_Name[Game_Tiles_Loaded]="Images/"+st.nextToken();
			
			if (m_fStandAlone)
    				tetrisBlock[Game_Tiles_Loaded] = Toolkit.getDefaultToolkit().getImage(Image_Name[Game_Tiles_Loaded]+".gif");
    			else
    				tetrisBlock[Game_Tiles_Loaded] = getImage(getDocumentBase(), 
											  Image_Name[Game_Tiles_Loaded]+".gif");

			//tetrisBlock[Game_Tiles_Loaded]=getImage(getDocumentBase(),
			//										Image_Name[Game_Tiles_Loaded]+
			//										".gif");
			GameTiles.addImage(tetrisBlock[Game_Tiles_Loaded],
							   Game_Tiles_Loaded++);
		}*/
	}

	public void destroy()
	{
	}


	public void paint(Graphics g)
	{
		if(screen_buff!=null) screen_buff.dispose();
		runtime.gc();
		screen_buff=g;
		//g.dispose();
		g=doubleBuffer.getGraphics();

/*		switch(GameStatus)
		{
		case GAME_LOADING: displayLoadingScreen(g);
			break;

		case GAME_ATTRACT: displayAttractScreen(g);
			break;
		
		case GAME_IN_PROGRESS: gameControl(g);
			break;

		case GAME_OVER: gameOverControl(g);
			break;

		default: displayErrorInGameCode(g);
			break;
		}*/

		
		// Draw updated Screen only if updating is required.
		//if(changed)
		//{
			screen_buff.drawImage(doubleBuffer,0,0,null);
			//System.out.println("Paint: "+CurrentGameSpeedCounter);
		//}
		//g.dispose();
		changed=false;
	}

	public void update(Graphics g)
	{
		if(screen_buff!=null) screen_buff.dispose();
		runtime.gc();
		screen_buff=g;
		//g.dispose();
		g=doubleBuffer.getGraphics();

		switch(GameStatus)
		{
		case GAME_LOADING: displayLoadingScreen(g);
			break;

		case GAME_ATTRACT: displayAttractScreen(g);
			break;
		
		case GAME_IN_PROGRESS: gameControl(g);
			break;

		case GAME_OVER: gameOverControl(g);
			break;

		default: displayErrorInGameCode(g);
			break;
		}

		
		// Draw updated Screen only if updating is required.
		if(changed)
		{
			screen_buff.drawImage(doubleBuffer,0,0,null);
			//System.out.println("Update: "+CurrentGameSpeedCounter);
		}
		//g.dispose();
		changed=false;
		//changed=true;
		//paint(g);
	}



	private void displayGameName(Graphics g)
	{
		int index=0;
		int x=1,y=29;
		
		for(int i=0;i<GAME_NAME.length;i++)
		{
			if(GAME_NAME[index]!=0)	
				drawSpriteOnGrid(GAME_NAME[index]-1,x,y,g);
			index++;
			x++;
			if(x>3)
			{
				x=1;
				y--;
				if(y<0)y=0;
			}
		}
	}

	private void drawNextPiece(Graphics g)
	{
		g.setPaintMode();
		g.setColor(Color.black);

		int x=(GAME_BOARD_OFFSET_X+GAME_BOARD_X+3)*16;
		int y=(GRAPHICS_SIZE_Y-17)-(GAME_BOARD_OFFSET_Y*16);

		g.fillRect(x,y-80,16*6,16*5);

		int w=GAME_BLOCKS[nextPiece][0];
		int rotation=(nextPiece==6?1:0);

		drawTetrisShapeOnGrid(nextPiece,
			(GAME_BOARD_OFFSET_X+GAME_BOARD_X+3)+( (6-w)/2)+((w&1)==1?1:0),
							  GAME_BOARD_OFFSET_Y+1+rotation,rotation,g);
	}

	private void displayAttractScreen(Graphics g)
	{
		if(!ScreenDrawn)
		{
			changed=true;
			ScreenDrawn=true;

			// Clear screen
			g.setColor(Color.black);
	 		g.fillRect(0,0,GRAPHICS_SIZE_X-1,GRAPHICS_SIZE_Y-1);

			drawSpriteBorder(GAME_BOARD_OFFSET_X,GAME_BOARD_OFFSET_Y,
							 GAME_BOARD_X+2,GAME_BOARD_Y+2,g);

			drawSpriteBorder(GAME_BOARD_OFFSET_X+GAME_BOARD_X+2,
							 GAME_BOARD_OFFSET_Y,
							 8,7,g);
			//for(int i=0;i<10;i++) eraseSpriteOnGrid(9+i,24,g);
			
			//displayGameName(g);
			drawNextPiece(g);
			
			g.setColor(Color.green);

			int x=(GAME_BOARD_OFFSET_X+GAME_BOARD_X+6)*16;
			SCORE_TABLE_X=((GAME_BOARD_OFFSET_X+GAME_BOARD_X+6)*16)*2;
			int y=GRAPHICS_SIZE_Y-((GAME_BOARD_OFFSET_Y+6)*16)-8;
			//g.drawString("Next Piece",x,y);
			drawCenteredString("Next Piece",x*2,y*2,g);
			drawTetrisGrid(g);
			drawScores(g);
			drawHighScores(g);
			drawStartGameMessage(g);
			StartNewGame=true;
			displayPlayerDetails(g);
		}

		if(Game_Focus && MouseClick) 
		{
			GameStatus=GAME_IN_PROGRESS;
			StartNewGame=true;
			Score=0;
			drawHighScores(g);
		}
		
		HiScoreControl(g);
	}

	private void gameOverControl(Graphics g)
	{
		if(blinkhighscoreY!=0 && (jbcounter++>20))
		{
			changed=true;
			jbcounter=0;
			BlinkHighScore(g);
		}

		if(LastHighScorePosition<10)
		{
			int temp=blinkhighscoreY;

			try
			{
				for(int i=9;i>LastHighScorePosition;i--)
				{
					High_Score[i]=High_Score[i-1];
					High_Score_Names[i]=High_Score_Names[i-1];
				}
				High_Score[LastHighScorePosition]=Score;
				High_Score_Names[LastHighScorePosition]=PlayerName;
				drawHighScores(g);
				LastHighScorePosition=20;
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
			blinkhighscoreY=temp;
			changed=true;
		}
		GameStatus=GAME_ATTRACT;
		ScreenDrawn=false;
	}

	private void HiScoreControl(Graphics g)
	{
		if(isHighScore()) drawHighScores(g);
		if(blinkhighscoreY!=0 && (jbcounter++>20))
		{
			changed=true;
			jbcounter=0;
			BlinkHighScore(g);
		}
	}

	private void gameControl(Graphics g)
	{
		LinesCompleted=0;
		if(StartNewGame)
		{
			StartNewGame=false;
			resetGameVariables();
			g.setColor(Color.black);
	 		g.fillRect(0,0,GRAPHICS_SIZE_X-1,GRAPHICS_SIZE_Y-1);

			drawSpriteBorder(GAME_BOARD_OFFSET_X,GAME_BOARD_OFFSET_Y,
							 GAME_BOARD_X+2,GAME_BOARD_Y+2,g);

			drawSpriteBorder(GAME_BOARD_OFFSET_X+GAME_BOARD_X+2,
							 GAME_BOARD_OFFSET_Y,
							 8,7,g);
			drawTetrisGrid(g);
			changed=true;
			newGamePiece();
			drawNextPiece(g);
			drawHighScores(g);
			displayPlayerDetails(g);
		}

		HiScoreControl(g);
		
		if(CurrentGameSpeedCounter++>=CurrentGameSpeed)
		{
			changed=true;
			CurrentGameSpeedCounter=0;
			eraseTetrisShape(g);
			// See if piece can move.
			if(!tryDropPiece())
			{
				drawTetrisShape(g);
				solderPiece();
				if(isGameOver()) GameStatus=GAME_OVER;
				else
				{
					LinesCompleted=anyLines(g);
					incrementScore(currentPiece,0,LinesCompleted);
					drawScores(g);
					if(isHighScore()) drawHighScores(g);
					if(LinesCompleted!=0) drawTetrisGrid(g);
				}
				newGamePiece();
				drawNextPiece(g);
			}
			drawTetrisShape(g);

			if((keyInput=='l') || (keyInput=='L') && (latch==false) && CurrentGameSpeedCounter==0)
			{
				changed=true;
				latch=true;
				PlayerToNextLevel();
				TotalLinesCompleted+=10;
				drawHighScores(g);
			}

		}

		if(keyInput==' ' && latch==false)
		{
			changed=true;
			latch=true;
			CurrentGameSpeedCounter=0;
			eraseTetrisShape(g);
			// See if piece can move.
			int LinesDropped=0;
			while(tryDropPiece()) LinesDropped++;
			{
				drawTetrisShape(g);
				solderPiece();
				if(isGameOver()) GameStatus=GAME_OVER;
				else
				{
					LinesCompleted=anyLines(g);
					incrementScore(currentPiece,LinesDropped,LinesCompleted);
					drawScores(g);
					if(isHighScore()) drawHighScores(g);
					if(LinesCompleted!=0) drawTetrisGrid(g);
				}
				newGamePiece();
				drawNextPiece(g);
			}
			drawTetrisShape(g);
		}

		if((keyInput==KEY_DOWN) && (latch==false))
		{
			changed=true;
			latch=true;
			eraseTetrisShape(g);
			tryRotatePieceLeft();
			drawTetrisShape(g);
		}

		if((keyInput==KEY_UP) && (latch==false))
		{
			changed=true;
			latch=true;
			eraseTetrisShape(g);
			tryRotatePieceRight();
			drawTetrisShape(g);
		}

		if((keyInput==KEY_LEFT) && (latch==false))
		{
			changed=true;
			latch=true;
			eraseTetrisShape(g);


			canPieceGoLeft();
			if(GamePieceX<-2) 
			{
				drawTetrisShape(g);
				GamePieceX=-2;
			}
			drawTetrisShape(g);
		}

		if((keyInput==KEY_RIGHT) && (latch==false))
		{
			changed=true;
			latch=true;
			eraseTetrisShape(g);
			canPieceGoRight();
			if(GamePieceX>GAME_BOARD_X+2) 
			{
				drawTetrisShape(g);
				GamePieceX=GAME_BOARD_X+2;
			}
			drawTetrisShape(g);
		}


		// Debug info
		/*
		if((keyInput=='g' || keyInput=='G') && latch==false)
		{
			latch=true;
			for(int i=GAME_BOARD_Y;i>=0;i--)
			{
				System.out.print(": ");
				for(int t=0;t<GAME_BOARD_X;t++)
				{
					System.out.print(" "+tetrisGameBoard[t][i]);
				}
				System.out.println();
			}
			System.out.println("==========");
			System.out.println("Level : "+currentLevel+"\nNext Level : "+NextLevel+
								"\nTotal Lines Completed : "+TotalLinesCompleted);

			System.out.println("Current Speed "+CurrentGameSpeedCounter);
			System.out.println("Current Speed Now : "+CurrentGameSpeed);
			System.out.println();
		}
		if((keyInput=='t' || keyInput=='T') && latch==false)
		{
			latch=true;
			drawTetrisGrid(g);
		}*/
	}

	private boolean isGameOver()
	{
		boolean result = false;

		for(int i=0;i<GAME_BOARD_X;i++)
		{
			if(tetrisGameBoard[i][GAME_BOARD_Y]!=0) result=true;
		}
		return result;
	}

	// See if any complete lines, if so update them!
	private int anyLines(Graphics g)
	{
		int lines=0;
		for(int i=0;i<GAME_BOARD_Y;i++)
		{
			int x=0;
			for(int t=0;t<GAME_BOARD_X;t++)
			{
				if(tetrisGameBoard[t][i]!=0)x++;
			}
			if(x==GAME_BOARD_X)
			{
				lines++;
				squashLines(i);
				i--;
			}
		}
		TotalLinesCompleted+=lines;

		if(TotalLinesCompleted>=NextLevel) 
		{
			PlayerToNextLevel();
			drawHighScores(g);
		}

		return lines;
	}

	private void PlayerToNextLevel()
	{
		currentLevel++;
		CurrentGameSpeed=SLOWEST_GAME_SPEED-currentLevel;
		NextLevel+=10;

		if(CurrentGameSpeed<1) CurrentGameSpeed=1;
	}

	private void squashLines(int y)
	{
		try
		{
			for (int i=y;i<GAME_BOARD_Y;i++)
			{
				for(int t=0;t<GAME_BOARD_X;t++)
				{
					tetrisGameBoard[t][i]=tetrisGameBoard[t][i+1];
				}
			}
			for(int t=0;t<GAME_BOARD_X;t++)
				tetrisGameBoard[t][GAME_BOARD_Y]=0;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
	}
	// Glue Piece to Grid.
	private void solderPiece()
	{
		boolean result=false;

		int w=GAME_BLOCKS[currentPiece][0],
			h=GAME_BLOCKS[currentPiece][1];  // get Width and Height.
		int offset=w*h;		// Calculate offset into array.
		int c=2+(offset*currentRotation);

		for(int i=0;i<h;i++)
			for(int t=0;t<w;t++)
		{
			if(!result)
			{
				if( GAME_BLOCKS[currentPiece][c]!=0)
				{
					if( (GamePieceX+t)<0 ) result=true;
					if(!result)	tetrisGameBoard[GamePieceX+t][GamePieceY+h-i-1]=GAME_BLOCKS[currentPiece][c];
				}
			}
			c++;
		}
	}

	// Returns TRUE if a Valid Move False if not.
	private boolean canPieceGoLeft()
	{
		GamePieceX--;
		if(!isPieceObstructed()) return true;
		GamePieceX++;
		return false;
	}

	private void tryRotatePieceRight()
	{
		RotateRight();
		if(!isPieceObstructed()) return;
		RotateLeft();
	}

	private void tryRotatePieceLeft()
	{
		RotateLeft();
		if(!isPieceObstructed()) return;
		RotateRight();
	}

	private void RotateRight()
	{
		currentRotation++;
		currentRotation&=3;
	}

	private void RotateLeft()
	{
		currentRotation--;
		currentRotation&=3;
	}

	private void incrementScore(int piece, int dropped, int lines)
	{
		Score+=((piece+((dropped*piece)/2)+1))*(lines+1)*(currentLevel+1);
	}


	// Returns TRUE if a Valid Move False if not.
	private boolean canPieceGoRight()
	{
		GamePieceX++;
		if(!isPieceObstructed()) return true;
		GamePieceX--;
		return false;
	}

	private boolean tryDropPiece()
	{
		GamePieceY--;
		// Check if we've hit the bottom
		if(GamePieceY<0)
		{
			GamePieceY=0;
			return false;
		}
		if(!isPieceObstructed()) return true;
		GamePieceY++;
		return false;
	}

	// Check to see if current selected piece is obstructed.
	// Returns TRUE if blockage.
	private boolean isPieceObstructed()
	{
		boolean result=false;

		int w=GAME_BLOCKS[currentPiece][0],
			h=GAME_BLOCKS[currentPiece][1];  // get Width and Height.
		int offset=w*h;		// Calculate offset into array.
		int c=2+(offset*currentRotation);

		for(int i=0;i<h;i++)
			for(int t=0;t<w;t++)
		{
			if(!result)
			{
				if( GAME_BLOCKS[currentPiece][c]!=0)
				{
					if( (GamePieceX+t)<0 ) result=true;
					if(!result)	if( tetrisGameBoard[GamePieceX+t][GamePieceY+h-i-1]!=0) result=true;
				}
			}
			c++;
		}

		return result;
	}

	//Draw the currently selected piece on grid.
	private void drawTetrisShape(Graphics g)
	{
		drawTetrisShapeWithinBounds(currentPiece,GamePieceX,GamePieceY,currentRotation,g);
	}

	private void eraseTetrisShape(Graphics g)
	{
		eraseTetrisShapeWithinBounds(currentPiece,GamePieceX,GamePieceY,currentRotation,g);
	}

	private void drawTetrisGrid(Graphics g)
	{
		int c;
		for(int i=0;i<GAME_BOARD_X;i++)
			for(int t=0;t<GAME_BOARD_Y;t++)
		{
			c=tetrisGameBoard[i][t];
			if(c!=0) drawSpriteOnGrid(tetrisGameBoard[i][t]-1,
									  i+GAME_BOARD_OFFSET_X+1,
									  t+GAME_BOARD_OFFSET_Y+1,
									  g);
			else
				eraseSpriteOnGrid(i+GAME_BOARD_OFFSET_X+1,
								  t+GAME_BOARD_OFFSET_Y+1,
								  g);
		}
	}

	private void resetGameVariables()
	{
		jbcounter=0;
		Score=0;
		blinkhighscoreY=0;
		LastHighScorePosition=20;
		updateHighScore=false;
		currentRotation=0;
		MouseClick=false;
		newGamePiece();
		currentLevel=0;
		CurrentGameSpeed=SLOWEST_GAME_SPEED;
		CurrentGameSpeedCounter=CurrentGameSpeed;
		isHighScore();
		NextLevel=10;
		TotalLinesCompleted=0;


		try
		{
			// Set board for blockers
			for(int i=0;i<GAME_BOARD_X+4;i++)
				for(int t=0;t<GAME_BOARD_Y+4;t++)
				tetrisGameBoard[i][t]=0xffff;
			// now clear gaming section
			for(int i=0;i<GAME_BOARD_X;i++)
				for(int t=0;t<GAME_BOARD_Y+4;t++)
				tetrisGameBoard[i][t]=0;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}

		if(Key_Control!=null)
		{
			Key_Control.finished=true;
			Key_Control.t.stop();
			Key_Control=null;
			
		}
		keyInput=0;
		latch=false;
	}

	private void newGamePiece()
	{
		currentPiece=nextPiece;
		currentRotation=0;

		nextPiece=(int)(7*rand.nextDouble());

		GamePieceX=4;
		GamePieceY=GAME_BOARD_Y;
	}

	private void drawScores(Graphics g)
	{
		int y=GRAPHICS_SIZE_Y-((GAME_BOARD_OFFSET_Y+GAME_BOARD_Y+1)*16)-8;
		g.setPaintMode();
		g.setColor(Color.black);
		g.fillRect((SCORE_TABLE_X/2)-64,y+8,140,16);
		g.setColor(Color.orange);
		drawCenteredString("Score",SCORE_TABLE_X,y*2,g);
		y+=16;
		g.setColor(Color.pink);
		drawCenteredString(""+Score,SCORE_TABLE_X,y*2,g);
	}

	private void drawStartGameMessage(Graphics g)
	{
		int y=GRAPHICS_SIZE_Y-(((GAME_BOARD_OFFSET_Y+GAME_BOARD_Y+2)*16)/2);
		int x=(GAME_BOARD_OFFSET_X+1)*16;

		g.setColor(Color.black);
		g.fillRect(x,y-8,160,16);
		g.setColor(Color.white);
		drawCenteredString2("Click The Game Board To Start",x+172,y*2,g);
	}

	private int blinkhighscoreY=0,blinkhighscoreX;

	private void drawHighScores(Graphics g)
	{
		int y=GRAPHICS_SIZE_Y-((GAME_BOARD_OFFSET_Y+GAME_BOARD_Y+1)*16)+24;
		int x=((GAME_BOARD_OFFSET_X+GAME_BOARD_X+2)*16)+8;
		int y2=0;
		boolean highlight=false;

		g.setPaintMode();
		g.setColor(Color.black);
		g.fillRect(x-8,y-8,128,208);
		g.setColor(Color.white);
		drawCenteredString("High Scores",SCORE_TABLE_X,y*2,g);
		y+=16;
		for(int i=0;i<10;i++)
		{
			g.setColor(Color.red);
			drawRightString(""+(i+1),x+4,y,g);
			g.setColor(Color.green);
			drawLeftString(High_Score_Names[i],x+8,y,g);
			g.setColor(Color.yellow);
			drawRightString(""+High_Score[i],GRAPHICS_SIZE_X-8,y,g);
			if(High_Score[i]<Score && highlight!=true)
			{
				highlight=true;
				y2=y;
			}
			y+=16;
		}

		y+=16;
		drawCenteredString("Level : "+(currentLevel+1),SCORE_TABLE_X,y*2,g);
		if(highlight)
		{
			if(GameStatus==GAME_IN_PROGRESS)	blinkhighscoreY=y2;
			BlinkHighScore(g);
		}
	}

	private void BlinkHighScore(Graphics g)
	{
		int x=((GAME_BOARD_OFFSET_X+GAME_BOARD_X+2)*16)+8;
		g.setXORMode(Color.black);
		g.setColor(Color.blue);
		g.fillRect(x-8,blinkhighscoreY-1,GRAPHICS_SIZE_X-x+7,16);
		g.setPaintMode();
	}

	private int LastHighScorePosition=20;
	private boolean updateHighScore=false;

	private boolean isHighScore()
	{
		boolean ishighscore=false;
		int position=20;
		
		for(int i=9;i>=0;i--)
		{
			if(High_Score[i]<Score)
			{
				position=i;
			}
		}
		if(LastHighScorePosition!=position)
		{
			LastHighScorePosition=position;
			//System.out.println("High Score");
			return true;
		}
		return ishighscore;
	}

	private void drawSpriteBorder(int x, int y, int width, int height, Graphics g)
	{
		int i,t;
		drawSpriteOnGrid(BORDER_BLOCK,x,y+height-1,g);
		for(i=0;i<(width-2);i++) drawSpriteOnGrid(BORDER_BLOCK+1,x+1+i,y+height-1,g);
		drawSpriteOnGrid(BORDER_BLOCK+2,x+1+i,y+height-1,g);

		for(t=0;t<height-2;t++) drawSpriteOnGrid(BORDER_BLOCK+3,x+1+i,y+height-t-2,g);
		drawSpriteOnGrid(BORDER_BLOCK+4,x+1+i,y+height-t-2,g);
		for(i;i>0;i--) drawSpriteOnGrid(BORDER_BLOCK+5,x+i,y+height-t-2,g);
		drawSpriteOnGrid(BORDER_BLOCK+6,x,y+height-t-2,g);
		for(t;t>0;t--) drawSpriteOnGrid(BORDER_BLOCK+7,x,y+t,g);
	}

	private void drawTetrisShapeOnGrid(int block, int x, int y, int rotation, Graphics g)
	{
		int w=GAME_BLOCKS[block][0],h=GAME_BLOCKS[block][1];  // get Width and Height.
		int offset=w*h;		// Calculate offset into array.
		int c=2+(offset*rotation);

		for(int i=0;i<h;i++)
			for(int t=0;t<w;t++)
		{
			if(GAME_BLOCKS[block][c]!=0)
			{
				drawSpriteOnGrid(GAME_BLOCKS[block][c]-1,x+t,y+h-i,g);
			}
			c++;
		}
	}

	private void drawTetrisShapeWithinBounds(int block, int x, int y, int rotation, Graphics g)
	{
		int w=GAME_BLOCKS[block][0],h=GAME_BLOCKS[block][1];  // get Width and Height.
		int offset=w*h;		// Calculate offset into array.
		int c=2+(offset*rotation);

		//y+=GAME_BOARD_OFFSET_Y+1;

		for(int i=0;i<h;i++)
			for(int t=0;t<w;t++)
		{
			if(GAME_BLOCKS[block][c]!=0)
			{
				if( ((y+h-i)<=GAME_BOARD_Y) && ((x+t)>=0 && (x+t)<GAME_BOARD_X) )
					drawSpriteOnGrid(GAME_BLOCKS[block][c]-1,x+t+GAME_BOARD_OFFSET_X+1,y+h-i+GAME_BOARD_OFFSET_Y,g);
			}
			c++;
		}
	}

	private void eraseTetrisShapeWithinBounds(int block, int x, int y, int rotation, Graphics g)
	{
		int w=GAME_BLOCKS[block][0],h=GAME_BLOCKS[block][1];  // get Width and Height.
		int offset=w*h;		// Calculate offset into array.
		int c=2+(offset*rotation);

		//y+=GAME_BOARD_OFFSET_Y+1;

		for(int i=0;i<h;i++)
			for(int t=0;t<w;t++)
		{
			if(GAME_BLOCKS[block][c]!=0)
			{
				if( ((y+h-i)<=GAME_BOARD_Y) && ((x+t)>=0 && (x+t)<GAME_BOARD_X) )
					eraseSpriteOnGrid(x+t+GAME_BOARD_OFFSET_X+1,
									  y+h-i+GAME_BOARD_OFFSET_Y,g);
			}
			c++;
		}
	}

	private void eraseTetrisShapeOnGrid(int block, int x, int y, int rotation, Graphics g)
	{
		int w=GAME_BLOCKS[block][0],h=GAME_BLOCKS[block][1];  // get Width and Height.
		int offset=w*h;		// Calculate offset into array.
		int c=2+(offset*rotation);

		for(int i=0;i<h;i++)
			for(int t=0;t<w;t++)
		{
			if(GAME_BLOCKS[block][c]!=0)
			{
				eraseSpriteOnGrid(x+t,y+h-i,g);
			}
			c++;
		}

	}


	private void drawSpriteOnGrid(int Sprite, int x, int y, Graphics g)
	{
		//g.drawImage(tetrisBlock[Sprite],x*16,(GRAPHICS_SIZE_Y-17)-(y*16),Color.black,null);
		Image img=createImage(new MemoryImageSource(16,16,Imagedata[Sprite],0,16));
		g.drawImage(img,x*16,(GRAPHICS_SIZE_Y-17)-(y*16),Color.black,null);
		img.flush();
	}

	private void eraseSpriteOnGrid(int x, int y, Graphics g)
	{
		g.setPaintMode();
		g.setColor(Color.black);
		g.fillRect(x*16,(GRAPHICS_SIZE_Y-17)-(y*16),16,16);
	}

	public void displayErrorInGameCode(Graphics g)
	{
		g.setColor(Color.black);
		g.setPaintMode();
		g.fillRect(0,0,GRAPHICS_SIZE_X-1,GRAPHICS_SIZE_Y-1);
		g.setColor(Color.white);
		{
			drawCenteredString2("Error, Game Status NOT Known.",GRAPHICS_SIZE_X,GRAPHICS_SIZE_Y,g);
			if(m_Tetris!=null)
			{
				m_Tetris.stop();
			}
		}
		changed=true;
	}

	private void displayPlayerDetails(Graphics g)
	{
		g.setColor(Color.yellow);
		g.setPaintMode();
		String Message="Welcome Firewalled Games Player.";
		drawCenteredString2(Message,GRAPHICS_SIZE_X-1,16,g);
	}

	private void displayLoadingScreen(Graphics g)
	{
		/*int filesLoaded=0;

		try
		{
			for(int i=0;i<Game_Tiles_Loaded;i++)
			{
				if(GameTiles.checkID(i,true)) filesLoaded++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			m_Tetris.stop();
		}

		try
		{

			if(GameTiles.isErrorAny())
			{
				System.out.println("Error loading graphic files.\n");
				Object[] list=GameTiles.getErrorsAny();
				for(int i=0;i<list.length;i++) System.out.println(list[i]);
				g.drawString("Errors were found loading graphics.",10,20);
				g.drawString("Please see the Java Console.",10,40);
				g.drawString("Program execution halted.",10,60);
				m_Tetris.stop();
			}

			g.setColor(Color.black);
			g.fillRect(30,100,300,50);
			g.setColor(Color.red);
			g.drawString("Files To Load : "+Game_Tiles_Loaded,40,100);
			g.drawString("Files Loaded : "+filesLoaded,40,120);

			int w=GRAPHICS_SIZE_X-100;
			int h=(GRAPHICS_SIZE_Y-16)/2;
	
			g.setColor(Color.yellow);
			g.fillRect(50,h,w,16);
			g.setColor(Color.green);
			drawCenteredString("Game Loading...... please wait.",
								GRAPHICS_SIZE_X,
								GRAPHICS_SIZE_Y,g);

			int percent=(w/Game_Tiles_Loaded)*filesLoaded;
			percent=filesLoaded==Game_Tiles_Loaded?w:percent;

			g.setXORMode(Color.blue);
			g.setColor(Color.yellow);
			g.fillRect(50,h,percent,16);

			g.setPaintMode();
			changed=true;

		// If game graphics now loaded let's attract a game!
			if(filesLoaded==Game_Tiles_Loaded) GameStatus=GAME_ATTRACT;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			m_Tetris.stop();
		}*/

		GameStatus=GAME_ATTRACT;
	}
	
	private void drawCenteredString(String s, int w, int h, Graphics g)
	{
		FontMetrics fm=g.getFontMetrics();
		int x=(w-fm.stringWidth(s))/2;
		int y=(fm.getAscent()+ (h-(fm.getAscent() + fm.getDescent()))/2);
		g.drawString(s,x,y);

		g.setColor(Color.red);
		g.drawString(s,x+1,y+1);
	}
	
	private void drawCenteredString2(String s, int w, int h, Graphics g)
	{
		FontMetrics fm=g.getFontMetrics();
		int x=(w-fm.stringWidth(s))/2;
		int y=(fm.getAscent()+ (h-(fm.getAscent() + fm.getDescent()))/2);
		g.drawString(s,x,y);
	}

	private void drawRightString(String s, int w, int h, Graphics g)
	{
		FontMetrics fm=g.getFontMetrics();
		int x=(w-fm.stringWidth(s));
		int y=h+fm.getAscent();
		g.drawString(s,x,y);
	}
	private void drawLeftString(String s, int w, int h, Graphics g)
	{
		FontMetrics fm=g.getFontMetrics();
		int x=w;
		int y=h+fm.getAscent();
		g.drawString(s,x,y);
	}
	public void drawStringXY(String s, int x, int h, Graphics g)
	{
		FontMetrics fm=g.getFontMetrics();
		int y=h+fm.getAscent();
		g.drawString(s,x,y);
	}

	public void start()
	{
		if (m_Tetris == null)
		{
			m_Tetris = new Thread(this);
			m_Tetris.start();
		}
	}
	
	public void stop()
	{
		if (m_Tetris != null)
		{
			m_Tetris.stop();
			m_Tetris = null;
		}
	}

	public void run()
	{
		m_Tetris.setPriority(Thread.MIN_PRIORITY);
		int delay;
		if(m_fStandAlone) delay=10;
		else delay=5;
		while(true)
		{
			repaint();
			//update(g);
			try
			{
				//runtime.gc();
				Thread.sleep(delay);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				m_Tetris.stop();
			}
		}
	}

	
	public boolean mouseDown(Event evt, int x, int y)
	{
		MouseClick=true;
		return true;
	}

	public boolean mouseUp(Event evt, int x, int y)
	{
		MouseClick=false;
		return true;
	}

	public boolean mouseEnter(Event evt, int x, int y)
	{
		Game_Focus=true;
		return true;
	}

	public boolean mouseExit(Event evt, int x, int y)
	{
		Game_Focus=false;
		return true;
	}

	public boolean keyDown(Event evtObj, int key)
	{
		//System.out.println("KeyDown.");
		
		if(oldKeyInput!=keyInput)
		{
			if(Key_Control!=null)
			{	
				Key_Control.finished=false;
				Key_Control.t.stop();
				Key_Control=null;
				//System.out.println("Stopping Keyboard Thread "+keyInput);
			}
		}
		else 
			if(Key_Control==null) 
		{
			Key_Control = new KeyboardControl();
			//System.out.println("Kicking off new Keyboard control thread.");
		}
		

		switch(key)
		{
		case Event.LEFT: keyInput=KEY_LEFT;
			break;

		case Event.RIGHT: keyInput=KEY_RIGHT;
			break;
		case Event.UP: keyInput=KEY_UP;
			break;
		case Event.DOWN: keyInput=KEY_DOWN;
			break;
		default: keyInput=(char)key;
			break;
		}

		latch=(oldKeyInput==keyInput)?true:false;
		//latch=true;
		oldKeyInput=keyInput;
		//System.out.println("Latch : "+latch);

		return true;
	}

	public boolean keyUp(Event evtObj, int key)
	{
		if(Key_Control!=null)
		{
			Key_Control.finished=true;
			Key_Control.t.stop();
			Key_Control=null;
			
		}
		keyInput=0;
		oldKeyInput=0;
		latch=false;
		return true;
	}

	public boolean handleEvent(Event evtObj)
	{
		if(evtObj.id == Event.WINDOW_DESTROY)
		{
			hide();
			return true;
		}
		return super.handleEvent(evtObj);
	}
}

class KeyboardControl implements Runnable
{
	Thread t;
	boolean finished=false;

	KeyboardControl()
	{
		t=new Thread(this);
		t.start();
		t.setPriority(Thread.MAX_PRIORITY);
	}

	synchronized public void run()
	{
		while(!finished)
		{
			try
			{
				//System.out.println("Entering the keyboard thread\n");
				Thread.sleep(10);  // Pause 1/6 Second.
				//System.out.println("Still In the keyboard thread\n");
				Tetris.oldKeyInput=0;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		//System.out.println("Leaving keyboard routine.\n");
	}
}