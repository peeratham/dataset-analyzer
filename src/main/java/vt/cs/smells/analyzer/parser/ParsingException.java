package vt.cs.smells.analyzer.parser;

public class ParsingException extends Exception {
	ParsingException(String msg){
		super(msg);
	}

	public ParsingException(String msg, Throwable cause) {
		super(msg,cause);
	}

	public ParsingException(Throwable e) {
		super(e);
	}

	public ParsingException() {
		super();
	}
	
}