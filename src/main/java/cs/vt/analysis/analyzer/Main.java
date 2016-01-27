package cs.vt.analysis.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;









import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.analysis.UnreachableCodeAnalyzer;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.visitor.DownUp;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.Printer;
import cs.vt.analysis.analyzer.visitor.Stop;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class Main {
	public static void main(String[] args) {
		

        // Configure Log4J
        PropertyConfigurator
          .configure(Main.class.getClassLoader()
                      .getResource("log4j.properties"));
        
        JSONParser jsonParser = new JSONParser();
        ScratchProject project = null;
        
//        try {
//
//        	InputStream in = Main.class.getClassLoader().getResource("project03.json").openStream();
//        	
//			Object obj = jsonParser.parse((new BufferedReader(new InputStreamReader(in))));
//            JSONObject jsonObject = (JSONObject) obj;
//            project = ScratchProject.loadProject(jsonObject);
//
//  
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        UnreachableCodeAnalyzer analyzer = new UnreachableCodeAnalyzer(project);
//		analyzer.analyze();
//		System.out.println(analyzer.getReport().getSummary());
//		System.out.println(analyzer.getReport().getFullReport());
		
		
		String pathToDataset = "C:\\Users\\Peeratham\\workspace\\scratch-dataset";
		File datasetDirectory = new File(pathToDataset);
		File[] files = datasetDirectory.listFiles();
		for (int i = 0; i < files.length; i++) {
			try {
				String string = FileUtils.readFileToString(files[i]);
				project = ScratchProject.loadProject(string);
				UnreachableCodeAnalyzer analyzer = new UnreachableCodeAnalyzer(project);
				analyzer.analyze();
				System.out.println(analyzer.getReport().getSummary());
				System.out.println(analyzer.getReport().getFullReport());
				
			} catch (IOException e) {
//				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			
		}
		
		
        
    }
	
	
}
