import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.*;
import org.graphstream.ui.layout.*;
import org.graphstream.ui.layout.springbox.implementations.*;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.util.InteractiveElement;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.graphstream.ui.swing_viewer.DefaultView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.opencsv.CSVWriter;

import java.time.Instant;
import java.time.Duration;

public class LiveCerebro implements MouseListener {
	//TODO: call init in the button push. remove for loop of feeding times.
	private static final int[] FEEDING_TIMES = {0, 10, 30, 50, 100, 200, 300, 500};
	private static int index = 0;
	// Graph structure for manipulating the graph such as nodes and edges
	private static Graph graph;
	// Panel on a graphic graph
	private static DefaultView view;
	private static Viewer viewer;
	// Layout to compute nodes' best possible positions
	private static Layout layout;

	// Files for creating the output CSV file
	private static File file;
	private static FileWriter outputfile;
	private static CSVWriter writer;
	// Objects for parsing JSON
	private static Object jsonParser;
	private static JSONObject jsonObject;
	private Instant starts;
	private Instant ends;
	private JButton button;
	private JFrame frame;

	// Map for storing keys of class name & method name and values of colors
	private HashMap<String, String> colors;

	// Tracks edge counts to display edge width
	private HashMap<String, Integer> edgeCountMap;

	// Palette of generated colors
	private ArrayList<String> palette = new ArrayList<>();
	// Index to keep track of next assignable color
	private int paletteIndex;

