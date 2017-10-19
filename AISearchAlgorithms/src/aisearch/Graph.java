package aisearch;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.util.HashMap;
import java.util.Map;

public class Graph extends Panel {
	
	private static final long serialVersionUID = 1L;
	private final Map<String, Node> nodesMap;
	private Node initialNode;
	private String goalNode;
	
	private Boolean showLegend;
	private long timer;
	
	public Graph() {
		super();
		nodesMap = new HashMap<String, Node>();
		initialNode = null;
		goalNode = "";
		showLegend = true;
		timer = 1000;        // 1000 milliseconds = 1 sec
	}
	
	public int getNumNodes() {
		return nodesMap.values().size();
	}
	
	public Node getNode(String id) {
		return nodesMap.get(id);
	}
	/*
	public List<Node> getNodes() {
		List<Node> nodesList = new ArrayList<>(nodesMap.values());
		return nodesList;
	}
	
	public List<String> getNodeIds() {
		List<String> nodesIds = new ArrayList<>(nodesMap.keySet());
		return nodesIds;
	}*/
	
	// Method that load a graph from a file
	public void loadGraph(String configuration) throws Exception {
		Node node;
		NodeReader reader;
		
		// Initialization of the graph
		nodesMap.clear();
		initialNode = null;
		goalNode = "";
		
		// Nodes Data Loading
		reader = new NodeReader();
		int numNodes = reader.open(configuration);
		System.out.println(numNodes);
		for (int i = 0; i < numNodes; i++) {
			node = reader.read();
			nodesMap.put(node.toString(), node);
			if (i == 0)
				initialNode = node;
			if (node.getIsGoal())
				goalNode = node.toString();
		}
		reader.close();		
	}
	
	public void reset() {
		for (Node node : nodesMap.values()) {
			node.setState(Node.State.NO_GENERATED);
			node.setIdPrevious("");
			node.setCostPrevious(0);
			node.setCostPath(0);
		}
	}
	
	
	// Methods to manage the formation of the graph
	public Node getInitialNode() {
		return initialNode;
	}
	
	public String getGoalNode() {
		return goalNode;
	}
	
	public String[] getStatistics() {
		String[] statistics = new String[2];
		int countNoVisited=0, countOpened=0, countClosed=0;
		
		for (Node node : nodesMap.values()) {
			switch (node.getState()) {
				case NO_GENERATED:
					countNoVisited++;
					break;
				case OPENED:
					countOpened++;
					break;
				case CLOSED:
					countClosed++;
					break;
				default:
					break;
			}
		}
		
		statistics[0] = "NO VISITED: " + Integer.toString(countNoVisited) +
				        " : VISITED: " + Integer.toString(countOpened + countClosed);
		statistics[1] = "OPENED: " + Integer.toString(countOpened) +
		                " : CLOSED: " + Integer.toString(countClosed);
		
		return statistics;
	}
	
