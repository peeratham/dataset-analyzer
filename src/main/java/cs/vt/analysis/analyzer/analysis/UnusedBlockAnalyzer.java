package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Insert;
import cs.vt.analysis.select.Collector;
import cs.vt.analysis.select.Evaluator;
import cs.vt.analysis.analyzer.nodes.BlockType;
import cs.vt.analysis.analyzer.nodes.CustomBlock;
import cs.vt.analysis.analyzer.nodes.ScratchProject;

public class UnusedBlockAnalyzer extends Analyzer{
	public HashSet<String> allBlock = new HashSet<String>();
	List<String> blockRelatedCommands = new ArrayList<String>();
	private ListAnalysisReport report = new ListAnalysisReport();
	public  ArrayList<Block> allBlocks;
	
	public UnusedBlockAnalyzer(){
		blockRelatedCommands.add("call");
	}

	public void analyze() {
		// TODO Auto-generated method stub
		Scriptable stage = project.getScriptable("Stage");		
		for (String name : project.getAllScriptables().keySet()) {
			if(name!="Stage"){
				allBlocks = AnalysisUtil.findBlock(project.getScriptable(name), "procDef"); 
			}
		}
		for(Block block :allBlocks){
			allBlock.add(block.getArgs(0).toString());
		}		
		
		for(String blockCommand : blockRelatedCommands){
			ArrayList<Block> varBlocks = Collector.collect(new Evaluator.BlockCommand(blockCommand), project);			
			for (Block block : varBlocks) {				
				String temp = block.getBlockType().getSpec().toString();
				if(allBlock.contains(temp)){
					allBlock.remove(temp);
				}
			}
		}		
		
	}
	
	public Report getReport() {
		// TODO Auto-generated method stub
		report.setTitle("Unused block");
		for (String s:allBlock) {
			report.addRecord(s);
		}
		return report;
	}
}
