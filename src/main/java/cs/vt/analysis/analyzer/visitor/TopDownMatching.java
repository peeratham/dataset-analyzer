package cs.vt.analysis.analyzer.visitor;



public class TopDownMatching extends Sequence{

	public TopDownMatching(Visitor v) {
		super(v, null);
		then = new AllForward(this);
	}

}
