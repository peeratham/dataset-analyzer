package cs.vt.analysis.analyzer.analysis;

import cs.vt.analysis.analyzer.nodes.ScratchProject;

public abstract class Analyzer {
	public ScratchProject project;
	public void setProject(ScratchProject project){
		this.project = project;
	}
	public abstract void analyze() throws AnalysisException;
	public abstract Report getReport();
}
