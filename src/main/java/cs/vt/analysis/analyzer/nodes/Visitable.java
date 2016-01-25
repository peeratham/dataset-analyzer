package cs.vt.analysis.analyzer.nodes;

import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public interface Visitable {
	public void accept(Visitor v) throws VisitFailure;
}
