package cs.vt.analysis.analyzer.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnalysisConfigurator {

	private String datasetPath;
	public List<Analyzer> analyzers =  new ArrayList<Analyzer>();

	public void addData(String pathToDataset) {
		this.datasetPath = pathToDataset;
		
	}

	public File getDatasetDirectory() {
		return new File(datasetPath);
	}

	public void addAnalysis(String classURL) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Analyzer analysis = (Analyzer) Class.forName(classURL).newInstance();
		analyzers.add(analysis);
	}
	
	public List<Analyzer> listAnalyzers(){
		return analyzers;
	}
	


}
