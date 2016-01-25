package cs.vt.analysis.analyzer.analysis;


import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;


public class BaseAnalyzer implements Analyzer {
	public ScratchProject project;
	public Visitor analysisVisitor;
	
	public BaseAnalyzer(ScratchProject project, Visitor v){
		this.project = project; 
		this.analysisVisitor = v;
	}
	
	public void analyze() {
		  try {
				project.accept(analysisVisitor);
			} catch (VisitFailure e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

}
