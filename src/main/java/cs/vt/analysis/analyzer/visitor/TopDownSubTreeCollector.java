package cs.vt.analysis.analyzer.visitor;

import java.util.ArrayList;
import java.util.List;

import cs.vt.analysis.analyzer.nodes.Block;



public class TopDownSubTreeCollector extends Sequence{

	public TopDownSubTreeCollector(Visitor v) {
		super(v, null);
		then = new SubTreeCollector(this);
	}

	public ArrayList<Block> getSubTreeList() {
		return ((SubTreeCollector) then).getSubTreeList();
	}

}
