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
	public HashSet<Block> allBlock = new HashSet<Block>();
	List<String> blockRelatedCommands = new ArrayList<String>();
	private ListAnalysisReport report = new ListAnalysisReport();
	public  ArrayList<Block> foreverBlocks;
	
	public UnusedBlockAnalyzer(){
		blockRelatedCommands.add("call");
	}

	public void analyze() {
		// TODO Auto-generated method stub
		Scriptable stage = project.getScriptable("Stage");		
		for (String name : project.getAllScriptables().keySet()) {
			if(name!="Stage"){
				foreverBlocks = AnalysisUtil.findBlock(project.getScriptable(name), "procDef"); 
			}
		}
		for(Block block :foreverBlocks){
			allBlock.add(block);
		}
		
		
		for(String varCommand : blockRelatedCommands){
			ArrayList<Block> varBlocks = Collector.collect(new Evaluator.BlockCommand(varCommand), project);
			for (Block block : varBlocks) {				
				List<Object> parts = block.getBlockType().getParts();			
				for (int i = 0; i < parts.size(); i++) {
					allBlock.remove(parts.get(i));
				}
			}
		}		
		
	}
	
	public Report getReport() {
		// TODO Auto-generated method stub
		report.setTitle("Unused block");
		for (Block b:allBlock) {
			report.addRecord(b.getArgs().get(0));//define XXXX? hardcode for just get name
		}
		return report;
	}
}
