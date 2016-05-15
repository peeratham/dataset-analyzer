package vt.cs.smells.analyzer.parser;

public class Insert {
	private String name;
	public String getName() {
		return name;
	}

	public void setText(String text) {
		this.name = text;
	}

	private String type;

	public Insert(String str){
		this.name = str;
	}
	
	public Insert(String typeSymbol, String inputType) {
		this.name = typeSymbol;
		this.type = inputType;
	}
	
	public String getType(){
		return this.type;		
	}

	@Override
	public String toString() {
		return "Insert [" + (name != null ? "name=" + name + ", " : "")
				+ (type != null ? "type=" + type : "") + "]";
	}

}
