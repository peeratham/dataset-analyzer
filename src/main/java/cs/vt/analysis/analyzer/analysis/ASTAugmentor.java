package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.List;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class ASTAugmentor {
	List<String> varRelatedCommands = new ArrayList<String>();
	List<Visitor> visitors;
	public ASTAugmentor(){
		varRelatedCommands.add("setVar:to:");
		varRelatedCommands.add("changeVar:by:");
		varRelatedCommands.add("readVariable");
		varRelatedCommands.add("showVariable:");
		varRelatedCommands.add("hideVariable:");
	
	class VariableCollector extends Identity{

		public void visitScript(Script script) throws VisitFailure {

		}

		public void visitBlock(Block block) throws VisitFailure {
			System.out.println(block.getCommand());
		}
	}

	TopDown visitor = new TopDown(new VariableCollector());
	}
	
	
}
