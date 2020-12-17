import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.*;
import org.graphstream.ui.view.View;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseListener;

public class GraphExample implements MouseListener{
	static View view;

	static Graph graph;
	public static void main(String args[]) {
		new GraphExample();
	}

	public GraphExample(){
		File lines = new File("output.txt");
		graph = new SingleGraph("Live Cerebro");
		HashMap<String, Integer> edgeCountMap = new HashMap<>();
		System.setProperty("org.graphstream.ui", "swing");

		String prevNode = "";
		try{
			Scanner in = new Scanner(lines);
			Viewer viewer = graph.display();
			view = viewer.getDefaultView();
			view.enableMouseOptions();
			view.addListener("Mouse", (MouseListener)this);
			graph.setAttribute("stylesheet", "graph { fill-color: black; }" +
					"node { fill-mode: gradient-radial; fill-color: rgba(204,255,255,228), rgba(204,255,255,8); }");
			// figure out how to fade the colors from darker to lighter red

			while(in.hasNextLine()) {
				String line = in.nextLine();
				String[] lineArgs = line.split(" ");
				String node1 = lineArgs[1];
				String node2 = lineArgs[3];
				Thread.sleep(200);
				if (graph.getNode(node1) == null) {
					graph.addNode(node1);
					Node n = graph.getNode(node1);
					n.setAttribute("ui.label", "    " + n.getId());
				}
				if (graph.getNode(node2) == null) {
					graph.addNode(node2);
					Node n = graph.getNode(node2);
					n.setAttribute("ui.label", "    " + n.getId());
				}

				if (graph.getEdge(node1 + " " + node2) == null) {
					edgeCountMap.put(node1 + " " + node2, 1);
					graph.addEdge(node1 + " " + node2, node1, node2);
					Edge edge = graph.getEdge(node1 + " " + node2);
					double weight = (3.3 - edgeCountMap.get(node1 + " " + node2));
					edge.setAttribute("layout.weight", weight);
					edge.setAttribute("ui.style", "size: 1px; fill-color: rgba(255,255,255,68);");
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
		}
		catch(InterruptedException e){
			System.out.println("Error: Interrupted Exception");
		}
		catch(FileNotFoundException e){
			System.out.println("Error: File Not Found Exception");
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