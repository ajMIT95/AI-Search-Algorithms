package aisearch;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Date;

import aisearch.algorithms.BeamSearch;
import aisearch.algorithms.BreadthFirstSearch;
import aisearch.algorithms.DepthFirstSearch;
import aisearch.algorithms.HillClimbing;

public class AISearch extends Frame {

	private static final long serialVersionUID = 1L;
	
	Dimension screen;
	int width, height, x, y;
	
	MenuBar MMenu;
	MenuItem MOpen, MExit, MAbout, MClean;
	Menu MTimer;
	CheckboxMenuItem CMITimer, CMIStep, CMILegend;
	CheckboxMenuItem CMIVeryFast, CMIFast, CMIMedium, CMISlow;
	Choice CSearch;
	Button BRun, BRestart;
	TextArea TAInformation;
	Panel PNorth, PSouth;
	FileDialog FDOpen;
	Dialog DAbout;
	
	private final Graph graph;
	private DepthFirstSearch dfs;
	private BreadthFirstSearch bfs;
	private HillClimbing hc;
	private BeamSearch beamSearch;
	//private AStar ae;
	
	public static void main(String[] args) {
		File graphFile = null;
		if(args.length != 1)
		{	
		    System.err.println("usage: ./AISeach.java graphFile");
			System.exit(1);
		}
		
		graphFile = new File(args[0]);
		new AISearch(graphFile);
	}
	
