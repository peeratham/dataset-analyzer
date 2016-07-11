package vt.cs.smells.analyzer.visitor;

import java.util.ArrayList;
import java.util.List;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;

public class SubTreeCollector implements Visitor {
	Visitor v;
	public final int maxSequenceLength = 8;
	private int minSequenceLength = 4;
	private static int length = 0;
	private ArrayList<Block> subtreeList = new ArrayList<Block>();

	public static ArrayList<ArrayList<Block>> cloneSequenceList = new ArrayList<ArrayList<Block>>();

	public SubTreeCollector(Visitor v) {
		this.v = v;
	}

	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		for (String name : scratchProject.getAllScriptables().keySet()) {
			scratchProject.getScriptable(name).accept(v);
			;
		}
	}

	public void visitScriptable(Scriptable scriptable) throws VisitFailure {
		for (int i = 0; i < scriptable.getNumScripts(); i++) {
			scriptable.getScript(i).accept(v);
		}
	}

	public void visitScript(Script script) throws VisitFailure {
		for (int i = 0; i < script.getBlocks().size(); i++) {
			script.getBlocks().get(i).accept(v);
		}
	}

	public void visitBlock(Block block) throws VisitFailure {
		if (block.hasNestedBlocks()) {
			subtreeList.add(block);
			for (ArrayList<Block> group : block.getNestedGroup()) {
				for (Block b : group) {
					b.accept(v);
				}
			}
		} else {
			for (int curSequenceLength = minSequenceLength; curSequenceLength < maxSequenceLength; curSequenceLength++) {
				Block current = block;
				ArrayList<Block> currentSubsequence = new ArrayList<Block>();
				currentSubsequence.add(current);
				while (current.getNextBlock() != null && !current.getNextBlock().hasNestedBlocks()
						&& currentSubsequence.size() < curSequenceLength) {
					current = current.getNextBlock();
					currentSubsequence.add(current);
				}
				if (currentSubsequence.size() >= curSequenceLength) {
					cloneSequenceList.add(currentSubsequence);
				}else{
					break;
				}
			}
		}
	}

	public ArrayList<Block> getSubTreeList() {
		return subtreeList;
	}

	public ArrayList<ArrayList<Block>> getCloneSequenceList() {
		return cloneSequenceList;
	}
}
