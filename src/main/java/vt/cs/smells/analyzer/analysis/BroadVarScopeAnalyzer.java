package vt.cs.smells.analyzer.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class BroadVarScopeAnalyzer extends Analyzer {
	public HashMap<String, HashSet<String>> globalVarRef = new HashMap<String, HashSet<String>>();
	List<String> varRelatedCommands = new ArrayList<String>();
	final String name = "BroadVariableScope";
	private String abbr = "BVS";
	private ListAnalysisReport report = new ListAnalysisReport(name, abbr);
	int count = 0;
	int varCount = 0;
	
	public BroadVarScopeAnalyzer(){
		varRelatedCommands.add("setVar:to:");
		varRelatedCommands.add("changeVar:by:");
		varRelatedCommands.add("readVariable");
		varRelatedCommands.add("showVariable:");
		varRelatedCommands.add("hideVariable:");
	}
	
	@Override
	public void analyze() throws AnalysisException {
		Scriptable stage = project.getScriptable("Stage");
		Map<String, Object> globals = stage.getAllVariables();

		//check if there's variable declaration at all
		for(String scriptableName: project.getAllScriptables().keySet()){
			Scriptable sprite = project.getScriptable(scriptableName);
			varCount +=sprite.getAllVariables().size();
		}
		
		for (String varName : globals.keySet()) {
			globalVarRef.put(varName, new HashSet<String>());
		}
		
		for(String varCommand : varRelatedCommands){
			ArrayList<Block> varBlockUsages = Collector.collect(new Evaluator.BlockCommand(varCommand), project);
			for (Block block : varBlockUsages) {
				List<Object> parts = block.getBlockType().getParts();
				Iterator<Object> args = block.getArgs().iterator();
				for (int i = 0; i < parts.size(); i++) {
					if (parts.get(i) instanceof Insert) {
						Object arg = args.next();
						if (((Insert) parts.get(i)).getName().contains("var") && globalVarRef.containsKey(arg)) {
							globalVarRef.get(arg).add(block.getBlockPath().getPathList().get(0));
						}
					}
				}
			}
		}
		
		for (String varName: globalVarRef.keySet()) {
			if(globalVarRef.get(varName).size()==1){
				report.addRecord(varName + globalVarRef.get(varName));
				count++;
			}
		}
	}
	
	@Override
	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		if(varCount!=0){
			conciseReport.put("count", count);
		}
		report.setConciseJSONReport(conciseReport);
		
		return report;
	}
	
	public static void main(String[] args) throws ParseException, ParsingException, IOException, AnalysisException{
		String projectSrc = Util.retrieveProjectOnline(116455523);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		BroadVarScopeAnalyzer analyzer = new BroadVarScopeAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}
}
