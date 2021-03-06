package vt.cs.smells.analyzer.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.nodes.Visitable;
import vt.cs.smells.pattern.VisitablePattern;
import vt.cs.smells.pattern.VisitableScriptPattern;

public class Match implements Visitor {
	private VisitablePattern pattern;
	private List maps = new ArrayList<Object>();
	public List getMaps() {
		return maps;
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
