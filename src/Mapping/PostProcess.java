package Mapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class PostProcess 
{
	private static int[][] map;
	private static PostWindow fenster;

	final static int WIDTH = Map.WIDTH;
	final static int HEIGHT = Map.HEIGHT;

	static BufferedWriter w;

	static float N;

	public PostProcess() throws IOException
	{
		map = new int[WIDTH][HEIGHT];
		N = 1.0f;

		read(Logger.getDir()+"/rawMap.dat");

		/////////////////////////////////////////
		// cutoff
		float limit = 2.0f/3.0f * N;
		for(int y = 0; y < HEIGHT; y++)
		{
			for(int x = 0; x < WIDTH; x++)
			{
				if(map[x][y] >= limit) map[x][y] = 1;
				else map[x][y] = 0;
			}
		}
		////////////////////////////////////////

		fenster = new PostWindow(map);
		fenster.makestrat();

	}
	
	public void update()
	{
		fenster.repaint(map);
	}
	
	public static void write() throws IOException
	{
		w = new BufferedWriter(new FileWriter(Logger.getDir()+"/processedMap.dat"));
		
		for(int y = 0; y < HEIGHT; y++)
		{
			for(int x = 0; x < WIDTH; x++)
			{	
				if(x == 0 || y == 0 || x == WIDTH-1 || y == HEIGHT-1) map[x][y] = 1;
				
				if(map[x][y] == 1) 	w.write(1 + " ");
				else 				w.write(0 + " ");
			}
			w.write("\n");
		}
		
		w.close();
	}

	public static void read(String file) throws IOException
	{
		FileReader input = new FileReader(file);
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		int y = 0;
		while((myLine = bufRead.readLine()) != null)
		{
			String[] array = myLine.split(" ");
			for(int x = 0; x < array.length; x++)
			{
				map[x][y] = Integer.parseInt(array[x]);
			}
			y++;
		}
		bufRead.close();
		input.close();

//		N++;
	}

	public void erodeThis()
	{
		map=erode(map,1);
		
		try {
			write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void dilateThis()
	{
		map=dilate(map,1);
		
		try {
			write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void reset()
	{
		try {
			read(Logger.getDir()+"rawMap.dat");
			write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int[][] erode(int[][] dot, int count)
	{
		int[][] newDot = new int[WIDTH][HEIGHT];

		for(int i = 0; i < count; i++)
		{
			for(int y = 0; y < HEIGHT -1; y++)
			{
				for(int x = 0; x < WIDTH -1; x++)
				{

					if(dot[x][y] == 1)
					{
						if(y == 0 && x == 0) // oben links 
						{
							if(dot[x+1][y] == 1 && 
									dot[x][y+1] == 1) 
							{
								newDot[x][y] = 1;
							}
							else newDot[x][y] = 0;

						}
						else if(y == 0 && x == dot.length-2) // oben rechts
						{
							if(dot[x-1][y] == 1 &&
									dot[x][y+1] == 1)
							{
								newDot[x][y] = 1;
							}
							else newDot[x][y] = 0;
						}
						else if(y == dot.length-2 && x == 0) // oben rechts
						{
							if(dot[x+1][y] == 1 && 
									dot[x][y-1] == 1)
							{
								newDot[x][y] = 1;
							}
							else newDot[x][y] = 0;
						}
						else if(y == dot.length-2 && x == dot.length-2) // unten rechts
						{
							if(dot[x-1][y] == 1 && 
									dot[x][y-1] == 1)
							{
								newDot[x][y] = 1;
							}
							else newDot[x][y] = 0;
						}
						else if(y == 0) //oben
						{
							if(dot[x+1][y] == 1 && 
									dot[x-1][y] == 1 && 
									dot[x][y+1] == 1) 
							{
								newDot[x][y] = 1;
							}
							else newDot[x][y] = 0;

						}
						else if(x == 0) //links
						{
							if(dot[x+1][y] == 1 && 
									dot[x][y+1] == 1 && 
									dot[x][y-1] == 1) 
							{
								newDot[x][y] = 1;
							}
							else newDot[x][y] = 0;

						}
						else if(y == dot.length-2) // unten
						{
							if(dot[x+1][y] == 1 && 
									dot[x-1][y] == 1 && 
									dot[x][y-1] == 1) 
							{
								newDot[x][y] = 1;
							}
							else newDot[x][y] = 0;

						}
						else if(x == dot.length-2) // rechts
						{
							if(dot[x-1][y] == 1 && 
									dot[x][y+1] == 1 && 
									dot[x][y-1] == 1) 
							{
								newDot[x][y] = 1;
							}
							else newDot[x][y] = 0;
						}
						else 
						{
							if(dot[x+1][y] == 1 && 
									dot[x][y+1] == 1 && 
									dot[x-1][y] == 1 && 
									dot[x][y-1] == 1) 
							{
								newDot[x][y] = 1;
							}
							else newDot[x][y] = 0;
						}
					}
				}
			}
			if(i < count)
			{
				for(int y = 0; y < HEIGHT -1; y++)
				{
					for(int x = 0; x < WIDTH -1; x++)
					{
						dot[x][y] = newDot[x][y];
					}
				}
			}
		}

		return newDot;
	}

	public static int[][] dilate(int[][] dot, int count)
	{
		int[][] newDot = new int[WIDTH][HEIGHT];

		for(int i = 0; i < count; i++)
		{

			for(int y = 0; y < HEIGHT -1; y++)
			{
				for(int x = 0; x < WIDTH -1; x++)
				{
					if(y == 0 && x == 0)
					{
						if(dot[x][y] == 1 || dot[x+1][y] == 1 || dot[x][y+1] == 1 || dot[x+1][y+1] == 1)
						{				
							newDot[x][y] = 1;
							newDot[x+1][y] = 1;
							newDot[x][y+1] = 1;
							newDot[x+1][y+1] = 1;
						}
					}
					else if(y == 0)	
					{
						if(dot[x][y] == 1 || dot[x+1][y] == 1 || dot[x-1][y] == 1 || dot[x][y+1] == 1 || dot[x+1][y+1] == 1 || dot[x-1][y+1] == 1)
						{				
							newDot[x][y] = 1;
							newDot[x+1][y] = 1;
							newDot[x-1][y] = 1;
							newDot[x][y+1] = 1;
							newDot[x+1][y+1] = 1;
							newDot[x-1][y+1] = 1;
						}
					}
					else if(x == 0)
					{
						if(dot[x][y] == 1 || dot[x+1][y] == 1 || dot[x][y+1] == 1 || dot[x][y-1] == 1 || dot[x+1][y+1] == 1 || dot[x+1][y-1] == 1)
						{				
							newDot[x][y] = 1;
							newDot[x+1][y] = 1;
							newDot[x][y+1] = 1;
							newDot[x][y-1] = 1;
							newDot[x+1][y+1] = 1;
							newDot[x+1][y-1] = 1;
						}
					}
					else
					{
						if(dot[x][y] == 1 || dot[x+1][y] == 1 || dot[x-1][y] == 1 || dot[x][y+1] == 1 || dot[x][y-1] == 1 || 
								dot[x+1][y+1] == 1 || dot[x-1][y+1] == 1 || dot[x+1][y-1] == 1 || dot[x-1][y-1] == 1)
						{				
							newDot[x][y] = 1;
							newDot[x+1][y] = 1;
							newDot[x-1][y] = 1;
							newDot[x][y+1] = 1;
							newDot[x][y-1] = 1;
							newDot[x+1][y+1] = 1;
							newDot[x-1][y+1] = 1;
							newDot[x+1][y-1] = 1;
							newDot[x-1][y-1] = 1;
						}
					}

				}
			}
			if(i < count)
			{
				for(int y = 0; y < HEIGHT -1; y++)
				{
					for(int x = 0; x < WIDTH -1; x++)
					{
						dot[x][y] = newDot[x][y];
					}
				}
			}
		}

		return newDot;
	}

}