	public void showLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}
	
	public void setTimer(long timer) {
		this.timer = timer;
	}
	
	@Override
	public void paint(Graphics g) {
		paintGraph();
		paintLegend();
	}
	
	private void paintLegend() {
		if (showLegend) {
			Graphics g = this.getGraphics();
			int cornerX = this.getWidth() - 155;
			int cornerY = 15;
			g.setColor(new Color(255, 255, 255));
			g.fillRect(cornerX, cornerY, 100,  95);
			g.setColor(Color.black);
			g.drawRect(cornerX, cornerY, 100,  95);
			
			Font font = g.getFont();
			g.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
			g.setColor(Color.black);
			g.drawString("Legend:", cornerX + 5, cornerY + 15);
			
			g.setFont(font);
			g.setColor(Color.gray);
			g.fillRect(cornerX + 10, cornerY + 26, 5, 5);
			g.drawString("Not Visited", cornerX + 20, cornerY + 32);
			
			g.setColor(Color.red.darker());
			g.fillRect(cornerX + 10, cornerY + 42, 5, 5);
			g.drawString("Current", cornerX + 20, cornerY + 48);
			
			g.setColor(Color.green.darker());
			g.fillRect(cornerX + 10, cornerY + 59, 5, 5);
			g.drawString("Opened", cornerX + 20, cornerY + 65);
			
			g.setColor(Color.orange.darker());
			g.fillRect(cornerX + 10, cornerY + 76, 5, 5);
			g.drawString("Closed", cornerX + 20, cornerY + 82);
		}
	}
	
	private void paintGraph() {
		if(!nodesMap.isEmpty()) {
			paintNodes();
			paintEdges();
			paintPath();
		}			
	}
	
	private void paintNodes() {
		Graphics g = this.getGraphics();
		for(Node node : nodesMap.values()) {
			node.paintNode(g);
		}
	}
	
	private void paintEdges() {
		Color color = Color.white;
		for(Node node : nodesMap.values()) {
			int N = node.getNumSuccessors();
			for(int i = 0; i < N; i++) {		
				// Paint the edge
				paintEdge(node.toString(), node.getIdSuccessor(i), color, node.getCostSuccessor(i));
			}
		}
	}	
						
	private void paintEdge(String idOrigin, String idDestination, Color color, double cost) {
		// Origin Coordinates
		Node origin = nodesMap.get(idOrigin);
		int xOrigin = origin.getX();
		int yOrigin = origin.getY();
		
		// Destination Coordinates
		Node destination = nodesMap.get(idDestination);
		int xDestination = destination.getX();
		int yDestination = destination.getY();
		
		Graphics g = this.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1.0f));
		g2.setColor(color);
		
		int xc = (int) ((distanceCoords(xOrigin, yOrigin, xDestination, yOrigin) * 10) /
						distanceCoords(xOrigin, yOrigin, xDestination, yDestination));
        int yc = (int) ((distanceCoords(xOrigin, yOrigin, xOrigin, yDestination) * 10) /
        				distanceCoords(xOrigin, yOrigin, xDestination, yDestination));
        
        // Draw the line
        if ((xOrigin > xDestination) && (yOrigin <= yDestination)) {
            xc = xDestination + xc;
            yc = yDestination - yc;
            g.drawLine(xOrigin - 7, yOrigin + 7, xc, yc);
        } else if ((xOrigin <= xDestination) && (yOrigin > yDestination)) {
            xc = xDestination - xc;
            yc = yDestination + yc;
            g.drawLine(xOrigin + 7, yOrigin - 7, xc, yc);
        } else if ((xOrigin <= xDestination) && (yOrigin <= yDestination)) {
            xc = xDestination - xc;
            yc = yDestination - yc;
            g.drawLine(xOrigin + 7, yOrigin + 7, xc, yc);
        } else if ((xOrigin > xDestination) && (yOrigin > yDestination)) {
            xc = xDestination + xc;
            yc = yDestination + yc;
            g.drawLine(xOrigin - 7, yOrigin + 7, xc, yc);
        }
        
        drawArrow(g2, xOrigin, yOrigin, xc, yc);
	}
	
	// METODOS PARA PINTAR FLECHAS
    int al = 10; // Arrow length
    int aw = 9; // Arrow width
    int haw = aw / 2; // Half arrow width
    int xValues[] = new int[3];
    int yValues[] = new int[3];

    public void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
        // Draw line
        //g.drawLine(x1, y1, x2, y2);
        // Calculate x-y values for arrow head
        calcValues(x1, y1, x2, y2);
        g.fillPolygon(xValues, yValues, 3);
    }

    /* CALC VALUES: Calculate x-y values. */
    public void calcValues(int x1, int y1, int x2, int y2) {
        // North or south
        if (x1 == x2) {
            // North
            if (y2 < y1) {
                arrowCoords(x2, y2, x2 - haw, y2 + al, x2 + haw, y2 + al);
            } // South
            else {
                arrowCoords(x2, y2, x2 - haw, y2 - al, x2 + haw, y2 - al);
            }
            return;
        }
        // East or West
        if (y1 == y2) {
            // East
            if (x2 > x1) {
                arrowCoords(x2, y2, x2 - al, y2 - haw, x2 - al, y2 + haw);
            } // West
            else {
                arrowCoords(x2, y2, x2 + al, y2 - haw, x2 + al, y2 + haw);
            }
            return;
        }
        // Calculate quadrant
        calcValuesQuad(x1, y1, x2, y2);
    }

    /*
	 * CALCULATE VALUES QUADRANTS: Calculate x-y values where direction is not
	 * parallel to eith x or y axis.
     */
    public void calcValuesQuad(int x1, int y1, int x2, int y2) {
        double arrowAng = Math.toDegrees(Math.atan((double) haw / (double) al));
        double dist = Math.sqrt(al * al + aw);
        double lineAng = Math.toDegrees(Math.atan(((double) Math.abs(x1 - x2))
                / ((double) Math.abs(y1 - y2))));
        // Adjust line angle for quadrant
        if (x1 > x2) {
            // South East
            if (y1 > y2) {
                lineAng = 180.0 - lineAng;
            }
        } else // South West
        {
            if (y1 > y2) {
                lineAng = 180.0 + lineAng;
            } // North West
            else {
                lineAng = 360.0 - lineAng;
            }
        }
        // Calculate coords
        xValues[0] = x2;
        yValues[0] = y2;
        calcCoords(1, x2, y2, dist, lineAng - arrowAng);
        calcCoords(2, x2, y2, dist, lineAng + arrowAng);
    }

    /*
	 * CALCULATE COORDINATES: Determine new x-y coords given a start x-y and a
	 * distance and direction
     */
    public void calcCoords(int index, int x, int y, double dist, double dirn) {
        while (dirn < 0.0) {
            dirn = 360.0 + dirn;
        }
        while (dirn > 360.0) {
            dirn = dirn - 360.0;
        }
        // North-East
        if (dirn <= 90.0) {
            xValues[index] = x + (int) (Math.sin(Math.toRadians(dirn)) * dist);
            yValues[index] = y - (int) (Math.cos(Math.toRadians(dirn)) * dist);
            return;
        }
        // South-East
        if (dirn <= 180.0) {
            xValues[index] = x
                    + (int) (Math.cos(Math.toRadians(dirn - 90)) * dist);
            yValues[index] = y
                    + (int) (Math.sin(Math.toRadians(dirn - 90)) * dist);
            return;
        }
        // South-West
        if (dirn <= 90.0) {
            xValues[index] = x
                    - (int) (Math.sin(Math.toRadians(dirn - 180)) * dist);
            yValues[index] = y
                    + (int) (Math.cos(Math.toRadians(dirn - 180)) * dist);
        } // North-West
        else {
            xValues[index] = x
                    - (int) (Math.cos(Math.toRadians(dirn - 270)) * dist);
            yValues[index] = y
                    - (int) (Math.sin(Math.toRadians(dirn - 270)) * dist);
        }
    }

    // ARROW COORDS: Load x-y value arrays */
    public void arrowCoords(int x1, int y1, int x2, int y2, int x3, int y3) {
        xValues[0] = x1;
        yValues[0] = y1;
        xValues[1] = x2;
        yValues[1] = y2;
        xValues[2] = x3;
        yValues[2] = y3;
    }
	
	public void paintPath() {
		Color color = Color.green;
		Node origin, destination;
		String idPrevious;
		double cost;
		if(!goalNode.isEmpty()) {
			destination = nodesMap.get(goalNode);
			while(!destination.getIdPrevious().isEmpty()) {
				idPrevious = destination.getIdPrevious(); 
				origin = nodesMap.get(idPrevious);
				cost = destination.getCostPrevious();
				paintEdge(origin.toString(), destination.toString(), color, cost);
				destination = origin;
			}
		}
	}
				
	public void sleep() {
		// a delay of 1 second
		try {
			Thread.sleep(timer);
		} catch (Exception e) {
			
		}
	}
	
	public double distanceCoords(int xo, int yo, int xd, int yd) {
        return Math.sqrt(Math.pow(xd - xo, 2) + Math.pow(yd - yo, 2));
}
}
