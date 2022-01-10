package Mapping;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class PostWindow extends JFrame
{
	private BufferStrategy strat;

	private int matrixSizeX;
	private int matrixSizeY;
	
	private final int WIDTH = 1000;
	private final int HEIGHT = 1000;
	
	private int cellSize;
	
	private int[][] map;

	public PostWindow(int[][] map)
	{
		super("Nachbearbeitung");

		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocation(this.getSize().width/2 + WIDTH/2, 0);
		setVisible(true);
			
		this.cellSize = WIDTH / (int)(PostProcess.WIDTH);
	
		matrixSizeX = (int) (PostProcess.WIDTH);
		matrixSizeY = (int) (PostProcess.HEIGHT);
			
		this.map =  map;
	}

	public void makestrat()
	{
		createBufferStrategy(2);
		strat = getBufferStrategy();
	}

	public void repaint(int[][] map)
	{
		this.map = map;
		Graphics g = strat.getDrawGraphics();
		draw(g);
		g.dispose();
		strat.show();
	}

	public void update()
	{

	}

	public void draw(Graphics g)
	{	
		for(int y = 0; y < matrixSizeX; y++)
		{
			for(int x = 0; x < matrixSizeY; x++)
			{
				g.setColor(Color.BLACK);
				g.drawRect(x*cellSize, y*cellSize, cellSize, cellSize);
				
				if(map[x][y] == 1) g.setColor(Color.BLACK);
				else g.setColor(Color.WHITE);
								
				g.fillRect(x*cellSize, y*cellSize, cellSize-1, cellSize-1);
			}
		}
	}
}
