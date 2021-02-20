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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.opencsv.CSVWriter;

import java.time.Instant;
import java.time.Duration;

public class GraphExample implements MouseListener{
	static View view;
	static Layout layout;
	static FileWriter outputfile;
	static File file;
	static CSVWriter writer;
	Instant starts;
	HashMap<String, String> colors;
//	String[] palette = {"rgba(0,255,255,228)", "rgba(25,255,255,228)", "rgba(50,255,255,228)", "rgba(75,255,255,228)","rgba(100,255,255,228)",
//			"rgba(125,255,255,228)","rgba(150,255,255,228)", "rgba(175,255,255,228)", "rgba(200,255,255,228)", "rgba(255,255,255,228)",
//			"rgba(255,25,255,228)", "rgba(255,75,255,228)", "rgba(255,100,255,228)", "rgba(255,125,255,228)", "rgba(255,150,255,228)",
//			"rgba(255,175,255,228)", "rgba(255,200,255,228)", "rgba(255,225,255,228)",
//			"rgba(255,0,255,228)", "rgba(255,50,255,228)",
//			"rgba(0,255,255,228)","rgba(25,255,255,228)", "rgba(75,255,255,228)","rgba(100,255,255,228)", "rgba(125,255,255,228)",
//			"rgba(175,255,255,228)", "rgba(200,255,255,228)", "rgba(225,255,255,228)",
//			"rgba(150,255,255,228)", "rgba(255,100,255,228)", "rgba(255,150,255,228)"};
	ArrayList<String> palette = new ArrayList<>();
	int paletteIndex = 0;
	HashMap<String, Integer> edgeCountMap;
	boolean addedNodes = false;

	static Graph graph;
	public static void main(String args[]) {
		int[] feedingTimes = {0, 10, 30, 50, 100, 200, 300};
		try{
			file = new File("output.csv");
			outputfile = new FileWriter(file);

			// create CSVWriter object filewriter object as parameter
			writer = new CSVWriter(outputfile);
			String[] header = { "Feeding", "Stabilizing"};
			writer.writeNext(header);
			for(int i = 0; i < feedingTimes.length; i++){

				new GraphExample(feedingTimes[i]);

			}

			writer.close();
		} catch(Exception writEx){
			System.out.println("Writer exception");
		}

	}

	public void generatePalette(){
		generateRgbValues(0);
		generateRgbValues(1);
		generateRgbValues(2);
	}

	private void generateRgbValues(int pos){
		String rgbValue = "";
		for(int i = 0; i <= 255; i += 10 ){
			if(pos == 0){
				rgbValue = "rgba(" + i + ",255,255,228)";
			} else if (pos == 1){
				rgbValue = "rgba(255," + i + ",255,228)";
			} else{
				rgbValue = "rgba(255,255," + i + ",228)";
			}
			palette.add(rgbValue);
			System.out.println(rgbValue);
		}

		for(int i = 0; i <= 255; i += 10){
			if(pos == 0){
				rgbValue = "rgba(" + i + "," + i + ",255,228)";
			} else if (pos == 1){
				rgbValue = "rgba(255," + i + "," + i + ",228)";
			} else{
				rgbValue = "rgba(" + i + ",255," + i + ",228)";
			}
			palette.add(rgbValue);
			System.out.println(rgbValue);
		}

		for(int i = 0; i <= 254; i += 10){
			if(pos == 0){
				rgbValue = "rgba(" + i + "," + i + "," + i + ",228)";
			} else if (pos == 1){
				rgbValue = "rgba(" + i + "," + i + "," + i + ",228)";
			} else{
				rgbValue = "rgba(" + i + "," + i + "," + i + ",228)";
			}
			palette.add(rgbValue);
			System.out.println(rgbValue);
		}
	}

	public GraphExample(int feedingTime){
		graph = new SingleGraph("Live Cerebro");
		edgeCountMap = new HashMap<>();
		colors = new HashMap<>();
		generatePalette();
//		palette = {"rgba(0,255,255,228)", "rgba(25,255,255,228)", "rgba(50,255,255,228)", "rgba(75,255,255,228)","rgba(100,255,255,228)",
//				"rgba(125,255,255,228)","rgba(150,255,255,228)", "rgba(175,255,255,228)", "rgba(200,255,255,228)", "rgba(255,255,255,228)",
//				"rgba(255,25,255,228)", "rgba(255,75,255,228)", "rgba(255,100,255,228)", "rgba(255,125,255,228)", "rgba(255,150,255,228)",
//				"rgba(255,175,255,228)", "rgba(255,200,255,228)", "rgba(255,225,255,228)",
//				"rgba(255,0,255,228)", "rgba(255,50,255,228)",
//				"rgba(0,255,255,228)","rgba(25,255,255,228)", "rgba(75,255,255,228)","rgba(100,255,255,228)", "rgba(125,255,255,228)",
//				"rgba(175,255,255,228)", "rgba(200,255,255,228)", "rgba(225,255,255,228)",
//				"rgba(150,255,255,228)", "rgba(255,100,255,228)", "rgba(255,150,255,228)"};
		 paletteIndex = 0;
		System.setProperty("org.graphstream.ui", "swing");

		String prevNode1 = "";
		String prevNode2 = "";



		try {

			// adding header to csv

//			feed(100);
//			feed(300);
//			feed(500);
//			feed(700);
//			feed(1000);
//			feed(2000);
//			feed(5000);
//			feed(10000);
			feed(feedingTime);

		}
//		catch(InterruptedException e){
//			System.out.println("Interrupted Exception");
//		}
		catch(Exception e) {
			//  Block of code to handle errors
			System.out.println(e );
		}

	}

