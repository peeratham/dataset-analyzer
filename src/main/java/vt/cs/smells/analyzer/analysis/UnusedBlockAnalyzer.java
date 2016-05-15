package vt.cs.smells.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import vt.cs.smells.analyzer.parser.Insert;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class UnusedBlockAnalyzer extends Analyzer{
	public HashSet<String> allBlock = new HashSet<String>();
	List<String> blockRelatedCommands = new ArrayList<String>();
	private ListAnalysisReport report = new ListAnalysisReport();
	public  ArrayList<Block> allBlocks;
	
	public UnusedBlockAnalyzer(){
		blockRelatedCommands.add("call");
	}

	public void analyze() throws AnalysisException{
		// TODO Auto-generated method stub
		Scriptable stage = project.getScriptable("Stage");		
		for (String name : project.getAllScriptables().keySet()) {
			if(name!="Stage"){
				allBlocks = AnalysisUtil.findBlock(project.getScriptable(name), "procDef"); 
			}
		}
		try {
			if(allBlocks!=null){
				for(Block block :allBlocks){
					allBlock.add(block.getArgs(0).toString());
				}
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
		} catch (Exception e) {
			throw new AnalysisException(e);
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
