package cs.vt.analysis.analyzer.visitor;

import java.util.ArrayList;
import java.util.List;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public class FragmentCollector implements Visitor{
	Visitor v;
	private static final int LIMIT = 3;
	private static int length = 0;
	private  List<ArrayList<Block>> fragmentList = new ArrayList<ArrayList<Block>>();
	
	
	public FragmentCollector(Visitor v){
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
			for(ArrayList<Block> group: block.getNestedGroup()){
				for(Block b : group){
					b.accept(v);
				}
			}	
		}
		Block current = block;
		ArrayList<Block> fragment = new ArrayList<Block>();
		while(current!=null && length < LIMIT){
			fragment.add(current);
			current = current.getNextBlock();
			length++;
		}
		length = 0;
		if(fragment.size()<LIMIT){
			return;
		}
		fragmentList.add(fragment);
	}
	
	public  List<ArrayList<Block>> getFragmentList(){
		return fragmentList;
	}
}
