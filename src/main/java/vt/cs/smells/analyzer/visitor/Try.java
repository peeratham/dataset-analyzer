package vt.cs.smells.analyzer.visitor;

public class Try extends Choice {

	public Try(Visitor v) {
		super(v, new Identity());
	}

}

