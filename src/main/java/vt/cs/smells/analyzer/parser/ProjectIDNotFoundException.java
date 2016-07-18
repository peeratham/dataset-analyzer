package vt.cs.smells.analyzer.parser;

public class ProjectIDNotFoundException extends ParsingException {
	ProjectIDNotFoundException(String msg){
		super(msg);
	}

	public ProjectIDNotFoundException(String msg, Throwable cause) {
		super(msg,cause);
	}

	public ProjectIDNotFoundException(Throwable e) {
		super(e);
	}

	public ProjectIDNotFoundException() {

	}


	
}