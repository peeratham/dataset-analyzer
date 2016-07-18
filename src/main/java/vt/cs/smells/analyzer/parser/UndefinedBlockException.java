package vt.cs.smells.analyzer.parser;

public class UndefinedBlockException extends ParsingException {
	UndefinedBlockException(String msg){
		super(msg);
	}

	public UndefinedBlockException(String msg, Throwable cause) {
		super(msg,cause);
	}

	public UndefinedBlockException(Throwable e) {
		super(e);
	}
	
}