package vt.cs.smells.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vt.cs.smells.analyzer.nodes.Visitable;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class VisitablePattern extends AbstractPattern implements Visitable {

	public void accept(Visitor v) throws VisitFailure {
	}

	public boolean match(Object term, Map bindings) {
		return false;
	}
	






}
