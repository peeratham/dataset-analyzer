package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
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
	
	private class DetectPollingVisitor extends Identity {
		@Override
		public void visitBlock(Block block) throws VisitFailure {
			super.visitBlock(block);
			if(block.getCommand().equals("doForever")){
				ArrayList<Block> nestedBlocks = block.getNestedGroup().get(0);
				for (Block b : nestedBlocks) {
					if(!b.getCommand().equals("doWaitUntil")){
						continue;
					}
					Block waitUntilBlock = b;
					Block compareEqualBlock = (Block) waitUntilBlock.getArgs().get(0);
					for(Object arg: compareEqualBlock.getArgs()){
						if(arg instanceof Block && ((Block) arg).getCommand().equals("readVariable")){
							String varName = (String) ((Block) arg).getArgs().get(0);
							if(!varMap.containsKey(varName)){
								break;
							}
							
						}
					}
					
				}
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
		DetectPollingVisitor pollingDetector = new DetectPollingVisitor();
		Visitor v2 = new TopDown(pollingDetector);
		try {
			v2.visitProject(project);
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
	public AnalysisReport getReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
