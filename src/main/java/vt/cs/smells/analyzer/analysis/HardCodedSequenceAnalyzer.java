package vt.cs.smells.analyzer.analysis;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;

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
