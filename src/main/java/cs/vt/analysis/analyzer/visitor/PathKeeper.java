package cs.vt.analysis.analyzer.visitor;

import java.util.Stack;

public interface PathKeeper {
	public void registerPathListener(Stack<String> path);
	public Stack<String> getPath();
}
