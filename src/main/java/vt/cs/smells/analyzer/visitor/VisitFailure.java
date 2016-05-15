package vt.cs.smells.analyzer.visitor;

public class VisitFailure extends Exception {

	public VisitFailure(String message) {
		super(message);
	}

	public VisitFailure() {
		super();
	}

}
