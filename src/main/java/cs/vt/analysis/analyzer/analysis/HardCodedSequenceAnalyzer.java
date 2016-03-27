package cs.vt.analysis.analyzer.analysis;

public class HardCodedSequenceAnalyzer extends Analyzer{

	private Report report = new ListAnalysisReport();
	
	@Override
	public void analyze() throws AnalysisException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Report getReport() {
		report.setProjectID(project.getProjectID());
		report.setTitle("HardCodedSequence");
		return report;
	}

}
