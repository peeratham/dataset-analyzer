package cs.vt.analysis.analyzer.parser;

public class ParsingException extends Exception {
	 private String message = null;
	 
	    public ParsingException() {
	        super();
	    }
	 
	    public ParsingException(String message) {
	        super(message);
	        this.message = message;
	    }
	 
	    public ParsingException(Throwable cause) {
	        super(cause);
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
