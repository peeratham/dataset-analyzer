package vt.cs.smells.analyzer;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.parser.ParsingException;

public class Main {
	private static final String DATASET_DIR = "C:\\Users\\Peeratham\\workspace\\scratch-dataset";
	private static final String ANALYSIS_OUTPUT_DIR = "C:\\Users\\Peeratham\\workspace\\analysis-output\\test-output";
	static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
        // Configure Log4J
		PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties"));
		
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		Date date = new Date();
		String currentDateTime = dateFormat.format(date);
		try {
			FileUtils.cleanDirectory(new File(ANALYSIS_OUTPUT_DIR));
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		
        
        AnalysisConfigurator config = new AnalysisConfigurator();
        config.addData(DATASET_DIR);
        
        File datasetDirectory = config.getDatasetDirectory();
       
        AnalysisManager blockAnalyzer = new AnalysisManager();
        
        for(File f: datasetDirectory.listFiles()){
        	try {
				JSONObject result = blockAnalyzer.analyze(FileUtils.readFileToString(f));
				int projectID = blockAnalyzer.getProjectID();
				
				File path = new File(ANALYSIS_OUTPUT_DIR, projectID+"-m-1");
				FileUtils.writeStringToFile(path, result.toJSONString());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AnalysisException e) {
				e.printStackTrace();
			} catch (ParsingException e) {
				e.printStackTrace();
			}
        }
    }
	
	
}
