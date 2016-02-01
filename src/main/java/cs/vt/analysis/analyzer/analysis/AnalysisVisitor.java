package cs.vt.analysis.analyzer.analysis;

import cs.vt.analysis.analyzer.visitor.Visitor;

public interface AnalysisVisitor extends Visitor{
	public AnalysisReport getReport();
}
