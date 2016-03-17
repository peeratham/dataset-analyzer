package cs.vt.analysis.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.analysis.AnalysisException;
import cs.vt.analysis.analyzer.analysis.Analyzer;
import cs.vt.analysis.analyzer.analysis.VisitorBasedAnalyzer;
import cs.vt.analysis.analyzer.analysis.visitors.AnalysisVisitor;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class AnalysisManager {
	JSONParser jsonParser;
	Visitor visitor;
	String input;
	private AnalysisConfigurator config = null;
	private int projectID;
	
	public AnalysisManager(){
		jsonParser = new JSONParser();
        
	}
	
	public JSONObject analyze(String src) throws  ParsingException, AnalysisException {
		JSONObject report = new JSONObject();
		ArrayList<JSONObject> analyses = new ArrayList<JSONObject>();
		if(config==null){
			config = getDefaultConfig();
		}
		ScratchProject project = null;
		try {
			project = ScratchProject.loadProject(src);
			projectID = project.getProjectID();
			
			
			for (Class k : config.listAnalyzers()) {
				Analyzer analyzer = null;
				if(Arrays.asList(k.getInterfaces()).contains(AnalysisVisitor.class)){
					AnalysisVisitor v = (AnalysisVisitor) k.newInstance();
					 analyzer = new VisitorBasedAnalyzer();
					((VisitorBasedAnalyzer) analyzer).addAnalysisVisitor(v);	
				}else{
					analyzer = (Analyzer) k.newInstance();
				}
				
				analyzer.setProject(project);
				
				
					try {
						analyzer.analyze();
					} catch (AnalysisException e) {
						System.err.println("==>Error analyzing projectID: "+ projectID);
						throw new AnalysisException("Analysis Error["+analyzer.getClass()+"]:\n"+e.getMessage());
					}
					analyzer.getReport().getJSONReport();
					analyses.add(analyzer.getReport().getJSONReport());
				
			}
		} catch (ParseException e) {
			System.err.println("==>Error parsing JSONObject of projectID: "+ projectID);
			throw new ParsingException(e);
		} catch (ParsingException e) {
			System.err.println("==>Error parsing projectID: "+ projectID);
			throw new ParsingException(e);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		report.put("_id", projectID);
		report.put("scriptCount", project.getScriptCount());
		report.put("spriteCount", project.getSpriteCount());
		for(JSONObject a: analyses){
			report.put(a.get("name"), a.get("records"));
		}
		
		
		return report;
      
	}
	
	public int getProjectID(){
		return projectID;
	}
	
	private AnalysisConfigurator getDefaultConfig() {
		AnalysisConfigurator defaultConfig = new AnalysisConfigurator();
		try {
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.UnreachableAnalysisVisitor");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.LongScriptVisitor");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.BroadCastWorkAround");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.UncommunicativeNamingVisitor");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.BroadVarScopeVisitor");
			defaultConfig.addAnalysis("cs.vt.analysis.analyzer.analysis.CloneAnalyzer");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return defaultConfig;
	}
	
	private void setConfig(AnalysisConfigurator config) {
		this.config = config;
		
	}

	
	public static void main(String[] args){
		AnalysisManager blockAnalyzer = new AnalysisManager();
		String src = null;
		int projectID = 97552510;
		try {
			src = Util.retrieveProjectOnline(projectID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File path = new File("C:\\Users\\Peeratham\\workspace\\analysis-output", projectID+"-m-1");
		try {
			String result = blockAnalyzer.analyze(src).toString();
			System.out.println(result);
			FileUtils.writeStringToFile(path, result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
