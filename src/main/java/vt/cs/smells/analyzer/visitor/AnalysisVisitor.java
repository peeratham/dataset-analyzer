package vt.cs.smells.analyzer.visitor;

import vt.cs.smells.analyzer.Report;

public interface AnalysisVisitor<Report> extends Visitor{
	public Report getReport();
}
