package cs.vt.analysis.analyzer.visitor;

import java.util.ArrayList;
import java.util.List;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public class SubTreeCollector implements Visitor{
	Visitor v;
	private static final int LIMIT = 3;
	private static int length = 0;
	private  ArrayList<Block> subtreeList = new ArrayList<Block>();
	
	
	public SubTreeCollector(Visitor v){
		this.v = v;
	}


	public void visitProject(ScratchProject scratchProject) throws VisitFailure {
		for (String name : scratchProject.getAllScriptables().keySet()) {
			scratchProject.getScriptable(name).accept(v);;
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

	public void visitBlock(Block block) throws VisitFailure{
		if(block.hasNestedBlocks()){
			subtreeList.add(block);
			for(ArrayList<Block> group: block.getNestedGroup()){
				for(Block b : group){
					b.accept(v);
				}
			}	
		}
	}
	


	public ArrayList<Block> getSubTreeList() {
		return subtreeList;
	}
}
