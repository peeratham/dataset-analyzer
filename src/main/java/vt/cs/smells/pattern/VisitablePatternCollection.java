package vt.cs.smells.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vt.cs.smells.analyzer.nodes.Block;

public class VisitablePatternCollection extends VisitablePattern{
	
	private ArrayList<Block> pattern;
	private int matchCount = 0;
	private List<ArrayList<Block>> patternList;
	
	public VisitablePatternCollection(List<ArrayList<Block>> fragmentList) {
		this.patternList = fragmentList;
	}
	

	@Override
	public boolean match(Object term, Map matchedRecords) {
		boolean success = false;
		if(!(term instanceof Block)){
			return false;
		}
		for (Iterator patternIter = patternList.iterator(); patternIter.hasNext();) {
			this.pattern = (ArrayList<Block>) patternIter.next();
			boolean currentMatch = tryMatch(term, matchedRecords);
			success = success||currentMatch;
		}
		return success;
	}
	
	static Block currentPatternBlock;
	static Block currentTermBlock;
	
	public boolean tryMatch(Object startTerm, Map<String, String> matchedRecords) {
		boolean success = true;
		
		
		currentPatternBlock = (Block) pattern.get(0);
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
				matchedRecords.put(pattern.toString(), ((Block) startTerm).getBlockPath().toString());
				break;
			}
		}
		
		return success;
		
	}	
}
