package vt.cs.smells.analyzer;

import java.util.ArrayList;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Visitable;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.TopDown;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class AnalysisUtil  {
	
	public static Visitor v = new TopDown(new AnalysisUtil().new FindBlockHelper());
	public Visitable node;
	public static ArrayList<Block> blocks;
	private static String blockCommand;
	
	public AnalysisUtil(){
		
	}
	
	public static ArrayList<Block> findBlock(Visitable node, String command){
		blockCommand = command;
		blocks = new ArrayList<Block>();
		try {
			node.accept(v);
		} catch (VisitFailure e) {
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

	private static void findBlockInSequenceHelper(Block block, String command, ArrayList<Block> result){
		
		
		while(block!=null){
			if(block.hasNestedBlocks()){
				for(ArrayList<Block> blk: block.getNestedGroup()){
					findBlockInSequenceHelper(blk.get(0), command, result);
				}
			}else{
				if(block.getCommand().equals(command)){
					result.add(block);
				}
				for(Object arg: block.getArgs()){
					if(arg instanceof Block){
						findBlockInSequenceHelper((Block) arg, command, result);
					}
				}
			}
			
			block = block.getNextBlock();
		}
	}
	public static ArrayList<Block> getBlockInSequence(Block block, String command) {
		
		ArrayList<Block> result = new ArrayList<Block>();
		findBlockInSequenceHelper(block, command, result);
		return result;
	}


	public static ArrayList<Block> getVarDefBlocks(Visitable root) {
		ArrayList<Block> varBlocks = findBlock(root, "setVar:to:");
		return varBlocks;
	}

	public static ArrayList<Block> getVarRefBlocks(Visitable root) {
		ArrayList<Block> varBlocks = findBlock(root, "readVariable");
		return varBlocks;
	}

}
