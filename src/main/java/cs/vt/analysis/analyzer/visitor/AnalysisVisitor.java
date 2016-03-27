package cs.vt.analysis.analyzer.visitor;

import cs.vt.analysis.analyzer.analysis.Report;

public interface AnalysisVisitor<Report> extends Visitor{
	public Report getReport();
}
