package cs.vt.analysis.select;

import java.util.ArrayList;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.nodes.Visitable;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

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
