package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class VisitablePattern extends AbstractPattern implements Visitable {

	public VisitablePattern(Visitable visitable) {
		
	}

	public void accept(Visitor v) throws VisitFailure {
	}

	public boolean match(Object term, Map bindings) {
		return false;
	}
	






}
