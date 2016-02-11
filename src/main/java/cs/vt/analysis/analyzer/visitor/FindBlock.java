package cs.vt.analysis.analyzer.visitor;

import java.util.ArrayList;
import java.util.HashSet;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Visitable;

public class FindBlock  {
	public ArrayList<Block> blocks = new ArrayList<Block>();
	public Visitor v = new TopDown(new FindBlockHelper());
	public Visitable node;
	
	private String blockCommand;
	
	public FindBlock(Visitable node){
		 
		this.node = node;
	}
	
	public ArrayList<Block> find(String blockCommand){
		this.blockCommand = blockCommand;
		try {
			node.accept(v);
		} catch (VisitFailure e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return blocks;
	}
	
	class FindBlockHelper extends Identity{
		@Override
		public void visitBlock(Block block) throws VisitFailure {
			super.visitBlock(block);
			if(block.getCommand().equals(blockCommand)){
				blocks.add(block);
			}
		}
	}


}