	public AISearch(File graphFile) {
		// Main Window
		super("AI-Search-Algorithms");
		this.setLayout(new BorderLayout());
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// Main Window's Menu
		MMenu = new MenuBar();
		Menu mFile = new Menu("File");
		Menu mView = new Menu("View"); 
		Menu mRun = new Menu("Run");
		Menu mHelp = new Menu("Help");
		
		// File Menu
		MOpen = new MenuItem("Open");
		MExit = new MenuItem("Exit");
		mFile.add(MOpen);
		mFile.addSeparator();
		mFile.add(MExit);
		
		// View Menu
		CMILegend = new CheckboxMenuItem("Legend for nodes");
		CMILegend.setState(true);
		MClean = new MenuItem("Clean message panel");
		mView.add(CMILegend);
		mView.addSeparator();
		mView.add(MClean);
		
		// Run Menu
		CMIStep = new CheckboxMenuItem("Step by step");
		CMIStep.setState(true);
		CMITimer = new CheckboxMenuItem("Timer");
		CMITimer.setState(false);
		mRun.add(CMIStep);
		mRun.add(CMITimer);
		
		// Speed Timer Submenu inside Run Menu
		MTimer = new Menu("Speed Timer");
		MTimer.setEnabled(false);
		CMIVeryFast = new CheckboxMenuItem("Very fast (0s)");
		CMIVeryFast.setState(false);
		CMIFast = new CheckboxMenuItem("Fast (0.1s)");
		CMIFast.setState(false);
		CMIMedium = new CheckboxMenuItem("Medium (0.5s)");
		CMIMedium.setState(false);
		CMISlow = new CheckboxMenuItem("Slow (1s)");
		CMISlow.setState(false);
		MTimer.add(CMIVeryFast);
		MTimer.add(CMIFast);
		MTimer.add(CMIMedium);
		MTimer.add(CMISlow);
		mRun.add(MTimer);
		
		// Help Menu
		MAbout = new MenuItem("About AI-Search-Algoritms");
		mHelp.add(MAbout);
		
		// Adding the menus to the menuBar
		MMenu.add(mFile);
		MMenu.add(mView);
		MMenu.add(mRun);
		MMenu.add(mHelp);
		
		// Listeners of all MenuItems and CheckboxMenuItems
		MOpen.addActionListener(new ControlMenuItem());
		MExit.addActionListener(new ControlMenuItem());
		
		CMILegend.addItemListener(new ControlCheckBox());
		MClean.addActionListener(new ControlMenuItem());
		
		CMIStep.addItemListener(new ControlCheckBox());
		CMITimer.addItemListener(new ControlCheckBox());
	
		CMIVeryFast.addItemListener(new ControlCheckBox());
		CMIFast.addItemListener(new ControlCheckBox());
		CMIMedium.addItemListener(new ControlCheckBox());
		CMISlow.addItemListener(new ControlCheckBox());
		
		MAbout.addActionListener(new ControlMenuItem());
		
		
		// File Dialog to open the graph file
		FDOpen = new FileDialog(this, "Open Graph File", FileDialog.LOAD);
		
		
		// Dialog to show the about info
		DAbout = new Dialog(this, "About");
		DAbout.setLayout(new GridLayout(12, 1));
		screen = Toolkit.getDefaultToolkit().getScreenSize();
		width = 400;
		height = 250;
		x = (screen.width - width) / 2;
		y = (screen.height - height) / 2;
		DAbout.setBounds(x, y, width, height);
		DAbout.setResizable(false);
		DAbout.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DAbout.setVisible(false);
			}
		});
		
		DAbout.add(new Label(""));
		DAbout.add(new Label("AI Search Algorithms Animation Software"));
		DAbout.add(new Label(""));
		DAbout.add(new Label(""));
		
		// Selection List
		CSearch = new Choice();
		CSearch.addItem(" Depth-First Search");
		CSearch.addItem(" Breadth-First Search");
		CSearch.addItem(" Hill Climbing");
		CSearch.addItem(" Beam Search");
		CSearch.addItem(" Best First Search");
		CSearch.addItem(" A* Search");
		CSearch.addItemListener(new ControlChoice());
		CSearch.setEnabled(false);
		
		// Buttons
		BRun = new Button(" Fine step ");
		BRestart = new Button(" Reset search ");
		BRun.setEnabled(false);
		BRestart.setEnabled(false);
		BRun.addActionListener(new ControlButton());
		BRestart.addActionListener(new ControlButton());
		
		// Text Area
		TAInformation = new TextArea("", 5, 70, TextArea.SCROLLBARS_VERTICAL_ONLY);
		TAInformation.setEditable(false);
		TAInformation.setBackground(Color.white);
		
		// Panels
		PNorth = new Panel();
		PNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
		PNorth.setBackground(Color.lightGray);
		PNorth.add(CSearch);
		PNorth.add(BRun);
		PNorth.add(BRestart);
		
		graph = new Graph();
		graph.setBackground(new Color(90, 138, 212));
		
		PSouth = new Panel();
		PSouth.setLayout(new BorderLayout());
		PSouth.setBackground(Color.lightGray);
		PSouth.add("North", new Label("Message panel"));
		PSouth.add("Center", TAInformation);
		
		// Adding the menuBar and the three panels to the main window
		this.setMenuBar(MMenu);
		this.add("North", PNorth);
		this.add("Center", graph);
		this.add("South", PSouth);
		
		this.setVisible(true);
		
		if (graphFile != null)
			loadGraph(graphFile.getAbsolutePath());
	}
	
	private void loadGraph(String path) {
		try {
			graph.loadGraph(path);
			TAInformation.append((new Date()).toString() + ": Graph file successfully loaded.\n");
			
			// Search Algorithm by default
			CSearch.select(0);
			dfs = new DepthFirstSearch(graph);
			TAInformation.append("\n" + (new Date()).toString() + ": Depth-First Search algorithm selected.\n");
			
			// Activating the buttons
			CSearch.setEnabled(true);
			BRun.setEnabled(true);
			BRestart.setEnabled(true);
			
			// Repainting the graph
			graph.repaint();
		} catch (Exception e) {
			CSearch.setEnabled(false);
			BRun.setEnabled(false);
			BRestart.setEnabled(false);
			TAInformation.append((new Date()).toString() + ": Error reading graph from file.\n");
		}
		
	}
	
	// Classes to control events
	private class ControlMenuItem implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource().equals(MOpen)) {
				FDOpen.setVisible(true);
				if (FDOpen.getFile() != null) {
					String path = FDOpen.getDirectory() + FDOpen.getFile();
					setTitle("AI-Search Algorithms - " + FDOpen.getFile());
					loadGraph(path);
				}
			} else if (e.getSource().equals(MClean)) {
				TAInformation.setText("");
			} else if (e.getSource().equals(MAbout)) {
				DAbout.setVisible(true);
			} else if (e.getSource().equals(MExit)) {
				System.exit(0);
			}
		}
	}
	
	private class ControlCheckBox implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource().equals(CMILegend)) {
				graph.showLegend(CMILegend.getState());
				graph.repaint();
			}
			else if (e.getSource().equals(CMIStep)) {
				CMITimer.setState(false);
				MTimer.setEnabled(false);
				BRun.setLabel("Next step");
			}
			else if (e.getSource().equals(CMITimer)) {
				CMIStep.setState(false);
				MTimer.setEnabled(true);
				BRun.setLabel("Start");
			}
			else if (e.getSource().equals(CMIVeryFast)) {
				graph.setTimer(0);
			}
			else if (e.getSource().equals(CMIFast)) {
				graph.setTimer(100);
			}
			else if (e.getSource().equals(CMIMedium)) {
				graph.setTimer(500);
			}
			else if (e.getSource().equals(CMISlow)) {
				graph.setTimer(1000);
			}
		}
	}
	
	private class ControlChoice implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			
			// Resetting the graph and the algorithms
			graph.reset();
			
			// Applying the algorithm selected
			switch(((Choice)e.getSource()).getSelectedIndex()) {
				case 0:
					dfs = new DepthFirstSearch(graph);
					TAInformation.append("\n" + (new Date()).toString() + ": Depth-First Search algorithm selected.\n");
					break;
				case 1:
					bfs = new BreadthFirstSearch(graph);
					TAInformation.append("\n" + (new Date()).toString() + ": Breadth-First Search algorithm selected.\n");
					break;
				case 2:
					hc = new HillClimbing(graph);
					TAInformation.append("\n" + (new Date()).toString() + ": Hill Climbing Search algorithm selected.\n");
					break;
				case 3:
					beamSearch = new BeamSearch(graph, 2);
					TAInformation.append("\n" + (new Date()).toString() + ": Beam Search algorithm selected.\n");
					break;
			}
			
			// Repainting the graph
			graph.repaint();
			
			// Activating the execution button
			BRun.setEnabled(true);
		}
	}
	
	private class ControlButton implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String str;
			
			if (e.getSource().equals(BRun)) {
				switch(CSearch.getSelectedIndex()) {
					case 0:
						if (CMIStep.getState()) {
							// show execution
							str = dfs.getStack();
							if (!str.isEmpty())
								TAInformation.append((new Date()).toString() + ": " + str + "\n");
							if (!dfs.step())
								update("Depth-First Search");
						}
						else {
							dfs.run();
							update("Depth-First Search");
						}
						break;		
					case 1:
						if (CMIStep.getState()) {
							// show execution
							str = bfs.getQueue();
							if (!str.isEmpty())
								TAInformation.append((new Date()).toString() + ": " + str + "\n");
							if (!bfs.step())
								update("Breadth-First Search");
						}
						else {
							bfs.run();
							update("Breadth-First Search");
						}
						break;
					case 2:
						if (CMIStep.getState()) {
							// show execution
							str = hc.getStack();
							if (!str.isEmpty())
								TAInformation.append((new Date()).toString() + ": " + str + "\n");
							if (!hc.step())
								update("Hill Climbing Search");
						}
						else {
							hc.run();
							update("Hill Climbing Search");
						}
						break;
					case 3:
						if (CMIStep.getState()) {
							// show execution
							str = beamSearch.getQueue();
							if (!str.isEmpty())
								TAInformation.append((new Date()).toString() + ": " + str + "\n");
							if (!beamSearch.step())
								update("Beam Search");
						}
						else {
							beamSearch.run();
							update("Beam Search");
						}
						break;/*
					case 4:
						if (CMIStep.getState()) {
							str = bf.getOpened();
							if (!str.isEmpty())
								TAInformation.append((new Date()).toString() + ": " + str + "\n");
							str = bf.getClosed();
							if (!str.isEmpty())
								TAInformation.append((new Date()).toString() + ": " + str + "\n");
							if (!bf.step())
								update("Best First Search");
						}
						else {
							bf.run();
							update("Best First Search");
						}
						break;
					case 5:
						if (CMIStep.getState()) {
							str = ae.getOpened();
							if (!str.isEmpty())
								TAInformation.append((new Date()).toString() + ": " + str + "\n");
							str = ae.getClosed();
							if (!str.isEmpty())
								TAInformation.append((new Date()).toString() + ": " + str + "\n");
							if (!ae.step())
								update("A* Search");
						}
						else {
							ae.run();
							update("A* Search");
						}
						break;*/	
				}
				graph.repaint();
			}
			else if (e.getSource().equals(BRestart)) {
				graph.reset();
				switch (CSearch.getSelectedIndex()) {
					case 0:
						dfs.reset();
						TAInformation.append((new Date()).toString() + ": Depth-First Search algorithm restarted.\n");
						break;
					case 1:
						bfs.reset();
						TAInformation.append((new Date()).toString() + ": Breadth-First Search algorithm restarted.\n");
						break;
					case 2:
						hc.reset();
						TAInformation.append((new Date()).toString() + ": Hill Climbing algorithm restarted.\n");
						break;
					case 3:
						beamSearch.reset();
						TAInformation.append((new Date()).toString() + ": Beam Search algorithm restarted.\n");
						break;
					case 4:
						//bf.reset();
						TAInformation.append((new Date()).toString() + ": Best First Search algorithm restarted.\n");
						break;
					case 5:
						//ae.reset();
						TAInformation.append((new Date()).toString() + ": A* Search algorithm restarted.\n");
						break;
				}
				graph.repaint();
				
				// Activating the execution button
				BRun.setEnabled(true);
			}
			
		}
	}
	
	private void update(String algorithm) {
		// show statistics
		String[] statistics = graph.getStatistics();
		TAInformation.append((new Date()).toString() + ": " + algorithm + " algorithm statistics.\n");
		for (String stat : statistics)
			TAInformation.append((new Date()).toString() + ": " + stat + "\n");
        
		// Deactivating the execution button
		BRun.setEnabled(false);
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
