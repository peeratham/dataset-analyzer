package vt.cs.smells.analyzer.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Insert;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class UnusedVariableAnalyzer extends Analyzer{
	private static final String name = "UnusedVariable";
	private static final String abbr = "UV";

	public HashSet<String> allVar = new HashSet<String>();
	List<String> varRelatedCommands = new ArrayList<String>();
	private ListAnalysisReport report = new ListAnalysisReport(name,abbr);
	int count = 0;
	List<String> varDeclarations = new ArrayList<>();
	
	
	public UnusedVariableAnalyzer(){
		varRelatedCommands.add("setVar:to:");
		varRelatedCommands.add("changeVar:by:");
		varRelatedCommands.add("readVariable");
		varRelatedCommands.add("showVariable:");
		varRelatedCommands.add("hideVariable:");
	}
	@Override
	public void analyze() throws AnalysisException {
		//all variable declarations
		for(Scriptable scriptable : project.getAllScriptables().values()){
			varDeclarations.addAll(scriptable.getAllVariables().keySet());
			allVar.addAll(scriptable.getAllVariables().keySet());
		}
		
		
//		Scriptable stage = project.getScriptable("Stage");
//		Map<String, Object> globals = stage.getAllVariables();
//		for (String varName : globals.keySet()) {
//			allVar.add(varName);
//		}
		for(String varCommand : varRelatedCommands){
			ArrayList<Block> varBlocks = Collector.collect(new Evaluator.BlockCommand(varCommand), project);
			for (Block block : varBlocks) {
				List<Object> parts = block.getBlockType().getParts();
				Iterator<Object> args = block.getArgs().iterator();
				for (int i = 0; i < parts.size(); i++) {					
					if (parts.get(i) instanceof Insert) {
						Object arg = args.next();
						if (((Insert) parts.get(i)).getName().contains("var") && allVar.contains(arg)) {
							allVar.remove(arg);
						}
					}
				}
			}
		}
		for (String s:allVar) {
			report.addRecord(s);	
			count++;
		}
	}

	@Override
	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		if(!varDeclarations.isEmpty()){
			conciseReport.put("count", count);
		}

		report.setConciseJSONReport(conciseReport);
		return report;
	}
	
	public static void main(String[] args) throws IOException, ParseException, ParsingException, AnalysisException{
		String projectSrc = Util.retrieveProjectOnline(118377854);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		UnusedVariableAnalyzer analyzer = new UnusedVariableAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		System.out.println(analyzer.getReport().getConciseJSONReport());
		
	}

}
