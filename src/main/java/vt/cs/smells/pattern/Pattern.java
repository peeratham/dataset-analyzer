package vt.cs.smells.pattern;

import java.util.Map;

public interface Pattern {
	public void addVariable(Object variable);
	public boolean match(Object term, Map bindings);

}
