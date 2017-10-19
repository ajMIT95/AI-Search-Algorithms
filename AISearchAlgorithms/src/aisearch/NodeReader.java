package aisearch;
import java.io.*;

public class NodeReader {
	
	private FileReader reader;
	private BufferedReader input;
	
	// Opening the text file
	public int open(String configuration) throws Exception {
		int numNodes;
		reader = new FileReader(configuration);
		input = new BufferedReader(reader);
		
		// Returning the number of nodes in the file
		numNodes = Integer.parseInt(input.readLine());
		return numNodes;
	}
	
	// Closing the text file
	public void close() throws Exception {
		input.close();
	}
	
	// Reading the information of a node in the text file
	public Node read() throws Exception {
		Node node = null;
		String line;
		String[] lineItems;
		int numSuccessors, i;
		
		// line 1: node id
		// line 2: position x, position y
		// line 3: value
		// line 4: isGoal
		// line 5: numSuccessors(N)
		// line 6: idSuccesor1, costPath1
		// ...  
		// line 6+N: idSuccesorN, costPathN 
		line = input.readLine(); // line 1
		
		
		if(line != null) {
			node = new Node(line);
			line = input.readLine(); // line 2
			lineItems = line.split(",");
			node.setPositionX(Integer.parseInt(lineItems[0])); // position x
			node.setPositionY(Integer.parseInt(lineItems[1])); // position y
			node.setValue(Double.parseDouble(input.readLine())); // line 3
			line = input.readLine(); // line 4
			if(line.equals("YES"))
				node.setIsGoal(true);
			
			numSuccessors = Integer.parseInt(input.readLine()); // line 5
			i = 0;
			// line 6 to line 6+N
			while (i < numSuccessors) {
				line = input.readLine();
				lineItems = line.split(",");
				node.addSuccessor(lineItems[0], Double.parseDouble(lineItems[1]));
				i++;
				System.out.println(node.toString() + "/" + lineItems[0]);
			}
					
		}
		
		return node;
	}
	

}
