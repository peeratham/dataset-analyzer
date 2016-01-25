package cs.vt.analysis.analyzer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;




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
        
        try {

        	InputStream in = Main.class.getClassLoader().getResource("project03.json").openStream();
        	
			Object obj = jsonParser.parse((new BufferedReader(new InputStreamReader(in))));
            JSONObject jsonObject = (JSONObject) obj;
            project = ScratchProject.loadProject(jsonObject);

  
        } catch (Exception e) {
            e.printStackTrace();
        }

        Visitor v = new DownUp(new Printer(), new Stop(), new Identity());
        
        try {
			project.accept(v);
		} catch (VisitFailure e) {
			e.printStackTrace();
		}
        
        
    }
	
	
}
