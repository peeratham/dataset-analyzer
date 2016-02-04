package cs.vt.analysis.analyzer.visitor;

import java.util.ArrayList;
import java.util.List;

import cs.vt.analysis.analyzer.nodes.Block;



public class TopDownCollector extends Sequence{

	public TopDownCollector(Visitor v) {
		super(v, null);
		then = new FragmentCollector(this);
	}
	
	public List<ArrayList<Block>> getFragmentList(){
		return FragmentCollector.getFragmentList();
	}

}
