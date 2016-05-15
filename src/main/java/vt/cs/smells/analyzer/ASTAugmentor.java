package vt.cs.smells.analyzer;

import java.util.ArrayList;
import java.util.List;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.TopDown;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

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
