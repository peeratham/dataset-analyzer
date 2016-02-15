package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;



public class BroadCastWorkAround extends BaseAnalyzer {
	Map<String, HashSet<Object>> varMap = new HashMap<String, HashSet<Object>>();
	HashSet<String> mayNotBeFlag = new HashSet<String>();
	HashSet<String> pollingVars = new HashSet<String>();
	private AnalysisReport report = new AnalysisReport();
	
	private class FlagVarCollectorVisitor extends Identity {
		@Override
		public void visitBlock(Block block) throws VisitFailure {
			super.visitBlock(block);
			if(block.getCommand().equals("setVar:to:")){
				HashSet<Object> valueSet = varMap.putIfAbsent(block.getArgs().get(0).toString(), new HashSet<Object>());
				if(valueSet==null){
					varMap.get(block.getArgs().get(0).toString()).add(block.getArgs().get(1));
				}else{
					valueSet.add(block.getArgs().get(1));
				}
				
			}
			if(block.getCommand().equals("changeVar:by:")){
				mayNotBeFlag.add(block.getArgs().get(0).toString());
			}
		}
	}
	

	
	
	@Override
	public void analyze() throws AnalysisException {
		FlagVarCollectorVisitor flagCollector = new FlagVarCollectorVisitor();
		Visitor v = new TopDown(flagCollector);
		try {
			v.visitProject(project);
		} catch (VisitFailure e) {
			throw new AnalysisException(e);
		}
		
		ArrayList<Block> foreverBlocks = AnalysisUtil.findBlock(project, "doForever");
		System.out.println(foreverBlocks);
		
		
		ArrayList<String> result = new ArrayList<String>();
		for(Block b: foreverBlocks){
			HashSet<String> setVarNames = new HashSet<String>();

			for(Block setVar :AnalysisUtil.getBlockInSequence(b, "setVar:to:")){
				setVarNames.add((String) setVar.getArgs().get(0));
			}
			
			
			HashSet<String> waitUntilVarNames = new HashSet<String>();
			ArrayList<Block> waitUntilBlocksInForever =  AnalysisUtil.getBlockInSequence(b, "doWaitUntil");
			for(Block wait: waitUntilBlocksInForever){
				Block readVar = AnalysisUtil.findBlock(wait, "readVariable").get(0);
				waitUntilVarNames.add((String) readVar.getArgs().get(0));
			}
			
			waitUntilVarNames.retainAll(setVarNames);
			report.addRecord(Arrays.toString(waitUntilVarNames.toArray())+"@"+b.getPath());
			
			
		}
	}

	@Override
	public String toString() {
		return "BroadCastWorkAround ["
				+ (varMap != null ? "varMap=" + varMap + ", " : "")
				+ (mayNotBeFlag != null ? "mayNotBeFlag=" + mayNotBeFlag : "")
				+ "]";
	}

	@Override
	public AnalysisReport getReport() {
		report.setTitle("BroadCastWorkaround");
		return report;
	}

}
