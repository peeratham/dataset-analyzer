package cs.vt.analysis.analyzer;

import java.io.IOException;

import org.json.simple.parser.JSONParser;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.Util;
import cs.vt.analysis.analyzer.visitor.BlockCounter;
import cs.vt.analysis.analyzer.visitor.DownUp;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.Stop;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class BlockAnalyzer {
	JSONParser jsonParser;
	ScratchProject project;
	Visitor visitor;
	String input;
	
	public BlockAnalyzer(){
		jsonParser = new JSONParser();
        
	}

	public void setVisitor(Visitor v) {
		visitor = v;
	}
	
	public void analyze() {	
        try {
			project.accept(visitor);
		} catch (VisitFailure e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public void setInputPath(String path){
//		try {
//            Object obj = jsonParser.parse(new FileReader(
//                    path));
//            JSONObject jsonObject = (JSONObject) obj;
//            project = ScratchProject.loadProject(jsonObject);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//	}
	
	public void setStringInput(String input){
		this.input = input;
		try {
            project = ScratchProject.loadProject(input);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public ScratchProject getProject(){
		return project;
	}
	
	public static void main(String[] args){
		BlockAnalyzer blockAnalyzer = new BlockAnalyzer();
		Visitor v = new BlockCounter();
		blockAnalyzer.setVisitor(new DownUp(v, new Stop(), new Identity()));
		try {
			blockAnalyzer.setStringInput(Util.readFile("src/main/resources/project03.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		blockAnalyzer.analyze();
		System.out.println(blockAnalyzer.getProject().getProjectID());
		System.out.println(((BlockCounter)v).getCount());
	}
}
