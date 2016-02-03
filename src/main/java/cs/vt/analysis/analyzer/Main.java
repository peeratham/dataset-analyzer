package cs.vt.analysis.analyzer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.analysis.AnalysisConfigurator;
import cs.vt.analysis.analyzer.analysis.AnalysisException;
import cs.vt.analysis.analyzer.analysis.AnalysisVisitor;
import cs.vt.analysis.analyzer.analysis.Analyzer;
import cs.vt.analysis.analyzer.analysis.VisitorBasedAnalyzer;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;

public class Main {
	static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
        // Configure Log4J
        PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties"));
        AnalysisConfigurator config = new AnalysisConfigurator();
        config.addData("C:\\Users\\Peeratham\\workspace\\scratch-dataset");
        
        File datasetDirectory = config.getDatasetDirectory();
        try {
//			config.addAnalysis("cs.vt.analysis.analyzer.analysis.UnreachableAnalysisVisitor");
			config.addAnalysis("cs.vt.analysis.analyzer.analysis.LongScriptVisitor");
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
     
		File[] files = datasetDirectory.listFiles();
		for (int i = 0; i < files.length; i++) {
			try {
				ScratchProject project = ScratchProject.loadProject(FileUtils.readFileToString(files[i]));
				for (Class k : config.listAnalyzers()) {
					AnalysisVisitor v = (AnalysisVisitor) k.newInstance();
					VisitorBasedAnalyzer analyzer = new VisitorBasedAnalyzer();
					analyzer.addAnalysisVisitor(v);
					analyzer.setProject(project);
					try {
						analyzer.analyze();
						logger.info(analyzer.getReport().getJSONReport());
					} catch (AnalysisException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} catch (IOException e) {
				logger.error("Fail to read file:"+e.getMessage());
			} catch (ParseException e) {
				logger.error("Fail to parse JSON file:"+files[i]);
				e.printStackTrace();
			} catch (ParsingException e) {
				logger.error("Fail to load project:"+files[i]);
				logger.error(e.getMessage());
				e.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
		
        
    }
	
	
}
