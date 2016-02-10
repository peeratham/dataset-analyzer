package cs.vt.analysis.analyzer.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.nodes.VisitablePattern;
import cs.vt.analysis.analyzer.nodes.VisitablePatternCollection;

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
