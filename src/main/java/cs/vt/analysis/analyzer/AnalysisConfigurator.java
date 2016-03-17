package cs.vt.analysis.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cs.vt.analysis.analyzer.analysis.Analyzer;

public class AnalysisConfigurator {

	private String datasetPath;
	public List<Analyzer> analyzers =  new ArrayList<Analyzer>();
	public List<Class> analyzerClasess =  new ArrayList<Class>();

	public void addData(String pathToDataset) {
		this.datasetPath = pathToDataset;
		
	}

	public File getDatasetDirectory() {
		return new File(datasetPath);
	}

	public void addAnalysis(String classURL) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class klass = Class.forName(classURL);
		analyzerClasess.add(klass);
	}
	
	public List<Class> listAnalyzers(){
		return analyzerClasess;
	}
}
