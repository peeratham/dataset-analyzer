package vt.cs.smells.analyzer.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.pattern.VisitablePattern;
import vt.cs.smells.pattern.VisitablePatternCollection;

public class SequencePatternCollectionMatch implements Visitor {
	private VisitablePatternCollection patternCollection;
	private List maps = new ArrayList<Object>();
//	map of pattern -> List of occurrence(Path-starting block)
	private Map results = new HashMap<String, ArrayList<String>>();
	
	
	
	public SequencePatternCollectionMatch(VisitablePatternCollection visitable){
		this.patternCollection = visitable;
	}
	
	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
	}
	
	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
	}

	public void visitScript(Script term) throws VisitFailure {
//		Block firstBlockOfTerm = term.getBlocks().get(0);
//		visitBlock(firstBlockOfTerm);

	}
	
	public void visitBlock(Block blockTerm) throws VisitFailure {
		Map matchedRecords = new HashMap();
		if(blockTerm instanceof Block){
			if ((patternCollection).match(blockTerm, matchedRecords)) {
				maps.add(matchedRecords);	//need to match all variable bindings to be added
//				results.put(patternCollection)
			} else {
				throw new VisitFailure("No match");
			}
		}else{
			throw new VisitFailure("Cannot compare objects of different types");
		}
	}
	
	public List getMatchedResults() {
		return maps;
	}

}
