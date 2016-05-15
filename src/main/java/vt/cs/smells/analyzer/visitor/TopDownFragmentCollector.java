package vt.cs.smells.analyzer.visitor;

import java.util.ArrayList;
import java.util.List;

import vt.cs.smells.analyzer.nodes.Block;



public class TopDownFragmentCollector extends Sequence{

	public TopDownFragmentCollector(Visitor v) {
		super(v, null);
		then = new FragmentCollector(this);
	}
	
	public List<ArrayList<Block>> getFragmentList(){
		return ((FragmentCollector) then).getFragmentList();
	}

	public List<ArrayList<Block>> getSubTreeList() {
		return ((FragmentCollector) then).getSubTreeList();
	}

}
