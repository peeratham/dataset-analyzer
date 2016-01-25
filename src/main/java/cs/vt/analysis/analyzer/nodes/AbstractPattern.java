package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractPattern implements Pattern {
	protected List variables = new ArrayList();
	
	public void addVariable(Object variable) {
		variables.add(variable);
	}
	
	protected boolean matchVariable(Object p, Object t, Map bindings){
		if (variables.contains(p)){
			if (bindings.containsKey(p)){
				return bindings.get(p).equals(t);
			} else {
				bindings.put(p, t);
				return true;
			}
		}else {
			return false;
		}
		
		}
	}