	public void feed(int feedingTime){
		Viewer viewer;
		try {

			viewer = graph.display();
			view = viewer.getDefaultView();
			layout = new SpringBox(false);
			graph.addSink(layout);
			layout.addAttributeSink(graph);
			layout.setStabilizationLimit(0.6);
			System.out.println("limit " + layout.getStabilizationLimit());

			view.enableMouseOptions();
			viewer.enableAutoLayout(layout);
			view.addListener("Mouse", (MouseListener) this);
			graph.clear();
			graph.setAttribute("stylesheet", "graph { fill-color: black; }" +

					"node { fill-mode: gradient-radial; fill-color: rgba(200,255,255,228), rgba(200,255,255,8); }");

			Object obj = new JSONParser().parse(new FileReader("jpacman-1.json"));

			// typecasting obj to JSONObject
			JSONObject jo = (JSONObject) obj;

			JSONArray arr = (JSONArray) jo.get("nodes");

			for (int i = 0; i < arr.size(); i++) {
				JSONObject entry = (JSONObject) arr.get(i);
				Thread.sleep(1);

				String className = String.valueOf(entry.get("className"));
				String methodName = String.valueOf(entry.get("methodName"));
				String node = String.valueOf(entry.get("id"));
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

			arr = (JSONArray) jo.get("links");
			starts = Instant.now();

			for (int i = 0; i < arr.size(); i++) {
				JSONObject entry = (JSONObject) arr.get(i);

				String node1 = String.valueOf(entry.get("startId"));
				String node2 = String.valueOf(entry.get("endId"));

				Thread.sleep(feedingTime);
				if (graph.getEdge(node1 + " " + node2) == null) {
					edgeCountMap.put(node1 + " " + node2, 1);
					graph.addEdge(node1 + " " + node2, node1, node2, true);
					Edge edge = graph.getEdge(node1 + " " + node2);
					double weight = (3.3 - edgeCountMap.get(node1 + " " + node2));
					edge.setAttribute("layout.weight", weight);
					edge.setAttribute("ui.style", "size: 2px; fill-color: rgba(255,255,255,68);");
				} else {
					// compare this to the max and if getting it + 1 is greater, then update max
					edgeCountMap.put(node1 + " " + node2, edgeCountMap.get(node1 + " " + node2) + 1);
					int count = edgeCountMap.get(node1 + " " + node2);
					Edge edge = graph.getEdge(node1 + " " + node2);
					double weight = 3.3 - count;
					edge.setAttribute("layout.weight", weight);
					String size = String.valueOf(2 * count);
					String str = "size: " + size + "px; fill-color: rgba(255,255,255,68);";
					edge.setAttribute("ui.style", str);
				}

			}
			while (layout.getStabilization() < .6) {
				layout.compute();
			}
			Instant ends = Instant.now();
			System.out.println("Seconds taken: " + Duration.between(starts, ends).getSeconds());
			String[] data1 = {String.valueOf(feedingTime), String.valueOf(Duration.between(starts, ends).getSeconds())};
			writer.writeNext(data1);
			writer.flush();

		}
		catch(InterruptedException e){
			System.out.println("Interrupted Exception");
		}
		catch(Exception e) {
			//  Block of code to handle errors
			System.out.println(e);
		}


	}

	// getX() and getY() functions return the
	// x and y coordinates of the current
	// mouse position
	// getClickCount() returns the number of
	// quick consecutive clicks made by the user

	// this function is invoked when the mouse is pressed
	public void mousePressed(MouseEvent e)
	{

	}

	// this function is invoked when the mouse is released
	public void mouseReleased(MouseEvent e)
	{

	}

	// this function is invoked when the mouse exits the component
	public void mouseExited(MouseEvent e)
	{

	}

	// this function is invoked when the mouse enters the component
	public void mouseEntered(MouseEvent e)
	{

	}

	// this function is invoked when the mouse is pressed or released
	public void mouseClicked(MouseEvent e)
	{
		try {
			EnumSet<InteractiveElement> set = EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE);
			GraphicElement ge = view.findGraphicElementAt(set, e.getX(), e.getY());
			System.out.println(ge.getId());
		}
		catch (Exception exception){
			System.out.println("Please select an element.");
		}
		// getClickCount gives the number of quick,
		// consecutive clicks made by the user
		// show the point where the mouse is i.e
//		// the x and y coordinates
//		System.out.println("mouse clicked at point:"
//				+ e.getX() + " "
//				+ e.getY() + "mouse clicked :" + e.getClickCount());
	}
}