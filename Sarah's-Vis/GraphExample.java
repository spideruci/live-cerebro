import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.*;
import org.graphstream.ui.view.View;
import org.spideruci.analysis.dynamic.api.LineProfiler;
import java.util.concurrent.BlockingQueue;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GraphExample implements MouseListener{
	static View view;
	static JFrame frame;
	static JLabel label;
	public static void main(String args[]) {
		BlockingQueue queue = LineProfiler.getQueue();
		Thread consumer = new Thread(new Runnable(){
			public void run() {
				try {
					String info = queue.take();
					System.out.println("CONSUMED " + info);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		try{
			consumer.start();
			consumer.join();
		} catch(InterruptedException ex){
			ex.printStackTrace();
		}
		System.out.println(queue.size());
		new GraphExample();
	}

	public GraphExample(){
		File lines = new File("output.txt");
		Graph graph = new SingleGraph("Tutorial 1");
		HashMap<String, String> map = new HashMap<>();

		System.setProperty("org.graphstream.ui", "swing");

		String prevNode = "";
		try{
			Scanner in = new Scanner(lines);
			frame = new JFrame();
			frame.setPreferredSize(new Dimension(600,600));
			Viewer viewer = graph.display();
			view = viewer.getDefaultView();
			view.enableMouseOptions();
			view.addListener("Mouse", (MouseListener)this);
			label = new JLabel("Mouse");
			frame.add(label);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			ViewerPipe fromViewer = viewer.newViewerPipe();
//			ViewerEventListener viewerListener =
//					new ViewerEventListener(fromViewer);
//			viewer.getDefaultView().setMouseManager(viewerListener);
//			viewer.getDefaultView().addMouseWheelListener(viewerListener);
//			fromViewer.addViewerListener(viewerListener);
//			fromViewer.addSink(this.graph);

			//viewer.getDefaultView().setMouseManager(manager);
			//viewer.enableAutoLayout();
//			viewer.getDefaultView().setMouseManager(new MouseManager(EnumSet.of(InteractiveElement.EDGE, InteractiveElement.NODE, InteractiveElement.SPRITE)));
			graph.setAttribute("stylesheet", "node { fill-mode: dyn-plain; fill-color: red, blue; }");
			// figure out how to fade the colors from darker to lighter red
			while(in.hasNextLine()){
				String line = in.nextLine();
				String[] lineArgs = line.split(" ");
				String node = lineArgs[3];
				map.put(node, lineArgs[1]);
				Thread.sleep(1000);
				if(graph.getNode(node) == null){
					graph.addNode(node);
					Node n = graph.getNode(node);
					n.setAttribute("ui.label", "    " + n.getId());
					n.setAttribute("ui.color", 0f);
				} else{
					Node n = graph.getNode(node);
					n.setAttribute("ui.color", 0f);
					Node prev = graph.getNode(prevNode);
					prev.setAttribute("ui.color", 1f);
				}
				if(prevNode != "" && map.get(prevNode).equals(map.get(node)) && graph.getEdge(prevNode + node) == null){
					graph.addEdge(prevNode + node, prevNode, node);
					Node prev = graph.getNode(prevNode);
					prev.setAttribute("ui.color", 1f);
				}
				prevNode = node;

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
		label.setText(ge.getId());
		// getClickCount gives the number of quick,
		// consecutive clicks made by the user
		// show the point where the mouse is i.e
//		// the x and y coordinates
//		System.out.println("mouse clicked at point:"
//				+ e.getX() + " "
//				+ e.getY() + "mouse clicked :" + e.getClickCount());
	}
}