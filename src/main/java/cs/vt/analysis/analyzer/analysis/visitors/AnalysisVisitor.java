package cs.vt.analysis.analyzer.analysis.visitors;

import cs.vt.analysis.analyzer.analysis.AnalysisReport;
import cs.vt.analysis.analyzer.visitor.Visitor;

public interface AnalysisVisitor extends Visitor{
	public AnalysisReport getReport();
}
