package cs.vt.analysis.analyzer.visitor;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

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
