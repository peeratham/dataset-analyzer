package cs.vt.analysis.analyzer.visitor;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

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
