package vt.cs.smells.analyzer.visitor;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;

public class Stop implements Visitor {

	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		throw new VisitFailure();	//always fail to stop; need condition when stop succeed to return;
	}

	public void visitScript(Script script) throws VisitFailure {
		throw new VisitFailure();	//always fail to stop; need condition when stop succeed to return;
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		throw new VisitFailure();	//always fail to stop; need condition when stop succeed to return;
		
	}

	public void visitBlock(Block block) throws VisitFailure {
		throw new VisitFailure();
	}

}
