package cs.vt.analysis.analyzer.visitor;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public interface Visitor {
//	public void visit(Object node )throws VisitFailure; //leaf node
	public void visitProject(ScratchProject scratchProject) throws VisitFailure;
	public void visitScriptable(Scriptable scriptable) throws VisitFailure;
	public void visitScript(Script script) throws VisitFailure;
	public void visitBlock(Block block) throws VisitFailure;
}
