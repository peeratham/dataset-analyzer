package vt.cs.smells.analyzer;



import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.visitor.AnalysisVisitor;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;


public class VisitorBasedAnalyzer extends Analyzer {
	public ScratchProject project;
	public AnalysisVisitor<ListAnalysisReport> analysisVisitor;
	
	
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
	public Report getReport() {
		Report report =analysisVisitor.getReport();
		report.setProjectID(project.getProjectID());
		return report;
	}

	
	

}
