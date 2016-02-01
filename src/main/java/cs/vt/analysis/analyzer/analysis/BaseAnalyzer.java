package cs.vt.analysis.analyzer.analysis;

import cs.vt.analysis.analyzer.nodes.ScratchProject;

abstract class BaseAnalyzer implements Analyzer{
	public ScratchProject project;
	public void addProject(ScratchProject project){
		this.project = project;
	}
	public abstract void analyze() throws AnalysisException;
	public abstract AnalysisReport getReport();
}
