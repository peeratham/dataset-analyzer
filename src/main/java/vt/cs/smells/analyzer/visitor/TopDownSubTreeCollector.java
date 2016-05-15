package vt.cs.smells.analyzer.visitor;

import java.util.ArrayList;
import java.util.List;

import vt.cs.smells.analyzer.nodes.Block;



public class TopDownSubTreeCollector extends Sequence{

	public TopDownSubTreeCollector(Visitor v) {
		super(v, null);
		then = new SubTreeCollector(this);
	}

	public ArrayList<Block> getSubTreeList() {
		return ((SubTreeCollector) then).getSubTreeList();
	}

}
