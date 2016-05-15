package vt.cs.smells.analyzer.visitor;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;

public class Sequence implements Visitor{
	protected Visitor first;
	protected Visitor then;
	
	public Sequence(Visitor first, Visitor then){
		this.first = first;
		this.then = then;
	}
	
//	@Override
//	public void visit(Object node) throws VisitFailure {
//		// TODO Auto-generated method stub
//		
//	}

	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		scratchProject.accept(first);
		scratchProject.accept(then);
		
	}

	public void visitScript(Script script) throws VisitFailure {
		script.accept(first);
		script.accept(then);
		
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		scriptable.accept(first);
		scriptable.accept(then);
	}

	public void visitBlock(Block block) throws VisitFailure {
		block.accept(first);
		block.accept(then);
	}

}
