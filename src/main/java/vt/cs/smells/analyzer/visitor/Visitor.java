package vt.cs.smells.analyzer.visitor;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;

public interface Visitor {
//	public void visit(Object node )throws VisitFailure; //leaf node
	public void visitProject(ScratchProject scratchProject) throws VisitFailure;
	public void visitScriptable(Scriptable scriptable) throws VisitFailure;
	public void visitScript(Script script) throws VisitFailure;
	public void visitBlock(Block block) throws VisitFailure;
}
