package cs.vt.analysis.analyzer.nodes;

import java.util.Map;

public interface Pattern {
	public void addVariable(Object variable);
	public boolean match(Object term, Map bindings);

}
