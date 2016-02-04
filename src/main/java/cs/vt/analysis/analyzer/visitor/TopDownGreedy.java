package cs.vt.analysis.analyzer.visitor;



public class TopDownGreedy extends Sequence{

	public TopDownGreedy(Visitor v) {
		super(v, null);
		then = new AllGreedy(this);
	}

}
