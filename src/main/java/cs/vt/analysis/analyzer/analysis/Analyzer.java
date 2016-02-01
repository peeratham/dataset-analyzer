package cs.vt.analysis.analyzer.analysis;

import cs.vt.analysis.analyzer.nodes.ScratchProject;

public interface Analyzer {
	public void analyze() throws AnalysisException;
	public void setProject(ScratchProject project);
	public AnalysisReport getReport();
}
