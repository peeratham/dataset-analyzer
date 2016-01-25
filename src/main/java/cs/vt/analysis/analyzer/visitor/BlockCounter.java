package cs.vt.analysis.analyzer.visitor;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public class BlockCounter implements Visitor{
	int counter = 0;

	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		// TODO Auto-generated method stub
		
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		// TODO Auto-generated method stub
		
	}

	public void visitScript(Script script) throws VisitFailure {
		// TODO Auto-generated method stub
		
	}

	public void visitBlock(Block block) throws VisitFailure {
		counter++;
		
	}
	
	public int getCount(){
		return counter;
	}
	
}
