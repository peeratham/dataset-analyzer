package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashSet;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Visitable;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

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
			}
			
			block = block.getNextBlock();
		}
	}
	public static ArrayList<Block> getBlockInSequence(Block block, String command) {
		
		ArrayList<Block> result = new ArrayList<Block>();
		findBlockInSequenceHelper(block, command, result);
		
		return result;
	}


}
