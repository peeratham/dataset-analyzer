package cs.vt.analysis.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.analysis.AnalysisConfigurator;
import cs.vt.analysis.analyzer.analysis.AnalysisException;
import cs.vt.analysis.analyzer.analysis.AnalysisVisitor;
import cs.vt.analysis.analyzer.analysis.Analyzer;
import cs.vt.analysis.analyzer.analysis.VisitorBasedAnalyzer;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.parser.Util;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class BlockAnalyzer {
	JSONParser jsonParser;
	Visitor visitor;
	String input;
	private AnalysisConfigurator config = null;
	private int projectID;
	
	public BlockAnalyzer(){
		jsonParser = new JSONParser();
        
	}
	
	public JSONObject analyze(String src) {
		JSONObject report = new JSONObject();
		ArrayList<JSONObject> analyses = new ArrayList<JSONObject>();
		if(config==null){
			config = getDefaultConfig();
		}
		try {
			ScratchProject project = ScratchProject.loadProject(src);
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
					analyzer.getReport().getJSONReport();
					analyses.add(analyzer.getReport().getJSONReport());
				} catch (AnalysisException e) {
					e.printStackTrace();
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		report.put("_id", projectID);
		report.put("analyses", analyses);
		
		
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
		BlockAnalyzer blockAnalyzer = new BlockAnalyzer();
		String src = null;
		int projectID = 97552510;
		try {
			src = Util.retrieveProjectOnline(projectID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(blockAnalyzer.analyze(src));
		File path = new File("C:\\Users\\Peeratham\\workspace\\analysis-output", "output01.txt");
		try {
			FileUtils.writeStringToFile(path, projectID+"\t"+blockAnalyzer.analyze(src).toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
