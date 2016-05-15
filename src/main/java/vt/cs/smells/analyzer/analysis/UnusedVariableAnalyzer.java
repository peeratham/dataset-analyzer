package vt.cs.smells.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Insert;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class UnusedVariableAnalyzer extends Analyzer{

	public HashSet<String> allVar = new HashSet<String>();
	List<String> varRelatedCommands = new ArrayList<String>();
	private ListAnalysisReport report = new ListAnalysisReport();
	
	public UnusedVariableAnalyzer(){
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
			allVar.add(varName);
		}
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
	}

	@Override
	public Report getReport() {
		// TODO Auto-generated method stub
		report.setTitle("Unused variable");
		for (String s:allVar) {
			report.addRecord(s);			
		}
		return report;
	}

}
