package cs.vt.analysis.analyzer.visitor;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public class Printer implements Visitor {

	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		System.out.println("project");
	}

	public void visitScript(Script script) throws VisitFailure {
		System.out.println("script");
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		System.out.println(scriptable.getName());
	}

	public void visitBlock(Block block) throws VisitFailure {
		System.out.println(block);
		
	}

}
