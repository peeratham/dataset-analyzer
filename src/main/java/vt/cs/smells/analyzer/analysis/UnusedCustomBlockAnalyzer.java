package vt.cs.smells.analyzer.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisUtil;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.BlockType;
import vt.cs.smells.analyzer.nodes.CustomBlock;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class UnusedCustomBlockAnalyzer extends Analyzer {
	private static final String name = "UnusedCustomBlock";
	private static final String abbr = "UCB";

	public HashSet<String> allBlock = new HashSet<String>();
	List<String> blockRelatedCommands = new ArrayList<String>();
	private ListAnalysisReport report = new ListAnalysisReport(name, abbr);
	public ArrayList<Block> allBlocks;
	int count = 0;
	boolean haveCustomBlock = false;

	Map<Scriptable, List<String>> scriptableToCustomBlocks = new HashMap<>();

	public UnusedCustomBlockAnalyzer() {
		blockRelatedCommands.add("call");
	}

	public void analyze() throws AnalysisException {
		
		
		for (String scriptable : project.getAllScriptables().keySet()) {
			List<Block> thisScriptableCustomBlocks = Collector.collect(new Evaluator.BlockCommand(
					"procDef"), project.getScriptable(scriptable));
			
			HashSet<String> definedCustomBlocks = new HashSet<String>();
			for (Block customBlock : thisScriptableCustomBlocks) {
				CustomBlock cb = (CustomBlock) customBlock.getArgs(0);
				definedCustomBlocks.add(cb.getBlockType().getSpec().toString());
				
			}
			
			HashSet<String> calledCustomBlockNames = new HashSet<String>();
			ArrayList<Block> calledCustomBlock = Collector.collect(
					new Evaluator.BlockCommand("call"), project.getScriptable(scriptable));
			
			
			for (Block customBlock : calledCustomBlock) {
				calledCustomBlockNames.add(customBlock.getBlockType().getSpec().toString());
			}
			
			//check if customblock is used
			haveCustomBlock = haveCustomBlock||!definedCustomBlocks.isEmpty();
			
			definedCustomBlocks.removeAll(calledCustomBlockNames);
			if(!definedCustomBlocks.isEmpty()){
				for(String blockName:definedCustomBlocks){
					report.addRecord(scriptable+"["+blockName+"]");
					count++;
				}	
			}
		}
	}

	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		if(haveCustomBlock){
			conciseReport.put("count", count);
		}
		report.setConciseJSONReport(conciseReport);

		return report;
	}

	public static void main(String[] args) throws IOException, ParseException,
			ParsingException, AnalysisException {
		String projectSrc = Util.retrieveProjectOnline(119170531);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		UnusedCustomBlockAnalyzer analyzer = new UnusedCustomBlockAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}

}
