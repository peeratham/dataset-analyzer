package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Insert;
import cs.vt.analysis.select.Collector;
import cs.vt.analysis.select.Evaluator;

public class BroadVarScopeAnalyzer extends Analyzer {
	public HashMap<String, HashSet<String>> globalVarRef = new HashMap<String, HashSet<String>>();
	List<String> varRelatedCommands = new ArrayList<String>();
	private AnalysisReport report = new AnalysisReport();
	
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
				List<Object> parts = block.getBlockSpec().getParts();
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
	}
	@Override
	public AnalysisReport getReport() {
		report.setTitle("Too Broad Variable Scope");
		for (String varName: globalVarRef.keySet()) {
			if(globalVarRef.get(varName).size()==1){
				report.addRecord(varName + globalVarRef.get(varName));
			}
			
		}
		return report;
	}
}
