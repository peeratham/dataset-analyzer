package cs.vt.analysis.analyzer.nodes;

import java.util.List;
import java.util.Map;

public class VisitableScriptPattern extends VisitablePattern{
	
	private Visitable pattern;
	
	public VisitableScriptPattern(Visitable block) {
		super(block);
		this.pattern = block;
	}
	
	@Override
	public boolean match(Object term, Map bindings) {
		return (term instanceof Block) && tryMatch(term, bindings);
	}
	static Block currentPatternBlock;
	static Block currentTermBlock;
	
	public boolean tryMatch(Object term, Map bindings) {
		boolean success = true;
		Script patternRef = (Script) pattern;
		Block firstPatternBlock = patternRef.getBlocks().get(0);
		currentPatternBlock = firstPatternBlock;
		currentTermBlock = (Block)term;
		while(currentPatternBlock!=null){
			success = matchBlock(currentPatternBlock, currentTermBlock, bindings);
			if(!success){
				return false;
			}
			//update
			currentPatternBlock = currentPatternBlock.getNextBlock();
			currentTermBlock = currentTermBlock.getNextBlock();
		}
		
		return success;
		
	}
	
	public boolean matchScript(Object term, Map bindings) {
		Script patternRef = (Script) pattern;
		Script termRef = (Script) term;
		List<Block> patternBlocks = patternRef.getBlocks();
		List<Block> termBlocks = termRef.getBlocks();


		
		for (int i = 0; i < patternBlocks.size(); i++) {
			Block pb = patternBlocks.get(i);
			Block tb = termBlocks.get(i);
			if(!matchBlock(pb, tb, bindings)){
				return false;
			}
		}
		
		return true;
	}
	
	private boolean matchBlock(Object pattern, Object term, Map bindings) {
		Block patternRef = (Block) pattern;
		Block termRef = (Block) term;
		
		if(!patternRef.getCommand().equals(termRef.getCommand())){
			return false;
		}
		
		List<Object> lhsArgs = (List<Object>) patternRef.getArgs();
		List<Object> rhsArgs = (List<Object>) termRef.getArgs();
		boolean success = true;
		if(lhsArgs.size()==rhsArgs.size()){
			for (int i = 0; i < lhsArgs.size(); i++) {
				success = matchVariable(lhsArgs.get(i),rhsArgs.get(i), bindings)||lhsArgs.get(i).equals(rhsArgs.get(i));
			}
		}
		return success;
		
	}

	
	
	

}
