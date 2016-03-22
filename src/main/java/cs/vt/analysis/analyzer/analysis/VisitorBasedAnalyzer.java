package cs.vt.analysis.analyzer.analysis;


import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.visitor.AnalysisVisitor;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;


public class VisitorBasedAnalyzer extends Analyzer {
	public ScratchProject project;
	public AnalysisVisitor analysisVisitor;
	
	
	public void setProject(ScratchProject project){
		this.project = project;
	}
	
	public void addAnalysisVisitor(AnalysisVisitor v){
		this.analysisVisitor = v;
	}
	
	@Override
	public void analyze() throws AnalysisException {
		  try {
				project.accept(analysisVisitor);
			} catch (Exception e) {
				e.printStackTrace();
				throw new AnalysisException(e);
			}

	}

	@Override
	public AnalysisReport getReport() {
		AnalysisReport report =analysisVisitor.getReport();
		report.setProjectID(project.getProjectID());
		return report;
	}

	
	

}
