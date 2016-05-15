package vt.cs.smells.analyzer.visitor;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;

public class One implements Visitor{
	protected Visitor visitor;
	
	public One(Visitor v){
		this.visitor = v;
	}

	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		scratchProject.accept(visitor);
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		scriptable.accept(visitor);
	}

	public void visitScript(Script script) throws VisitFailure {
		script.accept(visitor);
	}

	public void visitBlock(Block block) throws VisitFailure {
		block.accept(visitor);
	}

}
