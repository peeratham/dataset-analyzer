package vt.cs.smells.analyzer.nodes;

import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public interface Visitable {
	public void accept(Visitor v) throws VisitFailure;
}
