package vt.cs.smells.analyzer;

import vt.cs.smells.analyzer.nodes.ScratchProject;

public abstract class Analyzer {
	public ScratchProject project;
	public void setProject(ScratchProject project){
		this.project = project;
	}
	public abstract void analyze() throws AnalysisException;
	public abstract Report getReport();
}
