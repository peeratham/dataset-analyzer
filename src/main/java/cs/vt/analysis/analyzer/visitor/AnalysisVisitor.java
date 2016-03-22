package cs.vt.analysis.analyzer.visitor;

import cs.vt.analysis.analyzer.analysis.AnalysisReport;

public interface AnalysisVisitor extends Visitor{
	public AnalysisReport getReport();
}
