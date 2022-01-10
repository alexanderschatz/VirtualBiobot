package reinforcementLearning;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import math.Tuple;
import math.Vector;
import virtualBot.VirtualBot;
import virtualBot.World;

public class qLearning {

	private HashMap<Integer, Double> rewards = new HashMap<Integer, Double>();

	private static double[][] qTable;
	private ArrayList<Tuple<Integer, Integer>> positionTuples = World.getPositionTuples();
	private static ArrayList<Tuple<Tuple<Integer, Integer>, Integer>> stateList = new ArrayList<Tuple<Tuple<Integer, Integer>, Integer>>();
	private char[] actions = VirtualBot.getActions();

	private Vector oldPlayerPos;

	private boolean firstRun = true;

	private double learningRate;
	private double discount;
	private double epsilon;

	private int[][] worldMap;

	private PrintWriter writeOld;

	public qLearning() {
		worldMap = World.getMap();

		System.out.println(stateList);

		for (Tuple<Integer, Integer> position : positionTuples) {
			for (int phi = 0; phi < 360; phi += 90) {
				stateList.add(new Tuple<Tuple<Integer, Integer>, Integer>(position, phi));
			}
		}

//		for(Tuple<Tuple<Integer, Integer>, Integer> state: stateList) System.out.println(state);

		// qTable has dimension actions * states (all possible positions)
		qTable = new double[stateList.size()][actions.length];

		try {
			writeOld = new PrintWriter("output/oldQTable.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// set rewards
		rewards.put(0, 0.0); // empty field is minimally positive
		rewards.put(1, -10.0); // wall is very negative
		rewards.put(99, 10.0); // food is very positive

		// set qLearning parameter
		/*
		 * learning rate
		 * 
		 * determines how much newly acquired information overrides old information 0 =
		 * nothing is learned 1 = ignores prior knowledge
		 */
		this.learningRate = 0.5;

		/*
		 * discount
		 * 
		 * determines the importance of future rewards 0 = consider only current rewards
		 * 1 = strive for long-term high rewards
		 */
		this.discount = 0.9;

		/*
		 * epsilon / exploration rate
		 * 
		 * percentage which defines how many actions are chosen randomly
		 */
		this.epsilon = 0.1;
	}

	// initialize QTable with random values
	private void initializeQTable() {
//		try {
//			BufferedReader readMap = new BufferedReader(new FileReader("output/newQTable.txt"));
//			
//			String line;
//			int y = 0;
//			
//			System.out.println("reading..");
//			System.out.println(readMap.readLine());
//			while((line = readMap.readLine()) != null) {
//				System.out.println(line);
//				int x = 0;
//				for(String l: line.split("\t")) {
//					System.out.printf("%s \t");
//					qTable[x][y] = Double.parseDouble(l);
//					x++;
//					
//				}
//				System.out.println();
//				y++;
//			}
//			readMap.close();
//			System.out.println("done ..?");
//			
//		} catch (FileNotFoundException e) {
//			System.out.println("Unable to open file");
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println("Unable to read file");
//			e.printStackTrace();
//		}
		
		
		int actionSize = VirtualBot.getActions().length;
		int stateSize = World.getMatrixSizeX() * World.getMatrixSizeY();
		
		for (int y = 0; y < actionSize; y++) {
			for (int x = 0; x < stateList.size(); x++) {
				qTable[x][y] = Math.random();
			}
		}

		printQTable(writeOld);
		writeOld.close();
	}

	public void printQTable(PrintWriter write) {
		int actionSize = VirtualBot.getActions().length;
//		int stateSize = World.getMatrixSizeX() * World.getMatrixSizeY();

		for (int x = 0; x < stateList.size(); x++) {
//			write.printf("%d \t", x);
			write.print(stateList.get(x));
			for (int y = 0; y < actionSize; y++) {
				write.print("\t" + qTable[x][y]);
			}
			write.println();
		}
	}

	// returns new Action index
	public int newAction() {
		Tuple<Tuple<Integer, Integer>, Integer> currentState = new Tuple<Tuple<Integer, Integer>, Integer>(
				VirtualBot.getT_position(), VirtualBot.getOrientation());
		Tuple<Tuple<Integer, Integer>, Integer> oldState = new Tuple<Tuple<Integer, Integer>, Integer>(
				VirtualBot.getT_lastPosition(), VirtualBot.getOldOrientation());

		// get indices of currentState and oldState => index defines the stateIndex of
		// the QTable
		int outcomeStateIndex = stateList.indexOf(currentState);
		int oldStateIndex = stateList.indexOf(oldState);

//		System.out.println(oldStateIndex + "\t" + oldState + "\t//\t" + outcomeStateIndex + "\t" + currentState);

		// get indices of actions // new action is chosen after QTable was updated
		int newActionIndex = 0;
		int actionTakenIndex = VirtualBot.getLastActionIndex();

		// recalculate qValues
		if (firstRun) {
			// initialize QTable if this is the first run
			initializeQTable();
			firstRun = false;
		} else {
			// get reward
			double reward = VirtualBot.getReward();

			// calculate new qValue
			/*
			 * Q'(s_t, a_t) = Q(s_t, a_t) + alpha * (r_t + gamma * max_a(Q(s_t+1, a)) -
			 * Q(s_t, a_t))
			 * 
			 * Q'(s_t, a_t) .. new qValue at state s_t with action a_t Q(s_t, a_t) .. old
			 * qValue at state s_t with action a_t aplha .. learning rate r_t .. reward
			 * gamma .. discount factor max_a(Q(s_t+1, a)).. estimate of optimal future
			 * value == highest value
			 * 
			 * https://www.practicalai.io/teaching-ai-play-simple-game-using-q-learning/
			 * https://en.wikipedia.org/wiki/Q-learning
			 */

//			System.out.println("--------------------------------------------");

//			for(int i = 0; i < actions.length; i++) {
//				System.out.printf("old: %4.4f \t", qTable[oldStateIndex][i]);
//			}
//			System.out.println();
//			System.out.printf("%4.4f + %2.4f * (>%2.4f< + %2.4f * %4.4f - %4.4f) \n", qTable[oldStateIndex][actionTakenIndex],
//					learningRate, reward, discount, getHighestQValue(outcomeStateIndex),
//					qTable[oldStateIndex][actionTakenIndex]);

			qTable[oldStateIndex][actionTakenIndex] = qTable[oldStateIndex][actionTakenIndex] + learningRate * (reward
					+ discount * getHighestQValue(outcomeStateIndex) - qTable[oldStateIndex][actionTakenIndex]);

//			for(int i = 0; i < actions.length; i++) {
//				System.out.printf("new: %4.4f \t", qTable[oldStateIndex][i]);
//			}
//			System.out.println();
//			System.out.println(
//					"qValue(" + positionTuples.get(oldStateIndex) + 
//					", " + actions[actionTakenIndex] + 
//					") = " + qTable[oldStateIndex][actionTakenIndex]
//			);
		}

		// define new action
		if (Math.random() <= epsilon) {
			// exploration mode: next action is chosen randomly
			newActionIndex = (int) (Math.random() * actions.length);
		} else {
			// optimal mode: next action is chosen based on max possible qValue
			newActionIndex = getHighestQValueIndex(outcomeStateIndex);
		}

		return newActionIndex;
	}

	// returns highest value over all actions by outcomeStateIndex
	private double getHighestQValue(int outcomeStateIndex) {
		double maxValue = -99.0;

		for (int i = 0; i < actions.length; i++) {
			double qValue = qTable[outcomeStateIndex][i];
			if (qValue > maxValue)
				maxValue = qValue;
		}

		return maxValue;
	}

	// returns index of highest value over all actions by outcomeStateIndex
	private int getHighestQValueIndex(int outcomeStateIndex) {
		double maxValue = -99.0;
		int maxValueIndex = 0;

		for (int i = 0; i < actions.length; i++) {
			double qValue = qTable[outcomeStateIndex][i];
			if (qValue > maxValue) {
				maxValue = qValue;
				maxValueIndex = i;
			}
		}

		return maxValueIndex;
	}

	// getter

	public static double[][] getQTable() {
		return qTable;
	}

	public HashMap<Integer, Double> getRewards() {
		return rewards;
	}
	
	public static ArrayList<Tuple<Tuple<Integer, Integer>, Integer>> getStateList() {
		return stateList;
	}
}
