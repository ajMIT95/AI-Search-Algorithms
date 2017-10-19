package aisearch.algorithms;

import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import aisearch.Graph;
import aisearch.Node;
import aisearch.Node.State;

public class DepthFirstSearch implements Runnable {
	
	// DFS Variables
	private int j, numSuccessors;
	private int Step;
	private Node myGoal = null;
	private Node node, suc;
	private Deque<Queue<Node>> stack;
	private List<String> visited;
	boolean anyExtended;
	private Graphics g;
	private final Graph graph;
	private Queue<Node> layerNodes;
	
	// Initialization of the algorithm
	public DepthFirstSearch(Graph graph) {
		this.graph = graph;
		
		if (graph.getNumNodes() > 0)
			init();
	}
	
	public String getStack() {
		return stack.toString();
	}
	
	public void reset() {
		init();
	}
	
	public void init() {
		myGoal = null;
		j = 0;
		numSuccessors = 0;
		node = null;
		suc = null;
		stack = new ArrayDeque<>();
		visited = new ArrayList<>();
		layerNodes = new LinkedList<>();
		anyExtended = false;
		g = graph.getGraphics();
		
		node = graph.getInitialNode();
		layerNodes.add(node);
		stack.push(layerNodes);
		visited.add(node.toString());
		Step = 0;
	}

	@Override
	public void run() {
		while (Step < 4) {
			switch (Step) {
				case 0:
					step0();
					break;
				case 1:
					step1();
					break;
				case 2:
					step2();
					break;
				case 3:
					step3();
					break;	
				default:
					graph.paint(g);
			}
			graph.sleep();
		}
	}
	
	public boolean step() {
		boolean executing = true;
		switch (Step) {
			case 0:
				System.out.println("Step 0");
				step0();
				break;
			case 1:
				System.out.println("Step 1");
				step1();
				break;
			case 2:
				System.out.println("Step 2");
				step2();
				break;
			case 3:
				System.out.println("Step 3");
				step3();
				break;	
			default:
				executing = false;
		}
		return executing;
	}
	
	// Remove the first node on the stack
	private void step0() {
		if (!stack.isEmpty()) {
			Queue<Node> q = stack.element();
			node = q.remove();
			if (q.isEmpty())
				stack.pop();
			System.out.println(node.toString());
			node.setState(State.CURRENT);
			node.paintNode(g);
			if (node.getIsGoal())
				Step = 3;
			else {
				numSuccessors = node.getNumSuccessors();
				j = 0;
				Step = 1;
				layerNodes = new LinkedList<>();
				stack.push(layerNodes);
			}
		}
	}
	
	// Expand a successor of the first node on the stack
	private void step1() {
		anyExtended = false;
		while (j < numSuccessors && !anyExtended) {
			suc = graph.getNode(node.getIdSuccessor(j));
			if (suc != null && !visited.contains(suc.toString())) {
				layerNodes.add(suc);
				System.out.println("suc: " + suc.toString());
				visited.add(suc.toString());
				suc.setState(State.OPENED);
				suc.setIdPrevious(node.toString());
				suc.setCostPrevious(node.getCostSuccessor(j));
				suc.setCostPath(node.getCostSuccessor(j) + node.getCostPath());
				anyExtended = true;
			}
			
			System.out.println(anyExtended);
			
			if (suc.getIsGoal()) {
				myGoal = suc;
				Step = 2;
			}
			j++;
		}
		System.out.println(layerNodes);
		
		if (j == numSuccessors && anyExtended)
			Step = 2;
		if (j == numSuccessors && !anyExtended) {
			Step = 2;
			step2();
		}
		
	}
	
	// Close the element that was being expanded, indicating that the expansion has finished
	private void step2() {
		node.setState(State.CLOSED);
		
		if (myGoal != null)
			Step = 3;
		else
			Step = 0;
	}
	
	// Point the goal node
	private void step3() {
		myGoal.setState(State.CURRENT);
		Step = 4;
	}
}
