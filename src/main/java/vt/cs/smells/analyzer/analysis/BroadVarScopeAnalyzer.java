package vt.cs.smells.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Insert;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class BroadVarScopeAnalyzer extends Analyzer {
	public HashMap<String, HashSet<String>> globalVarRef = new HashMap<String, HashSet<String>>();
	List<String> varRelatedCommands = new ArrayList<String>();
	final String name = "TooBroadVariableScope";
	private String abbr = "BVS";
	private ListAnalysisReport report = new ListAnalysisReport(name, abbr);
	int count = 0;
	
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
		for (String varName : globals.keySet()) {
			globalVarRef.put(varName, new HashSet<String>());
		}
		
		for(String varCommand : varRelatedCommands){
			ArrayList<Block> varBlocks = Collector.collect(new Evaluator.BlockCommand(varCommand), project);
			for (Block block : varBlocks) {
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
		conciseReport.put("count", count);
		report.setConciseJSONReport(conciseReport);
		
		return report;
	}
}
