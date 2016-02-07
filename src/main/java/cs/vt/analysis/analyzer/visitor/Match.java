package cs.vt.analysis.analyzer.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.nodes.Visitable;
import cs.vt.analysis.analyzer.nodes.VisitablePattern;
import cs.vt.analysis.analyzer.nodes.VisitableScriptPattern;

public class Match implements Visitor {
	private VisitablePattern pattern;
	private List maps = new ArrayList<Object>();
	public List getMaps() {
		return maps;
	}
	
	public Match(Visitable visitable){
		this.pattern = new VisitablePattern(visitable);
	}
	
	public Match(VisitablePattern visitable){
		this.pattern = visitable;
	}
	
	public void addVariable(Object variable) {
		pattern.addVariable(variable);
	}
		
	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
	}
	
	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
	}

	public void visitScript(Script term) throws VisitFailure {

//		Map bindings = new HashMap();
//		if(term instanceof Script){
//			if (((VisitableScriptPattern)pattern).matchScript(term, bindings)) {
//				maps.add(bindings);	//need to match all variable bindings to be added
//			} else {
//				throw new VisitFailure("No match");
//			}
//		}else{
//			throw new VisitFailure("Cannot compare objects of different types");
//		}
		Block firstBlockOfTerm = term.getBlocks().get(0);
		visitBlock(firstBlockOfTerm);

	}
	
	//TODO fix block to work with getChild()
	public void visitBlock(Block blockTerm) throws VisitFailure {
		Map bindings = new HashMap();
		if(blockTerm instanceof Block){
			if (((VisitablePattern)pattern).match(blockTerm, bindings)) {
				maps.add(bindings);	//need to match all variable bindings to be added
			} else {
				throw new VisitFailure("No match");
			}
		}else{
			throw new VisitFailure("Cannot compare objects of different types");
		}
	}
	
	public Visitable visit(Visitable term) throws VisitFailure {
		Map bindings = new HashMap();
		if (pattern.match(term, bindings)) {
			maps.add(bindings);
			return term;
			
		} else {
			throw new VisitFailure("No match");
		}
	}

}
