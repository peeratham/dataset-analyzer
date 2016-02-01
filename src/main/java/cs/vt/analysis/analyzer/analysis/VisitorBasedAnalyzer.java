package cs.vt.analysis.analyzer.analysis;


import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;


abstract class VisitorBasedAnalyzer extends BaseAnalyzer {
	public ScratchProject project;
	public Visitor analysisVisitor;
	
	public VisitorBasedAnalyzer(ScratchProject project, Visitor v){
		this.project = project; 
		this.analysisVisitor = v;
	}
	
	public void addProject(ScratchProject project){
		this.project = project;
	}
	
	public void addVisitor(Visitor v){
		this.analysisVisitor = v;
	}
	
	@Override
	public void analyze() throws AnalysisException {
		  try {
				project.accept(analysisVisitor);
			} catch (Exception e) {
				throw new AnalysisException(e);
			}

	}
	
	

}
