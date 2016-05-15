package vt.cs.smells.analyzer.visitor;



public class TopDown extends Sequence{

	public TopDown(Visitor v) {
		super(v, null);
		then = new All(this);
	}

}
