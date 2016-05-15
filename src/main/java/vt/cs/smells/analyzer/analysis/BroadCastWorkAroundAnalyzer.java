package vt.cs.smells.analyzer.analysis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisUtil;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.TopDown;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class BroadCastWorkAroundAnalyzer extends Analyzer {
	Map<String, HashSet<Object>> varMap = new HashMap<String, HashSet<Object>>();
	HashSet<String> mayNotBeFlag = new HashSet<String>();
	HashSet<String> pollingVars = new HashSet<String>();
	private ListAnalysisReport report = new ListAnalysisReport();
	
	private class FlagVarCollectorVisitor extends Identity {
		@Override
		public void visitBlock(Block block) throws VisitFailure {
			super.visitBlock(block);
			if(block.getCommand().equals("setVar:to:")){
				Block varBlock = block;
				HashSet<Object> valueSet = varMap.putIfAbsent(varBlock.arg("varName"), new HashSet<Object>());
				if(valueSet==null){
					varMap.get(varBlock.arg("varName")).add(varBlock.arg("value"));
				}else{
					valueSet.add(varBlock.arg("value"));
				}
			}
			if(block.getCommand().equals("changeVar:by:")){
				Block varBlock = block;
				mayNotBeFlag.add(block.arg("varName"));
			}
		}
	}
	
	@Override
	public void analyze() throws AnalysisException {
		collectFlagVars();
		ArrayList<Block> foreverBlocks = AnalysisUtil.findBlock(project, "doForever");	
		for(Block loopBlock: foreverBlocks){
			HashSet<String> setVarNames = new HashSet<String>();
			for(Block setVar :AnalysisUtil.getVarDefBlocks(loopBlock)){
				setVarNames.add((String) setVar.arg("varName"));
			}

			HashSet<String> waitUntilVarNames = new HashSet<String>();
			ArrayList<Block> waitUntilBlocksInForever = AnalysisUtil.findBlock(loopBlock, "doWaitUntil");
			for(Block wait: waitUntilBlocksInForever){
				ArrayList<Block> varBlocks = AnalysisUtil.getVarRefBlocks(wait);
				if(!varBlocks.isEmpty()){
					Block readVar = varBlocks.get(0);
					waitUntilVarNames.add((String) readVar.arg("varName"));
				}				
			}

			waitUntilVarNames.retainAll(setVarNames);
			if(!waitUntilVarNames.isEmpty()){
				report.addRecord(Arrays.toString(waitUntilVarNames.toArray())+"@"+loopBlock.getBlockPath().toString());
			}
		}
	}
	

	private void collectFlagVars() throws AnalysisException {
		FlagVarCollectorVisitor flagCollector = new FlagVarCollectorVisitor();
		Visitor v = new TopDown(flagCollector);
		try {
			v.visitProject(project);
		} catch (VisitFailure e) {
			throw new AnalysisException(e);
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
	public Report getReport() {
		report.setProjectID(project.getProjectID());
		report.setTitle("BroadCastWorkaround");
		return report;
	}

}
