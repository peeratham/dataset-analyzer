package vt.cs.smells.analyzer.visitor;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;

public class Printer implements Visitor {

	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		System.out.println("ProjectID:"+scratchProject.getProjectID());
	}

	public void visitScript(Script script) throws VisitFailure {
		System.out.println("script");
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		System.out.println(scriptable.getName());
	}

	public void visitBlock(Block block) throws VisitFailure {
		System.out.println(block.getCommand());
		
	}

}
