import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.*;
import org.graphstream.ui.view.View;
import org.graphstream.ui.layout.*;
import org.graphstream.ui.layout.springbox.implementations.*;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseListener;
import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.time.Instant;
import java.time.Duration;

public class GraphExample implements MouseListener{
	static View view;
	static Layout layout;

	static Graph graph;
	public static void main(String args[]) {
		new GraphExample();
	}

	public GraphExample(){
		graph = new SingleGraph("Live Cerebro");
		HashMap<String, Integer> edgeCountMap = new HashMap<>();
		HashMap<String, String> colors = new HashMap<>();
		String[] palette = {"rgba(0,255,255,228)", "rgba(25,255,255,228)", "rgba(50,255,255,228)", "rgba(75,255,255,228)","rgba(100,255,255,228)",
				"rgba(125,255,255,228)","rgba(150,255,255,228)", "rgba(175,255,255,228)", "rgba(200,255,255,228)", "rgba(255,255,255,228)",
				"rgba(255,25,255,228)", "rgba(255,75,255,228)", "rgba(255,100,255,228)", "rgba(255,125,255,228)", "rgba(255,150,255,228)",
				"rgba(255,175,255,228)", "rgba(255,200,255,228)", "rgba(255,225,255,228)",
				"rgba(255,0,255,228)", "rgba(255,50,255,228)",
				"rgba(0,255,255,228)","rgba(25,255,255,228)", "rgba(75,255,255,228)","rgba(100,255,255,228)", "rgba(125,255,255,228)",
				"rgba(175,255,255,228)", "rgba(200,255,255,228)", "rgba(225,255,255,228)",
				"rgba(150,255,255,228)", "rgba(255,100,255,228)", "rgba(255,150,255,228)"};
		int paletteIndex = 0;
		System.setProperty("org.graphstream.ui", "swing");

		String prevNode1 = "";
		String prevNode2 = "";

		// parsing file "JSONExample.json"
		Instant starts = Instant.now();
		try {
			Viewer viewer = graph.display();
			view = viewer.getDefaultView();
			//TODO: get layout, get stabilization. make while loop below last for loop that continues to get stabilization and then at a limit you print out the timer
			layout = new SpringBox(false);
			graph.addSink(layout);
			layout.addAttributeSink(graph);
			System.out.println("limit " + layout.getStabilizationLimit());
			layout.setStabilizationLimit(0.3);
			view.enableMouseOptions();
			view.addListener("Mouse", (MouseListener)this);
			graph.setAttribute("stylesheet", "graph { fill-color: black; }" +
					//	"node { fill-mode: dyn-plain; fill-color: red, blue; }");

					"node { fill-mode: gradient-radial; fill-color: rgba(200,255,255,228), rgba(200,255,255,8); }");
			// figure out how to fade the colors from darker to lighter red
			//					"node { text-color: white;" +"fill-mode: gradient-radial; fill-color: rgba(170,255,255,228), rgba(170,255,255,8); }");


			Object obj = new JSONParser().parse(new FileReader("AbstractThings.trc-springbox.json"));

			// typecasting obj to JSONObject
			JSONObject jo = (JSONObject) obj;

			JSONArray arr = (JSONArray) jo.get("nodes");
			for (int i = 0; i < arr.size(); i++) {
				JSONObject entry = (JSONObject) arr.get(i);
				Thread.sleep(1);

				String className = String.valueOf(entry.get("className"));
				String methodName = String.valueOf(entry.get("methodName"));
				String node = String.valueOf(entry.get("id"));
				if(!colors.containsKey(className + " " + methodName)){
					colors.put(className +" " + methodName, palette[paletteIndex++]);
					graph.addNode(node);
					Node n = graph.getNode(node);
					n.setAttribute("ui.style", "fill-color: " + colors.get(className + " " + methodName) + ";");

				} else{
					graph.addNode(node);
					Node n = graph.getNode(node);
					n.setAttribute("ui.style", "fill-color: " + colors.get(className + " " + methodName) + ";");
				}
			}
			arr = (JSONArray) jo.get("links");

			for (int i = 0; i < arr.size(); i++) {
				JSONObject entry = (JSONObject) arr.get(i);

				String node1 = String.valueOf(entry.get("startId"));
				String node2 = String.valueOf(entry.get("endId"));
				//String node1 = String.valueOf(arr.get(i).getJSONObject(i).getInt("startId"));
				//String node2 = String.valueOf(arr.getJSONObject(i).getInt("endId"));

				Thread.sleep(100);
//				if (prevNode1.length() == 0) {
//					graph.addNode(node1);
//					Node n = graph.getNode(node1);
//					n.setAttribute("ui.style", "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					graph.addNode(node2);
//					Node n2 = graph.getNode(node2);
//					n2.setAttribute("ui.style", "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					prevNode1 = node1;
//					prevNode2 = node2;
//				} else {
//					if (graph.getNode(node1) == null) {
//						graph.addNode(node1);
//						Node n = graph.getNode(node1);
//						n.setAttribute("ui.style", "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					} else if (graph.getNode(node1) != null) {
//						Node n1 = graph.getNode(node1);
//						n1.setAttribute("ui.style", "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					}
//					if (graph.getNode(node2) == null) {
//						graph.addNode(node2);
//						Node n = graph.getNode(node2);
//						n.setAttribute("ui.style", "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					} else if (graph.getNode(node2) != null) {
//						Node n2 = graph.getNode(node2);
//						n2.setAttribute("ui.style", "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					}
//					Node prevn1 = graph.getNode(prevNode1);
//					prevn1.setAttribute("ui.style", "fill-mode: gradient-radial; fill-color: rgba(255,0,0,228), rgba(255,0,0,8); ");
//					Node prevn2 = graph.getNode(prevNode2);
//					prevn2.setAttribute("ui.style", "fill-mode: gradient-radial; fill-color: rgba(255,0,0,228), rgba(255,0,0,8); ");
//
//					prevNode1 = node1;
//					prevNode2 = node2;
//				}

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
			while(layout.getStabilization() < .9){
				layout.compute();
				System.out.println(layout.getStabilization());
			}
		}
		catch(InterruptedException e){
			System.out.println("Interrupted Exception");
		}
		catch(Exception e) {
			//  Block of code to handle errors
			System.out.println(e );
		}

		Instant ends = Instant.now();
		System.out.println("Seconds taken: " + Duration.between(starts, ends).getSeconds());

//		File lines = new File("output.txt");
//		graph = new SingleGraph("Live Cerebro");
//		HashMap<String, Integer> edgeCountMap = new HashMap<>();
//		System.setProperty("org.graphstream.ui", "swing");
//
//		String prevNode1 = "";
//		String prevNode2 = "";
//		try{
//			Scanner in = new Scanner(lines);
//			Viewer viewer = graph.display();
//			view = viewer.getDefaultView();
//			view.enableMouseOptions();
//			view.addListener("Mouse", (MouseListener)this);
//			graph.setAttribute("stylesheet", "graph { fill-color: black; }" +
//						//	"node { fill-mode: dyn-plain; fill-color: red, blue; }");
//
//					"node { fill-mode: gradient-radial; fill-color: rgba(200,255,255,228), rgba(200,255,255,8); }");
//			// figure out how to fade the colors from darker to lighter red
//			//					"node { text-color: white;" +"fill-mode: gradient-radial; fill-color: rgba(170,255,255,228), rgba(170,255,255,8); }");
//			boolean first = false;
//			while(in.hasNextLine()) {
//				String line = in.nextLine();
//				String[] lineArgs = line.split(" ");
//				String node1 = lineArgs[1];
//				String node2 = lineArgs[3];
//				Thread.sleep(300);
//				if(prevNode1.length() == 0){
//					graph.addNode(node1);
//					Node n = graph.getNode(node1);
//					n.setAttribute("ui.style",  "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					graph.addNode(node2);
//					Node n2 = graph.getNode(node2);
//					n2.setAttribute("ui.style",  "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					prevNode1 = node1;
//					prevNode2 = node2;
//				} else{
//					if (graph.getNode(node1) == null) {
//						graph.addNode(node1);
//						Node n = graph.getNode(node1);
//						n.setAttribute("ui.style",  "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					} else if (graph.getNode(node1) != null){
//						Node n1 = graph.getNode(node1);
//						n1.setAttribute("ui.style",  "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					}
//					if (graph.getNode(node2) == null) {
//						graph.addNode(node2);
//						Node n = graph.getNode(node2);
//						n.setAttribute("ui.style",  "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					} else if (graph.getNode(node2) != null){
//						Node n2 = graph.getNode(node2);
//						n2.setAttribute("ui.style",  "fill-mode: gradient-radial; fill-color: rgba(150,255,255,228), rgba(200,255,255,8); ");
//					}
//					Node prevn1 = graph.getNode(prevNode1);
//					prevn1.setAttribute("ui.style",  "fill-mode: gradient-radial; fill-color: rgba(255,0,0,228), rgba(255,0,0,8); ");
//					Node prevn2 = graph.getNode(prevNode2);
//					prevn2.setAttribute("ui.style",  "fill-mode: gradient-radial; fill-color: rgba(255,0,0,228), rgba(255,0,0,8); ");
//
//					prevNode1 = node1;
//					prevNode2 = node2;
//				}
//
//				if (graph.getEdge(node1 + " " + node2) == null) {
//					edgeCountMap.put(node1 + " " + node2, 1);
//					graph.addEdge(node1 + " " + node2, node1, node2);
//					Edge edge = graph.getEdge(node1 + " " + node2);
//					double weight = (3.3 - edgeCountMap.get(node1 + " " + node2));
//					edge.setAttribute("layout.weight", weight);
//					edge.setAttribute("ui.style", "size: 2px; fill-color: rgba(255,255,255,68);");}
//				 else {
//					// compare this to the max and if getting it + 1 is greater, then update max
//					edgeCountMap.put(node1 + " " + node2, edgeCountMap.get(node1 + " " + node2) + 1);
//					int count = edgeCountMap.get(node1 + " " + node2);
//					Edge edge = graph.getEdge(node1 + " " + node2);
//					double weight = 3.3 - count;
//					edge.setAttribute("layout.weight", weight);
//					String size = String.valueOf(2 * count);
//					String str = "size: " + size + "px; fill-color: rgba(255,255,255,68);";
//					edge.setAttribute("ui.style", str);
//
//				}
//
//			}
//		}
//		catch(InterruptedException e){
//			System.out.println("Error: Interrupted Exception");
//		}
//		catch(FileNotFoundException e){
//			System.out.println("Error: File Not Found Exception");
//		}

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
		EnumSet<InteractiveElement> set = EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE);
		GraphicElement ge = view.findGraphicElementAt(set, e.getX(), e.getY());
		System.out.println(ge.getId());
		// getClickCount gives the number of quick,
		// consecutive clicks made by the user
		// show the point where the mouse is i.e
//		// the x and y coordinates
//		System.out.println("mouse clicked at point:"
//				+ e.getX() + " "
//				+ e.getY() + "mouse clicked :" + e.getClickCount());
	}
}