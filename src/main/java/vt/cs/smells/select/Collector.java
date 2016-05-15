package vt.cs.smells.select;

import java.util.ArrayList;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.nodes.Visitable;
import vt.cs.smells.analyzer.visitor.TopDown;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class Collector {
	private Collector(){}
	
	public static ArrayList<Block> collect(Evaluator eval, Visitable root){
		ArrayList<Block> blocks = new ArrayList<Block>();
		try {
			root.accept(new TopDown(new Accumulator(eval, blocks)));
		} catch (VisitFailure e) {
			e.printStackTrace();
		}
		return blocks; 
	}
	
	private static class Accumulator implements Visitor {
		private final ArrayList<Block> blocks;
		private final Evaluator eval;
		Accumulator(Evaluator eval, ArrayList<Block>blocks){
			this.blocks = blocks;
			this.eval = eval;
		}

		public void visitProject(ScratchProject scratchProject)
				throws VisitFailure {
		}

		public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		}

		public void visitScript(Script script) throws VisitFailure {
		}

		public void visitBlock(Block block) throws VisitFailure {
			if(eval.matches(block)){
				blocks.add(block);
			}
		}
		
	}
}
