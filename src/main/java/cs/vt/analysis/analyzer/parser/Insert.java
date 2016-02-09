package cs.vt.analysis.analyzer.parser;

public class Insert {
	private String text;
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	private String type;

	public Insert(String str){
		this.text = str;
	}
	
	public Insert(String typeSymbol, String inputType) {
		this.text = typeSymbol;
		this.type = inputType;
	}

	@Override
	public String toString() {
		return "Insert [" + (text != null ? "text=" + text + ", " : "")
				+ (type != null ? "type=" + type : "") + "]";
	}

}
