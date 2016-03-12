package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.parser.CommandLoader;

public class CloneUtil {
	public static int hashSubTree(Block root){
		int hash = 7;
		int blockIndex = getBlockIndex(root);
		hash = hash*31 + blockIndex;
		
		hash = hash(root,hash);

		return hash;
	}
	
	private static int hash(Block b, int hash) {
		if(b.hasNestedBlocks()){
			for(ArrayList<Block> blocks : b.getNestedGroup()){
				for(Block block : blocks){
					hash += hash(block, hash);
				}
			}
		}else{
			int blockIndex = getBlockIndex(b);
			hash = hash*31 + blockIndex;
		}
		return hash;
	}

	public static int getBlockIndex(Block b){
		return CommandLoader.COMMAND_TO_INDEX.get(b.getCommand());
	}
}
