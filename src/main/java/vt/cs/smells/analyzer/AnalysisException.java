package vt.cs.smells.analyzer;

public class AnalysisException extends Exception {

	private String message;

	public AnalysisException(Exception e) {
		// TODO Auto-generated constructor stub
	}

	public AnalysisException() {
		super();
	}

	public AnalysisException(String msg) {
		super(msg);
		this.message = msg;
	}

	public AnalysisException(Throwable e) {
		super(e);
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
