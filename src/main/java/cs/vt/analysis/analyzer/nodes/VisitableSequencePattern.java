package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisitableSequencePattern extends VisitablePattern{
	
	private ArrayList<Block> pattern;
	private Visitable firstBlock;
	private int matchCount = 0;
	
	public VisitableSequencePattern(ArrayList<Block> sequencePattern) {
		super(sequencePattern.get(0));
		this.pattern = sequencePattern;
		this.firstBlock = sequencePattern.get(0);
	}
	
	

	@Override
	public boolean match(Object term, Map bindings) {
		return (term instanceof Block) && tryMatch(term, bindings);
	}
	
	static Block currentPatternBlock;
	static Block currentTermBlock;
	
	public boolean tryMatch(Object startTerm, Map<String, Integer> bindings) {
		boolean success = true;
		
		
		currentPatternBlock = (Block) firstBlock;
		currentTermBlock = (Block)startTerm;
		while(currentPatternBlock!=null){
			success = currentPatternBlock.commandMatches(currentTermBlock);
			if(!success){
				return false;
			}
			//update
			matchCount++;
			if(matchCount < pattern.size()){
				currentPatternBlock = currentPatternBlock.getNextBlock();
				currentTermBlock = currentTermBlock.getNextBlock();
			}else{
				matchCount = 0;
				bindings.put(((Block) startTerm).getPath(), 1);
				break;
			}
		}
		
		return success;
		
	}
	
	public boolean matchScript(Object term, Map bindings) {
		Script patternRef = (Script) firstBlock;
		Script termRef = (Script) term;
		List<Block> patternBlocks = patternRef.getBlocks();
		List<Block> termBlocks = termRef.getBlocks();


		
		for (int i = 0; i < patternBlocks.size(); i++) {
			Block pb = patternBlocks.get(i);
			Block tb = termBlocks.get(i);
			if(!pb.commandMatches(tb)){
				return false;
			}
		}
		
		return true;
	}
	
//	private boolean matchBlock(Object pattern, Object term, Map bindings) {
//		Block patternRef = (Block) pattern;
//		Block termRef = (Block) term;
//		
//		if(!patternRef.getCommand().equals(termRef.getCommand())){
//			return false;
//		}
//		
//		List<Object> lhsArgs = (List<Object>) patternRef.getArgs();
//		List<Object> rhsArgs = (List<Object>) termRef.getArgs();
//		boolean success = true;
//		if(lhsArgs.size()==rhsArgs.size()){
//			for (int i = 0; i < lhsArgs.size(); i++) {
//				success = matchVariable(lhsArgs.get(i),rhsArgs.get(i), bindings)||lhsArgs.get(i).equals(rhsArgs.get(i));
//			}
//		}
//		return success;
//		
//	}

	
	
	

}
