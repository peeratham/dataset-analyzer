package cs.vt.analysis.analyzer.analysis;

public class HardCodedSequence extends BaseAnalyzer{

	private AnalysisReport report = new AnalysisReport();
	
	@Override
	public void analyze() throws AnalysisException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AnalysisReport getReport() {
		report.setProjectID(project.getProjectID());
		report.setTitle("HardCodedSequence");
		return report;
	}

}
