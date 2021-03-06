package vt.cs.smells.analyzer.visitor;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;

public class Choice implements Visitor {
	Visitor first;
	Visitor then;
	public Choice(Visitor first, Visitor then){
		this.first = first;
		this.then = then;
	}
	
	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		try { scratchProject.accept(first);}
		catch (VisitFailure f){
			scratchProject.accept(then);
		}
	}

	public void visitScript(Script script) throws VisitFailure {
		try { script.accept(first);}
		catch (VisitFailure f){
			script.accept(then);
		}
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		try { scriptable.accept(first);}
		catch (VisitFailure f){
			scriptable.accept(then);
		}
		
	}

	public void visitBlock(Block block) throws VisitFailure {
		try { block.accept(first);}
		catch (VisitFailure f){
			block.accept(then);
		}
	}

}
