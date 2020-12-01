package org.spideruci.analysis.dynamic.api;

import static org.spideruci.analysis.dynamic.Profiler.REAL_OUT;
import static org.spideruci.analysis.dynamic.Profiler.REAL_ERR;
import org.spideruci.analysis.trace.EventType;
import org.spideruci.analysis.trace.TraceEvent;
import java.util.*; 
import java.io.FileWriter;   
import java.io.IOException;

public class LineProfiler extends EmptyProfiler implements IProfiler {
	LinkedHashMap<String, Integer> lineProfileCounts = new LinkedHashMap<>();;
	HashMap<Long, ClassAndMethod> parentMap = new HashMap<>();
	HashMap<String, Line> lineMap = new HashMap<>();
	ArrayList<String> instrumentedClasses = new ArrayList<>();
	ArrayList<String> instrumentedMethods = new ArrayList<>();
	public Queue<Line> queue = new LinkedList<>();
	FileWriter myWriter;
	
	
	

	@Override
	public String description() {
		return "LineProfiler";
	}

	@Override
	public void startProfiling() {
		try{
			myWriter = new FileWriter("output.txt");
		}
		catch (IOException err) {
			System.out.println("An error occurred.");
			err.printStackTrace();
		}
		REAL_OUT.println("Starting traceing");
	}

	@Override
	public void willInstrumentClass(final String className)  {
		instrumentedClasses.add(className);
	}
	
	@Override
	public void willInstrumentMethod(final TraceEvent e)  { 
		if (e.getType() != EventType.$$method$$) {
		return;
		}

		String methodName = e.getDeclName();
		String className = e.getDeclOwner();
		long id = e.getId();

		ClassAndMethod cnm = new ClassAndMethod(className, methodName);
		parentMap.put(id, cnm);
		instrumentedMethods.add(cnm.toString());
	}

	@Override
	public void willInstrumentCode(final TraceEvent e) {
		if (e.getType() != EventType.$line$) {
		return;
		}

		long id = e.getId();
		long declHostId = e.getInsnDeclHostId();

		if (!parentMap.containsKey(declHostId)) {
		return;
		}

		ClassAndMethod declaringParent = parentMap.get(declHostId);
		int lineNumber = e.getInsnLine();

		Line line = new Line(declaringParent, lineNumber);
		lineMap.put(String.valueOf(id), line);
	}

	@Override
	public void profileInsn(final TraceEvent e) {
		if (e.getExecInsnType() != EventType.$line$) {
		return;
		}

		String sourceLineInsnId = e.getExecInsnEventId();
		queue.add(lineMap.get(sourceLineInsnId));
		try{
			
			myWriter.write("Thread: " + Thread.currentThread().getId() +" Line:" + lineMap.get(sourceLineInsnId).lineNumber + "  Class: " + lineMap.get(sourceLineInsnId).className);
			myWriter.write(System.getProperty( "line.separator" ));
		}
		catch (IOException err) {
			System.out.println("An error occurred.");
			err.printStackTrace();
		}
		REAL_OUT.println("Thread: " + Thread.currentThread().getId() +" Line:" + lineMap.get(sourceLineInsnId).lineNumber + "  Class: " + lineMap.get(sourceLineInsnId).className);
		//System.out.println("Line:" + lineMap.get(sourceLineInsnId).lineNumber + "  Class: " + lineMap.get(sourceLineInsnId).className);
		if (lineProfileCounts.containsKey(sourceLineInsnId)) {
			int count = lineProfileCounts.get(sourceLineInsnId);
			lineProfileCounts.put(sourceLineInsnId, count + 1);
		} else {
			lineProfileCounts.put(sourceLineInsnId, 1);
		}
	}

	@Override
	public void endProfiling() {
		try{
			myWriter.close();
		}
		catch (IOException err) {
			System.out.println("An error occurred.");
			err.printStackTrace();
		}
		REAL_OUT.println("ending Line Profiler");

		// REAL_OUT.printf("Classes instrumented: %s\n", this.instrumentedClasses.size());
		// REAL_OUT.printf("Methods instrumented: %s\n", this.instrumentedMethods.size());

		// ArrayList<Map.Entry<String, Integer>> list = new ArrayList(this.lineProfileCounts.entrySet());
		// list.sort(Map.Entry.comparingByValue());

		// Collections.reverse(list);

		// for (Map.Entry<String, Integer> lineCount : list) {
		// 	String lineId = lineCount.getKey();

		// 	if (lineId == null || lineId.isEmpty()) {
		// 		continue;
		// 	}
			
		// 	int counter = lineCount.getValue(); // lineProfileCounts.get(lineId);

		// 	if (lineMap.containsKey(lineId)) {
		// 		Line l = lineMap.get(lineId);

		// 		// if (!l.className.contains("fop")) {
		// 		// continue;
		// 		// }


		// 		REAL_OUT.println("Count: " + counter);
		// 		REAL_OUT.println("  - " + l.className);
		// 		REAL_OUT.println("  - " + l.methodName);
		// 		REAL_OUT.println("  - Line: " + l.lineNumber);
		// 	} else {
		// 		REAL_OUT.println("LINE: \n  - " + lineId + "\n  - Coverage Counter:" + counter);
		// 	}

		// }// 
	}

	
  
}

class ClassAndMethod {
	String className;
	String methodName;
  
	public ClassAndMethod(String className, String methodName) {
	  this.className = className;
	  this.methodName = methodName;
	}
  
	@Override
	public String toString() {
	  return className + "." + methodName;
	}
  }
  
  class Line {
	String className;
	String methodName;
	int lineNumber;
  
	public Line(ClassAndMethod classAndMethod, int lineNumber) {
	  this.className = classAndMethod.className;
	  this.methodName = classAndMethod.methodName;
	  this.lineNumber = lineNumber;
	}
  }