	public LiveCerebro(String inputJson){
		try {
			// Initialize variables
			edgeCountMap = new HashMap<>();
			colors = new HashMap<>();
			paletteIndex = 0;
			file = new File("output.csv");
			outputfile = new FileWriter(file);
			writer = new CSVWriter(outputfile);
			jsonParser = new JSONParser().parse(new FileReader(inputJson));
			jsonObject = (JSONObject) jsonParser;

			// Generate color palette
			generatePalette();

			// Add headers to CSV file
			String[] header = { "Feeding", "Stabilizing"};
			writer.writeNext(header);

			// Feed in times
			//for(int i = 0; i < FEEDING_TIMES.length; i++){
				feed(0);

			//writer.close();

		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

	/*
		Initializes the graph, view, and layout
	 */
	private void init(){
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(800,700));
		frame.setLayout(new FlowLayout());
		graph = new DefaultGraph("Live Cerebro");
		System.setProperty("org.graphstream.ui", "swing");
		viewer = graph.display();
		view = (DefaultView) viewer.getDefaultView();
		view.setPreferredSize(new Dimension(700,600));
		frame.add(view);
		button = new JButton("Timer");

		frame.add(button);
		view.openInAFrame(false);
		frame.pack();

		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//		layout = new SpringBox(false);
//		graph.addSink(layout);
//		layout.addAttributeSink(graph);
//		layout.setStabilizationLimit(0.9);

		view.enableMouseOptions();
		viewer.enableAutoLayout(layout);
		view.addListener("Mouse", (MouseListener) this);

		//graph.addAttribute("ui.quality");
		graph.setAttribute("stylesheet", "graph { fill-color: black; }");
	}

	/*
		Helper method to create graph, nodes, edges and feed in edge creation delay
	 */
	private void feed(int feedingTime) {
		try {
			init();
			System.out.println(feedingTime);
			// Getting JSON array of nodes
			JSONArray arr = (JSONArray) jsonObject.get("nodes");

			for (int i = 0; i < arr.size(); i++) {
				// Getting each node in array
				JSONObject nodeObj = (JSONObject) arr.get(i);
				Thread.sleep(1);

				// Getting class name, method name, and ID
				String className = String.valueOf(nodeObj.get("className"));
				String methodName = String.valueOf(nodeObj.get("methodName"));
				String node = String.valueOf(nodeObj.get("id"));
				// If the map does not contain the class name and method name, add in a new color
				// Else, use the old color
				if (!colors.containsKey(className + " " + methodName)) {
					colors.put(className + " " + methodName, palette.get(paletteIndex++));
					graph.addNode(node);
					Node n = graph.getNode(node);
					n.setAttribute("ui.style", "fill-color: " + colors.get(className + " " + methodName) + ";");
				} else {
					graph.addNode(node);
					Node n = graph.getNode(node);
					n.setAttribute("ui.style", "fill-color: " + colors.get(className + " " + methodName) + ";");
				}
			}

			// Getting JSON array of links
			arr = (JSONArray) jsonObject.get("links");
			// Start the timer
			starts = Instant.now();

			for (int i = 0; i < arr.size(); i++) {
				// Getting each link the array
				JSONObject linkObj = (JSONObject) arr.get(i);

				// Getting start and end nodes
				String node1 = String.valueOf(linkObj.get("startId"));
				String node2 = String.valueOf(linkObj.get("endId"));

				// Delay for specified feeding time
				Thread.sleep(feedingTime);

				// If edge does not exist, add it in
				// Else, update the weight and size of edge
				if (graph.getEdge(node1 + " " + node2) == null) {
					edgeCountMap.put(node1 + " " + node2, 1);
					graph.addEdge(node1 + " " + node2, node1, node2, true);
					Edge edge = graph.getEdge(node1 + " " + node2);
					double weight = (5 - (edgeCountMap.get(node1 + " " + node2)))/Collections.max(edgeCountMap.values()) ;
					edge.setAttribute("layout.weight", weight);
					edge.setAttribute("ui.style", "size: 2px; fill-color: rgba(255,255,255,68);");
				} else {
					edgeCountMap.put(node1 + " " + node2, edgeCountMap.get(node1 + " " + node2) + 1);
					int count = edgeCountMap.get(node1 + " " + node2);
					Edge edge = graph.getEdge(node1 + " " + node2);
					double weight = (5 - (count))/Collections.max(edgeCountMap.values()) ;
					edge.setAttribute("layout.weight", weight);
					String size = String.valueOf(2 * count);
					String str = "size: " + size + "px; fill-color: rgba(255,255,255,68);";
					edge.setAttribute("ui.style", str);
				}

			}
//			// Compute layout while layout has not reached stabilization limit
//			while (layout.getStabilization() < 0.9) {
//				layout.compute();
//			}

			// End the timer after graph has stabilized
//			Instant ends = Instant.now();
//			System.out.println("Seconds taken: " + Duration.between(starts, ends).getSeconds());
//
//			// Add to the CSV output file
//			String[] data1 = {String.valueOf(feedingTime), String.valueOf(Duration.between(starts, ends).getSeconds())};
//			writer.writeNext(data1);
//			writer.flush();

				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ends = Instant.now();
						System.out.println("Seconds taken: " + Duration.between(starts, ends).getSeconds());

						// Add to the CSV output file
						String[] data1 = {String.valueOf(feedingTime), String.valueOf(Duration.between(starts, ends).getSeconds())};
						writer.writeNext(data1);
						try {
							writer.flush();

							writer.close();

						} catch(Exception endTimer){
							System.out.println("hi");
						}

					}
				} );

		}
		catch(InterruptedException e){
			System.out.println("Interrupted Exception");
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}

	/*
		Generate palette in three positions to ensure palette is filled with all colors
	 */
	public void generatePalette(){
		generateRgbValues(0);
		generateRgbValues(1);
		generateRgbValues(2);
	}

	/*
		Helper function to generate colors depending on position
	 */
	private void generateRgbValues(int pos){
		String rgbValue = "";
		for(int i = 5; i <= 255; i += 10 ){
			if(pos == 0){
				rgbValue = "rgba(" + i + ",255,255,228)";
			} else if (pos == 1){
				rgbValue = "rgba(255," + i + ",255,228)";
			} else{
				rgbValue = "rgba(255,255," + i + ",228)";
			}
			palette.add(rgbValue);
		}

		for(int i = 5; i <= 255; i += 10){
			if(pos == 0){
				rgbValue = "rgba(" + i + "," + i + ",255,228)";
			} else if (pos == 1){
				rgbValue = "rgba(255," + i + "," + i + ",228)";
			} else{
				rgbValue = "rgba(" + i + ",255," + i + ",228)";
			}
			palette.add(rgbValue);
		}

		for(int i = 20; i <= 255; i += 10){
			if(pos == 0){
				rgbValue = "rgba(" + i + "," + i + "," + i + ",228)";
			} else if (pos == 1){
				rgbValue = "rgba(" + i + "," + i + "," + i + ",228)";
			} else{
				rgbValue = "rgba(" + i + "," + i + "," + i + ",228)";
			}
			palette.add(rgbValue);
		}
	}

	/*
		Returns when mouse is pressed
	 */
	public void mousePressed(MouseEvent e)
	{
		return;
	}

	/*
		Returns when the mouse is released
	 */
	public void mouseReleased(MouseEvent e)
	{
		return;
	}

	/*
	 	Returns when the mouse exits the component
	 */
	public void mouseExited(MouseEvent e)
	{
		return;
	}

	/*
		Returns when the mouse enters the component
	 */
	public void mouseEntered(MouseEvent e)
	{
		return;
	}

	/*
		Outputs ID of node that was clicked
	 */
	public void mouseClicked(MouseEvent e)
	{
		try {
			EnumSet<InteractiveElement> set = EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE);
			GraphicElement ge = view.findGraphicElementAt(set, e.getX(), e.getY());
			System.out.println("Clicked: " + ge.getId());
		}
		catch (Exception exception){
			System.out.println("Please select an element.");
		}
	}
}