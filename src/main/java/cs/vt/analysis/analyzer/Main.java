package cs.vt.analysis.analyzer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.analysis.UnreachableCodeAnalyzer;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;

public class Main {
	static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
        // Configure Log4J
        PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties"));
        
        String pathToDataset = "C:\\Users\\Peeratham\\workspace\\scratch-dataset";
        File datasetDirectory = new File(pathToDataset);
     
		File[] files = datasetDirectory.listFiles();
		for (int i = 0; i < files.length; i++) {
			try {
				ScratchProject project = ScratchProject.loadProject(FileUtils.readFileToString(files[i]));
				UnreachableCodeAnalyzer analyzer = new UnreachableCodeAnalyzer(project);
				analyzer.analyze();
				logger.info(analyzer.getReport().getSummary());
				logger.info(analyzer.getReport().getFullReport());
			} catch (IOException e) {
				logger.error("Fail to read file:"+e.getMessage());
			} catch (ParseException e) {
				logger.error("Fail to parse JSON file:"+files[i]);
				e.printStackTrace();
			} catch (ParsingException e) {
				logger.error("Fail to load project:"+files[i]);
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		
        
    }
	
	
}
